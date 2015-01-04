package org.accela.spider.stage;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;

import org.accela.common.Assertion;
import org.accela.spider.data.WebPage;
import org.accela.spider.strategy.AbortPolicy;
import org.accela.spider.strategy.URLScheduler;
import org.accela.spider.strategy.URLSchedulerGroup;
import org.accela.spider.strategy.impl.DelegateAbortPolicy;
import org.accela.stage.InstantStage;
import org.accela.stage.RejectedOutputException;
import org.accela.stage.ScheduleFailureHandler;
import org.accela.stage.ScheduleInput;
import org.accela.stage.SchedulerStage;
import org.accela.stage.Stage;

public class SchedulingStage extends InstantStage<WebPage>
{
	private URLSchedulerGroup schedulers = null;

	private Stage<WebPage> nextStage = null;

	private AbortPolicy<WebPage, WebPage, String> abortPolicy = null;

	private SchedulerStage schedulerStage = null;

	private int maxScheduleCount = -1;

	private ScheduledButFailedHandler scheduledButFailedHandler = new ScheduledButFailedHandler();

	public SchedulingStage(Stage<WebPage> nextStage,
			URLScheduler[] schedulers,
			AbortPolicy<WebPage, WebPage, String> abortPolicy,
			ScheduledExecutorService executor,
			int maxScheduleCount)
	{
		if (null == nextStage)
		{
			throw new IllegalArgumentException("nextStage should not be null");
		}
		if (null == schedulers)
		{
			throw new IllegalArgumentException("schedulers should not be null");
		}
		if (null == executor)
		{
			throw new IllegalArgumentException("executor should not be null");
		}

		this.nextStage = nextStage;
		this.schedulers = new URLSchedulerGroup(schedulers);
		this.abortPolicy = new DelegateAbortPolicy<WebPage, WebPage, String>(abortPolicy);

		this.schedulerStage = new SchedulerStage(executor);
		this.maxScheduleCount = maxScheduleCount;
	}

	@Override
	protected void process(WebPage input) throws RejectedExecutionException
	{
		long delay = schedulers.schedule(input.getURL());
		if (delay > 0)
		{
			if (maxScheduleCount >= 0
					&& schedulerStage.getTaskCount() >= maxScheduleCount)
			{
				throw new RejectedExecutionException("schedule full");
			}
			else
			{
				try
				{
					this.output(schedulerStage, new ScheduleInput<WebPage>(
							input, SchedulingStage.this, delay,
							scheduledButFailedHandler));
				}
				catch (RejectedOutputException ex)
				{
					abortPolicy.onAbort(true,
							this,
							this,
							input,
							input,
							"the scheduler pool rejected the output",
							ex);
				}
			}
		}
		else
		{
			try
			{
				this.output(nextStage, input);
			}
			catch (RejectedOutputException ex)
			{
				abortPolicy.onAbort(true,
						this,
						nextStage,
						input,
						input,
						"the next stage rejected the output",
						ex);
			}
		}
	}

	private class ScheduledButFailedHandler implements
			ScheduleFailureHandler<WebPage>
	{
		@Override
		public void onScheduleFailure(ScheduleInput<WebPage> scheduleInput)
		{
			assert (scheduleInput != null) : Assertion.declare();

			abortPolicy.onAbort(true,
					SchedulingStage.this,
					SchedulingStage.this,
					scheduleInput.getOutput(),
					scheduleInput.getOutput(),
					"scheduled but failed",
					null);
		}
	}

	// ScheduleStage is hiding the existence of its member schedulerStage.
	// If don't override this method like this below, the taskCount will
	// not behave as if member schedulerStage is just part of class
	// ScheduleStage.
	@Override
	public int getTaskCount()
	{
		return super.getTaskCount() + schedulerStage.getTaskCount();
	}
}
