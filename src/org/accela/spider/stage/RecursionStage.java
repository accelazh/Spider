package org.accela.spider.stage;

import org.accela.spider.util.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.accela.common.Assertion;
import org.accela.spider.data.WebPage;
import org.accela.spider.strategy.AbortPolicy;
import org.accela.spider.strategy.Hyperlink;
import org.accela.spider.strategy.RecursionHyperlinkFilter;
import org.accela.spider.strategy.RecursionHyperlinkFilterGroup;
import org.accela.spider.strategy.URLNormalizer;
import org.accela.spider.strategy.impl.DelegateAbortPolicy;
import org.accela.stage.ConcurrentStage;
import org.accela.stage.RejectedOutputException;
import org.accela.stage.Stage;

public class RecursionStage extends ConcurrentStage<WebPage, WebPage>
{
	private Stage<WebPage> recursionStage = null;

	private Stage<WebPage> nextStage = null;

	private URLNormalizer normalizer = null;

	private RecursionHyperlinkFilterGroup filters = null;

	private AbortPolicy<WebPage, WebPage, String> abortPolicy = null;

	public RecursionStage(Stage<WebPage> recursionStage,
			Stage<WebPage> nextStage,
			URLNormalizer normalizer,
			RecursionHyperlinkFilter[] filters,
			AbortPolicy<WebPage, WebPage, String> abortPolicy,
			ExecutorService executor)
	{
		super(executor);

		if (null == recursionStage)
		{
			throw new IllegalArgumentException(
					"recursionStage should not be null");
		}
		if (null == nextStage)
		{
			throw new IllegalArgumentException("nextStage should not be null");
		}
		if (null == normalizer)
		{
			throw new IllegalArgumentException("normalizer should not be null");
		}
		if (null == filters)
		{
			throw new IllegalArgumentException("filters should not be null");
		}

		this.recursionStage = recursionStage;
		this.nextStage = nextStage;
		this.normalizer = normalizer;
		this.filters = new RecursionHyperlinkFilterGroup(filters);
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
		assert (input.getAnalysis() != null) : Assertion.declare();

		// output to the next stage
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

		// find which url to put into recursion
		URL normalizedParentURL = normalizer.normalize(input.getURL());
		Set<URL> recursionURLs = new HashSet<URL>();
		for (Hyperlink link : input.getLinks())
		{
			assert (link != null) : Assertion.declare();

			URL normalizedLinkURL = normalizer.normalize(link.getURL());
			if (recursionURLs.contains(normalizedLinkURL)) // 这一步用来避免重复的URL消耗如TotalCountRecursionHyperlinkFilter这样的过滤器的成功accept次数
			{
				continue;
			}

			if (filters.accept(input,
					normalizedParentURL,
					link,
					normalizedLinkURL))
			{
				// duplicated links will not be put into recursion, this is
				// important to avoid possibly making prefilter and filter
				// stages too busy, if encountered with badly designed webs.
				recursionURLs.add(normalizedLinkURL);
			}
			else
			{
				abortPolicy.onAbort(false,
						this,
						recursionStage,
						input,
						null,
						"refused by filter: " + normalizedLinkURL,
						null);
			}
		}

		// output recursion urls
		for (URL url : recursionURLs)
		{
			assert (url != null) : Assertion.declare();

			WebPage newPage = new WebPage(url, input.getRecursion() + 1);
			try
			{
				this.output(recursionStage, newPage);
			}
			catch (RejectedOutputException ex)
			{
				abortPolicy.onAbort(true,
						this,
						recursionStage,
						input,
						newPage,
						"the recursion stage rejected the output",
						ex);
			}

		}

	}
}
