package org.accela.spider.stage;

import org.accela.spider.data.WebPage;
import org.accela.spider.strategy.AbortPolicy;
import org.accela.spider.strategy.URLFilter;
import org.accela.spider.strategy.URLFilterGroup;
import org.accela.spider.strategy.impl.DelegateAbortPolicy;
import org.accela.stage.InstantStage;
import org.accela.stage.RejectedOutputException;
import org.accela.stage.Stage;

//this stage contains simple, quick filters that need no IO resource. so that
//it can be put in front of Schedule Stage and need no parallelization.
public class PrefilterStage extends InstantStage<WebPage>
{
	private URLFilterGroup filters = null;

	private Stage<WebPage> nextStage = null;

	private AbortPolicy<WebPage, WebPage, String> abortPolicy = null;

	public PrefilterStage(Stage<WebPage> nextStage, URLFilter[] filters,
			AbortPolicy<WebPage, WebPage, String> abortPolicy)
	{
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
