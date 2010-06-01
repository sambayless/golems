package com.golemgame.functional;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.golemgame.functional.component.BComponent;
import com.golemgame.mechanical.StructuralMachine;
import com.golemgame.model.Model;
import com.golemgame.model.ModelListener;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.mvc.Reference;
import com.golemgame.structural.Structural;
import com.golemgame.views.ViewManager;
import com.golemgame.views.Viewable;


/**
 * This is a repository of wire connections. All wires in this repository are unique.
 * Each machine has its own wire repository.
 * @author Sam
 *
 */
public class Wires implements Serializable, Viewable {
	private static final long serialVersionUID =1;
	private Set<Wire> wireSet = new HashSet<Wire>();
	
	private NodeModel model = new NodeModel();
	
	private Map<Reference,Collection<Wire>> wireReferences = new WeakHashMap<Reference,Collection<Wire>>();

	//WATCH OUT: REFERENCE is from the MVC package, not related to WEAKreference here!
	private Map<Reference, WeakReference<WirePort>> portMap = new WeakHashMap<Reference,WeakReference<WirePort>>();
	private Map<Model, WeakReference<WirePort>> portModelMap = new WeakHashMap<Model,WeakReference<WirePort>>();

	private ViewManager viewManager = new ViewManager();
	
	/*	
	*//**
	 * This map holds all the wires that each port contains.
	 *//*
	private Map<WirePort,Collection<Wire>> portWireMap = new WeakHashMap<WirePort,Collection<Wire>> ();
	*/
	public NodeModel getModel() {
		return model;
	}
	private StructuralMachine machine;
	
	private ModelListener portListener = new ModelListener()
	{

		private static final long serialVersionUID = 1L;

		public void modelMoved(Model source) {
			WeakReference<WirePort> sourceRef = portModelMap.get(source);
			if (sourceRef == null)
				return;
			WirePort port = sourceRef.get();
			if (port == null)
				return;
			
			
			
			for (Wire wire:port.getWires())
			{
				WeakReference<WirePort> otherRef = portMap.get( wire.getReference(!port.isInput()));
				if (otherRef != null)
				{
					WirePort other = otherRef.get();
					if (other != null)
					{
						port.getModel().updateWorldData();
						other.getModel().updateWorldData();
						wire.refreshPosition(port.getModel().getWorldTranslation(), other.getModel().getWorldTranslation());
					}
				}
		
			}
		}
		
	};
	
	public void addToMachine(StructuralMachine machine)
	{
		this.machine = machine;
		machine.getModel().addChild(getModel());
		//machine.getViewManager().registerViewable(this);
	}
	
	public class WireModelListener implements ModelListener
	{
		private static final long serialVersionUID = 1L;
		
		public WireModelListener() {
			super();
		
		}
		
		public void modelMoved(Model source) {
			
		}

	};
	
	
	public void removeFromMachine()
	{
		getModel().detachFromParent();
	}
	
	/**
	 * Order does not matter.
	 * Replaces any previous wire connecting the same ports.
	 * @param port1
	 * @param port2
	 */
	public void addWire(Wire wire)
	{
		
		//wires have hashValue set to just the input and output ports.
		wire.setWires(this);	
		
		wireSet.add(wire);
		viewManager.registerViewable(wire);
/*		WeakReference<WirePort> inputRef = this.portMap.get(wire.getReference(true));
		if (inputRef != null)
		{
			WirePort input = inputRef.get();
			if (input != null)
			{
				Collection<Wire> wireCollection = this.portWireMap.get(input);
				if (wireCollection == null)
				{
					wireCollection = new ArrayList<Wire>();
					this.portWireMap.put(input, wireCollection);
				}
				wireCollection.add(wire);
			}
		}*/
		
		Collection<Wire> inputConnectedWires = wireReferences.get(wire.getReference(true));
		if (inputConnectedWires == null)
		{
			inputConnectedWires = new ArrayList<Wire>();
			wireReferences.put(wire.getReference(true), inputConnectedWires);
		}
		inputConnectedWires.add(wire);
		
		Collection<Wire> outputConnectedWires = wireReferences.get(wire.getReference(false));
		if (outputConnectedWires == null)
		{
			outputConnectedWires = new ArrayList<Wire>();
			wireReferences.put(wire.getReference(false), outputConnectedWires);
		}
		outputConnectedWires.add(wire);
		
		this.getModel().addChild(wire.getModel());
		
		wire.addViewModes(this.machine.getViews());
		wire.refresh();
		
		refreshWirePosition(wire);
	}
	private void refreshWirePosition(Wire wire) {
		WeakReference<WirePort> inputRef = portMap.get(wire.getReference(true));
		WeakReference<WirePort> outputRef = portMap.get(wire.getReference(false));
		if(inputRef == null || outputRef == null)
			return;
		WirePort input = inputRef.get();
		WirePort output = outputRef.get();
		if(input== null || output == null)
			return;
		
		input.getModel().updateWorldData();
		output.getModel().updateWorldData();
		wire.refreshPosition(input.getModel().getWorldTranslation(), output.getModel().getWorldTranslation());

	}

/*	
	private void deleteWire(Wire wire)
	{
		wireSet.remove(wire);
		wire.delete();
		
		Collection<Wire> inputConnectedWires = wireReferences.get(wire.getReference(true));
		if (inputConnectedWires !=null)
			inputConnectedWires.remove(wire);
		
		
		Collection<Wire> outputConnectedWires = wireReferences.get(wire.getReference(false));
		if (outputConnectedWires !=null)
			outputConnectedWires.remove(wire);
	}
	*/
	/**
	 * Get all wires connected to the port with the given reference.
	 * @param reference
	 * @return
	 */
	public Collection<Wire> getWires(Reference reference)
	{
		return  wireReferences.get(reference);
	/*	WeakReference<WirePort> portRef =  portMap.get(reference);
		if (portRef == null)
			return null;
		WirePort port = portRef.get();
		return port == null? null:port.getWires();*/
	}

