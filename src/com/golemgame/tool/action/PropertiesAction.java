package com.golemgame.tool.action;

import java.util.concurrent.Callable;

public class PropertiesAction extends Action {

	protected Callable<?>callback = null;
	/**
	 * Provide a one-time call-back, to be called on completion of do action.
	 * @param callback
	 */
	public void setCallback(Callable<?> callback)
	{
		this.callback = callback;
	}
	
	@Override
	public String getDescription() {
		return "Properties";
	}

	@Override
	public Type getType() {
		return Action.PROPERTIES;
	}

}
