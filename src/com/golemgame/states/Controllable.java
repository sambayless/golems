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
package com.golemgame.states;



import com.golemgame.util.ControlledListener;
import com.jme.scene.Node;

public interface Controllable extends ControlledListener {


	public Node getRootNode();
	public Node getHUD();
	/**
	 * Initiate this state for displaying. 
	 */
	public void init();
	
	/**
	 * Remove this state from displaying
	 */
	public void remove();
	
	public void addListener(StateListener listener);
	public void removeListener(StateListener listener);

}
