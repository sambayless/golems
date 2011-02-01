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
package com.golemgame.util.input;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.golemgame.states.StateManager;
import com.jme.input.KeyInputListener;
import com.jme.input.MouseInputListener;
/**
 * A set of prioritized layers to which key and mouse listeners can subscribe.
 * Listeners may optionally report whether they have caught the event or not; a caught event is intercepted and not passed onto lower layers.
 * 
 * 
 * @author Sam
 *
 */
public class InputLayer implements KeyInputListener, MouseInputListener{
	 
	public static final int DEFAULT_LAYER = Integer.MAX_VALUE;
	public static final int SETTINGS_KEY_LAYER = -16;
	public static final int GUI_LAYER = -32;
	
	public static final int MENU_LAYER = -64;
	public static final int STATE_LAYER = 0;
	public static final int TOOL_LAYER = 64;
	public static final int USER_HUD_LAYER = 1024;
	public static final int MAX_PRIORITY =  Integer.MIN_VALUE;
	private static final InputLayer instance = new InputLayer();
	
	private Map<Integer, List<MouseInputListener>> mouseMap = new HashMap<Integer, List<MouseInputListener>>();
	private Map<Integer, List<KeyInputListener>> keyMap = new HashMap<Integer, List<KeyInputListener>>();
	
	private Map<Integer,List<LayeredMouseInputListener>> layeredMouseMap = new HashMap<Integer, List<LayeredMouseInputListener>>();
	private Map<Integer, List<LayeredKeyInputListener>> layeredKeyMap = new HashMap<Integer, List<LayeredKeyInputListener>>();
	
	private List<Integer> sortedMouseLayers = new CopyOnWriteArrayList<Integer>();
	private List<Integer> sortedKeyLayers = new CopyOnWriteArrayList<Integer>();
	
	private long blockLayer = ((long) Integer.MAX_VALUE)+1;
	private long currentBlockingLayer =((long) Integer.MAX_VALUE)+1;
	private boolean enabled = true;
	
	/*
	 * Note: I added locking to this thread without giving it too much thought - could be problems later.
	 */
	private Lock listenerLock = new ReentrantLock();
	
	/**
	 * Add a mouse listener at the specified layer
	 * @param listener
	 * @param layer
	 */
	public void addMouseListener(MouseInputListener listener, int layer)
	{
		listenerLock.lock();
		try{
			if (mouseMap.get(layer)== null)
			{
				mouseMap.put(layer, new CopyOnWriteArrayList<MouseInputListener>());
				sortedMouseLayers.add(layer);
				Integer[] _sortedMouseLayers = sortedMouseLayers.toArray(new Integer[0]);
				Arrays.sort(_sortedMouseLayers);
				//have to do this, because COW arrays cant be sorted directly
				sortedMouseLayers = new CopyOnWriteArrayList<Integer>(_sortedMouseLayers);
			}		
			mouseMap.get(layer).add(listener);
		}finally{
			listenerLock.unlock();
		}
	}
	
	/**
	 * Remove the specified listener.
	 * @param listener
	 * @param layer
	 * @return
	 */
	public boolean removeMouseListener(MouseInputListener listener, int layer)
	{
		listenerLock.lock();
		try{
			List<MouseInputListener> array = mouseMap.get(layer);
			if (array != null && array.remove(listener))
			{
				if (array.isEmpty() && (!layeredMouseMap.containsKey(layer) || layeredMouseMap.get(layer).isEmpty()))
				{	
					layeredMouseMap.remove(layer);
					mouseMap.remove(layer);
					int index = Collections.binarySearch(sortedMouseLayers, layer);
					if (index>=0)
						sortedMouseLayers.remove(index);
				}
				return true;
			}
			return false;
		}finally{
			listenerLock.unlock();
		}
	}
	
	/**
	 * Remove the specified listener.
	 * @param listener
	 */
	public void removeMouseListener(MouseInputListener listener)
	{
		listenerLock.lock();
		try{
		for (int layer:sortedMouseLayers)
		{
			if (removeMouseListener(listener, layer))
				break;			
		}}finally{
			listenerLock.unlock();
		}
	}
	
