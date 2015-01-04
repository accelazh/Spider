package org.accela.spider.strategy;

import org.accela.stage.Stage;

public interface AbortPolicy<InputType, OutputType, CauseType>
{
	public void onAbort(boolean causedByError, Stage<InputType> hostStage,
			Stage<OutputType> nextStage, InputType input, OutputType output,
			CauseType cause, Exception ex);
}
