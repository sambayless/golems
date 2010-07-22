package com.golemgame.physical.ode;

import java.util.Collection;
import java.util.Map;

import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.model.spatial.shape.PyramidModel;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GhostPyramidInterpreter;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.golemgame.states.physics.ode.PhysicsSpatialMonitor;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Pyramid;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.geometry.PhysicsMesh;
import com.jmex.physics.material.Material;

public class OdeGhostPyramid extends OdeGhostStructure {
	private GhostPyramidInterpreter interpreter;
	public OdeGhostPyramid(PropertyStore store) {
		super(store);
		interpreter = new GhostPyramidInterpreter(store);
	}
	
	//protected SpatialModel physicsModel = null;

	protected PhysicsCollisionGeometry collision = null;
/*		
	@Override
	public void buildCollidable(CollisionMember collidable) {
		 physicsModel = new PyramidModel(true);

		physicsModel.getLocalTranslation().set(interpreter.getLocalTranslation());
		physicsModel.getLocalRotation().set(interpreter.getLocalRotation());
		physicsModel.getLocalScale().set(interpreter.getPyramidScale());	
		
		collidable.registerCollidingModel(physicsModel);
		collidable.getModel().addChild(physicsModel);
		physicsModel.getListeners().clear();//?
		physicsModel.updateModelData();
		physicsModel.updateWorldData();
		
	}
	*/
	public PhysicsCollisionGeometry getGhost() {
		return collision;
	}

	@Override
	public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
			Vector3f store) {
		
		collision = physicsNode.createMesh("pyramid");
		//collision = physicsNode.createBox("");
		TriMesh pyramid = new Pyramid("",1f,1f);
		pyramid.getLocalScale().set(interpreter.getPyramidScale());
	
		((PhysicsMesh)collision).copyFrom(pyramid);
		collision.getLocalTranslation().set(interpreter.getLocalTranslation());
		collision.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(FastMath.PI, Vector3f.UNIT_X));

		collision.getLocalRotation().multLocal(interpreter.getLocalRotation());
	
		
				collision.getLocalScale().set(interpreter.getPyramidScale());
	//	getParentRotation().multLocal(collision.getLocalTranslation());
	//	collision.getLocalRotation().multLocal(getParentRotation());
				
		
		collision.getLocalRotation().set(getParentRotation().mult(collision.getLocalRotation()));
		getParentRotation().multLocal(collision.getLocalTranslation());	
		collision.getLocalTranslation().addLocal(getParentTranslation());
		
		collision.setIsCollidable(false);
		
		collision.setMaterial(Material.GHOST);
		//collision.setMaterial(Material.CONCRETE);
		PhysicsSpatialMonitor.getInstance().registerGhost(collision);
		physicsNode.attachChild(collision);
		
		OdePhysicsComponent comp = new OdePhysicsComponent(collision,null);
		comp.setMass( 0f);
		components.add(comp);
		
/*		pyramid.getLocalTranslation().set(collision.getLocalTranslation());
		pyramid.getLocalRotation().set(collision.getLocalRotation());
		pyramid.getLocalScale().set(interpreter.getPyramidScale());
		physicsNode.attachChild(pyramid);
		pyramid.updateRenderState();*/
	//	store.set(collision.getLocalTranslation());
		
		return 0;

	}
	
	public void buildRelationships( Map<OdePhysicalStructure, PhysicsNode> physicalMap, OdePhysicsEnvironment environment) 
	{
		
		SpatialModel sensorField = new PyramidModel(false);
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
