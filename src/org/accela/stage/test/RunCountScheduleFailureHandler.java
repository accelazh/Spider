package org.accela.stage.test;

import org.accela.stage.ScheduleFailureHandler;
import org.accela.stage.ScheduleInput;

public class RunCountScheduleFailureHandler implements
		ScheduleFailureHandler<Integer>
{
	private int runCount=0;
	
	@Override
	public void onScheduleFailure(ScheduleInput<Integer> scheduleInput)
	{
		runCount++;
	}

	public int getRunCount()
	{
		return runCount;
	}
}
