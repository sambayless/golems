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

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.golemgame.functional.component.BComponent;
import com.golemgame.functional.component.BMind;
import com.golemgame.mechanical.CompiledController;
import com.golemgame.mechanical.MachineSpaceSettings;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.View;
import com.golemgame.mvc.golems.MachineSpaceInterpreter;
import com.golemgame.mvc.golems.SurfacePropertiesInterpreter;
import com.golemgame.mvc.golems.SurfacePropertiesInterpreter.SurfaceType;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.physical.ode.compile.OdeCompiledPhysical;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.golemgame.physical.ode.sound.OdeSoundManager;
import com.golemgame.physical.sound.SoundComponent;
import com.golemgame.states.GeneralSettings;
import com.golemgame.states.StateManager;
import com.golemgame.structural.collision.CollisionManager;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.structural.collision.GroupDivider;
import com.golemgame.structural.collision.NonPropagatingCollisionMember;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jphya.body.Body;
import com.jphya.distance.DistanceModel;
import com.jphya.distance.InverseSquareDistanceModel;
import com.jphya.resonator.CompoundResontaor;
import com.jphya.resonator.ModalData;
import com.jphya.resonator.ModalResonator;
import com.jphya.scene.Scene;
import com.jphya.surface.FunctionSurface;
import com.jphya.surface.WhiteFunction;

public class OdePhysicsMachineSpace implements View{
	//this is the top level ode representation

	//private OdePhysicsWorld odePhysicsWorld;
	public final static float SPEED_SOUND_AIR = 343f;
	private static DistanceModel inverseDistance = new InverseSquareDistanceModel();
	
	private MachineSpaceInterpreter interpreter;	
	
	private Collection<OdePhysicsMachine> machines = new ArrayList<OdePhysicsMachine>();
	
	private MachineSpaceSettings machineSpaceSettings;
	
	public OdePhysicsMachineSpace(PropertyStore store) {
		interpreter = new MachineSpaceInterpreter(store);
		for (DataType val:interpreter.getMachines().getValues())
		{
			
			if(!(val instanceof PropertyStore))
				continue;
			
			OdePhysicsMachine machine = new OdePhysicsMachine((PropertyStore)val);
			machines.add(machine);
		}
		
		
			machineSpaceSettings = new MachineSpaceSettings(interpreter.getSettings());
			machineSpaceSettings.refresh();
		
	}
	
	public Vector3f getDirectionalGravity()
	{
	
		return machineSpaceSettings.getGravitySettings().getGravity();
	}
	
