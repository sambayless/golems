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
import com.golemgame.mvc.golems.validate.ValidationFailureException;

/**
 * Enforce a class type on a property store, IF this data is a property store and IF it doesnt have a classtype already
 * @author Sam
 *
 */
public class WeakClassType extends DataTypeRequirement {
	private final String classType;
	//private final String key;
	
	public WeakClassType(String key,String classType) {
		super(DataType.Type.PROPERTIES,key);
		this.classType = classType;
		//this.key = key;
	}

	@Override
	protected void enforeValue(DataType value) {
		PropertyStore store = (PropertyStore) value;
		if(store.getClassName()==null || store.getClassName().length() == 0)
		{
			store.setClassName(classType);
		}
	}

	@Override
	protected boolean testValue(DataType value)
			throws ValidationFailureException {
		PropertyStore store = (PropertyStore) value;
		return store.getClassName()!= null && store.getClassName().length()>0;
	}



}