	/**
	 * Add a mouse listener at the default lowest priority layer
	 * @param listener
	 */
	public void addMouseListener(MouseInputListener listener)
	{
		listenerLock.lock();
		try{
			addMouseListener(listener, DEFAULT_LAYER);
		}finally{
			listenerLock.unlock();
		}
	}
	
	/**
	 * Add a mouse listener at the specified layer
	 * @param listener
	 * @param layer
	 */
	public void addMouseListener(LayeredMouseInputListener listener, int layer)
	{
		listenerLock.lock();
		try{
			if (layeredMouseMap.get(layer)== null)
			{
				layeredMouseMap.put(layer, new CopyOnWriteArrayList<LayeredMouseInputListener>());
				sortedMouseLayers.add(layer);
				Integer[] _sortedMouseLayers = sortedMouseLayers.toArray(new Integer[0]);
				Arrays.sort(_sortedMouseLayers);
				//have to do this, because COW arrays cant be sorted directly
				sortedMouseLayers = new CopyOnWriteArrayList<Integer>(_sortedMouseLayers);
			}		
			layeredMouseMap.get(layer).add(listener);
		}finally
		{
			listenerLock.unlock();
		}
	}
	
	/**
	 * Remove the specified listener.
	 * @param listener
	 * @param layer
	 * @return
	 */
	public boolean removeMouseListener(LayeredMouseInputListener listener, int layer)
	{
		listenerLock.lock();
		try{
			List<LayeredMouseInputListener> array = layeredMouseMap.get(layer);
			if (array != null && array.remove(listener))
			{
	
				if (array.isEmpty() && (!mouseMap.containsKey(layer) || mouseMap.get(layer).isEmpty()))
				{	
					layeredMouseMap.remove(layer);
					mouseMap.remove(layer);
					int index = Collections.binarySearch(sortedMouseLayers, layer);
					if (index>=0)
						sortedMouseLayers.remove(index);
				}
				return true;
			}
			return false;
		}finally{
			listenerLock.unlock();
		}
	}
	
	/**
	 * Remove the specified listener.
	 * @param listener
	 */
	public void removeMouseListener(LayeredMouseInputListener listener)
	{		listenerLock.lock();
	try{
		for (int layer:sortedMouseLayers)
		{
			if (removeMouseListener(listener, layer))
				break;			
		}
	}finally{
		listenerLock.unlock();
	}
	}
	
	/**
	 * Add a mouse listener at the default lowest priority layer
	 * @param listener
	 */
	public void addMouseListener(LayeredMouseInputListener listener)
	{		listenerLock.lock();
	try{
		addMouseListener(listener, DEFAULT_LAYER);
	}finally{
		listenerLock.unlock();
	}
	}
	
	/**
	 * Add a mouse listener at the specified layer
	 * @param listener
	 * @param layer
	 */
	public void addKeyListener(KeyInputListener listener, int layer)
	{		listenerLock.lock();
	try{
		if (keyMap.get(layer)== null)
		{
			keyMap.put(layer, new CopyOnWriteArrayList<KeyInputListener>());
			sortedKeyLayers.add(layer);
			Integer[] _sortedKeyLayers = sortedKeyLayers.toArray(new Integer[0]);
			Arrays.sort(_sortedKeyLayers);
			//have to do this, because COW arrays cant be sorted directly
			sortedKeyLayers = new CopyOnWriteArrayList<Integer>(_sortedKeyLayers);
		
		}		
		keyMap.get(layer).add(listener);
	}finally{
		listenerLock.unlock();
	}
	}
	
	/**
	 * Remove the specified listener.
	 * @param listener
	 * @param layer
	 * @return
	 */
	public boolean removeKeyListener(KeyInputListener listener, int layer)
	{		listenerLock.lock();
	try{
		List<KeyInputListener> array = keyMap.get(layer);
		if (array != null && array.remove(listener))
		{
			if (array.isEmpty()  && (!layeredKeyMap.containsKey(layer) || layeredKeyMap.get(layer).isEmpty()))
			{	
				layeredKeyMap.remove(layer);
				keyMap.remove(layer);
				int index = Collections.binarySearch(sortedKeyLayers, layer);
				if (index>=0)
					sortedKeyLayers.remove(index);
			}
			return true;
		}
		return false;
	}finally{
		listenerLock.unlock();
	}
	}
	
