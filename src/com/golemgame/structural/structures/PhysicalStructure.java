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
package com.golemgame.structural.structures;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.golemgame.functional.LocalWireManager;
import com.golemgame.functional.Wire;
import com.golemgame.functional.WirePort;
import com.golemgame.mechanical.StructuralMachine;
import com.golemgame.mechanical.layers.Layer;
import com.golemgame.model.Model;
import com.golemgame.model.effect.ModelEffect;
import com.golemgame.model.effect.ModelEffectPool;
import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.golems.MachineInterpreter;
import com.golemgame.mvc.golems.PhysicalStructureInterpreter;
import com.golemgame.mvc.golems.StructureInterpreter;
import com.golemgame.mvc.golems.SurfacePropertiesInterpreter;
import com.golemgame.mvc.golems.SurfacePropertiesInterpreter.SurfaceType;
import com.golemgame.properties.Property;
import com.golemgame.states.StateManager;
import com.golemgame.structural.DesignViewFactory;
import com.golemgame.structural.MaterialWrapper;
import com.golemgame.structural.Structural;
import com.golemgame.structural.collision.CollisionListener;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.structural.collision.GroupDivider;
import com.golemgame.structural.group.StructureGroup;
import com.golemgame.structural.structures.decorators.PhysicalDecorator;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.ControlAction;
import com.golemgame.tool.action.DeleteAction;
import com.golemgame.tool.action.SelectAction;
import com.golemgame.tool.action.SetLayerAction;
import com.golemgame.tool.action.ViewModeAction;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.action.information.CollisionInformation;
import com.golemgame.tool.action.information.GroupInformation;
import com.golemgame.tool.action.information.PhysicalInfoAction;
import com.golemgame.tool.action.information.StaticMaterialInformation;
import com.golemgame.views.ViewManager;
import com.golemgame.views.Viewable;
import com.jmex.physics.material.Material;


public abstract class PhysicalStructure extends Structure implements Structural, CollisionListener,Serializable {

	private static final long serialVersionUID = 1L;

	private MaterialWrapper materialWrapper;

	
	private List<PhysicalDecorator> physicalDecorators = new ArrayList<PhysicalDecorator>();
	
	private List<WirePort> wireports = new ArrayList<WirePort>();
	
	private PhysicalStructureInterpreter interpreter;
	
	private ViewManager viewManager = new ViewManager();
	private SurfacePropertiesInterpreter surface;
	private Map<Reference,LocalWireManager> wireManagers;
	
	/**
	 * Each output port has a wire manager
	 */
	public Collection<LocalWireManager> getWireManagers() {
		return wireManagers.values();
	}
	
	public LocalWireManager getWireManager(Reference from)
	{
		return this.wireManagers.get(from);
	}
	

	protected boolean collisionMemberStateChange()
	{
		return collisionMemberStateChange(null);
	}
	
	public boolean isPropagating() {
		return true;
	}



	protected boolean collisionMemberStateChange(Set<Model> ignoreSet)
	{

/*		//this.getModel().updateWorldData();
		
		CollisionMember member = getStructuralCollisionMember();
		if (member != null)
		{
		
			member.getCollisionManager().count();
		//	long time = System.nanoTime();
			if (member.resolveCollisions(ignoreSet))
			{
		
				
			}
		//	System.out.println("State Change\t" +( System.nanoTime() - time));
			
		}*/
		return false;
		
	}

	
	protected Model[] getAppearanceModels()
	{
		return getControlledModels();
	}

	public PhysicalStructure(PropertyStore store)
	{
		this(new PhysicalStructureInterpreter(store));
	}
	
	public PhysicalStructure(PhysicalStructureInterpreter interpreter) {
		super(interpreter);
		this.interpreter = interpreter;
	//	this.materialWrapper = new MaterialWrapper(MaterialWrapper.MaterialClass.DEFAULT);
		this.wireManagers = new HashMap<Reference, LocalWireManager>();
	

	}
	
