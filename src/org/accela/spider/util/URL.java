package org.accela.spider.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.util.List;

import org.accela.common.Assertion;

//java.net.URL.equals() method tries to access the net to test whether 
//two URLs are equal. this result to very low performance when used in 
//collections. so I implemented myself URL class, which uses string to 
//test equality. by the way, days after I found java.net.URL.hashCode() 
//also has the same problem.
//Day 2: I have put strict check and normalization in the URL class, in 
//order to avoid scattered troublesome checks in SimpleURLNormalizer, 
//RecursionFilter and WebPageFileMapping and so on. I have also removed 
//useless methods from java.net.URL.
public class URL
{
	private java.net.URL url = null;

	public URL(String protocol,
			String host,
			int port,
			String path,
			String query,
			String ref) throws MalformedURLException
	{
		construct(protocol, host, port, path, query, ref);
	}

	public URL(String protocol,
			String host,
			String path,
			String query,
			String ref) throws MalformedURLException
	{
		if (null == protocol)
		{
			throw new MalformedURLException("null protocol");
		}
		if (null == host)
		{
			throw new MalformedURLException("null host");
		}
		if (null == path)
		{
			throw new MalformedURLException("null path");
		}

		path = path.trim();
		if (path.length() > 0 && !path.startsWith("/"))
		{
			path = "/" + path;
		}

		String urlStr = protocol + "://" + host + path;
		if (query != null)
		{
			urlStr += "?" + query;
		}
		if (ref != null)
		{
			urlStr += "#" + ref;
		}

		java.net.URL javaURL = new java.net.URL(urlStr);
		construct(javaURL);

	}

	public URL(String protocol, String host, String path)
			throws MalformedURLException
	{
		this(protocol, host, path, null, null);
	}

	public URL(String spec) throws MalformedURLException
	{
		java.net.URL javaURL = new java.net.URL(spec);
		construct(javaURL);
	}

	public URL(URL context, String spec) throws MalformedURLException
	{
		java.net.URL javaURL = new java.net.URL(context.getJavaURL(), spec);
		construct(javaURL);
	}

	public URL(java.net.URL javaURL) throws MalformedURLException
	{
		if (null == javaURL)
		{
			throw new IllegalArgumentException("javaURL should not be null");
		}

		construct(javaURL);
	}

	private void construct(java.net.URL javaURL) throws MalformedURLException
	{
		construct(javaURL.getProtocol(),
				javaURL.getHost(),
				javaURL.getPort() >= 0 ? javaURL.getPort() : javaURL
						.getDefaultPort(),
				javaURL.getPath(),
				javaURL.getQuery(),
				javaURL.getRef());
	}

	private void construct(String protocol,
			String host,
			int port,
			String path,
			String query,
			String ref) throws MalformedURLException
	{
		if (protocol != null)
		{
			protocol = protocol.trim();
		}
		if (host != null)
		{
			host = host.trim();
		}
		if (path != null)
		{
			path = path.trim();
		}
		if (!path.startsWith("/"))
		{
			path = "/" + path;
		}
		if (query != null)
		{
			query = query.trim();
		}
		if (ref != null)
		{
			ref = ref.trim();
		}

		if (!checkProtocolValid(protocol))
		{
			throw new MalformedURLException("illegal protocol: " + protocol);
		}
		if (!checkHostValid(host))
		{
			throw new MalformedURLException("illegal host: " + host);
		}
		if (!checkPortValid(port))
		{
			throw new MalformedURLException("illegal port: " + port);
		}
		if (!checkPathValid(path))
		{
			throw new MalformedURLException("illegal path: " + path);
		}
		if (!checkQueryValid(query))
		{
			throw new MalformedURLException("illegal query: " + query);
		}
		if (!checkRefValid(ref))
		{
			throw new MalformedURLException("illegal ref: " + ref);
		}

		protocol = normalizeProtocol(protocol);
		host = normalizeHost(host);
		port = normalizePort(port);
		path = normalizePath(path);
		query = normalizeQuery(query);
		ref = normalizeRef(ref);

		assert (protocol != null);
		assert (host != null);
		assert (port >= 0);
		assert (path != null);

		String urlStr = protocol + "://" + host + ":" + port + path;
		if (query != null)
		{
			urlStr += "?" + query;
		}
		if (ref != null)
		{
			urlStr += "#" + ref;
		}

		assert (null == this.url) : Assertion.declare();
		this.url = new java.net.URL(urlStr);

		assert (this.toString().trim().equals(this.toString())) : Assertion
				.declare();
	}

