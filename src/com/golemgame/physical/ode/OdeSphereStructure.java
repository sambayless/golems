package com.golemgame.physical.ode;

import java.util.Collection;
import java.util.Map;

import com.golemgame.model.spatial.shape.SphereFacade;
import com.golemgame.model.spatial.shape.SphereModel;
import com.golemgame.model.texture.TextureTypeKey.TextureShape;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.SphereInterpreter;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.structural.structures.decorators.PhysicalDecorator;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.geometry.PhysicsMesh;

public class OdeSphereStructure  extends OdePhysicalStructure{

	private SphereInterpreter interpreter;
	
	public SphereInterpreter getInterpreter() {
		return interpreter;
	}

	public OdeSphereStructure(PropertyStore store) {
		super(store);
		interpreter = new SphereInterpreter(store);
	}
	
	@Override
	protected TextureShape getPrefferedTextureShape()
	{
		return TextureShape.Sphere;
	}
	
	protected SphereModel physicsModel = null;
	protected SphereFacade physicsFacade = null;
	
	public void buildCollidable(CollisionMember collidable) {

		physicsModel = new SphereModel(true);

		physicsModel.getLocalTranslation().set(interpreter.getLocalTranslation());
		
		physicsModel.getLocalRotation().set(interpreter.getLocalRotation());
		
		if(interpreter.isEllipsoid()){
			physicsModel.getLocalScale().set(interpreter.getExtent()).multLocal(2f);		
		}else{
			//sometimes these are not the same, so this is important.
			physicsModel.getLocalScale().set(interpreter.getRadius(),interpreter.getRadius(),interpreter.getRadius()).multLocal(2f);
		}
		
		collidable.registerCollidingModel(physicsModel);
		collidable.getModel().addChild(physicsModel);
		physicsModel.getListeners().clear();//?
		physicsModel.updateModelData();
		physicsModel.updateWorldData();
		
		physicsFacade = new SphereFacade();
		physicsFacade.loadModelData(physicsModel);
		collidable.getModel().addChild(physicsFacade);
		
		super.getStructuralAppearanceEffect().attachModel(physicsFacade);
	}
	

	private transient PhysicsCollisionGeometry collision = null;
	public PhysicsCollisionGeometry getCollision() {
		return collision;
	}

	@Override
	public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
			Vector3f store) {

		if (interpreter.isEllipsoid())
		{
			collision = physicsNode.createMesh("sphere");
			((PhysicsMesh)collision).copyFrom((TriMesh)physicsModel.getSpatial());
		}else
		{
			collision = physicsNode.createSphere("sphere");
		}
		//and construct physics
		
		collision.getLocalTranslation().set(physicsModel.getLocalTranslation());
		collision.getLocalRotation().set(physicsModel.getLocalRotation());
		if (!interpreter.isEllipsoid())
		{
			
			physicsModel.getSpatial().updateWorldVectors();
			collision.getLocalScale().set(physicsModel.getSpatial().getWorldScale()).multLocal(0.5f);
			collision.updateWorldVectors();
		}

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
		if(interpreter.isEllipsoid())
		{
			Vector3f scale = interpreter.getExtent();
			return scale.x*2f + scale.y*2f + scale.z*2f;	
		
		}else{
			return interpreter.getRadius()*6f;
		}
	}


}
