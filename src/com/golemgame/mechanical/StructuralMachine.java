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
package com.golemgame.mechanical;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.golemgame.functional.Wire;
import com.golemgame.functional.Wires;
import com.golemgame.mechanical.layers.LayerRepository;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.ReferenceMap;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.FunctionalInterpreter;
import com.golemgame.mvc.golems.GolemsClassRepository;
import com.golemgame.mvc.golems.LayerInterpreter;
import com.golemgame.mvc.golems.LayerRepositoryInterpreter;
import com.golemgame.mvc.golems.MachineInterpreter;
import com.golemgame.mvc.golems.PhysicalStructureInterpreter;
import com.golemgame.mvc.golems.StructureInterpreter;
import com.golemgame.mvc.golems.WireInterpreter;
import com.golemgame.mvc.golems.WirePortInterpreter;
import com.golemgame.physical.PhysicsEnvironment;
import com.golemgame.states.StateManager;
import com.golemgame.structural.DesignViewFactory;
import com.golemgame.structural.Structural;
import com.golemgame.structural.collision.CollisionManager;
import com.golemgame.structural.group.GroupManager;
import com.golemgame.structural.structures.PhysicalStructure;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.action.mvc.AddComponentAction;
import com.golemgame.tool.action.mvc.AddWireAction;
import com.golemgame.tool.action.mvc.CopyComponentAction;
import com.golemgame.tool.action.mvc.MergeComponentAction;
import com.golemgame.tool.action.mvc.RemoveComponentAction;
import com.golemgame.views.ViewManager;
import com.golemgame.views.Viewable;
import com.golemgame.views.Viewable.ViewMode;


public class StructuralMachine implements Serializable, Viewable,Actionable,SustainedView {
	private static final long serialVersionUID = 1L;
	
	private PhysicsEnvironment environment;
	private  NodeModel model;

	//private transient PhysicalCollisionSpace physicalCollisionSpace= null;
	
	private CollisionManager structuralManager;
//	private CollisionManager physicalManager;
	
	private Wires wires;
	
	//private Map<PropertyStore,Structural> structures = new HashMap<PropertyStore,Structural>();
	//private Collection<Structural> structures = new HashSet<Structural>();
	private Map<Reference,WeakReference<Structural>> structureReferenceMap = new WeakHashMap<Reference,WeakReference<Structural>>();
	private Map<PropertyStore,WeakReference<Structural>> structureStoreMap = new WeakHashMap<PropertyStore,WeakReference<Structural>>();

	private GroupManager groupManager;
	private LayerRepository layerRepository;
	private ViewManager viewManager = new ViewManager();
	public ViewManager getViewManager() {
		return viewManager;
	}

	public void setViewManager(ViewManager viewManager) {
		this.viewManager = viewManager;
	}
	private MachineSpace space;
	
	public MachineSpace getSpace() {
		return space;
	}

	public void setSpace(MachineSpace space) {
		this.space = space;
	}

	public Wires getWires() {
		return wires;
	}
	private MachineInterpreter interpreter;
	
	public StructuralMachine(MachineInterpreter interpreter)
	{
		this.interpreter = interpreter;
		this.environment = new PhysicsEnvironment();
		model = new NodeModel();
	
		interpreter.getStore().setSustainedView(this);
		
		structuralManager = new CollisionManager();
		this.model.addChild(structuralManager.getModel());
		wires = new Wires();
		wires.addToMachine(this);
		
	//	groupManager = new GroupManager();
	//	physicalManager = new CollisionManager(new NodeModel());

	}
	
	public GroupManager getGroupManager()
	{
		return groupManager;
	}
	
	public Collection<Structural> getAllUngroupdStructures()
	{
		Collection<Structural> ungrouped = new ArrayList<Structural>();
		Collection<Structural> grouped = groupManager.getAllGroupedStructures();
		for(Structural structure:getStructures())
		{
			if (! grouped.contains(structure))
				ungrouped.add(structure);
		}
		return ungrouped;
	}
	
