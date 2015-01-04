package org.accela.spider.strategy.test;

import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.accela.spider.strategy.impl.PolitenessURLScheduler;
import org.accela.spider.util.URL;

import junit.framework.TestCase;

public class TestPolitenessURLScheduler extends TestCase
{
	public void testSingleHost100() throws MalformedURLException,
			InterruptedException
	{
		singleHostTest(1000, 100, 10);
		singleHostTest(100, 100, 5);
		singleHostTest(10, 100, 2);
		singleHostTest(1, 100, 1);

		try
		{
			singleHostTest(0, 100, 1);
			assert (false);
		}
		catch (IllegalArgumentException ex)
		{
			// pass;
		}
	}

	public void testSingleHost10() throws MalformedURLException,
			InterruptedException
	{
		Thread.sleep(1000);
		singleHostTest(100, 10, 10);
		Thread.sleep(1000);
		singleHostTest(10, 10, 4);
		Thread.sleep(1000);
		singleHostTest(1, 10, 1);

		try
		{
			singleHostTest(0, 100, 1);
			assert (false);
		}
		catch (IllegalArgumentException ex)
		{
			// pass;
		}
	}

	public void testSingleHost4() throws MalformedURLException,
			InterruptedException
	{
		Thread.sleep(1000);
		singleHostTest(100, 4, 1);
		Thread.sleep(1000);
		singleHostTest(10, 4, 4);
		Thread.sleep(1000);
		singleHostTest(1, 4, 10);

		try
		{
			singleHostTest(0, 100, 1);
			assert (false);
		}
		catch (IllegalArgumentException ex)
		{
			// pass;
		}
	}

	public void testSingleHost0() throws MalformedURLException,
			InterruptedException
	{
		singleHostTest(1000, 0, 1);
		singleHostTest(100, 0, 4);
		singleHostTest(10, 0, 8);
		singleHostTest(1, 0, 12);

		try
		{
			singleHostTest(0, 100, 1);
			assert (false);
		}
		catch (IllegalArgumentException ex)
		{
			// pass;
		}
	}

	private void singleHostTest(int taskPerRound,
			long interval,
			int intervalPerCleanUp) throws MalformedURLException,
			InterruptedException
	{
		URL url = new URL("http://www.sina.com.cn");
		PolitenessURLScheduler s = new PolitenessURLScheduler(taskPerRound,
				interval, intervalPerCleanUp);

		for (int count = 0; count < 10; count++)
		{
			for (int i = 0; i < taskPerRound; i++)
			{
				long delay = s.schedule(url);
				assert (0 == delay) : "count: "
						+ count
						+ ", i: "
						+ i
						+ ", delay: "
						+ delay;
			}

			long delay = 0;
			for (int i = 0; i < taskPerRound; i++)
			{
				delay = s.schedule(url);
				if (interval > 0)
				{
					assert (delay > 0) : "count: "
							+ count
							+ ", i: "
							+ i
							+ ", delay: "
							+ delay;
					assert (delay <= interval) : "count: "
							+ count
							+ ", i: "
							+ i
							+ ", delay: "
							+ delay;
				}
				else
				{
					assert (0 == delay) : "count: "
							+ count
							+ ", i: "
							+ i
							+ ", delay: "
							+ delay;
				}
			}

			Thread.sleep(delay);
		}
	}

	public void testMultiHost100() throws MalformedURLException,
			InterruptedException
	{
		multiHostTest(1000, 100, 1);
		multiHostTest(100, 100, 10);
		multiHostTest(10, 100, 50);
		multiHostTest(1, 100, 100);
	}

	public void testMultiHost10() throws MalformedURLException,
			InterruptedException
	{
		multiHostTest(1000, 10, 100);
		multiHostTest(100, 10, 50);
		multiHostTest(10, 10, 10);
		multiHostTest(1, 10, 1);
	}

	public void testMultiHost1() throws MalformedURLException,
			InterruptedException
	{
		multiHostTest(100, 1, 1);
		multiHostTest(10, 1, 4);
		multiHostTest(1, 1, 8);
	}

	public void testMultHost0() throws MalformedURLException,
			InterruptedException
	{
		multiHostTest(100, 0, 8);
		multiHostTest(10, 0, 4);
		multiHostTest(1, 0, 1);
	}

	private void multiHostTest(int taskPerRound_outer,
			long interval_outer,
			int intervalPerCleanUp_outer) throws InterruptedException,
			MalformedURLException
	{
		multiHostTest_inner(taskPerRound_outer,
				interval_outer,
				intervalPerCleanUp_outer);
		multiHostTest_inner(taskPerRound_outer,
				interval_outer,
				Integer.MAX_VALUE);
	}

