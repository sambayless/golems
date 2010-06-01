package com.golemgame.tool.action;

/**
 * This action tool has two primary uses: its changes the wire inhibit mode, and it switches to control mode
 * @author Sam
 *
 */
public class ModifyAction extends Action {

	protected boolean modify = false;
	
	@Override
	public Type getType() {
		return Action.MODIFY;
	}

	public boolean isModify() {
		return modify;
	}

	public void setModify(boolean modify) {
		this.modify = modify;
	}
	@Override
	public String getDescription() {
		return "Modify";
	}
}
