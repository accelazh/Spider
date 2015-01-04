package org.accela.stage.test;

import org.accela.stage.AbstractStage;
import org.accela.stage.RejectedInputException;

public class InputImplRejectedStage extends AbstractStage<Integer>
{
	@Override
	protected void inputImpl(Integer input) throws RejectedInputException
	{
		throw new RejectedInputException("InputImplRejectedStage");
	}

}
