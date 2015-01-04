package org.accela.spider.stage.test;

import java.net.MalformedURLException;

import org.accela.spider.data.WebPage;
import org.accela.spider.stage.ConstructingStage;
import org.accela.spider.util.URL;
import org.accela.stage.RejectedInputException;
import org.accela.stage.Stage;

import junit.framework.TestCase;

public class TestConstructingStage extends TestCase
{
	public void testRejection() throws MalformedURLException, RejectedInputException
	{
		TesterAbortPolicy<URL, WebPage, String> p = new TesterAbortPolicy<URL, WebPage, String>();
		ConstructingStage s=new ConstructingStage(new RejectiveStage<WebPage>(), p);
		for(int i=0;i<100;i++)
		{
			s.input(new URL("http://www.sina.com.cn"));
		}
		
		assert(p.getInvokeCount()==100);
	}
	
	public void testPass() throws MalformedURLException
	{
		final URL url=new URL("http://www.sina.com.cn");
		TesterAbortPolicy<URL, WebPage, String> p = new TesterAbortPolicy<URL, WebPage, String>();
		ConstructingStage s=new ConstructingStage(new Stage<WebPage>(){
			@Override
			public int getTaskCount()
			{
				return 0;
			}

			@Override
			public void input(WebPage input) throws RejectedInputException
			{
				assert(input.getURL().equals(url));
			}
		}, p);
		for(int i=0;i<100;i++)
		{
			try
			{
				s.input(url);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert(false);
			}
		}
		
		assert(p.getInvokeCount()==0);
	}
}
