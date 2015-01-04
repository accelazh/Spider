package org.accela.stage;

import org.accela.common.Assertion;

//like ConcurrentStage, but process the input immediately without creating a new thread
public abstract class InstantStage<InputType> extends AbstractStage<InputType>
{
	public InstantStage()
	{
		// do nothing
	}

	@Override
	protected void inputImpl(InputType input) throws Exception
	{
		assert (input != null) : Assertion.declare();

		taskIn();
		try
		{
			process(input);
		}
		finally
		{
			taskOut();
		}
	}

	protected abstract void process(InputType input) throws Exception;

}
