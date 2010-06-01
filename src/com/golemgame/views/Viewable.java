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
