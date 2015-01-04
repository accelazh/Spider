package org.accela.spider.strategy.test;

import java.net.MalformedURLException;

import org.accela.spider.data.WebPage;
import org.accela.spider.strategy.Hyperlink;
import org.accela.spider.strategy.RecursionHyperlinkFilter;
import org.accela.spider.strategy.RecursionHyperlinkFilterGroup;
import org.accela.spider.strategy.impl.URLOnlyHyperlink;
import org.accela.spider.util.URL;

import junit.framework.TestCase;

public class TestRecursionHyperlinkFilterGroup extends TestCase
{
	public void testSimple() throws MalformedURLException
	{
		WebPage p=new WebPage(new URL("http://www.sina.com.cn"));
		Hyperlink l=new URLOnlyHyperlink(new URL("http://www.sina.com.cn/"));
		
		RecursionHyperlinkFilterGroup g=new RecursionHyperlinkFilterGroup(new RecursionHyperlinkFilter[0]);
		assert(g.accept(p, p.getURL(), l, l.getURL()));
		
		g=new RecursionHyperlinkFilterGroup(new RecursionHyperlinkFilter[]{new TesterFilter(true), new TesterFilter(true),new TesterFilter(true)});
		assert(g.accept(p, p.getURL(), l, l.getURL()));
		
		g=new RecursionHyperlinkFilterGroup(new RecursionHyperlinkFilter[]{new TesterFilter(true), new TesterFilter(true),new TesterFilter(false)});
		assert(!g.accept(p, p.getURL(), l, l.getURL()));
	}
	
	private static class TesterFilter implements RecursionHyperlinkFilter
	{
		public boolean value = false;

		public TesterFilter(boolean value)
		{
			this.value = value;
		}

		@Override
		public boolean accept(WebPage parent,
				URL normalizedParentURL,
				Hyperlink link,
				URL normalizedLinkURL)
		{
			return value;
		}

	}
}
