package org.accela.spider.strategy.test;

import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.accela.spider.strategy.impl.RobotsTxtFilter;
import org.accela.spider.util.URL;

import junit.framework.TestCase;

public class TestRobotsTxtFilter extends TestCase
{
	// hosts that contains robots.txt
	private String noExist = "lakjfldas.com.cn";

	private String noRobot = "armorgames.com";

	private String chafanhou = "chafanhou.com";
	private String chafanhouNotAllowed1 = "/includes/";
	private String chafanhouNotAllowed2 = "/includes/helloWorld";
	private String chafanhouAllowed = "/includes2";

	private String google = "www.google.com";
	private String googleNotAllowed = "/search";
	private String googleAllowed1 = "/helloWorld";
	private String googleAllowed2 = "/nice/helloWorldHey/";

	// network connection presumed
	public void testSimple() throws MalformedURLException
	{
		RobotsTxtFilter f = new RobotsTxtFilter(null, Integer.MAX_VALUE);

		assert (test(f, false));
	}

	private boolean test(RobotsTxtFilter f, boolean disableTimeOut)
			throws MalformedURLException
	{
		boolean ret = true;
		long startTime = 0;

		// stage 1
		ret = ret && f.accept(new URL("http://" + noExist + "/helloWorld"));
		if (!ret)
		{
			System.out.println("stage 1 step 1");
			return false;
		}
		startTime = System.nanoTime();
		ret = ret && f.accept(new URL("http://" + noExist + "/helloWorld2"));
		if (!ret)
		{
			System.out.println("stage 1 step 2");
			return false;
		}
		ret = ret && f.accept(new URL("http://" + noExist + "/helloWorld3"));
		if (!ret)
		{
			System.out.println("stage 1 step 3");
			return false;
		}
		if (!disableTimeOut)
		{
			ret = ret && (System.nanoTime() - startTime <= 2000000);
		}
		if (!ret)
		{
			System.out.println("stage 1 step 4 time out: " + (System.nanoTime() - startTime));
			return false;
		}

		// stage 2
		ret = ret && f.accept(new URL("http://" + noRobot + "/helloWorld"));
		if (!ret)
		{
			System.out.println("stage 2 step 1");
			return false;
		}
		startTime = System.nanoTime();
		ret = ret && f.accept(new URL("http://" + noRobot + "/helloWorld2"));
		if (!ret)
		{
			System.out.println("stage 2 step 2");
			return false;
		}
		ret = ret && f.accept(new URL("http://" + noRobot + "/helloWorld3"));
		if (!ret)
		{
			System.out.println("stage 2 step 3");
			return false;
		}
		if (!disableTimeOut)
		{
			ret = ret && (System.nanoTime() - startTime <= 2000000);
		}
		if (!ret)
		{
			System.out.println("stage 2 step 4 time out: " + (System.nanoTime() - startTime));
			return false;
		}

		// stage 3
		ret = ret && !f.accept(new URL("http://" + chafanhou + chafanhouNotAllowed1));
		if (!ret)
		{
			System.out.println("stage 3 step 1");
			return false;
		}
		startTime = System.nanoTime();
		ret = ret && !f.accept(new URL("http://" + chafanhou + chafanhouNotAllowed2));
		if (!ret)
		{
			System.out.println("stage 3 step 2");
			return false;
		}
		ret = ret && f.accept(new URL("http://" + chafanhou + chafanhouAllowed));
		if (!ret)
		{
			System.out.println("stage 3 step 3");
			return false;
		}
		if (!disableTimeOut)
		{
			ret = ret && (System.nanoTime() - startTime <= 4000000);
		}
		if (!ret)
		{
			System.out.println("stage 3 step 4 time out: " + (System.nanoTime() - startTime));
			return false;
		}

		// stage 4
		ret = ret && !f.accept(new URL("http://" + google + googleNotAllowed));
		if (!ret)
		{
			System.out.println("stage 4 step 1");
			return false;
		}
		startTime = System.nanoTime();
		ret = ret && f.accept(new URL("http://" + google + googleAllowed1));
		if (!ret)
		{
			System.out.println("stage 4 step 2");
			return false;
		}
		ret = ret && f.accept(new URL("http://" + google + googleAllowed2));
		if (!ret)
		{
			System.out.println("stage 4 step 3");
			return false;
		}
		if (!disableTimeOut)
		{
			ret = ret && (System.nanoTime() - startTime <= 8000000);
		}
		if (!ret)
		{
			System.out.println("stage 4 step 4 time out: " + (System.nanoTime() - startTime));
			return false;
		}

		return ret;
	}

	// network connection presumed
	public void testMulti() throws InterruptedException
	{
		final RobotsTxtFilter f1 = new RobotsTxtFilter(null, Integer.MAX_VALUE);
		final RobotsTxtFilter f2 = new RobotsTxtFilter(null, 10);
		final RobotsTxtFilter f3 = new RobotsTxtFilter(null, 0);
		final RobotsTxtFilter[] fs = new RobotsTxtFilter[] { f1, f2, f3 };

		final AtomicBoolean failed = new AtomicBoolean(false);
		final AtomicBoolean run = new AtomicBoolean(true);

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
					boolean ret = false;
					try
					{
						ret = test(fs[i], true);
					}
					catch (MalformedURLException ex)
					{
						ex.printStackTrace();
						failed.set(true);
					}
					if (!ret)
					{
						failed.set(true);
						assert (false);
					}
				}
			}
		}

		for (int i = 0; i < 300; i++)
		{
			for (int j = 0; j < fs.length; j++)
			{
				new Thread(new Runner(j)).start();
			}
		}

		Thread.sleep(10000);
		run.set(false);

		assert (!failed.get());
	}
}
