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
