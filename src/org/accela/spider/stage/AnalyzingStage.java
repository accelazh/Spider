package org.accela.spider.stage;

import java.util.Collections;
import java.util.concurrent.ExecutorService;

import org.accela.common.Assertion;
import org.accela.spider.data.Analysis;
import org.accela.spider.data.WebPage;
import org.accela.spider.strategy.AbortPolicy;
import org.accela.spider.strategy.Analyzer;
import org.accela.spider.strategy.impl.DelegateAbortPolicy;
import org.accela.stage.ConcurrentStage;
import org.accela.stage.RejectedOutputException;
import org.accela.stage.Stage;

public class AnalyzingStage extends ConcurrentStage<WebPage, WebPage>
{
	private Stage<WebPage> nextStage = null;

	private Analyzer analyser = null;

	private AbortPolicy<WebPage, WebPage, String> abortPolicy = null;

	public AnalyzingStage(Stage<WebPage> nextStage, Analyzer analyser,
			AbortPolicy<WebPage, WebPage, String> abortPolicy,
			ExecutorService executor)
	{
		super(executor);

		if (null == nextStage)
		{
			throw new IllegalArgumentException("nextStage should not be null");
		}
		if (null == analyser)
		{
			throw new IllegalArgumentException("analyser should not be null");
		}

		this.nextStage = nextStage;
		this.analyser = analyser;
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
		assert (input.getURL() != null) : Assertion.declare();
		assert (input.getContent() != null) : Assertion.declare();
		assert (input.getLinks() != null) : Assertion.declare();

		Analysis analysis = analyser.analyse(
				input.getURL(),
				input.getContent(),
				Collections.unmodifiableList(input.getLinks()));

		if(null==analysis)
		{
			abortPolicy.onAbort(
					true,
					this,
					nextStage,
					input,
					null,
					"analyser returns null analysis, which is not tolerated",
					null);
			
			assert(false):Assertion.declare();
			return;
		}
		
		input.setAnalysis(analysis);

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
