package org.accela.stage.test;

import java.util.concurrent.Executors;

import org.accela.common.Assertion;
import org.accela.stage.ConcurrentStage;

public class TimedStage extends ConcurrentStage<Integer, Integer>
{
	public TimedStage()
	{
		super(Executors.newCachedThreadPool());
	}

	@Override
	protected Integer preprocess(Integer input) throws Exception
	{
		return input;
	}

	@Override
	protected void process(Integer input)
	{
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException ex)
		{
			ex.printStackTrace();
			assert (false) : Assertion.declare();
		}
	}

}