	public Wire getWire(WirePort port1, WirePort port2)
	{
		if(port1.isInput() == port2.isInput())
			return null;
		WirePort input = port1.isInput()?port1:port2;
		WirePort output = port1 == input? port2:port1;
		
		Reference inRef = input.getReference();
		Reference outRef = output.getReference();
		
		for (Wire wire:wireSet)
		{
			if(wire.getReference(true).equals(inRef) && wire.getReference(false).equals(outRef))
				return wire;
			
		}
		return null;
	}
	
/*	*//**
	 * Delete all wires connected to the given port; return a list of deleted wires.
	 * @param toDelete
	 * @return
	 *//*
	public List<Wire> deletePort(WirePort toDelete)
	{
		
		List<Wire> removed = getWires(toDelete);
		
		for(Wire remove:removed)
			deleteWire(remove);
		
		return removed;
	}*/
/*	
	*//**
	 * Get all wires connected to the given port.
	 * @param toDelete
	 * @return
	 *//*
	public List<Wire> getWires(WirePort port)
	{
		ArrayList<Wire> wires = new ArrayList<Wire>();
		if(port.isInput())
		{
			for (Wire wire:wireSet)
				if(wire != null && wire.getInputPort().equals(port))
					wires.add(wire);
		}else
		{
			for (Wire wire:wireSet)
			{				
				if(wire != null && wire.getOutputPort().equals(port))
					wires.add(wire);
			}
					
		}

		return wires;
	}
	*/
	
	public void buildConnections(Map<WirePort, BComponent> wireMap)
	{
		for (Wire wire:this.wireSet)
		{
	
			Reference inRef = wire.getReference(true);
			Reference outRef = wire.getReference(false);
			WeakReference<WirePort> inputRef = portMap.get(inRef);
			WeakReference<WirePort> outputRef = portMap.get(outRef);
			
			WirePort input = inputRef.get();
			WirePort output = outputRef.get();
			
			BComponent inputComponent = wireMap.get(input);
			BComponent connectComponent = wireMap.get(output); 
			
			if(inputComponent == null || connectComponent == null)
				continue;
		
			
			connectComponent.attachOutput(inputComponent,wire.isNegative());
					
			
		}
	}
	
	
	
	

	public boolean addViewMode(ViewMode viewMode) {
		return viewManager.addViewMode(viewMode);
	}

	public boolean addViewModes(Collection<ViewMode> viewModes) {
		return viewManager.addViewModes(viewModes);
	}
	public boolean clearViews() {
		return viewManager.clearViews();
	}
	public void registerWirePort(WirePort port)
	{
		this.portMap.put(port.getReference(), new WeakReference<WirePort> (port) );
		this.portModelMap.put(port.getModel(), new WeakReference<WirePort>(port));
		port.getModel().addModelListener(portListener);
		portListener.modelMoved(port.getModel());
	}

	

	public Collection<ViewMode> getViews() {

		return viewManager.getViews();
	}

	public void refreshView() {
		viewManager.refreshView();
	}

	public boolean removeViewMode(ViewMode viewMode) {
		return viewManager.removeViewMode(viewMode);
	}

	
	public Wires() {
		super();

	}

	

	/**
	 * This method connects wire views to their wire ports
	 * @param reference
	 * @return
	 */
	public WirePort getWirePort(Reference reference)
	{
		//in the future, this can be made faster by caching it into a table of some sort...
		for (Structural structure: machine.getStructures())
		{
			for (WirePort wirePort: structure.getWirePorts())
			{
				if(reference.equals(wirePort.getReference()))
				{
					return wirePort;
				}
			}
		}
		return null;
	}
	
	public void removeWire(Wire wire) {
		this.wireSet.remove(wire);
		viewManager.removeViewable(wire);
		Collection<Wire> outputWires = this.wireReferences.get(wire.getReference(false));
		if (outputWires != null)
		{
			outputWires.remove(wire);
			if (outputWires.isEmpty())
				this.wireReferences.remove(wire.getReference(false));
		}
		
		Collection<Wire> inputWires = this.wireReferences.get(wire.getReference(true));
		if (inputWires != null)
		{
			inputWires.remove(wire);
			if (inputWires.isEmpty())
				this.wireReferences.remove(wire.getReference(true));
		}
		//this.wireReferences.remove(wire.getReference(false));
		//this.wireReferences.remove(wire.getReference(true));
		
	/*	WeakReference<WirePort> inputRef = this.portMap.get(wire.getReference(true));
		if (inputRef != null)
		{
			WirePort input = inputRef.get();
			if (input != null)
			{
				Collection<Wire> wireCollection = this.portWireMap.get(input);
				if (wireCollection != null)
				{
					wireCollection.remove(wire);
				}
			
			}
		}*/
		
		this.getModel().detachChild(wire.getModel());
//		wire.removeFromWires();
	}


}
