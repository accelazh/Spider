package org.accela.spider.strategy.test;

import org.accela.spider.strategy.impl.StageLoadMonitor;
import org.accela.stage.RejectedInputException;
import org.accela.stage.Stage;

import junit.framework.TestCase;

public class TestStageLoadMonitor extends TestCase
{
	public void testSimple()
	{
		StageLoadMonitor m=new StageLoadMonitor(new TesterStage<String>(0));
		assert(m.getTaskCount()==0);
		
		m=new StageLoadMonitor(new TesterStage<String>(100));
		assert(m.getTaskCount()==100);
		
		m=new StageLoadMonitor(new TesterStage<String>(-100));
		assert(m.getTaskCount()==0);
	}
	
	private static class TesterStage<T> implements Stage<T>
	{
		public int value=0;
		
		public TesterStage(int value)
		{
			this.value=value;
		}
		
		@Override
		public int getTaskCount()
		{
			return value;
		}

		@Override
		public void input(T input) throws RejectedInputException
		{
			throw new RejectedInputException();
		}

	}
}
