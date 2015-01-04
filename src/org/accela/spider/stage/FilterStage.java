package org.accela.spider.stage;

import java.util.concurrent.ExecutorService;

import org.accela.spider.data.WebPage;
import org.accela.spider.strategy.AbortPolicy;
import org.accela.spider.strategy.URLFilter;
import org.accela.spider.strategy.URLFilterGroup;
import org.accela.spider.strategy.impl.DelegateAbortPolicy;
import org.accela.stage.ConcurrentStage;
import org.accela.stage.RejectedOutputException;
import org.accela.stage.Stage;

//this stage contains filters that are complex, slow or need database and IO resource, 
//in contrast to Prefilter Stage. So that this stage needs parallelization and must be
//put after Schedule Stage.
public class FilterStage extends ConcurrentStage<WebPage, WebPage>
{
	private URLFilterGroup filters = null;

	private Stage<WebPage> nextStage = null;

	private AbortPolicy<WebPage, WebPage, String> abortPolicy = null;

	public FilterStage(Stage<WebPage> nextStage, URLFilter[] filters,
			AbortPolicy<WebPage, WebPage, String> abortPolicy,
			ExecutorService executor)
	{
		super(executor);

		if (null == nextStage)
		{
			throw new IllegalArgumentException("nextStage should not be null");
		}
		if (null == filters)
		{
			throw new IllegalArgumentException("filters should not be null");
		}

		this.nextStage = nextStage;
		this.filters = new URLFilterGroup(filters);
		this.abortPolicy = new DelegateAbortPolicy<WebPage, WebPage, String>(abortPolicy);
	}

	@Override
	protected WebPage preprocess(WebPage input)
	{
		return input;
	}

	@Override
	protected void process(WebPage input)
	{
		if (!filters.accept(input.getURL()))
		{
			abortPolicy.onAbort(
					false,
					this,
					nextStage,
					input,
					null,
					"refused by filter", null);

			return;
		}
		
		try
		{
			this.output(nextStage, input);
		}
		catch (RejectedOutputException ex)
		{
			abortPolicy.onAbort(
					true,
					this,
					nextStage,
					input,
					input,
					"the next stage rejected the output", ex);
		}
	}

}
