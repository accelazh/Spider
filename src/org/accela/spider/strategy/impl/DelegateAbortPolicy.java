package org.accela.spider.strategy.impl;

import org.accela.spider.strategy.AbortPolicy;
import org.accela.stage.Stage;

public class DelegateAbortPolicy<InputType, OutputType, CauseType> implements
		AbortPolicy<InputType, OutputType, CauseType>
{
	private AbortPolicy<InputType, OutputType, CauseType> policy = null;

	public DelegateAbortPolicy()
	{
		this(null);
	}

	public DelegateAbortPolicy(AbortPolicy<InputType, OutputType, CauseType> policy)
	{
		this.policy = policy;
	}

	public AbortPolicy<InputType, OutputType, CauseType> getPolicy()
	{
		return policy;
	}

	public void setPolicy(AbortPolicy<InputType, OutputType, CauseType> policy)
	{
		this.policy = policy;
	}

	@Override
	public void onAbort(boolean causedByError,
			Stage<InputType> hostStage,
			Stage<OutputType> nextStage,
			InputType input,
			OutputType output,
			CauseType cause,
			Exception ex)
	{
		if (policy != null)
		{
			policy.onAbort(causedByError,
					hostStage,
					nextStage,
					input,
					output,
					cause,
					ex);
		}
	}

}
