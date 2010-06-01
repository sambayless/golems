package com.golemgame.mechanical.layers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.golemgame.mechanical.StructuralMachine;
import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.LayerInterpreter;
import com.golemgame.mvc.golems.LayerRepositoryInterpreter;
import com.golemgame.mvc.golems.StructureInterpreter;
import com.golemgame.states.StateManager;
import com.golemgame.structural.Structural;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.action.mvc.AddComponentAction;
import com.golemgame.tool.action.mvc.CopyComponentAction;
import com.golemgame.tool.action.mvc.RemoveComponentAction;

/**
 * Each layer contains a set of references to components.
 * @author Sam
 *
 */
public class LayerRepository implements Actionable, SustainedView{

	//private Map<Reference,Layer> referenceToLayer = new HashMap<Reference,Layer>();
	private Map<PropertyStore,Layer> layers = new HashMap<PropertyStore,Layer>();
	//private Map<Structural,Layer> layerMap = new HashMap<Structural,Layer>();
	private LayerRepositoryInterpreter interpreter;
	private CopyOnWriteArrayList<LayerRepositoryListener> listeners = new  CopyOnWriteArrayList<LayerRepositoryListener>();
	
	private final StructuralMachine machine;
	public StructuralMachine getMachine() {
		return machine;
	}

	private Layer activeLayer = null;
	
	public void refresh() {
		//ensure the layer to reference map matches the repository
		
		CollectionType collection = interpreter.getLayers();
		
		//remove any layers that we shouldn't have...
		ArrayList<Layer> toRemove = new ArrayList<Layer>();
		for(PropertyStore store:layers.keySet())
		{
			if(!collection.contains(store))
			{
				//delete this layer
				toRemove.add(layers.get(store));
				//layers.remove(store);
							
			}
		}		
		
		for(Layer layer:toRemove)
		{
			if(layer!=null)
			{
				
				deleteLayer(layer);
			}	
		}
		

		
		for(DataType data:collection.getValues())
		{
			if(data instanceof PropertyStore)
			{
				PropertyStore layerStore = (PropertyStore)data;
				
				if(!layers.containsKey(layerStore))
				{
					//construct and add a new layer
					
					Layer layer = new Layer(this,layerStore);
					layers.put(layerStore, layer);
				
					addLayer(layer);
				}
			}
		}
		
		if(! this.layers.containsValue(activeLayer))
		{
			activeLayer = null;
			setActiveLayer(this.getDefaultLayer());
		}
	}
	
	private void addLayer(Layer layer)
	{
		for(LayerRepositoryListener listener:listeners)
		{
			listener.layerAdded(layer);
		}
	}

	private void deleteLayer(Layer layer) {
		/*for(Reference ref:layer.getReferences())
		{
			referenceToLayer.remove(ref);
		}*/
		layers.remove(layer.getStore());
		for(LayerRepositoryListener listener:listeners)
		{
			listener.layerRemoved(layer);
		}
	}

	public LayerRepository(PropertyStore store,StructuralMachine machine) {
		super();
		this.machine = machine;
		interpreter = new LayerRepositoryInterpreter (store);
		interpreter.getStore().setSustainedView(this);
	}

	public PropertyStore getStore() {
	
		return interpreter.getStore();
	}

	public void remove() {
		
	}

	public void setActiveLayer(Layer layer)
	{
		if(this.activeLayer!=layer)
		{
			Layer oldActive = activeLayer;
			activeLayer = layer;
			
			if(oldActive!=null)
			{
				oldActive.broadcastState();
			}
			activeLayer.broadcastState();
		}
	}
	
	public void registerRepositoryListener(LayerRepositoryListener listener)
	{
		this.listeners.add(listener);
	}
	
	public void removeRepositoryListener(LayerRepositoryListener listener){
		this.listeners.remove(listener);
	}

	public Collection<Layer> getLayers() {
		return layers.values();
	}

	public Action<?> getAction(Type type) throws ActionTypeException {
		switch(type)
		{
			case  ADD_COMPONENT:		
				return new AddComponent(getStore());
			case REMOVE_COMPONENT:		
				return new RemoveComponent(getStore(),this);
			case COPY_COMPONENT:		
				return new CopyComponent(getStore(),this);
		}
		throw new ActionTypeException();
	}
	

	private static class AddComponent extends AddComponentAction
	{
		
