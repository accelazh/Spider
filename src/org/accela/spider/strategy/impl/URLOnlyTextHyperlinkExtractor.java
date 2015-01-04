package org.accela.spider.strategy.impl;

import java.net.MalformedURLException;
import org.accela.spider.util.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.accela.spider.data.Content;
import org.accela.spider.strategy.Hyperlink;
import org.accela.spider.strategy.HyperlinkExtractor;

public class URLOnlyTextHyperlinkExtractor implements HyperlinkExtractor
{
	@Override
	public List<Hyperlink> extract(URL context, Content content)
	{
		if (null == context)
		{
			throw new IllegalArgumentException("context should not be null");
		}
		if (null == content)
		{
			throw new IllegalArgumentException("content should not be null");
		}

		Pattern pattern = Pattern.compile("href\\s*=\\s*",
				Pattern.CASE_INSENSITIVE); // 这一步不能放在构造函数里，否则会被多线程侵蚀数据

		String text = content.getText();
		if (null == text)
		{
			throw new IllegalArgumentException("text should not be null");
		}

		List<Hyperlink> list = new LinkedList<Hyperlink>();
		Matcher matcher = pattern.matcher(text);
		while (matcher.find())
		{
			int startIdx = matcher.end();
			if (startIdx < 0 || startIdx >= text.length()-1)
			{
				continue;
			}
			int endIdx = 0;
			if (text.charAt(startIdx) == '"')
			{
				startIdx++;
				endIdx = text.indexOf('"', startIdx);
			}
			else
			{
				endIdx = Math.min(text.indexOf('>', startIdx), text
						.indexOf(' ', startIdx));
			}
			if (endIdx < 0 || endIdx >= text.length())
			{
				continue;
			}
			String urlStr = text.substring(startIdx, endIdx).trim();

			URL newURL = null;
			try
			{
				newURL = new URL(context, urlStr);
			}
			catch (MalformedURLException ex)
			{
				continue;
			}
			list.add(new URLOnlyHyperlink(newURL));
		}

		return list;
	}

}
