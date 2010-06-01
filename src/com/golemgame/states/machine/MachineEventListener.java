package com.golemgame.states.machine;

import com.golemgame.mechanical.StructuralMachine;

public interface MachineEventListener {
	public void machineClosed(StructuralMachine machine);
	public void newMachine(StructuralMachine machine);
}
