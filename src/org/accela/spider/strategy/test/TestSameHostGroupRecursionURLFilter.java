package org.accela.spider.strategy.test;

import java.net.MalformedURLException;

import org.accela.spider.data.WebPage;
import org.accela.spider.strategy.Hyperlink;
import org.accela.spider.strategy.impl.URLOnlyHyperlink;
import org.accela.spider.strategy.impl.SameHostGroupRecursionHyperlinkFilter;
import org.accela.spider.util.URL;

import junit.framework.TestCase;

public class TestSameHostGroupRecursionURLFilter extends TestCase
{
	public void testSimple() throws MalformedURLException
	{
		SameHostGroupRecursionHyperlinkFilter f=new SameHostGroupRecursionHyperlinkFilter();
		
		WebPage p=new WebPage(new URL("http://www.sina.com.cn"));
		Hyperlink l1=new URLOnlyHyperlink(new URL("http://www.sina.com.cn/"));
		Hyperlink l2=new URLOnlyHyperlink(new URL("http://www.sina.com.cn/nice"));
		Hyperlink l3=new URLOnlyHyperlink(new URL("http://www.sina2.com.cn/"));
		Hyperlink l4=new URLOnlyHyperlink(new URL("http://www.sina2.com.cn/nice"));
		Hyperlink l5=new URLOnlyHyperlink(new URL("http://www.sina2.com.cn"));
		
		Hyperlink l6=new URLOnlyHyperlink(new URL("http://sina.com.cn"));
		Hyperlink l7=new URLOnlyHyperlink(new URL("http://sina.com/cn"));
		
		Hyperlink l8=new URLOnlyHyperlink(new URL("http://sports.sina.com.cn/"));
		Hyperlink l9=new URLOnlyHyperlink(new URL("http://sina.com.cn"));
		
		Hyperlink l10=new URLOnlyHyperlink(new URL("http://sports.sina.com.cn/"));
		Hyperlink l11=new URLOnlyHyperlink(new URL("http://www.sina.com.cn"));
		
		assert(f.accept(p, p.getURL(), l1, l1.getURL()));
		assert(f.accept(p, p.getURL(), l2, l2.getURL()));
		assert(!f.accept(p, p.getURL(), l3, l3.getURL()));
		assert(!f.accept(p, p.getURL(), l4, l4.getURL()));
		assert(!f.accept(p, p.getURL(), l5, l5.getURL()));
		
		assert(!f.accept(p, l6.getURL(), l7, l7.getURL()));
		
		assert(f.accept(p, l8.getURL(), l9, l9.getURL()));
		assert(f.accept(p, l9.getURL(), l8, l8.getURL()));
		
		assert(f.accept(p, l10.getURL(), l11, l11.getURL()));
		assert(f.accept(p, l11.getURL(), l10, l10.getURL()));
	}
}
