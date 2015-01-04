package org.accela.spider.util;

import java.io.IOException;

public class ContentUnpreferedException extends IOException
{
	private static final long serialVersionUID = 1L;

	public ContentUnpreferedException()
	{
		super();
	}

	public ContentUnpreferedException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ContentUnpreferedException(String message)
	{
		super(message);
	}

	public ContentUnpreferedException(Throwable cause)
	{
		super(cause);
	}

}
