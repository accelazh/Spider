package org.accela.spider.strategy.test;

import java.io.IOException;
import java.net.MalformedURLException;

import org.accela.spider.data.Content;
import org.accela.spider.strategy.impl.TextContentFetcher;
import org.accela.spider.util.ContentUnpreferedException;
import org.accela.spider.util.URL;

import junit.framework.TestCase;

public class TestTextContentFetcher extends TestCase
{
	//connect your computer to Internet first
	public void testSimple() throws MalformedURLException
	{
		TextContentFetcher f=new TextContentFetcher();
		
		Content c=null;
		try
		{
			c = f.fetchContent(new URL("http://www.sina.com.cn"));
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			assert(false);
		}
		assert(c.getText().length()>100);

		c=null;
		try
		{
			c = f.fetchContent(new URL("http://www.csdn.com.cn/"));
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			assert(false);
		}
		assert(c.getText().length()>100);
		
		c=null;
		try
		{
			c = f.fetchContent(new URL("http://download.winzip.com/winzip145.exe"));
			assert(false);
		}
		catch(ContentUnpreferedException ex)
		{
			//pass
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			assert(false);
		}
	}
}
