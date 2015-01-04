package org.accela.spider.stage.test;

import java.net.MalformedURLException;
import java.util.concurrent.Executors;

import org.accela.spider.data.WebPage;
import org.accela.spider.data.impl.TextContent;
import org.accela.spider.stage.AnalyzingStage;
import org.accela.spider.strategy.impl.EmptyAnalyzer;
import org.accela.spider.util.URL;
import org.accela.stage.RejectedInputException;

import junit.framework.TestCase;

public class TestAnalyzingStage extends TestCase
{
	public void testRejection() throws MalformedURLException, InterruptedException
	{
		TesterAbortPolicy<WebPage, WebPage, String> p = new TesterAbortPolicy<WebPage, WebPage, String>();
		AnalyzingStage s=new AnalyzingStage(new RejectiveStage<WebPage>(), new EmptyAnalyzer(), p, Executors.newCachedThreadPool());
		for(int i=0;i<100;i++)
		{
			try
			{
				WebPage page=new WebPage(new URL("http://www.sina.com.cn"));
				page.setContent(new TextContent(""));
				s.input(page);
				
				Thread.sleep(1);
				assert(page.getAnalysis()!=null);
			}
			catch (RejectedInputException ex)
			{
				ex.printStackTrace();
				assert(false);
			}
		}
		
		Thread.sleep(100);
		
		assert(p.getInvokeCount()==100):p.getInvokeCount();
	}
}
