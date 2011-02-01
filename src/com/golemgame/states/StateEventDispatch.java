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


import javax.swing.event.EventListenerList;


public class StateEventDispatch {

	private EventListenerList listenerList = new EventListenerList();
	
	/**
	 * Should be called by EventNotifiers each time an Event is generated.
	 * @param event
	 */
	public void generateEvent(StateEvent event)
	{
	     Object[] listeners = listenerList.getListenerList();	       
	        // Process the listeners last to first, notifying
	        // those that are interested in this event
	        for (int i = listeners.length-2; i>=0; i-=2) {
	          if (listeners[i] == StateListener.class)
	          {
	        	  if (event instanceof StateDeactivateEvent)
	        		  ((StateListener)listeners[i+1]).deactivate((StateDeactivateEvent)event);
	        	  else if (event instanceof StateFinishedEvent)
	        		  ((StateListener)listeners[i+1]).finished((StateFinishedEvent)event);
	          }
	                   
	        }
	}
	
	public void addSettingListener(StateListener listener)
	{
		
		listenerList.add(StateListener.class, listener);
	}
	public void removeSettingListener(StateListener listener)
	{
		listenerList.remove(StateListener.class,listener);
	}
	
}
