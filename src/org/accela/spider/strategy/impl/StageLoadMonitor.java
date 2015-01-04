package org.accela.spider.strategy.impl;

import org.accela.common.Assertion;
import org.accela.spider.strategy.LoadMonitor;
import org.accela.stage.Stage;

public class StageLoadMonitor implements LoadMonitor
{
	private Stage<?> stage=null;
	
	public StageLoadMonitor(Stage<?> stage)
	{
		if(null==stage)
		{
			throw new IllegalArgumentException("stage should not be null");
		}
		
		this.stage=stage;
	}

	@Override
	public int getTaskCount()
	{
		assert(this.stage!=null):Assertion.declare();
		
		return Math.max(0, this.stage.getTaskCount());
	}

	public Stage<?> getStage()
	{
		return stage;
	}

}
