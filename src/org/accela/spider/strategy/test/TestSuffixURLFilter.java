package org.accela.spider.strategy.test;

import java.net.MalformedURLException;

import org.accela.spider.strategy.impl.SuffixURLFilter;
import org.accela.spider.util.URL;

import junit.framework.TestCase;

public class TestSuffixURLFilter extends TestCase
{
	public void testSimple() throws MalformedURLException
	{
		SuffixURLFilter f=new SuffixURLFilter(new String[]{"abc", "d"});
		
		assert(!f.accept(new URL("http://www.sina.com.cn")));
		assert(!f.accept(new URL("http://www.sina.com.cn/")));
		assert(!f.accept(new URL("http://www.sina.com.cn/abc/")));
		assert(!f.accept(new URL("http://www.sina.com.cn/abc/abc")));
		assert(f.accept(new URL("http://www.sina.com.cn/abc/abc.abc")));
		assert(f.accept(new URL("http://www.sina.com.cn/abc/abc.d")));
		
		f=new SuffixURLFilter(new String[]{"abc", "d", ""});
		
		assert(f.accept(new URL("http://www.sina.com.cn")));
		assert(f.accept(new URL("http://www.sina.com.cn/")));
		assert(f.accept(new URL("http://www.sina.com.cn/abc/")));
		assert(f.accept(new URL("http://www.sina.com.cn/abc/abc")));
		assert(f.accept(new URL("http://www.sina.com.cn/abc/abc.abc")));
		assert(f.accept(new URL("http://www.sina.com.cn/abc/abc.d")));
		assert(f.accept(new URL("http://www.sina.com.cn/abc/abc.")));
		assert(f.accept(new URL("http://www.sina.com.cn/abc/abc.  ")));
	}
}
