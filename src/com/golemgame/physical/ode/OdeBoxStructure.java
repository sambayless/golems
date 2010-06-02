package com.golemgame.physical.ode;

import java.util.Collection;
import java.util.Map;

import com.golemgame.model.spatial.shape.BoxModel;
import com.golemgame.model.texture.TextureTypeKey.TextureShape;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.BoxInterpreter;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.structural.structures.decorators.PhysicalDecorator;
import com.jme.math.Vector3f;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;

public class OdeBoxStructure  extends OdePhysicalStructure{

	protected BoxInterpreter interpreter;
	
	public OdeBoxStructure(PropertyStore store) {
		super(store);
		interpreter = new BoxInterpreter(store);
	}
	
	@Override
	protected TextureShape getPrefferedTextureShape()
	{
		return TextureShape.Box;
	}
	
	protected BoxModel physicsModel = null;
	


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

		super.getStructuralAppearanceEffect().attachModel(physicsModel);
	}
	
	
	
	private PhysicsCollisionGeometry collision = null;
	
	public PhysicsCollisionGeometry getCollisionGeometry()
	{
		return  collision;
	}	
	
	public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
			Vector3f store) {
	
		collision=physicsNode.createBox("box");

		collision.getLocalTranslation().set(physicsModel.getLocalTranslation());
		collision.getLocalRotation().set(physicsModel.getLocalRotation());
		collision.getLocalScale().set(physicsModel.getLocalScale());

		collision.setIsCollidable(false);
		
		collision.setMaterial(getMaterial());

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

		
		final PhysicsCollisionGeometry collision = this.collision;

		
		for(PhysicalDecorator decorator:super.getPhysicalDecorators())
			decorator.attach(physicsMap.get(this), collision);
		
	
	}


	public float getLinearResonanceScalar()
	{
		Vector3f scale = interpreter.getExtent();
		return scale.x*2f + scale.y*2f + scale.z*2f;
	}
	
}
