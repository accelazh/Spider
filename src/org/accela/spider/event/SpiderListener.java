package org.accela.spider.event;

import org.accela.spider.util.URL;

import org.accela.spider.data.WebPage;

public interface SpiderListener
{
	public void onBegin(URL url, int recursion);
	
	public void onEnd(WebPage page);
}
