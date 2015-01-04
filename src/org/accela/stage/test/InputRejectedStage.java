package org.accela.stage.test;

import java.util.concurrent.Executors;

import org.accela.common.Assertion;
import org.accela.stage.ConcurrentStage;
import org.accela.stage.RejectedInputException;

public class InputRejectedStage extends ConcurrentStage<Integer, Integer>
{
	public InputRejectedStage()
	{
		super(Executors.newCachedThreadPool());
	}

	@Override
	protected void process(Integer input)
	{
		assert(input!=null):Assertion.declare();
	}

	@Override
	public void input(Integer input) throws RejectedInputException
	{
		throw new RejectedInputException("InputRejectedStage");
	}

	@Override
	protected Integer preprocess(Integer input) throws Exception
	{
		return input;
	}

}
