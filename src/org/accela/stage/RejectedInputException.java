package org.accela.stage;

public class RejectedInputException extends Exception
{
	private static final long serialVersionUID = 1L;

	public RejectedInputException()
	{
		super();
	}

	public RejectedInputException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public RejectedInputException(String message)
	{
		super(message);
	}

	public RejectedInputException(Throwable cause)
	{
		super(cause);
	}

}
