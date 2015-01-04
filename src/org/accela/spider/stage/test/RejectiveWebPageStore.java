package org.accela.spider.stage.test;

import org.accela.spider.data.WebPage;
import org.accela.spider.data.WebPageStore;
import org.accela.spider.data.WebPageStoreException;
import org.accela.spider.util.URL;

public class RejectiveWebPageStore implements WebPageStore
{
	@Override
	public boolean contains(URL url)
	{
		return false;
	}

	@Override
	public long getStamp(URL url)
	{
		return -1;
	}

	@Override
	public void put(WebPage page) throws WebPageStoreException
	{
		throw new WebPageStoreException();
	}

}
