package org.accela.spider.strategy.test;

import java.net.MalformedURLException;

import org.accela.spider.strategy.URLFilter;
import org.accela.spider.strategy.URLFilterGroup;
import org.accela.spider.util.URL;

import junit.framework.TestCase;

public class TestURLFilterGroup extends TestCase
{
	public void testSimple() throws MalformedURLException
	{
		URLFilterGroup g=new URLFilterGroup(new URLFilter[0]);
		assert(g.accept(new URL("http://www.sina.com.cn")));
		
		g=new URLFilterGroup(new URLFilter[]{new TesterFilter(true), new TesterFilter(true), new TesterFilter(true)});
		assert(g.accept(new URL("http://www.sina.com.cn")));
		
		g=new URLFilterGroup(new URLFilter[]{new TesterFilter(true), new TesterFilter(true), new TesterFilter(false)});
		assert(!g.accept(new URL("http://www.sina.com.cn")));
		
	}
	
	private static class TesterFilter implements URLFilter
	{
		public boolean value=false;
		
		public TesterFilter(boolean value)
		{
			this.value=value;
		}
		
		@Override
		public boolean accept(URL url)
		{
			return value;
		}
	}
}
