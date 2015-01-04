package org.accela.spider.strategy.test;

import java.net.MalformedURLException;

import org.accela.spider.data.WebPage;
import org.accela.spider.data.impl.MemoryWebPageStore;
import org.accela.spider.strategy.impl.DateURLFilter;
import org.accela.spider.util.URL;

import junit.framework.TestCase;

public class TestDateURLFilter extends TestCase
{
	public void testSimple() throws MalformedURLException, InterruptedException
	{
		URL url1=new URL("http://www.sina1.com.cn");
		WebPage page1=new WebPage(url1);
		page1.setStamp(System.currentTimeMillis());
		
		URL url2=new URL("http://www.sina2.com.cn");
		WebPage page2=new WebPage(url2);
		page2.setStamp(System.currentTimeMillis());
		
		MemoryWebPageStore s=new MemoryWebPageStore();
		DateURLFilter f=new DateURLFilter(s, 1000);
		
		assert(f.accept(url1));
		assert(f.accept(url2));
		
		s.put(page1);
		s.put(page2);
		
		assert(!f.accept(url1));
		assert(!f.accept(url2));
		
		Thread.sleep(1005);
		
		assert(f.accept(url1));
		assert(f.accept(url2));
	}
}