	/*public void clearViews() {
		views.clear();
		for (Viewable viewable:viewables)
		{
			if(viewable != null)
				viewable.clearViews();	
		}
		refreshView();
	}*/
	
	/**
	 * Merge the given machine into this one.
	 * Note: this is destructive; the machine being merged into this cannot be used afterwards.
	 * @param mergeWith
	 *//*
	public void merge(StructuralMachine mergeWith)
	{

		
		Set<Model> collisionIgnore = new HashSet<Model>();
		for(Structural structural:mergeWith.getStructures())
		{
			try{
				CollisionMember member = ((CollisionInformation)	structural.getActionable().getAction(Action.COLLISION_INFO)).getCollisionMember();
				collisionIgnore.addAll(member.getCollisionModels());
			}catch(ActionTypeException e2)
			{
				
			}
		}
		

		
		for(ViewMode view:getViews())
		{
			for (Structural viewable: getStructures())
			{
				//should clear the old view mode too...
				viewable.clearViews();
				viewable.addViewMode(view);
			}
			wires.clearViews();
			wires.addViewMode(view);
		}
	
		
	
	//	structuralManager.markAllAsNeedingResolution();
	////	this.structuralManager.resolveAll();
		this.getModel().updateModelData();
		//structuralManager.markAllAsNeedingResolution();
		
	
		//this.structuralManager.resolveAll();
	
		
	}*/
	
	
	public PhysicsEnvironment getEnvironment() {
		return environment;
	}

	public void registerStructural(Structural structural)
	{
		this.structureReferenceMap.put(structural.getID(), new WeakReference<Structural>( structural));
		this.structureStoreMap.put(structural.getStore(), new WeakReference<Structural>( structural));
	}

	public void removeStructural(Structural structural)
	{
		this.structureReferenceMap.remove(structural.getID());
		this.structureStoreMap.remove(structural.getStore());
	}

	public NodeModel getModel() {
		return model;
	}

	public void addToSpace(MachineSpace space)
	{
		this.setSpace(space);
		space.getModel().addChild(getModel());
	}
	
	public void removeFromSpace(MachineSpace space)
	{
		this.setSpace(null);
		space.getModel().detachChild(getModel());
	}
	
	public Collection<Structural> getStructures() {
		ArrayList<Structural> tempList = new ArrayList<Structural>();
		for(WeakReference<Structural> s:structureReferenceMap.values())
		{
			Structural struc = s.get();
			if(struc!=null)
				tempList.add(struc);
		}
		return tempList;
	}

	public CollisionManager getStructuralManager() {
		return structuralManager;
	}
	
	/*
	public void addViewMode(ViewMode viewMode) {

			
		if (views.add(viewMode))
		{
			for (Viewable viewable:getStructures())
			{
				if(viewable != null)
				{
					viewable.addViewMode(viewMode);	
					
	
				}
			}
			wires.addViewMode(viewMode);
		}
	}

	public void removeViewMode(ViewMode viewMode) {
		if (views.remove(viewMode))
		{
			for (Viewable viewable:getStructures())
			{
				if(viewable != null)
					viewable.removeViewMode(viewMode);	
			}
			wires.removeViewMode(viewMode);
		}
	}



	public Collection<ViewMode> getViews() {
		return views;
	}

	public void refreshView() {
		for (Viewable viewable:getStructures())
		{
			if(viewable != null)
				viewable.refreshView();
		}
		wires.refreshView();
	}

	public void addViewModes(Collection<ViewMode> viewModes) {
		for(ViewMode viewMode:viewModes )
			addViewMode(viewMode);
	}*/

	public boolean addViewMode(ViewMode viewMode) {
		this.wires.addViewMode(viewMode);
		return viewManager.addViewMode(viewMode);
		
	}

	public boolean addViewModes(Collection<ViewMode> viewModes) {
		this.wires.addViewModes(viewModes);
		return viewManager.addViewModes(viewModes);
	}
	public boolean clearViews() {
		this.wires.clearViews();
		return viewManager.clearViews();
	}
	

