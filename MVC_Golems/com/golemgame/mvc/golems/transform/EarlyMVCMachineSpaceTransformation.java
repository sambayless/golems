package com.golemgame.mvc.golems.transform;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.MachineSpaceInterpreter;

public class EarlyMVCMachineSpaceTransformation extends Transformation {
	
	public EarlyMVCMachineSpaceTransformation() {
		super("0.53.7");		
	}
	
	public final static String MACHINES_OLD = "MachineSpace.machines";
	@Override
	public void apply(PropertyStore store) {
	//	MachineSpaceInterpreter machineSpace = new MachineSpaceInterpreter(store);
		if(store.hasProperty(MACHINES_OLD))
		{
			store.setProperty(MachineSpaceInterpreter.MACHINES, store.getProperty(MACHINES_OLD));
			store.nullifyKey(MACHINES_OLD);
		}
	}

}
