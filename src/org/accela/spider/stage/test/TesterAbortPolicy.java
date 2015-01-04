package org.accela.spider.stage.test;

import java.util.concurrent.atomic.AtomicInteger;

import org.accela.spider.strategy.AbortPolicy;
import org.accela.stage.Stage;

public class TesterAbortPolicy<InputType, OutputType, CauseType> implements
		AbortPolicy<InputType, OutputType, CauseType>
{
	private AtomicInteger invokeCount=new AtomicInteger(0);
	
	@Override
	public void onAbort(boolean causedByError,
			Stage<InputType> hostStage,
			Stage<OutputType> nextStage,
			InputType input,
			OutputType output,
			CauseType cause,
			Exception ex)
	{
		invokeCount.incrementAndGet();
	}

	public int getInvokeCount()
	{
		return invokeCount.get();
	}

}
