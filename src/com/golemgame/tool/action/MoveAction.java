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

public abstract class MoveAction extends Action<MoveAction> {
	
	protected Vector3f position = new Vector3f();
	protected Vector3f oldPosition = new Vector3f();
	
	/**
	 * Some subclasses may need this information
	 */
	protected Vector3f startingPosition = new Vector3f();
	
	public Vector3f getStartingPosition() {
		return startingPosition;
	}

	public void setStartingPosition(Vector3f startingPosition) {
		this.startingPosition = startingPosition;
	}

	@Override
	public Type getType() {
		return Action.MOVE;
	}
	
	public abstract void setPosition(Vector3f position);

	@Override
	public MoveAction copy() {
		try{
			MoveAction copy = (MoveAction) this.clone();
			copy.position = position.clone();
			copy.oldPosition = oldPosition.clone();
			return copy;
		}catch(Exception e)
		{
			return null;
		}		
	}

	@Override
	public MoveAction merge(MoveAction mergeWith) throws ActionMergeException 
	{
		
	
			MoveAction merged = this;
			merged.position = mergeWith.position;
			return merged;
		
	
	}
	@Override
	public String getDescription() {
		return "Move";
	}


}
