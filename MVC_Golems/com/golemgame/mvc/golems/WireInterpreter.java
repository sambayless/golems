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