		private final LayerRepositoryInterpreter interpreter;
		
		public AddComponent(PropertyStore store) {
			super();
			this.interpreter = new LayerRepositoryInterpreter(store);
		}

		@Override
		public boolean doAction() {
			if(!interpreter.getLayers().contains(getComponent()))
				interpreter.addLayer(getComponent());
			interpreter.refresh();
			return true;
		}

		@Override
		public boolean undoAction() {
			interpreter.removeLayer(this.getComponent());
			interpreter.refresh();
			return true;
		}
		
	}
	
	private static class RemoveComponent extends RemoveComponentAction
	{
		private final LayerRepositoryInterpreter interpreter;
		private LayerRepository tempRepository;
		public RemoveComponent(PropertyStore store,LayerRepository tempRepository) {
			super();
			this.tempRepository = tempRepository;
			this.interpreter = new LayerRepositoryInterpreter(store);
		}
		
		private AddComponentAction addAction = null;
		private Collection<RemoveComponentAction> removeComponents = new ArrayList<RemoveComponentAction>();
		@Override
		public boolean doAction() {
			
			if(isFirstUse())
			{
				Layer layer = tempRepository.layers.get(this.getComponent());
				
				//delete all the structures in this layer
				if(layer!=null)
				{
					for(Structural s:layer.getMembers())
					{
						try {
							RemoveComponentAction rem = (RemoveComponentAction) tempRepository.machine.getAction(Action.REMOVE_COMPONENT);
							rem.setComponent(s.getStore());
							removeComponents.add(rem);
								
						} catch (ActionTypeException e) {
							StateManager.logError(e);
						}
						
					}
				}
				
				
				
				if(interpreter.getLayers().getValues().size()==1)
				{//ensure atleast one layer
					try {
						addAction= (AddComponentAction) tempRepository.getAction(Action.ADD_COMPONENT);
						LayerInterpreter newLayer = new LayerInterpreter();
						newLayer.setLayerName("Layer 1");
						addAction.setComponent(newLayer.getStore());
					
					} catch (ActionTypeException e) {
						StateManager.logError(e);
					}
					
					
				}
				tempRepository = null;
			}
			
		
			if(addAction != null)
				addAction.doAction();
			
			for(RemoveComponentAction rem:removeComponents)
			{
				rem.doAction();
			}
			interpreter.removeLayer(this.getComponent());	
			interpreter.refresh();
			return true;
		}

		@Override
		public boolean undoAction() {
			if(addAction != null)
				addAction.undoAction();
			if(!interpreter.getLayers().contains(getComponent()))
				interpreter.addLayer(getComponent());
			for(RemoveComponentAction rem:removeComponents)
			{
				rem.undoAction();
			}
			interpreter.refresh();
			return true;
		}
		
	}
	
	private static class CopyComponent extends CopyComponentAction
	{
		private final LayerRepositoryInterpreter interpreter;
		private LayerRepository tempRepository;
		public CopyComponent(PropertyStore store,LayerRepository tempRepository) {
			super();
			this.tempRepository = tempRepository;
			this.interpreter = new LayerRepositoryInterpreter(store);
		}
		private Collection<CopyComponentAction> copyStructures = new ArrayList<CopyComponentAction>();

		private Collection<AddComponentAction> addLayers = new ArrayList<AddComponentAction>();
		
		private Collection<PropertyStore> newLayers = new ArrayList<PropertyStore>();

		
		@Override
		public Collection<PropertyStore> getCopiedComponents() {
			return newLayers;
		}

