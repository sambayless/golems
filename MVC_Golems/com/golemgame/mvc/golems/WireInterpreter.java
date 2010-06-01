package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;

/**
 * Wires do NOT have any knowledge of their own location.
 *
 * @author Sam
 *
 */
public class WireInterpreter extends StoreInterpreter {
	private static final String NEGATIVE = "Wire.negative";
	
	private static final String PORT1 = "Wire.port.1";
	private static final String PORT2 = "Wire.port.2";
	
	public WireInterpreter() {
		this(new PropertyStore());
		
	}


	public WireInterpreter(PropertyStore store) {
		super(store);
		super.getStore().setClassName(GolemsClassRepository.WIRE_CLASS);
	}

	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(NEGATIVE);
		keys.add(PORT1);
		keys.add(PORT2);		
		return super.enumerateKeys(keys);
	}

	public Reference getPortID(boolean input)
	{
		if(input)
		{
			return getStore().getReference(PORT1);
		}else
		{
			return getStore().getReference(PORT2);
		}
	}
	
	
	public void setPortID(Reference portRef ,boolean port1)
	{
		if(port1)
		{
			super.getStore().setProperty(PORT1, portRef);
		}else
			getStore().setProperty(PORT2,portRef);
	}
	
	public boolean isNegative()
	{
		return super.getStore().getBoolean(NEGATIVE,false);
	}
	
	public void setNegative(boolean negative)
	{
		 super.getStore().setProperty(NEGATIVE, negative);
	}


	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(PORT1))
			return defaultStore;
		if(key.equals(PORT2))
			return defaultStore;
		return super.getDefaultValue(key);
	}
	
	
	
}
