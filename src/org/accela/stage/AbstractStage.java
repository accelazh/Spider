package org.accela.stage;

import java.util.concurrent.atomic.AtomicInteger;

import org.accela.common.Assertion;

public abstract class AbstractStage<InputType> implements Stage<InputType>
{
	private AtomicInteger taskCount = new AtomicInteger(0);

	@Override
	public void input(InputType input) throws RejectedInputException
	{
		if (null == input)
		{
			throw new IllegalArgumentException("input should not be null");
		}

		try
		{
			assert (input != null) : Assertion.declare();
			inputImpl(input);
		}
		catch (Exception ex)
		{
			throw new RejectedInputException(ex);
		}
	}

	protected abstract void inputImpl(InputType input) throws Exception;

	// when you are implementation subclass of AbstractStage, use this method
	// instead of calling directly out.input(output). This method provides extra
	// protection and handle the exception thrown. We may add mechanics such as
	// hooked failure handlers in the future, which will rely on this method. If
	// you don't use this method to send outputs to other stage, these mechanics
	// may not work correctly.
	protected <OutputType> void output(Stage<OutputType> out, OutputType output)
			throws RejectedOutputException
	{
		if (null == out)
		{
			throw new IllegalArgumentException("out should not be null");
		}
		if (null == output)
		{
			throw new IllegalArgumentException("output should not be null");
		}
		try
		{

			out.input(output);
		}
		catch (RejectedInputException ex)
		{
			// It is not easy to apply rejection handler mechanics to this
			// class,
			// since there may be more than one output stages and various types
			// of
			// object can be output to them. In the handler's handle method:
			// handle(Stage<InputType> in, Stage<OutputType> out, InputType in,
			// OutputType out),
			// we can't figure out a appropriate OutputType to use.
			throw new RejectedOutputException(ex);
		}
		catch (Exception ex)
		{
			System.err
					.println("Stage.input(InputType) throws an exception other than "
							+ "RejectedInputException, which should be caused by logic error "
							+ "in your code. Fix it!");
			ex.printStackTrace();
			assert (false) : Assertion.declare();
		}
	}

	@Override
	public int getTaskCount()
	{
		return taskCount.get();
	}

	//subclasses is in response to manage taskCount using taskIn() and taskOut()
	protected void taskIn()
	{
		taskCount.incrementAndGet();
	}

	protected void taskOut()
	{
		taskCount.decrementAndGet();
		assert (taskCount.get() >= 0) : Assertion.declare();
	}
}
