package org.accela.spider.util.test;

import org.accela.spider.util.RobotsTxt;

import junit.framework.TestCase;

public class TestRobotsTxt extends TestCase
{
	public void testEmpty()
	{
		RobotsTxt r = new RobotsTxt("");

		assert (!r.block("", ""));
		assert (!r.block("google", "/hello"));
		assert (!r.block("*", "/"));
	}

	public void testBlock()
	{
		RobotsTxt r = new RobotsTxt("User-agent: *\n"
				+ "Disallow: /cgi-bin/\n"
				+ "Disallow: /tmp/\n"
				+ "Disallow: /~joe/\n");

		assert (r.block("", "/cgi-bin"));
		assert (r.block("", "/cgi-bin/"));
		assert (r.block("", "/cgi-bin/help"));
		assert (r.block("", "/cgi-bin/help/"));
		assert (r.block("", "/cgi-bin/help/nice"));
		assert (r.block("", "/cgi-bin/help/nice/"));

		assert (!r.block("", "/"));
		assert (r.block("", "/tmp"));
		assert (r.block("", "/tmp/"));
		assert (r.block("", "/tmp/nice"));
		assert (r.block("", "/tmp/nice/"));
		assert (r.block("", "/tmp/nice/help"));
		assert (r.block("", "/tmp/nice/help/"));

		assert (!r.block("g", "/joe/"));
		assert (r.block("g", "/~joe"));
		assert (r.block("g", "/~joe/"));
		assert (r.block("g", "/~joe/nice"));
		assert (r.block("g", "/~joe/nice/"));
		assert (r.block("g", "/~joe/nice/help"));
		assert (r.block("g", "/~joe/nice/help/"));
	}

	public void testAllBlock()
	{
		RobotsTxt r = new RobotsTxt("User-agent: *\n"
				+ "Disallow: /\n"
				+ "Disallow: /tmp/\n"
				+ "Disallow: /~joe/\n");

		assert (r.block("google", "/"));
		assert (r.block("google", "/tmp/"));
		assert (r.block("google", "/~joe/"));
		assert (r.block("google", "/tmp/hello"));
		assert (r.block("google", "/nice"));
		assert (r.block("google", "/nice/"));
		assert (r.block("google", "/nice/hello"));
	}

	public void testUnblock()
	{
		RobotsTxt r = new RobotsTxt("User-agent: *\n"
				+ "Disallow: \n"
				+ "Disallow:\n"
				+ "Disallow: /tmp/\n"
				+ "Disallow: /~joe/\n");

		assert (!r.block(null, "/"));
		assert (r.block("", "/tmp"));
		assert (r.block("", "/tmp/"));
		assert (r.block("g", "/~joe/"));
		assert (r.block("google", "/tmp"));
		assert (r.block("google", "/tmp/"));
		assert (r.block("google", "/tmp/hello"));
		assert (!r.block("google", "/nice"));
		assert (!r.block("google", "/nice/"));
		assert (!r.block("google", "/nice/hello"));
	}

	public void testUserAgentWildcard()
	{
		RobotsTxt r = new RobotsTxt("User-agent: *\n"
				+ "Disallow: /cgi-bin/\n"
				+ "Disallow: /tmp/\n"
				+ "Disallow: /~joe/\n");

		// null agent
		assert (!r.block(null, "/nice"));
		assert (!r.block(null, "/nice/"));

		assert (r.block(null, "/cgi-bin"));
		assert (r.block(null, "/cgi-bin/"));
		assert (r.block(null, "/cgi-bin/nice"));

		assert (r.block(null, "/tmp"));
		assert (r.block(null, "/tmp/"));
		assert (r.block(null, "/tmp/nice"));

		assert (r.block(null, "/~joe"));
		assert (r.block(null, "/~joe/"));
		assert (r.block(null, "/~joe/nice"));

		// empty agent
		assert (!r.block("", "/nice"));
		assert (!r.block("", "/nice/"));

		assert (r.block("", "/cgi-bin"));
		assert (r.block("", "/cgi-bin/"));
		assert (r.block("", "/cgi-bin/nice"));

		assert (r.block("", "/tmp"));
		assert (r.block("", "/tmp/"));
		assert (r.block("", "/tmp/nice"));

		assert (r.block("", "/~joe"));
		assert (r.block("", "/~joe/"));
		assert (r.block("", "/~joe/nice"));

		// wildcard agent
		assert (!r.block("*", "/nice"));
		assert (!r.block("*", "/nice/"));

		assert (r.block("*", "/cgi-bin"));
		assert (r.block("*", "/cgi-bin/"));
		assert (r.block("*", "/cgi-bin/nice"));

		assert (r.block("*", "/tmp"));
		assert (r.block("*", "/tmp/"));
		assert (r.block("*", "/tmp/nice"));

		assert (r.block("*", "/~joe"));
		assert (r.block("*", "/~joe/"));
		assert (r.block("*", "/~joe/nice"));

		// google agent
		assert (!r.block("google", "/nice"));
		assert (!r.block("google", "/nice/"));

		assert (r.block("google", "/cgi-bin"));
		assert (r.block("google", "/cgi-bin/"));
		assert (r.block("google", "/cgi-bin/nice"));

		assert (r.block("google", "/tmp"));
		assert (r.block("google", "/tmp/"));
		assert (r.block("google", "/tmp/nice"));

		assert (r.block("google", "/~joe"));
		assert (r.block("google", "/~joe/"));
		assert (r.block("google", "/~joe/nice"));
	}

