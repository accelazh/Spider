package org.accela.spider.stage;

import org.accela.common.Assertion;
import org.accela.spider.data.WebPage;
import org.accela.spider.strategy.AbortPolicy;
import org.accela.spider.strategy.impl.DelegateAbortPolicy;
import org.accela.stage.InstantStage;
import org.accela.stage.RejectedOutputException;
import org.accela.stage.Stage;

public class StampingStage extends InstantStage<WebPage>
{
	private Stage<WebPage> nextStage = null;

	private AbortPolicy<WebPage, WebPage, String> abortPolicy = null;

	public StampingStage(Stage<WebPage> nextStage,
			AbortPolicy<WebPage, WebPage, String> abortPolicy)
	{
		if (null == nextStage)
		{
			throw new IllegalArgumentException("nextStage should not be null");
		}

		this.nextStage = nextStage;
		this.abortPolicy = new DelegateAbortPolicy<WebPage, WebPage, String>(abortPolicy);
	}

	@Override
	protected void process(WebPage input)
	{
		assert (input.getURL() != null):Assertion.declare();
		assert (input.getContent() != null):Assertion.declare();
		assert (input.getLinks() != null):Assertion.declare();
		assert (input.getAnalysis() != null):Assertion.declare();

		input.setStamp(System.currentTimeMillis());

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
