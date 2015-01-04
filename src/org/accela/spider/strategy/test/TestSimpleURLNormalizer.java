package org.accela.spider.strategy.test;

import java.net.MalformedURLException;

import org.accela.spider.strategy.impl.SimpleURLNormalizer;
import org.accela.spider.util.URL;

import junit.framework.TestCase;

public class TestSimpleURLNormalizer extends TestCase
{
	public void testTruncateQuery() throws MalformedURLException
	{
		SimpleURLNormalizer n = new SimpleURLNormalizer(true);

		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./index.htm.asp?id=10&query=100#fragment"))
				.equals(new URL("http://sina.com:80/hello/nice")));
		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./index.htm.asp?id=10&query=100#fragment"))
				.equals(new URL("http://sina.com/hello/nice")));
		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./index.htm.asp?id=10&query=100#fragment"))
				.toString().equals("http://sina.com:80/hello/nice"));

		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./default.ttt?id=10&query=100#fragment"))
				.equals(new URL("http://sina.com:80/hello/nice")));
		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./default.ttt.htm.asp?id=10&query=100#fragment"))
				.equals(new URL("http://sina.com/hello/nice")));
		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./default.ttt.htm.asp?id=10&query=100#fragment"))
				.toString().equals("http://sina.com:80/hello/nice"));

		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./?id=10&query=100#fragment"))
				.toString().equals("http://sina.com:80/hello/nice"));

		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./badman?id=10&query=100#fragment"))
				.toString().equals("http://sina.com:80/hello/nice/badman"));

		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM:1234/../.././hello/good/abc/../.././nice/./badman?id=10&query=100#fragment"))
				.toString().equals("http://sina.com:1234/hello/nice/badman"));

		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./index.htm.asp?id=10&query=100#fragment"))
				.equals(n.normalize(new URL("http://sina.com:80/hello/nice"))));
		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./index.htm.asp?id=10&query=100#fragment"))
				.equals(n.normalize(new URL("http://sina.com/hello/nice"))));
		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./index.htm.asp?id=10&query=100#fragment"))
				.toString().equals(n.normalize(new URL(
				"http://sina.com:80/hello/nice/")).toString()));

		assert (n.normalize(new URL("http://www.sina.com.cn")).toString()
				.equals("http://sina.com.cn:80/"));
		assert (n.normalize(new URL("http://www.sina.com.cn")).equals(new URL(
				"http://sina.com.cn:80/")));
		assert (n.normalize(new URL("http://www.sina.com.cn")).equals(n
				.normalize(new URL("http://sina.com.cn:80/"))));

		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./index?id=10&query=100#fragment"))
				.toString().equals("http://sina.com:80/hello/nice"));

		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./index.asp?id=10&query=100#fragment"))
				.toString().equals("http://sina.com:80/hello/nice"));

		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./index"))
				.toString().equals("http://sina.com:80/hello/nice"));

		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./default"))
				.toString().equals("http://sina.com:80/hello/nice"));

		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./default2"))
				.toString().equals("http://sina.com:80/hello/nice/default2"));

		assert (n.normalize(new URL("http://www.sina.com.cn")).toString()
				.equals("http://sina.com.cn:80/"));

	}
	
	public void testNotTruncateQuery() throws MalformedURLException
	{
		SimpleURLNormalizer n = new SimpleURLNormalizer(false);

		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./index.htm.asp?search=1000:id=10&query=100#fragment"))
				.equals(new URL("http://sina.com:80/hello/nice?id=10&query=100&search=1000")));
		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./index.htm.asp?id=10&search= 1000 &query=100#fragment"))
				.equals(new URL("http://sina.com/hello/nice?id=10&query=100&search=1000")));
		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./index.htm.asp?id=10&query=100&search=1000#fragment"))
				.toString().equals("http://sina.com:80/hello/nice?id=10&query=100&search=1000"));

		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./default.ttt?search=?1000&id=10&query=100#fragment adsf"))
				.equals(new URL("http://sina.com:80/hello/nice?id=10&query=100&search=?1000")));
		
		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./default.ttt.htm.asp?id=10&query=100&search=1000&id=100&query=100#fragment"))
				.equals(new URL("http://sina.com/hello/nice?id=10&query=100&search=1000")));
		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./default.ttt.htm.asp?id=10:search=1000&query=100#fragment"))
				.toString().equals("http://sina.com:80/hello/nice?id=10&query=100&search=1000"));

		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./?id=10&query=100#fragment"))
				.toString().equals("http://sina.com:80/hello/nice?id=10&query=100"));

		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./badman?id=10&query=100#fragment"))
				.toString().equals("http://sina.com:80/hello/nice/badman?id=10&query=100"));

		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM:1234/../.././hello/good/abc/../.././nice/./badman?id=10&query=100#fragment"))
				.toString().equals("http://sina.com:1234/hello/nice/badman?id=10&query=100"));

		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./index.htm.asp?id=10&query=100#fragment"))
				.equals(n.normalize(new URL("http://sina.com:80/hello/nice?id=10&query=100"))));
		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./index.htm.asp?search=10&search=100&id=10&query=100#fragment"))
				.equals(n.normalize(new URL("http://sina.com/hello/nice?id=10&query=100&search=10"))));
		
		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./index.htm.asp?search=10&search=100&id=10&query=100#fragment"))
				.toString().equals("http://sina.com:80/hello/nice?id=10&query=100&search=10"));
		
		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./index.htm.asp?id=10&query=100:a=1:a=2:a=3:b=4:b=5&100=c&700=c#fragment"))
				.toString().equals(n.normalize(new URL(
				"http://sina.com:80/hello/nice/?100=c&700=c&a=1&b=4&id=10&query=100")).toString()));
		
		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./index.htm.asp?id=10&query=100:a=1:a=2:a=3:b=4:b=5&100=c&700=c#fragment"))
				.toString().equals(
				"http://sina.com:80/hello/nice?100=c&700=c&a=1&b=4&id=10&query=100"));

		assert (n.normalize(new URL("http://www.sina.com.cn")).toString()
				.equals("http://sina.com.cn:80/"));
		assert (n.normalize(new URL("http://www.sina.com.cn")).equals(new URL(
				"http://sina.com.cn:80/")));
		assert (n.normalize(new URL("http://www.sina.com.cn")).equals(n
				.normalize(new URL("http://sina.com.cn:80/"))));

		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./index?id=10&query=100a&query=1000a#fragment"))
				.toString().equals("http://sina.com:80/hello/nice?id=10&query=100a"));

		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./index.asp?id=10&query=100a&query=1000a#fragment"))
				.toString().equals("http://sina.com:80/hello/nice?id=10&query=100a"));

		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./index?search=10&search=100:id=500&id=10:query=100a:query=1000a#fragment"))
				.toString().equals("http://sina.com:80/hello/nice?id=500&query=100a&search=10"));

		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./default?search=10&search=100:id=500&id=10:query=100a:query=1000a#fragment"))
				.toString().equals("http://sina.com:80/hello/nice?id=500&query=100a&search=10"));

		assert (n
				.normalize(new URL(
						"hTtP://WwW.SinA.coM/../.././hello/good/abc/../.././nice/./default2?search=10&search=100:id=500&id=10:query=100a:query=1000a#fragment"))
				.toString().equals("http://sina.com:80/hello/nice/default2?id=500&query=100a&search=10"));

		assert (n.normalize(new URL("http://www.sina.com.cn")).toString()
				.equals("http://sina.com.cn:80/"));

	}
}
