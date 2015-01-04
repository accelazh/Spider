package org.accela.spider.strategy.test;

import java.net.MalformedURLException;

import org.accela.spider.strategy.impl.HttpURLFilter;
import org.accela.spider.util.URL;

import junit.framework.TestCase;

public class TestHttpURLFilter extends TestCase
{
	public void testSimple() throws MalformedURLException
	{
		HttpURLFilter f=new HttpURLFilter();
		assert(f.accept(new URL("http://hello.com")));
		assert(f.accept(new URL("https://hello.com")));
		assert(!f.accept(new URL("ftp://hello.com")));
	}
}
