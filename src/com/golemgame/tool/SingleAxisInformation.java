package com.golemgame.tool;

import com.golemgame.tool.action.information.ActionInformation;

public class SingleAxisInformation extends ActionInformation {

	@Override
	public com.golemgame.tool.action.Action.Type getType() {
		return SINGLE_AXIS_INFO;
	}
	
	public boolean useSingleAxis()
	{
		return true;
	}

}
