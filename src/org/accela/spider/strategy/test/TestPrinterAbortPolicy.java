package org.accela.spider.strategy.test;

import java.io.IOException;

import org.accela.spider.strategy.impl.PrinterAbortPolicy;
import org.accela.stage.RejectedInputException;
import org.accela.stage.Stage;

import junit.framework.TestCase;

public class TestPrinterAbortPolicy extends TestCase
{
	//need to be tested manually
	public void testSimple()
	{
		PrinterAbortPolicy<String, Integer, String> p=new PrinterAbortPolicy<String, Integer, String>();
		p.onAbort(false, new TesterStage<String>("hostStage"), new TesterStage<Integer>("nextStage"), "hello world", 99, "testing cause", new IOException("Exception message of hello world"));
		System.out.println("=========================================");
		p.onAbort(true, new TesterStage<String>("hostStage"), new TesterStage<Integer>("nextStage"), "hello world", 99, "testing cause", new IOException("Exception message of hello world"));
		System.err.println("=========================================");
		p.onAbort(false, null, null, null, null, null, null);
		System.out.println("=========================================");
	}
	
	private static class TesterStage<T> implements Stage<T>
	{
		public String name="";
		
		public TesterStage(String name)
		{
			this.name=name;
		}
		
		@Override
		public int getTaskCount()
		{
			return 0;
		}

		@Override
		public void input(T input) throws RejectedInputException
		{
			throw new RejectedInputException();
		}

		@Override
		public String toString()
		{
			return "TesterStage [name=" + name + "]";
		}
		
		

	}
}
