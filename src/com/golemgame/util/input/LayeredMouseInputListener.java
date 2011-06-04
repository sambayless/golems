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
package com.golemgame.util.input;


public interface LayeredMouseInputListener {
	
	/**
	 * Return true to indicate that no lower priority layers should received this event.
	 * @param button
	 * @param pressed
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean onButton(int button, boolean pressed, int x, int y) ;
	
	/**
	 * *Return true to indicate that no lower priority layers should received this event.
	 * @param xDelta
	 * @param yDelta
	 * @param newX
	 * @param newY
	 * @return
	 */
	public boolean onMove(int xDelta, int yDelta, int newX, int newY);

	/**
	 * Return true to indicate that no lower priority layers should received this event.
	 * @param wheelDelta
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean onWheel(int wheelDelta, int x, int y) ;

}
