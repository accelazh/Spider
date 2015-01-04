package org.accela.spider.util.test;

import java.util.LinkedList;
import java.util.List;

import org.accela.spider.util.URLPath;

import junit.framework.TestCase;

public class TestURLPath extends TestCase
{
	private URLPath URLPath(String str)
	{
		return new URLPath(str);
	}

	public void testParseEmpty()
	{
		assert (URLPath("").getPath().equals("/"));
		assert (URLPath("  ").getPath().equals("/"));
		assert (URLPath("/").getPath().equals("/"));
		assert (URLPath("/..").getPath().equals("/.."));
		assert (URLPath("/../").getPath().equals("/.."));
		assert (URLPath("/../..").getPath().equals("/../.."));
		assert (URLPath("../..").getPath().equals("/../.."));

		assert (URLPath("").getName().equals(""));
		assert (URLPath("  ").getName().equals(""));
		assert (URLPath("/").getName().equals(""));
		assert (URLPath("/..").getName().equals(""));
		assert (URLPath("/../").getName().equals(""));
		assert (URLPath("/../..").getName().equals(""));
		assert (URLPath("../..").getName().equals(""));
	}

	public void testParseIncomplete()
	{
		assert (URLPath("hello/world").getPath().equals("/hello/world"));
		assert (URLPath("hello/world/").getPath().equals("/hello/world"));

		assert (URLPath("hello/world").getName().equals("world"));
		assert (URLPath("hello/world/").getName().equals("world"));
	}

	public void testSuccessiveSlash()
	{
		URLPath path = new URLPath("/hello////world");
		assert (path.getPath().equals("/hello/world"));
		assert (path.countToken() == 2);
		assert (path.getName().equals("world"));

		path = new URLPath("/hello////world/");
		assert (path.getPath().equals("/hello/world"));
		assert (path.countToken() == 2);
		assert (path.getName().equals("world"));

		path = new URLPath("/hello////world///");
		assert (path.getPath().equals("/hello/world"));
		assert (path.countToken() == 2);
		assert (path.getName().equals("world"));

		path = new URLPath("////hello////world///");
		assert (path.getPath().equals("/hello/world"));
		assert (path.countToken() == 2);
		assert (path.getName().equals("world"));

		path = new URLPath("/hello/  /  /  /world/");
		assert (path.getPath().equals("/hello/world"));
		assert (path.countToken() == 2);
		assert (path.getName().equals("world"));

		path = new URLPath("/hello/  /  /  /world//  ");
		assert (path.getPath().equals("/hello/world"));
		assert (path.countToken() == 2);
		assert (path.getName().equals("world"));

		path = new URLPath("/hello/  /  /  /world//../  ");
		assert (path.getPath().equals("/hello"));
		assert (path.countToken() == 1);
		assert (path.getName().equals("hello"));

		path = new URLPath("/hello/  /  /  /world//../  .. ");
		assert (path.getPath().equals("/"));
		assert (path.countToken() == 0);
		assert (path.getName().equals(""));

		path = new URLPath("nice/hello/  /  /  /world//../  .. ");
		assert (path.getPath().equals("/nice"));
		assert (path.countToken() == 1);
		assert (path.getName().equals("nice"));
	}

