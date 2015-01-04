package org.accela.spider.strategy.impl;

import java.io.IOException;

import org.accela.spider.util.URL;
import org.accela.spider.util.URLTextDownloader;

import org.accela.spider.data.Content;
import org.accela.spider.data.impl.TextContent;
import org.accela.spider.strategy.ContentFetcher;

public class TextContentFetcher implements ContentFetcher
{
	@Override
	public Content fetchContent(URL url) throws IOException
	{
		if (null == url)
		{
			throw new IllegalArgumentException("url should not be null");
		}

		URLTextDownloader downloader = new URLTextDownloader();
		return new TextContent(downloader.download(url, "text/html"));
	}

}
