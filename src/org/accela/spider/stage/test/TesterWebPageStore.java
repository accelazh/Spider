package org.accela.spider.stage.test;

import java.util.concurrent.atomic.AtomicInteger;

import org.accela.spider.data.WebPage;
import org.accela.spider.data.WebPageStore;
import org.accela.spider.data.WebPageStoreException;
import org.accela.spider.util.URL;

public class TesterWebPageStore implements WebPageStore
{
	private AtomicInteger count=new AtomicInteger(0);
	
	@Override
	public boolean contains(URL url)
	{
		return false;
	}

	@Override
	public long getStamp(URL url)
	{
		return 0;
	}

	@Override
	public void put(WebPage page) throws WebPageStoreException
	{
		count.incrementAndGet();
	}

	public int getCount()
	{
		return count.get();
	}

}