	public void testParseDir()
	{
		URLPath path = new URLPath("/nice/hello/");
		assert (path.getPath().equals("/nice/hello"));
		assert (path.getName().equals("hello"));
		assert (path.countToken() == 2);

		path = new URLPath("/nice/hello");
		assert (path.getPath().equals("/nice/hello"));
		assert (path.getName().equals("hello"));
		assert (path.countToken() == 2);

		path = new URLPath("nice/hello/");
		assert (path.getPath().equals("/nice/hello"));
		assert (path.getName().equals("hello"));
		assert (path.countToken() == 2);

		path = new URLPath("nice/hello");
		assert (path.getPath().equals("/nice/hello"));
		assert (path.getName().equals("hello"));
		assert (path.countToken() == 2);

		path = new URLPath("nice/hello/   ");
		assert (path.getPath().equals("/nice/hello"));
		assert (path.getName().equals("hello"));
		assert (path.countToken() == 2);

		path = new URLPath("/");
		assert (path.getPath().equals("/"));
		assert (path.getName().equals(""));
		assert (path.countToken() == 0);

		path = new URLPath("");
		assert (path.getPath().equals("/"));
		assert (path.getName().equals(""));
		assert (path.countToken() == 0);

		path = new URLPath("   ");
		assert (path.getPath().equals("/"));
		assert (path.getName().equals(""));
		assert (path.countToken() == 0);

		path = new URLPath("/hello/world/.");
		assert (path.getPath().equals("/hello/world"));
		assert (path.getName().equals("world"));
		assert (path.countToken() == 2);

		path = new URLPath("/hello/world/bad/..");
		assert (path.getPath().equals("/hello/world"));
		assert (path.getName().equals("world"));
		assert (path.countToken() == 2);

		path = new URLPath("/hello/world/bad/   /   /   /nice/");
		assert (path.getPath().equals("/hello/world/bad/nice"));
		assert (path.getName().equals("nice"));
		assert (path.countToken() == 4);

		path = new URLPath("/hello/world/bad/   /   /   ");
		assert (path.getPath().equals("/hello/world/bad"));
		assert (path.getName().equals("bad"));
		assert (path.countToken() == 3);
	}

	public void testParseFile()
	{
		URLPath path = new URLPath("/nice/hello");
		assert (path.getPath().equals("/nice/hello"));
		assert (path.getName().equals("hello"));
		assert (path.countToken() == 2);

		path = new URLPath("nice/hello");
		assert (path.getPath().equals("/nice/hello"));
		assert (path.getName().equals("hello"));
		assert (path.countToken() == 2);

		path = new URLPath("/a");
		assert (path.getPath().equals("/a"));
		assert (path.getName().equals("a"));
		assert (path.countToken() == 1);
	}

	public void testParseDot()
	{
		URLPath path = new URLPath("/../a/b/../c/./d.html");
		assert (path.getPath().equals("/../a/c/d.html"));
		assert (path.getName().equals("d.html"));

		path = new URLPath("/../a/b/../../c/././d.html");
		assert (path.getPath().equals("/../c/d.html"));
		assert (path.getName().equals("d.html"));

		path = new URLPath("/../a/b/../../c/././d.html/.");
		assert (path.getPath().equals("/../c/d.html"));
		assert (path.getName().equals("d.html"));

		path = new URLPath("/../a/b/../../../c/././d.html/.");
		assert (path.getPath().equals("/../../c/d.html"));
		assert (path.getName().equals("d.html"));

		path = new URLPath("/../a/b/../../../c/././d.html");
		assert (path.getPath().equals("/../../c/d.html"));
		assert (path.getName().equals("d.html"));

		path = new URLPath("/../a/b/../../../c/././d.html/");
		assert (path.getPath().equals("/../../c/d.html"));
		assert (path.getName().equals("d.html"));

		path = new URLPath("/../a/b/../../../c/././d.html/.");
		assert (path.getPath().equals("/../../c/d.html"));
		assert (path.getName().equals("d.html"));

		path = new URLPath("/../a/b/../../../c/././d.html/..");
		assert (path.getPath().equals("/../../c"));
		assert (path.getName().equals("c"));

		path = new URLPath("/../a/b/../../../.../c/././d.html/..");
		assert (path.getPath().equals("/../../.../c"));
		assert (path.getName().equals("c"));

		path = new URLPath("/../..");
		assert (path.getPath().equals("/../.."));
		assert (path.getName().equals(""));

		path = new URLPath("/a/b/../..");
		assert (path.getPath().equals("/"));
		assert (path.getName().equals(""));

		path = new URLPath("/a/b/c/../..");
		assert (path.getPath().equals("/a"));
		assert (path.getName().equals("a"));

		path = new URLPath("/a/b/c/../../d/../../..");
		assert (path.getPath().equals("/.."));
		assert (path.getName().equals(""));
		path = new URLPath("/a/b/c/.../.../d/.../.../..");
		assert (path.getPath().equals("/a/b/c/.../.../d/..."));
		assert (path.getName().equals("..."));
	}

