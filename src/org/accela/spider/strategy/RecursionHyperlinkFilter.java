package org.accela.spider.strategy;

import org.accela.spider.util.URL;

import org.accela.spider.data.WebPage;

public interface RecursionHyperlinkFilter
{
	// don't forget to normalize the URL in parent and link if
	// you want to give a accurate result
	public boolean accept(WebPage parent,
			URL normalizedParentURL,
			Hyperlink link,
			URL normalizedLinkURL);
}
