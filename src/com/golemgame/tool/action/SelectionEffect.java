package com.golemgame.tool.action;

import com.golemgame.tool.action.information.FocusInformation;
import com.golemgame.tool.action.information.OrientationInformation;

public abstract class SelectionEffect extends Action<SelectionEffect> {

	protected boolean engage = true;
	protected FocusInformation focus = null;
	protected OrientationInformation orientation = null;
	
	@Override
	public String getDescription() {
		
		return "Selection Effect";
	}

	@Override
	public Type getType() {
	
		return Action.SELECTIONEFFECT;
	}
	
	public void setInformation(FocusInformation focus, OrientationInformation orientation)
	{
		this.focus = focus;
		this.orientation = orientation;
	}
	
	public void update()
	{
		
	}
	
	public abstract void setAction(Action.Type type);

	public boolean isEngage() {
		return engage;
	}

	public void setEngage(boolean engage) {
		this.engage = engage;
	}

}