	public Collection<ViewMode> getViews() {
		return viewManager.getViews();
	}

	public void refreshView() {
		viewManager.refreshView();
		this.wires.refreshView();
	}

	public boolean removeViewMode(ViewMode viewMode) {
		
		this.wires.removeViewMode(viewMode);
		return viewManager.removeViewMode(viewMode);
	}

	public Action<?> getAction(Type type) throws ActionTypeException {
		switch(type)
		{
			case  ADD_COMPONENT:		
				return new AddComponent(interpreter,this);
			case REMOVE_COMPONENT:		
				return new RemoveComponent(interpreter);
			case COPY_COMPONENT:		
				return new CopyComponent(interpreter,this);
			case MERGE_COMPONENT:
				return new MergeComponent(interpreter);
	
		}
		
		throw new ActionTypeException();
	}


	private static boolean isMachineComponent(String className) {
		return structuralClasses.contains(className);
	}

	private final static Set<String> structuralClasses = new HashSet<String>();
	static{
		structuralClasses.add(GolemsClassRepository.AXLE_CLASS);
	//	structuralClasses.add(GolemsClassRepository.BALL_SOCKET_CLASS_OLD);
		structuralClasses.add(GolemsClassRepository.BATTERY_CLASS);
		structuralClasses.add(GolemsClassRepository.CAMERA_CLASS);
		structuralClasses.add(GolemsClassRepository.BOX_CLASS);
		structuralClasses.add(GolemsClassRepository.CAP_CLASS);
		structuralClasses.add(GolemsClassRepository.CONE_CLASS);
		structuralClasses.add(GolemsClassRepository.CYL_CLASS);
		structuralClasses.add(GolemsClassRepository.DISTANCE_SENSOR_CLASS);
		structuralClasses.add(GolemsClassRepository.GEAR_CLASS);
		structuralClasses.add(GolemsClassRepository.RACK_GEAR_CLASS);
		structuralClasses.add(GolemsClassRepository.HINGE_CLASS);
		structuralClasses.add(GolemsClassRepository.HYDRAULIC_CLASS);
		structuralClasses.add(GolemsClassRepository.PYRAMID_CLASS);
		structuralClasses.add(GolemsClassRepository.ROCKET_CLASS);
		structuralClasses.add(GolemsClassRepository.MODIFIER_CLASS);
		structuralClasses.add(GolemsClassRepository.SPHERE_CLASS);
		structuralClasses.add(GolemsClassRepository.TOUCH_SENSOR_CLASS);
		structuralClasses.add(GolemsClassRepository.TUBE_CLASS);
		structuralClasses.add(GolemsClassRepository.OSCILLOSCOPE_CLASS);
		structuralClasses.add(GolemsClassRepository.GENERAL_SENSOR_CLASS);
		structuralClasses.add(GolemsClassRepository.INPUT_CLASS);
		structuralClasses.add(GolemsClassRepository.BALL_SOCKET_CLASS);
		structuralClasses.add(GolemsClassRepository.GRAPPLE_CLASS);
		structuralClasses.add(GolemsClassRepository.INPUT_CLASS);
		structuralClasses.add(GolemsClassRepository.CONTACT_CLASS);
	}
	
	
	public void refresh() {
		CollectionType structureCollection = interpreter.getStructures();

		ArrayList<Structural> toRemove = new ArrayList<Structural>();
		//long[] times = new long[5];
		//times[0] = System.nanoTime();
		for(Structural structure:this.getStructures())
		{
			if (!structureCollection.getValues().contains(structure.getStore()))
			{
				//delete this machine
				toRemove.add(structure);
			}
		}
	//	times[1] = System.nanoTime();
		StateManager.getGame().lock();
		try{
		for(Structural structure:toRemove)
		{
			//remove the VIEW from this machine
			this.removeStructural(structure);
			
			structure.remove();
		}
	//	times[2] = System.nanoTime();
		if (this.layerRepository == null || ! this.layerRepository.getStore().equals(interpreter.getLayerRepository()))
		{
			this.layerRepository = new LayerRepository(interpreter.getLayerRepository(),this);
			//this.layerRepository.setMachine(this);
	
		}
		layerRepository.clearStructures();
		layerRepository.refresh();
	
		for(DataType data:structureCollection.getValues())
		{
			if(! (data instanceof PropertyStore))
					continue;
			String className = ((PropertyStore)data).getClassName();
			if(!isMachineComponent(className))//only copy things that can actually go in a machine.
			{
				StateManager.logError(new RuntimeException("Machines can't contain: " + className));
				continue;
			}
			//boolean exists = false;
			Structural structure = this.getStructure((PropertyStore)data);
			
/*			for(Structural structure:getStructures())
			{
				if(structure.getStore().equals(data))
				{
					exists = true;
					break;
				}
			}*/
			if(structure==null)
			{
				//try{
		
				PhysicalStructure component = (PhysicalStructure) DesignViewFactory.constructView((PropertyStore)data);
				this.registerStructural(component);
				//add the VIEW to this machine
				component.addToMachine(this);
				
				component.refresh();
				
				component.addViewModes(this.getViews());
				
				structure = component;
			/*	}catch(ClassCastException e)
				{//if an uninterpretable class, or something other than a structure, gets created here for some reason, this may happen.
					StateManager.logError(e);
				}*/
			}
			//if(structure instanceof PhysicalStructure)
			{
			//	PhysicalStructure physStruct = (PhysicalStructure) structure;
				//ensure the structure is mapped to the correct layer
				
								
				if(!getLayerRepository().hasLayer(structure.getLayerReference()))
				{
					//the following is a potentially dangerous alteration of the model from the view...
					//its not undoable, for example...
					structure.getStore().setProperty(StructureInterpreter.LAYER, getLayerRepository().getDefaultLayer().getID());
				}
				
				//getLayerRepository().setLayer(structure,structure.getLayerReference());
			
			
			//	structure.refreshView();
			}
			
		}
		}finally{
			StateManager.getGame().unlock();
		}
/*		
		for(DataType data:interpreter.getStructures().getValues())
		{
			if (data instanceof PropertyStore)
				((PropertyStore)data).refresh();
		}*/
		//refresh wires
	//	times[3] = System.nanoTime();
/*		if (this.wires != null && this.wires.getStore().equals(interpreter.getWires()))
		{
			
		}else
		{
			//do the wires need to be deleted in any special way?
			wires = new Wires(new WireStoreInterpreter( interpreter.getWires()));
			wires.addToMachine(this);
			wires.refresh();
		}*/
		
		//interpreter.getWires().refresh();
		
		if (this.groupManager == null || ! this.groupManager.getStore().equals(interpreter.getGroups()))
		{
			this.groupManager = new GroupManager(interpreter.getGroups());
			this.groupManager.setMachine(this);
			groupManager.refresh();
		}
		
		
	
		//
		//this will get all the wire ports in this machine, match them up with the ids of the wires
		//and then use that to position the wires
		//wire models will be moved into a model in the wire store.
	/*	times[4] = System.nanoTime();
		String time = "Machine Refresh:\t";
		for (int i = 1; i< times.length;i++)
		{
			time+= times[i] - times[i-1] + "\t";
		}
		System.out.println(time);*/
	}
	

