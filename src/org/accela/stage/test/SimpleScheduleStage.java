package org.accela.stage.test;

import java.util.concurrent.Executors;

import org.accela.stage.SchedulerStage;

public class SimpleScheduleStage extends SchedulerStage
{
	public SimpleScheduleStage(int coreCount)
	{
		super(Executors.newScheduledThreadPool(coreCount));
	}
	
}
