package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;

public class WirePortInterpreter extends StoreInterpreter {
	//wireports hold references to the wires that connect to them...
	//for now, only OUTPUT ports will hold any references.
	//Wireports also all have a reference identifying them.
	
	public final static String WIRES = "wires";
	
	public static final String ID = "id";
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(WIRES);
		keys.add(ID);	
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(ID))
			return defaultReference;
		if(key.equals(WIRES))
			return defaultCollection;
		return super.getDefaultValue(key);
	}
	
	public WirePortInterpreter(PropertyStore store) {
		super(store);
		store.setClassName(GolemsClassRepository.WIRE_PORT_CLASS);
		
	}

	
	public WirePortInterpreter() {
		this(new PropertyStore());
	}

	public Reference getID()
	{
		return getStore().getReference(ID);
	}
	
	

	public CollectionType getWires()
	{
		return getStore().getCollectionType(WIRES);
	}
	
	public PropertyStore addWire(PropertyStore wire)
	{
	
		WireInterpreter interpreter = new WireInterpreter(wire);
	
		PropertyStore oldWire = getWire(interpreter.getPortID(true));

		if(oldWire != null)
			removeWire(oldWire);
		
		
		this.getWires().addElement(wire);
		return oldWire;
	}
	
	public void removeWire(PropertyStore wire)
	{
		this.getWires().removeElement(wire);
	}


	
	public PropertyStore getWire(Reference input)
	{
		for(DataType data:getWires().getValues())
		{//can cache this data into a map in the future
			if (data instanceof PropertyStore)
			{
				WireInterpreter interpreter = new WireInterpreter((PropertyStore)data);
				if (interpreter.getPortID(true).equals(input))
				{
					return (PropertyStore) data;
				}
			}
	
		}
		return null;
	}
	
	
}
