package org.accela.spider.strategy.impl;

import org.accela.spider.util.URL;
import java.util.List;

import org.accela.spider.data.Analysis;
import org.accela.spider.data.Content;
import org.accela.spider.data.impl.EmptyAnalysis;
import org.accela.spider.strategy.Analyzer;
import org.accela.spider.strategy.Hyperlink;

public class EmptyAnalyzer implements Analyzer
{
	@Override
	public Analysis analyse(URL url, Content content, List<Hyperlink> links)
	{
		if(null==url)
		{
			throw new IllegalArgumentException("url should not be null");
		}
		if(null==content)
		{
			throw new IllegalArgumentException("content should not be null");
		}
		if(null==links)
		{
			throw new IllegalArgumentException("links should not be null");
		}
		
		return new EmptyAnalysis();
	}

}
