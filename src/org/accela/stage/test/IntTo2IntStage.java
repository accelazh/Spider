package org.accela.stage.test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.accela.common.Assertion;
import org.accela.stage.ConcurrentStage;
import org.accela.stage.RejectedOutputException;
import org.accela.stage.Stage;

public class IntTo2IntStage extends ConcurrentStage<Integer, Integer>
{
	private Stage<Integer> outLeft = null;
	private Stage<Integer> outRight = null;
	private Stage<Integer> outThree = null;
	private boolean smallExecutor = false;
	private boolean procFail = false;
	private int rejectedCount = 0;
	private int runCount=0;
	private int preprocessCount=0;
	
	private int value=0;
	private boolean preprocessRuned=false;

	public IntTo2IntStage(Stage<Integer> outLeft, Stage<Integer> outRight,
			boolean smallExecutor, boolean procFail, Stage<Integer> outThree)
	{
		super(smallExecutor ? new ThreadPoolExecutor(2, 2, 60L,
				TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2))
				: Executors.newCachedThreadPool());

		this.outLeft = outLeft;
		this.outRight = outRight;
		this.smallExecutor = smallExecutor;
		this.procFail = procFail;
		this.outThree = outThree;
	}

	@Override
	protected void process(Integer input)
	{
		assert (input != null):Assertion.declare();
		
		runCount++;
		
		if (smallExecutor)
		{
			synchronized (this)
			{
				try
				{
					wait();
				} catch (InterruptedException ex)
				{
					ex.printStackTrace();
					assert (false):Assertion.declare();
				}
			}
		}
		if (procFail)
		{
			throw new NullPointerException("Sorry, I failed");
		}

		this.value=input;
		if (outLeft != null)
		{
			try
			{
				output(outLeft, input + 1);
			} catch (RejectedOutputException ex)
			{
				rejectedCount++;
			} catch (Exception ex)
			{
				ex.printStackTrace();
				assert (false):Assertion.declare();
			}
		}
		if (outRight != null)
		{
			try
			{
				output(outRight, input + 1);
			} catch (RejectedOutputException ex)
			{
				rejectedCount++;
			} catch (Exception ex)
			{
				ex.printStackTrace();
				assert (false):Assertion.declare();
			}
		}
		if (outThree != null)
		{
			try
			{
				output(outThree, input + 1);
			} catch (RejectedOutputException ex)
			{
				rejectedCount++;
			} catch (Exception ex)
			{
				ex.printStackTrace();
				assert (false):Assertion.declare();
			}
		}
	}

	public Stage<Integer> getOutLeft()
	{
		return outLeft;
	}

	public Stage<Integer> getOutRight()
	{
		return outRight;
	}

	public boolean isProcFail()
	{
		return procFail;
	}

	public int getRejectedCount()
	{
		return rejectedCount;
	}

	public Stage<Integer> getOutThree()
	{
		return outThree;
	}

	public void setOutThree(Stage<Integer> outThree)
	{
		this.outThree = outThree;
	}

	public void setOutLeft(IntTo2IntStage outLeft)
	{
		this.outLeft = outLeft;
	}

	public void setOutRight(IntTo2IntStage outRight)
	{
		this.outRight = outRight;
	}

	public int getValue()
	{
		return value;
	}

	@Override
	protected Integer preprocess(Integer input) throws Exception
	{
		preprocessRuned=true;
		preprocessCount++;
		return input;
	}

	public boolean isPreprocessRuned()
	{
		return preprocessRuned;
	}

	public int getRunCount()
	{
		return runCount;
	}

	public int getPreprocessCount()
	{
		return preprocessCount;
	}

}