	public PropertyStore getStore() {
		return interpreter.getStore();
	}

/*	public Collection<Physical> getPhysicals() {
		List<Physical> physicals =new ArrayList<Physical>();
		for(Structural structure:getStructures())
			if (structure.getPhysical()!=null)
			{
				for(Physical physical:structure.getPhysical())
					physicals.add(physical);
			}
		return physicals;
	}*/

	private static class AddComponent extends AddComponentAction
	{
		
		private final MachineInterpreter interpreter;
	//	private StructuralMachine tempRef;
		public AddComponent(MachineInterpreter interpreter,StructuralMachine tempRef) {
			super();
			this.interpreter = interpreter;
		}

		@Override
		public boolean doAction() {
			interpreter.addStructure(this.getComponent());
			/*if(isFirstUse())
			{
				
				 * Test first if there is an extant layer claiming this structure. 
				 
				
				Layer layer = tempRef.getLayerRepository().getActiveLayer();
				
				AddMemberAction  add =(AddMemberAction) layer.getAction(Action.ADD_MEMBER);
				{
					
				}
				
				tempRef = null;
			}*/
			//add this component to the active layer, if there is an active layer
			
			interpreter.refresh();
			return true;
		}

		@Override
		public boolean undoAction() {
			interpreter.removeStructure(this.getComponent());
			interpreter.refresh();
			return true;
		}
		
	}
	
	
	
	
	private static class CopyComponent extends CopyComponentAction
	{
		private final MachineInterpreter interpreter;
		private StructuralMachine view = null;//this will be nullified after the first use
		
