package org.accela.stage;

import java.util.concurrent.ExecutorService;

import org.accela.common.Assertion;

//This class represents a processing station, with one input port and 0~n output ports
//each output port can be connected to an other processing station. The processing station
//will use executor to each task sent to it by the input port asynchronously.
public abstract class ConcurrentStage<InputType, PreprocessedInputType> extends
		AbstractStage<InputType>
{
	private ExecutorService executor = null;

	public ConcurrentStage(ExecutorService executor)
	{
		if (null == executor)
		{
			throw new IllegalArgumentException("executor should not be null");
		}
		if (executor.isShutdown())
		{
			throw new IllegalArgumentException(
					"executor should not have been shut down");
		}

		this.executor = executor;
	}

	@Override
	protected void inputImpl(InputType input) throws Exception
	{
		assert (input != null) : Assertion.declare();

		PreprocessedInputType preprocessedInput = preprocess(input);

		if (preprocessedInput != null)
		{
			taskIn();
			try
			{
				executor.submit(new Worker(preprocessedInput));
			}
			catch(Exception ex)
			{
				taskOut();
				throw ex;
			}
		}
	}

	// Invoke immediately by the same thread invoking inputImpl.
	// The input object passing to process() should be returned,
	// and a null return indicates process() has no need to be
	// executed.Since process() requires queuing and possible thread creating
	// operation, which can be expensive, consider carefully what operation
	// should be put in preprocess() other than process()
	protected abstract PreprocessedInputType preprocess(InputType input)
			throws Exception;

	// Implement how you handle the input of InputType and send outputs
	// to one of more other Stages here. When you extends the class, you
	// can save output Stages bound to this Stage as members of the subclass.
	// Use output(OutputType output) throws RejectedOutputException method to
	// send the outputs. NEVER invoke directly Stage.input(...) of the output
	// Stage to send outputs. output(OutputType output) provides protection
	// if the references of output Stages you saved as members of the subclass
	// are accessed by more than one threads, since output(OutputType output)
	// method will copy the output reference and use it more than one time. If
	// subclass members of references to output Stages are changed during output
	// (OutputType output)'s invocation, it will guarantee all steps of its
	// processing be using the same old reference. There others reasons for
	// using
	// output(OutputType output), see AbstractStage.output(OutputType output).
	protected abstract void process(PreprocessedInputType input);

	public ExecutorService getExecutor()
	{
		return executor;
	}

	// =================================================================================

	private class Worker implements Runnable
	{
		private PreprocessedInputType input = null;

		public Worker(PreprocessedInputType input)
		{
			if (null == input)
			{
				throw new IllegalArgumentException("input should not be null");
			}

			this.input = input;
		}

		@Override
		public void run()
		{
			assert (input != null) : Assertion.declare();

			try
			{
				process(input);
			}
			catch (Exception ex)
			{
				System.err
						.println("ConcurrentStage.process(InputType) is not allow "
								+ "to throw an exception, which may cause thread leaking "
								+ "to the executor!");
				ex.printStackTrace();
				assert (false) : Assertion.declare();
			}
			finally
			{
				taskOut();	//ConcurrentStage's taskCount refers to its waiting queue size plus processing task count
			}
		}
	}
}
