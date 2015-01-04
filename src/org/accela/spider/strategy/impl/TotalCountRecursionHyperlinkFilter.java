package org.accela.spider.strategy.impl;

import org.accela.spider.util.URL;

import org.accela.spider.data.WebPage;
import org.accela.spider.strategy.Hyperlink;
import org.accela.spider.strategy.RecursionHyperlinkFilter;

public class TotalCountRecursionHyperlinkFilter implements RecursionHyperlinkFilter
{
	private long totalCount = 0;

	private long curCount = 0;

	public TotalCountRecursionHyperlinkFilter(long totalCount)
	{
		if (totalCount < 0)
		{
			throw new IllegalArgumentException(
					"totalCount should not be negative");
		}

		this.totalCount = totalCount;
		this.curCount = 0;
	}

	@Override
	public synchronized boolean accept(WebPage parent,
			URL normalizedParentURL,
			Hyperlink link,
			URL normalizedLinkURL)
	{
		if (null == parent)
		{
			throw new IllegalArgumentException("parent should not be null");
		}
		if (null == normalizedParentURL)
		{
			throw new IllegalArgumentException("normalizedParentURL should not be null");
		}
		if (null == link)
		{
			throw new IllegalArgumentException("link should not be null");
		}
		if (null == normalizedLinkURL)
		{
			throw new IllegalArgumentException("normalizedLinkURL should not be null");
		}

		if (curCount < totalCount)
		{
			this.curCount++;
			return true;
		}
		else
		{
			return false;
		}
	}

	public long getTotalCount()
	{
		return totalCount;
	}

	public synchronized long getCurCount()
	{
		return curCount;
	}

	public void reset()
	{
		curCount=0;
	}
}
