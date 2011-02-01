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

public class StoreValidationRequirement extends WeakClassType {
	private final ValidationFactory target;
	private final String classType;
	public StoreValidationRequirement(String storeKey,String classType, ValidationFactory target) {
		super(storeKey,classType);
		this.classType = classType;
		this.target = target;
	}

	public void enforeValue(DataType store) {
		
		target.makeValid((PropertyStore)store, classType);
	//	target.makeValid((PropertyStore)store);
			
		
	}

	public boolean testValue(DataType store) throws ValidationFailureException {
		 target.isValid((PropertyStore)store);	
		 return true;//is this behaviour correct? Yes, isValid will throw an exception if needed.
	}
}