	/**
	 * Register a wireport, but dont accept duplicates.
	 * Also: the order in which they are registered turns must remain constant.
	 * This order is recomended: Input,Output, Aux. Input
	 * @param port
	 */
	protected void registerWirePort(WirePort port)
	{
		if (!wireports.contains(port))
		{
			this.wireports.add(port);
			this.viewManager.registerViewable(port);
		}
		port.setStructuralOwner(this);//important to do this, because old save files might be missing this information
	}
	
	public List<WirePort> getWirePorts()
	{
		return wireports;
	}
	
	public void notifyDelete() {
	
	}

	
	public void notifyUndelete() {
	
	}
	
	
	protected void initialize()
	{
		super.initialize();
		if(this.getStructuralCollisionMember()!= null)
		{
			this.getStructuralCollisionMember().addCollisionListener(this);
		}
	
		
	}

	

	
	public void addModelEffect(ModelEffect effect) {
		this.getAppearance().addEffect(effect, false);
		
	}


	
	@Override
	public boolean isSelectable() {
		Layer layer = getLayer();
		return super.isSelectable() &&  (layer==null?true:layer.isEditable());
	}

	public void setStatic(boolean isStatic) {
		interpreter.setStatic(isStatic);
		
	}

	protected abstract CollisionMember getStructuralCollisionMember();
	





	
	public Actionable getActionable() {
		return this;
	}


/*
	
	public Physical[] getPhysical() {
		return new Physical[]{this};
	}
*/


	
	public boolean isMindful() {
		return false;
	}

	
	public boolean isPhysical() {
		return true;
	}


	public void removeFromMachine(StructuralMachine machine) {
		machine.getStructuralManager().removeCollisionMember(this.getStructuralCollisionMember());
	}
	
	
	public void addToMachine(StructuralMachine machine) {
		this.setMachine(machine);
		machine.getViewManager().registerViewable(this);
		if(this.getStructuralCollisionMember()!= null)
			machine.getStructuralManager().addCollisionMember(this.getStructuralCollisionMember());
	}



	
	public Material getMaterial() {
		return materialWrapper.constructMaterial();
	}

	public MaterialWrapper getMaterialWrapper() {
		return materialWrapper;
	}

	
	public boolean isStatic() {
		return interpreter.isStatic();
	}


/*

	*//**
	 * Set some model data (such as appearance) to match the given physical structure
	 * @param from
	 *//*
	protected void set(PhysicalStructure from)
	{
		super.set(from);
		for(PhysicalDecorator decorator:from.getPhysicalDecorators())
		{
			this.addPhysicalDecorator(decorator.copy());
		}
		
		this.setStatic(from.isStatic());
		
		this.materialWrapper = new MaterialWrapper( from.materialWrapper);
		
		getStructuralAppearanceEffect().set(from.getStructuralAppearanceEffect());
		
		appearance = from.getAppearance().makeCopy(getAppearanceModels(), true);
		appearance.addEffect(getStructuralAppearanceEffect(), true);
		//this fails to apply lighting to the new model...
		this.addViewModes(getMachine().getViews());
		appearance.reapply();	
	}
	*/


	//private Set<Viewable.ViewMode> views = new HashSet<Viewable.ViewMode>();
	
	public boolean addViewMode(Viewable.ViewMode viewMode) {
		
		if(viewManager.addViewMode(viewMode))
		{
			refreshView();
			return true;
		}
		return false;
	}

	
	

	public boolean clearViews() {
		if(viewManager.clearViews()){
			refreshView();
			return true;
		}
		return false;
	}

	public Collection<Viewable.ViewMode> getViews()
	{
		return viewManager.getViews();
	}
	
	public boolean addViewModes(Collection<Viewable.ViewMode> viewModes) {
		if(viewManager.addViewModes(viewModes))
		{
			refreshView();
			return true;
		}
		return false;
/*		for(Viewable.ViewMode viewMode:viewModes )
			addViewMode(viewMode);*/
	}
	
	
	
	
	@Override
	public void remove() {
		super.remove();
		getMachine().getViewManager().removeViewable(this);
		for(LocalWireManager manager:this.getWireManagers())
		{
			manager.remove();
		}
		
	}

