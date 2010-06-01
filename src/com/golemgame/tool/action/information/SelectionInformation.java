package com.golemgame.tool.action.information;

import com.golemgame.tool.action.Action;

public abstract class SelectionInformation extends ActionInformation {

	@Override
	public Type getType() {
		return Action.SELECTINFO;
	}

	public boolean isSelectable()
	{
		return true;
	}
	
	public boolean isMultipleSelectable()
	{
		return true;
	}
}
