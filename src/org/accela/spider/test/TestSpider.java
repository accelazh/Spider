package org.accela.spider.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.accela.spider.Spider;
import org.accela.spider.SpiderAttributes;
import org.accela.spider.SpiderStageName;
import org.accela.spider.data.WebPage;
import org.accela.spider.data.WebPageStore;
import org.accela.spider.data.WebPageStoreException;
import org.accela.spider.data.impl.FileWebPageStore;
import org.accela.spider.util.URL;

import junit.framework.TestCase;

public class TestSpider extends TestCase
{
	public void testSimple() throws MalformedURLException,
			FileNotFoundException
	{
		final AtomicInteger count = new AtomicInteger(0);

		final AtomicLong startTime = new AtomicLong(System.currentTimeMillis());

		WebPageStore store = new WebPageStore()
		{
			private FileWebPageStore fileStore=new FileWebPageStore(new File("download"));
			
			@Override
			public boolean contains(URL url)
			{
				return fileStore.contains(url);
			}

			@Override
			public long getStamp(URL url)
			{
				return fileStore.getStamp(url);
			}

			@Override
			public void put(WebPage page) throws WebPageStoreException
			{
				System.out.println(page.getURL());
				count.incrementAndGet();

				fileStore.put(page);
			}

		};
		final Spider spider = new Spider(store);
		SpiderAttributes attr = new SpiderAttributes(spider);

		attr.setAnalyzingStageAbortPolicy(null);
		attr.setBeginningListenerStageAbortPolicy(null);
		attr.setConstructingStageAbortPolicy(null);
		//attr.setDeliveringStageAbortPolicy(null);
		attr.setEndingListenerStageAbortPolicy(null);
		attr.setExtractingHyperlinkStageAbortPolicy(null);
		attr.setFetchingContentStageAbortPolicy(null);
		attr.setFilterStageAbortPolicy(null);
		attr.setNormalizationStageAbortPolicy(null);
		attr.setPrefilterStageAbortPolicy(null);
		attr.setRecursionStageAbortPolicy(null);
		attr.setSchedulingStageAbortPolicy(null);
		attr.setStampingStageAbortPolicy(null);

		System.setOut(new PrintStream("SysOut.txt"));
		System.setErr(new PrintStream("SysErr.txt"));

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				while (true)
				{
					StringBuffer out = new StringBuffer();
					for (SpiderStageName n : SpiderStageName.values())
					{
						out.append(n.name()
								+ ": "
								+ spider.getLoadMonitor(n).getTaskCount()
								+ "\t\t");
					}
					out.append("All minus Scheduling: "
							+ (spider.getLoadMonitor(SpiderStageName.All)
									.getTaskCount() - spider
									.getLoadMonitor(SpiderStageName.Scheduling)
									.getTaskCount())
							+ "\t\t");
					out.append("Finished: " + count.get() + "\t\t");
					out.append("Elapse: "
							+ (long) ((System.currentTimeMillis() - startTime
									.get()) / 1000)
							+ " sec"
							+ "\t\t");

					System.out.println(out);

					try
					{
						Thread.sleep(1000);
					}
					catch (InterruptedException ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}).start();

		spider.initialize(attr);

		spider.input(new URL("http://www.sina.com.cn"));
		
		//spider.shutdown();
	}

	public static void main(String[] args) throws MalformedURLException,
			FileNotFoundException
	{
		TestSpider t = new TestSpider();
		t.testSimple();
	}

}
