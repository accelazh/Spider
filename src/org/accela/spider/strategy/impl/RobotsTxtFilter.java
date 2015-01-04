package org.accela.spider.strategy.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import org.accela.spider.util.URL;

import org.accela.common.Assertion;
import org.accela.spider.strategy.URLFilter;
import org.accela.spider.util.PeriodicallyClearConcurrentHashMap;
import org.accela.spider.util.RobotsTxt;
import org.accela.spider.util.URLTextDownloader;

//robots exclusion protocol is implemented using this filter
public class RobotsTxtFilter implements URLFilter
{
	private String agent = null;
	private PeriodicallyClearConcurrentHashMap<URL, RobotsTxt> records = null;

	public RobotsTxtFilter(String agent, long clearUpInterval)
	{
		if (clearUpInterval < 0)
		{
			throw new IllegalArgumentException("clearUpInterval should not be negative");
		}

		this.agent = agent;
		records = new PeriodicallyClearConcurrentHashMap<URL, RobotsTxt>(clearUpInterval);
	}

	public long getClearUpInterval()
	{
		return records.getPeriod();
	}

	@Override
	public boolean accept(URL url)
	{
		if (null == url)
		{
			throw new IllegalArgumentException("url should not be null");
		}

		// get host URL
		URL hostURL = null;
		try
		{
			hostURL = new URL(url.getProtocol(), url.getHost(), "/");
		}
		catch (MalformedURLException ex)
		{
			ex.printStackTrace();
			assert (false) : Assertion.declare();
			return true;
		}

		// get robots.txt
		RobotsTxt rt = records.get(hostURL);
		if (null == rt)
		{
			assert (hostURL.getPath().equals("/")) : Assertion.declare();
			rt = fetchRobotsTxt(hostURL);

			assert (rt != null) : Assertion.declare();
			records.put(hostURL, rt);
		}

		// check access
		if (null == rt)
		{
			return true;
		}
		else
		{
			return !rt.block(agent, url.getPath());
		}
	}

	private RobotsTxt fetchRobotsTxt(URL hostURL)
	{
		assert (hostURL != null) : Assertion.declare();
		assert (hostURL.getPath().equals("/"));

		URLTextDownloader downloader = new URLTextDownloader();

		URL robotTxtURL = null;
		try
		{
			robotTxtURL = new URL(hostURL.getProtocol(), hostURL.getHost(), "/robots.txt");
		}
		catch (MalformedURLException ex)
		{
			ex.printStackTrace();
			assert (false) : Assertion.declare();
		}
		String content = null;
		try
		{
			content = downloader.download(robotTxtURL);
		}
		catch (IOException ex)
		{
			return new RobotsTxt("");
		}

		return new RobotsTxt(content);
	}

}
