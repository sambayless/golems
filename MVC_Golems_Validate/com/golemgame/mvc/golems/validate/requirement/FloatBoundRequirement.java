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
import com.golemgame.mvc.golems.validate.ValidationFailureException;

public class FloatBoundRequirement extends DataTypeRequirement {
	private final float max;
	private final float min;
	public FloatBoundRequirement(float min,float max, String key) {
		super(DataType.Type.FLOAT, new FloatType(0f),key);
		this.min = min;
		this.max = max;
		
	}
	
	@Override
	protected boolean testValue(DataType value) throws ValidationFailureException {
		FloatType v =(FloatType) value;
		if(v.getValue() < min)
			return false;
		if(v.getValue() > max)
			return false;
	
		return super.testValue(value);
	}
	@Override
	protected void enforeValue(DataType value) {
		FloatType v =(FloatType) value;
		
		if(v.getValue() < min)
			v.setValue(min);
		else if (v.getValue() > max)
			v.setValue(max);
	}
}
