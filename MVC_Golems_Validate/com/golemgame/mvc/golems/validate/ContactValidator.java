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
package com.golemgame.mvc.golems.validate;

import com.golemgame.mvc.BoolType;
import com.golemgame.mvc.golems.SphereInterpreter;
import com.golemgame.mvc.golems.validate.requirement.StrictDataTypeRequirement;
import com.golemgame.mvc.golems.validate.requirement.Vector3MinimumRequirement;
import com.jme.math.Vector3f;

public class ContactValidator extends PhysicalValidator {


	public ContactValidator() {
		super();
		super.requireData(SphereInterpreter.RADII, new Vector3f(0.5f,0.5f,0.5f));
		//super.requireData(SphereInterpreter.ELLIPSOID, false);
		super.addRequirement(new StrictDataTypeRequirement(SphereInterpreter.ELLIPSOID,new BoolType(false)) );
		super.addRequirement( new Vector3MinimumRequirement(MIN_EXTENT,MIN_EXTENT,MIN_EXTENT,SphereInterpreter.RADII));
		
	}


}
