package org.accela.spider.stage.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.accela.stage.ConcurrentStage;

public class TesterStage<InputType> extends
		ConcurrentStage<InputType, InputType>
{
	private AtomicLong procTime = null;
	
	private AtomicInteger count=new AtomicInteger(0);

	public TesterStage(long procTime, ExecutorService executor)
	{
		super(executor);

		this.procTime = new AtomicLong(procTime);
	}

	@Override
	protected InputType preprocess(InputType input)
	{
		count.incrementAndGet();
		return input;
	}

	@Override
	protected void process(InputType input)
	{
		long startTime=System.nanoTime();
		while(System.nanoTime()-startTime<procTime.get()*1000000)
		{
			try
			{
				Thread.sleep(1);
			}
			catch (InterruptedException ex)
			{
				//do nothing
			}
		}
	}

	public long getProcTime()
	{
		return procTime.get();
	}

	public void setProcTime(long procTime)
	{
		this.procTime.set(procTime);
	}

	public int getCount()
	{
		return count.get();
	}

}
