package com.golemgame.tool.action;


import com.golemgame.util.input.InputLayer;

public class ModalPropertiesAction extends PropertiesAction {
	
	private boolean frozen=false;
	protected void freeze(boolean freeze) 
	{
		frozen = freeze;
		//GameStateManager.getInstance().setActive(!freeze);
		InputLayer.get().setEnabled(!freeze);
		/*
		if (freeze)
		{
			
			GameStateManager.getInstance().deactivateAllChildren();
		}
		else
			GameStateManager.getInstance().activateAllChildren();
*/
	}
	public boolean isFrozen() {
		return frozen;
	}

}
