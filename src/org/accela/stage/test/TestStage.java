package org.accela.stage.test;

import java.lang.reflect.Method;

import org.accela.common.Assertion;
import org.accela.stage.RejectedInputException;
import org.accela.stage.ScheduleInput;

import junit.framework.TestCase;

public class TestStage extends TestCase
{
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testInputNull()
	{
		IntTo2IntStage s = new IntTo2IntStage(null, null, false, false, null);
		assert (s.getTaskCount() == 0);
		boolean hasException = false;
		try
		{
			s.input(null);
		}
		catch (Exception ex)
		{
			assert (ex.getClass() == IllegalArgumentException.class);
			hasException = true;
		}
		assert (hasException);
		assert (s.getTaskCount() == 0);
	}

	public void testLinkedStage()
	{
		IntTo2IntStage s = new IntTo2IntStage(new IntTo2IntStage(
				new IntTo2IntStage(null, null, false, false, null),
				new IntTo2IntStage(null, null, false, false, null), false,
				false, null), new IntTo2IntStage(new IntTo2IntStage(null, null,
				false, false, null), new IntTo2IntStage(null, null, false,
				false, null), false, false, null), false, false, null);

		IntTo2IntStage curS1 = (IntTo2IntStage) ((IntTo2IntStage) s
				.getOutLeft()).getOutLeft();
		IntTo2IntStage curS2 = (IntTo2IntStage) ((IntTo2IntStage) s
				.getOutLeft()).getOutRight();
		IntTo2IntStage curS3 = (IntTo2IntStage) ((IntTo2IntStage) s
				.getOutRight()).getOutLeft();
		IntTo2IntStage curS4 = (IntTo2IntStage) ((IntTo2IntStage) s
				.getOutRight()).getOutRight();
		IntTo2IntStage[] curSs = { curS1, curS2, curS3, curS4 };
		for (int count = 0; count < 97; count++)
		{
			for (int i = 0; i < curSs.length; i++)
			{
				curSs[i].setOutLeft(new IntTo2IntStage(null, null, false,
						false, null));
				curSs[i] = (IntTo2IntStage) curSs[i].getOutLeft();
			}
		}

		try
		{
			s.input(1);
			assert (s.getTaskCount() > 0);
		}
		catch (RejectedInputException ex)
		{
			ex.printStackTrace();
			assert (false);
		}

		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException ex)
		{
			ex.printStackTrace();
		}

