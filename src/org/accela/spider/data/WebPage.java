package org.accela.spider.data;

import java.util.LinkedList;
import java.util.List;

import org.accela.common.Assertion;
import org.accela.spider.strategy.Hyperlink;
import org.accela.spider.util.URL;

public class WebPage
{
	private URL url=null;
	
	private Content content=null;
	
	private List<Hyperlink> links=new LinkedList<Hyperlink>();
	
	private Analysis analysis=null;
	
	private long Stamp=0;
	
	private int recursion=0;
	
	public WebPage(URL url)
	{
		this(url, 0);
	}
	
	public WebPage(URL url, int recursion)
	{
		if(null==url)
		{
			throw new IllegalArgumentException("url should not be null");
		}
		if(recursion<0)
		{
			throw new IllegalArgumentException("recursion should not be negative");
		}
		
		this.url=url;
		this.recursion=recursion;
	}

	public URL getURL()
	{
		return url;
	}
	
	public Content getContent()
	{
		return content;
	}

	public void setContent(Content content)
	{
		if(null==content)
		{
			throw new IllegalArgumentException("content should not be null");
		}
		
		this.content = content;
	}

	public List<Hyperlink> getLinks()
	{
		return links;
	}

	public Analysis getAnalysis()
	{
		return analysis;
	}

	public void setAnalysis(Analysis analysis)
	{
		if(null==analysis)
		{
			throw new IllegalArgumentException("analysis should not be null");
		}
		
		this.analysis = analysis;
	}

	public long getStamp()
	{
		return Stamp;
	}

	public void setStamp(long stamp)
	{
		if(stamp<0)
		{
			throw new IllegalArgumentException("stamp should not be negative");
		}
		
		this.Stamp = stamp;
	}
	
	public int getRecursion()
	{
		assert(recursion>=0):Assertion.declare();
		return this.recursion;
	}

	public void setRecursion(int recursion)
	{
		if(recursion<0)
		{
			throw new IllegalArgumentException("recursion should not be negative");
		}
		this.recursion = recursion;
	}
	
	public void increaseRecursion()
	{
		assert(recursion>=0):Assertion.declare();
		recursion++;
	}

	@Override
	public String toString()
	{
		return super.toString()+"[url="+url.toString()+",recursion="+recursion+"]";
	}
	
}
