package org.accela.spider.stage.test;

import org.accela.spider.strategy.URLScheduler;
import org.accela.spider.util.URL;

public class TesterURLScheduler implements URLScheduler
{
	private long delay=0;
	
	private int count=0;
	
	public TesterURLScheduler(long delay)
	{
		this.delay=delay;
	}
	
	@Override
	public long schedule(URL url)
	{
		count++;
		return delay;
	}

	public long getDelay()
	{
		return delay;
	}

	public void setDelay(long delay)
	{
		this.delay = delay;
	}

	public int getCount()
	{
		return count;
	}

}
