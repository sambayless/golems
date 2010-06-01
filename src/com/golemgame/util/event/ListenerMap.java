package com.golemgame.util.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This is a map of listener lists, each mapped to a different object.
 * @author Sam
 *
 */
public class ListenerMap<K,V> implements Iterable<K>{
	
	private Map<K,List<V>> listenerMap = new HashMap<K,List<V>>();
	
	private List<K> listenerObjects = new CopyOnWriteArrayList<K>();
	
	public void clear()
	{
		listenerObjects.clear();
		listenerMap.clear();
	}
	
	public void addListener(K source, V listener)
	{
	
	
		List<V> list = listenerMap.get(source);
			if(list == null)
			{
				list = new ArrayList<V>();
				listenerMap.put(source, list);
			}
			if(!listenerObjects.contains(source))
				listenerObjects.add(source);
			list.add(listener);

	}
	
	public void removeListener(K source, V listener)
	{
		
		List<V> list = listenerMap.get(source);
			if(list != null)
			{
				list.remove(listener);
				if(list.isEmpty())
				{
					listenerMap.remove(source);
					listenerObjects.remove(source);
				}
				
			}

	}
	
	public List<V> getListeners(K source)
	{
		return(listenerMap.get(source));
	}

	
	public Iterator<K> iterator() {
		return listenerObjects.iterator();
	}
	
	
	
	
	
	
	
}
