package org.accela.spider.stage;

import org.accela.spider.util.URL;

import org.accela.spider.data.WebPage;
import org.accela.spider.strategy.AbortPolicy;
import org.accela.spider.strategy.impl.DelegateAbortPolicy;
import org.accela.stage.InstantStage;
import org.accela.stage.RejectedOutputException;
import org.accela.stage.Stage;

public class ConstructingStage extends InstantStage<URL>
{
	private Stage<WebPage> nextStage = null;

	private AbortPolicy<URL, WebPage, String> abortPolicy = null;

	public ConstructingStage(Stage<WebPage> nextStage,
			AbortPolicy<URL, WebPage, String> abortPolicy)
	{
		if (null == nextStage)
		{
			throw new IllegalArgumentException("nextStage should not be null");
		}

		this.nextStage = nextStage;
		this.abortPolicy =  new DelegateAbortPolicy<URL, WebPage, String>(abortPolicy);
	}

	@Override
	protected void process(URL input)
	{
		WebPage newPage = new WebPage(input, 0);
		try
		{
			this.output(nextStage, newPage);
		}
		catch (RejectedOutputException ex)
		{
			abortPolicy.onAbort(
					true,
					this,
					nextStage,
					input,
					newPage,
					"the next stage rejected the output",
					ex);
		}

	}

}
