package org.accela.spider;

import org.accela.spider.util.URL;
import java.util.LinkedList;
import java.util.List;

import org.accela.spider.data.WebPage;
import org.accela.spider.event.SpiderListener;
import org.accela.spider.strategy.AbortPolicy;
import org.accela.spider.strategy.Analyzer;
import org.accela.spider.strategy.ContentFetcher;
import org.accela.spider.strategy.LoadMonitor;
import org.accela.spider.strategy.RecursionHyperlinkFilter;
import org.accela.spider.strategy.HyperlinkExtractor;
import org.accela.spider.strategy.URLFilter;
import org.accela.spider.strategy.URLNormalizer;
import org.accela.spider.strategy.URLScheduler;
import org.accela.spider.strategy.impl.DateURLFilter;
import org.accela.spider.strategy.impl.DuplicatedWithParentRecursionHyperlinkFilter;
import org.accela.spider.strategy.impl.EmptyAnalyzer;
import org.accela.spider.strategy.impl.HttpURLFilter;
import org.accela.spider.strategy.impl.LimitedRecursionHyperlinkFilter;
import org.accela.spider.strategy.impl.LoadURLScheduler;
import org.accela.spider.strategy.impl.PolitenessURLScheduler;
import org.accela.spider.strategy.impl.PrinterAbortPolicy;
import org.accela.spider.strategy.impl.RepeatedURLFilter;
import org.accela.spider.strategy.impl.RobotsTxtFilter;
import org.accela.spider.strategy.impl.SameHostGroupRecursionHyperlinkFilter;
import org.accela.spider.strategy.impl.URLOnlyTextHyperlinkExtractor;
import org.accela.spider.strategy.impl.SimpleURLNormalizer;
import org.accela.spider.strategy.impl.SuffixURLFilter;
import org.accela.spider.strategy.impl.TaskIntervalURLScheduler;
import org.accela.spider.strategy.impl.TextContentFetcher;
import org.accela.spider.strategy.impl.TotalCountRecursionHyperlinkFilter;

public class SpiderAttributes
{
	// ====spider shared====
	private int cpuBoundThreadNum = Runtime.getRuntime().availableProcessors();

	private int netBoundThreadNum = 6 * cpuBoundThreadNum;

	private int diskBoundThreadNum = 2 * cpuBoundThreadNum;

	// ====constructing stage====
	private AbortPolicy<URL, WebPage, String> constructingStageAbortPolicy = new PrinterAbortPolicy<URL, WebPage, String>();

	// ====normalization stage====
	private AbortPolicy<WebPage, WebPage, String> normalizationStageAbortPolicy = new PrinterAbortPolicy<WebPage, WebPage, String>();

	private URLNormalizer normalizationStageNormalizer = new SimpleURLNormalizer(true);

	// ====beginning stage====
	private AbortPolicy<WebPage, WebPage, String> beginningListenerStageAbortPolicy = new PrinterAbortPolicy<WebPage, WebPage, String>();

	private List<SpiderListener> beginningListenerStageListeners = new LinkedList<SpiderListener>();

	// ====prefilter stage====
	private AbortPolicy<WebPage, WebPage, String> prefilterStageAbortPolicy = new PrinterAbortPolicy<WebPage, WebPage, String>();

	private List<URLFilter> prefilterStageFilters = new LinkedList<URLFilter>();

	// ====scheduling stage====
	private AbortPolicy<WebPage, WebPage, String> schedulingStageAbortPolicy = new PrinterAbortPolicy<WebPage, WebPage, String>();

	private List<URLScheduler> schedulingStageSchedulers = new LinkedList<URLScheduler>();

	private int schedulingStageMaxScheduleCount = -1;

	// ====filter stage====
	private AbortPolicy<WebPage, WebPage, String> filterStageAbortPolicy = new PrinterAbortPolicy<WebPage, WebPage, String>();

	private List<URLFilter> filterStageFilters = new LinkedList<URLFilter>();

	// ===fetching content stage====
	private AbortPolicy<WebPage, WebPage, String> fetchingContentStageAbortPolicy = new PrinterAbortPolicy<WebPage, WebPage, String>();

