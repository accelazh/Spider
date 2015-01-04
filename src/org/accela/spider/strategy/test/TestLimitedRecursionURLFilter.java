package org.accela.spider.strategy.test;

import java.net.MalformedURLException;

import org.accela.spider.data.WebPage;
import org.accela.spider.strategy.Hyperlink;
import org.accela.spider.strategy.impl.LimitedRecursionHyperlinkFilter;
import org.accela.spider.strategy.impl.URLOnlyHyperlink;
import org.accela.spider.util.URL;

public class TestLimitedRecursionURLFilter
{
	public void testSimple() throws MalformedURLException
	{
		LimitedRecursionHyperlinkFilter f=new LimitedRecursionHyperlinkFilter(10);
		WebPage p=new WebPage(new URL("http://www.sina.com.cn"));
		assert(p.getRecursion()==0);
		Hyperlink l=new URLOnlyHyperlink(new URL("http://www.sina.com.cn"));
		
		assert(f.accept(p, p.getURL(), l, l.getURL()));
		
		p.setRecursion(5);
		assert(f.accept(p, p.getURL(), l, l.getURL()));
		
		p.setRecursion(11);
		assert(!f.accept(p, p.getURL(), l, l.getURL()));
	}
}
