package org.accela.spider.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.accela.common.Assertion;

public class URLQuery
{
	private SortedMap<String, String> map = null;

	public URLQuery(String query)
	{
		if (null == query)
		{
			throw new IllegalArgumentException("query should not be null");
		}

		map = parse(query.trim());
		assert (map != null) : Assertion.declare();
	}

	private SortedMap<String, String> parse(String query)
	{
		if (null == query)
		{
			throw new IllegalArgumentException("query should not be null");
		}

		return sort(cleanize(pairize(tokenize(query))));
	}

	private List<String> tokenize(String query)
	{
		List<String> tokenList = new LinkedList<String>();
		StringTokenizer tokens = new StringTokenizer(query, "&:");
		while (tokens.hasMoreTokens())
		{
			String token = tokens.nextToken().trim();
			if (token.contains("="))
			{
				tokenList.add(token);
			}
		}

		return tokenList;
	}

	private List<Pair> pairize(List<String> tokens)
	{
		List<Pair> pairs = new LinkedList<Pair>();
		for (String token : tokens)
		{
			if (!token.contains("="))
			{
				continue;
			}

			int idx = token.indexOf('=');
			assert (idx >= 0) : Assertion.declare();

			String field = token.substring(0, idx).trim();
			String value = token.substring(idx + 1).trim();
			if (field.length() <= 0)
			{
				continue;
			}
			if (value.length() <= 0)
			{
				continue;
			}

			pairs.add(new Pair(field, value));
		}

		return pairs;
	}

	private Map<String, String> cleanize(List<Pair> pairs)
	{
		Map<String, String> map = new HashMap<String, String>();
		for (Pair pair : pairs)
		{
			if (map.containsKey(pair.getField()))
			{
				continue;
			}

			map.put(pair.getField(), pair.getValue());
		}

		return map;
	}

	private SortedMap<String, String> sort(Map<String, String> map)
	{
		SortedMap<String, String> sortedMap = new TreeMap<String, String>();
		sortedMap.putAll(map);

		return sortedMap;
	}

	private static class Pair
	{
		private String field = null;

		private String value = null;

		public Pair(String field, String value)
		{
			if (null == field)
			{
				throw new IllegalArgumentException("field should not be null");
			}
			if (null == value)
			{
				throw new IllegalArgumentException("value should not be null");
			}

			this.field = field;
			this.value = value;
		}

		public String getField()
		{
			return field;
		}

		public String getValue()
		{
			return value;
		}

	}

	// ====================================================================

	public String getQuery()
	{
		StringBuffer query = new StringBuffer();

		for (String field : map.keySet())
		{
			assert (field != null) : Assertion.declare();
			String value = map.get(field);
			assert (value != null) : Assertion.declare();

			query.append(field + "=" + value);
			query.append("&"); // 必须使用'&'作分隔符，如果使用':',URLFileMapping会导致无法创建文件路径
		}
		if (query.length() > 0)
		{
			assert (query.charAt(query.length() - 1) == '&') : Assertion
					.declare();
			query.delete(query.length() - 1, query.length());
		}

		assert (checkValid()) : Assertion.declare();
		assert (new URLQuery(query.toString()).equals(this)) : Assertion
				.declare();

		return query.toString();
	}

	public String get(String field)
	{
		if (null == field)
		{
			throw new IllegalArgumentException("field should not be null");
		}

		assert (checkValid()) : Assertion.declare();
		return map.get(field);
	}

	public boolean contains(String field)
	{
		if (null == field)
		{
			throw new IllegalArgumentException("field should not be null");
		}

		assert (checkValid()) : Assertion.declare();
		return map.containsKey(field);
	}

	public int size()
	{
		assert (checkValid()) : Assertion.declare();
		return map.size();
	}

	public SortedMap<String, String> getMap()
	{
		assert (checkValid()) : Assertion.declare();
		return new TreeMap<String, String>(map);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (null == obj)
		{
			return false;
		}
		if (!(obj instanceof URLQuery))
		{
			return false;
		}

		assert (checkValid()) : Assertion.declare();

		URLQuery other = (URLQuery) obj;
		return this.map.equals(other.map);
	}

	@Override
	public int hashCode()
	{
		assert (checkValid()) : Assertion.declare();
		return map.hashCode();
	}

	@Override
	public String toString()
	{
		assert (checkValid()) : Assertion.declare();
		return getQuery();
	}

	private boolean checkValid()
	{
		if (null == map)
		{
			return false;
		}

		List<Pair> pairs = new ArrayList<Pair>();
		for (String field : map.keySet())
		{
			if (null == field)
			{
				return false;
			}
			if (null == map.get(field))
			{
				return false;
			}

			pairs.add(new Pair(field, map.get(field)));
		}

		for (int i = 0; i < pairs.size(); i++)
		{
			Pair p = pairs.get(i);
			if (null == p)
			{
				return false;
			}

			if (null == p.getField())
			{
				return false;
			}
			if (!p.getField().trim().equals(p.getField()))
			{
				return false;
			}
			if (p.getField().length() <= 0)
			{
				return false;
			}

			if (null == p.getValue())
			{
				return false;
			}
			if (!p.getValue().trim().equals(p.getValue()))
			{
				return false;
			}
			if (p.getValue().length() <= 0)
			{
				return false;
			}

			for (int j = i - 1; j >= 0; j--)
			{
				Pair p_inner = pairs.get(j);

				if (p_inner.getField().equals(p.getField()))
				{
					return false;
				}
				if (p_inner.getField().compareTo(p.getField()) >= 0)
				{
					return false;
				}

			}// end of inner for

		}// end of outer for

		return true;
	}

}
