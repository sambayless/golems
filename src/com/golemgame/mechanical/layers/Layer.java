package com.golemgame.mechanical.layers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.LayerInterpreter;
import com.golemgame.states.StateManager;
import com.golemgame.structural.Structural;

public class Layer implements SustainedView {
	private final LayerRepository repository;
	//private Collection<Structural> members = new HashSet<Structural>();
	private final LayerInterpreter interpreter;
	public LayerInterpreter getInterpreter() {
		return interpreter;
	}

	private CopyOnWriteArrayList<LayerListener> listeners = new  CopyOnWriteArrayList<LayerListener>();
	private boolean visible = true;
	private boolean locked = false;
	

	public Layer(LayerRepository repository, PropertyStore store) {
		super();
		this.repository = repository;
		interpreter = new LayerInterpreter (store);
		interpreter.getStore().setSustainedView(this);
	}

	public PropertyStore getStore() {
	
		return interpreter.getStore();
	}

	public void remove() {
		
	}


	public void refresh() {
	
		for(LayerListener listener:listeners)
		{
			listener.refreshLayer();
		}
	}

	public String getName() {
		return interpreter.getLayerName();
	}

	public void addStructure(Structural structure)
	{
	//	this.members.add(structure);
	}
	
	public void removeStructure(Structural structure)
	{
	//	this.members.remove(structure);
	}
	
	
	
	public Collection<Structural> getMembers() {
		Collection<Structural> members = new ArrayList<Structural>();
		Reference id = this.getID();
		for(Structural s: repository.getMachine().getStructures())
		{
			if(s.getLayerReference()!=null && s.getLayerReference().equals(id))
				members.add(s);
		}
		return members;
	}

	public void registerListener(LayerListener listener)
	{
		this.listeners.add(listener);
	}
	
	public void removeListener(LayerListener listener){
		this.listeners.remove(listener);
	}

	public Reference getID()
	{
		return interpreter.getID();
	}
	
	public LayerRepository getRepository() {
		return repository;
	}
	
	public boolean isLocked() {
		return locked;
	}

	public void broadcastState()
	{
		for(LayerListener listener:listeners)
			listener.layerState(visible, locked, repository.getActiveLayer() == this);
	}
	
	public void setLocked(boolean locked) {
		if(locked!=this.locked)
		{
			this.locked = locked;
			for(LayerListener listener:listeners)
				listener.layerState(visible, locked, repository.getActiveLayer() == this);
			checkEditable();
		}
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		if(visible!=this.visible)
		{
			this.visible = visible;
			for(LayerListener listener:listeners)
				listener.layerState(visible, locked, repository.getActiveLayer() == this);
			repository.refreshView();//this is CRITICALLY important to selection as well as being able to see the items
			checkEditable();
		}
	}

	public void setActive() {
		repository.setActiveLayer(this);
	}

	public boolean isEditable() {
		return visible &! locked;
	}

	public void clearStructures() {
	//	members.clear();
	}

	public boolean isActive() {
		return (repository.getActiveLayer()==this);
	}
	
	private void checkEditable()
	{
		repository.validateActiveLayer();
		if(!this.isEditable())
		{
			
			for(Structural s:getMembers())
			{
				StateManager.getToolManager().deselect(s);
			}
		}
	}

	
/*	private static class AddComponent extends AddMemberAction
	{
		
		private final LayerInterpreter interpreter;
		
		public AddComponent(PropertyStore store) {
			super();
			this.interpreter = new LayerInterpreter(store);
		}

		@Override
		public boolean doAction() {
		
			interpreter.addMember(getMember());
			interpreter.refresh();
			return true;
		}

		@Override
		public boolean undoAction() {
		//	interpreter.removeMember(getMember());
			interpreter.refresh();
			return true;
		}
		
	}
	
	private static class RemoveComponent extends RemoveMemberAction
	{
		private final LayerInterpreter interpreter;
	
		public RemoveComponent(PropertyStore store) {
			super();
		
			this.interpreter = new LayerInterpreter(store);
		}
	
		@Override
		public boolean doAction() {
		
			interpreter.removeMember(getMember());
			interpreter.refresh();
			return true;
		}

		@Override
		public boolean undoAction() {
			interpreter.addMember(getMember());
			interpreter.refresh();
			return true;
		}
		
	}*/
}
