package org.accela.spider.stage.test;

import java.net.MalformedURLException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.accela.spider.data.WebPage;
import org.accela.spider.stage.SchedulingStage;
import org.accela.spider.strategy.LoadMonitor;
import org.accela.spider.strategy.URLScheduler;
import org.accela.spider.strategy.impl.LoadURLScheduler;
import org.accela.spider.strategy.impl.PrinterAbortPolicy;
import org.accela.spider.strategy.impl.StageLoadMonitor;
import org.accela.spider.strategy.impl.TaskIntervalURLScheduler;
import org.accela.spider.util.URL;
import org.accela.stage.RejectedInputException;

import junit.framework.TestCase;

public class TestSchedulingStage extends TestCase
{
	public void testRejection() throws MalformedURLException,
			InterruptedException
	{
		final int DELAY = 100;
		final int MAX_SCHEDULED = 10;
		TesterURLScheduler ts = null;
		SchedulingStage s = new SchedulingStage(new EmptyStage<WebPage>(),
				new URLScheduler[] { ts = new TesterURLScheduler(DELAY) },
				new PrinterAbortPolicy<WebPage, WebPage, String>(), Executors
						.newScheduledThreadPool(MAX_SCHEDULED / 2),
				MAX_SCHEDULED);
		for (int i = 0; i < MAX_SCHEDULED; i++)
		{
			try
			{
				s.input(new WebPage(new URL("http://www.sina.com.cn")));
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false) : "i: " + i;
			}
		}
		for (int i = 0; i < MAX_SCHEDULED; i++)
		{
			try
			{
				s.input(new WebPage(new URL("http://www.sina.com.cn")));
				assert (false) : "i: " + i;
			}
			catch (RejectedInputException ex)
			{
				// pass
			}
		}

		assert (ts.getCount() == 2 * MAX_SCHEDULED) : ts.getCount();

