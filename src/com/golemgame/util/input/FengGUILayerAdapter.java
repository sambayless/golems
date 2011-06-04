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


public class FengGUILayerAdapter implements LayeredKeyInputListener,
		LayeredMouseInputListener {

	private FengJMEListener fengGUIListener;
	
	public FengGUILayerAdapter(FengJMEListener fengGUIListener) {
		super();
		this.fengGUIListener = fengGUIListener;
	}
	
	
	public boolean onKey(char character, int keyCode, boolean pressed) {
		fengGUIListener.onKey(character, keyCode, pressed);
		return fengGUIListener.wasKeyHandled();
	}

	
	public boolean onButton(int button, boolean pressed, int x, int y) {
		fengGUIListener.onButton(button, pressed, x, y);
	//	System.out.println(fengGUIListener.wasMouseHandled());
		return fengGUIListener.wasMouseHandled();
	}

	
	public boolean onMove(int xDelta, int yDelta, int newX, int newY) {
		fengGUIListener.onMove(xDelta, yDelta, newX, newY);
		return fengGUIListener.wasMouseHandled();
	}

	
	public boolean onWheel(int wheelDelta, int x, int y) {
		fengGUIListener.onWheel((int)Math.signum(wheelDelta), x, y);//only pass a single rotation to the listener
		return fengGUIListener.wasMouseHandled();
	}



}
