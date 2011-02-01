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
import java.util.HashMap;
import java.util.Map;

import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.physical.PhysicsObject;
import com.golemgame.physical.PhysicsWorld;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;

public class OdePhysicsWorld implements PhysicsWorld{

	private final SpatialModel spatial;
	private final PhysicsSpace physics;
	public PhysicsSpace getPhysics() {
		return physics;
	}

	private Collection<OdePhysicsObject> physicsObjects = new ArrayList<OdePhysicsObject>();
	private Map<SpatialModel,OdePhysicsComponent> compMap = new HashMap<SpatialModel,OdePhysicsComponent>();
	private Map<PhysicsNode,OdePhysicsObject> physicsMap = new HashMap<PhysicsNode,OdePhysicsObject>();
	private Map<PhysicsCollisionGeometry,OdePhysicsComponent> physicsComponenetMap = new HashMap<PhysicsCollisionGeometry,OdePhysicsComponent>();
	
	public OdePhysicsWorld(PhysicsSpace physics,SpatialModel spatial) {
		super();
		this.physics = physics;
		this.spatial = spatial;
	}

	public SpatialModel getSpatial() {
		return spatial;
	}

	public OdePhysicsObject getPhysicsNode(PhysicsNode node)
	{
		return physicsMap.get(node);
	}
	
	public void addPhysicsObject(OdePhysicsObject object)
	{
		this.physicsObjects.add(object);
		this.physicsMap.put(object.getPhysicsNode(), object);
		for(PhysicsComponent component:object.getComponents())
		{
			OdePhysicsComponent comp = (OdePhysicsComponent) component;
			physicsComponenetMap.put(comp.getCollisionGeometry(), comp);		
			compMap.put(comp.getSpatial(), comp);
		}
	}
	
	public void clear()
	{
		this.physicsComponenetMap.clear();
		this.physicsMap.clear();
		this.physicsObjects.clear();
	}
	
	public PhysicsComponent getComponent(SpatialModel spatial) {
		return compMap.get(spatial);
	}

	public OdePhysicsComponent getPhysicsComponent(PhysicsCollisionGeometry collisionGeom)
	{
		return physicsComponenetMap.get(collisionGeom);
	}
	
	@SuppressWarnings("unchecked")
	public Collection<PhysicsObject> getPhysicsObjects() {
		return (Collection<PhysicsObject> ) (Collection) physicsObjects;
	}
}
