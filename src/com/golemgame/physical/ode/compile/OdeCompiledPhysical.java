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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.golemgame.mechanical.CompiledController;
import com.golemgame.model.Model;
import com.golemgame.model.ParentModel;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.physical.ode.OdePhysicsMachineSpace;
import com.golemgame.physical.ode.OdePhysicsWorld;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;

/**
 * This class represents a physical machine that is ready to be added into a
 * physics environment. It can be enabled, disabled, and removed.
 * 
 * @author Sam
 * 
 */
public class OdeCompiledPhysical {

	private OdePhysicsWorld physicsWorld;
	private OdePhysicsEnvironment compiledEnvironment;
	private Collection<PhysicsNode> physicsNodes;
	private Collection<CompiledController> compiledControllers;
	private Map<PhysicsNode, NodeModel> physicsMap;
	private Collection<NodeModel> emptyNodes;
	// private Map<Model,ModelData> dataMap;
	private OdePhysicsMachineSpace machine;

	/**
	 * Reset this machine to its original translations, rotations, etc.
	 */
	public void reset() {
		for (CompiledController controller : compiledControllers) {
			controller.resetPositions();
		}

	}

	public void setEnabled(boolean enabled) {
		for (PhysicsNode physicsNode : physicsNodes) {
			physicsNode.setActive(enabled);
		}
	}

	public void attachToModel(ParentModel parent) {
		for (Model model : physicsMap.values()) {
			parent.addChild(model);
		}
		for (Model model : emptyNodes)
			parent.addChild(model);
	}

	public void detach() {
		for (Model model : physicsMap.values()) {
			if (model.getParent() != null)
				model.getParent().detachChild(model);
		}
		for (Model model : emptyNodes) {
			if (model.getParent() != null)
				model.getParent().detachChild(model);
		}
		this.physicsMap = null;
	}

	public void delete() {
		// this.setEnabled(false);
		machine.remove();
		for (CompiledController controller : this.compiledControllers)
			controller.finish();

		for (PhysicsNode physics : physicsNodes) {
			physics.removeFromParent();

		}
		this.physicsWorld = null;
		this.compiledEnvironment = null;
		this.physicsNodes = null;
		this.compiledControllers = null;
		// this.dataMap = null;
		this.physicsMap = null;
		// Runtime.getRuntime().
	}

	public OdePhysicsWorld getPhysicsWorld() {
		return physicsWorld;
	}

	public OdePhysicsEnvironment getCompiledEnvironment() {
		return compiledEnvironment;
	}

	public OdeCompiledPhysical(OdePhysicsMachineSpace machine, Map<PhysicsNode, NodeModel> physicsMap, Collection<NodeModel> emptyNodes, Collection<CompiledController> compiledControllers,
			OdePhysicsEnvironment compiledEnvironment, OdePhysicsWorld physicsWorld) {
		super();
		this.physicsWorld = physicsWorld;
		this.machine = machine;
		this.compiledControllers = compiledControllers;
		this.physicsNodes = physicsMap.keySet();
		this.physicsMap = physicsMap;
		this.compiledEnvironment = compiledEnvironment;
		this.emptyNodes = emptyNodes;
		// dataMap = new HashMap<Model,ModelData>();
		for (NodeModel model : physicsMap.values()) {
			model.updateWorldData();
			// dataMap.put(model, model.getModelData());
		}

		mapIslands(physicsMap, compiledEnvironment);
		// mapMachines(physicsMap);
	}

	private void mapIslands(Map<PhysicsNode, NodeModel> physicsMap, OdePhysicsEnvironment compiledEnvironment) {

		if (physicsMap.keySet().isEmpty())
			return; // incase there ARE no physics objects

		PhysicsSpace space = physicsMap.keySet().iterator().next().getSpace();

		Map<PhysicsNode, OdeCompiledIsland> islandMap = new HashMap<PhysicsNode, OdeCompiledIsland>();

		for (Joint joint : space.getJoints()) {
			List<PhysicsNode> nodes = new ArrayList<PhysicsNode>(joint.getNodes());
			PhysicsNode staticForJoint = compiledEnvironment.getStaticForJoint(joint);
			if (staticForJoint != null) {
				nodes.add(staticForJoint);
			}

			List<OdeCompiledIsland> islands = new ArrayList<OdeCompiledIsland>();
			for (PhysicsNode node : nodes) {
				if (islandMap.containsKey(node))
					islands.add(islandMap.get(node));
			}

			// if there are more than one island in this group, merge them
			// together
			OdeCompiledIsland island = mergeIslands(islandMap, islands);
			island.addAll(nodes);
			island.addJoint(joint);
			for (PhysicsNode node : nodes)
				islandMap.put(node, island);
		}

		compiledEnvironment.setIslandMap(islandMap);

	}

	private OdeCompiledIsland mergeIslands(Map<PhysicsNode, OdeCompiledIsland> islandMap, List<OdeCompiledIsland> islands) {
		if (islands.size() == 1) {
			return islands.get(0);
		}
		OdeCompiledIsland primaryIsland;
		if (islands.isEmpty())
			primaryIsland = new OdeCompiledIsland();
		else
			primaryIsland = islands.get(0);

		for (OdeCompiledIsland island : islands) {
			if (island == primaryIsland)
				continue;

			primaryIsland.addAll(island.getNodes());
			primaryIsland.addAllJoints(island.getJoints());
			for (PhysicsNode physics : island.getNodes())
				islandMap.put(physics, primaryIsland);
		}

		return primaryIsland;
	}

}
