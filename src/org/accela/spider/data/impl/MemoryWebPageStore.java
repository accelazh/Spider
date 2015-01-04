package org.accela.spider.data.impl;

import org.accela.spider.util.URL;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.accela.spider.data.WebPage;
import org.accela.spider.data.WebPageStore;

public class MemoryWebPageStore implements WebPageStore
{
	private ConcurrentMap<URL, WebPage> pages = new ConcurrentHashMap<URL, WebPage>();

	@Override
	public boolean contains(URL url)
	{
		if (null == url)
		{
			throw new IllegalArgumentException("url should not be null");
		}

		return pages.containsKey(url);
	}

	@Override
	public long getStamp(URL url)
	{
		if (null == url)
		{
			throw new IllegalArgumentException("url should not be null");
		}

		WebPage page = pages.get(url);
		if (null == page)
		{
			return -1;
		}
		else
		{
			return page.getStamp();
		}
	}

	@Override
	public void put(WebPage page)
	{
		if (null == page)
		{
			throw new IllegalArgumentException("page should not be null");
		}
		if (null == page.getURL())
		{
			throw new IllegalArgumentException(
					"page.getURL() should not be null");
		}

		pages.put(page.getURL(), page);
	}
	
	public WebPage get(URL url)
	{
		if(null==url)
		{
			throw new IllegalArgumentException("url should not be null");
		}
		
		return pages.get(url);
	}
	
	public WebPage remove(URL url)
	{
		if(null==url)
		{
			throw new IllegalArgumentException("url should not be null");
		}
		
		return pages.remove(url);
	}
	
	public int size()
	{
		return pages.size();
	}
	
	public boolean isEmpty()
	{
		return pages.isEmpty();
	}
	
	public void clear()
	{
		pages.clear();
	}
	
	public Collection<URL> urls()
	{
		return pages.keySet();
	}
	
	public Collection<WebPage> pages()
	{
		return pages.values();
	}
}
