package org.accela.spider.stage.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;

import org.accela.spider.data.Content;
import org.accela.spider.data.WebPage;
import org.accela.spider.stage.FetchingContentStage;
import org.accela.spider.strategy.Hyperlink;
import org.accela.spider.strategy.impl.URLOnlyTextHyperlinkExtractor;
import org.accela.spider.strategy.impl.TextContentFetcher;
import org.accela.spider.util.URL;
import org.accela.stage.RejectedInputException;

import junit.framework.TestCase;

public class TestFetchingContentStage extends TestCase
{
	// network connection presumed
	public void testRejection() throws MalformedURLException,
			InterruptedException
	{
		TesterAbortPolicy<WebPage, WebPage, String> p = new TesterAbortPolicy<WebPage, WebPage, String>();
		FetchingContentStage s = new FetchingContentStage(
				new RejectiveStage<WebPage>(), new TextContentFetcher(), p,
				Executors.newCachedThreadPool());
		for (int i = 0; i < 100; i++)
		{
			try
			{
				WebPage page = new WebPage(new URL("http://www.google.com"));
				s.input(page);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
		}

		Thread.sleep(8000);

		assert (p.getInvokeCount() == 100) : p.getInvokeCount();
	}

	// network connection presumed
	public void testPass() throws MalformedURLException,
			RejectedInputException, InterruptedException
	{
		TesterAbortPolicy<WebPage, WebPage, String> p = new TesterAbortPolicy<WebPage, WebPage, String>();
		FetchingContentStage s = new FetchingContentStage(
				new EmptyStage<WebPage>(), new TextContentFetcher(), p,
				Executors.newCachedThreadPool());

		WebPage page = new WebPage(new URL("http://www.sina.com.cn"));
		s.input(page);

		Thread.sleep(10000);

		assert (p.getInvokeCount() == 0);
		assert (page.getContent() != null);
		assert (page.getContent().getText().length() > 1000);
	}

	// network connection presumed
	public void testPerformance() throws MalformedURLException,
			InterruptedException
	{
		WebPage page1 = new WebPage(new URL("http://www.sina.com.cn"));
		WebPage page2 = new WebPage(new URL("http://www.google.com"));
		WebPage page3 = new WebPage(new URL(
				"http://www.abcdefghijklmnop.com.cn/nice/hello/world"));
		WebPage page4 = new WebPage(new URL("http://chafanhou.com/"));
		WebPage page5 = new WebPage(new URL(
				"https://computing.llnl.gov/tutorials/pthreads/"));
		WebPage page6 = new WebPage(new URL(
				"http://www.ibm.com/developerworks/cn/java/j-jtp0730/"));
		WebPage page7 = new WebPage(new URL(
				"http://download.winzip.com/winzip145.exe"));

		WebPage[] pages = new WebPage[] { page1, page2, page3, page4, page5,
				page6, page7 };
		for (int i = 0; i < pages.length; i++)
		{
			assert (pages[i].getContent() == null);
		}

		TesterAbortPolicy<WebPage, WebPage, String> p = new TesterAbortPolicy<WebPage, WebPage, String>();
		FetchingContentStage s = new FetchingContentStage(
				new EmptyStage<WebPage>(), new TextContentFetcher(), p,
				Executors.newCachedThreadPool());

		for (int i = 0; i < pages.length; i++)
		{
			try
			{
				s.input(pages[i]);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
		}

		Thread.sleep(10000);
		for (int i = 0; i < pages.length; i++)
		{
			if (pages[i] == page3 || pages[i] == page7)
			{
				assert (pages[i].getContent() == null);
			}
			else
			{
				assert (pages[i].getContent() != null);
			}
		}
		assert (p.getInvokeCount() == 2) : p.getInvokeCount();
	}
	
	//network connection presumed
	public void testDownloadAllLinksOnSina() throws MalformedURLException, IOException, InterruptedException
	{
		URL url=new URL("http://www.sina.com.cn");
		TextContentFetcher fetcher=new TextContentFetcher();
		Content content=fetcher.fetchContent(url);
		URLOnlyTextHyperlinkExtractor extractor=new URLOnlyTextHyperlinkExtractor();
		List<Hyperlink> links=extractor.extract(url, content);
		assert(links.size()>1000);
		
		List<WebPage> pages=new LinkedList<WebPage>();
		for(Hyperlink l : links)
		{
			pages.add(new WebPage(l.getURL()));
		}
		
		TesterAbortPolicy<WebPage, WebPage, String> p = new TesterAbortPolicy<WebPage, WebPage, String>();
		FetchingContentStage s = new FetchingContentStage(
				new EmptyStage<WebPage>(), new TextContentFetcher(), p,
				Executors.newCachedThreadPool());
		
		for(WebPage page : pages)
		{
			try
			{
				s.input(page);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert(false);
			}
		}
		
		Thread.sleep(10000);
		
		int sum=0;
		for(WebPage page : pages)
		{
			if(page.getContent()!=null)
			{
				sum++;
			}
		}
		assert(sum>40);
		//System.out.println("total downloaded: "+sum+"/"+pages.size());
	}
}
