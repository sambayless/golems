package com.golemgame.tool.action.information;

import com.golemgame.tool.action.Action;

/**
 * Action information class provides supplementary information to action tools
 * @author Sam
 *
 */
public abstract class ActionInformation extends Action<ActionInformation>
{
	protected boolean changed = false;
	
	
	
	@Override
	public final boolean doAction() {
		return false;
	}

	@Override
	public final boolean undoAction() {
		return false;
	}

	public boolean isChanged() {
		if (changed)
		{
			changed= false;
			return true;
		}
		return false;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	@Override
	public String getDescription() {
		return "Information";
	}
}
