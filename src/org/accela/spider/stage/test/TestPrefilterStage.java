package org.accela.spider.stage.test;

import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.accela.spider.data.WebPage;
import org.accela.spider.stage.PrefilterStage;
import org.accela.spider.strategy.URLFilter;
import org.accela.spider.strategy.impl.RepeatedURLFilter;
import org.accela.spider.strategy.impl.SuffixURLFilter;
import org.accela.spider.util.URL;
import org.accela.stage.RejectedInputException;
import org.accela.stage.Stage;

import junit.framework.TestCase;

public class TestPrefilterStage extends TestCase
{
	public void testRejection() throws MalformedURLException
	{
		TesterAbortPolicy<WebPage, WebPage, String> p = new TesterAbortPolicy<WebPage, WebPage, String>();
		PrefilterStage s = new PrefilterStage(new RejectiveStage<WebPage>(),
				new URLFilter[0], p);
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

		assert (p.getInvokeCount() == 100) : p.getInvokeCount();
	}

	public void testFilter() throws MalformedURLException, InterruptedException
	{
		final AtomicInteger taskCount = new AtomicInteger(0);
		final AtomicBoolean run=new AtomicBoolean(true);
		TesterAbortPolicy<WebPage, WebPage, String> p = new TesterAbortPolicy<WebPage, WebPage, String>();
		final PrefilterStage s = new PrefilterStage(new Stage<WebPage>()
		{
			@Override
			public int getTaskCount()
			{
				return 0;
			}

			@Override
			public void input(WebPage input) throws RejectedInputException
			{
				taskCount.incrementAndGet();
			}

		}, new URLFilter[] {
				new RepeatedURLFilter(100),
				new SuffixURLFilter(new String[] { "html", "htm", "shtml",
						"shtm", "asp", "aspx", "php", "jsp", "" }) }, p);

		class Runner implements Runnable
		{
			URL url = null;

			public Runner(URL url)
			{
				this.url = url;
			}

			@Override
			public void run()
			{
				while (run.get())
				{
					try
					{
						s.input(new WebPage(url));
					}
					catch (RejectedInputException ex)
					{
						ex.printStackTrace();
						assert (false);
					}

					try
					{
						Thread.sleep(1);
					}
					catch (InterruptedException ex)
					{
						ex.printStackTrace();
					}

				}
			}
		}

		long startTime=System.currentTimeMillis();
		
		new Thread(new Runner(new URL("http://www.google.com"))).start();
		new Thread(new Runner(new URL("http://www.sina.com.cn/index.html")))
				.start();
		new Thread(
				new Runner(new URL("http://www.helloworld.com/download.zip")))
				.start();

		
		Thread.sleep(10000);
		run.set(false);
		long endTime=System.currentTimeMillis();
		
		Thread.sleep(1000);
		
		assert(taskCount.get()<((endTime-startTime)/100+1)*2);
		assert(taskCount.get()>=((endTime-startTime)/100)*2*0.9);
		assert(p.getInvokeCount()>=((endTime-startTime)/100)*0.9);
		
	}
}