	private boolean checkProtocolValid(String protocol)
	{
		if (null == protocol)
		{
			return false;
		}

		if (protocol.trim().length() <= 0)
		{
			return false;
		}

		for (int i = 0; i < protocol.length(); i++)
		{
			if (!Character.isLetterOrDigit(protocol.charAt(i)))
			{
				return false;
			}
		}

		return true;
	}

	private String normalizeProtocol(String protocol)
	{
		return protocol.trim().toLowerCase();
	}

	private boolean checkHostValid(String host)
	{
		if (!checkHostValidShallow(host))
		{
			return false;
		}

		host = normalizeHost(host);
		if (!checkHostValidShallow(host))
		{
			return false;
		}

		return true;
	}

	private boolean checkHostValidShallow(String host)
	{
		if (null == host)
		{
			return false;
		}

		if (host.trim().length() <= 0)
		{
			return false;
		}

		for (int i = 0; i < host.length(); i++)
		{
			char ch = host.charAt(i);

			if (!Character.isLetterOrDigit(ch) && ch != '-' && ch != '.')
			{
				return false;
			}
		}

		if (host.startsWith(".") || host.endsWith("."))
		{
			return false;
		}

		if (host.startsWith("-") || host.endsWith("-"))
		{
			return false;
		}

		if (host.contains(".."))
		{
			return false;
		}

		if (!host.contains("."))
		{
			return false;
		}

		return true;
	}

	private String normalizeHost(String host)
	{
		host = host.trim().toLowerCase();
		if (host.startsWith("www."))
		{
			host = host.substring("www.".length()).trim();
		}

		return host;
	}

	private boolean checkPortValid(int port)
	{
		if (port < 0)
		{
			return false;
		}

		return true;
	}

	private int normalizePort(int port)
	{
		return Math.max(0, port);
	}

	private boolean checkPathValid(String path)
	{
		if (null == path)
		{
			return false;
		}

		if (!path.startsWith("/"))
		{
			if (path.length() != 0)
			{
				return false;
			}
		}

		if (!path.trim().equals(path))
		{
			return false;
		}

		for (int i = 0; i < path.length(); i++)
		{
			char ch = path.charAt(i);
			if (ch < ' ')
			{
				return false;
			}
			if ('\\' == ch
					|| ':' == ch
					|| '*' == ch
					|| '?' == ch
					|| '"' == ch
					|| '<' == ch
					|| '>' == ch
					|| '|' == ch)
			{
				return false;
			}
		}

		return true;
	}

	private String normalizePath(String path)
	{
		List<String> pathTokens = new URLPath(path).getTokens();
		while (pathTokens.size() > 0 && pathTokens.get(0).equals(".."))
		{
			pathTokens.remove(0);
		}
		assert (!pathTokens.contains("..")): Assertion.declare();
		assert (!pathTokens.contains(".")): Assertion.declare();

		path = new URLPath(pathTokens).getPath();
		assert (checkPathValid(path)) : Assertion.declare();

		return path;
	}

	private boolean checkQueryValid(String query)
	{
		if (null == query)
		{
			return true;
		}

		if (query.contains("#"))
		{
			return false;
		}

		for (int i = 0; i < query.length(); i++)
		{
			char ch = query.charAt(i);
			if (ch < ' ')
			{
				return false;
			}
		}

		if (!query.contains("="))
		{
			if (query.length() != 0)
			{
				return false;
			}
		}

		return true;
	}

	private String normalizeQuery(String query)
	{
		if (null == query)
		{
			return null;
		}

		URLQuery urlQuery = new URLQuery(query);
		query = urlQuery.getQuery().trim();

		if (query.length() <= 0)
		{
			query = null;
		}

		return query;
	}

	private boolean checkRefValid(String ref)
	{
		if (null == ref)
		{
			return true;
		}

		for (int i = 0; i < ref.length(); i++)
		{
			char ch = ref.charAt(i);
			if (ch < ' ')
			{
				return false;
			}
		}

		return true;
	}

