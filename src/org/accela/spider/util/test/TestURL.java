package org.accela.spider.util.test;

import java.net.MalformedURLException;

import org.accela.spider.util.URL;

import junit.framework.TestCase;

public class TestURL extends TestCase
{
	public void testEmptyURL() throws MalformedURLException
	{
		URL url = new URL("http://www.sina.com.cn/");
		assert (url.getPath().equals("/"));

		url = new URL("http://www.sina.com.cn");
		assert (url.getPath().equals("/"));

		url = new URL("http://www.sina.com.cn/nice");
		assert (url.getPath().equals("/nice"));

		try
		{
			url = new URL("http://www.sina.com.cn\\nice");
			assert (false);
		}
		catch (Exception ex)
		{
			assert (ex instanceof MalformedURLException);
		}
		
		try
		{
			url=new URL("http://");
			assert (false);
		}
		catch (Exception ex)
		{
			assert (ex instanceof MalformedURLException);
		}
		
		try
		{
			url=new URL("http:///");
			assert (false);
		}
		catch (Exception ex)
		{
			assert (ex instanceof MalformedURLException);
		}
		
		try
		{
			url=new URL("  http://  ");
			assert (false);
		}
		catch (Exception ex)
		{
			assert (ex instanceof MalformedURLException);
		}
		
		try
		{
			url=new URL("www.sina.com.cn");
			assert (false);
		}
		catch (Exception ex)
		{
			assert (ex instanceof MalformedURLException);
		}
		
	}

	public void testMalformedURL()
	{
		String[] protocols = new String[] {
				"  http  ",
				null,
				"",
				"  ",
				"'http", };
		String[] hosts = new String[] {
				"  9www.si-na.com.cn  ",
				null,
				"www.si_na.com.cn",
				"www.si na.com.cn",
				".www.sina.com.cn",
				"www.sina.com.cn.",
				"-www.sina.com.cn",
				"www.sina.com.cn-",
				"www..sina.com.cn",
				"sina",
				"www.-sina.com.cn",
				"  " };
		int[] ports = new int[] { 0, -1, -80, };
		String[] paths = new String[] {
				"",
				"  ",
				"  /  ",
				"/",
				"/ho\\me",
				"/ho:me",
				"/ho*me",
				"/ho?me",
				"/ho\"me",
				"/ho<me",
				"/ho>me",
				"/ho|me",
				"/home\ndf", };
		String[] queries = new String[] {
				" id=.*|<>\"?  \\/&& search = 100 ",
				"#id=8&search=500",
				"id8&search500",
				"df\nid=8&search=500", };
		String[] refs = new String[] {
				" #sharpid.*|<>\"?  \\/ ",
				"d \r d",
				"aljsdflsadf\bdf",
				"df\nid=8&search=500", };

		for (int i_p = 0; i_p < protocols.length; i_p++)
		{
			for (int i_h = 0; i_h < hosts.length; i_h++)
			{
				for (int i_pa = 0; i_pa < paths.length; i_pa++)
				{
					for (int i_po = 0; i_po < ports.length; i_po++)
					{
						for (int i_q = 0; i_q < queries.length; i_q++)
						{
							for (int i_r = 0; i_r < refs.length; i_r++)
							{
								if (0 == i_p
										&& 0 == i_h
										&& (i_pa <= 3)
										&& 0 == i_po
										&& 0 == i_q
										&& 0 == i_r)
								{
									URL url = null;
									try
									{
										url = new URL(protocols[i_p],
												hosts[i_h], ports[i_po],
												paths[i_pa], queries[i_q],
												refs[i_r]);
									}
									catch (Exception ex)
									{
										ex.printStackTrace();
										assert (false);
									}
									assert (url.toString()
											.equals("http://9www.si-na.com.cn:0/?id=.*|<>\"?  \\/&search=100##sharpid.*|<>\"?  \\/"));
								}
								else
								{
									try
									{
										new URL(protocols[i_p], hosts[i_h],
												ports[i_po], paths[i_pa],
												queries[i_q], refs[i_r]);
										assert (false) : i_p
												+ ", "
												+ i_h
												+ ", "
												+ i_po
												+ ", "
												+ i_pa
												+ ", "
												+ i_q
												+ ", "
												+ i_r;
									}
									catch (Exception ex)
									{
										assert (ex instanceof MalformedURLException);
									}
								}
							}
						}
					}
				}
			}
		}

		try
		{
			new URL("http://");
			assert (false);
		}
		catch (Exception ex)
		{
			assert (ex instanceof MalformedURLException);
		}

	}// end of function

