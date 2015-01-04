package org.accela.spider.stage.test;

import java.net.MalformedURLException;

import org.accela.spider.data.WebPage;
import org.accela.spider.data.impl.EmptyAnalysis;
import org.accela.spider.data.impl.TextContent;
import org.accela.spider.stage.StampingStage;
import org.accela.spider.util.URL;
import org.accela.stage.RejectedInputException;

import junit.framework.TestCase;

public class TestStampingStage extends TestCase
{
	public void testRejection() throws MalformedURLException,
			InterruptedException
	{
		TesterAbortPolicy<WebPage, WebPage, String> p = new TesterAbortPolicy<WebPage, WebPage, String>();
		StampingStage s = new StampingStage(new RejectiveStage<WebPage>(), p);
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

		assert (p.getInvokeCount() == 100) : p.getInvokeCount();
	}
	
	public void testStamping() throws MalformedURLException
	{
		TesterAbortPolicy<WebPage, WebPage, String> p = new TesterAbortPolicy<WebPage, WebPage, String>();
		StampingStage s = new StampingStage(new EmptyStage<WebPage>(), p);
		for (int i = 0; i < 100; i++)
		{
			try
			{
				WebPage page = new WebPage(new URL("http://www.google.com"));
				page.setContent(new TextContent(""));
				page.setAnalysis(new EmptyAnalysis());
				assert(page.getStamp()==0);
				
				s.input(page);
				
				assert(System.currentTimeMillis()-page.getStamp()<=1);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
		}

		assert (p.getInvokeCount() == 0) : p.getInvokeCount();
	}
}