	public void testIsParent()
	{
		assert (new URLPath("/").isParent(new URLPath("/hello")));
		assert (new URLPath("/").isParent(new URLPath("/hello/")));

		assert (!new URLPath("/nice").isParent(new URLPath("/nice")));
		assert (!new URLPath("/nice/").isParent(new URLPath("/nice")));

		assert (!new URLPath("/nice/").isParent(new URLPath("/nice/")));
		assert (!new URLPath("/nice").isParent(new URLPath("/nice/")));

		assert (new URLPath("/nice/").isParent(new URLPath("/nice/hello")));
		assert (new URLPath("/nice/hello/").isParent(new URLPath(
				"/nice/hello/nice")));

		assert (new URLPath("/nice/hello").isParent(new URLPath(
				"/nice/hello/nice")));
		assert (!new URLPath("/nice/hello/good/").isParent(new URLPath(
				"/nice/hello/good")));
		assert (!new URLPath("/nice/hello/good/").isParent(new URLPath(
				"/nice/hello/good2")));
		assert (!new URLPath("/nice/hello/good/").isParent(new URLPath(
				"/nice/hello/good2/")));
		assert (!new URLPath("/nice/hello/good/").isParent(new URLPath(
				"/nice/hello/good2/bad")));

		assert (!new URLPath("/nice/hello/good/").isParent(new URLPath(
				"/nice/hello/good")));
		assert (!new URLPath("/nice/hello/good/").isParent(new URLPath(
				"/nice/hello/good/")));

		assert (new URLPath("../../nice/").isParent(new URLPath(
				"/../../nice/hello")));
		assert (!new URLPath("/../../nice/").isParent(new URLPath(
				"/../../../nice/hello")));

		assert (!new URLPath("../../../../nice/").isParent(new URLPath(
				"/../../../nice/hello")));

		assert (new URLPath("../../../..").isParent(new URLPath(
				"/../../../nice/hello")));

		assert (new URLPath("../../../").isParent(new URLPath(
				"/../../../nice/hello")));

		assert (new URLPath("../../../../").isParent(new URLPath(
				"/../../../nice/hello")));

		assert (!new URLPath("/../../nice/").isParent(new URLPath(
				"/../../nice/../hello")));

		assert (!new URLPath("/../../../nice").isParent(new URLPath(
				"/../../nice/")));

		assert (!new URLPath("/../../../nice/").isParent(new URLPath(
				"/../../nice/")));

		assert (new URLPath("/../../..").isParent(new URLPath("/../nice/")));

		assert (new URLPath("/../../..").isParent(new URLPath("/nice/")));

		assert (new URLPath("/../../..").isParent(new URLPath("/nice")));
	}

