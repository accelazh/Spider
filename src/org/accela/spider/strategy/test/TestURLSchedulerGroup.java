package org.accela.spider.strategy.test;

import java.net.MalformedURLException;

import org.accela.spider.strategy.URLScheduler;
import org.accela.spider.strategy.URLSchedulerGroup;
import org.accela.spider.util.URL;

import junit.framework.TestCase;

public class TestURLSchedulerGroup extends TestCase
{
	public void testSimple() throws MalformedURLException
	{
		URLSchedulerGroup g=new URLSchedulerGroup(new URLScheduler[0]);
		assert(g.schedule(new URL("http://www.sina.com.cn"))==0);
		
		g=new URLSchedulerGroup(new URLScheduler[]{new TesterScheduler(-1), new TesterScheduler(-100), new TesterScheduler(-1000)});
		assert(g.schedule(new URL("http://www.sina.com.cn"))==0);
		
		g=new URLSchedulerGroup(new URLScheduler[]{new TesterScheduler(-1), new TesterScheduler(1), new TesterScheduler(-1000)});
		assert(g.schedule(new URL("http://www.sina.com.cn"))==1);
		
		g=new URLSchedulerGroup(new URLScheduler[]{new TesterScheduler(10), new TesterScheduler(20), new TesterScheduler(30)});
		assert(g.schedule(new URL("http://www.sina.com.cn"))==30);
		
		g=new URLSchedulerGroup(new URLScheduler[]{new TesterScheduler(30), new TesterScheduler(20), new TesterScheduler(10)});
		assert(g.schedule(new URL("http://www.sina.com.cn"))==30);
		
	}
	
	private static class TesterScheduler implements URLScheduler
	{
		public long value=0;
		
		public TesterScheduler(long value)
		{
			this.value=value;
		}
		
		@Override
		public long schedule(URL url)
		{
			return value;
		}
		
	}
}
