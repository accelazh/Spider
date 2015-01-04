package org.accela.spider.strategy;

import java.util.Arrays;

import org.accela.common.Assertion;

public class LoadMonitorGroup implements LoadMonitor
{
	private LoadMonitor[] monitors = null;

	public LoadMonitorGroup(LoadMonitor[] monitors)
	{
		if (null == monitors)
		{
			throw new IllegalArgumentException("monitors should not be null");
		}

		this.monitors = new LoadMonitor[monitors.length];
		for (int i = 0; i < this.monitors.length; i++)
		{
			if (null == monitors[i])
			{
				throw new IllegalArgumentException("monitor should not be null");
			}
			this.monitors[i] = monitors[i];
		}
	}

	@Override
	public int getTaskCount()
	{
		int sum = 0;
		for (int i = 0; i < monitors.length; i++)
		{
			assert (monitors[i] != null) : Assertion.declare();

			sum += Math.max(0, monitors[i].getTaskCount());
		}

		return sum;
	}

	public LoadMonitor[] getMonitors()
	{
		return Arrays.copyOf(monitors, monitors.length);
	}

}
