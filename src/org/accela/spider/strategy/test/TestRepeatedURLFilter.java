package org.accela.spider.strategy.test;

import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.accela.spider.strategy.impl.RepeatedURLFilter;
import org.accela.spider.util.URL;

import junit.framework.TestCase;

public class TestRepeatedURLFilter extends TestCase
{
	public void testSingle100() throws MalformedURLException,
			InterruptedException
	{
		singleTest(100);
	}

	public void testSingle10() throws MalformedURLException,
			InterruptedException
	{
		singleTest(10);
	}

	public void testSingle1() throws MalformedURLException,
			InterruptedException
	{
		singleTest(1);
	}

	public void testSingle0() throws MalformedURLException,
			InterruptedException
	{
		singleTest(0);
	}

	private void singleTest(long interval) throws MalformedURLException,
			InterruptedException
	{
		URL url = new URL("http://www.sina.com.cn");
		URL url2 = new URL("http://www.sina2.com.cn");
		RepeatedURLFilter s = new RepeatedURLFilter(interval);

		for (int count = 0; count < 10; count++)
		{
			assert (s.accept(url)) : "count: " + count;
			assert (interval != 0 ? !s.accept(url) : s.accept(url)) : "count: "
					+ count;

			Thread.sleep(interval / 2);

			assert (interval != 0 ? !s.accept(url) : s.accept(url)) : "count: "
					+ count;
			assert (s.accept(url2)) : "count: " + count;
			assert (interval != 0 ? !s.accept(url2) : s.accept(url2)) : "count: "
					+ count;

			Thread.sleep(interval / 2 + 1);
		}
	}

	public void testMultiAcception100() throws MalformedURLException,
			InterruptedException
	{
		multiTestAcception(100);
		Thread.sleep(1000);
	}

	public void testMultiRejection100() throws MalformedURLException,
			InterruptedException
	{
		multiTestRejection(100);
		Thread.sleep(1000);
	}

	public void testMultiAcception10() throws MalformedURLException,
			InterruptedException
	{
		multiTestAcception(10);
		Thread.sleep(1000);
	}

	public void testMultiRejection10() throws MalformedURLException,
			InterruptedException
	{
		multiTestRejection(10);
		Thread.sleep(1000);
	}

	public void testMultiAcception1() throws MalformedURLException,
			InterruptedException
	{
		multiTestAcception(1);
		Thread.sleep(1000);
	}

	public void testMultiRejection1() throws MalformedURLException,
			InterruptedException
	{
		multiTestRejection(1);
		Thread.sleep(1000);
	}

	public void testMultiAcception0() throws MalformedURLException,
			InterruptedException
	{
		multiTestAcception(0);
		Thread.sleep(1000);
	}

	public void testMultiRejection0() throws MalformedURLException,
			InterruptedException
	{
		multiTestRejection(0);
		Thread.sleep(1000);
	}

	private void multiTestAcception(long interval_outer)
			throws MalformedURLException, InterruptedException
	{
		final long interval = interval_outer;
		final int urlCount = 1000;
		final URL[] urls = new URL[urlCount];
		for (int i = 0; i < urlCount; i++)
		{
			urls[i] = new URL("http://www.sina" + i + ".com.cn");
		}

		final AtomicBoolean run = new AtomicBoolean(true);
		final AtomicBoolean failed = new AtomicBoolean(false);

		final RepeatedURLFilter s = new RepeatedURLFilter(interval);

		class Runner implements Runnable
		{
			private int i = 0;

			public Runner(int i)
			{
				this.i = i;
			}

			@Override
			public void run()
			{
				while (run.get())
				{
					boolean ret = s.accept(urls[i]);
					if (!ret)
					{
						System.out.println("not accepted: " + i);
						failed.set(true);
						assert (false);
					}

					try
					{
						Thread.sleep(interval + 1);
					}
					catch (InterruptedException ex)
					{
						ex.printStackTrace();
						assert (false) : "sleep waken up: " + i;
					}
				}
			}

		}

		for (int i = 0; i < urlCount; i++)
		{
			new Thread(new Runner(i)).start();
		}

		Thread.sleep(10 * interval + 1);
		run.set(false);
		Thread.sleep(1000);

		assert (!failed.get());
	}

	private void multiTestRejection(long interval_outer)
			throws MalformedURLException, InterruptedException
	{
		final long interval = interval_outer;
		final int urlCount = 100;
		final URL[] urls = new URL[urlCount];
		for (int i = 0; i < urlCount; i++)
		{
			urls[i] = new URL("http://www.sina" + i + ".com.cn");
		}

		final AtomicInteger[] acceptCounts = new AtomicInteger[urlCount];
		for (int i = 0; i < urlCount; i++)
		{
			acceptCounts[i] = new AtomicInteger(0);
		}

		final AtomicBoolean run = new AtomicBoolean(true);

		final RepeatedURLFilter s = new RepeatedURLFilter(interval);

		long startTime = System.currentTimeMillis();
		class Runner implements Runnable
		{
			private int i = 0;

			public Runner(int i)
			{
				this.i = i;
			}

			@Override
			public void run()
			{
				while (run.get())
				{
					if (s.accept(urls[i]))
					{
						acceptCounts[i].incrementAndGet();
					}
				}
			}

		}

		for (int i = 0; i < urlCount; i++)
		{
			new Thread(new Runner(i)).start();
		}

		if (interval != 0)
		{
			Thread.sleep(10 * interval);
		}
		else
		{
			Thread.sleep(20);
		}
		run.set(false);

		long endTime = System.currentTimeMillis();

		Thread.sleep(1000);

		for (int i = 0; i < urlCount; i++)
		{
			if (interval > 0)
			{
				assert (acceptCounts[i].get() <= ((endTime - startTime)
						/ interval + 1)) : "i: "
						+ i
						+ ", acceptCount: "
						+ acceptCounts[i].get()
						+ ", expected: "
						+ ((endTime - startTime) / interval + 1);
			}
			else
			{
				assert (acceptCounts[i].get() > 0) : i;
			}
		}

	}
}
