package com.golemgame.settings;


import java.util.HashMap;
import java.util.Map;

import javax.swing.event.EventListenerList;

import com.golemgame.util.input.InputLayer;



public class SettingsEventDispatch {

	private EventListenerList listenerList = new EventListenerList();
	
	private Map<SettingsListener, Integer> layerMap = new HashMap<SettingsListener,Integer>();
	
	/**
	 * Should be called by EventNotifiers each time an Event is generated.
	 * @param event
	 */
	public void generateEvent(SettingChangedEvent<?> event)
	{
	     Object[] listeners = listenerList.getListenerList();	       
	        // Process the listeners last to first, notifying
	        // those that are interested in this event
	        for (int i = listeners.length-2; i>=0; i-=2) {
	          if (listeners[i] == SettingsListener.class)
	          {
	        	  SettingsListener listener = ((SettingsListener)listeners[i+1] );

	        	  int compare = 0;
	        	  if(layerMap.get(listener)!=null)
	        		  compare =layerMap.get(listener);
	        	  if (!InputLayer.get().isCurrentlyBlocked(compare ) )
	        		  listener.valueChanged(event);
	          }
	                   
	        }
	}
	
	public void addSettingListener(SettingsListener listener, int layer)
	{
		layerMap.put(listener, layer);
		listenerList.add(SettingsListener.class, listener);
	}
	public void removeSettingListener(SettingsListener listener)
	{
		layerMap.remove(listener);
		listenerList.remove(SettingsListener.class,listener);
	}
	
}
