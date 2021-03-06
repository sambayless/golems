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
package com.golemgame.functional.component;

import java.util.Collection;
import java.util.LinkedList;

/**
 * An efficient mapping of signals (floats) to components.
 * @author Sam
 *
 */
public class SignalComponentMap {
	
	private LinkedList<SignalPair> unusedPairs = new LinkedList<SignalPair>();
	private LinkedList<SignalPair> usedPairs = new LinkedList<SignalPair>();
	
	/**
	 * Apply all signals to their destinations
	 */
	public void applyAll()
	{
		while(usedPairs.peek() != null)
		{
			SignalPair pair = usedPairs.remove();
			pair.apply();
			pair.clear();
			unusedPairs.addFirst(pair);
		}
		usedPairs.clear();
	}
	
	public void put(float signal, boolean inhibit, Collection<BComponent> destination)
	{
		SignalPair pair;
		if(unusedPairs.peek() == null)
		{
			pair = new SignalPair();
		}else
		{
			pair = unusedPairs.remove();
		}
		pair.set(signal,inhibit, destination);
		usedPairs.addFirst(pair);
	}
	
	/**
	 * A recyclable class that stores signals and their destinations
	 * @author Sam
	 *
	 */
	private static class SignalPair
	{
		private float signal;
		private Collection<BComponent> destination;
		private boolean inhibit;
		
		public void set(float signal, boolean inhibit, Collection<BComponent> destination)
		{
			this.inhibit = inhibit;
			this.signal = signal;
			this.destination = destination;
		}
		
		public void clear()
		{
			inhibit = false;
			signal = Float.NaN;
			destination = null;
		}
		
		public void apply()
		{
			if(destination!=null)
			{
				for (BComponent comp:destination)
				{
					comp.modulateState(signal, inhibit);
				}
			}
		}
	}
}
