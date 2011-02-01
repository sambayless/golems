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
package com.golemgame.states.machine;

import java.util.concurrent.CopyOnWriteArrayList;

import com.golemgame.mechanical.StructuralMachine;

public class MachineSpaceManager {
	private CopyOnWriteArrayList<MachineEventListener> listeners = new CopyOnWriteArrayList<MachineEventListener>();
	private boolean enabled = false;
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void registerMachineListener(MachineEventListener listener)
	{
		listeners.add(listener);
	}
	
	public void removeMachineListener(MachineEventListener listener)
	{
		listeners.remove(listener);
	}
	
	public void newMachine(StructuralMachine machine )
	{		
		if(!enabled)
			return;
		for(MachineEventListener listener:listeners)
		{
			listener.newMachine(machine);
		}
	}
	
	public void closeMachine(StructuralMachine machine )
	{		
		if(!enabled)
			return;
		for(MachineEventListener listener:listeners)
		{
			listener.machineClosed(machine);
		}
	}
}
