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

import com.jme.math.Vector3f;

public abstract class ScaleAction extends Action<ScaleAction> {

	protected Vector3f newScale = new Vector3f();
	protected Vector3f oldScale = new Vector3f();
	
	public abstract void setScale(Vector3f scale);


	@Override
	public ScaleAction copy() {
		try{
			ScaleAction copy = (ScaleAction) this.clone();
			copy.newScale = newScale.clone();
			copy.oldScale = oldScale.clone();
			return copy;
		}catch(Exception e)
		{
			return null;
		}		
	}
	
	@Override
	public String getDescription() {
		return "Scale";
	}

	@Override
	public Type getType() {
		return Type.SCALE;
	}

}