	protected ViewManager getViewManager(){
		return viewManager;
	}
	
	public Layer getLayer()
	{
		return getMachine().getLayerRepository().getLayer( interpreter.getLayer());
	}
	
	@Override
	public void refresh() {
		super.refresh();
		
		refreshDecorators();
		
		if(this.materialWrapper ==null || this.materialWrapper.getStore()!= interpreter.getMaterialProperties())
		{
			this.materialWrapper = new MaterialWrapper(interpreter.getMaterialProperties());
		}
		this.materialWrapper.refresh();
		
		
		if(surface==null ||this.surface.getStore()!=interpreter.getSurfaceProperties())
		{
			surface = new SurfacePropertiesInterpreter(this.interpreter.getSurfaceProperties());
			if(surface.getSoundSurface()==SurfaceType.INFER)
			{
				surface.setMaterial(SurfacePropertiesInterpreter.getDefaultSurface(materialWrapper.getBaseClass()));
			}
		}

		this.surface.refresh();

		
		refreshWireManagers();

		
		//this.getAppearance().reapply();//this causes the appearance effect problem...
	}

	private void refreshWireManagers() {
		if(this.getWirePorts().isEmpty())
			return;//dont do anything
		
		CollectionType outputCollection = interpreter.getOutputs();
		CollectionType inputCollection = interpreter.getInputs();
		ArrayList<LocalWireManager> toRemove = new ArrayList<LocalWireManager>();

		for(LocalWireManager structure:this.getWireManagers())
		{
			if (!outputCollection.getValues().contains(structure.getStore()) && ! (inputCollection.getValues().contains(structure.getStore())))
			{
				//delete this machine
				toRemove.add(structure);
			}
		}
		
		for(LocalWireManager structure:toRemove)
		{
			this.wireManagers.remove(structure);			
		}
		
		int numInputs = 0;
		int numOutputs = 0;
		
		for (WirePort port:this.getWirePorts())
		{
			if (port.isInput())
				numInputs ++;
			else
				numOutputs ++;
		}
		
		//ArrayList<PropertyStore> toAdd = new ArrayList<PropertyStore>();
		int portNum= 0;
		
		for(int i = 0; i < numOutputs;i++)
		{
			PropertyStore data = interpreter.getOutput(i);
		
			boolean exists = false;

			for(LocalWireManager structure: getWireManagers())
			{
				if(structure.getStore().equals(data))
				{
					exists = true;
					break;
				}
			}
			if(!exists)
			{
				LocalWireManager component =new LocalWireManager((PropertyStore)data);
				component.setPortNumber(portNum);
				component.setIsInput(false);
				component.setStructure(this);
			
				//set the wire port...
				int pNum = 0;
				
				for(WirePort port:this.getWirePorts())
				{//could slightly speed this up by putting the ports in an array by number
					if (port.isInput() == component.isInput())
					{
						if (pNum == component.getPortNumber())
						{
							component.setWirePort(port);
							port.setWireManager(component);
							break;
						}
						pNum++;
					}
				}
				if (component.getWirePort() == null)
					break;
				
				component.refresh();		
				this.registerWirePort(component.getWirePort());
				
				this.wireManagers.put(component.getReference(), component);
			//	this.viewManager.registerViewable(component.getWirePort());
				this.getMachine().getWires().registerWirePort(component.getWirePort());
			}
			portNum++;
		}
		portNum= 0;
		for(int i = 0; i < numInputs;i++)
		{
			PropertyStore data = interpreter.getInput(i);
			
			boolean exists = false;

			for(LocalWireManager structure: getWireManagers())
			{
				if(structure.getStore().equals(data))
				{
					exists = true;
					break;
				}
			}
			if(!exists)
			{
				LocalWireManager component =new LocalWireManager((PropertyStore)data);
				component.setPortNumber(portNum);
				component.setIsInput(true);
				component.setStructure(this);
			
				//set the wire port...
				int pNum = 0;
				for(WirePort port:this.getWirePorts())
				{
					if (port.isInput() == component.isInput())
					{
						if (pNum == component.getPortNumber())
						{
							component.setWirePort(port);
							port.setWireManager(component);
							break;
						}
						pNum++;
					}
				}
				if (component.getWirePort() == null)
					break;
				component.refresh();				
				this.wireManagers.put(component.getReference(), component);
				this.getMachine().getWires().registerWirePort(component.getWirePort());
			}
			portNum++;
		}


		
	}
	


	

