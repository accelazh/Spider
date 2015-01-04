package org.accela.spider.strategy.test;

import java.io.IOException;
import java.util.List;

import org.accela.spider.data.Content;
import org.accela.spider.strategy.Hyperlink;
import org.accela.spider.strategy.impl.URLOnlyTextHyperlinkExtractor;
import org.accela.spider.strategy.impl.TextContentFetcher;
import org.accela.spider.util.URL;

import junit.framework.TestCase;

public class TestURLOnlyTextHyperlinkExtractor extends TestCase
{
	// preset: network connection to Internet
	public void testSimple() throws IOException
	{
		URL url = new URL("http://www.sina.com.cn");
		TextContentFetcher fetcher = new TextContentFetcher();
		Content content = fetcher.fetchContent(url);

		URLOnlyTextHyperlinkExtractor extractor = new URLOnlyTextHyperlinkExtractor();
		List<Hyperlink> links = extractor.extract(url, content);

		for (Hyperlink l : links)
		{
			assert (l != null);
		}
		assert (links.size() > 1000) : links.size();
		// System.out.println(links.size());

		url = new URL("http://www.csdn.com.cn");
		content = fetcher.fetchContent(url);
		links = extractor.extract(url, content);

		for (Hyperlink l : links)
		{
			assert (l != null);
		}
		assert (links.size() > 400) : links.size();
		// System.out.println(links.size());

		url = new URL("http://www.narutom.com/");
		content = fetcher.fetchContent(url);
		links = extractor.extract(url, content);

		for (Hyperlink l : links)
		{
			assert (l != null);
		}
		assert (links.size() > 100) : links.size();
		// System.out.println(links.size());

	}
}
