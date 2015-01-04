package org.accela.spider.strategy.impl;

import java.net.MalformedURLException;
import java.util.List;

import org.accela.common.Assertion;
import org.accela.spider.util.URL;
import org.accela.spider.util.URLPath;

import org.accela.spider.strategy.URLNormalizer;

public class SimpleURLNormalizer implements URLNormalizer
{
	private boolean trimQuery=false;
	
	public SimpleURLNormalizer()
	{
		this(false);
	}
	
	public SimpleURLNormalizer(boolean trimQuery)
	{
		this.trimQuery=trimQuery;
	}
	
	@Override
	public URL normalize(URL url)
	{
		if (null == url)
		{
			throw new IllegalArgumentException("url should not be null");
		}

		String protocol = url.getProtocol();
		String host = url.getHost();
		int port=url.getPort();
		String path=url.getPath();
		String query=trimQuery?null:url.getQuery();
		String ref=null;

		List<String> urlTokens = new URLPath(path).getTokens();
		String file = urlTokens.size() > 0 ? urlTokens
				.get(urlTokens.size() - 1) : "";
		int idxOfDot = file.indexOf('.');
		String fileName = idxOfDot > 0 ? file.substring(0, idxOfDot) : file;
		if (fileName.equalsIgnoreCase("index")
				|| fileName.equalsIgnoreCase("default"))
		{
			assert(urlTokens.size()>0):Assertion.declare();
			urlTokens.remove(urlTokens.size() - 1);
		}

		path = new URLPath(urlTokens).getPath();

		// ==generate new URL==
		URL newUrl = null;
		try
		{
			newUrl = new URL(protocol, host, port, path, query, ref);
		}
		catch (MalformedURLException ex)
		{
			ex.printStackTrace();
			assert (false) : Assertion.declare();
		}

		return newUrl;
	}

	public boolean isTrimQuery()
	{
		return trimQuery;
	}

}