	public void testEmptyQueryAndRefToNull() throws MalformedURLException
	{
		assert (new URL("http", "www.sina.com.cn", 81, "/path", null, null)
				.toString().equals("http://sina.com.cn:81/path"));
		assert (new URL("http", "www.sina.com.cn", "/path", null, null)
				.toString().equals("http://sina.com.cn:80/path"));

		assert (new URL("http", "www.sina.com.cn", 81, "/path", "\n ", " ")
				.toString().equals("http://sina.com.cn:81/path"));
		assert (new URL("http", "www.sina.com.cn", "/path", "  ", "\r")
				.toString().equals("http://sina.com.cn:80/path"));

		assert (new URL("http://www.sina.com.cn:81/path").toString()
				.equals("http://sina.com.cn:81/path"));
		assert (new URL("http://www.sina.com.cn/path").toString()
				.equals("http://sina.com.cn:80/path"));

		assert (new URL("http://www.sina.com.cn:81/path?  #  ").toString()
				.equals("http://sina.com.cn:81/path"));
		assert (new URL("http://www.sina.com.cn/path?id=# \n\r\f ").toString()
				.equals("http://sina.com.cn:80/path"));

		assert (new URL("http://www.sina.com.cn:81/path?  #  ").getQuery() == null);
		assert (new URL("http://www.sina.com.cn:81/path?  #  ").getRef() == null);
		assert (new URL("http://www.sina.com.cn/path?id=# \n\r\f ").getQuery() == null);
		assert (new URL("http://www.sina.com.cn/path?id=# \n\r\f ").getRef() == null);
	}

	public void testContextConstructor() throws MalformedURLException
	{
		assert (new URL(new URL("http://www.sina.com.cn/hello/world/"),
				"/bad/man").toString().equals("http://sina.com.cn:80/bad/man"));
		assert (new URL(new URL("http://www.sina.com.cn/hello/world/"),
				"bad/man").toString()
				.equals("http://sina.com.cn:80/hello/bad/man"));
	}

