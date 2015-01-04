package org.accela.spider.stage.test;

import org.accela.stage.RejectedInputException;
import org.accela.stage.Stage;

public class EmptyStage<InputType> implements Stage<InputType>
{
	private int invokeCount=0;
	
	@Override
	public int getTaskCount()
	{
		return 0;
	}

	@Override
	public void input(InputType input) throws RejectedInputException
	{
		invokeCount++;
	}

	public int getInvokeCount()
	{
		return invokeCount;
	}

}
