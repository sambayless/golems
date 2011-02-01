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

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class BComponent implements Serializable{
	
	private static final long serialVersionUID = 1;

	public static final float MAX_LINEAR_VELOCITY = 300;
	public static final float MAX_ANGULAR_VELOCITY = 100;
	
	private final Collection<BComponent> positiveConnections = new ArrayList<BComponent>();
	private final Collection<BComponent> inhibittedConnections= new ArrayList<BComponent>();
	//private final Set<BComponent> inhibitConnections = new HashSet<BComponent>();
	private final Set<BComponent> connectsToThis = new HashSet<BComponent>();
		
	/**
	 * A field that contains the sum of inputs received by this component before each update cycle.
	 * This is reset to 0 after updating.
	 */
	protected float state; //Subclasses may choose to use this field if they wish;
	
	/**
	 * Genereate a signal from this component, given its current state.
	 * Subclasses may overried this to change its behaviour.
	 * Resets state to 0.
	 * @param time The time since the last call to update
	 */
	public float generateSignal(float time)
	{			
		if(state>1f)
			state =1f;
		else if(state<-1f)
			state = -1f;
		
		float oldState = state;
		state =0;		
		return oldState;
	}

	

	public Collection<BComponent> getPositiveConnections() {
		return positiveConnections;
	}



	public Collection<BComponent> getInhibittedConnections() {
		return inhibittedConnections;
	}


	/**
	 * Provides limited access to this component's state
	 * @param stateChange
	 */
	public final void modulateState(float stateChange, boolean inhibit)
	{
		state+= stateChange * (inhibit?-1:1);
	}
	
	/**
	 * Inform this component of any components connect TO it. Dont know if this is important.
	 * @param comp
	 * @param attach
	 */
	private final void notifyOfConnection(BComponent comp, boolean attach)
	{
		if (attach)
			connectsToThis.add(comp);
		else
			connectsToThis.remove(comp);
	}

	public final void attachOutput(BComponent comp)//, boolean inhibit)
	{
		this.attachOutput(comp, false);
		
		
	}
	public final void attachOutput(BComponent comp, boolean inhibit)
	{
		comp.notifyOfConnection(this,true);
		if(inhibit)
			this.inhibittedConnections.add(comp);
		else
			this.positiveConnections.add(comp);

	}

	public final boolean removeOutput(BComponent comp)
	{
		comp.notifyOfConnection(this,false);
		return (positiveConnections.remove(comp) || inhibittedConnections.remove(comp));
	}
	

	
	/**
	 * Clear any outputs leading from this component to any others.
	 * @return
	 */
	public boolean clearOutputs()
	{
		boolean success=true;
		Iterator<BComponent> i = connectsToThis.iterator();
		while (i.hasNext())
		{
			BComponent comp = i.next();
			i.remove();
			success &= comp.removeOutput(this);//record if there are any failures
		}
		return success;
	}
	
	public boolean isConnectedTo()
	{
		return !connectsToThis.isEmpty();
	}


	private void writeObject(ObjectOutputStream out) throws IOException
	{
		throw new NotSerializableException();
	}
	private void readObject(ObjectInputStream in) throws IOException
	{
		throw new NotSerializableException();
	}
	
}