	public OdeCompiledPhysical compilePhysics(PhysicsSpace space,	Map<OdePhysicalStructure,PhysicsNode> physicalMap, OdePhysicsEnvironment compiledEnvironment )
	{
		/*int t = 0;
		long[] times = new long[11];
		times[t++] = System.nanoTime();*/
		
		CollisionManager manager = new CollisionManager();
		Map<CollisionMember,OdePhysicalStructure> collisionMap = new HashMap<CollisionMember,OdePhysicalStructure>();
		ArrayList<OdePhysicalStructure> physicalList = new ArrayList<OdePhysicalStructure>();
		manager.getModel().setUpdateLocked(false);

		//times[t++] = System.nanoTime();
		
		
		for(OdePhysicsMachine machine:machines)
		{
			ArrayList<OdePhysicalStructure> substructures = new ArrayList<OdePhysicalStructure>();
			
			for(OdePhysicalStructure physical:machine.getPhysicals())
			{
				substructures.addAll(physical.getSubstructures());
			}
			
			physicalList.addAll(machine.getPhysicals());
			physicalList.addAll(substructures);
			for (OdePhysicalStructure physical:physicalList)
			{
				//a physical structure may be composed of more than one actual, independent compoennt (ie, a spring).
				//need to allow for multiple components in a single structure...
				//this whole method should deal with components, not structures...
				//ok, so let an ode structure generate sub-structures first
				
				NodeModel model = new NodeModel();
				
				CollisionMember physicsCollision;
				if(physical.isPropagating())
					physicsCollision = new CollisionMember(model,null);
				else
					physicsCollision = new NonPropagatingCollisionMember(model,null);
				
				manager.addCollisionMember(physicsCollision);
				physicsCollision.getModel().setUpdateLocked(false);
				physical.buildCollidable(physicsCollision);
				if (physicsCollision.getCollisionModels().isEmpty())
				{
					physicsCollision.delete();
					continue;//dont make empty physics models.
				}
				collisionMap.put(physicsCollision,physical);
			}
			
		}
	//	times[t++] = System.nanoTime();
		//this is by far the slowest part (as would be expected)
		manager.resolveAll();
	//	times[t++] = System.nanoTime();
	//	times[t++] = System.nanoTime();
		Map<PhysicsNode,NodeModel> physicsNodes = new HashMap<PhysicsNode,NodeModel>();
				
		/*
		 * This list will collect any physics collision groups that have no collision geometries in them
		 * These elements will be added to a static, non-physical node.
		 */
		List<NodeModel> emptyGroups = new ArrayList<NodeModel> ();
		
		
		physicalMap.clear();
		
		OdePhysicsWorld physicsWorld = new OdePhysicsWorld(space,StateManager.getFunctionalState().getRootModel());
	
		Collection<Collection<CollisionMember>> groups = manager.getCollisionGroups(propagationDivider);
	//	times[t++] = System.nanoTime();
		Map<PhysicsCollisionGeometry,PhysicsComponent> compMap = new HashMap<PhysicsCollisionGeometry,PhysicsComponent>();
		for (Collection<CollisionMember> group:groups)
		{
			if (group.isEmpty())
				continue;//dont build physics if the group is (somehow) empty
			
			boolean isStatic = false;
			for (CollisionMember member:group)
			{	
				OdePhysicalStructure physical = collisionMap.get(member);
				if (physical != null)
					isStatic |= physical.isStatic();
			}
			
			PhysicsNode physicsNode;
			if (isStatic)
				physicsNode = space.createStaticNode();
			else
				physicsNode = space.createDynamicNode();
			
			NodeModel model = buildPhysicsModel(physicsNode);
			physicsNodes.put(physicsNode,model);
			Vector3f centerOfMass = new Vector3f();
			float totalMass = 0;
			ArrayList<PhysicsComponent> components = new ArrayList<PhysicsComponent>();
			for (CollisionMember member:group)
			{
				components.clear();
				if (member.getModel() instanceof NodeModel)
				{
					NodeModel pModel = (NodeModel) member.getModel();
					
					Node oldParent = pModel.getNode();
					if (oldParent.getChildren()!= null)
					{
						for (Spatial child:oldParent.getChildren().toArray(new Spatial[oldParent.getQuantity()]))
						{
							child.updateWorldVectors();
							
							SpatialModel.giveSpatialAway(child, physicsNode);
							
						}
					}
					
					OdePhysicalStructure physical = collisionMap.get(member);
					
					//center mass here...
					
					if (physical != null)
					{		
						physicalMap.put(physical,physicsNode);
						Vector3f center = new Vector3f();
						float mass = physical.buildCollisionGeometries(physicsNode,components, center);
						totalMass += mass;
						centerOfMass.addLocal(center.multLocal(mass));
					}
					
					for(PhysicsComponent comp:components)
					{
						compMap.put(((OdePhysicsComponent)comp).getCollisionGeometry(), comp);
						if(StateManager.SOUND_ENABLED) {
							if (GeneralSettings.getInstance().getSoundEnabled().isValue()) {
								physical.buildSound((OdePhysicsComponent)comp);
							}
						}
					}
			
				
					
				}
				for(PhysicsComponent comp:components)
				{
					compMap.put(((OdePhysicsComponent)comp).getCollisionGeometry(), comp);
				}
			}
			
			if(!hasPhysicsGeometries(physicsNode))
			{
				NodeModel nonPhysicalNode = new NodeModel();
				while(physicsNode.getChildren()!= null &&! physicsNode.getChildren().isEmpty() )
					nonPhysicalNode.getNode().attachChild(physicsNode.getChildren().get(0));
				/*
				 * 	for (CollisionMember member:group)
				{
					nonPhysicalNode.addChild(member.getModel());
				}
				 */
				emptyGroups.add(nonPhysicalNode);
				physicsNode.delete();
				physicsNodes.remove(physicsNode);
				physicsNode.removeFromParent();
				
				continue;//dont include the physics node if it has no physics objects.
			}
			if(totalMass != 0)
				centerOfMass.divideLocal(totalMass);
		
			physicsNode.getLocalTranslation().addLocal(centerOfMass);
			physicsNode.updateWorldVectors();
			if (physicsNode.getChildren()!= null)
			{
				for (Spatial spatial:physicsNode.getChildren())
				{
					spatial.getLocalTranslation().subtractLocal(centerOfMass);
					spatial.updateWorldVectors();
					spatial.updateWorldBound();
				}
			}
			physicsNode.updateWorldBound();
	
			
		}
	//	times[t++] = System.nanoTime();
		ArrayList<CompiledController> controllers = new ArrayList<CompiledController>();
		
		for (PhysicsNode physicsNode:physicsNodes.keySet().toArray(new PhysicsNode[]{}))
		{
			try{
			if (!physicsNode.isStatic())
			{
				DynamicPhysicsNode dynamic = (DynamicPhysicsNode) physicsNode;
				
				
				dynamic.computeMass();
				
			}
			}catch(IllegalStateException e)
			{//thrown if there are no collision geometries
				//kill the physics node, attach any children to the root
				physicsNodes.remove(physicsNode);
				//shouldn't be reached, normally.
				
			}
		}
		
/*		for(PhysicsNode p:physicsNodes.keySet())
		{	//default collision properties: statics and nonstatics can only collide with each other	
				for (Spatial s:p.getChildren())
				{
					if(s instanceof PhysicsCollisionGeometry)
					{
						((OdeGeometry) s).getOdeGeom().setCategoryBits(0);
						((OdeGeometry) s).getOdeGeom().setCollideBits(0);// | 1<<1);
						if(p.isStatic())
						{
							((OdeGeometry) s).getOdeGeom().setCategoryBits(1);
							((OdeGeometry) s).getOdeGeom().setCollideBits(1);// | 1<<1);
							System.out.println(((OdeGeometry) s).getOdeGeom().getCategoryBits());
							System.out.println(((OdeGeometry) s).getOdeGeom().getCollideBits());
							
						}else{
							((OdeGeometry) s).getOdeGeom().setCategoryBits(1);
							((OdeGeometry) s).getOdeGeom().setCollideBits(0);// | 1<<0);
							System.out.println(((OdeGeometry) s).getOdeGeom().getCategoryBits());
							System.out.println(((OdeGeometry) s).getOdeGeom().getCollideBits());
							
						}
					}
				}
			
		}
		*/
		
		compiledEnvironment.setContactGroups( OdeContactManager.buildContactGroups(physicalList,physicalMap,compMap));
		
		
	//	times[t++] = System.nanoTime();
		for (OdePhysicalStructure physical:physicalList)
		{
			 physical.buildRelationships(physicalMap, compiledEnvironment);
	
		}
//		times[t++] = System.nanoTime();
		for (PhysicsNode physicsNode:physicsNodes.keySet())
		{
			if (!physicsNode.isStatic())
			{
				DynamicPhysicsNode dynamic = (DynamicPhysicsNode) physicsNode;
				dynamic.computeMass();
				
			}
		}
		
		for(PhysicsNode node:physicsNodes.keySet())
		{
			OdePhysicsObject object = new OdePhysicsObject(node);
			for(Spatial s:node.getChildren())
			{
				if(s instanceof PhysicsCollisionGeometry)
				{
					PhysicsComponent comp = compMap.get(s);
					if(comp instanceof OdePhysicsComponent)
					{
						object.addComponent((OdePhysicsComponent) comp);
						
					}
				}
			}
			if(StateManager.SOUND_ENABLED) {
				if (compiledEnvironment.getSoundScene()!=null && GeneralSettings.getInstance().getSoundEnabled().isValue()) {
					buildSoundComponents(object,compMap,compiledEnvironment.getSoundScene());
				}
			}
			physicsWorld.addPhysicsObject(object);
		}
		
	//	times[t++] = System.nanoTime();
		OdeCompiledPhysical compiled = new OdeCompiledPhysical(this,physicsNodes,emptyGroups,controllers, compiledEnvironment,physicsWorld);
	//	times[t++] = System.nanoTime();
		
/*		for (int i = 1;i<t;i++)
			System.out.println("Time " + i + ":" + (times[i] - times[i-1])/ 1000000.0 );*/
		return compiled;
	}
	