	private void refreshDecorators() {
		CollectionType decoratorCollection = interpreter.getPhysicalDecorators();

		ArrayList<PhysicalDecorator> toRemove = new ArrayList<PhysicalDecorator>();
	
		for(PhysicalDecorator decorator:this.getPhysicalDecorators())
		{
			if (!decoratorCollection.getValues().contains(decorator.getStore()))
			{
				//delete this machine
				toRemove.add(decorator);
			}
		}
		
		for(PhysicalDecorator decorator:toRemove)
		{
			//remove the VIEW from this machine
			//this.removeStructural(structure);
			//structure.delete();
			this.physicalDecorators.remove(decorator);
		}
		
		for(DataType data:decoratorCollection.getValues())
		{
			if(! (data instanceof PropertyStore))
					continue;
			boolean exists = false;
			for(PhysicalDecorator decorator:getPhysicalDecorators())
			{
				if(decorator.getStore().equals(data))
				{
					exists = true;
					break;
				}
			}
			if(!exists)
			{
				PhysicalDecorator component = (PhysicalDecorator) DesignViewFactory.constructView((PropertyStore)data);
				
				this.physicalDecorators.add(component);
				component.refresh();
				
			
			}
		}
		
		for(DataType data:interpreter.getPhysicalDecorators().getValues())
		{
			if (data instanceof PropertyStore)
				((PropertyStore)data).refresh();
		}
	}

	public void refreshView()
	{
		Layer layer = getLayer();
		Collection<ViewMode> views = viewManager.getViews();
		
		if (!(views.contains(SELECTED) && layer.isEditable() && views.contains(Viewable.ViewMode.MATERIAL)))
		{
			this.getAppearance().removeEffect(ModelEffectPool.getInstance().getSelectedEffect(), false);
		}
		if (views.contains(Viewable.ViewMode.MATERIAL))
		{
			//control based on layer:
			PhysicalStructure.this.setSelectable(true);
			if(layer.isVisible())
			{
			
				
				this.getModel().setVisible(true);
				this.getAppearance().addEffect(	this.getStructuralAppearanceEffect(),true);
			}else{
			//	PhysicalStructure.this.setSelectable(false);
				this.getModel().setVisible(false);
				
			}
			
			if (views.contains(SELECTED) && layer.isEditable())
			{
				this.getAppearance().addEffect(ModelEffectPool.getInstance().getSelectedEffect(), false);
				this.setSelectable(true);
			}
			
		}else if (views.contains(Viewable.FUNCTIONAL))	//material view takes precedences over functional view.
		{
			if(layer.isVisible())
			{
				PhysicalStructure.this.setSelectable(false);
				//for (Model model:this.getControlledModels())
				this.getModel().setVisible(true);
				//this.getAppearance().addEffect(	this.getStructuralAppearanceEffect(),true);

					this.getAppearance().addEffect(	ModelEffectPool.getInstance().getWireModeHideEffect(),false);
			}else{
				PhysicalStructure.this.setSelectable(false);
				this.getModel().setVisible(false);
				this.getAppearance().addEffect(	ModelEffectPool.getInstance().getWireModeHideEffect(),false);
			}
		}else
		{
			PhysicalStructure.this.setSelectable(false);
		}
		

		
		
		
		if ((!views.contains(SELECTED)) && views.contains(Viewable.GROUPMODE) && layer.isVisible())//selection takes priority over group mode
		{
			StructureGroup group = getMachine().getGroupManager().getGroup(this);
			if (group == null )
			{
				if (! views.contains(Viewable.MATERIAL))
				{
					this.getAppearance().addEffect(ModelEffectPool.getInstance().getUnselectableEffect(), false);
					this.setSelectable(true);
				}else
					this.setSelectable(true);
			}else 
			{
				this.getAppearance().addEffect(group.getColorEffect(), false);
				this.setSelectable(true);
			}			
		}

		this.getAppearance().reapply();
		
		viewManager.refreshView();
/*		for(WirePort port:this.getWirePorts())
		{
			if (port != null)
				port.refreshView();
		}*/
	}

