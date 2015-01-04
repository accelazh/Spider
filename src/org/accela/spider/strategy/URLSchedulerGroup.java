package org.accela.spider.strategy;

import org.accela.spider.util.URL;
import java.util.Arrays;

import org.accela.common.Assertion;

public class URLSchedulerGroup implements URLScheduler
{
	private URLScheduler[] schedulers = null;

	public URLSchedulerGroup(URLScheduler[] schedulers)
	{
		if (null == schedulers)
		{
			throw new IllegalArgumentException("filters should not be null");
		}

		this.schedulers = new URLScheduler[schedulers.length];
		for (int i = 0; i < this.schedulers.length; i++)
		{
			if (null == schedulers[i])
			{
				throw new IllegalArgumentException("schedulers should not be null");
			}
			this.schedulers[i] = schedulers[i];
		}
	}

	// schedulers are guaranteed to run in the
	// sequence as passed in
	@Override
	public long schedule(URL url)
	{
		if (null == url)
		{
			throw new IllegalArgumentException("url should not be null");
		}

		long delay=0;
		for (int i = 0; i < schedulers.length; i++)
		{
			assert (schedulers[i] != null) : Assertion.declare();
			
			delay = Math.max(delay, schedulers[i].schedule(url));
		}

		return delay;
	}
	
	public URLScheduler[] getSchedulers()
	{
		return Arrays.copyOf(schedulers, schedulers.length);
	}

}
