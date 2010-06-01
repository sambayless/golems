package com.golemgame.tool.action;



public class CreateAction extends Action<CreateAction> {

	@Override
	public Type getType() {
		return Action.CREATE;
	}

	@Override
	public CreateAction copy() {
		try{
			CreateAction copy = (CreateAction) this.clone();
			
			return copy;
		}catch(Exception e)
		{
			return null;
		}
	}
	@Override
	public String getDescription() {
		return "Create";
	}
}
