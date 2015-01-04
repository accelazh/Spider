package org.accela.spider.strategy.test;

import java.net.MalformedURLException;

import org.accela.spider.data.WebPage;
import org.accela.spider.strategy.Hyperlink;
import org.accela.spider.strategy.impl.DuplicatedWithParentRecursionHyperlinkFilter;
import org.accela.spider.strategy.impl.URLOnlyHyperlink;
import org.accela.spider.util.URL;

import junit.framework.TestCase;

public class TestDuplicatedWithParentRecursionURLFilter extends TestCase
{
	public void testSimple() throws MalformedURLException
	{
		DuplicatedWithParentRecursionHyperlinkFilter f=new DuplicatedWithParentRecursionHyperlinkFilter();
		
		WebPage p=new WebPage(new URL("http://www.sina.com.cn/"));
		Hyperlink l1=new URLOnlyHyperlink(new URL("http://www.sina.com.cn/"));
		Hyperlink l2=new URLOnlyHyperlink(new URL("http://www.sina.com.cn/nice"));
		
		assert(!f.accept(p, p.getURL(), l1, l1.getURL()));
		assert(f.accept(p, p.getURL(), l2, l2.getURL()));
	}
}
