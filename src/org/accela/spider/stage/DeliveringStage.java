package org.accela.spider.stage;

import org.accela.spider.util.URL;
import java.util.concurrent.ExecutorService;

import org.accela.common.Assertion;
import org.accela.spider.data.WebPage;
import org.accela.spider.data.WebPageStore;
import org.accela.spider.data.WebPageStoreException;
import org.accela.spider.strategy.AbortPolicy;
import org.accela.spider.strategy.impl.DelegateAbortPolicy;
import org.accela.stage.ConcurrentStage;
import org.accela.stage.RejectedOutputException;
import org.accela.stage.Stage;

public class DeliveringStage extends ConcurrentStage<WebPage, WebPage>
{
	private Stage<URL> nextStage = null; // ֮������һ���׶�ֻ����URL������Ϊ���ǵ��Ժ���ֲ��C++,����Ȱ�WebPage���ݸ����ݿ⣬�ְ�WebPage���ݸ���һ�׶Σ��������������Ȩģ�������⣬�Ӷ�����ָ����һ��ȥ�ͷ��ڴ�

	private AbortPolicy<WebPage, URL, String> abortPolicy = null;

	private WebPageStore store = null;

	// next stage can be null because this is the last stage by default
	public DeliveringStage(Stage<URL> nextStage,
			WebPageStore store,
			AbortPolicy<WebPage, URL, String> abortPolicy,
			ExecutorService executor)
	{
		super(executor);

		if (null == store)
		{
			throw new IllegalArgumentException("store should not be null");
		}

		this.nextStage = nextStage;
		this.store = store;
		this.abortPolicy =  new DelegateAbortPolicy<WebPage, URL, String>(abortPolicy);
	}

	@Override
	protected WebPage preprocess(WebPage input)
	{
		return input;
	}

	@Override
	protected void process(WebPage input)
	{
		assert (input.getURL() != null) : Assertion.declare();
		assert (input.getContent() != null) : Assertion.declare();
		assert (input.getLinks() != null) : Assertion.declare();
		assert (input.getAnalysis() != null) : Assertion.declare();

		// output to the next stage
		if (nextStage != null)
		{
			try
			{
				this.output(nextStage, input.getURL());
			}
			catch (RejectedOutputException ex)
			{
				abortPolicy.onAbort(true, this, nextStage, input, input
						.getURL(), "the next stage rejected the output", ex);
			}
		}

		// output to WebPageStore
		try
		{
			store.put(input);
		}
		catch (WebPageStoreException ex)
		{
			abortPolicy.onAbort(true,
					this,
					null,
					input,
					null,
					"failed to put input web page to WebPageStore",
					ex);
		}
	}

}
