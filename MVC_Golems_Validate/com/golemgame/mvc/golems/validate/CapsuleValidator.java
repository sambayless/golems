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

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.CapsuleInterpreter;
import com.golemgame.mvc.golems.CylinderInterpreter;
import com.golemgame.mvc.golems.validate.requirement.FloatBoundRequirement;
import com.golemgame.mvc.golems.validate.requirement.Requirement;

public class CapsuleValidator extends PhysicalValidator {

	public CapsuleValidator() {
		super();
		super.requireData(CylinderInterpreter.CYL_HEIGHT,  1f);
		super.requireData(CylinderInterpreter.CYL_RADIUS,  0.5f);
		
		super.addRequirement( new FloatBoundRequirement(0f, Float.POSITIVE_INFINITY,CylinderInterpreter.CYL_HEIGHT));
		super.addRequirement( new FloatBoundRequirement(MIN_EXTENT, Float.POSITIVE_INFINITY,CylinderInterpreter.CYL_RADIUS));
		super.addRequirement(new Requirement(){

			public void enfore(PropertyStore store) {		
				float height = store.getFloat(CapsuleInterpreter.CYL_HEIGHT);
				float radius = store.getFloat(CapsuleInterpreter.CYL_RADIUS);
				if(height<0)
					store.setProperty(CapsuleInterpreter.CYL_HEIGHT, 0f);
			}

			public boolean test(PropertyStore store) {
				float height = store.getFloat(CapsuleInterpreter.CYL_HEIGHT);
				float radius = store.getFloat(CapsuleInterpreter.CYL_RADIUS);
				if(height<0f)
					return false;
				return true;
			}
			
		});
	}


	
	
	
}