	/**
	 * Remove the specified listener.
	 * @param listener
	 */
	public void removeKeyListener(KeyInputListener listener)
	{		listenerLock.lock();
	try{
		for (int layer:sortedKeyLayers)
		{
			if (removeKeyListener(listener, layer))
				break;			
		}
	}finally{
		listenerLock.unlock();
	}
	}
	
	/**
	 * Add a mouse listener at the default lowest priority layer
	 * @param listener
	 */
	public void addKeyListener(KeyInputListener listener)
	{		listenerLock.lock();
	try{
		addKeyListener(listener, DEFAULT_LAYER);
	}finally{
		listenerLock.unlock();
	}
	}
	
	/**
	 * Add a mouse listener at the specified layer
	 * @param listener
	 * @param layer
	 */
	public void addKeyListener(LayeredKeyInputListener listener, int layer)
	{		listenerLock.lock();
	try{
		if (layeredKeyMap.get(layer)== null)
		{
			layeredKeyMap.put(layer, new CopyOnWriteArrayList<LayeredKeyInputListener>());
			sortedKeyLayers.add(layer);
			Integer[] _sortedKeyLayers = sortedKeyLayers.toArray(new Integer[0]);
			Arrays.sort(_sortedKeyLayers);
			//have to do this, because COW arrays cant be sorted directly
			sortedKeyLayers = new CopyOnWriteArrayList<Integer>(_sortedKeyLayers);
		}		
		layeredKeyMap.get(layer).add(listener);
	}finally{
		listenerLock.unlock();
	}
	}
	
	/**
	 * Remove the specified listener.
	 * @param listener
	 * @param layer
	 * @return
	 */
	public boolean removeKeyListener(LayeredKeyInputListener listener, int layer)
	{		listenerLock.lock();
	try{
		List<LayeredKeyInputListener> array = layeredKeyMap.get(layer);
		if (array != null && array.remove(listener))
		{	
			if (array.isEmpty()&& (!keyMap.containsKey(layer) || keyMap.get(layer).isEmpty()))
			{	
				layeredKeyMap.remove(layer);
				keyMap.remove(layer);
				int index = Collections.binarySearch(sortedKeyLayers, layer);
				if (index>=0)
					sortedKeyLayers.remove(index);
			}
			return true;
		}
		return false;
		
	}finally{
		listenerLock.unlock();
	}
	}
	
	/**
	 * Remove the specified listener.
	 * @param listener
	 */
	public void removeKeyListener(LayeredKeyInputListener listener)
	{		listenerLock.lock();
	try{
		for (int layer:sortedKeyLayers)
		{
			if (removeKeyListener(listener, layer))
				break;			
		}
	}finally{
		listenerLock.unlock();
	}
	}
	
