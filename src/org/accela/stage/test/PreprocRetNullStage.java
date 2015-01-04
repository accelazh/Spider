package org.accela.stage.test;

import java.util.concurrent.Executors;

import org.accela.common.Assertion;
import org.accela.stage.ConcurrentStage;

public class PreprocRetNullStage extends ConcurrentStage<Integer, String>
{
	private boolean preprocessRuned = false;

	private boolean processRuned = false;

	public PreprocRetNullStage()
	{
		super(Executors.newCachedThreadPool());
	}

	@Override
	protected String preprocess(Integer input) throws Exception
	{
		assert (input != null):Assertion.declare();

		preprocessRuned = true;

		return null;
	}

	@Override
	protected void process(String input)
	{
		assert (false):Assertion.declare();
		processRuned = true;
	}

	public boolean isPreprocessRuned()
	{
		return preprocessRuned;
	}

	public boolean isProcessRuned()
	{
		return processRuned;
	}

}