	@SuppressWarnings("unchecked")
	private void buildSoundComponents(OdePhysicsObject physics, Map<PhysicsCollisionGeometry, PhysicsComponent> compMap, Scene soundScene) {
		
		//Sort the components objects into sets by sound class.
		//within each set: Tally up the combined width, height, length.
		//finally, produce a sound component for each sound type invovled, add them all to the physics object.
		
		ArrayList<OdePhysicsComponent>[] soundComps = (ArrayList<OdePhysicsComponent>[]) new ArrayList[SurfacePropertiesInterpreter.SurfaceType.values().length];
		
		for(PhysicsComponent comp:physics.getComponents())
		{
			if(comp instanceof OdePhysicsComponent)
			{
				ArrayList<OdePhysicsComponent> comps = soundComps[((OdePhysicsComponent)comp).getSoundSurface().ordinal()];
				if(comps == null)
				{
					comps = new ArrayList<OdePhysicsComponent>();
					soundComps[((OdePhysicsComponent)comp).getSoundSurface().ordinal()] = comps;
				}
				
				comps.add((OdePhysicsComponent)comp);		
				//((OdePhysicsComponent)comp).setSoundComponent(physical.constructSoundComponent(compiledEnvironment.getSoundScene(),(OdePhysicsComponent)comp));
			}
		}
		
		//we may want various special sound properties associated with a particular component in the future (in particular, a surface).
		
	
		
		//have to change jphya like so: A body can be associated wtih mutliple surfaces, AND with multiple resonators.
		//all resonators are simultaneously activated...
		//CompoundResontaor totalResonator = new CompoundResontaor(soundScene);
		//plan: ditch the compound resonator. Have separate bodies for each surface type; otherwise add them up the same way.
		//boolean hasResonance = false;
		for(SurfaceType surface:SurfaceType.values())
		{
			ArrayList<OdePhysicsComponent> comps = soundComps[surface.ordinal()];
			if(comps == null)
				continue;
			
			ModalData localData = OdeSoundManager.getModalData(surface);
			if(localData == null)
				continue;
			
			Body soundBody = new Body();
			
			ModalResonator localResonator = new ModalResonator(soundScene);
			localResonator.setData(localData);
			
			float soundSize = 0;
			for(OdePhysicsComponent comp:comps)
			{
				//get a surface type for this component.
				//associate that surface with this component; each contact will use the surface for the contacting elements.
				soundSize += comp.getLinearResonanceSize();
			}
			float freq =  1f/(soundSize);//this is modelled roughly as a box
			localResonator.setAuxFreqScale(freq/(SoundComponent.getBaseBoxFrequency()));//multiply by two, just because that gets the sound into a really nice range, and its pretty arbitrary anyways
			localResonator.setAuxAmpScale(0.05f);
			soundBody.setResonator(localResonator);
			//totalResonator.addResonator(localResonator);
	
			WhiteFunction whitefun = new WhiteFunction();
			FunctionSurface whitesurf = new FunctionSurface(soundScene);
		//	if(surface==SurfaceType.TIN)
		//		whitesurf.setHardness(0.8f);
			
			whitesurf.setFun(whitefun); // White noise surface texture.
			whitesurf.setContactMasterGain(32000.0f); 
			whitesurf.setCutoffFreqAtRoll(10.0f); 												
			whitesurf.setCutoffFreqRate(1000.0f); 												
			whitesurf.setCutoffFreq2AtRoll(10.0f); 
			whitesurf.setCutoffFreq2Rate(1000.0f);
			whitesurf.setContactDirectGain(0.0f); 											
			whitesurf.setContactAmpLimit(1000);
			
			
			soundBody.setSurface(whitesurf);
			soundBody.setDistanceModel(inverseDistance);
			soundBody.setCurrentDistance(Float.NaN);
			
			SoundComponent soundComp = new SoundComponent(physics);
			soundComp.setBody(soundBody);
			for(PhysicsComponent comp:comps)
			{
				((OdePhysicsComponent)comp).setSoundComponent(soundComp);
			}
		}
	
		
	
	}

