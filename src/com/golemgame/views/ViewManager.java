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
