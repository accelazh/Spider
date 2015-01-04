package org.accela.spider.stage;

import java.util.LinkedList;
import java.util.List;

import org.accela.common.Assertion;
import org.accela.spider.data.WebPage;
import org.accela.spider.event.SpiderListener;
import org.accela.spider.strategy.AbortPolicy;
import org.accela.spider.strategy.impl.DelegateAbortPolicy;
import org.accela.stage.InstantStage;
import org.accela.stage.RejectedOutputException;
import org.accela.stage.Stage;

public class EndingListenerStage extends InstantStage<WebPage>
{
	private Stage<WebPage> nextStage = null;

	private List<SpiderListener> listeners = null;

	private AbortPolicy<WebPage, WebPage, String> abortPolicy = null;

	public EndingListenerStage(Stage<WebPage> nextStage,
			SpiderListener[] listeners,
			AbortPolicy<WebPage, WebPage, String> abortPolicy)
	{
		if (null == nextStage)
		{
			throw new IllegalArgumentException("nextStage should not be null");
		}
		if (null == listeners)
		{
			throw new IllegalArgumentException("listeners should not be null");
		}

		this.nextStage = nextStage;
		this.listeners = new LinkedList<SpiderListener>();
		for (SpiderListener l : listeners)
		{
			if (null == l)
			{
				throw new IllegalArgumentException(
						"listener should not be null");
			}
			this.listeners.add(l);
		}
		this.abortPolicy = new DelegateAbortPolicy<WebPage, WebPage, String>(abortPolicy);

	}

	@Override
	protected void process(WebPage input)
	{
		for (SpiderListener l : listeners)
		{
			assert (l != null) : Assertion.declare();

			l.onEnd(input);
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
					"the next stage rejected the output",
					ex);
		}

	}
}