	private void multiHostTest_inner(int taskPerRound_outer,
			long interval_outer,
			int intervalPerCleanUp_outer) throws InterruptedException,
			MalformedURLException
	{
		final long interval = interval_outer;
		final int taskPerRound = taskPerRound_outer;
		final int intervalPerCleanUp = intervalPerCleanUp_outer;

		final int THREAD_PER_URL = 10;
		final int URL_COUNT = 10;
		final URL[] urls = new URL[URL_COUNT];
		for (int i = 0; i < URL_COUNT; i++)
		{
			urls[i] = new URL("http://www.sina" + i + ".com.cn");
		}
		final AtomicInteger[] taskCounts = new AtomicInteger[URL_COUNT];
		for (int i = 0; i < URL_COUNT; i++)
		{
			taskCounts[i] = new AtomicInteger(0);
		}
		final AtomicLong[] startTimes = new AtomicLong[URL_COUNT];
		for (int i = 0; i < URL_COUNT; i++)
		{
			startTimes[i] = new AtomicLong(0);
		}
		final AtomicLong[] endTimes = new AtomicLong[URL_COUNT];
		for (int i = 0; i < URL_COUNT; i++)
		{
			endTimes[i] = new AtomicLong(0);
		}
		final AtomicInteger[] testCounts = new AtomicInteger[URL_COUNT];
		for (int i = 0; i < URL_COUNT; i++)
		{
			testCounts[i] = new AtomicInteger(0);
		}

		final int NUM_INTERVAL = 20;
		final AtomicInteger[][] accessPerInterval = new AtomicInteger[(int) (NUM_INTERVAL * 1000)][URL_COUNT];
		for (int i = 0; i < accessPerInterval.length; i++)
		{
			for (int j = 0; j < accessPerInterval[i].length; j++)
			{
				accessPerInterval[i][j] = new AtomicInteger(0);
			}
		}
		final AtomicInteger[][] acceptPerInterval = new AtomicInteger[(int) (NUM_INTERVAL * 1000)][URL_COUNT];
		for (int i = 0; i < acceptPerInterval.length; i++)
		{
			for (int j = 0; j < acceptPerInterval[i].length; j++)
			{
				acceptPerInterval[i][j] = new AtomicInteger(0);
			}
		}
		final AtomicInteger totalInterval = new AtomicInteger(0);

		final AtomicBoolean run = new AtomicBoolean(true);
		final AtomicBoolean failed = new AtomicBoolean(false);

		final PolitenessURLScheduler s = new PolitenessURLScheduler(
				taskPerRound, interval, intervalPerCleanUp);

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
					if (0 == startTimes[i].get())
					{
						startTimes[i].set(System.nanoTime());
					}

					int idx = 0 == interval ? 0
							: (int) ((System.nanoTime() - startTimes[i].get()) / (interval * 1000000));
					accessPerInterval[idx][i].incrementAndGet();
					totalInterval.set(Math.max(totalInterval.get(), idx+1));

					long delay = s.schedule(urls[i]);
					testCounts[i].incrementAndGet();
					if (0 == delay)
					{
						taskCounts[i].incrementAndGet();
						acceptPerInterval[idx][i].incrementAndGet();

						int factor = (intervalPerCleanUp == Integer.MAX_VALUE) ? 1
								: 2;

						if (interval != 0
								&& taskCounts[i].get() > (((System.nanoTime() - startTimes[i]
										.get())
										/ (interval * 1000000) + 1)
										* taskPerRound * factor)) // 由于有PeriodicallyClearHashMap的自动清除功能的影响，每个interval的任务限额可能被逾越，但不应该超过其两倍
						{
							failed.set(true);
							assert (false) : "to much tasks: "
									+ (taskCounts[i].get() - ((System
											.nanoTime() - startTimes[i].get())
											/ (interval * 1000000) + 1)
											* taskPerRound
											* factor);
						}
					}
					else
					{
						if (0 == interval)
						{
							failed.set(true);
							assert (false);
						}
						if (delay > interval)
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

					endTimes[i].set(System.nanoTime());
				}
			}

		}

		for (int i = 0; i < URL_COUNT; i++)
		{
			for (int j = 0; j < THREAD_PER_URL; j++)
			{
				new Thread(new Runner(i)).start();
			}
		}

		Thread.sleep(NUM_INTERVAL * interval + 1);
		run.set(false);

		Thread.sleep(1000);

		int totalTestCount = 0;
		int totalTaskCount = 0;
		for (int i = 0; i < URL_COUNT; i++)
		{

			totalTestCount += testCounts[i].get();
			totalTaskCount += taskCounts[i].get();

			if (interval != 0)
			{
				//System.out.print("i: "
				//		+ i
				//		+ ", taskCount: "
				//		+ taskCounts[i].get()
				//		+ ", prefered: "
				//		+ (((endTimes[i].get() - startTimes[i].get())
				//				/ (interval * 1000000) * taskPerRound))
				//		+ ", elapse: "
				//		+ (endTimes[i].get() - startTimes[i].get())
				//		+ ", testCount: "
				//		+ testCounts[i].get()
				//		+ ", passRate: "
				//		+ 1.0
				//		* taskCounts[i].get()
				//		/ testCounts[i].get());
			}

			//System.out.print("\t\t\t");
			for (int idx = 0; idx < totalInterval.get(); idx++)
			{
				//System.out.print(accessPerInterval[idx][i] + "\t");
			}
			//System.out.print("\t\t\t");
			for (int idx = 0; idx < totalInterval.get(); idx++)
			{
				//System.out.print(acceptPerInterval[idx][i] + "\t");
			}
			//System.out.println();

		}
		//System.out.println("totalTaskCount: " + totalTaskCount);
		//System.out.println("totalTestCount: " + totalTestCount);

		// 每一个interval，必须至少有一个线程的调度通过次数达到配额
		if (interval != 0)
		{
			int unfoundCount = 0;
			for (int i = 0; i < totalInterval.get(); i++)
			{
				boolean found = false;
				for (int j = 0; j < URL_COUNT; j++)
				{
					if (acceptPerInterval[i][j].get() > taskPerRound * 0.9)
					{
						found = true;
						break;
					}
				}
				if (!found)
				{
					unfoundCount++;
					//System.out.println(i);
				}
			}
			assert (unfoundCount < totalInterval.get() * 0.5) : "unfoundCout: "
					+ unfoundCount
					+ ", totalInterval: "
					+ totalInterval.get();
		}
		else
		{
			assert (taskCounts[0].get() > 0);
		}

		assert (!failed.get());
	}
}
