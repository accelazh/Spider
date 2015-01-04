package org.accela.spider.stage.test;

import java.util.concurrent.atomic.AtomicInteger;

import org.accela.spider.data.WebPage;
import org.accela.spider.event.SpiderListener;
import org.accela.spider.util.URL;

public class TesterSpiderListener implements SpiderListener
{
	private AtomicInteger bCount = new AtomicInteger(0);
	private AtomicInteger eCount = new AtomicInteger(0);

	@Override
	public void onBegin(URL url, int recursion)
	{
		bCount.incrementAndGet();
	}

	@Override
	public void onEnd(WebPage page)
	{
		eCount.incrementAndGet();
	}

	public int getbCount()
	{
		return bCount.get();
	}

	public int geteCount()
	{
		return eCount.get();
	}

}
