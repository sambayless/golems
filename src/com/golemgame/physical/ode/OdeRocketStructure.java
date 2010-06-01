package com.golemgame.physical.ode;

import java.util.Map;

import com.golemgame.constructor.Updatable;
import com.golemgame.constructor.UpdateManager;
import com.golemgame.constructor.UpdateManager.Stream;
import com.golemgame.functional.component.BComponent;
import com.golemgame.functional.component.BMind;
import com.golemgame.functional.component.BRocket;
import com.golemgame.functional.component.BRocket.RocketCallback;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.golems.RocketInterpreter;
import com.golemgame.mvc.golems.WirePortInterpreter;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.golemgame.structural.structures.RocketPropellentProperties;
import com.golemgame.structural.structures.RocketProperties;
import com.golemgame.structural.structures.particles.ParticleEffect;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Disk;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsNode;

public class OdeRocketStructure extends OdeCylinderStructure {
	public static final float MAX_LINEAR_VELOCITY = 300;
	
	private RocketInterpreter interpreter;
	private RocketProperties rocketProperties;
	public OdeRocketStructure(PropertyStore store) {
		super(store);
		interpreter = new RocketInterpreter(store);
		rocketProperties = new RocketPropertiesImpl(interpreter);
		propellentProperties = new RocketPropellentProperties(interpreter.getPropellantProperties());
		propellentProperties.refresh();
	}
	private RocketPropellentProperties propellentProperties= null;
	
	private float getMaxForce() {
		return rocketProperties.getMaxForce();
	}
	
	private float rocketForce = 0f;
	@Override
	public void buildMind(BMind mind,
			Map<OdePhysicalStructure, PhysicsNode> physicalMap,
			Map<Reference, BComponent> wireMap, OdePhysicsEnvironment environment) {
		rocketForce = 0;
		PhysicsNode physicsNode = physicalMap.get(this);
		if (physicsNode != null )
		{
			final Vector3f position = new Vector3f(cylCollision.getLocalTranslation());
			final Vector3f direction = new Vector3f(cylCollision.getLocalRotation().multLocal(new Vector3f(0,0,1)));
			RocketCallback callback;
			if(!physicsNode.isStatic())
			{
				final DynamicPhysicsNode dynamicPhysics = (DynamicPhysicsNode)physicsNode;
				callback = new RocketCallback()
				{
					private Vector3f storeVelocity = new Vector3f();
					private Vector3f storeForce = new Vector3f();
					
					public void applyForce(float forceAmount) {
						Vector3f force = storeForce;
						rocketForce = forceAmount;
						if (Math.abs(forceAmount) <= FastMath.FLT_EPSILON)
							return;
						dynamicPhysics.updateWorldVectors();
							force.set(direction);
							dynamicPhysics.getWorldRotation().mult(force,force); //force is still a unit vector here
						float curVel = force.dot(dynamicPhysics.getLinearVelocity(storeVelocity));
					
						if (curVel<MAX_LINEAR_VELOCITY)
						{
							force.multLocal(forceAmount);
							dynamicPhysics.addForce(force, position);
						}
						
						
					}
				};
			}else
			{
				callback = new RocketCallback()
				{								
					public void applyForce(float forceAmount) {
						rocketForce = forceAmount;
					}
				};
			}
			
		
			WirePortInterpreter in = new WirePortInterpreter(interpreter.getInput());

			BRocket rocket = new BRocket(callback,rocketProperties);
			mind.addComponent(rocket);
			wireMap.put(in.getID(), rocket);
		}
	}
	private Updatable particleUpdate;
	@Override
	public void buildRelationships(
			 Map<OdePhysicalStructure, PhysicsNode> physicsMap, OdePhysicsEnvironment compiledEnvironment) {


		
		if (this.propellentProperties.effectsEnabled() && this.propellentProperties.getRocketEffects().getNumberOfParticles()>0)
		{
			//System.out.println(propellentProperties.getRocketEffects().getStore().formatted());
			
			final ParticleEffect effect = new ParticleEffect(propellentProperties.getRocketEffects().getStore());
			final Spatial physicsModel = super.physicsModel.getSpatial();
			//final Box output = new Box("",new Vector3f(), 3, 3, 3);
			final Disk output = new Disk("", 5,5, 0.5f);
			output.getLocalScale().set(physicsModel.getLocalScale());
			output.setCullMode(SceneElement.CULL_ALWAYS);
			output.getLocalTranslation().set(physicsModel.getLocalTranslation());
			output.getLocalRotation().set(physicsModel.getLocalRotation());
			float offset = physicsModel.getLocalScale().z/2f;
		
			final Vector3f offsetVector =  output.getLocalRotation().mult(new Vector3f(0,0,offset));
			output.getLocalTranslation().addLocal(offsetVector);
			PhysicsNode physicsNode = physicsMap.get(this);
			
			if (physicsNode == null)
				return;
			
			physicsNode.attachChild(effect.getSpatial());
			physicsNode.attachChild(output);
			output.updateModelBound();
			effect.setGeom(output);
			
			output.updateWorldVectors();
			effect.getSpatial().updateWorldVectors();
			
			
			
			
			final Vector3f forwards = new Vector3f(0,0,-1f);
			final Vector3f backwards = new Vector3f(0,0,1f);
			particleUpdate = new Updatable()
			{
	
			
				private boolean firstUpdate = true;
				public void update(float time) {
				
					//System.out.println(rocketForce);
					output.getLocalTranslation().set(physicsModel.getLocalTranslation());
					//effect.setDirection(backwards);
					if (rocketForce<0)
					{
						output.getLocalTranslation().addLocal(offsetVector);
						effect.setDirection(backwards);
					}else
					{
						output.getLocalTranslation().subtractLocal(offsetVector);
						effect.setDirection(forwards);
					}
					
					
					
					//output.getParent().updateWorldVectors();
					//output.updateGeometricState(time, true);
					//effect.getSpatial().updateWorldVectors();
					
					if (getMaxForce() > 0)
					{
						float percent = Math.abs( rocketForce/getMaxForce());
						if (percent< FastMath.FLT_EPSILON)
							percent = 0;
						
						effect.setEngagement(percent);
						
					}else
					{
						effect.disengage();
					}
				
					if(firstUpdate)
					{
						firstUpdate = false;
						effect.init();
					}
					effect.update(time);
			
				}
		
			};
			};
			if(particleUpdate!=null)
				UpdateManager.getInstance().add(particleUpdate,Stream.PHYSICS_RENDER);

	}
		@Override
		public void remove() {
			super.remove();		
			UpdateManager.getInstance().remove(particleUpdate);		
		}
	
		private class  RocketPropertiesImpl implements RocketProperties
		{
			private final float maxForce;
			
			public RocketPropertiesImpl(RocketInterpreter interpreter) {
				super();
				maxForce = interpreter.getMaxAcceleration();
			}

			public float getMaxForce() {
				return maxForce;
			}

			public void setMaxForce(float maxAcceleration) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		
}

