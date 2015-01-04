package org.accela.spider.strategy.impl;

import org.accela.spider.util.URL;

import org.accela.spider.strategy.URLFilter;
import org.accela.spider.util.PeriodicallyClearConcurrentHashMap;

//重复的URL请求总是需要过滤的。短时间内，多次重复的URL需要过滤；数据库中，刚刚
//被更新过URL，一定时间内也不应该再被重复更新。这是过滤重复请求的两个方面，前者
//限定的重复时间较短，比如1分钟；后者限定的时间较长，比如1天。前者仅仅访问内存
//就可以做到，后者需要数据库。如果仅仅只有对后者的过滤，那么会出现两种问题。第
//一种，每次检测重复都需要访问数据库，开销较大。第二种，如果数据库中还没有某一
//条URL的记录，那么测试就会允许这条URL通过，并让Spider下载其内容，然后在数据库中
//放入其记录。但是如果在数据库中放入对应记录之前，这条URL请求被重复大量次数地发送
//给spider，那么每一个URL都会通过测试，spider就会为每一URL请求重复地下载。这样，
//有意不良设计的网站就可以破坏spider。第一种过滤的中设定的限定时间，比如1分钟，
//这段时间足以让URL的下载结果存入数据库，从而避免了上述问题。但是第一种过滤的限定
//时间也不应该太长，并且不应该超过第二种过滤的限定时间。否则，比如6:00更新的网页，
//本来24小时后第二天6:00应该再更新，但是如果5:00一个下载这个网页URL请求发送给
//spider，当然这个请求不会通过，但是接下来到第三天5:00前，这个网页都不能被下载了。
//
//上述的第一种过滤功能由RepeatedURLFilter实现，放在PrefilterStage中；第二种过滤
//功能由DateURLFilter实现，放在FilterStage中。
public class RepeatedURLFilter implements URLFilter
{
	private PeriodicallyClearConcurrentHashMap<URL, URL> records=null;
	
	public RepeatedURLFilter(long repetitionInterval)
	{
		if(repetitionInterval<0)
		{
			throw new IllegalArgumentException("repetitionInterval should not be negative");
		}
		
		this.records=new PeriodicallyClearConcurrentHashMap<URL, URL>(repetitionInterval);
	}
	
	@Override
	public boolean accept(URL url)
	{
		if (null == url)
		{
			throw new IllegalArgumentException("url should not be null");
		}
		
		if(records.containsKey(url))
		{
			return false;
		}
		else
		{
			records.put(url, url);
			return true;
		}
	}

	public long getRepetitionInterval()
	{
		return records.getPeriod();
	}

}
