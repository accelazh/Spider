package org.accela.spider.strategy.impl;

import org.accela.spider.util.URL;

import org.accela.common.Assertion;
import org.accela.spider.strategy.Hyperlink;

public class URLOnlyHyperlink extends Object implements Hyperlink
{
	private URL url = null;

	public URLOnlyHyperlink(URL url)
	{
		if (null == url)
		{
			throw new IllegalArgumentException("url should not be null");
		}

		this.url = url;
	}

	@Override
	public URL getURL()
	{
		assert (this.url != null) : Assertion.declare();
		return this.url;
	}

}
