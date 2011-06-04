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
package com.golemgame.mvc.golems.validate.requirement;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.DataType.Type;
import com.golemgame.mvc.golems.validate.ValidationFailureException;

public class DataTypeRequirement implements Requirement {

	private final DataType.Type requiredType;
	private final DataType defaultValue;
	private final String key;
	

	public DataTypeRequirement(Type requiredType, DataType defaultValue,String key) {
		super();
		this.requiredType = requiredType;
		this.defaultValue = defaultValue;
		this.key = key;
	}
	
	public DataTypeRequirement(Type requiredType,String key) {
		super();
		this.requiredType = requiredType;
		this.defaultValue = null;
		this.key = key;
	}



	public void enfore(PropertyStore store) {
		DataType v;
		if(defaultValue == null)
		{
			v= store.getProperty(key,requiredType);
		}else
		{
			v = store.getProperty(key, defaultValue);
		}
		enforeValue(v);
	}

	public boolean test(PropertyStore store) throws ValidationFailureException {
		DataType v =  store.getProperty(key);
		if (v == null &! (requiredType == null))
			return false;
		if (v.getType() == requiredType)
			return true;
		return testValue(v);
	}

	protected boolean testValue(DataType value)throws ValidationFailureException 
	{
		return true;
	}
	
	protected void enforeValue(DataType value)
	{
		
	}

/*	public DataType enfore(DataType value) {
		if(!DataType.isType(value, requiredType))
			return defaultValue.deepCopy();
				
		return value;
	}*/

}
