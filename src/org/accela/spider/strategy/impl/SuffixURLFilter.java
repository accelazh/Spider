package org.accela.spider.strategy.impl;

import org.accela.spider.util.URL;
import org.accela.spider.util.URLPath;

import java.util.Arrays;

import org.accela.common.Assertion;
import org.accela.spider.strategy.URLFilter;

public class SuffixURLFilter implements URLFilter
{
	private String[] suffixes;

	public SuffixURLFilter(String[] suffixes)
	{
		assert (suffixes != null):Assertion.declare();

		this.suffixes = new String[suffixes.length];
		for (int i = 0; i < this.suffixes.length; i++)
		{
			if (null == suffixes[i])
			{
				throw new IllegalArgumentException("suffix should not be null");
			}
			this.suffixes[i] = suffixes[i];
		}
	}

	@Override
	public boolean accept(URL url)
	{
		if (null == url)
		{
			throw new IllegalArgumentException("url should not be null");
		}
		
		String suffix=extractSuffix(url);
		assert(suffix!=null):Assertion.declare();
		
		for(int i=0;i<suffixes.length;i++)
		{
			assert(suffixes[i]!=null):Assertion.declare();
			if(suffixes[i].equalsIgnoreCase(suffix))
			{
				return true;
			}
		}
		
		return false;
	}

	private String extractSuffix(URL url)
	{
		String path=url.getPath();
		String file=new URLPath(path).getName();
		
		int idx = file.lastIndexOf('.');
		if (idx > 0 && idx < file.length() - 1)
		{
			return file.substring(idx + 1);
		}
		
		return "";
	}
	
	public String[] getSuffixes()
	{
        return Arrays.copyOf(suffixes, suffixes.length);
	}

}
