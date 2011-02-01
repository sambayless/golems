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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.fenggui.Display;
import org.fenggui.composite.Window;

import com.golemgame.states.StateManager;


public abstract class TabbedWindow  implements IFengGUIDisplayable{

	private final ArrayList<ITab> tabs = new ArrayList<ITab>();
	public ArrayList<ITab> getTabs() {
		return tabs;
	}

	/**
	 * Keep a list of all tabs that have been opened since this window was most recently displayed
	 * Only those tabs will get close signals (As others may not have been initiated). 
	 * Only tabs that have not been opened since the most recent window display will be given open signals.
	 */
	private Set<ITab> openedTabs = new HashSet<ITab>();
	private ITab currentTab = dummyTab;
	public ITab getCurrentTab() {
		return currentTab;
	}

	protected Window window;
	private Object owner;
	protected boolean cancel = false;
	private String title;
	private String description;
	
	private static final ITab dummyTab = new TabAdapter("Dummy")
	{

	
		
	};
	
	/**
	 * Forget which tabs have been opened before.
	 */
	public void clearOpenedTabs()
	{
		openedTabs.clear();
	}
	
	public String getDescription() {
	return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setTitle(String title) {
		this.title = title;
		window.setTitle(title);
	}

	public String getTitle() {
		return title;
	}

	private ArrayList<CloseListener> closeListeners = null;

	
	public void display(Display display) {

		display(display,0);
	}
	
	public void display(final Display display, final int priority)
	{
		openedTabs.clear();
		 cancel=false;
			try{
			StateManager.getGame().executeInGL(new Callable<Object>()
					{
		
						
						public Object call() throws Exception {					
							
							display.addWidget(window,priority);
							display.bringToFront(window);
							display.setFocusedWidget(window);
							if (window.getDisplayY() + window.getHeight()> window.getParent().getSize().getHeight())
								window.getPosition().setY(window.getParent().getSize().getHeight() - window.getHeight());
							display.layout();

							displayTab(getCurrentTab());//refresh the current tab.
							return null;
						}
				
					});
			}catch(Exception e)
			{
				StateManager.logError(e);
			}
	}

	
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
	
	}

	public void addTab(ITab tab) {
		if(tab instanceof EmptyPropertyTab)
			return;
		tabs.add(tab);
		insertTabButton(tab);
		if (tabs.size() == 1)
		{
			displayTab(tab);
		
			
		}
	}
	
	protected abstract void insertTabButton(ITab tab);

	public void displayTab(ITab tab)
	{
		if(tab instanceof EmptyPropertyTab)
			return;
		if(tab==null)
			this.currentTab = dummyTab;
		else
		{
			this.currentTab = tab;
			if (openedTabs.add(tab))
			{
				this.currentTab.open();
			}
		}
		

	}

	public Window getWindow() {
		return window;
	}

	public void close(boolean cancel)
	{
		this.setCancel(cancel);
		this.close();
	}
	
	public void close() {
		try{
			StateManager.getGame().executeInGL(new Callable<Object>()
					{
	
						
						public Object call() throws Exception {					
							if (getWindow().getDisplay() != null)
								TabbedWindow.this.getWindow().getDisplay().removeWidget(TabbedWindow.this.getWindow());
							return null;
						}
				
					});
			}catch(Exception e)
			{
				StateManager.logError(e);
			}
			
			Set<ITab> uniqueSet = new HashSet<ITab>();
			//remove duplicates
			uniqueSet.addAll(openedTabs);
			
			for (ITab tab:uniqueSet)
			{
				tab.close(cancel);				
			}
			
			if (closeListeners != null && ! closeListeners.isEmpty())
			{
				CloseListener[] listenerArray = closeListeners.toArray(new CloseListener[closeListeners.size()]);
				closeListeners.clear();
				for (CloseListener listener:listenerArray)
					listener.close(this);
			
			}
			clearOpenedTabs();
			this.owner = null;
	}

	public boolean isCancel() {
		return cancel;
	}

	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}

	public TabbedWindow() {
		super();
	}

	
	public void addCloseListener(CloseListener listener) {
		if (closeListeners == null)
		{
			closeListeners = new ArrayList<CloseListener>();
		}
		closeListeners.add(listener);
		
	}

	
	public void removeCloseListener(CloseListener listener) {
		if (closeListeners != null)
		{
				closeListeners.remove(listener);
		}
	}

	
	public Object getOwner() {
		return this.owner;
	}

	
	public Object setOwner(Object owner) {
		Object old = this.owner;
		this.owner = owner;
		return old;
	}

}