		public CopyComponent(MachineInterpreter interpreter,StructuralMachine view) {
			super();
			this.interpreter = interpreter;
			this.view = view;
						
		}

		private Collection<PropertyStore> newComponents = new ArrayList<PropertyStore>();
		
		private Collection<AddWireAction> addWireActions = null;
		
		
		@Override
		public Collection<PropertyStore> getCopiedComponents() {
			return newComponents;
		}

		@Override
		public boolean doAction() {
	/*		long[] times = new long[6];
			int t = 0;*/
			if (isFirstUse())
			{
				//assume that, on the first use, the current view is valid
			
				//make all references in each component new;
				//but first match them up against existing wires
				//and handle wires:
				//if there are wire
				
				
				//there is a problem: we dont want all the references to be changed!
				//if a copied object is supposed to refer to something that doesnt get copied, then its reference
				//should remain unaltered.
				
			//	times [t++] = System.nanoTime();
				addWireActions = new ArrayList<AddWireAction>();
				
				ReferenceMap referenceMap = new ReferenceMap();
				Iterator<PropertyStore> iterator = this.getComponents().iterator();
			
				Set<Reference> wireInputReferences = new HashSet<Reference>();
				
				while(iterator.hasNext())
				{
					//the only references we want to renew are the IDS of the structures being copied,
					//and the ids of the wireports/wiremanagers being copied.
					PropertyStore compStore =iterator.next();
					if(!isMachineComponent(compStore.getClassName()))//only copy things that can actually go in a machine.
						continue;
					PhysicalStructureInterpreter interp = new PhysicalStructureInterpreter(compStore);
					Reference structureRef = interp.getReference();
					referenceMap.addReferenceToRenew(structureRef);
					
					for (DataType data: interp.getInputs().getValues())
					{
						if (data instanceof PropertyStore)
						{
							WirePortInterpreter portInterp = new WirePortInterpreter((PropertyStore)data);
							referenceMap.addReferenceToRenew(portInterp.getID());
							//each input will have to manually have its wire copied, since only outputs know their wires.
							wireInputReferences.add(portInterp.getID());
						}
					}
					
					for (DataType data: interp.getOutputs().getValues())
					{
						if (data instanceof PropertyStore)
						{
							WirePortInterpreter portInterp = new WirePortInterpreter((PropertyStore)data);
							referenceMap.addReferenceToRenew(portInterp.getID());
						}
					}
				}
				
				
				
				
				iterator = this.getComponents().iterator();
				while(iterator.hasNext())
				{
					PropertyStore store = iterator.next();
					PropertyStore copy = store.uniqueDeepCopy(referenceMap);
					newComponents.add(copy);
					iterator.remove();
					//to keep memory requirements down, remove the old ones as we add the new ones. (can't hurt, even if it is unlikely to help usually...)
				}
				
				
				//one more thing: wires are only stored in the outputs. if an input is copied, but not the output, then the wires wont
				//get copied.
				
				//go through each wire manager that is an ouput, check if it has a wire to the original input, and duplicate it if neccesary.
				
				for (Reference inputPortRef :wireInputReferences)
				{
					Collection<Wire> wires = view.getWires().getWires(inputPortRef);
					//for each wire, determine if its output end was copied; if it wasn't, manually copy it
					if (wires == null)
						continue;
					Reference newRef = referenceMap.getNewReference(inputPortRef);
					
					for(Wire wire:wires)
					{
						if(!referenceMap.getReferencesToRenew().contains(wire.getReference(false)))
						{
							WireInterpreter interp = new WireInterpreter(wire.getStore().deepCopy());
							interp.setPortID(newRef, true);
							
							try {
								AddWireAction addWire = (AddWireAction) wire.getManager().getAction(Action.ADD_WIRE);
								addWire.setComponent(interp.getStore());
								addWireActions.add(addWire);
							} catch (ActionTypeException e) {
								StateManager.logError(e);
							}catch(NullPointerException e)//seems to occur after copying components with multiple wires
							{
								StateManager.logError(e);
							}
							
							//add this wire to the output port
						
							
						}
					}
				}
			
				view = null;
			}
		//	times [t++] = System.nanoTime();
			for (PropertyStore store:newComponents)
				interpreter.addStructure(store);
			

			for(AddWireAction addWire:addWireActions)
				addWire.doAction();
			
			//times [t++] = System.nanoTime();
			interpreter.refresh();//if you dont refresh the model before adding the wires, the wires wont be able to find the input/output ports...

			return true;
		}

