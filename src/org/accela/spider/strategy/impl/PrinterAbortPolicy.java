package org.accela.spider.strategy.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.accela.common.Assertion;
import org.accela.spider.strategy.AbortPolicy;
import org.accela.stage.Stage;

public class PrinterAbortPolicy<InputType, OutputType, CauseType> implements
		AbortPolicy<InputType, OutputType, CauseType>
{
	@Override
	public void onAbort(boolean causedByError,
			Stage<InputType> hostStage,
			Stage<OutputType> nextStage,
			InputType input,
			OutputType output,
			CauseType cause,
			Exception ex)
	{
		PrintStream out = causedByError ? System.err : System.out;

		out.println("A task is aborted. ");
		if (hostStage != null)
		{
			out.println("\thostStage: " + hostStage);
		}
		if (nextStage != null)
		{
			out.println("\tnextStage: " + nextStage);
		}
		if (input != null)
		{
			out.println("\tinput: " + input);
		}
		if (input != null)
		{
			out.println("\toutput: " + output);
		}
		if (cause != null)
		{
			out.println("\tcause: " + cause);
		}
		if (ex != null)
		{
			out.println("\texception: ");
			out.println(getExceptionStactTraceWithIndent(ex, "\t"));
		}
	}

	private String getExceptionStactTraceWithIndent(Exception ex, String indent)
	{
		StringBuffer out = new StringBuffer();

		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ex.printStackTrace(new PrintStream(byteOut));

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new ByteArrayInputStream(byteOut.toByteArray())));
		String line = null;
		try
		{
			while ((line = reader.readLine()) != null)
			{
				out.append(indent + line);
				out.append('\n');
			}
			out.deleteCharAt(out.length()-1);
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
			assert (false) : Assertion.declare();
		}
		finally
		{
			try
			{
				byteOut.close();
				reader.close();
			}
			catch (IOException exception)
			{
				exception.printStackTrace();
				assert (false) : Assertion.declare();
			}
		}

		return out.toString();
	}

}
