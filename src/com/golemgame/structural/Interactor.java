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
package com.golemgame.structural;

/**
 * An interface for machine elements that observe user input, such as key strokes.
 * This is intentionally separate from an InputLayer listener,
 * and may later listen to other user events as well.
 * @author Sam
 *
 */
public interface Interactor {
	public boolean onKey(char character, int keyCode, boolean pressed);
/*	public boolean onButton(int button, boolean pressed, int x, int y);
	public boolean onMove(int delta, int delta2, int newX, int newY); 
	public boolean onWheel(int wheelDelta, int x, int y);*/
}
