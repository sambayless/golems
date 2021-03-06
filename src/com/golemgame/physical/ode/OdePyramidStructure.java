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

import java.util.Collection;
import java.util.Map;

import com.golemgame.model.spatial.shape.PyramidModel;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.PyramidInterpreter;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.structural.structures.decorators.PhysicalDecorator;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.geometry.PhysicsMesh;

public class OdePyramidStructure extends OdePhysicalStructure{
	
	private PyramidInterpreter interpreter;
	
	public OdePyramidStructure(PropertyStore store) {
		super(store);
		interpreter = new PyramidInterpreter(store);
	}
	

	private  PyramidModel physicsModel = null;
	
	public void buildCollidable(CollisionMember collidable) {
	
		 physicsModel = new PyramidModel(true);

		 physicsModel.getLocalTranslation().set(interpreter.getLocalTranslation());
		 physicsModel.getLocalRotation().set(interpreter.getLocalRotation());
	
		physicsModel.getLocalScale().set(interpreter.getPyramidScale());
		 
		collidable.registerCollidingModel(physicsModel);
		collidable.getModel().addChild(physicsModel);
		physicsModel.getListeners().clear();
		physicsModel.updateModelData();
		physicsModel.updateWorldData();
	
		super.getStructuralAppearanceEffect().attachModel(physicsModel);
	}
	

	private transient PhysicsMesh collision = null;
	
	@Override
	public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
			Vector3f store) {

		collision = physicsNode.createMesh("pyramid");
		collision.copyFrom((TriMesh)physicsModel.getSpatial());

		//and construct physics
		
		collision.getLocalTranslation().set(physicsModel.getLocalTranslation());
		collision.getLocalRotation().set(physicsModel.getLocalRotation());
		
		
		collision.setIsCollidable(false);
		
		collision.setMaterial(this.getMaterial());

		physicsNode.attachChild(collision);
		collision.updateWorldVectors();//note: it is critical to update world scale before calculating the volume!

		OdePhysicsComponent jointComp = new OdePhysicsComponent(collision,physicsModel);
		jointComp.setMass( collision.getVolume()*getMaterial().getDensity());
		components.add(jointComp);
		
		store.set(collision.getLocalTranslation());
		return collision.getVolume() * this.getMaterial().getDensity();
	}

	
	public void buildRelationships( Map<OdePhysicalStructure, PhysicsNode> physicsMap, OdePhysicsEnvironment compiledEnvironment) 
	{
		for(PhysicalDecorator decorator:super.getPhysicalDecorators())
			decorator.attach(physicsMap.get(this), collision);
	}
	

	@Override
	public float getLinearResonanceScalar()
	{
		Vector3f scale = interpreter.getPyramidScale();
		return scale.x + scale.y + scale.z;
	}

}
