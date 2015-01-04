package org.accela.stage;

public class RejectedOutputException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	public RejectedOutputException()
	{
		super();
	}

	public RejectedOutputException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public RejectedOutputException(String message)
	{
		super(message);
	}

	public RejectedOutputException(Throwable cause)
	{
		super(cause);
	}

}