	private ContentFetcher fetchingContentStageFetcher = new TextContentFetcher();

	// ====extracting hyperlink stage===
	private AbortPolicy<WebPage, WebPage, String> extractingHyperlinkStageAbortPolicy = new PrinterAbortPolicy<WebPage, WebPage, String>();

	private HyperlinkExtractor extractingHyperlinkStageExtractor = new URLOnlyTextHyperlinkExtractor();

	// ====analyzing stage====
	private AbortPolicy<WebPage, WebPage, String> analyzingStageAbortPolicy = new PrinterAbortPolicy<WebPage, WebPage, String>();

	private Analyzer analyzingStageAnalyzer = new EmptyAnalyzer();

	// ====stamping stage====
	private AbortPolicy<WebPage, WebPage, String> stampingStageAbortPolicy = new PrinterAbortPolicy<WebPage, WebPage, String>();

	// ====recursion stage====
	private AbortPolicy<WebPage, WebPage, String> recursionStageAbortPolicy = new PrinterAbortPolicy<WebPage, WebPage, String>();

	private URLNormalizer recursionStageNormalizer = normalizationStageNormalizer;

	private List<RecursionHyperlinkFilter> recursionStageFilters = new LinkedList<RecursionHyperlinkFilter>();

	// ====ending stage====
	private AbortPolicy<WebPage, WebPage, String> endingListenerStageAbortPolicy = new PrinterAbortPolicy<WebPage, WebPage, String>();

	private List<SpiderListener> endingListenerStageListeners = new LinkedList<SpiderListener>();

	// ====delivering stage====
	private AbortPolicy<WebPage, URL, String> deliveringStageAbortPolicy = new PrinterAbortPolicy<WebPage, URL, String>();

	public SpiderAttributes(Spider spider)
	{
		if (null == spider)
		{
			throw new IllegalArgumentException("spider should not be null");
		}

		prefilterStageFilters.add(new SuffixURLFilter(new String[] { "html",
				"htm", "shtml", "shtm", "asp", "aspx", "php", "jsp", "txt", "" }));
		prefilterStageFilters.add(new HttpURLFilter());
		prefilterStageFilters.add(new RepeatedURLFilter(3600 * 1000));

		schedulingStageMaxScheduleCount=1000;	//数字太大了会因为轮询而消耗过多cpu，而缓存任务的功能以后会由硬盘队列来完成
		
		schedulingStageSchedulers.add(new LoadURLScheduler(
				new LoadMonitor[] { spider.getLoadMonitorForScheduling() }, 10000,
				1000));
		schedulingStageSchedulers.add(new TaskIntervalURLScheduler(Integer.MAX_VALUE, 100));
		schedulingStageSchedulers
				.add(new PolitenessURLScheduler(10000, 100, 1000));

		filterStageFilters.add(new DateURLFilter(spider.getStore(),
				24 * 3600 * 1000));
		filterStageFilters.add(new RobotsTxtFilter(null, 24 * 3600 * 1000));

		recursionStageFilters.add(new DuplicatedWithParentRecursionHyperlinkFilter());
		recursionStageFilters.add(new LimitedRecursionHyperlinkFilter(
				Integer.MAX_VALUE));
		recursionStageFilters.add(new TotalCountRecursionHyperlinkFilter(
				Long.MAX_VALUE));
		recursionStageFilters.add(new SameHostGroupRecursionHyperlinkFilter());
	}

	public int getCpuBoundThreadNum()
	{
		return cpuBoundThreadNum;
	}

	public void setCpuBoundThreadNum(int cpuBoundThreadNum)
	{
		this.cpuBoundThreadNum = cpuBoundThreadNum;
	}

	public int getNetBoundThreadNum()
	{
		return netBoundThreadNum;
	}

	public void setNetBoundThreadNum(int netBoundThreadNum)
	{
		this.netBoundThreadNum = netBoundThreadNum;
	}

	public int getDiskBoundThreadNum()
	{
		return diskBoundThreadNum;
	}

	public void setDiskBoundThreadNum(int diskBoundThreadNum)
	{
		this.diskBoundThreadNum = diskBoundThreadNum;
	}

