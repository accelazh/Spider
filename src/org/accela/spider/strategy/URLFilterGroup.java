package org.accela.spider.strategy;

import org.accela.spider.util.URL;
import java.util.Arrays;

import org.accela.common.Assertion;

public class URLFilterGroup implements URLFilter
{
	private URLFilter[] filters = null;

	public URLFilterGroup(URLFilter[] filters)
	{
		if (null == filters)
		{
			throw new IllegalArgumentException("filters should not be null");
		}

		this.filters = new URLFilter[filters.length];
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
	public boolean accept(URL url)
	{
		if(null==url)
		{
			throw new IllegalArgumentException("url should not be null");
		}
		
		for(int i=0;i<filters.length;i++)
		{
			assert(filters[i]!=null):Assertion.declare();
			if(!filters[i].accept(url))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public URLFilter[] getFilters()
	{
		return Arrays.copyOf(filters, filters.length);
	}

}
