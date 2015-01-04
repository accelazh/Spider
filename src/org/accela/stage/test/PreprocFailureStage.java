package org.accela.stage.test;

import java.util.concurrent.Executors;

import org.accela.common.Assertion;
import org.accela.stage.ConcurrentStage;

public class PreprocFailureStage extends ConcurrentStage<Integer, String>
{
	public PreprocFailureStage()
	{
		super(Executors.newCachedThreadPool());
	}

	@Override
	protected String preprocess(Integer input) throws Exception
	{
		assert(input!=null):Assertion.declare();
		throw new NullPointerException("Sorry, I failed");
	}

	@Override
	protected void process(String input)
	{
		assert(false):Assertion.declare();
	}

}