	public AbortPolicy<URL, WebPage, String> getConstructingStageAbortPolicy()
	{
		return constructingStageAbortPolicy;
	}

	public void setConstructingStageAbortPolicy(AbortPolicy<URL, WebPage, String> constructingStageAbortPolicy)
	{
		this.constructingStageAbortPolicy = constructingStageAbortPolicy;
	}

	public AbortPolicy<WebPage, WebPage, String> getNormalizationStageAbortPolicy()
	{
		return normalizationStageAbortPolicy;
	}

	public void setNormalizationStageAbortPolicy(AbortPolicy<WebPage, WebPage, String> normalizationStageAbortPolicy)
	{
		this.normalizationStageAbortPolicy = normalizationStageAbortPolicy;
	}

	public URLNormalizer getNormalizationStageNormalizer()
	{
		return normalizationStageNormalizer;
	}

	public void setNormalizationStageNormalizer(URLNormalizer normalizationStageNormalizer)
	{
		if (null == normalizationStageNormalizer)
		{
			throw new IllegalArgumentException(
					"normalizationStageNormalizer should not be null");
		}

		this.normalizationStageNormalizer = normalizationStageNormalizer;
	}

	public AbortPolicy<WebPage, WebPage, String> getBeginningListenerStageAbortPolicy()
	{
		return beginningListenerStageAbortPolicy;
	}

	public void setBeginningListenerStageAbortPolicy(AbortPolicy<WebPage, WebPage, String> beginningListenerStageAbortPolicy)
	{
		this.beginningListenerStageAbortPolicy = beginningListenerStageAbortPolicy;
	}

	public List<SpiderListener> getBeginningListenerStageListeners()
	{
		return beginningListenerStageListeners;
	}

	public AbortPolicy<WebPage, WebPage, String> getPrefilterStageAbortPolicy()
	{
		return prefilterStageAbortPolicy;
	}

	public void setPrefilterStageAbortPolicy(AbortPolicy<WebPage, WebPage, String> prefilterStageAbortPolicy)
	{
		this.prefilterStageAbortPolicy = prefilterStageAbortPolicy;
	}

	public List<URLFilter> getPrefilterStageFilters()
	{
		return prefilterStageFilters;
	}

	public AbortPolicy<WebPage, WebPage, String> getSchedulingStageAbortPolicy()
	{
		return schedulingStageAbortPolicy;
	}

	public void setSchedulingStageAbortPolicy(AbortPolicy<WebPage, WebPage, String> schedulingStageAbortPolicy)
	{
		this.schedulingStageAbortPolicy = schedulingStageAbortPolicy;
	}

	public List<URLScheduler> getSchedulingStageSchedulers()
	{
		return schedulingStageSchedulers;
	}

	public int getSchedulingStageMaxScheduleCount()
	{
		return schedulingStageMaxScheduleCount;
	}

	public void setSchedulingStageMaxScheduleCount(int schedulingStageMaxScheduleCount)
	{
		this.schedulingStageMaxScheduleCount = schedulingStageMaxScheduleCount;
	}

	public AbortPolicy<WebPage, WebPage, String> getFilterStageAbortPolicy()
	{
		return filterStageAbortPolicy;
	}

	public void setFilterStageAbortPolicy(AbortPolicy<WebPage, WebPage, String> filterStageAbortPolicy)
	{
		this.filterStageAbortPolicy = filterStageAbortPolicy;
	}

	public List<URLFilter> getFilterStageFilters()
	{
		return filterStageFilters;
	}

	public AbortPolicy<WebPage, WebPage, String> getFetchingContentStageAbortPolicy()
	{
		return fetchingContentStageAbortPolicy;
	}

	public void setFetchingContentStageAbortPolicy(AbortPolicy<WebPage, WebPage, String> fetchingContentStageAbortPolicy)
	{
		this.fetchingContentStageAbortPolicy = fetchingContentStageAbortPolicy;
	}

	public ContentFetcher getFetchingContentStageFetcher()
	{
		return fetchingContentStageFetcher;
	}

	public void setFetchingContentStageFetcher(ContentFetcher fetchingContentStageFetcher)
	{
		if (null == fetchingContentStageFetcher)
		{
			throw new IllegalArgumentException(
					"fetchingContentStageFetcher should not be null");
		}

		this.fetchingContentStageFetcher = fetchingContentStageFetcher;
	}