		Thread.sleep(DELAY + 1);
		assert (ts.getCount() == 3 * MAX_SCHEDULED) : ts.getCount();
		Thread.sleep(DELAY + 1);
		assert (ts.getCount() == 4 * MAX_SCHEDULED) : ts.getCount();
		Thread.sleep(DELAY + 1);
		assert (ts.getCount() == 5 * MAX_SCHEDULED) : ts.getCount();
	}

	public void testOutputFailure() throws MalformedURLException,
			InterruptedException
	{
		final int DELAY = 1000;
		final int MAX_SCHEDULED = 10;
		TesterURLScheduler ts = null;
		TesterAbortPolicy<WebPage, WebPage, String> p = null;
		SchedulingStage s = new SchedulingStage(new RejectiveStage<WebPage>(),
				new URLScheduler[] { ts = new TesterURLScheduler(DELAY) },
				p = new TesterAbortPolicy<WebPage, WebPage, String>(),
				Executors.newSingleThreadScheduledExecutor(), MAX_SCHEDULED);
		for (int i = 0; i < MAX_SCHEDULED; i++)
		{
			try
			{
				s.input(new WebPage(new URL("http://www.sina.com.cn")));
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false) : "i: " + i;
			}
		}
		ts.setDelay(0);
		assert (p.getInvokeCount() == 0);
		Thread.sleep(DELAY + 1);
		assert (p.getInvokeCount() == MAX_SCHEDULED) : p.getInvokeCount();
	}

	public void testOutputSucc() throws MalformedURLException,
			InterruptedException
	{
		final int DELAY = 1000;
		final int MAX_SCHEDULED = 10;
		TesterURLScheduler ts = null;
		TesterAbortPolicy<WebPage, WebPage, String> p = null;
		EmptyStage<WebPage> es = null;
		SchedulingStage s = new SchedulingStage(es = new EmptyStage<WebPage>(),
				new URLScheduler[] { ts = new TesterURLScheduler(DELAY) },
				p = new TesterAbortPolicy<WebPage, WebPage, String>(),
				Executors.newSingleThreadScheduledExecutor(), MAX_SCHEDULED);
		for (int i = 0; i < MAX_SCHEDULED; i++)
		{
			try
			{
				s.input(new WebPage(new URL("http://www.sina.com.cn")));
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false) : "i: " + i;
			}
		}
		ts.setDelay(0);
		assert (p.getInvokeCount() == 0);
		assert (es.getInvokeCount() == 0);
		Thread.sleep(DELAY + 10);
		assert (p.getInvokeCount() == 0);
		assert (es.getInvokeCount() == MAX_SCHEDULED):es.getInvokeCount();
	}

	public void testPerformance() throws InterruptedException,
			MalformedURLException, RejectedInputException
	{
		final long PROC_TIME = 100;
		final int PROC_MAX_THREAD = 100;
		final int PROC_MAX_TASK = 1024;
		final long DELAY = 1000;
		final TesterStage<WebPage> ts = new TesterStage<WebPage>(PROC_TIME,
				Executors.newFixedThreadPool(PROC_MAX_THREAD));
		final SchedulingStage s = new SchedulingStage(ts,
				new URLScheduler[] { new LoadURLScheduler(
						new LoadMonitor[] { new StageLoadMonitor(ts) },
						PROC_MAX_TASK, DELAY) },
				new PrinterAbortPolicy<WebPage, WebPage, String>(), Executors
						.newSingleThreadScheduledExecutor(), -1);

		final AtomicBoolean put = new AtomicBoolean(true);
		final AtomicBoolean run = new AtomicBoolean(true);
		final AtomicBoolean failed = new AtomicBoolean(false);

		class TaskMonitor implements Runnable
		{
			@Override
			public void run()
			{
				while (run.get())
				{
					/*
					 * System.out.println("s.getTaskCount(): " +
					 * s.getTaskCount() + ", ts.getTaskCount(): " +
					 * ts.getTaskCount());
					 */

					if (!put.get()
							&& s.getTaskCount() > 0
							&& ts.getTaskCount() <= 0)
					{
						failed.set(true);
						assert (false) : s.getTaskCount()
								+ ", "
								+ ts.getTaskCount();
					}
					if (ts.getTaskCount() > PROC_MAX_TASK)
					{
						failed.set(true);
						assert (false);
					}

					try
					{
						Thread.sleep(100);
					}
					catch (InterruptedException ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}

		class TaskPutter implements Runnable
		{
			@Override
			public void run()
			{
				while (put.get())
				{
					try
					{
						s.input(new WebPage(new URL("http://www.sina.com.cn")));
					}
					catch (MalformedURLException ex)
					{
						ex.printStackTrace();
						assert (false);
					}
					catch (RejectedInputException ex)
					{
						ex.printStackTrace();
						assert (false);
					}
				}
			}
		}

		new Thread(new TaskMonitor()).start();
		new Thread(new TaskPutter()).start();

		Thread.sleep(100);
		put.set(false);

		Thread.sleep(11000);
		assert (s.getTaskCount() == 0);
		assert (!failed.get());
	}

	public void testTaskInterval() throws InterruptedException
	{
		final long PROC_TIME = 100;
		final int PROC_MAX_THREAD = 100;
		final int PROC_MAX_TASK = 1024;
		final long DELAY = 1000;
		final TesterStage<WebPage> ts = new TesterStage<WebPage>(PROC_TIME,
				Executors.newFixedThreadPool(PROC_MAX_THREAD));
		final SchedulingStage s = new SchedulingStage(ts, new URLScheduler[] {
				new LoadURLScheduler(new LoadMonitor[] { new StageLoadMonitor(
						ts) }, PROC_MAX_TASK, DELAY),
				new TaskIntervalURLScheduler(10, 1000) },
				new PrinterAbortPolicy<WebPage, WebPage, String>(), Executors
						.newSingleThreadScheduledExecutor(), -1);

		final AtomicBoolean put = new AtomicBoolean(true);
		final AtomicBoolean run = new AtomicBoolean(true);
		final AtomicBoolean failed = new AtomicBoolean(false);

		final long startTime = System.currentTimeMillis();

		class TaskMonitor implements Runnable
		{
			@Override
			public void run()
			{
				while (run.get())
				{
					/*
					 * System.out.println("s.getTaskCount(): " +
					 * s.getTaskCount() + ", ts.getTaskCount(): " +
					 * ts.getTaskCount() + ", ts.getCount(): " + ts.getCount());
					 */

					if (ts.getCount() > ((System.currentTimeMillis() - startTime) / 1000 + 1) * 10)
					{
						failed.set(true);
						assert (false) : "too many tasks";
					}
					if (ts.getCount() < (System.currentTimeMillis() - startTime) / 1000 * 10)
					{
						failed.set(true);
						assert (false) : "too few tasks";
					}

					try
					{
						Thread.sleep(100);
					}
					catch (InterruptedException ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}

		class TaskPutter implements Runnable
		{
			@Override
			public void run()
			{
				while (put.get())
				{
					try
					{
						s.input(new WebPage(new URL("http://www.sina.com.cn")));
					}
					catch (MalformedURLException ex)
					{
						ex.printStackTrace();
						assert (false);
					}
					catch (RejectedInputException ex)
					{
						ex.printStackTrace();
						assert (false);
					}
				}
			}
		}

		new Thread(new TaskMonitor()).start();
		new Thread(new TaskPutter()).start();

		Thread.sleep(1000);
		put.set(false);
		Thread.sleep(11000);

		assert (!failed.get());
	}

}
