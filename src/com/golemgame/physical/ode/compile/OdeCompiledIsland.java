package com.golemgame.physical.ode.compile;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.PhysicsNode;

public class OdeCompiledIsland {
	private Set<Joint> joints = new HashSet<Joint>();
	
	private Set<PhysicsNode> nodes = new HashSet<PhysicsNode>();
	
	public void addJoint(Joint joint)
	{
		joints.add(joint);
	}
	
	public boolean containsJoint(Joint joint)
	{
		return joints.contains(joint);
	}
	
	public void addPhyiscsNode(PhysicsNode node)
	{
		nodes.add(node);
	}
	
	public boolean containsPhysicsNode(PhysicsNode node)
	{
		return nodes.contains(node);
	}
	
	public void addAll(Collection<PhysicsNode> nodes)
	{
		for (PhysicsNode node: nodes)
		{
			addPhyiscsNode(node);
		}
	}
	
	public Collection<PhysicsNode> getNodes()
	{
		return this.nodes;
	}
	public Collection<Joint> getJoints()
	{
		return this.joints;
	}

	public void addAllJoints(Collection<Joint> joints) {
		this.joints.addAll(joints);
		
	}
}
