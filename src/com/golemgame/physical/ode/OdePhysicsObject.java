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
