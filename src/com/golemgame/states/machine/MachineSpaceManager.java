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
