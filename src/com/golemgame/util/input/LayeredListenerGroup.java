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

import java.util.HashSet;
import java.util.Set;

import com.jme.input.KeyInputListener;
import com.jme.input.MouseInputListener;


/**
 * This class collects groups of listeners that receive events from either the mouse or keyboard, so that they can all be added to event sources as a group
 * Just like a normal listener group, except that this one can be set to block input to lower priority layers.
 * @author Sam
 *
 */
public class LayeredListenerGroup  implements LayeredKeyInputListener, LayeredMouseInputListener{

		private Set<KeyInputListener> keyListeners = new HashSet<KeyInputListener>();
		private Set<MouseInputListener> mouseListeners = new HashSet<MouseInputListener>();		
	
		public LayeredListenerGroup(boolean blocking) {
			super();
			this.blocking = blocking;
		}		

		public LayeredListenerGroup() {
			super();
		}

		private boolean blocking = false;
		
		public boolean isBlocking() {
			return blocking;
		}

		public void setBlocking(boolean blocking) {
			this.blocking = blocking;
		}

		public void addKeyListener(KeyInputListener listener)
		{
			keyListeners.add(listener);
		}
		
		public void addMouseListener(MouseInputListener listener)
		{
			mouseListeners.add(listener);
		}		

		public void removeKeyListener(KeyInputListener listener)
		{
			keyListeners.remove(listener);
		}
		
		public void removeMouseListener(MouseInputListener listener)
		{
			mouseListeners.remove(listener);
		}		

		
		public boolean onButton(int button, boolean pressed, int x, int y) {
			for (MouseInputListener input:mouseListeners)
				input.onButton(button, pressed, x, y);		
			return blocking;
		}

		
		public boolean onMove(int xDelta, int yDelta, int newX, int newY) {		
			for (MouseInputListener input:mouseListeners)
				input.onMove(xDelta, yDelta, newX, newY);
			return blocking;
		}

		
		public boolean onWheel(int wheelDelta, int x, int y) {
			for (MouseInputListener input:mouseListeners)
				input.onWheel(wheelDelta, x, y);
			return blocking;
		}

		
		public boolean onKey(char character, int keyCode, boolean pressed) {
			for (KeyInputListener input:keyListeners)
				input.onKey(character, keyCode, pressed);	
			return blocking;
		}	
		
}
