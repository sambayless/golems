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
