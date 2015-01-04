package org.accela.stage.test;

import org.accela.stage.AbstractStage;

public class InputImplFailureStage extends AbstractStage<Integer>
{
	@Override
	protected void inputImpl(Integer input) throws Exception
	{
		throw new NullPointerException("InputImplFailureStage");
	}

}
