package org.accela.spider.stage;

import java.io.IOException;

import org.accela.spider.util.ContentUnpreferedException;
import org.accela.spider.util.URL;
import java.util.concurrent.ExecutorService;

import org.accela.common.Assertion;
import org.accela.spider.data.Content;
import org.accela.spider.data.WebPage;
import org.accela.spider.strategy.AbortPolicy;
import org.accela.spider.strategy.ContentFetcher;
import org.accela.spider.strategy.impl.DelegateAbortPolicy;
import org.accela.stage.ConcurrentStage;
import org.accela.stage.RejectedOutputException;
import org.accela.stage.Stage;

public class FetchingContentStage extends ConcurrentStage<WebPage, WebPage>
{
	private Stage<WebPage> nextStage = null;
	
	private ContentFetcher fetcher=null;

	private AbortPolicy<WebPage, WebPage, String> abortPolicy = null;

	public FetchingContentStage(Stage<WebPage> nextStage, ContentFetcher fetcher, 
			AbortPolicy<WebPage, WebPage, String> abortPolicy,
			ExecutorService executor)
	{
		super(executor);

		if (null == nextStage)
		{
			throw new IllegalArgumentException("nextStage should not be null");
		}
		if (null == fetcher)
		{
			throw new IllegalArgumentException("fetcher should not be null");
		}

		this.nextStage = nextStage;
		this.fetcher=fetcher;
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
		URL url = input.getURL();
		assert (url != null):Assertion.declare();

		Content content=null;
		try
		{
			content = fetcher.fetchContent(url);
		}
		catch(ContentUnpreferedException ex)
		{
			abortPolicy.onAbort(
					false,
					this,
					nextStage,
					input,
					null,
					ex.getMessage(), null);
			return;
		}
		catch (IOException ex)
		{
			abortPolicy.onAbort(
					true,
					this,
					nextStage,
					input,
					null,
					"IOException occured when opening connection", ex);
			return;
		}
		
		if(null==content)
		{
			abortPolicy.onAbort(
					true,
					this,
					nextStage,
					input,
					null,
					"content fetcher returns null content, which is not tolerated",
					null);
			
			assert(false):Assertion.declare();
			return;
		}
		if(null==content.getText())
		{
			abortPolicy.onAbort(
					true,
					this,
					nextStage,
					input,
					null,
					"content fetcher returns content.getText()==null, which is not tolerated",
					null);
			
			assert(false):Assertion.declare();
			return;
		}

		input.setContent(content);
		
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
