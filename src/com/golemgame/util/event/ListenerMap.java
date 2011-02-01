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