	public boolean removeViewMode(	com.golemgame.views.Viewable.ViewMode viewMode) {
		if(viewManager.removeViewMode(viewMode)){
			refreshView();
			return true;
		}
		return false;
	}

	protected void showSelectEffect(boolean engage) {
		if(engage)
			this.addViewMode(SELECTED);
		else
			this.removeViewMode(SELECTED);
	}

	public void addPhysicalDecorator(PropertyStore store)
	{
		this.interpreter.addPhysicalDecorator(store);
		this.interpreter.refresh();
		//physicalDecorators.add(decorator);
	}
	
	public void removePhysicalDecorator(PropertyStore store)
	{
		this.interpreter.removePhysicalDecorator(store);
		this.interpreter.refresh();
	}
	
	public List<PhysicalDecorator> getPhysicalDecorators()
	{
		return physicalDecorators;		
	}
	


	public Action<?> getAction(Type type) throws ActionTypeException {
		
		switch(type)
		{
	
		case SELECT:
			return new Select();
		//case COPY:
		//	return new Copy();
			
		case GET_GROUP:
			return new GroupInfo();
		case DELETE:
			return new Delete(interpreter,getMachine().getInterpreter(),this);
	//	case CREATE:
	//		return new Create();
		case MODEL:
			return new ModelInfo();
		case VIEWMODE:
			return new ViewModeImpl();

		case STATIC_INFO:
			return new StaticInfo();
		case COLLISION_INFO:
			return new CollisionInfo();
		case PHYSICAL_INFO:
			return new PhysicalInfo();
		case SET_LAYER:
			return new SetLayer(getStore());
		default:
			return super.getAction(type);

		}
	}
	

	
	
