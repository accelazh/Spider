package org.accela.spider.stage;

import org.accela.common.Assertion;
import org.accela.spider.util.URL;

import org.accela.spider.data.WebPage;
import org.accela.spider.strategy.AbortPolicy;
import org.accela.spider.strategy.URLNormalizer;
import org.accela.spider.strategy.impl.DelegateAbortPolicy;
import org.accela.stage.InstantStage;
import org.accela.stage.RejectedOutputException;
import org.accela.stage.Stage;

//the normalizer should be simple, quick and cpu bound. so that normalizer stage doesn't 
//need parallelization. so that Normalization Stage can be put in front of Schedule Stage.
public class NormalizationStage extends InstantStage<WebPage>
{
	private URLNormalizer normalizer = null;

	private Stage<WebPage> nextStage = null;

	private AbortPolicy<WebPage, WebPage, String> abortPolicy = null;

	public NormalizationStage(Stage<WebPage> nextStage, URLNormalizer normalizer,
			AbortPolicy<WebPage, WebPage, String> abortPolicy)
	{
		if (null == nextStage)
		{
			throw new IllegalArgumentException("nextStage should not be null");
		}
		if (null == normalizer)
		{
			throw new IllegalArgumentException("cleaner should not be null");
		}

		this.nextStage = nextStage;
		this.normalizer = normalizer;
		this.abortPolicy = new DelegateAbortPolicy<WebPage, WebPage, String>(abortPolicy);
	}

	@Override
	protected void process(WebPage input)
	{
		URL newURL=normalizer.normalize(input.getURL());
		if(null==newURL)
		{
			abortPolicy.onAbort(
					true,
					this,
					nextStage,
					input,
					null,
					"URL normalizer returns null, which is not tolerated",
					null);
			
			assert(false):Assertion.declare();
			return;
		}
		
		input=new WebPage(newURL, input.getRecursion());
		
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
