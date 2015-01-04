package org.accela.spider.strategy.test;

import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.accela.spider.strategy.impl.TaskIntervalURLScheduler;
import org.accela.spider.util.URL;

import junit.framework.TestCase;

public class TestTaskIntervalURLScheduler extends TestCase
{
	public void testSingle100() throws MalformedURLException,
			InterruptedException
	{
		singleTest(1000, 100);
		singleTest(100, 100);
		singleTest(10, 100);
		singleTest(1, 100);

		try
		{
			singleTest(0, 100);
			assert (false);
		}
		catch (IllegalArgumentException ex)
		{
			// pass;
		}
	}

	public void testSingle10() throws MalformedURLException,
			InterruptedException
	{
		Thread.sleep(1000);
		singleTest(100, 10);
		Thread.sleep(1000);
		singleTest(10, 10);
		Thread.sleep(1000);
		singleTest(1, 10);

		try
		{
			singleTest(0, 100);
			assert (false);
		}
		catch (IllegalArgumentException ex)
		{
			// pass;
		}
	}

	public void testSingle1() throws MalformedURLException,
			InterruptedException
	{
		Thread.sleep(1000);
		singleTest(100, 1);
		Thread.sleep(1000);
		singleTest(10, 1);
		Thread.sleep(1000);
		singleTest(1, 1);

		try
		{
			singleTest(0, 100);
			assert (false);
		}
		catch (IllegalArgumentException ex)
		{
			// pass;
		}
	}

	public void testSingle0() throws MalformedURLException,
			InterruptedException
	{
		singleTest(1000, 0);
		singleTest(100, 0);
		singleTest(10, 0);
		singleTest(1, 0);

		try
		{
			singleTest(0, 100);
			assert (false);
		}
		catch (IllegalArgumentException ex)
		{
			// pass;
		}
	}

	private void singleTest(int taskPerRound, long interval)
			throws MalformedURLException, InterruptedException
	{
		URL url = new URL("http://www.sina.com.cn");
		TaskIntervalURLScheduler s = new TaskIntervalURLScheduler(taskPerRound, interval);

		for (int count = 0; count < 100; count++)
		{
			long startTime = System.nanoTime();

			for (int i = 0; i < taskPerRound; i++)
			{
				long delay = s.schedule(url);
				assert (0 == delay) : "count: " + count + ", i: " + i + ", delay: " + delay;
			}

			Thread.sleep(Math.max(0, (interval * 1000000 - (System.nanoTime() - startTime)) / 2 / 1000000));

			long delay = 0;
			for (int i = 0; i < taskPerRound; i++)
			{
				delay = s.schedule(url);
				if (interval > 0)
				{
					assert (delay > 0) : "count: " + count + ", i: " + i + ", delay: " + delay;
					assert (delay <= interval) : "count: " + count + ", i: " + i + ", delay: " + delay;
				}
				else
				{
					assert (0 == delay) : "count: " + count + ", i: " + i + ", delay: " + delay;
				}
			}

			Thread.sleep(delay);
		}
	}

	public void testMulti100() throws MalformedURLException,
			InterruptedException
	{
		multiTest(1000, 100);
		multiTest(100, 100);
		multiTest(10, 100);
		multiTest(1, 100);
	}

	public void testMulti10() throws MalformedURLException,
			InterruptedException
	{
		multiTest(1000, 10);
		multiTest(100, 10);
		multiTest(10, 10);
		multiTest(1, 10);
	}

	public void testMulti1() throws MalformedURLException, InterruptedException
	{
		multiTest(100, 1);
		multiTest(10, 1);
		multiTest(1, 1);
	}

	public void testMult0() throws MalformedURLException, InterruptedException
	{
		multiTest(100, 0);
		multiTest(10, 0);
		multiTest(1, 0);
	}

