package org.accela.spider.stage.test;

import org.accela.stage.RejectedInputException;
import org.accela.stage.Stage;

public class RejectiveStage<InputType> implements Stage<InputType>
{
	@Override
	public int getTaskCount()
	{
		return 0;
	}

	@Override
	public void input(InputType input) throws RejectedInputException
	{
		throw new RejectedInputException();
	}

}