	/**
	 * Add a mouse listener at the default lowest priority layer
	 * @param listener
	 */
	public void addKeyListener(LayeredKeyInputListener listener)
	{
		listenerLock.lock();
		try{
		addKeyListener(listener, DEFAULT_LAYER);
	}finally{
		listenerLock.unlock();
	}
	}
	
	

	
	public void onButton(int button, boolean pressed, int x, int y) {
		if (!enabled)
			return;
		listenerLock.lock();
		try{
		//Copy each array so that it can't be modified during this period
		//Integer[] _sortedMouseLayers = sortedMouseLayers.toArray(new Integer[sortedMouseLayers.size()]);
		
		MouseInputListener[][] _mouseMap = new MouseInputListener[sortedMouseLayers.size()][];
		LayeredMouseInputListener[][] _layeredMouseMap = new LayeredMouseInputListener[sortedMouseLayers.size()][];
		currentBlockingLayer = blockLayer;//reset the current blocking layer
		for (int i = 0; i<sortedMouseLayers.size(); i ++)
		{
			List<MouseInputListener> listeners = mouseMap.get(sortedMouseLayers.get(i));
			if (listeners != null)
				_mouseMap[i] = listeners.toArray(new MouseInputListener[listeners.size()]);
			List<LayeredMouseInputListener> layerListeners = layeredMouseMap.get(sortedMouseLayers.get(i));
			if (layerListeners != null)
				_layeredMouseMap[i] = layerListeners.toArray(new LayeredMouseInputListener[layerListeners.size()]);
		}		
		
		for (int layer = 0; layer<sortedMouseLayers.size(); layer++)
		{
			if (sortedMouseLayers.get(layer)>=blockLayer)
				break;
			if (_mouseMap[layer] != null)
				for (MouseInputListener listener:_mouseMap[layer])
				{
					if (listener != null)
						listener.onButton(button, pressed, x, y);					
				}	
	
			if (_layeredMouseMap[layer] != null)
			{
				boolean layerFlag = false;
				for (LayeredMouseInputListener listener:_layeredMouseMap[layer])
				{
					if (listener != null)
						layerFlag |= listener.onButton(button, pressed, x, y);
				}
				if (layerFlag)
					break;
			}
		}
		}catch(Exception e)
		{
			StateManager.logError(e);
		
		
		}finally{
			listenerLock.unlock();
			
		}
	}

	
	public void onMove(int xDelta, int yDelta, int newX, int newY) {
		if (!enabled)
			return;
		listenerLock.lock();
		try{
		//Copy each array so that it can't be modified during this period
		//Integer[] _sortedMouseLayers = sortedMouseLayers.toArray(new Integer[sortedMouseLayers.size()]);
		MouseInputListener[][] _mouseMap = new MouseInputListener[sortedMouseLayers.size()][];
		LayeredMouseInputListener[][] _layeredMouseMap = new LayeredMouseInputListener[sortedMouseLayers.size()][];
		currentBlockingLayer = blockLayer;//reset the current blocking layer
		for (int i = 0; i <sortedMouseLayers.size(); i ++)
		{
			List<MouseInputListener> listeners = mouseMap.get(sortedMouseLayers.get(i));
			if (listeners != null)
				_mouseMap[i] = listeners.toArray(new MouseInputListener[listeners.size()]);
			List<LayeredMouseInputListener> layerListeners = layeredMouseMap.get(sortedMouseLayers.get(i));
			if (layerListeners != null)
				_layeredMouseMap[i] = layerListeners.toArray(new LayeredMouseInputListener[layerListeners.size()]);
		}		
		
		for (int layer = 0; layer<sortedMouseLayers.size(); layer++)
		{
			if (sortedMouseLayers.get(layer)>=blockLayer)
				break;
			if (_mouseMap[layer] != null)
				for (MouseInputListener listener:_mouseMap[layer])
				{
					if (listener != null)
						listener.onMove(xDelta, yDelta, newX, newY);			
				}	
	
			if (_layeredMouseMap[layer] != null)
			{
				boolean layerFlag = false;
				for (LayeredMouseInputListener listener:_layeredMouseMap[layer])
				{
					if (listener != null)
						layerFlag |= listener.onMove(xDelta, yDelta, newX, newY);
				}
				if (layerFlag)
					break;
			}
		}
		}catch(Exception e)
		{
			StateManager.logError(e);
		
		
		}finally{
		listenerLock.unlock();
		}
	}

	
	public void onWheel(int wheelDelta, int x, int y) {
		if (!enabled)
			return;
		listenerLock.lock();
		try{
		//Copy each array so that it can't be modified during this period
		//Integer[] _sortedMouseLayers = sortedMouseLayers.toArray(new Integer[sortedMouseLayers.size()]);
		MouseInputListener[][] _mouseMap = new MouseInputListener[sortedMouseLayers.size()][];
		LayeredMouseInputListener[][] _layeredMouseMap = new LayeredMouseInputListener[sortedMouseLayers.size()][];
		currentBlockingLayer = blockLayer;//reset the current blocking layer
		for (int i = 0; i<sortedMouseLayers.size(); i ++)
		{
			List<MouseInputListener> listeners = mouseMap.get(sortedMouseLayers.get(i));
			if (listeners != null)
				_mouseMap[i] = listeners.toArray(new MouseInputListener[listeners.size()]);
			List<LayeredMouseInputListener> layerListeners = layeredMouseMap.get(sortedMouseLayers.get(i));
			if (layerListeners != null)
				_layeredMouseMap[i] = layerListeners.toArray(new LayeredMouseInputListener[layerListeners.size()]);
		}		
		
		for (int layer = 0; layer<sortedMouseLayers.size(); layer++)
		{
			if (sortedMouseLayers.get(layer)>=blockLayer)
				break;
			if (_mouseMap[layer] != null)
				for (MouseInputListener listener:_mouseMap[layer])
				{
					if (listener != null)
						listener.onWheel(wheelDelta, x, y);				
				}	
	
			if (_layeredMouseMap[layer] != null)
			{
				boolean layerFlag = false;
				for (LayeredMouseInputListener listener:_layeredMouseMap[layer])
				{
					if (listener != null)
						layerFlag |= listener.onWheel(wheelDelta, x, y);
				}
				if (layerFlag)
					break;
			}
		}
		}catch(Exception e)
		{
			StateManager.logError(e);
		
		
		
		}finally{
			listenerLock.unlock();}
	}

	
	public void onKey(char character, int keyCode, boolean pressed) {
		if (!enabled)
			return;
		listenerLock.lock();
		try{
		//Copy each array so that it can't be modified during this period
		Integer[] _sortedKeyLayers = sortedKeyLayers.toArray(new Integer[sortedKeyLayers.size()]);
		KeyInputListener[][] _keyMap = new KeyInputListener[_sortedKeyLayers.length][];
		LayeredKeyInputListener[][] _layeredKeyMap = new LayeredKeyInputListener[_sortedKeyLayers.length][];
		currentBlockingLayer = blockLayer;//reset the current blocking layer
		for (int i = 0; i < _sortedKeyLayers.length;i ++)
		{
			List<KeyInputListener> listeners = keyMap.get(_sortedKeyLayers[i]);
			if (listeners != null)
				_keyMap[i] = listeners.toArray(new KeyInputListener[listeners.size()]);
			List<LayeredKeyInputListener> layerListeners = layeredKeyMap.get(_sortedKeyLayers[i]);
			if (layerListeners != null)
				_layeredKeyMap[i] = layerListeners.toArray(new LayeredKeyInputListener[layerListeners.size()]);
		}		
		
		for (int layer = 0; layer<_sortedKeyLayers.length; layer++)
		{
			if (_sortedKeyLayers[layer]>=blockLayer)
				break;
			if (_keyMap[layer] != null)
				for (KeyInputListener listener:_keyMap[layer])
				{
					if (listener != null)
						listener.onKey(character, keyCode, pressed);	
				}	
	
			if (_layeredKeyMap[layer] != null)
			{
				boolean layerFlag = false;
				for (LayeredKeyInputListener listener:_layeredKeyMap[layer])
				{
					if (listener != null)
						layerFlag |= listener.onKey(character, keyCode, pressed);
				}
				if (layerFlag)
					break;
			}
		}
		}catch(Exception e)
		{
			StateManager.logError(e);
	
		
		}finally{
			listenerLock.unlock();
			
		}
	}

	public static InputLayer get() {
		return instance;
	}
	
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	/**
	 * Stops input from reaching this layer, or any layer of lower priority than it.
	 * See removeBlockLayer
	 * @param layer
	 */
	public void blockLayer(int layer)
	{
		this.blockLayer= layer;
	}
	
	/**
	 * All layers will now receive input.
	 */
	public void removeBlockLayer()
	{
		this.blockLayer = ((long) Integer.MAX_VALUE) + 1;
	}
	
	/**
	 * Determine if the given layer is blocked or not (by the blocking setting)
	 * @param layer
	 * @return
	 */
	public boolean isBlocked(int layer)
	{		
		return layer >=blockLayer;
	}
	
	/**
	 * Determine if the given layer is blocked on this current listening run or not
	 * @param layer
	 * @return
	 */
	public boolean isCurrentlyBlocked(int layer)
	{
		return layer >=currentBlockingLayer;
	}
	
}