	/*protected class Copy extends CopyAction
	{
		private PhysicalStructure copy = null;
		private ModelData copiedData;

		private ArrayList<WirePortCopyPair> wirePortPairs;
		@Override
		public Actionable getControlled() {
			return PhysicalStructure.this;
		}
		
		public boolean doAction() {
			if (isFirstUse())
			{//first time calling this
				copiedData = getModel().getWorldData();
				
				try{
					StructuralMachine destination = getDestination();
					if(destination == null)
						destination = getMachine();
					copy = PhysicalStructure.this.copy(destination);
					
					
					 * Copying wires:
					 * We want two different behaviours for copying wires:
					 * If both ends of the wire are in the copying group, then
					 * create a new wire between both objects.
					 * 
					 * If only one end is in the copying group,
					 * then create a new wire going from the copy to the uncopied member.
					 * 
					 
					
					
					
					Collection<Model> copiedModels = super.getCoCopyList();
					
					copy.getModel().loadWorldData(copiedData);
					
					for (int i =0;i<PhysicalStructure.this.getWirePorts().size();i++)
					{
						WirePort wirePort = PhysicalStructure.this.getWirePorts().get(i);
						for (Wire wire:wirePort.getWires())
						{
							WirePort otherPort = wire.getOther(wirePort);
							if(otherPort != null)
							{
								PhysicalStructure owner = otherPort.getStructuralOwner();
								if (owner == null)
								{
									StateManager.getLogger().warning("Wireport with null owner");
									continue;//this is reached if the file being imported improperly has references to other imported machines.
								}
									
									
								boolean structureCopied = false;
							
								for (Model model:	owner.getStructuralCollisionMember().getCollisionModels())
								{
									if (copiedModels.contains(model))
									{
										structureCopied = true;
										break;
									}
								}
							
								if (structureCopied)
								{
									//the connection will have to be formed, later, after all members are copied,
									//between copied members.
									//this will happen in the post copy callback.
									if(wirePortPairs == null)
									{
										wirePortPairs = new ArrayList<WirePortCopyPair>();
									}
									WirePort copyPort = copy.getWirePorts().get(i);
									copyPort.addViewModes(getMachine().getViews());
									wirePortPairs.add(new WirePortCopyPair(copyPort,otherPort,wire.isNegative()));
								
								}else
								{//form a connection between the copy port and the original
									WirePort copyPort = copy.getWirePorts().get(i);
									WirePort.formConnection(otherPort, copyPort, wire.isNegative());
									copyPort.addViewModes(getMachine().getViews());
								}
								
							}
						}
					}
			
				}catch(NotCopyableException e)
				{
					return false;
				}
			}else
			{
				if (copy == null )
					return false;
				try{
					copy.reCopy();
				}catch(FailedCopyException e)
				{
					return false;
				}
				
			}
			
			
		
			//Moved this up from the bottom of the method, so that collision detection would work.
			//this may have unintended consequences on undo/redo.
			copy.getModel().loadWorldData(copiedData);
			for (Viewable.ViewMode view:getMachine().getViews())
				copy.addViewMode(view);
			
					CollisionMember member = getStructuralCollisionMember();
				
					if (member != null)
					{
						if (this.getCoCopyList() == null)
						{
							copy.getStructuralCollisionMember().resolveCollisionsIgnore(PhysicalStructure.this.getStructuralCollisionMember());
						}else
						{
							copy.getStructuralCollisionMember().resolveCollisions(getCoCopyList());
						}
					}
			return true;
		}

		
		private class WirePortCopyPair implements Serializable
		{
			private static final long serialVersionUID = 1L;
			private WirePort thisCopyPort;
			private boolean negative;
			public boolean isNegative() {
				return negative;
			}
			public void setNegative(boolean negative) {
				this.negative = negative;
			}
			private WirePort originalOtherPort;
			public WirePortCopyPair(WirePort thisCopyPort,
					WirePort originalOtherPort) {
				super();
				this.originalOtherPort = originalOtherPort;
				this.thisCopyPort = thisCopyPort;
			}
			
			
			public WirePortCopyPair(WirePort thisCopyPort, WirePort originalOtherPort,
					boolean negative) {
				super();
				this.originalOtherPort = originalOtherPort;
				this.thisCopyPort = thisCopyPort;
				this.negative = negative;
			}
			public WirePort getThisCopyPort() {
				return thisCopyPort;
			}
			public void setThisCopyPort(WirePort thisCopyPort) {
				this.thisCopyPort = thisCopyPort;
			}
			public WirePort getOriginalOtherPort() {
				return originalOtherPort;
			}
			public void setOriginalOtherPort(WirePort originalOtherPort) {
				this.originalOtherPort = originalOtherPort;
			}
			
			
		}
		
		@Override
		public void postCopyCallback(Map<Actionable, Copyable<?>> copyPairs) {
			super.postCopyCallback(copyPairs);
			if(this.wirePortPairs == null)
				return;
			for (WirePortCopyPair portPair:this.wirePortPairs)
			{
				//find the copy of the other end of this pair
				Copyable<?> other  = copyPairs.get(portPair.getOriginalOtherPort().getStructuralOwner());
				if (other != null)
				{
					int i = portPair.getOriginalOtherPort().getStructuralOwner().getWirePorts().indexOf(portPair.getOriginalOtherPort());
					if (i>= 0)
					{
						if (other instanceof PhysicalStructure)
						{
							if(((PhysicalStructure) other).getWirePorts().size()>i)
							{
								WirePort copyOther = ((PhysicalStructure) other).getWirePorts().get(i);
								if (copyOther != null)
								{
									Wire newWire = WirePort.formConnection(portPair.thisCopyPort, copyOther, portPair.isNegative());
									if(newWire != null)
									{
										for (Viewable.ViewMode view:getMachine().getViews())//get the machines views, not these views.
											newWire.addViewMode(view);										
									}
								}
							}
							
						}
					}
				
				}
			}
		}



		public CopyAction copy() {
			CopyAction copy = super.copy();
			try{
				Copy copyImpl = (Copy) copy;
			
				copyImpl.copy = this.copy;
				return copyImpl;
			}catch(Exception e)
			{
				return null;
			}	
		}

		
		
		public PhysicalStructure getCopy() {
			return copy;
		}

		
		public boolean undoAction() {
			if (copy != null)
			{
				try{
					copy.unCopy();
				}catch(FailedCopyException e)
				{
					return false;
				}
			}

			return true;
		}
		
	}*/

