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

import java.util.Collection;
import java.util.HashSet;

/**
 * A view manager manages a list of registered viewables.
 * @author Sam
 *
 */
public class ViewManager implements Viewable {

	private static final long serialVersionUID = 1L;

	private Collection<Viewable> viewables = new HashSet<Viewable>();
	protected Collection<ViewMode> views = new HashSet<ViewMode>();
	
	public boolean addViewMode(ViewMode viewMode) {
		if(views.add(viewMode)){
			for(Viewable v:viewables)
				v.addViewMode(viewMode);
			return true;
		}
		return false;
	}

	public boolean addViewModes(Collection<ViewMode> viewModes) {
		if(views.addAll(viewModes)){
			for(Viewable v:viewables)
				v.addViewModes(viewModes);
			return true;
		}
		return false;
	}

	public boolean clearViews() {
		if(!views.isEmpty()){
			views.clear();
			for(Viewable v:viewables)
				v.clearViews();
			return true;
		}
		return false;
	}

	public Collection<ViewMode> getViews() {
		return views;
	}

	public void refreshView() {
		for(Viewable v:viewables)
			v.refreshView();
	}

	public boolean removeViewMode(ViewMode viewMode) {
		if(this.views.remove(viewMode)){
			for(Viewable v:viewables)
				v.removeViewMode(viewMode);
			return true;
		}
		return false;
	}
	
	public boolean registerViewable(Viewable viewable)
	{
	//	if(!viewables.contains(viewable))
		if(this.viewables.add(viewable))
		{
			//viewable.refreshView();//this is dangerous, it can call refresh view while things are still being set up
			return true;
		}
		return false;
	}
	public void removeViewable(Viewable viewable)
	{
		this.viewables.remove(viewable);
	}

}