		@Override
		public boolean doAction() {
			
			//for each layer, create a copy, then collect all structures in that layer and copy them, assigning their layer to the new layer.
			if(isFirstUse())
			{
				for(PropertyStore layerStore: this.getComponents())
				{
					Layer layer = tempRepository.layers.get(layerStore);
				
					if(layer!=null)
					{
						PropertyStore newLayer = layerStore.deepCopy();
						LayerInterpreter newLayerInterpreter = new LayerInterpreter(newLayer);
						newLayerInterpreter.setID(Reference.createUniqueReference());
						newLayerInterpreter.setLayerName(newLayerInterpreter.getLayerName() + " (Copy)");
						
						try {
							AddComponentAction addLayer = (AddComponentAction)tempRepository.getAction(ADD_COMPONENT) ;
							addLayer.setComponent(newLayer);
							if(addLayer.doAction())
							{
								newLayers.add(newLayer);
								addLayers.add(addLayer);
								try {
									CopyComponentAction copy = (CopyComponentAction) tempRepository.getMachine().getAction(Action.COPY_COMPONENT);
				
									//copy all the structures in this layer
									for(Structural s:layer.getMembers())
									{							
										copy.getComponents().add(s.getStore());					
									}
									if(copy.doAction())
									{	
										copyStructures.add(copy);//have to not only copy the components, but assign them to this new layer...
									
										for(PropertyStore store:copy.getCopiedComponents())
										{
											//for each structure, add it to the new layer
											StructureInterpreter interp = new StructureInterpreter(store);
											interp.setLayer(newLayerInterpreter.getID());
										}
									}
								} catch (ActionTypeException e) {
									StateManager.logError(e);
								}
							}
						} catch (ActionTypeException e) {
							StateManager.logError(e);
						}
					}
					
					
				}
				tempRepository = null;//nullify this reference.
			}else{
				for(AddComponentAction a:this.addLayers)
					a.doAction();
				
				for(CopyComponentAction c:this.copyStructures)
					c.doAction();
			}
			
			interpreter.refresh();
			return true;
		}

		@Override
		public boolean undoAction() {
			
			for(CopyComponentAction c:this.copyStructures)
				c.undoAction();
			
			for(AddComponentAction a:this.addLayers)
				a.undoAction();
			
			interpreter.refresh();
			return true;
		}
		
	}


	public String generateUniqueName() {
		int highest = 1;
		boolean search = true;
		while(search){
			search = false;
			for(Layer layer:getLayers())
			{
				if (layer.getName().toUpperCase().contains("LAYER " + highest))
				{
					search = true;
					highest++;
				}
			}
		}
		return "Layer " + highest;
	}
	
	public Collection<Layer> getEditableLayers()
	{
		Collection<Layer>  layerSet = new ArrayList<Layer>();
		for(Layer layer:getLayers())
		{
			if((!layer.isLocked())&& layer.isVisible())
				layerSet.add(layer);
		}
		return layerSet;
	}

	public Layer getActiveLayer() {
		if(activeLayer ==null)
			activeLayer = getDefaultLayer();
		return activeLayer;
	}
/*
	public boolean isSelectable(Reference id) {
		Layer layer = referenceToLayer.get(id);
		if(layer==null)
			return true;//default
		return layer.isEditable();
	}*/
	
	public Layer getDefaultLayer()
	{
		if(getLayers().isEmpty())
			return null;
		
		
		
		return getLayers().iterator().next();
	}

	/**
	 * Null references are mapped to the default layer
	 * @param layer
	 * @return
	 */	
	public Layer getLayer(Reference layerID) {
		Layer ret = getDefaultLayer();
		for(Layer layer:layers.values())
		{
			if(layer.getID().equals(layerID))
			{
				ret = layer;
				break;
			}
		}
		return ret;
	}

	/**
	 * Safely set the layer for a structure, ensuring that all other layers dont contain that structure.
	 * @param physicalStructure
	 * @param reference
	 *//*
	public void setLayer(Structural physicalStructure,
			Reference reference) {
		
		Layer oldLayer = layerMap.get(physicalStructure);
		if((oldLayer!=null) && (! oldLayer.getID().equals(reference)))
		{
			oldLayer.removeStructure(physicalStructure);
		}
		
		layerMap.put(physicalStructure, getLayer(reference));
		getLayer(reference).addStructure(physicalStructure);
		
	}*/
	
/*	public Layer getLayer(PhysicalStructure structure)
	{
		return layerMap.get(structure);
	}
*/
	public void clearStructures() {
	//	layerMap.clear();
		for(Layer layer:layers.values())
			layer.clearStructures();
	}

	public void refreshView() {
		machine.refreshView();
	}

	public boolean hasLayer(Reference layerID) {
		for(Layer layer:layers.values())
		{
			if(layer.getID().equals(layerID))
			{
				return true;
				
			}
		}
		return false;
	}

	public void validateActiveLayer() {
		if(!activeLayer.isEditable())
		{
			//change the active layer to a not locked one, if possible
			for(Layer l:this.getLayers())
			{
				if(l.isEditable())
				{
					this.setActiveLayer(l);
					break;
				}
			}
		}
	}
}