	public void testEquals()
	{
		assert (new URLPath("/").equals(new URLPath("")));
		assert (new URLPath("/").equals(new URLPath("/")));

		assert (!new URLPath("/").equals(new URLPath("/hello")));
		assert (!new URLPath("/").equals(new URLPath("/hello/")));

		assert (new URLPath("/nice").equals(new URLPath("/nice")));
		assert (new URLPath("/nice/").equals(new URLPath("/nice")));

		assert (new URLPath("/nice/").equals(new URLPath("/nice/")));
		assert (new URLPath("/nice").equals(new URLPath("/nice/")));
		assert (new URLPath("/nice/world").equals(new URLPath("/nice/world/")));

		assert (!new URLPath("/nice/").equals(new URLPath("/nice/hello")));
		assert (new URLPath("/nice/hello/").equals(new URLPath("/nice/hello/")));
		assert (new URLPath("/nice/hello/").equals(new URLPath("/nice/hello")));
		assert (new URLPath("/nice/hello").equals(new URLPath("/nice/hello/")));

		assert (new URLPath("/nice/hello/good/").equals(new URLPath(
				"/nice/hello/good")));
		assert (new URLPath("/nice/hello/good").equals(new URLPath(
				"/nice/hello/good/")));
		assert (new URLPath("/nice/hello/././good").equals(new URLPath(
				"/nice/hello/nice/../good/")));
		assert (new URLPath("/nice/hello/good/nice/..").equals(new URLPath(
				"/nice/hello/good/")));
		assert (!new URLPath("/nice/hello/good/").equals(new URLPath(
				"/nice/hello/good2/")));
		assert (!new URLPath("/nice/hello/good/").equals(new URLPath(
				"/nice/hello2/good/")));
		assert (!new URLPath("/nice/hello/good/").equals(new URLPath(
				"/nice/hello/good/bad")));

		assert (new URLPath("/nice/.").equals(new URLPath("/nice/")));
		assert (new URLPath("/nice/good/..").equals(new URLPath("/nice/")));
		assert (new URLPath("/nice/good/..").equals(new URLPath("/nice")));
	}

	public void testContains()
	{
		assert (new URLPath("/").contains(new URLPath("/hello")));
		assert (new URLPath("/").contains(new URLPath("/hello/")));

		assert (new URLPath("/hello").contains(new URLPath("/hello/")));
		assert (new URLPath("/hello/nice/")
				.contains(new URLPath("/hello/nice")));

		assert (new URLPath("/hello/nice/good").contains(new URLPath(
				"/hello/nice/good")));
		assert (new URLPath("/hello/nice/good").contains(new URLPath(
				"/hello/nice/good/")));
		assert (new URLPath("/hello/nice/good/").contains(new URLPath(
				"/hello/nice/good")));
		assert (new URLPath("/hello/nice/good/").contains(new URLPath(
				"/hello/nice/good/")));
		assert (new URLPath("/hello/nice/good/").contains(new URLPath(
				"/hello/nice/good/nice/..")));
		assert (new URLPath("/hello/nice/good/").contains(new URLPath(
				"/hello/nice/good/nice/bad/..")));

		assert (new URLPath("/hello/./././nice/good/.././nice/..")
				.contains(new URLPath("/hello/nice/nice/bad")));
		assert (new URLPath("/hello/./././nice/good/.././nice/..")
				.contains(new URLPath("/hello/nice/nice/bad/")));

		assert (new URLPath("/hello/./././nice/good/.././nice/..")
				.contains(new URLPath("/hello/nice/nice/bad")));
		assert (new URLPath("/hello/./././nice/good/../.././nice/..")
				.contains(new URLPath("/hello/nice/nice/bad/")));

		assert (!new URLPath("/hello/./././nice/good/.././nice/..")
				.contains(new URLPath("/hello2/nice/nice/bad")));
		assert (!new URLPath("/hello/./././nice/good/../.././nice/..")
				.contains(new URLPath("/hello2/nice/nice/bad/")));

		assert (!new URLPath("/hello/./././nice/good/.././nice/..")
				.contains(new URLPath("/")));
		assert (!new URLPath("/hello/./././nice/good/../.././nice/..")
				.contains(new URLPath("/")));

		assert (!new URLPath("/hello/nice/good/")
				.contains(new URLPath("/hello")));
		assert (!new URLPath("/hello/./././nice/good/../.././nice/..")
				.contains(new URLPath("/")));

		assert (new URLPath("../../nice/").contains(new URLPath(
				"/../../nice/hello")));
		assert (!new URLPath("/../../nice/").contains(new URLPath(
				"/../../../nice/hello")));
		assert (!new URLPath("/../../nice/").contains(new URLPath(
				"/../../nice/../hello")));

		assert (new URLPath("../../nice/").contains(new URLPath(
				"/../../nice/hello/..")));
		assert (new URLPath("../../nice").contains(new URLPath(
				"/../../nice/hello/..")));
		assert (new URLPath("../../nice").contains(new URLPath(
				"/../../nice/ok/hello/..")));
	}

