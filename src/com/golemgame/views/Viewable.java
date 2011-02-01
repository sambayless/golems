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
package com.golemgame.views;

import java.io.Serializable;
import java.util.Collection;


/**
 * Viewable's can have their appearance (and possibly other properties) changed.
 * This can be used to implement standard appearances for different types of objects.
 * @author Sam
 *
 */
public interface Viewable extends Serializable{
	public static enum ViewMode
	{
		MATERIAL(),FUNCTIONAL(),GHOST(),RUNNING(),SELECTED(),UNSELECTED(),GROUPMODE();
	}
	
	public static final ViewMode MATERIAL = ViewMode.MATERIAL;
	public static final ViewMode FUNCTIONAL = ViewMode.FUNCTIONAL;
	public static final ViewMode RUNNING = ViewMode.RUNNING;
	public static final ViewMode SELECTED = ViewMode.SELECTED;
	public static final ViewMode UNSELECTED = ViewMode.UNSELECTED;
	public static final ViewMode GHOST = ViewMode.GHOST;
	public static final ViewMode GROUPMODE = ViewMode.GROUPMODE;
	/**
	 * Apply this ViewMode to this Viewable.
	 * Automatically refreshes the view afterward.
	 * @param viewMode The ViewMode to apply.
	 */
	public boolean addViewMode(ViewMode viewMode);	
	
	public boolean addViewModes(Collection<ViewMode> viewModes);	
	
	public boolean removeViewMode(ViewMode viewMode);
	
//	public boolean setViewMode(ViewMode viewMode);
	
	public boolean clearViews();
	
	public void refreshView();
	
	public Collection<ViewMode> getViews();
}
