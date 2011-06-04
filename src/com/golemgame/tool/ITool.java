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
package com.golemgame.tool;

import com.jme.math.Vector2f;

public interface ITool {
	/**
	 * Return true to attempt standard selection behaviour. Return false to skip selection behaviour
	 * @param button
	 * @param pressed
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean mouseButton(int button, boolean pressed, int x, int y);
	
	public void mouseMovementAction(Vector2f mousePos, boolean left, boolean right);
	
	public void  scrollMove(int wheelDelta, int x, int y);
	
	public void deselect();
	
	public void showPrimaryEffect(boolean show);
//	public void createNewUndoPoint();
	
	public void focus();
	public void copy();
	public void delete();
	public void properties();
	public void xyPlane(boolean value);
	public void yzPlane(boolean value);
	public void xzPlane(boolean value);
}