		@Override
		public boolean undoAction() {
			

			for(AddWireAction addWire:addWireActions)
				addWire.undoAction();
			
			for (PropertyStore store:newComponents)
				interpreter.removeStructure(store);
			
			
			interpreter.refresh();
			return true;
		}
		
	}
	
	
	private static class RemoveComponent extends RemoveComponentAction
	{
		private final MachineInterpreter interpreter;
		
		public RemoveComponent(MachineInterpreter interpreter) {
			super();
			this.interpreter = interpreter;
		}
		@Override
		public boolean doAction() {
			interpreter.removeStructure(this.getComponent());			
			interpreter.refresh();
			return true;
		}

		@Override
		public boolean undoAction() {
			interpreter.addStructure(this.getComponent());
			interpreter.refresh();
			return true;
		}
		
	}
	/**
	 * Merge another machine in
	 * @author Sam
	 *
	 */
	private static class MergeComponent extends MergeComponentAction
	{

		
			private final MachineInterpreter interpreter;
			
			public MergeComponent(MachineInterpreter interpreter) {
				super();
				this.interpreter = interpreter;
			}
			
			private Collection<PropertyStore> newComponents = new ArrayList<PropertyStore>();
			
			private Collection<AddWireAction> addWireActions = null;
			
			private Collection<PropertyStore> newLayers = new ArrayList<PropertyStore>();
			

			
			
