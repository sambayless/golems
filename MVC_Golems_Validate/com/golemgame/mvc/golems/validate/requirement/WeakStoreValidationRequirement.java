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
import com.golemgame.mvc.golems.validate.ValidationFactory;
import com.golemgame.mvc.golems.validate.ValidationFailureException;

/**
 * Will attempt to validate the given key. Enforces the requirement that the key is a 
 * property store, then validates if the supplied factory has a validator for the store's class type.
 * @author Sam
 *
 */
public class WeakStoreValidationRequirement extends DataTypeRequirement {
	private final ValidationFactory target;
	
	public WeakStoreValidationRequirement(String key, ValidationFactory target) {
		super(DataType.Type.PROPERTIES,key);
		this.target = target;
	}
	
	@Override
	protected void enforeValue(DataType value) {
		PropertyStore store = (PropertyStore) value;
		target.makeValid(store);
	}

	@Override
	protected boolean testValue(DataType value)
			throws ValidationFailureException {
		PropertyStore store = (PropertyStore) value;
		target.isValid((PropertyStore)store);	
		return true;
	}


}
