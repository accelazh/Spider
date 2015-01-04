package org.accela.spider.stage.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.accela.spider.data.Content;
import org.accela.spider.data.WebPage;
import org.accela.spider.data.impl.TextContent;
import org.accela.spider.stage.ExtractingHyperlinkStage;
import org.accela.spider.strategy.Hyperlink;
import org.accela.spider.strategy.impl.TextContentFetcher;
import org.accela.spider.strategy.impl.URLOnlyTextHyperlinkExtractor;
import org.accela.spider.util.URL;
import org.accela.stage.RejectedInputException;
import org.accela.stage.Stage;

import junit.framework.TestCase;

public class TestExtractingHyperlinkStage extends TestCase
{
	public void testRejection() throws InterruptedException,
			MalformedURLException
	{
		TesterAbortPolicy<WebPage, WebPage, String> p = new TesterAbortPolicy<WebPage, WebPage, String>();
		ExtractingHyperlinkStage s = new ExtractingHyperlinkStage(
				new RejectiveStage<WebPage>(),
				new URLOnlyTextHyperlinkExtractor(), p, Executors
						.newCachedThreadPool());
		for (int i = 0; i < 100; i++)
		{
			try
			{
				WebPage page = new WebPage(new URL("http://www.google.com"));
				page.setContent(new TextContent(""));
				s.input(page);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
		}

		Thread.sleep(10);

		assert (p.getInvokeCount() == 100) : p.getInvokeCount();
	}

	// network connection presumed
	public void testExtracting() throws IOException, InterruptedException
	{
		final AtomicBoolean failed = new AtomicBoolean(true);

		URL url = new URL("http://www.sina.com.cn");
		TextContentFetcher fetcher = new TextContentFetcher();
		Content content = fetcher.fetchContent(url);
		WebPage page = new WebPage(url);
		page.setContent(content);
		
		long startTime=System.nanoTime();
		URLOnlyTextHyperlinkExtractor extractor=new URLOnlyTextHyperlinkExtractor();
		List<Hyperlink> links=extractor.extract(url, content);
		assert(links.size()>1000);
		long endTime=System.nanoTime();
		//System.out.println(endTime-startTime);
		
		startTime=System.nanoTime();
		links=extractor.extract(url, content);	//连续两次对同一文本进行处理，速度会大大提升
		endTime=System.nanoTime();
		//System.out.println(endTime-startTime);
		
		TesterAbortPolicy<WebPage, WebPage, String> p = new TesterAbortPolicy<WebPage, WebPage, String>();
		ExtractingHyperlinkStage s = new ExtractingHyperlinkStage(
				new Stage<WebPage>()
				{
					@Override
					public int getTaskCount()
					{
						return 0;
					}

					@Override
					public void input(WebPage input)
							throws RejectedInputException
					{
						if (input.getLinks() == null
								|| input.getLinks().size() < 1000)
						{
							failed.set(true);
							System.err
									.println("input.getLinks.size() is too small: "
											+ input.getLinks().size());
							assert (false);

							return;
						}

						failed.set(false);
					}
				}, new URLOnlyTextHyperlinkExtractor(), p, Executors
						.newCachedThreadPool());

		try
		{
			s.input(page);
		}
		catch (RejectedInputException ex)
		{
			ex.printStackTrace();
			assert (false);
		}

		Thread.sleep((long)((endTime-startTime)/1000000*1.4));

		assert(p.getInvokeCount()==0);
		assert (!failed.get());
	}
}