	public void testComment()
	{
		RobotsTxt r = new RobotsTxt("User-agent: *\n"
				+ "  #Disallow: /\n"
				+ "Disallow: /cgi-bin/\n"
				+ "Disallow: /tmp/#helloworld\n"
				+ "Disallow: /~joe/\n");

		assert (!r.block("", "/nice"));
		assert (!r.block("", "/nice/"));
		assert (!r.block("", "/"));

		assert (r.block("", "/cgi-bin"));
		assert (r.block("", "/cgi-bin/"));
		assert (r.block("", "/cgi-bin/nice"));

		assert (r.block("", "/tmp"));
		assert (r.block("", "/tmp/"));
		assert (r.block("", "/tmp/nice"));

		assert (r.block("", "/~joe"));
		assert (r.block("", "/~joe/"));
		assert (r.block("", "/~joe/nice"));
	}

	public void testNoColon()
	{
		RobotsTxt r = new RobotsTxt("User-agent: *\n"
				+ "Disallow /cgi-bin/\n"
				+ "Disallow: /tmp/\n"
				+ "Disallow: /~joe/\n");

		assert (!r.block("", "/nice"));
		assert (!r.block("", "/nice/"));
		assert (!r.block("", "/"));

		assert (!r.block("", "/cgi-bin"));
		assert (!r.block("", "/cgi-bin/"));
		assert (!r.block("", "/cgi-bin/nice"));

		assert (r.block("", "/tmp"));
		assert (r.block("", "/tmp/"));
		assert (r.block("", "/tmp/nice"));

		assert (r.block("", "/~joe"));
		assert (r.block("", "/~joe/"));
		assert (r.block("", "/~joe/nice"));
	}

	public void testNoBlank()
	{
		RobotsTxt r = new RobotsTxt("User-agent:*\n"
				+ "Disallow:/cgi-bin/\n"
				+ "Disallow:/tmp/\n"
				+ "Disallow:/~joe/\n");

		assert (!r.block("", "/nice"));
		assert (!r.block("", "/nice/"));
		assert (!r.block("", "/"));

		assert (r.block("", "/cgi-bin"));
		assert (r.block("", "/cgi-bin/"));
		assert (r.block("", "/cgi-bin/nice"));

		assert (r.block("", "/tmp"));
		assert (r.block("", "/tmp/"));
		assert (r.block("", "/tmp/nice"));

		assert (r.block("", "/~joe"));
		assert (r.block("", "/~joe/"));
		assert (r.block("", "/~joe/nice"));
	}

	public void testCapitalization()
	{
		RobotsTxt r = new RobotsTxt("user-agent: *\n"
				+ "disallow: /cgi-bin/\n"
				+ "disALLow: /tmp/\n"
				+ "dIsAlLoW: /~joe/\n");

		assert (!r.block("", "/nice"));
		assert (!r.block("", "/nice/"));
		assert (!r.block("", "/"));

		assert (r.block("", "/cgi-bin"));
		assert (r.block("", "/cgi-bin/"));
		assert (r.block("", "/cgi-bin/nice"));

		assert (r.block("", "/tmp"));
		assert (r.block("", "/tmp/"));
		assert (r.block("", "/tmp/nice"));

		assert (r.block("", "/~joe"));
		assert (r.block("", "/~joe/"));
		assert (r.block("", "/~joe/nice"));
	}

	public void testMultiAgent()
	{
		RobotsTxt r = new RobotsTxt("User-agent: Google\n"
				+ "Disallow:\n"
				+ "\n"
				+ "User-agent: *\n"
				+ "Disallow: /");

		assert (!r.block("Google", "/"));
		assert (!r.block("  Google  ", "  / "));
		assert (r.block("google", "/"));
		assert (r.block("", "/"));
		assert (r.block(null, "/"));
		assert (r.block("nice", "/"));
		assert (r.block("*", "/"));
	}

	public void testIdentifyDirAndFile()
	{
		RobotsTxt r = new RobotsTxt("User-agent: *\n"
				+ "disallow: /cgi-bin/file\n");

		assert (r.block("", "/cgi-bin/file"));
		assert (r.block("", "/cgi-bin/file/"));
		assert (r.block("", "/cgi-bin/file/hello"));

		r = new RobotsTxt("User-agent: *\n" + "disallow: /cgi-bin/file/\n");
		assert (r.block("", "/cgi-bin/file"));
		assert (r.block("", "/cgi-bin/file/"));
		assert (r.block("", "/cgi-bin/file/nice"));
	}
}
