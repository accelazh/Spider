package org.accela.spider.util.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.accela.spider.util.ContentUnpreferedException;
import org.accela.spider.util.URL;
import org.accela.spider.util.URLTextDownloader;

import junit.framework.TestCase;

public class TestURLTextDownloader extends TestCase
{
	// connection to Internet presumed
	public void testSimple() throws MalformedURLException, IOException
	{
		URLTextDownloader downloader = new URLTextDownloader();
		String txt = downloader.download(new URL("http://www.sina.com.cn"));
		assert (txt.length() > 1000);
		txt = downloader.download(new URL("http://www.google.com/robots.txt"));
		assert (txt.length() > 1000);
	}

	// connection to Internet presumed
	public void testPreferedType() throws MalformedURLException, IOException
	{
		URLTextDownloader downloader = new URLTextDownloader();

		String txt = null;
		try
		{
			txt = downloader.download(new URL("http://www.sina.com.cn"),
					"text/html");
		}
		catch (ContentUnpreferedException ex)
		{
			assert (false);
		}
		assert (txt.length() > 1000);
		
		txt=null;
		try
		{
			txt = downloader.download(new URL("http://www.google.com/robots.txt"),
					"text/html");
			assert(false);
		}
		catch (ContentUnpreferedException ex)
		{
			//pass
		}
	}
	
	// connection to Internet presumed
	public void testPrecision() throws MalformedURLException, IOException
	{
		URLTextDownloader downloader = new URLTextDownloader();
		String txt1 = downloader.download(new URL("http://www.sina.com.cn"));
		String txt2 = downloader.download(new URL("http://www.sina.com.cn"), "text/html");
		
		StringBuffer txt3Buf=new StringBuffer();
		BufferedReader in=new BufferedReader(new InputStreamReader(new URL("http://www.sina.com.cn").openConnection().getInputStream()));
		int c=0;
		while((c=in.read())!=-1)
		{
			txt3Buf.append((char)c);
		}
		in.close();
		String txt3=txt3Buf.toString();
		
		assert(txt1.equals(txt2));
		assert(txt2.equals(txt3));
	}
	
	//network connection presumed
	public void testInterruption() throws InterruptedException
	{
		final URLTextDownloader downloader = new URLTextDownloader();
		final AtomicBoolean failed=new AtomicBoolean(false);
		final AtomicInteger count=new AtomicInteger(0);
		
		Thread t=new Thread(new Runnable(){
			@Override
			public void run()
			{
				try
				{
					downloader.download(new URL("http://www.sina.com.cn"));
					
					failed.set(true);
					assert(false);
				}
				catch (MalformedURLException ex)
				{
					ex.printStackTrace();
					failed.set(true);
					assert(false);
				}
				catch (InterruptedIOException ex)
				{
					count.incrementAndGet();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
					failed.set(true);
					assert(false);
				}
			}
		});
		t.start();

		t.interrupt();
		
		Thread.sleep(100);
		
		assert(count.get()==1);
		assert(!failed.get());
	}
	
	public void testCharset()
	{
		//test manually
	}

}