	protected class StaticInfo extends StaticMaterialInformation
	{

		@Override
		public Actionable getControlled() {
			return PhysicalStructure.this;
		}
		
		public boolean isStatic() {
		
			return PhysicalStructure.this.isStatic();
		}


		
	}
	
	
	
	
	@Override
	public Collection<Property> getPropertySet() {
		 Collection<Property> properties = super.getPropertySet();
		 
		 Property material = new Property(Property.PropertyType.MATERIAL,this.interpreter.getMaterialProperties());		 
		 properties.add(material);
		 properties.add(new Property(Property.PropertyType.SOUND,this.interpreter.getSurfaceProperties()));		 
	 
		 properties.add( new Property(Property.PropertyType.PHYSICAL,this.interpreter.getStore()));
		 
		 
		 Property appearance = new Property(Property.PropertyType.APPEARANCE,this.interpreter.getAppearanceStore());		 
		 properties.add(appearance);
		 
		return properties;
	}



	
	private class ViewModeImpl extends ViewModeAction
	{
		
	}
	
	protected abstract class ControlImpl extends ControlAction
	{

		
		public boolean doAction() {
			
			if (resolve)
			{
				if (isFirstUse())
				{
					
						PhysicalStructure.this.collisionMemberStateChange();
					
				}
			}else
			{
				
			}
		
			return 	super.doAction();
		}

		
		public boolean undoAction() {

	
				return 	super.undoAction();

		}
		
		
		public ControlAction copy() {
			ControlAction copy = super.copy();
			try{
				ControlImpl copyImpl = (ControlImpl) copy;
	
				return copyImpl;
			}catch(Exception e)
			{
				return null;
			}	
		}
		
	}
/*	
	protected class GroupInfo extends GroupInformation
	{

		
		public Actionable getGroupActionable() {
			return getStructuralCollisionMember().getGroup();
		}
		
	}*/

	
	
/*	protected class AddToGroup extends AddToGroupAction
	{

		@Override
		public AddToGroupAction copy() {
			AddToGroup copy = new AddToGroup();
			copy.setGroup(getGroup());
			return copy;
		}

		@Override
		public boolean doAction() {
			
			super.getGroup().getAction(Action.ADD_MEMBER);
			
			return true;
		}

		@Override
		public boolean undoAction() {
			super.getGroup().removeMember(PhysicalStructure.this);
			return true;
		}
		
	}*/
	
	protected class GroupInfo extends GroupInformation
	{
		@Override
		public Actionable getControlled() {
			return PhysicalStructure.this;
		}
		
		
		public List<Actionable> getGroupMembers(boolean includeStatics) {
			CollisionMember member = getStructuralCollisionMember();
			//member.resolveCollisions();
			if (member != null)
			{
				Set<CollisionMember> group = new HashSet<CollisionMember>();
					member.getGroup(includeStatics? null:staticsDivider,group);
				group.add(member);
				List<Actionable> actionList = new ArrayList<Actionable>();
				for(CollisionMember element:group)
				{
					if( element.getActionable()!=null)
						actionList.add(element.getActionable());
				}
				return actionList;
			}else
				return null;
		}

