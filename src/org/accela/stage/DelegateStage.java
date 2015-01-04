package org.accela.stage;

public class DelegateStage<InputType> implements Stage<InputType>
{
	private volatile Stage<InputType> stage = null;

	public DelegateStage()
	{
		this(null);
	}

	public DelegateStage(Stage<InputType> stage)
	{
		this.stage = stage;
	}

	public Stage<InputType> getStage()
	{
		return stage;
	}

	public void setStage(Stage<InputType> stage)
	{
		this.stage = stage;
	}

	@Override
	public int getTaskCount()
	{
		return this.stage!=null?this.stage.getTaskCount():0;
	}

	@Override
	public void input(InputType input) throws RejectedInputException
	{
		if (null == input)
		{
			throw new IllegalArgumentException("input should not be null");
		}
		
		if(this.stage!=null)
		{
			this.stage.input(input);
		}
	}
	

}
