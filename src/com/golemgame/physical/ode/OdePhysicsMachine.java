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