		@Override
		public Collection<StructureGroup> getAssignedGroups() {
			return getMachine().getGroupManager().getGroups(PhysicalStructure.this);
		}

		@Override
		public StructureGroup getAssignedGroup() {
			return getMachine().getGroupManager().getGroup(PhysicalStructure.this);
		}
	
		
		
		
	}
	
	protected static class Delete extends DeleteAction
	{
		private final StructureInterpreter interpreter;
		private final MachineInterpreter machineInterpreter;
		private  PhysicalStructure structure;
		public Delete(StructureInterpreter interpreter,MachineInterpreter machineInterpreter,PhysicalStructure structure) {
			super();
			this.structure = structure;
			this.interpreter = interpreter;
			this.machineInterpreter = machineInterpreter;
			super.setStore(machineInterpreter.getStore());
		}

		private List<DeleteAction> wireDeletions;
	
		public boolean doAction() {
			if(super.isFirstUse())
			{
				wireDeletions = new ArrayList<DeleteAction>();
				for (WirePort port:structure.getWirePorts())
				{
					Collection<Wire> wires = structure.getMachine().getWires().getWires(port.getReference());
					
					//remove each wire
					if (wires != null)
					{
						for (Wire wire:wires)
						{
							try {
								wireDeletions.add((DeleteAction) wire.getAction(Action.DELETE));
							} catch (ActionTypeException e) {
								StateManager.logError(e);
							}
						}
					}
				}
					structure = null;
					setFirstUse(false);
			}
			
			for (DeleteAction delete:wireDeletions)
				delete.doAction();
			
			machineInterpreter.removeStructure(interpreter.getStore());
			
			return true;
		}

		
		public boolean undoAction() {
			machineInterpreter.addStructure(interpreter.getStore());
			
			for (DeleteAction delete:wireDeletions)
				delete.undoAction();
			return true;
		}
		
	}
	
	protected class CollisionInfo extends CollisionInformation
	{
		@Override
		public Actionable getControlled() {
			return PhysicalStructure.this;
		}
		
		
		public CollisionMember getCollisionMember() {
			
			return PhysicalStructure.this.getStructuralCollisionMember();
		}
		
	}

	protected class PhysicalInfo extends PhysicalInfoAction
	{
		@Override
		public Actionable getControlled() {
			return PhysicalStructure.this;
		}
		
		
		public PhysicalStructure getPhysicalStructure() {
			//if(isPhysical())
				return PhysicalStructure.this;
			//else
			//	return null;
		}
		
	}
	
	protected static class Select extends SelectAction
	{
		

		
		public boolean doAction() 
		{

			return true;
		}



		public boolean undoAction() 
		{
		
			return true;

		}
		
		
		
	};

	private static class SetLayer extends SetLayerAction{
		private Reference oldLayer;
		
		private PhysicalStructureInterpreter interpreter;
		private SetLayer(PropertyStore store) {
			super();
			interpreter = new PhysicalStructureInterpreter(store);
		}

		@Override
		public boolean doAction() {
			if(isFirstUse())
			{
				oldLayer = interpreter.getLayer();
			}
			interpreter.setLayer(getLayer());
			interpreter.refresh();
			return true;
		}

		@Override
		public boolean undoAction() {
			interpreter.setLayer(oldLayer);
			interpreter.refresh();
			return true;
		}
		
	}
	
	private final static GroupDivider staticsDivider = new GroupDivider()
	{
		
		public boolean dividesGroup(CollisionMember member)
		{
			return member.isStatic() || member.isDeleted();
		}
		
		
		public boolean propagatesGroup(CollisionMember member) {
			return member.propagatesCollisions();
		}
		
		public boolean isGroupable(CollisionMember member) {
			return member.isSelectable();
		}
	};


	
	
}
