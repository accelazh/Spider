package org.accela.spider.strategy.impl;

import org.accela.spider.util.URL;

import org.accela.spider.data.WebPage;
import org.accela.spider.strategy.Hyperlink;
import org.accela.spider.strategy.RecursionHyperlinkFilter;

public class DuplicatedWithParentRecursionHyperlinkFilter implements
		RecursionHyperlinkFilter
{
	@Override
	public boolean accept(WebPage parent,
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
			throw new IllegalArgumentException(
					"normalizedParentURL should not be null");
		}
		if (null == link)
		{
			throw new IllegalArgumentException("link should not be null");
		}
		if (null == normalizedLinkURL)
		{
			throw new IllegalArgumentException(
					"normalizedLinkURL should not be null");
		}

		return !normalizedParentURL.equals(normalizedLinkURL);
	}

}
