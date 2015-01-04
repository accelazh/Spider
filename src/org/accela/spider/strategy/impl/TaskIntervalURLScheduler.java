package org.accela.spider.strategy.impl;

import org.accela.spider.strategy.URLScheduler;
import org.accela.spider.util.URL;

//if you want to control the cpu usage of spider, you can use this scheduler 
//to control how many tasks the spider should process in a given time interval. 
public class TaskIntervalURLScheduler implements URLScheduler
{
	private int taskPerRound = 0;
	
	private int taskCount=0;

	private long nanoInterval = 0;

	private long intervalStart = 0;

	private static final long ONE_MILLION = 1000000;

	public TaskIntervalURLScheduler(int taskPerRound, long interval)
	{
		if (taskPerRound < 1)
		{
			throw new IllegalArgumentException(
					"taskPerRound should not be less than 1");
		}
		if (interval < 0)
		{
			throw new IllegalArgumentException(
					"interval should not be negative");
		}

		this.taskPerRound=taskPerRound;
		this.taskCount=0;
		this.nanoInterval = interval * ONE_MILLION; // 使用nano时间提高精度至1ms以上
		this.intervalStart = System.nanoTime() - nanoInterval;
	}

	
	@Override
	public synchronized long schedule(URL url)
	{
		if(null==url)
		{
			throw new IllegalArgumentException("url should not be null");
		}
		
		long curTask = System.nanoTime();
		long curInterval=curTask-intervalStart;
		if(curInterval<nanoInterval)
		{
			if(taskCount<taskPerRound)
			{
				taskCount++;
				return 0;
			}
			else
			{
				return (nanoInterval-curInterval)/ONE_MILLION+1;
			}
		}
		else
		{
			taskCount=1;
			intervalStart=curTask;
			return 0;
		}
	}
	
	public int getTaskPerRound()
	{
		return taskPerRound;
	}


	public long getInterval()
	{
		return nanoInterval / ONE_MILLION;
	}

}
