package com.golemgame.tool.action.information;

import com.golemgame.tool.action.Action;

public abstract class SelectionPriorityInformation  extends ActionInformation {


	@Override
	public Type getType() {
		return Action.SELECTION_PRIORITY;
	}
	
	/**
	 * Get the priority of the actionable for selection. Higher numbers are selected first, if there
	 * is a competition.
	 * @return
	 */
	public int getSelectionPriority()
	{
		return 0;
	}
	
}
