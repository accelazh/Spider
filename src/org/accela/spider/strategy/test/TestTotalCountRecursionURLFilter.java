package org.accela.spider.strategy.test;

import java.net.MalformedURLException;

import org.accela.spider.data.WebPage;
import org.accela.spider.strategy.Hyperlink;
import org.accela.spider.strategy.impl.URLOnlyHyperlink;
import org.accela.spider.strategy.impl.TotalCountRecursionHyperlinkFilter;
import org.accela.spider.util.URL;

import junit.framework.TestCase;

public class TestTotalCountRecursionURLFilter extends TestCase
{
	public void testSimple() throws MalformedURLException
	{
		WebPage p = new WebPage(new URL("http://www.sina.com.cn"));
		Hyperlink l = new URLOnlyHyperlink(new URL("http://www.sina.com.cn/"));
		TotalCountRecursionHyperlinkFilter f = new TotalCountRecursionHyperlinkFilter(100);
		
		for (int i = 0; i < 100; i++)
		{
			assert(f.accept(p, p.getURL(), l, l.getURL()));
			assert(f.getCurCount()==i+1);
			assert(f.getTotalCount()==100);
		}
		for (int i = 0; i < 100; i++)
		{
			assert(!f.accept(p, p.getURL(), l, l.getURL()));
			assert(f.getCurCount()==100);
			assert(f.getTotalCount()==100);
		}
		
		f.reset();
		
		for (int i = 0; i < 100; i++)
		{
			assert(f.accept(p, p.getURL(), l, l.getURL()));
			assert(f.getCurCount()==i+1);
			assert(f.getTotalCount()==100);
		}
		for (int i = 0; i < 100; i++)
		{
			assert(!f.accept(p, p.getURL(), l, l.getURL()));
			assert(f.getCurCount()==100);
			assert(f.getTotalCount()==100);
		}
		
	}
}