	public void testGetTokens()
	{
		URLPath path = new URLPath("/../../nice/hello/world/job/.././good");
		List<String> tokens = path.getTokens();
		assert (new URLPath(tokens).getPath()
				.equals("/../../nice/hello/world/good"));
		assert (new URLPath(tokens).getTokens().size() == 6);

		tokens.remove(0);
		tokens.remove(0);
		assert (new URLPath(tokens).getPath().equals("/nice/hello/world/good"));
		assert (new URLPath(tokens).getTokens().size() == 4);
	}

	public void testTokenNormalize()
	{
		List<String> tokens = new LinkedList<String>();
		tokens.add(null);
		tokens.add("hello");
		tokens.add(null);
		tokens.add("");
		tokens.add(null);
		tokens.add("  ");
		tokens.add(null);
		tokens.add("..");
		tokens.add(null);
		tokens.add(".");
		tokens.add(null);
		tokens.add("nice");
		tokens.add(null);
		tokens.add("");
		tokens.add(null);
		assert (new URLPath(tokens).getPath().equals("/nice"));
		assert (new URLPath(tokens).getTokens().size()==1);

		tokens = new LinkedList<String>();
		tokens.add(null);
		tokens.add("hello");
		tokens.add(null);
		tokens.add("");
		tokens.add(null);
		tokens.add("  ");
		tokens.add(null);
		tokens.add("..");
		tokens.add(null);
		tokens.add(".");
		tokens.add(null);
		tokens.add("nice");
		tokens.add(null);
		tokens.add("");
		tokens.add(null);
		tokens.add(".");
		assert (new URLPath(tokens).getPath().equals("/nice"));
		assert (new URLPath(tokens).getTokens().size()==1);

		tokens = new LinkedList<String>();
		tokens.add(null);
		tokens.add("hello");
		tokens.add(null);
		tokens.add("");
		tokens.add(null);
		tokens.add("  ");
		tokens.add(null);
		tokens.add("..");
		tokens.add(null);
		tokens.add(".");
		tokens.add(null);
		tokens.add("nice");
		tokens.add(null);
		tokens.add("");
		tokens.add(null);
		tokens.add(".");
		tokens.add(null);
		tokens.add("good");
		tokens.add(null);
		tokens.add("hello");
		tokens.add(null);
		tokens.add("..");
		assert (new URLPath(tokens).getPath().equals("/nice/good"));
		assert (new URLPath(tokens).getTokens().size()==2);

		tokens = new LinkedList<String>();
		tokens.add(null);
		tokens.add("hello");
		tokens.add(null);
		tokens.add("");
		tokens.add(null);
		tokens.add("  ");
		tokens.add(null);
		tokens.add("..");
		tokens.add(null);
		tokens.add(".");
		tokens.add(null);
		tokens.add("nice");
		tokens.add(null);
		tokens.add("");
		tokens.add(null);
		tokens.add(".");
		tokens.add(null);
		tokens.add("good");
		tokens.add(null);
		tokens.add("hello");
		tokens.add(null);
		tokens.add("..");
		tokens.add("bad");
		assert (new URLPath(tokens).getPath().equals("/nice/good/bad"));
		assert (new URLPath(tokens).getTokens().size()==3);

		tokens = new LinkedList<String>();
		tokens.add(null);
		tokens.add("hello");
		tokens.add(null);
		tokens.add("");
		tokens.add(null);
		tokens.add("  ");
		tokens.add(null);
		tokens.add("..");
		tokens.add(null);
		tokens.add(".");
		tokens.add(null);
		tokens.add("nice");
		tokens.add(null);
		tokens.add("");
		tokens.add(null);
		tokens.add(".");
		tokens.add(null);
		tokens.add("good");
		tokens.add(null);
		tokens.add("hello");
		tokens.add(null);
		tokens.add("..");
		tokens.add(null);
		tokens.add("bad");
		tokens.add(null);
		tokens.add("   ");
		assert (new URLPath(tokens).getPath().equals("/nice/good/bad"));
		assert (new URLPath(tokens).getTokens().size()==3);
	}

