package org.accela.spider.stage.test;

import java.net.MalformedURLException;

import org.accela.spider.data.WebPage;
import org.accela.spider.event.SpiderListener;
import org.accela.spider.stage.BeginningListenerStage;
import org.accela.spider.util.URL;
import org.accela.stage.RejectedInputException;

import junit.framework.TestCase;

public class TestBeginningListenerStage extends TestCase
{
	public void testRejection() throws MalformedURLException
	{
		TesterSpiderListener l1 = new TesterSpiderListener();
		TesterSpiderListener l2 = new TesterSpiderListener();
		TesterAbortPolicy<WebPage, WebPage, String> p = new TesterAbortPolicy<WebPage, WebPage, String>();
		BeginningListenerStage s = new BeginningListenerStage(
				new RejectiveStage<WebPage>(), new SpiderListener[] { l1, l2 },
				p);
		for (int i = 0; i < 100; i++)
		{
			try
			{
				s.input(new WebPage(new URL("http://www.sina.com.cn")));
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
		}

		assert (p.getInvokeCount() == 100);
		assert (l1.getbCount() == 0);
		assert (l2.getbCount() == 0);
		assert (l1.geteCount() == 0);
		assert (l2.geteCount() == 0);
	}

	public void testListener() throws MalformedURLException
	{
		TesterSpiderListener l1 = new TesterSpiderListener();
		TesterSpiderListener l2 = new TesterSpiderListener();
		TesterAbortPolicy<WebPage, WebPage, String> p = new TesterAbortPolicy<WebPage, WebPage, String>();
		BeginningListenerStage s = new BeginningListenerStage(
				new EmptyStage<WebPage>(), new SpiderListener[] { l1, l2 },
				p);
		for (int i = 0; i < 100; i++)
		{
			try
			{
				s.input(new WebPage(new URL("http://www.sina.com.cn")));
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
		}

		assert (p.getInvokeCount() == 0);
		assert (l1.getbCount() == 100);
		assert (l2.getbCount() == 100);
		assert (l1.geteCount() == 0);
		assert (l2.geteCount() == 0);
	}
}
