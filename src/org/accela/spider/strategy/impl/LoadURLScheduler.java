package org.accela.spider.strategy.impl;

import org.accela.spider.util.URL;

import org.accela.spider.strategy.LoadMonitor;
import org.accela.spider.strategy.LoadMonitorGroup;
import org.accela.spider.strategy.URLScheduler;

public class LoadURLScheduler implements URLScheduler
{
	private LoadMonitorGroup monitors = null;

	private long taskDelay = 0;

	private int taskCapacity = 0;

	public LoadURLScheduler(LoadMonitor[] monitors, int taskCapacity,
			long taskDelay)
	{
		if (null == monitors)
		{
			throw new IllegalArgumentException("monitors should not be null");
		}
		if (taskDelay < 0)
		{
			throw new IllegalArgumentException(
					"taskDelay should no be negative");
		}
		if (taskCapacity < 0)
		{
			throw new IllegalArgumentException(
					"taskCapacity should no be negative");
		}

		this.monitors = new LoadMonitorGroup(monitors);
		this.taskDelay = taskDelay;
		this.taskCapacity = taskCapacity;
	}

	@Override
	public long schedule(URL url)
	{
		if (null == url)
		{
			throw new IllegalArgumentException("url should not be null");
		}

		if (monitors.getTaskCount() >= taskCapacity)
		{
			return taskDelay;
		}
		else
		{
			return 0;
		}
	}
	
	public LoadMonitor[] getLoadMonitors()
	{
		return monitors.getMonitors();
	}

	public long getTaskDelay()
	{
		return taskDelay;
	}

	public int getTaskCapacity()
	{
		return taskCapacity;
	}

}
