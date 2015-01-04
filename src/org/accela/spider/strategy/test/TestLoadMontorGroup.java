package org.accela.spider.strategy.test;

import org.accela.spider.strategy.LoadMonitor;
import org.accela.spider.strategy.LoadMonitorGroup;

import junit.framework.TestCase;

public class TestLoadMontorGroup extends TestCase
{
	public void testSimple()
	{
		LoadMonitorGroup g=new LoadMonitorGroup(new LoadMonitor[0]);
		assert(g.getTaskCount()==0);
		
		g=new LoadMonitorGroup(new LoadMonitor[]{new TesterMonitor(0), new TesterMonitor(0), new TesterMonitor(0)});
		assert(g.getTaskCount()==0);
		
		g=new LoadMonitorGroup(new LoadMonitor[]{new TesterMonitor(10), new TesterMonitor(20), new TesterMonitor(30)});
		assert(g.getTaskCount()==60);
		
		g=new LoadMonitorGroup(new LoadMonitor[]{new TesterMonitor(-10), new TesterMonitor(20), new TesterMonitor(-30)});
		assert(g.getTaskCount()==20);
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
