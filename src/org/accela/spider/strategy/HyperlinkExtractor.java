package org.accela.spider.strategy;

import org.accela.spider.util.URL;
import java.util.List;

import org.accela.spider.data.Content;

public interface HyperlinkExtractor
{
	public List<Hyperlink> extract(URL context, Content content);
}
