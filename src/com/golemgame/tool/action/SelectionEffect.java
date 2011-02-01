/*******************************************************************************
 * Copyright 2008, 2009, 2010 Sam Bayless.
 * 
 *     This file is part of Golems.
 * 
 *     Golems is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Golems is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Golems. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
