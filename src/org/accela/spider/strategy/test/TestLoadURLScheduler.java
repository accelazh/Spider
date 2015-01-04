package org.accela.spider.strategy.test;

import java.net.MalformedURLException;

import org.accela.spider.strategy.LoadMonitor;
import org.accela.spider.strategy.impl.LoadURLScheduler;
import org.accela.spider.util.URL;

import junit.framework.TestCase;

public class TestLoadURLScheduler extends TestCase
{
	public void testSimple() throws MalformedURLException
	{
		LoadURLScheduler s = new LoadURLScheduler(new LoadMonitor[0], 100, 120);
		assert (s.schedule(new URL("http://www.sina.com.cn")) == 0);

		TesterMonitor[] ts = new TesterMonitor[] { new TesterMonitor(20),
				new TesterMonitor(11), new TesterMonitor(30),
				new TesterMonitor(40) };
		s=new LoadURLScheduler(ts, 100, 120);
		assert(s.schedule(new URL("http://www.sina.com.cn"))==120);
		
		ts[1].value=5;
		assert(s.schedule(new URL("http://www.sina.com.cn"))==0);
	}

	private static class TesterMonitor implements LoadMonitor
	{
		public int value = 0;

		public TesterMonitor(int value)
		{
			this.value = value;
		}

		@Override
		public int getTaskCount()
		{
			return value;
		}

	}
}
