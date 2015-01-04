package org.accela.spider.strategy;

import org.accela.spider.util.URL;
import java.util.List;

import org.accela.spider.data.Analysis;
import org.accela.spider.data.Content;

public interface Analyzer
{
	public Analysis analyse(URL url, Content content, List<Hyperlink> links);
}
