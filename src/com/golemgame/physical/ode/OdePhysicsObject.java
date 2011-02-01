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

import com.golemgame.physical.PhysicsComponent;
import com.golemgame.physical.PhysicsObject;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jmex.physics.PhysicsNode;

public class OdePhysicsObject implements PhysicsObject{
	private final PhysicsNode physicsNode;
	public PhysicsNode getPhysicsNode() {
		return physicsNode;
	}
	
	public boolean isStatic()
	{
		return this.physicsNode.isStatic();
	}

	private Collection<OdePhysicsComponent> components = new ArrayList<OdePhysicsComponent>();
	
	public OdePhysicsObject(PhysicsNode physicsNode) {
		super();
		this.physicsNode = physicsNode;
	}

	@SuppressWarnings("unchecked")
	public Collection<PhysicsComponent> getComponents() {		
		return ( Collection<PhysicsComponent>) (Collection) components;
	}

	public void addComponent(OdePhysicsComponent comp) {
		comp.setParent(this);
		this.components.add(comp);

	}
	
	public Quaternion getLocalRotation(Quaternion store) {
		store.set(physicsNode.getLocalRotation());
		return store;
	}
	
	public Vector3f getLocalTranslation(Vector3f store) {
		store.set(physicsNode.getLocalTranslation());
		return store;
	}
	
	public Vector3f getWorldTranslation(Vector3f store) {
		store.set(physicsNode.getWorldTranslation());
		return store;
	}
}
