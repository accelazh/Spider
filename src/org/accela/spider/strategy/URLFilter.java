package org.accela.spider.strategy;

import org.accela.spider.util.URL;

public interface URLFilter
{
	public boolean accept(URL url);
}
