package org.accela.stage;

public interface ScheduleFailureHandler<OutputType>
{
	public void onScheduleFailure(ScheduleInput<OutputType> scheduleInput);
}
