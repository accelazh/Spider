package org.accela.spider.strategy.impl;

import org.accela.spider.util.URL;

import org.accela.spider.data.WebPageStore;
import org.accela.spider.strategy.URLFilter;

//see RepeatedURLFilter
public class DateURLFilter implements URLFilter
{
	private WebPageStore store = null;

	private long updateInterval = 0;

	public DateURLFilter(WebPageStore store, long updateInterval)
	{
		if (null == store)
		{
			throw new IllegalArgumentException("store should not be null");
		}
		if (updateInterval < 0)
		{
			throw new IllegalArgumentException(
					"updateInterval should not be negative");
		}

		this.store = store;
		this.updateInterval = updateInterval;
	}

	@Override
	public boolean accept(URL url)
	{
		if (null == url)
		{
			throw new IllegalArgumentException("url should not be null");
		}

		if (!store.contains(url))
		{
			return true;
		}

		long curDate = System.currentTimeMillis();
		long urlDate = store.getStamp(url);

		if (urlDate < 0 || curDate - urlDate >= updateInterval)
		{
			return true;
		}

		return false;
	}

	public WebPageStore getStore()
	{
		return store;
	}

	public long getUpdateInterval()
	{
		return updateInterval;
	}

}