	public AbortPolicy<WebPage, WebPage, String> getExtractingHyperlinkStageAbortPolicy()
	{
		return extractingHyperlinkStageAbortPolicy;
	}

	public void setExtractingHyperlinkStageAbortPolicy(AbortPolicy<WebPage, WebPage, String> extractingHyperlinkStageAbortPolicy)
	{
		this.extractingHyperlinkStageAbortPolicy = extractingHyperlinkStageAbortPolicy;
	}

	public HyperlinkExtractor getExtractingHyperlinkStageExtractor()
	{
		return extractingHyperlinkStageExtractor;
	}

	public void setExtractingStageHyperlinkExtractor(HyperlinkExtractor extractingHyperlinkStageExtractor)
	{
		if (null == extractingHyperlinkStageExtractor)
		{
			throw new IllegalArgumentException(
					"extractingHyperlinkStageExtractor should not be null");
		}

		this.extractingHyperlinkStageExtractor = extractingHyperlinkStageExtractor;
	}

	public AbortPolicy<WebPage, WebPage, String> getAnalyzingStageAbortPolicy()
	{
		return analyzingStageAbortPolicy;
	}

	public void setAnalyzingStageAbortPolicy(AbortPolicy<WebPage, WebPage, String> analyzingStageAbortPolicy)
	{
		this.analyzingStageAbortPolicy = analyzingStageAbortPolicy;
	}

	public Analyzer getAnalyzingStageAnalyzer()
	{
		return analyzingStageAnalyzer;
	}

	public void setAnalyzingStageAnalyzer(Analyzer analyzingStageAnalyzer)
	{
		if (null == analyzingStageAnalyzer)
		{
			throw new IllegalArgumentException(
					"analyzingStageAnalyzer should not be null");
		}

		this.analyzingStageAnalyzer = analyzingStageAnalyzer;
	}

	public AbortPolicy<WebPage, WebPage, String> getStampingStageAbortPolicy()
	{
		return stampingStageAbortPolicy;
	}

	public void setStampingStageAbortPolicy(AbortPolicy<WebPage, WebPage, String> stampingStageAbortPolicy)
	{
		this.stampingStageAbortPolicy = stampingStageAbortPolicy;
	}

	public AbortPolicy<WebPage, WebPage, String> getRecursionStageAbortPolicy()
	{
		return recursionStageAbortPolicy;
	}

	public void setRecursionStageAbortPolicy(AbortPolicy<WebPage, WebPage, String> recursionStageAbortPolicy)
	{
		this.recursionStageAbortPolicy = recursionStageAbortPolicy;
	}

	public URLNormalizer getRecursionStageNormalizer()
	{
		return recursionStageNormalizer;
	}

	public void setRecursionStageNormalizer(URLNormalizer recursionStageNormalizer)
	{
		if (null == recursionStageNormalizer)
		{
			throw new IllegalArgumentException(
					"recursionStageNormalizer should not be null");
		}

		this.recursionStageNormalizer = recursionStageNormalizer;
	}

	public List<RecursionHyperlinkFilter> getRecursionStageFilters()
	{
		return recursionStageFilters;
	}

	public AbortPolicy<WebPage, WebPage, String> getEndingListenerStageAbortPolicy()
	{
		return endingListenerStageAbortPolicy;
	}

	public void setEndingListenerStageAbortPolicy(AbortPolicy<WebPage, WebPage, String> endingListenerStageAbortPolicy)
	{
		this.endingListenerStageAbortPolicy = endingListenerStageAbortPolicy;
	}

	public List<SpiderListener> getEndingListenerStageListeners()
	{
		return endingListenerStageListeners;
	}

	public AbortPolicy<WebPage, URL, String> getDeliveringStageAbortPolicy()
	{
		return deliveringStageAbortPolicy;
	}

	public void setDeliveringStageAbortPolicy(AbortPolicy<WebPage, URL, String> deliveringStageAbortPolicy)
	{
		this.deliveringStageAbortPolicy = deliveringStageAbortPolicy;
	}

}
