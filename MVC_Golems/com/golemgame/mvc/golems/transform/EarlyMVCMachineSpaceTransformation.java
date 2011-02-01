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
