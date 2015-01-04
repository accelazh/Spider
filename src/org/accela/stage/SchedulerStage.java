package org.accela.stage;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.accela.common.Assertion;

//input a task, in which delay is specified. after the specified delay, SchedulerStage
//will do the output according to the task. the task is an ScheduleInput object. it 
//contains the delay, the object to output and where to output it.
public class SchedulerStage extends AbstractStage<ScheduleInput<?>>
{
	private ScheduledExecutorService executor = null;

	public SchedulerStage(ScheduledExecutorService executor)
	{
		if (null == executor)
		{
			throw new IllegalArgumentException("executor should not be null");
		}
		if (executor.isShutdown())
		{
			throw new IllegalArgumentException(
					"executor should not have been shut down");
		}

		this.executor = executor;
	}

	@Override
	protected void inputImpl(ScheduleInput<?> input) throws Exception
	{
		assert (input != null) : Assertion.declare();

		taskIn();
		try
		{
			executor.schedule(
					new Worker(input),
					input.getDelay(),
					TimeUnit.MILLISECONDS);
		}
		catch (Exception ex)
		{
			taskOut();
			throw ex;
		}

	}

	public ScheduledExecutorService getExecutor()
	{
		return executor;
	}

	// =============================================================================

	private class Worker implements Runnable
	{
		private ScheduleInput<?> input = null;

		public Worker(ScheduleInput<?> input)
		{
			if (null == input)
			{
				throw new IllegalArgumentException("input should not be null");
			}

			this.input = input;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void run()
		{

			assert (input != null) : Assertion.declare();

			taskOut();	//SchedulerStage's taskCount refers to its waiting queue size
			
			Object output = input.getOutput();
			assert (output != null) : Assertion.declare();
			Stage<Object> out = (Stage<Object>) input.getOut();
			assert (out != null) : Assertion.declare();

			try
			{
				SchedulerStage.this.output(out, output);
			}
			catch (RejectedOutputException ex)
			{
				if (input.getRetry() <= 0)
				{
					if (input.getHandler() != null)
					{
						ScheduleFailureHandler<Object> handler = (ScheduleFailureHandler<Object>) input
								.getHandler();
						handler
								.onScheduleFailure((ScheduleInput<Object>) input);
					}
				}
				else
				{
					try
					{
						input.decreaseRetry();
						SchedulerStage.this.input(input);
					}
					catch (RejectedInputException ex_inner)
					{
						if (input.getHandler() != null)
						{
							ScheduleFailureHandler<Object> handler = (ScheduleFailureHandler<Object>) input
									.getHandler();
							handler
									.onScheduleFailure((ScheduleInput<Object>) input);
						}
					}// inner catch
				}// end of if

			}// outer catch
			catch (Exception ex)
			{
				assert (false) : Assertion.declare();
			}
		}
	}
}
