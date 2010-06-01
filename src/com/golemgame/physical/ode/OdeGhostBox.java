package com.golemgame.physical.ode;

import java.util.Collection;
import java.util.Map;

import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.model.spatial.shape.BoxModel;
import com.golemgame.model.texture.TextureTypeKey.TextureShape;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GhostBoxInterpreter;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.golemgame.states.physics.PhysicsSpatialMonitor;
import com.jme.math.Vector3f;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.material.Material;

public class OdeGhostBox extends OdeGhostStructure {
	private GhostBoxInterpreter interpreter;
	public OdeGhostBox(PropertyStore store) {
		super(store);
		interpreter = new GhostBoxInterpreter(store);
	}
	
//	protected BoxModel physicsModel = null;

	protected PhysicsCollisionGeometry collision = null;
/*	
	@Override
	public void buildCollidable(CollisionMember collidable) {
		physicsModel = new BoxModel(true);

		physicsModel.getLocalTranslation().set(interpreter.getLocalTranslation());
		physicsModel.getLocalRotation().set(interpreter.getLocalRotation());
		physicsModel.getLocalScale().set(interpreter.getExtent()).multLocal(2f);		
		
		collidable.registerCollidingModel(physicsModel);
		collidable.getModel().addChild(physicsModel);
		physicsModel.getListeners().clear();//?
		physicsModel.updateModelData();
		physicsModel.updateWorldData();
		
	}*/
/*
 * 	visualBox.updateWorldData();
		
		//construct a shared visual model from the visual box
		 physicsModel = visualBox.makeSharedModel();

		 physicsNode.worldToLocal(visualBox.getWorldTranslation(), physicsModel.getLocalTranslation());
		 
		physicsModel.getLocalRotation().set(visualBox.getWorldRotation());
		
		physicsModel.getLocalScale().set(visualBox.getWorldScale());
		
		

		physicsModel.getListeners().clear();
		physicsModel.updateModelData();
		physicsModel.updateWorldData();
		physicsModel.setVisible(false);
		physicsNode.attachChild(physicsModel.getSpatial());
		
		collision=physicsNode.createBox("box");
		PhysicsSpatialMonitor.getInstance().registerGhost(collision);
		//and construct physics
		
		collision.getLocalTranslation().set(physicsModel.getLocalTranslation());
		collision.getLocalRotation().set(physicsModel.getLocalRotation());
		collision.getLocalScale().set(physicsModel.getLocalScale());


		collision.setIsCollidable(false);
		
		collision.setMaterial(Material.GHOST);

		physicsNode.attachChild(collision);
		
		store.set(collision.getLocalTranslation());
		
		
 */
	
	
	@Override
	public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
			Vector3f store) {
		collision=physicsNode.createBox("box");

		collision.getLocalTranslation().set(interpreter.getLocalTranslation());
		collision.getLocalRotation().set(interpreter.getLocalRotation());
		
		getParentRotation().multLocal(collision.getLocalTranslation());
		collision.getLocalRotation().set(getParentRotation().mult(collision.getLocalRotation()));
		collision.getLocalTranslation().addLocal(getParentTranslation());
		
		collision.getLocalScale().set(interpreter.getExtent()).multLocal(2f);	
		physicsNode.updateWorldVectors();
		physicsNode.worldToLocal(collision.getLocalTranslation(), collision.getLocalTranslation());

		
		collision.setIsCollidable(false);
		
		collision.setMaterial(Material.GHOST);
		PhysicsSpatialMonitor.getInstance().registerGhost(collision);
		physicsNode.attachChild(collision);
		
		store.set(collision.getLocalTranslation());
		
		OdePhysicsComponent comp = new OdePhysicsComponent(collision,null);
		comp.setMass( 0f);
		components.add(comp);


		

		
		
		return 0;

	}
	

	@Override
	protected TextureShape getPrefferedShape() {
		return TextureShape.Box;
	}


	public PhysicsCollisionGeometry getGhost() {
		return collision;
	}

	public void buildRelationships( Map<OdePhysicalStructure, PhysicsNode> physicalMap, OdePhysicsEnvironment environment) 
	{
		SpatialModel sensorField = new BoxModel(false);
		collision.getParent().attachChild(sensorField.getSpatial());
		sensorField.getLocalTranslation().set(this.collision.getLocalTranslation());
		sensorField.getLocalScale().set(this.collision.getLocalScale());
		sensorField.getLocalRotation().set(this.collision.getLocalRotation());
		super.getStructuralAppearanceEffect().attachModel(sensorField);
		sensorField.updateModelData();
		
		sensorField.updateModelData();
		super.getStructuralAppearanceEffect().attachModel(sensorField);
		sensorField.setVisible(false);
		super.setSensorField(sensorField);

		
	//	collision.getPhysicsNode().setCollisionGroup(environment.getSensorCollisionGroup());
		environment.registerSensorSpatial(collision);
	}
}
