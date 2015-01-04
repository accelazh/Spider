package org.accela.spider.strategy;

import org.accela.spider.util.URL;

public interface URLScheduler
{
	//returns 0 indicates that the task should be carried out immediately
	//returns negative is identical with returns 0
	public long schedule(URL url);
}
