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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class ObservableViewManager extends ViewManager{
	private static final long serialVersionUID = 1L;

	private List<ViewListener> listeners = new CopyOnWriteArrayList<ViewListener>();
	
	public void setViewMode(ViewMode viewMode)
	{
		if(super.getViews().size() != 1 || !super.getViews().contains(viewMode))
		{
			super.clearViews();//avoid extra calls to refresh view for listeners
			addViewMode(viewMode);
		}
		//if (! (views.size() == 1 &&  views.contains(viewMode)))
		//{
			//views.clear();
			//if (views.add(viewMode))
			//	informListeners (views);
		//}
	}
	
	private void informListeners(Collection<ViewMode> views)
	{
		for(ViewListener listener:listeners)
		{
			listener.viewChanged(views);
		}
	}
	
	public boolean addViewMode(ViewMode viewMode)
	{

		if(super.addViewMode(viewMode)){
			informListeners (views);
			return true;
		}
		return false;
	}
	
	public boolean removeViewMode(ViewMode viewMode)
	{
		if(super.removeViewMode(viewMode)){
				informListeners (views);
			return true;
		}
		return false;
	}

	public void registerListener(ViewListener listener)
	{
		if (this.listeners.add(listener))
			listener.viewChanged(views);

	}
	
	public void removeListener(ViewListener listener)
	{
		this.listeners.remove(listener);
	}
	
	public static interface ViewListener
	{
		public void viewChanged(Collection<ViewMode> currentViews );	
	}
	
	private static final List<ViewMode> defaultList;
	static{
		defaultList = new ArrayList<ViewMode>();
		defaultList.add(ViewMode.MATERIAL);
	}
	public void refreshViews() {
		super.refreshView();
		informListeners(views);
		
	}


}
