package com.golemgame.structural.structures.decorators;

import java.io.Serializable;

import com.golemgame.mvc.SustainedView;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;

public interface PhysicalDecorator extends Serializable,SustainedView{
	
	/**
	 * Attach this decorator to the physics.
	 * Should be called in the build relationships phase of building physics.
	 * @param physicsNode
	 * @param involvedGeometry
	 */
	public void attach(PhysicsNode physicsNode, PhysicsCollisionGeometry[] involvedGeometries);
	
	public void attach(PhysicsNode physicsNode, PhysicsCollisionGeometry involvedGeometry);
	
//	public PhysicalDecorator copy();
}
