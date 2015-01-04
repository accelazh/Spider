package org.accela.spider.strategy.impl;

import org.accela.spider.util.URL;

import org.accela.spider.strategy.URLFilter;

public class HttpURLFilter implements URLFilter
{
	@Override
	public boolean accept(URL url)
	{
		if(null==url)
		{
			throw new IllegalArgumentException("url should not be null");
		}
		
		if(url.getProtocol().equalsIgnoreCase("http")
				||url.getProtocol().equalsIgnoreCase("https"))
		{
			return true;
		}
		
		return false;
	}

}
