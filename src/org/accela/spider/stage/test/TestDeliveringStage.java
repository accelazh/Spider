package org.accela.spider.stage.test;

import java.net.MalformedURLException;
import java.util.concurrent.Executors;

import org.accela.spider.data.WebPage;
import org.accela.spider.data.impl.EmptyAnalysis;
import org.accela.spider.data.impl.TextContent;
import org.accela.spider.stage.DeliveringStage;
import org.accela.spider.util.URL;
import org.accela.stage.RejectedInputException;

import junit.framework.TestCase;

public class TestDeliveringStage extends TestCase
{
	public void testRejection() throws MalformedURLException,
			InterruptedException
	{
		RejectiveWebPageStore store = new RejectiveWebPageStore();
		TesterAbortPolicy<WebPage, URL, String> p = new TesterAbortPolicy<WebPage, URL, String>();
		DeliveringStage s = new DeliveringStage(new RejectiveStage<URL>(),
				store, p, Executors.newCachedThreadPool());
		for (int i = 0; i < 100; i++)
		{
			try
			{
				WebPage page = new WebPage(new URL("http://www.google.com"));
				page.setContent(new TextContent(""));
				page.setAnalysis(new EmptyAnalysis());
				s.input(page);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
		}

		Thread.sleep(1);
		assert (p.getInvokeCount() == 200) : p.getInvokeCount();
	}
}