	public void testGetTokenUnrelevant()
	{
		URLPath path = new URLPath("/hello/nice/good");
		path.getTokens().clear();
		assert (path.toString().equals("/hello/nice/good"));
		assert (path.getTokens().size() == 3);
	}

	public void testEmptyToken()
	{
		URLPath path = new URLPath(new LinkedList<String>());
		assert (path.getPath().equals("/"));
		assert (path.getTokens().size() == 0);

		List<String> tokens = new LinkedList<String>();
		tokens.add("");
		tokens.add("  ");
		tokens.add(" \r\n\t\b\f  ");

		path = new URLPath(tokens);
		assert (path.getPath().equals("/"));
		assert (path.getTokens().size() == 0);
	}

	public void testIllegalToken()
	{
		List<String> tokens = new LinkedList<String>();
		tokens.add(null);
		tokens.add("hello");
		tokens.add(null);
		tokens.add("");
		tokens.add(null);
		tokens.add("  ");
		tokens.add(null);
		tokens.add("..");
		tokens.add(null);
		tokens.add(".");
		tokens.add(null);
		tokens.add("hello/world");
		tokens.add("nice");
		tokens.add(null);
		tokens.add("");
		tokens.add(null);
		tokens.add(".");
		tokens.add(null);
		tokens.add("good");
		tokens.add(null);
		tokens.add("hello");
		tokens.add(null);
		tokens.add("..");
		tokens.add(null);
		tokens.add("bad");
		tokens.add(null);
		tokens.add("   ");
		try
		{
			new URLPath(tokens);
			assert (false);
		}
		catch (Exception ex)
		{
			assert (ex instanceof IllegalArgumentException);
		}
	}

	public void testRandomGetTokens()
	{
		for (int i = 0; i < 5000; i++)
		{
			randomGetTokensTest();
		}
	}

	private void randomGetTokensTest()
	{
		// generate list
		List<String> tokens = new LinkedList<String>();
		int size = (int) (500 + Math.random() * 1000);
		for (int i = 0; i < size; i++)
		{
			double rand = Math.random();
			if (rand < 0.1)
			{
				tokens.add(null);
			}
			else if (rand < 0.2)
			{
				tokens.add("");
			}
			else if (rand < 0.3)
			{
				tokens.add("   ");
			}
			else if (rand < 0.4)
			{
				tokens.add(".");
			}
			else if (rand < 0.5)
			{
				tokens.add("..");
			}
			else if (rand < 0.6)
			{
				tokens.add("    nice    ");
			}
			else if (rand < 0.7)
			{
				tokens.add("nice");
			}
			else if (rand < 0.8)
			{
				tokens.add("    world    ");
			}
			else if (rand < 0.9)
			{
				tokens.add("world");
			}
			else
			{
				tokens.add("hello");
			}
		}

		double rand = Math.random();
		if (rand < 0.2)
		{
			tokens.add("");
		}
		else if (rand < 0.4)
		{
			tokens.add("   ");
		}
		else if (rand < 0.6)
		{
			tokens.add(".");
		}
		else if (rand < 0.8)
		{
			tokens.add("..");
		}
		else
		{
			// do nothing
		}

		// test path
		URLPath path = new URLPath(tokens);
		assert (path.countToken() >= 0); // URLPath.checkValid() will do the
											// test
		assert (path.getTokens().size()>=0); // URLPath.getTokens() will do some
											// check
	}

}
