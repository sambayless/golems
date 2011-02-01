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

import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.model.spatial.shape.BoxModel;
import com.golemgame.model.spatial.shape.ConeFacade;
import com.golemgame.model.spatial.shape.ConeModel;
import com.golemgame.model.texture.TextureTypeKey.TextureShape;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GhostConeInterpreter;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.golemgame.states.physics.ode.PhysicsSpatialMonitor;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.geometry.PhysicsMesh;
import com.jmex.physics.material.Material;

public class OdeGhostCone extends OdeGhostStructure {
	private GhostConeInterpreter interpreter;
	public OdeGhostCone(PropertyStore store) {
		super(store);
		interpreter = new GhostConeInterpreter(store);
	}
	
	//protected SpatialModel physicsModel = null;

	protected  PhysicsMesh meshCollision = null;
	@Override
	protected TextureShape getPrefferedShape()
	{
		return TextureShape.Cone;
	}
/*	@Override
	public void buildCollidable(CollisionMember collidable) {
		 physicsModel = new ConeModel(true);
			physicsModel.getLocalTranslation().set(interpreter.getLocalTranslation());
			physicsModel.getLocalRotation().set(interpreter.getLocalRotation());
			 physicsModel.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI,Vector3f.UNIT_X));

			physicsModel.getLocalScale().set(interpreter.getRadius()*2f,interpreter.getRadius()*2f ,interpreter.getHeight());		

			collidable.registerCollidingModel(physicsModel);
			collidable.getModel().addChild(physicsModel);
			physicsModel.getListeners().clear();
			physicsModel.updateModelData();
			physicsModel.updateWorldData();
		
	}
*/
	@Override
	public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
			Vector3f store) {
		meshCollision = physicsNode.createMesh("cylMesh");
		
		ConeModel physicsModel = new ConeModel();
		physicsModel.getLocalScale().set(interpreter.getRadius()*2f,interpreter.getRadius()*2f,interpreter.getHeight());
		//Vector3f scale = new Vector3f(physicsModel.getLocalScale());
		//physicsModel.getLocalScale().multLocal(1f);
		//physicsModel.updateWorldData();
		meshCollision.copyFrom((TriMesh) ((SpatialModel)physicsModel).getSpatial());
		meshCollision.getLocalTranslation().set(interpreter.getLocalTranslation());
		meshCollision.getLocalRotation().set(interpreter.getLocalRotation());
		meshCollision.getLocalRotation().set(getParentRotation().mult(meshCollision.getLocalRotation()));
	//	getParentRotation().multLocal(meshCollision.getLocalTranslation());
		meshCollision.getLocalRotation().multLocal(getParentRotation());
		meshCollision.getLocalTranslation().addLocal(getParentTranslation());
		
		
		
	
		//physicsModel.getLocalScale().set(scale);
		//physicsModel.updateWorldData();
		//and construct physics
	
		//meshCollision.getLocalTranslation().set(physicsModel.getLocalTranslation());
		//meshCollision.getLocalRotation().set(physicsModel.getLocalRotation());
		
		meshCollision.setMaterial(Material.GHOST);
		PhysicsSpatialMonitor.getInstance().registerGhost(meshCollision);
		
		physicsNode.attachChild(meshCollision);
		
		OdePhysicsComponent comp = new OdePhysicsComponent(meshCollision,null);
		comp.setMass( 0f);
		components.add(comp);
	//	store.set(physicsModel.getLocalTranslation());
		

		return 0;

	}
	
	public PhysicsCollisionGeometry getGhost() {
		return meshCollision;
	}

	public void buildRelationships( Map<OdePhysicalStructure, PhysicsNode> physicalMap, OdePhysicsEnvironment environment) 
	{
	//	meshCollision.getPhysicsNode().setCollisionGroup(environment.getSensorCollisionGroup());
		
		SpatialModel sensorField = new ConeFacade();
		meshCollision.getParent().attachChild(sensorField.getSpatial());
		sensorField.getLocalTranslation().set(this.meshCollision.getLocalTranslation());
		sensorField.getLocalScale().set(this.meshCollision.getLocalScale());
		sensorField.getLocalRotation().set(this.meshCollision.getLocalRotation());
		super.getStructuralAppearanceEffect().attachModel(sensorField);
		sensorField.updateModelData();
		
		sensorField.updateModelData();
		super.getStructuralAppearanceEffect().attachModel(sensorField);
		sensorField.setVisible(false);
		super.setSensorField(sensorField);
		
		
		
		environment.registerSensorSpatial(meshCollision);
	}
}
