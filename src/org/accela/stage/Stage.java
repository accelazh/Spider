package org.accela.stage;

public interface Stage<InputType>
{
	public void input(InputType input) throws RejectedInputException;
	
	public int getTaskCount();
	
}