			@Override
			public boolean doAction() {
			
				if (isFirstUse())
				{
					//cant assume that collisions are unlikely, because someone might import a model into itself!
					ReferenceMap referenceMap = new ReferenceMap(true);
					//collect all existing references
					LayerRepositoryInterpreter myLayerRepository = new LayerRepositoryInterpreter(interpreter.getLayerRepository());
					for(DataType d:myLayerRepository.getLayers().getValues())
					{
						if(d instanceof PropertyStore)
						{
						//	LayerInterpreter existingLayer = new LayerInterpreter((PropertyStore)d);
							referenceMap.addForbiddenReference(((PropertyStore)d).getReference(LayerInterpreter.ID));
							
						}
					}
					
					for (DataType data: interpreter.getStructures().getValues())
					{
						if(data.getType() == DataType.Type.PROPERTIES)
						{
							PropertyStore store = (PropertyStore)data;
							referenceMap.addForbiddenReference(store.getReference(StructureInterpreter.REFERENCE));
							if(store.hasProperty(FunctionalInterpreter.OUTPUTS,DataType.Type.COLLECTION))
							{
								CollectionType ports = store.getCollectionType(FunctionalInterpreter.OUTPUTS);
								for(DataType d:ports.getValues())
								{
									if(d instanceof PropertyStore)
									{
										PropertyStore portStore = (PropertyStore)d;
										referenceMap.addForbiddenReference(portStore.getReference(WirePortInterpreter.ID));
	
									}
								}
							}
							if(store.hasProperty(FunctionalInterpreter.INPUTS,DataType.Type.COLLECTION))
							{
								CollectionType ports = store.getCollectionType(FunctionalInterpreter.INPUTS);
								for(DataType d:ports.getValues())
								{
									if(d instanceof PropertyStore)
									{
										PropertyStore portStore = (PropertyStore)d;
										referenceMap.addForbiddenReference(portStore.getReference(WirePortInterpreter.ID));
	
									}
								}
							}
						}
					}
					
					
					
					MachineInterpreter machine = new MachineInterpreter(this.getComponent().uniqueDeepCopy(referenceMap));
				
					
				
					LayerRepositoryInterpreter layerInterp = new LayerRepositoryInterpreter( machine.getLayerRepository());
					for(DataType store: layerInterp.getLayers().getValues())
					{
						if(store instanceof PropertyStore)
						{
							newLayers.add((PropertyStore)store);
							//myLayerRepository.addLayer((PropertyStore)store);
						}
					}
					
					for (DataType data: machine.getStructures().getValues())
					{
						if(data.getType() == DataType.Type.PROPERTIES)
						{
							newComponents.add((PropertyStore)data);
							//interpreter.addStructure((PropertyStore) data);
						}
					}
				}
				
				
				LayerRepositoryInterpreter myLayerRepository = new LayerRepositoryInterpreter(interpreter.getLayerRepository());
				
				for(PropertyStore store: newLayers)
				{
			
						
					myLayerRepository.addLayer((PropertyStore)store);
					
				}
				
				for (PropertyStore data: newComponents)
				{
	
						interpreter.addStructure((PropertyStore) data);
				
				}
				
				interpreter.refresh();
				myLayerRepository.refresh();
				return true;
			}

			@Override
			public boolean undoAction() {
				LayerRepositoryInterpreter myLayerRepository = new LayerRepositoryInterpreter(interpreter.getLayerRepository());
				
				for(PropertyStore store: newLayers)
				{
			
						
					myLayerRepository.removeLayer((PropertyStore)store);
					
				}
				
				for (PropertyStore data: newComponents)
				{
	
						interpreter.removeStructure((PropertyStore) data);
				
				}
				
				/*MachineInterpreter machine = new MachineInterpreter(this.getComponent());
				for (DataType data: machine.getStructures().getValues())
				{
					if(data.getType() == DataType.Type.PROPERTIES)
					{
						interpreter.removeStructure((PropertyStore) data);
					}
				}
				
				LayerRepositoryInterpreter myLayerRepository = new LayerRepositoryInterpreter(interpreter.getLayerRepository());
				
				LayerRepositoryInterpreter layerInterp = new LayerRepositoryInterpreter( machine.getLayerRepository());
				for(DataType store: layerInterp.getLayers().getValues())
				{
					if(store instanceof PropertyStore)
					{
						myLayerRepository.removeLayer((PropertyStore)store);
					}
				}
				*/
			
				interpreter.refresh();
				myLayerRepository.refresh();
				return true;
			}
			
		
	}
	
	public MachineInterpreter getInterpreter() {
		return interpreter;
	}

	public Structural getStructure(Reference id) {
		WeakReference<Structural> sRef = this.structureReferenceMap.get(id);
		if(sRef ==null)
			return null;
		return sRef.get();
	}

	public Structural getStructure(PropertyStore store) {
		WeakReference<Structural> sRef = this.structureStoreMap.get(store);
		if(sRef ==null)
			return null;
		return sRef.get();
	}
	
	
	public void remove() {
		for (Structural structure:this.getStructures())
			structure.remove();
	}

	public LayerRepository getLayerRepository() {
		return layerRepository;
	}
}
