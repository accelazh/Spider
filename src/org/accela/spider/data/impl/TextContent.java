package org.accela.spider.data.impl;

import org.accela.spider.data.Content;

public class TextContent implements Content
{
	private String text = null;

	public TextContent(String text)
	{
		if(null==text)
		{
			throw new IllegalArgumentException("text should not be null");
		}
		
		this.text=text;
	}
	
	@Override
	public String getText()
	{
		return text;
	}

}
