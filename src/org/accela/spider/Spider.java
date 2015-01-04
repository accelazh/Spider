package org.accela.spider;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.accela.common.Assertion;
import org.accela.spider.data.WebPage;
import org.accela.spider.data.WebPageStore;
import org.accela.spider.event.SpiderListener;
import org.accela.spider.stage.AnalyzingStage;
import org.accela.spider.stage.BeginningListenerStage;
import org.accela.spider.stage.ConstructingStage;
import org.accela.spider.stage.DeliveringStage;
import org.accela.spider.stage.EndingListenerStage;
import org.accela.spider.stage.ExtractingHyperlinkStage;
import org.accela.spider.stage.FetchingContentStage;
import org.accela.spider.stage.FilterStage;
import org.accela.spider.stage.NormalizationStage;
import org.accela.spider.stage.PrefilterStage;
import org.accela.spider.stage.RecursionStage;
import org.accela.spider.stage.SchedulingStage;
import org.accela.spider.stage.StampingStage;
import org.accela.spider.strategy.LoadMonitor;
import org.accela.spider.strategy.LoadMonitorGroup;
import org.accela.spider.strategy.RecursionHyperlinkFilter;
import org.accela.spider.strategy.URLFilter;
import org.accela.spider.strategy.URLScheduler;
import org.accela.spider.strategy.impl.StageLoadMonitor;
import org.accela.spider.util.URL;
import org.accela.stage.DelegateStage;
import org.accela.stage.RejectedInputException;
import org.accela.stage.Stage;

public class Spider implements Stage<URL>
{
	private volatile boolean initialized = false;

	private volatile boolean shutdown = false;

	private WebPageStore store = null;

	private ScheduledExecutorService scheduledExecutor = null;

	private ExecutorService cpuBoundExecutor = null;

	private ExecutorService netBoundExecutor = null;

	private ExecutorService diskBoundExecutor = null;

	private Map<SpiderStageName, Stage<?>> stages = new HashMap<SpiderStageName, Stage<?>>();

	private DelegateStage<URL> constructingStage = new DelegateStage<URL>();

	private DelegateStage<WebPage> normalizationStage = new DelegateStage<WebPage>();

	private DelegateStage<WebPage> beginningListenerStage = new DelegateStage<WebPage>();

	private DelegateStage<WebPage> prefilterStage = new DelegateStage<WebPage>();

	private DelegateStage<WebPage> schedulingStage = new DelegateStage<WebPage>();

	private DelegateStage<WebPage> filterStage = new DelegateStage<WebPage>();

	private DelegateStage<WebPage> fetchingContentStage = new DelegateStage<WebPage>();

	private DelegateStage<WebPage> extractingHyperlinkStage = new DelegateStage<WebPage>();

	private DelegateStage<WebPage> analyzingStage = new DelegateStage<WebPage>();

	private DelegateStage<WebPage> stampingStage = new DelegateStage<WebPage>();

	private DelegateStage<WebPage> recursionStage = new DelegateStage<WebPage>();

	private DelegateStage<WebPage> endingListenerStage = new DelegateStage<WebPage>();

	// why use delegate stage? before initializing spider with spider
	// attributes,
	// we need to construct spider attributes which needs load monitor. but load
	// monitors need reference to stages, which should be constructed AFTER the
	// initialization spider. that's a circular dependency. to break it, I
	// choose
	// to use delegate stage which holds concrete stage objects and can be
	// constructed before the initialization of spider, and pass delegate stage
	// to
	// load monitor.
	// similar problem exists when initializing stages.
	private DelegateStage<WebPage> deliveringStage = new DelegateStage<WebPage>();

