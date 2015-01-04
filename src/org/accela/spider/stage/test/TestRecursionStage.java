package org.accela.spider.stage.test;

import java.net.MalformedURLException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.accela.spider.data.WebPage;
import org.accela.spider.data.impl.EmptyAnalysis;
import org.accela.spider.data.impl.TextContent;
import org.accela.spider.stage.RecursionStage;
import org.accela.spider.strategy.RecursionHyperlinkFilter;
import org.accela.spider.strategy.impl.DuplicatedWithParentRecursionHyperlinkFilter;
import org.accela.spider.strategy.impl.LimitedRecursionHyperlinkFilter;
import org.accela.spider.strategy.impl.SameHostGroupRecursionHyperlinkFilter;
import org.accela.spider.strategy.impl.SimpleURLNormalizer;
import org.accela.spider.strategy.impl.TotalCountRecursionHyperlinkFilter;
import org.accela.spider.strategy.impl.URLOnlyHyperlink;
import org.accela.spider.util.URL;
import org.accela.stage.RejectedInputException;
import org.accela.stage.Stage;

import junit.framework.TestCase;

public class TestRecursionStage extends TestCase
{
	public void testRejection() throws MalformedURLException,
			InterruptedException
	{
		TesterAbortPolicy<WebPage, WebPage, String> p = new TesterAbortPolicy<WebPage, WebPage, String>();
		RecursionStage s = new RecursionStage(new RejectiveStage<WebPage>(),
				new RejectiveStage<WebPage>(), new SimpleURLNormalizer(),
				new RecursionHyperlinkFilter[0], p, Executors
						.newCachedThreadPool());
		for (int i = 0; i < 100; i++)
		{
			try
			{
				WebPage page = new WebPage(new URL("http://www.google.com"));
				page.setContent(new TextContent(""));
				page.setAnalysis(new EmptyAnalysis());
				page.getLinks().add(new URLOnlyHyperlink(new URL(
						"http://www.google.com")));
				page.getLinks().add(new URLOnlyHyperlink(new URL(
						"http://www.google.com")));
				page.getLinks().add(new URLOnlyHyperlink(new URL(
						"http://www.google1.com")));
				page.getLinks().add(new URLOnlyHyperlink(new URL(
						"http://www.google1.com")));
				page.getLinks().add(new URLOnlyHyperlink(new URL(
						"http://www.google2.com")));
				page.getLinks().add(new URLOnlyHyperlink(new URL(
						"http://www.google2.com")));
				page.getLinks().add(new URLOnlyHyperlink(new URL(
						"http://www.google3.com")));
				page.getLinks().add(new URLOnlyHyperlink(new URL(
						"http://www.google3.com")));
				page.getLinks().add(new URLOnlyHyperlink(new URL(
						"http://www.google4.com")));
				page.getLinks().add(new URLOnlyHyperlink(new URL(
						"http://www.google4.com")));

				s.input(page);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
		}

		Thread.sleep(2);

		assert (p.getInvokeCount() == (100 + 500)) : p.getInvokeCount();
	}

	public void testRecursionIncrement() throws MalformedURLException,
			InterruptedException
	{
		final AtomicBoolean failed = new AtomicBoolean(false);
		TesterAbortPolicy<WebPage, WebPage, String> p = new TesterAbortPolicy<WebPage, WebPage, String>();
		RecursionStage s = new RecursionStage(new Stage<WebPage>()
		{
			@Override
			public int getTaskCount()
			{
				return 0;
			}

			@Override
			public void input(WebPage input) throws RejectedInputException
			{
				if (input.getRecursion() != 1)
				{
					System.err.println("input.getRecursion()!=1: "
							+ input.getRecursion());
					failed.set(true);
				}
			}
		}, new Stage<WebPage>()
		{
			@Override
			public int getTaskCount()
			{
				return 0;
			}

			@Override
			public void input(WebPage input) throws RejectedInputException
			{
				if (input.getRecursion() != 0)
				{
					System.err.println("input.getRecursion()!=0: "
							+ input.getRecursion());
					failed.set(true);
				}
			}
		}, new SimpleURLNormalizer(), new RecursionHyperlinkFilter[0], p,
				Executors.newCachedThreadPool());
		for (int i = 0; i < 100; i++)
		{
			try
			{
				WebPage page = new WebPage(new URL("http://www.google.com"));
				page.setContent(new TextContent(""));
				page.setAnalysis(new EmptyAnalysis());
				page.getLinks().add(new URLOnlyHyperlink(new URL(
						"http://www.google.com")));
				page.getLinks().add(new URLOnlyHyperlink(new URL(
						"http://www.google.com")));
				page.getLinks().add(new URLOnlyHyperlink(new URL(
						"http://www.google1.com")));
				page.getLinks().add(new URLOnlyHyperlink(new URL(
						"http://www.google1.com")));
				page.getLinks().add(new URLOnlyHyperlink(new URL(
						"http://www.google2.com")));
				page.getLinks().add(new URLOnlyHyperlink(new URL(
						"http://www.google2.com")));
				page.getLinks().add(new URLOnlyHyperlink(new URL(
						"http://www.google3.com")));
				page.getLinks().add(new URLOnlyHyperlink(new URL(
						"http://www.google3.com")));
				page.getLinks().add(new URLOnlyHyperlink(new URL(
						"http://www.google4.com")));
				page.getLinks().add(new URLOnlyHyperlink(new URL(
						"http://www.google4.com")));

				s.input(page);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
		}

		Thread.sleep(1000);

		assert (p.getInvokeCount() == 0) : p.getInvokeCount();
	}

	public void testFilterAndNormalizer() throws MalformedURLException,
			InterruptedException
	{
		TesterAbortPolicy<WebPage, WebPage, String> p = new TesterAbortPolicy<WebPage, WebPage, String>();
		EmptyStage<WebPage> rs = new EmptyStage<WebPage>();
		EmptyStage<WebPage> ns = new EmptyStage<WebPage>();
		final RecursionStage s = new RecursionStage(rs, ns,
				new SimpleURLNormalizer(), new RecursionHyperlinkFilter[] {
						new DuplicatedWithParentRecursionHyperlinkFilter(),
						new LimitedRecursionHyperlinkFilter(3),
						new SameHostGroupRecursionHyperlinkFilter(),
						new TotalCountRecursionHyperlinkFilter(1000) }, p,
				Executors.newCachedThreadPool());

		final WebPage page = new WebPage(new URL(
				"http://www.sina.com.cn/../nice/./hello/index"));
		page.setContent(new TextContent(""));
		page.setAnalysis(new EmptyAnalysis());
		for (int i = 0; i < 2; i++)
		{
			// should pass
			page.getLinks().add(new URLOnlyHyperlink(new URL(
					"http://sina.com.cn")));
			// should be filtered by same host
			page.getLinks().add(new URLOnlyHyperlink(new URL(
					"http://sina.com/cn")));
			// should be filtered by duplicated with parent
			page.getLinks().add(new URLOnlyHyperlink(new URL(
					"http://sina.com.cn/nice/hello/default.asp")));
		}

		class Runner implements Runnable
		{
			@Override
			public void run()
			{
				for (int i = 0; i < 10; i++)
				{
					try
					{
						s.input(page);
					}
					catch (RejectedInputException ex)
					{
						ex.printStackTrace();
						assert (false);
					}
				}
			}
		}

		// first run
		for (int i = 0; i < 10; i++)
		{
			new Thread(new Runner()).start();
		}

		Thread.sleep(70);

		assert (rs.getInvokeCount() == 100) : rs.getInvokeCount();
		assert (ns.getInvokeCount() == 100) : ns.getInvokeCount();
		assert (p.getInvokeCount() == 400) : p.getInvokeCount();

		// set recursion limit
		page.setRecursion(3);
		for (int i = 0; i < 10; i++)
		{
			new Thread(new Runner()).start();
		}

		Thread.sleep(70);

		assert (rs.getInvokeCount() == 100) : rs.getInvokeCount();
		assert (ns.getInvokeCount() == 200) : ns.getInvokeCount();
		assert (p.getInvokeCount() == 1000) : p.getInvokeCount();

		// set recursion limit
		page.setRecursion(4);
		for (int i = 0; i < 10; i++)
		{
			new Thread(new Runner()).start();
		}

		Thread.sleep(70);

		assert (rs.getInvokeCount() == 100) : rs.getInvokeCount();
		assert (ns.getInvokeCount() == 300) : ns.getInvokeCount();
		assert (p.getInvokeCount() == 1600) : p.getInvokeCount();

		// restore recursion limit
		page.setRecursion(2);
		for (int i = 0; i < 10; i++)
		{
			new Thread(new Runner()).start();
		}

		Thread.sleep(70);

		assert (rs.getInvokeCount() == 200) : rs.getInvokeCount();
		assert (ns.getInvokeCount() == 400) : ns.getInvokeCount();
		assert (p.getInvokeCount() == 2000) : p.getInvokeCount();

		// reach total count
		for (int i = 0; i < 80; i++)
		{
			new Thread(new Runner()).start();
		}

		Thread.sleep(600);

		assert (rs.getInvokeCount() == 1000) : rs.getInvokeCount();
		assert (ns.getInvokeCount() == 1200) : ns.getInvokeCount();
		assert (p.getInvokeCount() == 5200) : p.getInvokeCount();

		// exceeds total count
		for (int i = 0; i < 10; i++)
		{
			new Thread(new Runner()).start();
		}

		Thread.sleep(70);

		assert (rs.getInvokeCount() == 1000) : rs.getInvokeCount();
		assert (ns.getInvokeCount() == 1300) : ns.getInvokeCount();
		assert (p.getInvokeCount() == 5800) : p.getInvokeCount();
	}

	public void testPerformance() throws MalformedURLException,
			InterruptedException
	{
		TesterAbortPolicy<WebPage, WebPage, String> p = new TesterAbortPolicy<WebPage, WebPage, String>();
		EmptyStage<WebPage> rs = new EmptyStage<WebPage>();
		EmptyStage<WebPage> ns = new EmptyStage<WebPage>();
		final RecursionStage s = new RecursionStage(rs, ns,
				new SimpleURLNormalizer(), new RecursionHyperlinkFilter[] {
						new DuplicatedWithParentRecursionHyperlinkFilter(),
						new LimitedRecursionHyperlinkFilter(3),
						new SameHostGroupRecursionHyperlinkFilter(),
						new TotalCountRecursionHyperlinkFilter(999) }, p,
				Executors.newCachedThreadPool());

		final WebPage page = new WebPage(new URL(
				"http://www.sina.com.cn/../nice/./hello/index"));
		page.setContent(new TextContent(""));
		page.setAnalysis(new EmptyAnalysis());
		for (int i = 0; i < 2; i++)
		{
			// should pass
			page.getLinks().add(new URLOnlyHyperlink(new URL(
					"http://sina.com.cn")));
			// should be filtered by same host
			page.getLinks().add(new URLOnlyHyperlink(new URL(
					"http://sina.com/cn")));
			// should be filtered by duplicated with parent
			page.getLinks().add(new URLOnlyHyperlink(new URL(
					"http://sina.com.cn/nice/hello/default.asp")));
		}

		class Runner implements Runnable
		{
			@Override
			public void run()
			{
				for (int i = 0; i < 10; i++)
				{
					try
					{
						s.input(page);
					}
					catch (RejectedInputException ex)
					{
						ex.printStackTrace();
						assert (false);
					}
				}
			}
		}

		for (int i = 0; i < 100; i++)
		{
			new Thread(new Runner()).start();
		}

		Thread.sleep(700);
		assert (rs.getInvokeCount() == 999) : rs.getInvokeCount();
		assert (ns.getInvokeCount() == 1000) : ns.getInvokeCount();
		assert (p.getInvokeCount() == 4002) : p.getInvokeCount();
	}
}