		for (int i = 0; i < curSs.length; i++)
		{
			assert (curSs[i].getValue() == 100);
			assert (curSs[i].isPreprocessRuned());
			assert (curSs[i].getTaskCount() == 0);
		}

	}

	public void testStageFull()
	{
		IntTo2IntStage s = new IntTo2IntStage(new IntTo2IntStage(
				new IntTo2IntStage(null, null, true, false, null), null, false,
				false, null), null, false, false, null);

		for (int i = 0; i < 100; i++)
		{
			try
			{
				s.input(i);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
		}

		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException ex)
		{
			ex.printStackTrace();
		}

		assert (((IntTo2IntStage) ((IntTo2IntStage) s.getOutLeft())
				.getOutLeft()).getRejectedCount() == 0);
		assert (((IntTo2IntStage) s.getOutLeft()).getRejectedCount() == 96);
		assert (s.getRejectedCount() == 0);

		assert (s.getTaskCount() == 0);
		assert (((IntTo2IntStage) ((IntTo2IntStage) s.getOutLeft())
				.getOutLeft()).getTaskCount() == 4);
		assert (((IntTo2IntStage) s.getOutLeft()).getTaskCount() == 0);

	}

	// 你应该检查是否ExecutorStage.Worker.run()中正确地捕捉到process()抛出的错误
	public void testProcFail()
	{
		int assertionCount=Assertion.getCount();
		
		IntTo2IntStage s = new IntTo2IntStage(new IntTo2IntStage(
				new IntTo2IntStage(null, null, false, true, null), null, false,
				false, null), null, false, false, null);

		try
		{
			s.input(10);
		}
		catch (RejectedInputException ex)
		{
			ex.printStackTrace();
			assert (false);
		}

		assert (s.getRejectedCount() == 0);
		
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException ex)
		{
			ex.printStackTrace();
			assert(false);
		}
		
		assert(Assertion.getCount()==assertionCount+1);
	}

	// 你应该检查是否AbstractStage.output()中正确地捕捉到input()抛出的错误
	public void testInputFailureStage()
	{
		int assertionCount=Assertion.getCount();
		
		IntTo2IntStage s = new IntTo2IntStage(new IntTo2IntStage(
				new InputFailureStage(), null, false, false, null), null,
				false, false, null);

		try
		{
			s.input(10);
		}
		catch (RejectedInputException ex)
		{
			ex.printStackTrace();
			assert (false);
		}

		assert (s.getRejectedCount() == 0);
		
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException ex)
		{
			ex.printStackTrace();
			assert(false);
		}
		assert(assertionCount==Assertion.getCount()-1);
	}

	public void testInputRejectedStage()
	{
		IntTo2IntStage s = new IntTo2IntStage(new IntTo2IntStage(
				new InputRejectedStage(), null, false, false, null), null,
				false, false, null);

		try
		{
			s.input(10);
		}
		catch (RejectedInputException ex)
		{
			ex.printStackTrace();
			assert (false);
		}

		try
		{
			Thread.sleep(100);
		}
		catch (InterruptedException ex)
		{
			ex.printStackTrace();
		}

		assert (s.getRejectedCount() == 0);
		assert (((IntTo2IntStage) s.getOutLeft()).getRejectedCount() == 1);
		assert (s.getTaskCount() == 0);
		assert (((IntTo2IntStage) s.getOutLeft()).getTaskCount() == 0);
	}

	public void testInputImplFailureStage()
	{
		IntTo2IntStage s = new IntTo2IntStage(new IntTo2IntStage(
				new InputImplFailureStage(), null, false, false, null), null,
				false, false, null);

		try
		{
			s.input(10);
		}
		catch (RejectedInputException ex)
		{
			ex.printStackTrace();
			assert (false);
		}

		try
		{
			Thread.sleep(100);
		}
		catch (InterruptedException ex)
		{
			ex.printStackTrace();
		}

		assert (s.getRejectedCount() == 0);
		assert (((IntTo2IntStage) s.getOutLeft()).getRejectedCount() == 1);
	}

	public void testInputImplRejectedStage()
	{
		IntTo2IntStage s = new IntTo2IntStage(new IntTo2IntStage(
				new InputImplRejectedStage(), null, false, false, null), null,
				false, false, null);

		try
		{
			s.input(10);
		}
		catch (RejectedInputException ex)
		{
			ex.printStackTrace();
			assert (false);
		}

		try
		{
			Thread.sleep(100);
		}
		catch (InterruptedException ex)
		{
			ex.printStackTrace();
		}

		assert (s.getRejectedCount() == 0);
		assert (((IntTo2IntStage) s.getOutLeft()).getRejectedCount() == 1);
	}

	public void testPreprocFailure()
	{
		IntTo2IntStage s = new IntTo2IntStage(new IntTo2IntStage(
				new PreprocFailureStage(), null, false, false, null), null,
				false, false, null);

		try
		{
			s.input(10);
		}
		catch (RejectedInputException ex)
		{
			ex.printStackTrace();
			assert (false);
		}

		try
		{
			Thread.sleep(100);
		}
		catch (InterruptedException ex)
		{
			ex.printStackTrace();
		}

		assert (s.getRejectedCount() == 0);
		assert (((IntTo2IntStage) s.getOutLeft()).getRejectedCount() == 1);
	}

	public void testPreprocRetNullStage()
	{
		IntTo2IntStage s = new IntTo2IntStage(new IntTo2IntStage(
				new PreprocRetNullStage(), null, false, false, null), null,
				false, false, null);

		try
		{
			s.input(10);
		}
		catch (RejectedInputException ex)
		{
			ex.printStackTrace();
			assert (false);
		}

		try
		{
			Thread.sleep(100);
		}
		catch (InterruptedException ex)
		{
			ex.printStackTrace();
		}

		assert (s.getRejectedCount() == 0);
		assert (((IntTo2IntStage) s.getOutLeft()).getRejectedCount() == 0);
		assert (((PreprocRetNullStage) ((IntTo2IntStage) s.getOutLeft())
				.getOutLeft()).isPreprocessRuned());
		assert (!((PreprocRetNullStage) ((IntTo2IntStage) s.getOutLeft())
				.getOutLeft()).isProcessRuned());
	}

	public void testSchedulerStageSucc()
	{
		for (int i = 1; i <= 4; i++)
		{
			IntTo2IntStage s = new IntTo2IntStage(null, null, false, false,
					null);
			RunCountScheduleFailureHandler handler1 = new RunCountScheduleFailureHandler();
			ScheduleInput<Integer> input1 = new ScheduleInput<Integer>(
					(int) Math.random(), s, 2, 100, handler1);
			RunCountScheduleFailureHandler handler2 = new RunCountScheduleFailureHandler();
			ScheduleInput<Integer> input2 = new ScheduleInput<Integer>(
					(int) Math.random(), s, 10, 10, handler2);
			RunCountScheduleFailureHandler handler3 = new RunCountScheduleFailureHandler();
			ScheduleInput<Integer> input3 = new ScheduleInput<Integer>(
					(int) Math.random(), s, 100, 0, handler3);
			RunCountScheduleFailureHandler handler4 = new RunCountScheduleFailureHandler();
			ScheduleInput<Integer> input4 = new ScheduleInput<Integer>(
					(int) Math.random(), s, 100, 1, handler4);
			SimpleScheduleStage s2 = new SimpleScheduleStage(i);

			try
			{
				s2.input(input1);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
			try
			{
				s2.input(input2);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
			try
			{
				s2.input(input3);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
			try
			{
				s2.input(input4);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}

			assert (s.getRunCount() == 0);
			assert (s.getPreprocessCount() == 0);
			
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException ex)
			{
				ex.printStackTrace();
			}

			assert (input1.getRetry() == 100);
			assert (input2.getRetry() == 10);
			assert (input3.getRetry() == 0);
			assert (input4.getRetry() == 1);

			assert (s.getRunCount() == 4);
			assert (s.getPreprocessCount() == 4);

			assert (handler1.getRunCount() == 0);
			assert (handler2.getRunCount() == 0);
			assert (handler3.getRunCount() == 0);
			assert (handler4.getRunCount() == 0);
		}

	}

	public void testSchedulerStageFail()
	{
		for (int i = 1; i <= 4; i++)
		{
			IntTo2IntStage s = new IntTo2IntStage(null, null, true, false, null);
			try
			{
				s.input(1);
				s.input(2);
				s.input(3);
				s.input(4);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}

			RunCountScheduleFailureHandler handler1 = new RunCountScheduleFailureHandler();
			ScheduleInput<Integer> input1 = new ScheduleInput<Integer>(
					(int) Math.random(), s, 2, 100, handler1);
			RunCountScheduleFailureHandler handler2 = new RunCountScheduleFailureHandler();
			ScheduleInput<Integer> input2 = new ScheduleInput<Integer>(
					(int) Math.random(), s, 10, 10, handler2);
			RunCountScheduleFailureHandler handler3 = new RunCountScheduleFailureHandler();
			ScheduleInput<Integer> input3 = new ScheduleInput<Integer>(
					(int) Math.random(), s, 100, 0, handler3);
			RunCountScheduleFailureHandler handler4 = new RunCountScheduleFailureHandler();
			ScheduleInput<Integer> input4 = new ScheduleInput<Integer>(
					(int) Math.random(), s, 100, 1, handler4);
			SimpleScheduleStage s2 = new SimpleScheduleStage(i);

			try
			{
				s2.input(input1);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
			try
			{
				s2.input(input2);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
			try
			{
				s2.input(input3);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
			try
			{
				s2.input(input4);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}

			assert (s.getRunCount() == 2):s.getRunCount();
			assert (s.getPreprocessCount() == 4);
			
			try
			{
				Thread.sleep(1500);
			}
			catch (InterruptedException ex)
			{
				ex.printStackTrace();
			}

			assert (input1.getRetry() == 0);
			assert (input2.getRetry() == 0);
			assert (input3.getRetry() == 0);
			assert (input4.getRetry() == 0);

			assert (s.getRunCount() == 2);
			assert (s.getPreprocessCount() == 119);

			assert (handler1.getRunCount() == 1);
			assert (handler2.getRunCount() == 1);
			assert (handler3.getRunCount() == 1);
			assert (handler4.getRunCount() == 1);

		}
	}

	public void testScheduleStageSuccTaskCount()
	{
		for (int i = 1; i <= 4; i++)
		{
			IntTo2IntStage s = new IntTo2IntStage(null, null, false, false,
					null);
			RunCountScheduleFailureHandler handler1 = new RunCountScheduleFailureHandler();
			ScheduleInput<Integer> input1 = new ScheduleInput<Integer>(
					(int) Math.random(), s, 10, 100, handler1);
			RunCountScheduleFailureHandler handler2 = new RunCountScheduleFailureHandler();
			ScheduleInput<Integer> input2 = new ScheduleInput<Integer>(
					(int) Math.random(), s, 10, 10, handler2);
			RunCountScheduleFailureHandler handler3 = new RunCountScheduleFailureHandler();
			ScheduleInput<Integer> input3 = new ScheduleInput<Integer>(
					(int) Math.random(), s, 100, 0, handler3);
			RunCountScheduleFailureHandler handler4 = new RunCountScheduleFailureHandler();
			ScheduleInput<Integer> input4 = new ScheduleInput<Integer>(
					(int) Math.random(), s, 100, 1, handler4);
			SimpleScheduleStage s2 = new SimpleScheduleStage(i);

			try
			{
				s2.input(input1);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
			try
			{
				s2.input(input2);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
			try
			{
				s2.input(input3);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
			try
			{
				s2.input(input4);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}

			assert (s2.getTaskCount() == 4);

			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException ex)
			{
				ex.printStackTrace();
			}

			assert (input1.getRetry() == 100);
			assert (input2.getRetry() == 10);
			assert (input3.getRetry() == 0);
			assert (input4.getRetry() == 1);

			assert (s.getRunCount() == 4);
			assert (s.getPreprocessCount() == 4);

			assert (handler1.getRunCount() == 0);
			assert (handler2.getRunCount() == 0);
			assert (handler3.getRunCount() == 0);
			assert (handler4.getRunCount() == 0);

			assert (s2.getTaskCount() == 0);
		}

		for (int i = 1; i <= 4; i++)
		{
			IntTo2IntStage s = new IntTo2IntStage(null, null, true, false, null);
			try
			{
				s.input(1);
				s.input(2);
				s.input(3);
				s.input(4);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}

			RunCountScheduleFailureHandler handler1 = new RunCountScheduleFailureHandler();
			ScheduleInput<Integer> input1 = new ScheduleInput<Integer>(
					(int) Math.random(), s, 10, 100, handler1);
			RunCountScheduleFailureHandler handler2 = new RunCountScheduleFailureHandler();
			ScheduleInput<Integer> input2 = new ScheduleInput<Integer>(
					(int) Math.random(), s, 10, 10, handler2);
			RunCountScheduleFailureHandler handler3 = new RunCountScheduleFailureHandler();
			ScheduleInput<Integer> input3 = new ScheduleInput<Integer>(
					(int) Math.random(), s, 100, 0, handler3);
			RunCountScheduleFailureHandler handler4 = new RunCountScheduleFailureHandler();
			ScheduleInput<Integer> input4 = new ScheduleInput<Integer>(
					(int) Math.random(), s, 100, 1, handler4);
			final SimpleScheduleStage s2 = new SimpleScheduleStage(i);

			try
			{
				s2.input(input1);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
			try
			{
				s2.input(input2);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
			try
			{
				s2.input(input3);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
			try
			{
				s2.input(input4);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}

			assert (s2.getTaskCount() == 4);

			try
			{
				Thread.sleep(1500);
			}
			catch (InterruptedException ex)
			{
				ex.printStackTrace();
			}

			assert (input1.getRetry() == 0);
			assert (input2.getRetry() == 0);
			assert (input3.getRetry() == 0);
			assert (input4.getRetry() == 0);

			assert (s.getRunCount() == 2);
			assert (s.getPreprocessCount() == 119);

			assert (handler1.getRunCount() == 1);
			assert (handler2.getRunCount() == 1);
			assert (handler3.getRunCount() == 1);
			assert (handler4.getRunCount() == 1);

			assert (s2.getTaskCount() == 0);
		}
	}

	public void testConcurrentStageTaskCount()
	{
		TimedStage s = new TimedStage();

		for (int i = 0; i < 10; i++)
		{
			try
			{
				s.input(5);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
		}

		assert (s.getTaskCount() == 10);

		try
		{
			Thread.sleep(1100);
		}
		catch (InterruptedException ex)
		{
			ex.printStackTrace();
			assert (false);
		}

		assert (s.getTaskCount() == 0);
	}

	public static void main(String[] args) throws Exception
	{
		TestStage t = new TestStage();
		t.setUp();

		for (Method m : t.getClass().getMethods())
		{
			if (m.getName().startsWith("test"))
			{
				m.invoke(t);
			}
		}

		t.tearDown();
	}
}
