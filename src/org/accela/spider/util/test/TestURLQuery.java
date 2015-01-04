package org.accela.spider.util.test;

import org.accela.spider.util.URLQuery;

import junit.framework.TestCase;

public class TestURLQuery extends TestCase
{
	public void testSimple()
	{
		assert (new URLQuery("").getQuery().equals(""));
		assert (new URLQuery("  ").getQuery().equals(""));
		assert (new URLQuery(" h = n").getQuery().equals("h=n"));
		assert (new URLQuery(" & h = n &").getQuery().equals("h=n"));
		assert (new URLQuery(" & h = n &    ").getQuery().equals("h=n"));
		assert (new URLQuery(" & h = n &  =  ").getQuery().equals("h=n"));
		assert (new URLQuery(" & h = n &  g=  ").getQuery().equals("h=n"));
		assert (new URLQuery(" & h = n &  =g  ").getQuery().equals("h=n"));
		assert (new URLQuery(" & h = n &  ==g  ").getQuery().equals("h=n"));
		assert (new URLQuery(" & h = n &&& gg=dd").getQuery().equals("gg=dd&h=n"));
		assert(new URLQuery("h=1&h=2&h=3:g=gg&g=100").getQuery().equals("g=gg&h=1"));
		assert (new URLQuery(
				"\n\r\t nihao = 100 == 1000  &:%dd?aaa:  bb=cc&nic e=:good= & &&hello = wol rd :bad == nice : bbad = = nice  : ")
				.getQuery()
				.equals("bad== nice&bb=cc&bbad== nice&hello=wol rd&nihao=100 == 1000"));
	}

}
