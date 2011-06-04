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
package com.golemgame.functional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.WirePortInterpreter;
import com.golemgame.structural.structures.PhysicalStructure;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.action.mvc.AddWireAction;
import com.golemgame.tool.action.mvc.RemoveWireAction;

/**
 * This class just maintains a list of wires in sync with a wire repository, connected to a particular output port, along with the neccesary info to
 * connect those wires to wire ports and the wire set.
 * 
 * The wire models live only in the output nodes; input nodes are only used for their references.
 * However, the input nodes also hold a list of wires for easy access.
 * 
 * @author Sam
 *
 */
public class LocalWireManager implements Actionable, SustainedView{

	public static final LocalWireManager dummyManager = new LocalWireManager(new PropertyStore());

	private int portNumber;
	private boolean isInput;
	
	
	private WirePortInterpreter interpreter;
	

	private PhysicalStructure structure;

	private Map<PropertyStore, Wire> wireMap;
	private WirePort port;
	
	public LocalWireManager(PropertyStore store) {
		super();
		interpreter = new WirePortInterpreter(store);
		interpreter.getStore().setSustainedView(this);
		this.wireMap = new HashMap<PropertyStore,Wire>();
		
	}

	public void invertView(PropertyStore store) {
		
		
	}
	
	public void remove() {
		for(Wire wire:this.getWires())
			wire.remove();
	}
	public void refresh() {
		if(isInput)
			return;//for the moment, only output nodes hold the wires.
		
		this.interpreter.getStore().setSustainedView(this);
		
		//ensure all wires are represented properly
		CollectionType structureCollection = interpreter.getWires();

		ArrayList<Wire> toRemove = new ArrayList<Wire>();

		for(Wire wire:this.wireMap.values())
		{
			if (!structureCollection.getValues().contains(wire.getStore()))
			{
				//delete this machine
				toRemove.add(wire);
			}
		}
	
		for(Wire wire:toRemove)
		{
			//remove the VIEW from this machine
			wire.remove();
			this.wireMap.remove(wire.getStore());
			this.structure.getMachine().getWires().removeWire(wire);
		
		}
	
		for(DataType data:structureCollection.getValues())
		{
			if(! (data instanceof PropertyStore))
					continue;
			
			Wire structure = this.wireMap.get((PropertyStore)data);
			
			if(structure==null)
			{
				Wire wire = new Wire((PropertyStore)data);				
				
				wire.attachToManager(this);
				
		
				
				this.wireMap.put((PropertyStore)data, wire);
				
				//register this wire with the wire manager somehow
				this.structure.getMachine().getWires().addWire(wire);
				
				wire.refresh();
				
				wire.addViewModes(this.getStructure().getViews());
			}
		}
	} 

	public PropertyStore getStore() {

		return interpreter.getStore();
	}

	public void setPortNumber(int portNum) {
		this.portNumber = portNum;
	}

	public void setIsInput(boolean b) {
		this.isInput = b; 
	}

	public boolean isInput() {

		return isInput;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public PhysicalStructure getStructure() {
		return structure;
	}

	public void setWirePort(WirePort port) {
		this.port = port;
	}

	public void setStructure(PhysicalStructure physicalStructure) {
		
		this.structure = physicalStructure;
	}

	public Reference getReference() {
	
		return interpreter.getID();
	}

	public WirePort getWirePort() {
		return port;
	}

	public Collection<Wire> getWires()
	{
		return this.wireMap.values();
	}
	
	public Action<?> getAction(Type type) throws ActionTypeException {
		if(isInput)
			throw new ActionTypeException();//for the moment, only output nodes hold the wires.
		switch(type)
		{
		//SOMETIMES the sustained view for this local wire manager's interpreter is WRONG. Incorrect: sometimes, wires (probably those that were copied) have the wrong localwiremanager set.
			case ADD_WIRE:
				return new AddWire(interpreter);
			case REMOVE_WIRE:
				return new RemoveWire(interpreter);
		}
		
		throw new ActionTypeException();

	}
	
	
	private  class AddWire extends AddWireAction
	{
		private final WirePortInterpreter interpreter;
		
		public AddWire(WirePortInterpreter interpreter) {
			super();
			this.interpreter = interpreter;
		}
		
		private PropertyStore previousWire = null;
		@Override
		public boolean doAction() {
			
			previousWire= interpreter.addWire(this.getComponent());
			
			interpreter.refresh();
			this.getComponent().getSustainedView().refresh();
			return true;
		}

		@Override
		public boolean undoAction() {
			interpreter.removeWire(this.getComponent());
			if (previousWire != null)
				interpreter.addWire(previousWire);
			interpreter.refresh();
			this.getComponent().getSustainedView().refresh();
			return true;
		}
		

		
	}
	
	private static class RemoveWire extends RemoveWireAction
	{
	private final WirePortInterpreter interpreter;
		
		public RemoveWire(WirePortInterpreter interpreter) {
			super();
			this.interpreter = interpreter;
		}
		
		@Override
		public boolean doAction() {
			interpreter.removeWire(this.getComponent());
			interpreter.refresh();
			this.getComponent().getSustainedView().refresh();
			super.setFirstUse(false);
			return true;
		}

		@Override
		public boolean undoAction() {
			interpreter.addWire(this.getComponent());			
			interpreter.refresh();
			this.getComponent().getSustainedView().refresh();
			return true;
		}
		
	}
	
}