	private void multiTest(int taskPerRound, long interval)
			throws MalformedURLException, InterruptedException
	{
		final long INTERVAL = interval;
		final int TASKPERROUND = taskPerRound;
		final TaskIntervalURLScheduler s = new TaskIntervalURLScheduler(taskPerRound, interval);
		final URL url = new URL("http://www.sina.com.cn");

		final AtomicBoolean run = new AtomicBoolean(true);
		final AtomicBoolean failed = new AtomicBoolean(false);

		final AtomicInteger taskCount = new AtomicInteger(0);
		final long startTime = System.nanoTime();

		for (int i = 0; i < 1000; i++)
		{
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					while (run.get())
					{
						long delay = s.schedule(url);
						if (0 == delay)
						{
							taskCount.incrementAndGet();
							if (INTERVAL != 0)
							{
								if (taskCount.get() > ((System.nanoTime() - startTime) / (INTERVAL * 1000000) + 1) * TASKPERROUND)
								{
									failed.set(true);
									assert (false) : "to much tasks: " + (taskCount.get() - ((System.nanoTime() - startTime) / (INTERVAL * 1000000) + 1) * TASKPERROUND);
								}
							}
						}
						else
						{
							if (0 == INTERVAL)
							{
								failed.set(true);
								assert (false);
							}
							if (delay > INTERVAL)
							{
								failed.set(true);
								assert (false);
							}
							if (delay < 0)
							{
								failed.set(true);
								assert (false);
							}
						}
					}
				}
			}).start();
		}

		final int NUM_INTERVAL = 100;
		Thread.sleep(NUM_INTERVAL * interval + 1);
		run.set(false);

		long endTime = System.nanoTime();

		Thread.sleep(1000);

		if (INTERVAL != 0)
		{
			assert (taskCount.get() >= ((endTime - startTime) / (INTERVAL * 1000000) * TASKPERROUND) * 0.9);
		}
		else
		{
			assert (taskCount.get() > 0);
		}

		assert (!failed.get());
	}

	public void testMulti2_Performance() throws MalformedURLException,
			InterruptedException
	{
		multiTest2(10000, 100);
	}

	private void multiTest2(int taskPerRound, long interval)
			throws MalformedURLException, InterruptedException
	{
		final int URL_COUNT = 100;
		final URL[] urls = new URL[URL_COUNT];
		for (int i = 0; i < URL_COUNT; i++)
		{
			urls[i] = new URL("http://www.sina" + i + ".com.cn");
		}
		final AtomicInteger taskCount = new AtomicInteger(0);

		final long INTERVAL = interval;
		final int TASKPERROUND = taskPerRound;

		final AtomicBoolean run = new AtomicBoolean(true);
		final AtomicBoolean failed = new AtomicBoolean(false);

		final long startTime = System.nanoTime();

		final TaskIntervalURLScheduler s = new TaskIntervalURLScheduler(taskPerRound, interval);

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
					long delay = s.schedule(urls[i]);
					if (0 == delay)
					{
						taskCount.incrementAndGet();
						if (INTERVAL != 0)
						{
							if (taskCount.get() > (((System.nanoTime() - startTime) / (INTERVAL * 1000000) + 1) * TASKPERROUND))
							{
								failed.set(true);
								assert (false) : "to much tasks: " + (taskCount.get() - ((System.nanoTime() - startTime) / (INTERVAL * 1000000) + 1) * TASKPERROUND);
							}
						}
					}
					else
					{
						if (0 == INTERVAL)
						{
							failed.set(true);
							assert (false);
						}
						if (delay > INTERVAL)
						{
							failed.set(true);
							assert (false);
						}
						if (delay < 0)
						{
							failed.set(true);
							assert (false);
						}
					}
				}
			}

		}

		for (int i = 0; i < URL_COUNT; i++)
		{
			for (int j = 0; j < 10; j++)
			{
				new Thread(new Runner(i)).start();
			}
		}

		final int NUM_INTERVAL = 100;
		Thread.sleep(NUM_INTERVAL * interval + 1);
		run.set(false);

		long endTime = System.nanoTime();

		Thread.sleep(1000);

		if (INTERVAL != 0)
		{
			assert (taskCount.get() >= ((endTime - startTime) / (INTERVAL * 1000000) * TASKPERROUND) * 0.9);
		}
		else
		{
			assert (taskCount.get() > 0);
		}

		assert (!failed.get());

	}

}
