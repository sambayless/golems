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
package com.golemgame.physical.ode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.golemgame.functional.component.BComponent;
import com.golemgame.functional.component.BMind;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.View;
import com.golemgame.mvc.golems.MachineInterpreter;
import com.golemgame.mvc.golems.WireInterpreter;
import com.golemgame.physical.PhysicsEnvironment;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.jmex.physics.PhysicsNode;

public class OdePhysicsMachine implements View{
	private MachineInterpreter interpreter;
	public Collection<OdePhysicalStructure> structures;
	
	public OdePhysicsMachine(PropertyStore store) {
		interpreter = new MachineInterpreter(store);
		
		structures = new ArrayList<OdePhysicalStructure>();
		
		
		for(DataType val:interpreter.getStructures().getValues())
		{
			if(!(val instanceof PropertyStore))
				continue;
			
			try{
				OdePhysicalStructure structure = OdeViewFactory.createStructure((PropertyStore)val);
				structures.add(structure);
			}catch(IllegalArgumentException e)
			{
				
			}
			
		}
		
	}
	


	public Collection<OdePhysicalStructure> getPhysicals() {
		return structures;
	}
	
	public void buildMinds(BMind mind,
			Map<OdePhysicalStructure, PhysicsNode> physicalMap,
			Map<Reference, BComponent> wireMap,
			OdePhysicsEnvironment compilationEnvironment) {
		
		for(OdePhysicalStructure structure:structures)
			structure.buildMind(mind, physicalMap, wireMap, compilationEnvironment);

	}



	public void buildConnections(Map<Reference, BComponent> wireMap) {
		
		Collection<PropertyStore> wires = new ArrayList<PropertyStore>();
		for(OdePhysicalStructure structure:structures)
		{
			structure.getWires(wires);
		}

	
			for (PropertyStore wireStore:wires)
			{
				WireInterpreter wire = new WireInterpreter(wireStore);
				Reference inRef = wire.getPortID(true);
				Reference outRef = wire.getPortID(false);
				
				BComponent inputComponent = wireMap.get(inRef);
				BComponent connectComponent = wireMap.get(outRef); 
				
				if(inputComponent == null || connectComponent == null)
					continue;

				connectComponent.attachOutput(inputComponent,wire.isNegative());
			
			}
		
		
	}
	public PropertyStore getStore() {
		return interpreter.getStore();
	}

	public void remove() {
		for(OdePhysicalStructure structure:structures)
			structure.remove();
		structures.clear();
	}
}
