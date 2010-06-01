package com.golemgame.tool.action;

public class DeleteAction extends Action<DeleteAction> {

	@Override
	public Type getType() {
		return Action.DELETE;
	}

	@Override
	public DeleteAction copy() {
		try{
			DeleteAction copy = (DeleteAction) this.clone();
			
			return copy;
		}catch(Exception e)
		{
			return null;
		}
	}
	@Override
	public String getDescription() {
		return "Delete";
	}
}
