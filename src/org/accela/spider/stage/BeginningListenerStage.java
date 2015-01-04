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

//this stage should be put after normalization stage, in order to make sure
//that when a task is finished, the url remains the same in beginning listeners 
//and in ending listeners.
public class BeginningListenerStage extends InstantStage<WebPage>
{
	private Stage<WebPage> nextStage = null;

	private List<SpiderListener> listeners = null;

	private AbortPolicy<WebPage, WebPage, String> abortPolicy = null;

	// listeners must be initialized in constructor to avoid competing problems
	// such as
	// using getters/setters to access listeners while they are invoked in
	// process(...)
	public BeginningListenerStage(Stage<WebPage> nextStage,
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
		assert (input.getURL() != null) : Assertion.declare();

		try
		{
			this.output(nextStage, input);
		}
		catch (RejectedOutputException ex)
		{
			abortPolicy.onAbort(true,
					this,
					nextStage,
					input,
					input,
					"the next stage rejected the output",
					ex);
			return;
		}

		for (SpiderListener l : listeners)
		{
			assert (l != null) : Assertion.declare();

			l.onBegin(input.getURL(), input.getRecursion());
		}
	}
}