	private boolean hasPhysicsGeometries(PhysicsNode physicsNode) {
		if(physicsNode.getChildren() == null)
			return false;
		
		for (Spatial child:physicsNode.getChildren())
		{
			if (child instanceof PhysicsCollisionGeometry)
			{
				return true;
			}
		}
		return false;
	}
	


	
	private NodeModel buildPhysicsModel(final PhysicsNode physicsNode)
	{
		NodeModel physicsModel = new NodeModel()
		{
			private static final long serialVersionUID = 1L;
			
			protected Spatial buildSpatial() {
				return physicsNode;
			}
			private void writeObject(ObjectOutputStream out) throws IOException
			{
				throw new NotSerializableException();
			}
			private void readObject(ObjectInputStream in) throws IOException
			{
				throw new NotSerializableException();
			}
			
		};
		physicsModel.setUpdateLocked(false);
		return physicsModel;
	}
	
	private final static GroupDivider propagationDivider = new GroupDivider()
	{
		
		public boolean dividesGroup(CollisionMember member)
		{
			return false;
		}
		
		
		public boolean propagatesGroup(CollisionMember member) {
			return member.propagatesCollisions();
		}


		public boolean isGroupable(CollisionMember member) {
			return true;
		}
		
		
	};

	public void buildMinds(BMind mind,
			Map<OdePhysicalStructure, PhysicsNode> physicalMap,
			Map<Reference, BComponent> wireMap,
			OdePhysicsEnvironment compilationEnvironment) {
		
		for (OdePhysicsMachine machine:machines)
			machine.buildMinds(mind, physicalMap, wireMap, compilationEnvironment);
		
	}


	public void buildConnections(Map<Reference, BComponent> wireMap) {
		for (OdePhysicsMachine machine:machines)
			machine.buildConnections(wireMap);
	}

	public PropertyStore getStore() {
		return interpreter.getStore();
	}

	public void remove() {
		for(OdePhysicsMachine machine:this.machines)
			machine.remove();
		machines.clear();
	}


	



}
