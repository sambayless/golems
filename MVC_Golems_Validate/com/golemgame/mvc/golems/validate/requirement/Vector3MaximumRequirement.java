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
import com.golemgame.mvc.Vector3Type;
import com.golemgame.mvc.golems.validate.ValidationFailureException;
import com.jme.math.Vector3f;

public class Vector3MaximumRequirement extends DataTypeRequirement {

	private final float maxX;
	private final float maxY;
	private final float maxZ;

	public Vector3MaximumRequirement(float minX, float minY, float minZ,String key) {
		super(DataType.Type.VECTOR3, new Vector3Type(new Vector3f(minX,minY,minZ)), key);
		this.maxX = minX;
		this.maxY = minY;
		this.maxZ = minZ;
	}
	
	@Override
	protected boolean testValue(DataType value) throws ValidationFailureException {
		Vector3Type v =(Vector3Type) value;
		if(v.getValue().x > maxX)
			return false;
		if(v.getValue().y > maxY)
			return false;
		if(v.getValue().z > maxZ)
			return false;
		return super.testValue(value);
	}
	@Override
	protected void enforeValue(DataType value) {
		Vector3Type v =(Vector3Type) value;
		
		if(v.getValue().x > maxX)
			v.getValue().x = maxX;
		if(v.getValue().y > maxY)
			v.getValue().y = maxY;
		if(v.getValue().z > maxZ)
			v.getValue().z = maxZ;

	}




	
}
