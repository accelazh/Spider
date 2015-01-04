package org.accela.spider.data.test;

import java.net.MalformedURLException;

import org.accela.spider.data.WebPage;
import org.accela.spider.data.impl.MemoryWebPageStore;
import org.accela.spider.util.URL;

import junit.framework.TestCase;

public class TestMemoryWebPageStore extends TestCase
{
	public void testSimple() throws MalformedURLException
	{
		URL url1=new URL("http://www.sina1.com.cn");
		WebPage page1=new WebPage(url1);
		page1.setStamp(System.currentTimeMillis());
		assert(page1.getStamp()>0);
		
		URL url2=new URL("http://www.sina2.com.cn");
		WebPage page2=new WebPage(url2);
		page2.setStamp(System.currentTimeMillis());
		assert(page2.getStamp()>0);
		
		MemoryWebPageStore store=new MemoryWebPageStore();
		assert(!store.contains(url1));
		assert(store.getStamp(url1)==-1);
		assert(store.get(url1)==null);
		assert(store.remove(url1)==null);
		assert(!store.contains(url2));
		assert(store.getStamp(url2)==-1);
		assert(store.get(url2)==null);
		assert(store.remove(url2)==null);
		assert(store.size()==0);
		assert(store.isEmpty());
		assert(store.urls().size()==0);
		assert(store.pages().size()==0);
		
		store.put(page1);
		store.put(page2);
		
		assert(store.contains(url1));
		assert(store.getStamp(url1)==page1.getStamp());
		assert(store.get(url1)==page1);
		assert(store.contains(url2));
		assert(store.getStamp(url2)==page2.getStamp());
		assert(store.get(url2)==page2);
		assert(store.size()==2);
		assert(!store.isEmpty());
		assert(store.urls().size()==2);
		assert(store.pages().size()==2);
		assert(store.remove(url1)==page1);
		assert(store.remove(url2)==page2);
		assert(store.urls().size()==0);
		assert(store.pages().size()==0);
		
		store.put(page1);
		store.put(page2);
		store.clear();
		assert(store.urls().size()==0);
		assert(store.pages().size()==0);
		
		store.put(page1);
		store.put(page2);
		URL url3=new URL("http://www.sina1.com.cn");
		WebPage page3=new WebPage(url3);
		page3.setStamp(1000);
		store.put(page3);
		assert(store.urls().size()==2);
		assert(store.pages().size()==2);
		assert(store.get(url1).getStamp()==1000);
		
	}
}
