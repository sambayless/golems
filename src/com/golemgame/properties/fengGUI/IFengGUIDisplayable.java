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
package com.golemgame.properties.fengGUI;

import org.fenggui.Display;



public interface IFengGUIDisplayable {
	/**
	 * Display this component in the FengGUI display
	 * @param display
	 */
	public void display(Display display);
	public void display(Display display,int priority);
	public String getTitle();
	public String getDescription();
	public void setEnabled(boolean enabled);
	
	/**
	 * Add a listener to be notified the next time that this displayable is closed.
	 * The listener will be removed from the displayable after being called.
	 * @param listener
	 */
	public void addCloseListener(CloseListener listener);
	
	public void removeCloseListener(CloseListener listener);
	public Object setOwner(Object owner);
	public Object getOwner();
	/**
	 * Remove this component from the FengGUI display.
	 * Set the owner to null.
	 */
	public void close();
}
