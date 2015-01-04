package org.accela.spider.stage.test;

import java.net.MalformedURLException;
import java.util.concurrent.Executors;

import org.accela.spider.data.WebPage;
import org.accela.spider.data.impl.MemoryWebPageStore;
import org.accela.spider.stage.FilterStage;
import org.accela.spider.strategy.URLFilter;
import org.accela.spider.strategy.impl.DateURLFilter;
import org.accela.spider.util.URL;
import org.accela.stage.RejectedInputException;

import junit.framework.TestCase;

public class TestFilterStage extends TestCase
{
	public void testRejection() throws MalformedURLException,
			InterruptedException
	{
		TesterAbortPolicy<WebPage, WebPage, String> p = new TesterAbortPolicy<WebPage, WebPage, String>();
		FilterStage s = new FilterStage(new RejectiveStage<WebPage>(),
				new URLFilter[0], p, Executors.newCachedThreadPool());
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

		Thread.sleep(1);

		assert (p.getInvokeCount() == 100) : p.getInvokeCount();
	}

	public void testFilter() throws RejectedInputException,
			MalformedURLException, InterruptedException
	{
		final long UPDATE_INTERVAL = 1000;
		EmptyStage<WebPage> es = new EmptyStage<WebPage>();
		MemoryWebPageStore store = new MemoryWebPageStore();
		TesterAbortPolicy<WebPage, WebPage, String> p = new TesterAbortPolicy<WebPage, WebPage, String>();
		FilterStage s = new FilterStage(es,
				new URLFilter[] { new DateURLFilter(store, UPDATE_INTERVAL) },
				p, Executors.newCachedThreadPool());

		URL url1 = new URL("http://www.sina1.com.cn");
		WebPage page1 = new WebPage(url1);
		page1.setStamp(System.currentTimeMillis());

		URL url2 = new URL("http://www.sina2.com.cn");
		WebPage page2 = new WebPage(url2);
		page2.setStamp(System.currentTimeMillis());

		s.input(page1);
		s.input(page2);
		Thread.sleep(1);
		assert (es.getInvokeCount() == 2):es.getInvokeCount();

		store.put(page1);
		store.put(page2);

		s.input(page1);
		s.input(page2);
		Thread.sleep(100);
		assert (es.getInvokeCount() == 2):es.getInvokeCount();

		Thread.sleep(1000);

		s.input(page1);
		s.input(page2);
		Thread.sleep(10);
		assert (es.getInvokeCount() == 4):es.getInvokeCount();
	}
}
