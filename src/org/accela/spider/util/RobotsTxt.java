package org.accela.spider.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.accela.common.Assertion;

//see robot exclusion protocol on wikipedia
public class RobotsTxt
{
	private List<Rule> rules = null;

	public RobotsTxt(String text)
	{
		if (null == text)
		{
			throw new IllegalArgumentException("text should not be null");
		}

		rules = parse(text);
	}

	private List<Rule> parse(String text)
	{
		if (null == text)
		{
			throw new IllegalArgumentException("text should not be null");
		}

		List<Rule> rules = new LinkedList<Rule>();
		Rule lastRule = null;

		try
		{
			BufferedReader reader = new BufferedReader(new StringReader(text));
			String line = null;

			while ((line = reader.readLine()) != null)
			{
				line = line.trim();

				int indexOfSharp = line.indexOf('#');
				if (indexOfSharp >= 0)
				{
					line = line.substring(0, indexOfSharp);
				}
				if (!line.contains(":"))
				{
					continue;
				}

				StringTokenizer tokens = new StringTokenizer(line, ":");
				if (tokens.countTokens() < 1)
				{
					continue;
				}

				String label = tokens.nextToken().trim();
				String value = tokens.hasMoreTokens() ? tokens.nextToken().trim() : "";

				if (label.equalsIgnoreCase("user-agent"))
				{
					lastRule = new Rule(new UserAgent(value), new DisallowedPathGroup());
					rules.add(lastRule);
				}
				else if (label.equalsIgnoreCase("disallow") && lastRule != null)
				{
					lastRule.getPathes().add(new DisallowedPath(value));
				}
				else
				{
					// do nothing
				}
			}

			reader.close();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			assert (false) : Assertion.declare();
		}

		return rules;
	}

	public boolean block(String agent, String path)
	{
		if (null == path)
		{
			throw new IllegalArgumentException("path should not be null");
		}

		if (null == agent)
		{
			agent = "";
		}

		agent = agent.trim();
		path = path.trim();
		for (Rule r : rules)
		{
			assert (r != null) : Assertion.declare();

			if (r.getAgent().match(agent))
			{
				return r.getPathes().match(path);
			}
		}

		return false;
	}

	// =========================================================

	private static class UserAgent
	{
		private String name = null;

		public UserAgent(String name)
		{
			if (null == name)
			{
				throw new IllegalArgumentException("name should not be null");
			}

			this.name = name.trim();
		}

		public boolean match(String other)
		{
			if (null == other)
			{
				throw new IllegalArgumentException("other should not be null");
			}

			other = other.trim();
			if (this.name.equals("*"))
			{
				return true;
			}
			else if (this.name.equals(other))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}

	private static class DisallowedPath
	{
		private URLPath path = null;

		public DisallowedPath(String path)
		{
			if (null == path)
			{
				throw new IllegalArgumentException("path should not be null");
			}

			path = path.trim();
			if (path.equals(""))
			{
				this.path = null;
			}
			else
			{
				this.path = new URLPath(path);
			}
		}

		@SuppressWarnings("unused")
		public boolean match(String other)
		{
			if (null == other)
			{
				throw new IllegalArgumentException("other should not be null");
			}

			return match(new URLPath(other));
		}

		// test if a given url path should be disallowed to access
		public boolean match(URLPath other)
		{
			if (null == other)
			{
				throw new IllegalArgumentException("other should not be null");
			}

			if (null == this.path)
			{
				return false;
			}
			else
			{
				return path.contains(other);
			}
		}

	}

	private static class DisallowedPathGroup
	{
		private List<DisallowedPath> paths = new LinkedList<DisallowedPath>();

		public void add(DisallowedPath path)
		{
			if (null == path)
			{
				throw new IllegalArgumentException("path should not be null");
			}

			this.paths.add(path);
		}

		public boolean match(String other)
		{
			if (null == other)
			{
				throw new IllegalArgumentException("other should not be null");
			}

			return match(new URLPath(other));
		}

		public boolean match(URLPath other)
		{
			if (null == other)
			{
				throw new IllegalArgumentException("other should not be null");
			}

			for (DisallowedPath path : paths)
			{
				assert (path != null) : Assertion.declare();

				if (path.match(other))
				{
					return true;
				}
			}

			return false;
		}

	}

	private static class Rule
	{
		private UserAgent agent = null;

		private DisallowedPathGroup pathes = null;

		public Rule(UserAgent agent, DisallowedPathGroup pathes)
		{
			if (null == agent)
			{
				throw new IllegalArgumentException("agent should not be null");
			}
			if (null == pathes)
			{
				throw new IllegalArgumentException("pathes should not be null");
			}

			this.agent = agent;
			this.pathes = pathes;
		}

		public UserAgent getAgent()
		{
			return agent;
		}

		public DisallowedPathGroup getPathes()
		{
			return pathes;
		}

	}

}
