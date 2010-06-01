package com.golemgame.physical.ode;

import java.util.Collection;
import java.util.Map;

import com.golemgame.functional.component.BComponent;
import com.golemgame.functional.component.BMind;
import com.golemgame.functional.component.BTouchSensor;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.golems.DistanceSensorInterpreter;
import com.golemgame.mvc.golems.WirePortInterpreter;
import com.golemgame.mvc.golems.DistanceSensorInterpreter.SensorMode;
import com.golemgame.physical.EnvironmentListener;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.physical.PhysicsEnvironment;
import com.golemgame.physical.ode.compile.OdeCompiledIsland;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.golemgame.states.physics.PhysicsSpatialMonitor;
import com.golemgame.states.physics.PhysicsSpatialMonitor.PhysicsCollisionListener;
import com.golemgame.structural.collision.CollisionMember;
import com.jme.math.Vector3f;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpatial;
import com.jmex.physics.contact.ContactInfo;

public class OdeDistanceSensor extends OdePyramidStructure {
	private DistanceSensorInterpreter interpreter;
	
	private OdeGhostStructure distanceGhost = null;
	public OdeDistanceSensor(PropertyStore store) {
		super(store);
		interpreter = new DistanceSensorInterpreter(store);
		
		PropertyStore ghost = interpreter.getGhost();

		distanceGhost=  OdeViewFactory.constructGhost(ghost);

	}
	
	
	@Override
	public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
			Vector3f store) {
		if(this.distanceGhost != null)
		{
			this.distanceGhost.setParentTranslation(interpreter.getLocalTranslation());
			this.distanceGhost.setParentRotation(interpreter.getLocalRotation());
			this.distanceGhost.buildCollisionGeometries(physicsNode,components, new Vector3f());
		
		}
		
		
		
		return super.buildCollisionGeometries(physicsNode,components, store);
	}

	@Override
	public void buildMind(BMind mind,
			Map<OdePhysicalStructure, PhysicsNode> physicalMap,
			Map<Reference, BComponent> wireMap, final OdePhysicsEnvironment environment) {
		
		final PhysicsNode structuralPhysicsNode = physicalMap.get(this);		
		
		if(this.distanceGhost.getGhost() != null)
		{
			final BTouchSensor touch = new BTouchSensor();
			final boolean ignoreStatics = interpreter.ignoreStatics();
			final SensorMode sensorMode = interpreter.getSensorMode();
			PhysicsCollisionGeometry ghost = this.distanceGhost.getGhost();
			
			
			
			
			
			
			PhysicsSpatialMonitor.getInstance().addPhysicsCollisionListener(ghost,new PhysicsCollisionListener()
			{

				
				public void collisionOccured(ContactInfo info,
						PhysicsSpatial source) {
					
					PhysicsNode node1 = info.getNode1();
					//PhysicsNode node2  = info.getNode2();
					PhysicsSpatial otherSpatial;
					if (source.getPhysicsNode() == node1)
					{
						otherSpatial = info.getGeometry2();
					}else
						otherSpatial = info.getGeometry1();
					
					if(otherSpatial == null)
						return;
					
					if(PhysicsSpatialMonitor.getInstance().isGhost(otherSpatial) || PhysicsSpatialMonitor.getInstance().isGhost(otherSpatial.getPhysicsNode()))
					{
						return;//there may be ghosts that are not sensors
					}
					
					boolean reportContact = (! (ignoreStatics && isStatic(otherSpatial)));
					
					reportContact &= !isSensor(otherSpatial);
					
					switch (sensorMode)
					{
					
						case IGNORE_SIMILAR:
							reportContact &= !isSimilar (otherSpatial);
						break;
						case IGNORE_NON_STATIC:
							reportContact &= isStatic (otherSpatial);
						break;
						case IGNORE_SELF:
							reportContact &= ! isSelf (otherSpatial);
						break;
						
						default:
					}
					if(reportContact)
						touch.notifyOfCollision();
					
				}

				private boolean isStatic(PhysicsSpatial otherSpatial)
				{
					return otherSpatial.getPhysicsNode().isStatic();
				}
				
				private boolean isSelf(PhysicsSpatial otherSpatial) {
					return (otherSpatial.getPhysicsNode() == structuralPhysicsNode);
					
				}

				private boolean isSimilar(PhysicsSpatial otherSpatial) {
					if (isSelf(otherSpatial))
						return true;
					
					OdeCompiledIsland  island = environment.getJointIsland(otherSpatial);
					if (island == null)
						return false;
					return (island.containsPhysicsNode(structuralPhysicsNode));
				}

				private boolean isSensor(PhysicsSpatial otherSpatial) {
					return environment.isSensorSpatial(otherSpatial);
				}
				
			});
			
			WirePortInterpreter out = new WirePortInterpreter(interpreter.getOutput());

			mind.addSource(touch);
			mind.addComponent(touch);
			wireMap.put(out.getID(), touch);
		}
	}


	@Override
	public void buildCollidable(CollisionMember collidable) {
		// TODO Auto-generated method stub
		super.buildCollidable(collidable);
	}


	@Override
	public void buildRelationships(
			Map<OdePhysicalStructure, PhysicsNode> physicsMap,
			OdePhysicsEnvironment compiledEnvironment) {
		if(this.distanceGhost != null)
			this.distanceGhost.buildRelationships(physicsMap, compiledEnvironment);

		compiledEnvironment.addEnvironmentListener(new EnvironmentListener(){

			public void environmentChanged(PhysicsEnvironment source) {
				if(distanceGhost!=null)
				{
					distanceGhost.setShowSensorField(source.isShowSensorFields());
				}
			}
			
		});
	
		super.buildRelationships(physicsMap, compiledEnvironment);
	}
	
	
}
