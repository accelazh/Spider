package org.accela.spider.strategy;

import java.io.IOException;
import org.accela.spider.util.URL;

import org.accela.spider.data.Content;

public interface ContentFetcher
{
	//if you find the content is not what you want, e.g. not text format, 
	//you can just throw a ContentUnwantedException.
	public Content fetchContent(URL url) throws IOException;
}
