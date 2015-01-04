package org.accela.spider.stage.test;

import java.net.MalformedURLException;

import org.accela.spider.data.WebPage;
import org.accela.spider.stage.NormalizationStage;
import org.accela.spider.strategy.impl.SimpleURLNormalizer;
import org.accela.spider.util.URL;
import org.accela.stage.RejectedInputException;
import org.accela.stage.Stage;

import junit.framework.TestCase;

public class TestNormalizationStage extends TestCase
{
	public void testRejection() throws MalformedURLException
	{
		TesterAbortPolicy<WebPage, WebPage, String> p = new TesterAbortPolicy<WebPage, WebPage, String>();
		NormalizationStage s=new NormalizationStage(new RejectiveStage<WebPage>(), new SimpleURLNormalizer(), p);
		for(int i=0;i<100;i++)
		{
			try
			{
				s.input(new WebPage(new URL("http://www.sina.com.cn")));
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert(false);
			}
		}
		
		assert(p.getInvokeCount()==100);
	}
	
	public void testNormalization() throws MalformedURLException
	{
		URL url=new URL("http://www.sina.com.cn/nice/../.././hello/good/index.htm?id=100&query=1000#fragment");
		TesterAbortPolicy<WebPage, WebPage, String> p = new TesterAbortPolicy<WebPage, WebPage, String>();
		NormalizationStage s=new NormalizationStage(new Stage<WebPage>(){
			@Override
			public int getTaskCount()
			{
				return 0;
			}

			@Override
			public void input(WebPage input) throws RejectedInputException
			{
				assert(input.getURL().toString().equals("http://sina.com.cn:80/hello/good?id=100&query=1000")):input.getURL().toString();
			}
			
		}, new SimpleURLNormalizer(), p);
		
		for(int i=0;i<100;i++)
		{
			try
			{
				s.input(new WebPage(url));
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
