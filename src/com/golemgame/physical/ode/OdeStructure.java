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