	public Spider(WebPageStore store)
	{
		if (null == store)
		{
			throw new IllegalArgumentException("store should not be null");
		}

		this.store = store;

		this.stages.put(SpiderStageName.Constructing, constructingStage);
		this.stages.put(SpiderStageName.Normalization, normalizationStage);
		this.stages.put(SpiderStageName.BeginningListener,
				beginningListenerStage);
		this.stages.put(SpiderStageName.Prefilter, prefilterStage);
		this.stages.put(SpiderStageName.Scheduling, schedulingStage);
		this.stages.put(SpiderStageName.Filter, filterStage);
		this.stages.put(SpiderStageName.FetchingContent, fetchingContentStage);
		this.stages.put(SpiderStageName.ExtractingHyperlink,
				extractingHyperlinkStage);
		this.stages.put(SpiderStageName.Analyzing, analyzingStage);
		this.stages.put(SpiderStageName.Stamping, stampingStage);
		this.stages.put(SpiderStageName.Recursion, recursionStage);
		this.stages.put(SpiderStageName.EndingListener, endingListenerStage);
		this.stages.put(SpiderStageName.Delivering, deliveringStage);

		this.stages.put(SpiderStageName.All, this);
	}

	public WebPageStore getStore()
	{
		assert (store != null) : Assertion.declare();
		return store;
	}

	public LoadMonitor getLoadMonitor()
	{
		return getLoadMonitor(SpiderStageName.All);
	}

	public LoadMonitor getLoadMonitor(SpiderStageName name)
	{
		if (null == name)
		{
			throw new IllegalArgumentException("name should not be null");
		}

		Stage<?> stage = stages.get(name);
		assert (stage != null) : Assertion.declare();

		return new StageLoadMonitor(stage);
	}

	// LoadURLScheduler on SchedulingStage needs some load monitor to
	// initialize.
	// this method generate the appropriate load monitors to use.
	// DO NOT use spider.getLoadMonitor() to acquire the load monitor for
	// initializing LoadURLScheduler.
	public LoadMonitor getLoadMonitorForScheduling()
	{
		List<LoadMonitor> lms = new LinkedList<LoadMonitor>();
		lms.add(this.getLoadMonitor(SpiderStageName.Filter));
		lms.add(this.getLoadMonitor(SpiderStageName.FetchingContent));
		lms.add(this.getLoadMonitor(SpiderStageName.ExtractingHyperlink));
		lms.add(this.getLoadMonitor(SpiderStageName.Analyzing));
		lms.add(this.getLoadMonitor(SpiderStageName.Recursion));
		lms.add(this.getLoadMonitor(SpiderStageName.Delivering));
		return new LoadMonitorGroup(lms.toArray(new LoadMonitor[0]));
	}

