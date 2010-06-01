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
