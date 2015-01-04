package org.accela.stage;

import org.accela.common.Assertion;

public class ScheduleInput<OutputType>
{
	private OutputType output = null;

	private Stage<OutputType> out = null;

	private long delay = 0;

	private int retry = 0;

	private ScheduleFailureHandler<OutputType> handler = null;

	public ScheduleInput(OutputType output, Stage<OutputType> out, long delay)
	{
		this(output, out, delay, 0, null);
	}

	public ScheduleInput(OutputType output, Stage<OutputType> out, long delay,
			int retry)
	{
		this(output, out, delay, retry, null);
	}

	public ScheduleInput(OutputType output, Stage<OutputType> out, long delay,
			ScheduleFailureHandler<OutputType> handler)
	{
		this(output, out, delay, 0, handler);
	}

	public ScheduleInput(OutputType output, Stage<OutputType> out, long delay,
			int retry, ScheduleFailureHandler<OutputType> handler)
	{
		if (null == output)
		{
			throw new IllegalArgumentException("output should not be null");
		}
		if (null == out)
		{
			throw new IllegalArgumentException("out should not be null");
		}
		if (delay < 0)
		{
			throw new IllegalArgumentException("delay should not be negative");
		}
		if (retry < 0)
		{
			throw new IllegalArgumentException("retry should not be negative");
		}

		this.output = output;
		this.out = out;
		this.delay = delay;
		this.retry = retry;
		this.handler = handler;
	}

	public long getDelay()
	{
		assert (delay >= 0) : Assertion.declare();
		return delay;
	}

	public int getRetry()
	{
		assert (retry >= 0) : Assertion.declare();
		return retry;
	}

	public void decreaseRetry()
	{
		retry -= Math.min(1, retry);
	}

	public OutputType getOutput()
	{
		assert (output != null) : Assertion.declare();
		return output;
	}

	public Stage<OutputType> getOut()
	{
		assert (out != null) : Assertion.declare();
		return out;
	}

	public ScheduleFailureHandler<OutputType> getHandler()
	{
		return handler;
	}
}