	public void testNormalize() throws MalformedURLException
	{
		URL url = new URL(
				" hTtP ",
				"  WwW.SinA-Nice.com.cn  \n",
				100,
				"hello/../.. / .. / .. / nice /world/././../ I / aM / not / good/ ... / index.html/",
				"   search = ?article & ?id = 500 : jkl = \"*250?\"\n",
				" \r\t#nice world# ");
		assert (url.toString()
				.equals("http://sina-nice.com.cn:100/nice/I/aM/not/good/.../index.html??id=500&jkl=\"*250?\"&search=?article##nice world#"));

		url = new URL(
				" hTtP ",
				"  WwW.SinA-Nice.com.cn  \n",
				100,
				"hello/../.. / .. / .. / nice /world/././../../../ I / aM / not / good/ ... //../../../../ index.html/../..",
				"search = ?article & ?id = 500 : jkl = \"*250?\"\n",
				" \r\t#nice world# ");
		assert (url.toString()
				.equals("http://sina-nice.com.cn:100/??id=500&jkl=\"*250?\"&search=?article##nice world#"));

		url = new URL(" hTtP ", "  WwW.SinA-Nice.com.cn  \n", 0, "",
				"search = ?article & ?id = 500 : jkl = \"*250?\"\n",
				" \r\t#nice world# ");
		assert (url.toString()
				.equals("http://sina-nice.com.cn:0/??id=500&jkl=\"*250?\"&search=?article##nice world#"));

		url = new URL(" hTtP ", "  WwW.SinA-Nice.com.cn  \n", 100, "  ",
				"search = ?article & ?id = 500 : jkl = \"*250?\"\n",
				" \r\t#nice world# ");
		assert (url.toString()
				.equals("http://sina-nice.com.cn:100/??id=500&jkl=\"*250?\"&search=?article##nice world#"));

		url = new URL(" hTtP ", "  WwW.SinA-Nice.com.cn  \n",
				Integer.MAX_VALUE, "  /",
				"search = ?article & ?id = 500 : jkl = \"*250?\"\n",
				" \r\t#nice world# ");
		assert (url.toString().equals("http://sina-nice.com.cn:"
				+ Integer.MAX_VALUE
				+ "/??id=500&jkl=\"*250?\"&search=?article##nice world#"));

		url = new URL(" hTtP ", "  WwW.SinA-Nice.com.cn  \n", 100, " // ",
				"search = ?article & ?id = 500 : jkl = \"*250?\"\n",
				" \r\t#nice world# ");
		assert (url.toString()
				.equals("http://sina-nice.com.cn:100/??id=500&jkl=\"*250?\"&search=?article##nice world#"));

		url = new URL(
				" hTtP ",
				"  WwW.SinA-Nice.com.cn  \n",
				100,
				"hello/../.. / .. / .. / nice /world/././../ I / aM / not / good/ ... / index.html/",
				"search =  & ?id =  : jkl = \n", " \r\t#nice world# ");
		assert (url.toString()
				.equals("http://sina-nice.com.cn:100/nice/I/aM/not/good/.../index.html##nice world#"));

		url = new URL(
				" hTtP ",
				"  WwW.SinA-Nice.com.cn  \n",
				100,
				"hello/../.. / .. / .. / nice /world/././../ I / aM / not / good/ ... / index.html/",
				"search = ?article & ?id = 500 : jkl = \"*250?\"\n", " \r\t ");
		assert (url.toString()
				.equals("http://sina-nice.com.cn:100/nice/I/aM/not/good/.../index.html??id=500&jkl=\"*250?\"&search=?article"));

		url = new URL(
				" hTtP ",
				"  WwW.SinA-Nice.com.cn  \n",
				100,
				"hello/../.. / .. / .. / nice /world/././../ I / aM / not / good/ ... / index.html/",
				"search =  & ?id =  : jkl = \n", " \r\t ");
		assert (url.toString()
				.equals("http://sina-nice.com.cn:100/nice/I/aM/not/good/.../index.html"));

		url = new URL(
				" hTtP ",
				"  WwW.SinA-Nice.com.cn  \n",
				100,
				"hello/../.. / .. / .. / nice /world/././../ I / aM / not / good/ ... / index.html/",
				"", "");
		assert (url.toString()
				.equals("http://sina-nice.com.cn:100/nice/I/aM/not/good/.../index.html"));

		url = new URL(
				" hTtP ",
				"  WwW.SinA-Nice.com.cn  \n",
				100,
				"hello/../.. / .. / .. / nice /world/././../ I / aM / not / good/ ... / index.html/",
				"  \n \r ", " \t \n ");
		assert (url.toString()
				.equals("http://sina-nice.com.cn:100/nice/I/aM/not/good/.../index.html"));

		url = new URL(
				" hTtP://WwW.SinA-Nice.com.cn:100/hello/../.. / .. / .. / nice /world/././../ I / aM / not / good/ ... / index.html/?#");
		assert (url.toString()
				.equals("http://sina-nice.com.cn:100/nice/I/aM/not/good/.../index.html"));

		url = new URL(
				" hTtP://WwW.SinA-Nice.com.cn:100/hello/../.. / .. / .. / nice /world/././../ I / aM / not / good/ ... / index.html/?  \r\t # \n ");
		assert (url.toString()
				.equals("http://sina-nice.com.cn:100/nice/I/aM/not/good/.../index.html"));

		url = new URL(
				" hTtP ",
				"  WwW.SinA-Nice.com.cn  \n",
				100,
				"hello/../.. / .. / .. / nice /world/././../ I / aM / not / good/ ... / index.html/",
				"search = ?article & ?id = 500 : jkl = \"*250?\"&search = ?article100 & ?id = 600 : jkl = \"*350?\"\n",
				" \r\t#nice world# ");
		assert (url.toString()
				.equals("http://sina-nice.com.cn:100/nice/I/aM/not/good/.../index.html??id=500&jkl=\"*250?\"&search=?article##nice world#"));

	}

	public void testEqualAndHashcode() throws MalformedURLException
	{
		long startTime = System.currentTimeMillis();

		assert (new URL("http://www.sina.com.cn").equals(new URL(
				"http://sina.com.cn:80/")));
		int hashCode = new URL("http://www.sina.com.cn").hashCode();
		assert (hashCode >= 0 || hashCode < 0);

		assert (System.currentTimeMillis() - startTime < 1);
	}

}
