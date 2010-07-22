package com.golemgame.physical.ode;

import java.util.Collection;
import java.util.Map;

import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.model.spatial.shape.CylinderFacade;
import com.golemgame.model.texture.TextureTypeKey.TextureShape;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GhostCylinderInterpreter;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.golemgame.states.physics.ode.PhysicsSpatialMonitor;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.geometry.PhysicsMesh;
import com.jmex.physics.material.Material;

public class OdeGhostCylinder extends OdeGhostStructure {
	private GhostCylinderInterpreter interpreter;
	public OdeGhostCylinder(PropertyStore store) {
		super(store);
		interpreter = new GhostCylinderInterpreter(store);
	}
	
//	protected SpatialModel physicsModel = null;
	@Override
	protected TextureShape getPrefferedShape()
	{
		return TextureShape.Cylinder;
	}
	protected  PhysicsMesh meshCollision = null;

	protected  PhysicsCollisionGeometry cylCollision = null;
/*	
	@Override
	public void buildCollidable(CollisionMember collidable) {
		//construct a shared visual model from the visual box
		 physicsModel = new CylinderModel(true);

		// collidable.getModel().worldToLocal(cylinderModel.getWorldTranslation(), physicsModel.getLocalTranslation());
		 
		 physicsModel.getLocalTranslation().set(interpreter.getLocalTranslation());
		 physicsModel.getLocalRotation().set(interpreter.getLocalRotation());
		 physicsModel.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI,Vector3f.UNIT_Y));

		 physicsModel.getLocalScale().set(interpreter.getRadius()*2f,interpreter.getRadius()*2f,interpreter.getHeight());
		
		
		collidable.registerCollidingModel(physicsModel);
		collidable.getModel().addChild(physicsModel);
		physicsModel.getListeners().clear();
		physicsModel.updateModelData();
		physicsModel.updateWorldData();

		
	}*/

	@Override
	public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
			Vector3f store) {
		cylCollision = physicsNode.createCylinder("cylinder");

		cylCollision.getLocalTranslation().set(interpreter.getLocalTranslation());
		
		cylCollision.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_X));

		cylCollision.getLocalRotation().multLocal(interpreter.getLocalRotation());
		
		cylCollision.getLocalRotation().set(getParentRotation().mult(cylCollision.getLocalRotation()));
		
	
		
		
		getParentRotation().multLocal(cylCollision.getLocalTranslation());
		cylCollision.getLocalTranslation().addLocal(getParentTranslation());
		
		cylCollision.getLocalScale().set(interpreter.getRadius(),interpreter.getRadius(),interpreter.getHeight());
		
		cylCollision.setIsCollidable(false);
		
		cylCollision.setMaterial(Material.GHOST);
		PhysicsSpatialMonitor.getInstance().registerGhost(cylCollision);
		
		cylCollision.updateWorldVectors();
		
	/*float radius = cylCollision.getLocalScale().x;
		float length = cylCollision.getLocalScale().z;


		
		//Build an aserisk down the middle of the cylinder, out of boxes, to act as the inner, backup cylinder.
		//its radius will be 90% of the actual radius.
		
		{
			float numberOfBoxes = 8;
			float boxRadius = radius * 0.96f;
			float boxLength = length * 0.96f;
			
			float theta = FastMath.PI/numberOfBoxes;	
			
			float halfWidth = FastMath.sin(theta/2f)*boxRadius;
			
			float boxHeight =FastMath.sqrt( boxRadius*boxRadius - halfWidth*halfWidth) * 2f;
			float boxWidth = halfWidth*2f;
		
			for (int i = 0 ;i< numberOfBoxes;i++)
			{
				//create the box, then rotate it into position
				PhysicsBox  box = physicsNode.createBox("CylinderAsterisk" + i);
				box.getLocalScale().set(boxHeight ,boxWidth ,boxLength);
				box.getLocalTranslation().set(cylCollision.getLocalTranslation());
				Quaternion boxRotation = new Quaternion().fromAngleNormalAxis(theta*i,Vector3f.UNIT_Z);
		
				box.getLocalRotation().set(cylCollision.getLocalRotation());
			//	box.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI,Vector3f.UNIT_Y));
				box.getLocalRotation().multLocal(boxRotation);
				box.setIsCollidable(false);
			//	box.setMaterial(this.getMaterial());
				physicsNode.attachChild(box);
				physicsNode.updateWorldVectors();
				box.setMaterial(Material.GHOST);
				PhysicsSpatialMonitor.getInstance().registerGhost(box);
			//	box.getLocalRotation().multLocal(cylCollision.getLocalRotation());
						}			
		}*/
		OdePhysicsComponent comp = new OdePhysicsComponent(cylCollision,null);
		comp.setMass( 0f);
		components.add(comp);
		physicsNode.attachChild(cylCollision);
		cylCollision.updateWorldVectors();//note: it is critical to update world scale before calculating the volume!

		store.set(cylCollision.getLocalTranslation());
	
		return 0;

	}
	
	public PhysicsCollisionGeometry getGhost() {
		return cylCollision;
	}

	public void buildRelationships( Map<OdePhysicalStructure, PhysicsNode> physicalMap, OdePhysicsEnvironment environment) 
	{
	/*	cylCollision.getPhysicsNode().setCollisionGroup(environment.getSensorCollisionGroup());
		if(meshCollision!=null)
			meshCollision.getPhysicsNode().setCollisionGroup(environment.getSensorCollisionGroup());*/
		
		SpatialModel sensorField = new CylinderFacade();
		cylCollision.getParent().attachChild(sensorField.getSpatial());
		sensorField.getLocalTranslation().set(this.cylCollision.getLocalTranslation());
		sensorField.getLocalScale().set(this.cylCollision.getLocalScale());
		sensorField.getLocalScale().x *=2f;
		sensorField.getLocalScale().y *=2f;
		
		sensorField.getLocalRotation().set(this.cylCollision.getLocalRotation());
		super.getStructuralAppearanceEffect().attachModel(sensorField);
		sensorField.updateModelData();
		
		sensorField.updateModelData();
		super.getStructuralAppearanceEffect().attachModel(sensorField);
		sensorField.setVisible(false);
		super.setSensorField(sensorField);

		
		environment.registerSensorSpatial(cylCollision);
	}
}
