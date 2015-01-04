package org.accela.spider.strategy.impl;

import org.accela.spider.util.URL;
import org.accela.spider.strategy.URLScheduler;
import org.accela.spider.util.PeriodicallyClearConcurrentHashMap;

//wikipedia web crawler has the description for politeness
public class PolitenessURLScheduler implements URLScheduler
{
	private int taskPerRound = 0;

	private long interval = 0;

	private PeriodicallyClearConcurrentHashMap<String, TaskIntervalURLScheduler> records = null;

	public PolitenessURLScheduler(int taskPerRound,
			long interval,
			int intervalPerCleanUp)
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
		if (intervalPerCleanUp < 1)
		{
			throw new IllegalArgumentException(
					"intervalPerCleanUp should not be negative");
		}

		this.taskPerRound = taskPerRound;
		this.interval = interval;
		this.records = new PeriodicallyClearConcurrentHashMap<String, TaskIntervalURLScheduler>(
				intervalPerCleanUp * interval);
	}

	@Override
	public long schedule(URL url)
	{
		if (null == url)
		{
			throw new IllegalArgumentException("url should not be null");
		}

		String host = url.getHost();
		TaskIntervalURLScheduler record = records.get(host);
		if (null == record)
		{
			record = new TaskIntervalURLScheduler(taskPerRound, interval);
			TaskIntervalURLScheduler old = records.putIfAbsent(host, record);
			if (old != null)
			{
				record = old;
			}
		}

		return record.schedule(url);
	}

	public int getTaskPerRound()
	{
		return taskPerRound;
	}

	public long getInterval()
	{
		return interval;
	}

	public int intervalPerCleanUp()
	{
		return (int) (records.getPeriod() / interval);
	}

}
