package org.accela.spider.strategy;

import org.accela.spider.util.URL;
import java.util.Arrays;

import org.accela.common.Assertion;
import org.accela.spider.data.WebPage;

public class RecursionHyperlinkFilterGroup implements RecursionHyperlinkFilter
{
	private RecursionHyperlinkFilter[] filters = null;

	public RecursionHyperlinkFilterGroup(RecursionHyperlinkFilter[] filters)
	{
		if (null == filters)
		{
			throw new IllegalArgumentException("filters should not be null");
		}

		this.filters = new RecursionHyperlinkFilter[filters.length];
		for (int i = 0; i < this.filters.length; i++)
		{
			if (null == filters[i])
			{
				throw new IllegalArgumentException("fiter should not be null");
			}
			this.filters[i] = filters[i];
		}
	}

	// filters are guaranteed to run in the
	// sequence as passed in
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

		for (int i = 0; i < filters.length; i++)
		{
			assert (filters[i] != null) : Assertion.declare();
			if (!filters[i].accept(parent, normalizedParentURL, link, normalizedLinkURL))
			{
				return false;
			}
		}

		return true;
	}
	
	public RecursionHyperlinkFilter[] getFilters()
	{
		return Arrays.copyOf(filters, filters.length);
	}
}
