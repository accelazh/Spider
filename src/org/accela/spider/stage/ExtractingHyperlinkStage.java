package org.accela.spider.stage;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.accela.common.Assertion;
import org.accela.spider.data.WebPage;
import org.accela.spider.strategy.AbortPolicy;
import org.accela.spider.strategy.Hyperlink;
import org.accela.spider.strategy.HyperlinkExtractor;
import org.accela.spider.strategy.impl.DelegateAbortPolicy;
import org.accela.stage.ConcurrentStage;
import org.accela.stage.RejectedOutputException;
import org.accela.stage.Stage;

public class ExtractingHyperlinkStage extends ConcurrentStage<WebPage, WebPage>
{
	private Stage<WebPage> nextStage = null;

	private AbortPolicy<WebPage, WebPage, String> abortPolicy = null;

	private HyperlinkExtractor extractor = null;

	public ExtractingHyperlinkStage(Stage<WebPage> nextStage,
			HyperlinkExtractor extractor,
			AbortPolicy<WebPage, WebPage, String> abortPolicy,
			ExecutorService executor)
	{
		super(executor);

		if (null == nextStage)
		{
			throw new IllegalArgumentException("nextStage should not be null");
		}
		if (null == extractor)
		{
			throw new IllegalArgumentException("extractor should not be null");
		}

		this.nextStage = nextStage;
		this.abortPolicy = new DelegateAbortPolicy<WebPage, WebPage, String>(abortPolicy);
		this.extractor = extractor;
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

		List<Hyperlink> links = input.getLinks();
		assert (links != null) : Assertion.declare();
		assert (links.size() == 0) : Assertion.declare();

		List<Hyperlink> newLinks = extractor.extract(input.getURL(), input
				.getContent());
		if (null == newLinks)
		{
			abortPolicy.onAbort(true,
					this,
					nextStage,
					input,
					null,
					"hyperlinkExtractor returns null",
					null);
			return;
		}

		for (Hyperlink link : newLinks)
		{
			if (null == link)
			{
				continue;
			}
			if (links.contains(link))
			{
				continue;
			}

			links.add(link);
		}

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
		}

	}

}
