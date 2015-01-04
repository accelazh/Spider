package org.accela.spider.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.accela.common.Assertion;

public class URLPath
{
	private List<String> pathTokens = null;

	public URLPath(String path)
	{
		if (null == path)
		{
			throw new IllegalArgumentException("path should not be null");
		}

		this.pathTokens = normalize(parse(path.trim()));
	}

	public URLPath(List<String> pathTokens)
	{
		if (null == pathTokens)
		{
			throw new IllegalArgumentException("pathTokens should not be null");
		}

		this.pathTokens = normalize(pathTokens);
	}

	// =============将String转换成Token链表==================

	private List<String> parse(String path)
	{
		if (null == path)
		{
			throw new IllegalArgumentException("path should not be null");
		}

		return tokenize(completionize(path));
	}

	private String completionize(String path)
	{
		if (!path.startsWith("/"))
		{
			return "/" + path;
		}
		else
		{
			return path;
		}
	}

	private List<String> tokenize(String path)
	{
		assert (path.startsWith("/")) : Assertion.declare();

		List<String> tokens = new LinkedList<String>();
		int slashIdx = path.indexOf('/');
		while (true)
		{
			int nextSlashIdx = path.indexOf('/', slashIdx + 1);
			if (nextSlashIdx >= 0)
			{
				tokens.add(path.substring(slashIdx + 1, nextSlashIdx));
				slashIdx = nextSlashIdx;
			}
			else
			{
				tokens.add(path.substring(slashIdx + 1));
				break;
			}
		}

		assert (tokens.size() > 0) : Assertion.declare();
		assert (!path.endsWith("/") || (path.endsWith("/") && tokens.get(tokens
				.size() - 1).equals(""))) : Assertion.declare();

		return tokens;
	}

	// ===================将Token链表标准化========================

	private List<String> normalize(List<String> path)
	{
		if (null == path)
		{
			throw new IllegalArgumentException("path should not be null");
		}
		for(String token : path)
		{
			if(null==token)
			{
				continue;
			}
			
			if(token.contains("/"))
			{
				throw new IllegalArgumentException("token should not contain '/'");
			}
		}
		
		return simplify(cleanize(path));
	}

	private List<String> cleanize(List<String> path)
	{
		List<String> newPath = new LinkedList<String>();
		for (String token : path)
		{
			if (null == token)
			{
				continue;
			}

			String trimedToken = token.trim();
			if (trimedToken.equals(""))
			{
				continue;
			}
			
			newPath.add(trimedToken);
		}

		return newPath;
	}

	private List<String> simplify(List<String> path)
	{
		List<String> newPath = new LinkedList<String>();
		for (String token : path)
		{
			if (token.equals("."))
			{
				continue;
			}
			else if (token.equals(".."))
			{
				if (newPath.size() > 0
						&& !newPath.get(newPath.size() - 1).equals(".."))
				{
					newPath.remove(newPath.size() - 1);
				}
				else
				{
					newPath.add(token);
				}
			}
			else
			{
				newPath.add(token);
			}
		}// end of for

		return newPath;
	}

	// ========================普通方法========================

	public String getPath()
	{
		assert (checkValid()) : Assertion.declare();

		StringBuffer buf = new StringBuffer();

		for (String token : pathTokens)
		{
			assert (token != null) : Assertion.declare();

			buf.append('/');
			buf.append(token);
		}
		
		String path=buf.toString();
		if(!path.startsWith("/"))
		{
			assert(path.length()==0);
			path="/"+path;
		}

		assert (path.startsWith("/")) : Assertion.declare();
		assert(path.trim().equals(path)):Assertion.declare();
		assert(!path.contains("//")):Assertion.declare();
		assert(!path.endsWith("/")||pathTokens.size()==0);
		assert(new URLPath(path).equals(this)):Assertion.declare();
		
		return path;
	}

	public String getName()
	{
		assert (checkValid()) : Assertion.declare();
		if(pathTokens.size()<=0)
		{
			return "";
		}
		else 
		{
			String last= pathTokens.get(pathTokens.size()-1);
			if(last.equals(".."))
			{
				return "";
			}
			else
			{
				return last;
			}
		}
	}

	public int countToken()
	{
		assert (checkValid()) : Assertion.declare();
		return pathTokens.size();
	}

	public String getToken(int idx)
	{
		assert (checkValid()) : Assertion.declare();
		return pathTokens.get(idx);
	}

	public List<String> getTokens()
	{
		List<String> tokens=new LinkedList<String>(pathTokens);
		
		assert (checkValid()) : Assertion.declare();
		assert(new URLPath(tokens).equals(this)): Assertion.declare();
		
		return tokens;
	}

	public boolean contains(URLPath other)
	{
		if (null == other)
		{
			return false;
		}

		assert (checkValid()) : Assertion.declare();
		return isParent(other) || equals(other);
	}

	public boolean isParent(URLPath other)
	{
		if(null==other)
		{
			throw new IllegalArgumentException("other should not be null");
		}
		assert (checkValid()) : Assertion.declare();
		
		int dotCount=countDoubleDot();
		int otherDotCount=other.countDoubleDot();
		
		if(dotCount<otherDotCount)
		{
			return false;
		}
		if(dotCount>otherDotCount)
		{
			if(pathTokens.size()>dotCount)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		
		if(pathTokens.size()>=other.pathTokens.size())
		{
			return false;
		}
		
		Iterator<String> itr=pathTokens.iterator();
		Iterator<String> otherItr=other.pathTokens.iterator();
		
		while(itr.hasNext())
		{
			if(!itr.next().equals(otherItr.next()))
			{
				return false;
			}
		}
		
		return true;
	}
	
	private int countDoubleDot()
	{
		int dotCount=0;
		for (String token : pathTokens)
		{
			if (token.equals(".."))
			{
				dotCount++;
			}
			else
			{
				break;
			}
		}
		
		return dotCount;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (null == obj)
		{
			return false;
		}
		if (!(obj instanceof URLPath))
		{
			return false;
		}

		assert (checkValid()) : Assertion.declare();

		URLPath other = (URLPath) obj;
		return pathTokens.equals(other.pathTokens);
	}

	@Override
	public int hashCode()
	{
		assert (checkValid()) : Assertion.declare();
		return pathTokens.hashCode();
	}

	@Override
	public String toString()
	{
		assert (checkValid()) : Assertion.declare();
		return getPath();
	}

	// =======================自检方法=========================

	private boolean checkValid()
	{
		int dotCount=0;
		int i = 0;
		String lastToken = null;
		for (String token : pathTokens)
		{
			if (null == token)
			{
				return false;
			}
			if (!token.trim().equals(token))
			{
				return false;
			}
			if (token.equals("."))
			{
				return false;
			}

			if (token.trim().equals(""))
			{
				return false;
			}

			if (token.equals("..") && i > 0 && !lastToken.equals(".."))
			{
				return false;
			}
			
			if(token.equals(".."))
			{
				dotCount++;
			}

			i++;
			lastToken = token;
		}
		if(countDoubleDot()!=dotCount)
		{
			return false;
		}
		
		return true;
	}
}