	private String normalizeRef(String ref)
	{
		if (null == ref)
		{
			return null;
		}

		ref = ref.trim();
		if (ref.length() <= 0)
		{
			ref = null;
		}

		return ref;
	}

	public java.net.URL getJavaURL()
	{
		assert (this.url != null) : Assertion.declare();
		return this.url;
	}

	private boolean testMayNullStringEqual(String s1, String s2)
	{
		if (s1 != null && s2 != null)
		{
			return s1.equals(s2);
		}
		else if (null == s1 && null == s2)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		if (null == obj)
		{
			return false;
		}
		if (!(obj instanceof URL))
		{
			return false;
		}

		URL other = (URL) obj;
		if (!testMayNullStringEqual(this.getProtocol(), other.getProtocol())
				|| !testMayNullStringEqual(this.getHost(), other.getHost())
				|| (this.getPort() != other.getPort())
				|| !testMayNullStringEqual(this.getPath(), other.getPath())
				|| !testMayNullStringEqual(this.getQuery(), other.getQuery())
				|| !testMayNullStringEqual(this.getRef(), other.getRef()))
		{
			return false;
		}

		return true;
	}

	public boolean equalsByNet(URL other)
	{
		return url.equals(other.url);
	}

	public String getProtocol()
	{
		assert (url.getProtocol() != null) : Assertion.declare();
		assert (url.getProtocol().toLowerCase().equals(url.getProtocol())) : Assertion
				.declare();
		return url.getProtocol();
	}

	public String getHost()
	{
		assert (url.getHost() != null) : Assertion.declare();
		assert (url.getHost().toLowerCase().equals(url.getHost())) : Assertion
				.declare();
		assert (!url.getHost().contains("/")) : Assertion.declare();
		return url.getHost();
	}

	public int getPort()
	{
		assert (url.getPort() >= 0) : Assertion.declare();
		return url.getPort();
	}

	public String getPath()
	{
		assert (url.getPath() != null) : Assertion.declare();
		assert (url.getPath().trim().equals(url.getPath())) : Assertion
				.declare();
		assert (url.getPath().startsWith("/")) : Assertion.declare();
		assert (!url.getPath().endsWith("/") || url.getPath().equals("/"));
		return url.getPath();
	}

	public String getQuery()
	{
		assert (url.getQuery() == null || (url.getQuery().trim().equals(url
				.getQuery()) && url.getQuery().length() > 0)) : Assertion
				.declare();
		return url.getQuery();
	}

	public String getRef()
	{
		assert (url.getRef() == null || (url.getRef().trim().equals(url
				.getRef()) && url.getRef().length() > 0)) : Assertion.declare();
		return url.getRef();
	}

	private int mayNullStringHashCode(String str)
	{
		if (null == str)
		{
			return 0;
		}
		else
		{
			return str.hashCode();
		}
	}

	@Override
	public int hashCode()
	{
		int sum = 0;
		sum += mayNullStringHashCode(this.getProtocol());
		sum *= 31;
		sum += mayNullStringHashCode(this.getHost());
		sum *= 31;
		sum += this.getPort();
		sum *= 31;
		sum += mayNullStringHashCode(this.getPath());
		sum *= 31;
		sum += mayNullStringHashCode(this.getQuery());
		sum *= 31;
		sum += mayNullStringHashCode(this.getRef());
		sum *= 31;

		return sum;
	}

	public int hashCodeByNet()
	{
		return this.url.hashCode();
	}

	public URLConnection openConnection() throws IOException
	{
		return url.openConnection();
	}

	public final InputStream openStream() throws IOException
	{
		return url.openStream();
	}

	@Override
	public String toString()
	{
		String defStr = url.toString();

		assert (defStr.equals(getString())) : Assertion.declare();
		return defStr;
	}

	private String getString()
	{
		String str = getProtocol()
				+ "://"
				+ getHost()
				+ ":"
				+ getPort()
				+ getPath();
		if (getQuery() != null)
		{
			str += "?" + getQuery();
		}
		if (getRef() != null)
		{
			str += "#" + getRef();
		}

		for (int i = 0; i < str.length(); i++)
		{
			assert (str.charAt(i) >= ' ') : Assertion.declare();
		}

		return str;
	}
}
