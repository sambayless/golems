package com.golemgame.physical.ode;

import java.util.Collection;
import java.util.Map;

import com.golemgame.model.spatial.shape.CapsuleCombinedFacade;
import com.golemgame.model.spatial.shape.CapsuleCombinedModel;
import com.golemgame.model.texture.TextureTypeKey.TextureShape;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.CapsuleInterpreter;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.structural.structures.decorators.PhysicalDecorator;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;

public class OdeCapsuleStructure extends OdePhysicalStructure{

	private CapsuleInterpreter interpreter;
	
	public CapsuleInterpreter getInterpreter() {
		return interpreter;
	}


	public OdeCapsuleStructure(PropertyStore store) {
		super(store);
		interpreter = new CapsuleInterpreter(store);
	}
	
	protected CapsuleCombinedModel physicsModel;
/*	private transient SpatialModel physicsCylinder = null;
	private transient SpatialModel physicsLeftCap = null;
	private transient SpatialModel physicsRightCap= null;*/
	//private transient NodeModel physicsInternalNode = null;
	@Override
	protected TextureShape getPrefferedTextureShape()
	{
		return TextureShape.Capsule;
	}

	
	
	public void buildCollidable(CollisionMember collidable) {
	

		float radius = interpreter.getRadius();
		float height = interpreter.getHeight();
		if(height<0f)
			height = 0f;
		//can improve this so that it just uses a proper cylinder or sphere only.
		
		//construct a shared visual model from the visual box

		physicsModel = new CapsuleCombinedModel(true);
		

		
		collidable.getModel().updateWorldData();
		
		physicsModel.getLocalRotation().set(interpreter.getLocalRotation());
		physicsModel.getLocalTranslation().set(interpreter.getLocalTranslation());
		

		collidable.getModel().addChild(physicsModel);
			
		physicsModel.updateWorldData();
		
		if(interpreter.getHeight()>0)
			collidable.registerCollidingModel(physicsModel.getCyl());
		
		collidable.registerCollidingModel(physicsModel.getLeft());
		collidable.registerCollidingModel(physicsModel.getRight());
		
		physicsModel.getListeners().clear();
		physicsModel.getCyl().getListeners().clear();
		physicsModel.getLeft().getListeners().clear();
		physicsModel.getRight().getListeners().clear();
		
		physicsModel.rebuild(radius,height+radius*2f);
	
		CapsuleCombinedFacade capsuleFacade  = new CapsuleCombinedFacade();
		capsuleFacade.loadModelData(physicsModel);
		capsuleFacade.rebuild(radius,height+radius*2f);
		collidable.getModel().addChild(capsuleFacade);
		capsuleFacade.updateWorldData();
		
		super.getStructuralAppearanceEffect().attachModel(capsuleFacade);
	}

	protected PhysicsCollisionGeometry collision;
	
	
	public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
			Vector3f store) {
		float radius = interpreter.getRadius();
		float height = interpreter.getHeight();
		
		if (height>0f)
		{
			collision=physicsNode.createCapsule("Capsule");
	
			//and construct physics
			
			collision.getLocalTranslation().set(physicsModel.getLocalTranslation());
			collision.getLocalRotation().set(physicsModel.getLocalRotation());
			collision.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI,Vector3f.UNIT_Y));

			collision.getLocalScale().set(physicsModel.getCyl().getLocalScale());
			collision.getLocalScale().x/=2f;
			collision.getLocalScale().y/=2f;
			
		}else//this is actually a sphere
		{
			collision = physicsNode.createSphere("CapsuleSphere");
			collision.getLocalTranslation().set(physicsModel.getLocalTranslation());
		//	collision.getLocalRotation().set(capsuleModel.getLocalRotation());
			
			collision.getLocalScale().set(radius,radius,radius);
		
		}
	
		collision.setIsCollidable(false);
		collision.setMaterial(this.getMaterial());

		physicsNode.attachChild(collision);
		
		store.set(collision.getLocalTranslation());
		
		collision.updateWorldVectors();//note: it is critical to update world scale before calculating the volume!

		OdePhysicsComponent comp = new OdePhysicsComponent(collision,physicsModel);
		comp.setMass( collision.getVolume() * this.getMaterial().getDensity());
		components.add(comp);
		
		return collision.getVolume() * this.getMaterial().getDensity();
	}

	
	public void buildRelationships( Map<OdePhysicalStructure, PhysicsNode> physicsMap, OdePhysicsEnvironment compiledEnvironment) 
	{
		for(PhysicalDecorator decorator:super.getPhysicalDecorators())
			decorator.attach(physicsMap.get(this), collision);
	}

	public float getLinearResonanceScalar()
	{
		return interpreter.getHeight() + 4f *interpreter.getRadius();
	}


}
