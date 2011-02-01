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

import com.golemgame.model.Model;
import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.model.spatial.shape.ConeFacade;
import com.golemgame.model.spatial.shape.ConeModel;
import com.golemgame.model.texture.TextureTypeKey.TextureShape;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.ConeInterpreter;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.structural.structures.decorators.PhysicalDecorator;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.geometry.PhysicsMesh;

public class OdeConeStructure extends OdePhysicalStructure {
	private ConeInterpreter interpreter;
	
	public OdeConeStructure(PropertyStore store) {
		super(store);
		interpreter = new ConeInterpreter(store);
	}
	

	private  SpatialModel physicsModel = null;
	protected  PhysicsMesh meshCollision = null;

	
	public void buildCollidable(CollisionMember collidable) {
	
		
		
		//construct a shared visual model from the visual box
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
		
		Model physicsFacade = new ConeFacade();
		
		physicsFacade.getLocalTranslation().set(physicsModel.getLocalTranslation());
		physicsFacade.getLocalScale().set(physicsModel.getLocalScale());
		physicsFacade.getLocalRotation().set(physicsModel.getLocalRotation());
		collidable.getModel().addChild(physicsFacade);
		
		super.getStructuralAppearanceEffect().attachModel(physicsFacade);
	}
	
	@Override
	protected TextureShape getPrefferedTextureShape()
	{
		return TextureShape.Cone;
	}
	
	
	public void buildRelationships( Map<OdePhysicalStructure, PhysicsNode> physicsMap, OdePhysicsEnvironment compiledEnvironment) 
	{

/*		PhysicsCollisionGeometry[] involved = new PhysicsCollisionGeometry[cylinders.length + 2];
		                   
		for(int i= 0; i< cylinders.length;i++)
		{
			involved[i] = cylinders[i];
		};
		
		involved[involved.length-2] =meshCollision;
		involved[involved.length-1] =cylCollision;
*/
		for(PhysicalDecorator decorator:super.getPhysicalDecorators())
			decorator.attach(physicsMap.get(this), meshCollision);
	}


	//private transient PhysicsCollisionGeometry[]  cylinders = null;
	
	
	public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
			Vector3f store) {


		meshCollision = physicsNode.createMesh("cylMesh");
		Vector3f scale = new Vector3f(physicsModel.getLocalScale());
		physicsModel.getLocalScale().multLocal(1f);
		physicsModel.updateWorldData();
		meshCollision.copyFrom((TriMesh) ((SpatialModel)physicsModel).getSpatial());
		physicsModel.getLocalScale().set(scale);
		physicsModel.updateWorldData();
		//and construct physics
	
		meshCollision.getLocalTranslation().set(physicsModel.getLocalTranslation());
		meshCollision.getLocalRotation().set(physicsModel.getLocalRotation());
	//	collision.getLocalScale().set(shared.getLocalScale());

	//	meshCollision.setIsCollidable(false);
		meshCollision.setMaterial(getMaterial());
		//meshCollision.setMaterial(MaterialWrapper.WEIGHTLESS);
		physicsNode.attachChild(meshCollision);
		
		OdePhysicsComponent comp = new OdePhysicsComponent(meshCollision,physicsModel);
		comp.setMass( meshCollision.getVolume() * this.getMaterial().getDensity());
		components.add(comp);
		
		Vector3f cm = new Vector3f(0,-interpreter.getHeight()/4f,0);
		interpreter.getLocalRotation().multLocal(cm);
		cm.addLocal(interpreter.getLocalTranslation());
		store.set(cm);
		return meshCollision.getVolume() * this.getMaterial().getDensity();
	}
	
	public float getLinearResonanceScalar()
	{
		return interpreter.getHeight() + 4f *interpreter.getRadius();
	}
	/*public static PhysicsCollisionGeometry[] coneApproximation(float radius, float height, PhysicsNode physicsNode, int detail)
	{
		
		 * A second approximation, which is better in a lot of ways, but only works for the second half of the cylinder (when the width is less that the length
		 * is to use capsules. These have alot of advantages over cylinders.
		 * 
		 * 
		 
		
		
		PhysicsCollisionGeometry[] cylinders = new PhysicsCollisionGeometry[detail+1];
		if (detail <= 0)
			detail = 1;
		

		
		float heightPerSection = height/(detail + 1);
		
		for (int i = 1; i<detail + 1; i++)
		{
			float bottom = heightPerSection * (i);
			float top = heightPerSection * (i+1);
			
			float currentRadius = radius/height * bottom;
			
			PhysicsCylinder section = physicsNode.createCylinder("Cone Section" + i);
			section.getLocalScale().x = currentRadius;
			section.getLocalScale().y = currentRadius;
			section.getLocalScale().z = top-bottom;
			section.getLocalTranslation().z -= height/2f;
			section.getLocalTranslation().z += (bottom+top)/2f;
			cylinders[i-1]=section;
		}
		
		PhysicsSphere end = physicsNode.createSphere("Cone end");
		
	
		float position = heightPerSection;
		
		float sphereRadius = radius/height * position;
		
		end.getLocalTranslation().z -= height/2f;
		end.getLocalTranslation().z += position;
		end.getLocalScale().set(sphereRadius, sphereRadius, sphereRadius);
		cylinders[detail] = end;
		return cylinders;
		
	}
	
*/
}
