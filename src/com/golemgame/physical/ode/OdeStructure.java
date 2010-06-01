package com.golemgame.physical.ode;

import java.util.Map;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.View;
import com.golemgame.mvc.golems.StructureInterpreter;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.jmex.physics.PhysicsNode;

public class OdeStructure implements View{

	//private static final PhysicsCollisionGeometry[] empty = new PhysicsCollisionGeometry[0];
	
	private StructureInterpreter interpreter;
	
	public PropertyStore getStore() {
		return interpreter.getStore();
	}

	public OdeStructure(PropertyStore store)
	{
		interpreter = new StructureInterpreter(store);
	}
	/*public PhysicsCollisionGeometry[] getCollisionGeometry()
	{
		return empty;
	}*/
	
	public void clear()
	{
		
	}
	
	public void buildRelationships( Map<OdePhysicalStructure, PhysicsNode> physicalMap, OdePhysicsEnvironment compiledEnvironment) 
	{
		
	}
	public void remove() {
		
		
	}
}
