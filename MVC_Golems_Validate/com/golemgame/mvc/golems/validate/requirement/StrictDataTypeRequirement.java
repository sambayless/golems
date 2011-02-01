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
import com.golemgame.mvc.FloatType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.validate.ValidationFailureException;

public class StrictDataTypeRequirement extends DataTypeRequirement {
	private final DataType requiredValue;
	private final String key;
	public StrictDataTypeRequirement( String key, DataType requiredValue) {
		super(requiredValue.getType(), requiredValue, key);
		this.key = key;
		this.requiredValue = requiredValue;
	}
	@Override
	protected boolean testValue(DataType value) throws ValidationFailureException {
		FloatType v =(FloatType) value;
		
		return requiredValue.equals(v);
	}
	@Override
	public void enfore(PropertyStore store) {
		
		 store.setProperty(key,requiredValue.deepCopy());
		
	}
}