	public synchronized void initialize(SpiderAttributes attr)
	{
		if (null == attr)
		{
			throw new IllegalArgumentException("attr should not be null");
		}
		if (initialized)
		{
			throw new IllegalStateException("already initialized");
		}
		if (shutdown)
		{
			throw new IllegalStateException("already shutdown");
		}

		// 添加Spider自己需要使用的监听器
		attr.getBeginningListenerStageListeners().add(0, new SpiderListener()
		{
			@Override
			public void onBegin(URL url, int recursion)
			{
				assert (url != null) : Assertion.declare();
				assert (recursion >= 0);

				Spider.this.onTaskBegin(url, recursion);
			}

			@Override
			public void onEnd(WebPage page)
			{
				assert (page != null) : Assertion.declare();

				assert (false) : Assertion.declare();
			}

		});
		attr.getEndingListenerStageListeners().add(0, new SpiderListener()
		{

			@Override
			public void onBegin(URL url, int recursion)
			{
				assert (url != null) : Assertion.declare();
				assert (recursion >= 0) : Assertion.declare();

				assert (false) : Assertion.declare();
			}

			@Override
			public void onEnd(WebPage page)
			{
				assert (page != null) : Assertion.declare();

				Spider.this.onTaskEnd(page);
			}

		});

		// 初始化线程池
		scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
		cpuBoundExecutor = Executors.newFixedThreadPool(attr
				.getCpuBoundThreadNum());
		netBoundExecutor = Executors.newFixedThreadPool(attr
				.getNetBoundThreadNum());
		diskBoundExecutor = Executors.newFixedThreadPool(attr
				.getDiskBoundThreadNum());

		// 初始化各个Stage
		constructingStage.setStage(new ConstructingStage(normalizationStage,
				attr.getConstructingStageAbortPolicy()));
		normalizationStage.setStage(new NormalizationStage(
				beginningListenerStage, attr.getNormalizationStageNormalizer(),
				attr.getNormalizationStageAbortPolicy()));
		beginningListenerStage.setStage(new BeginningListenerStage(
				prefilterStage, attr.getBeginningListenerStageListeners()
						.toArray(new SpiderListener[0]), attr
						.getBeginningListenerStageAbortPolicy()));
		prefilterStage.setStage(new PrefilterStage(schedulingStage, attr
				.getPrefilterStageFilters().toArray(new URLFilter[0]), attr
				.getPrefilterStageAbortPolicy()));
		schedulingStage.setStage(new SchedulingStage(filterStage, attr
				.getSchedulingStageSchedulers().toArray(new URLScheduler[0]),
				attr.getSchedulingStageAbortPolicy(), scheduledExecutor, attr
						.getSchedulingStageMaxScheduleCount()));
		filterStage.setStage(new FilterStage(fetchingContentStage, attr
				.getFilterStageFilters().toArray(new URLFilter[0]), attr
				.getFilterStageAbortPolicy(), diskBoundExecutor));
		fetchingContentStage
				.setStage(new FetchingContentStage(extractingHyperlinkStage,
						attr.getFetchingContentStageFetcher(), attr
								.getFetchingContentStageAbortPolicy(),
						netBoundExecutor));
		extractingHyperlinkStage
				.setStage(new ExtractingHyperlinkStage(analyzingStage, attr
						.getExtractingHyperlinkStageExtractor(), attr
						.getExtractingHyperlinkStageAbortPolicy(),
						cpuBoundExecutor));
		analyzingStage.setStage(new AnalyzingStage(stampingStage, attr
				.getAnalyzingStageAnalyzer(), attr
				.getAnalyzingStageAbortPolicy(), cpuBoundExecutor));
		stampingStage.setStage(new StampingStage(recursionStage, attr
				.getStampingStageAbortPolicy()));
		recursionStage.setStage(new RecursionStage(normalizationStage,
				endingListenerStage, attr.getRecursionStageNormalizer(), attr
						.getRecursionStageFilters()
						.toArray(new RecursionHyperlinkFilter[0]), attr
						.getRecursionStageAbortPolicy(), cpuBoundExecutor));
		endingListenerStage.setStage(new EndingListenerStage(deliveringStage,
				attr.getEndingListenerStageListeners()
						.toArray(new SpiderListener[0]), attr
						.getEndingListenerStageAbortPolicy()));
		deliveringStage.setStage(new DeliveringStage(null, store, attr
				.getDeliveringStageAbortPolicy(), diskBoundExecutor));

		this.initialized = true;
	}

	private void onTaskBegin(URL url, int recursion)
	{
		assert (url != null) : Assertion.declare();
		assert (recursion >= 0) : Assertion.declare();

		// do nothing, reserved for future
	}

	private void onTaskEnd(WebPage page)
	{
		assert (page != null) : Assertion.declare();

		// do nothing, reserved for future
	}

	// =============================================================

	@Override
	public void input(URL input)
	{
		if (null == input)
		{
			throw new IllegalArgumentException("input should not be null");
		}
		if (!initialized)
		{
			throw new IllegalStateException("not initialized");
		}
		if (shutdown)
		{
			throw new IllegalStateException("already shutdown");
		}

		try
		{
			this.constructingStage.input(input);
		}
		catch (RejectedInputException ex)
		{
			ex.printStackTrace();
			assert (false);
		}
	}

	@Override
	public int getTaskCount()
	{
		int sum = 0;

		sum += constructingStage.getTaskCount();
		sum += normalizationStage.getTaskCount();
		sum += beginningListenerStage.getTaskCount();
		sum += prefilterStage.getTaskCount();
		sum += schedulingStage.getTaskCount();
		sum += filterStage.getTaskCount();
		sum += fetchingContentStage.getTaskCount();
		sum += extractingHyperlinkStage.getTaskCount();
		sum += analyzingStage.getTaskCount();
		sum += stampingStage.getTaskCount();
		sum += recursionStage.getTaskCount();
		sum += endingListenerStage.getTaskCount();
		sum += deliveringStage.getTaskCount();

		return sum;
	}

	public void shutdown()
	{
		shutdown = true;
		scheduledExecutor.shutdownNow();
		cpuBoundExecutor.shutdownNow();
		netBoundExecutor.shutdownNow();
		diskBoundExecutor.shutdownNow();
	}

}
