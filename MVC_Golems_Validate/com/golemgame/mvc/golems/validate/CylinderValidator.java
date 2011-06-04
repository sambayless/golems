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

import com.golemgame.mvc.golems.CylinderInterpreter;
import com.golemgame.mvc.golems.validate.requirement.FloatBoundRequirement;

public class CylinderValidator extends PhysicalValidator {


	public CylinderValidator() {
		super();
		super.requireData(CylinderInterpreter.CYL_HEIGHT,  1f);
		super.requireData(CylinderInterpreter.CYL_RADIUS,  0.5f);
		
		super.addRequirement( new FloatBoundRequirement(MIN_EXTENT*2f, Float.POSITIVE_INFINITY,CylinderInterpreter.CYL_HEIGHT));
		super.addRequirement( new FloatBoundRequirement(MIN_EXTENT, Float.POSITIVE_INFINITY,CylinderInterpreter.CYL_RADIUS));
	}
/*
 * 	
	
	
		SphereInterpreter sphere = new SphereInterpreter();
		sphere.setEllispoid(false);
		sphere.setExtent(new Vector3f(0.5f,0.5f,0.5f));
		loadStructureDefaults(sphere.getStore());
		defaults.put(GolemsClassRepository.SPHERE_CLASS, sphere.getStore());
		
		PyramidInterpreter pyr = new PyramidInterpreter();
		pyr.setExtent(new Vector3f(0.5f,0.5f,0.5f));
		loadStructureDefaults(pyr.getStore());
		defaults.put(GolemsClassRepository.PYRAMID_CLASS, pyr.getStore());
		
		CapsuleInterpreter cap = new CapsuleInterpreter();
		cap.setHeight(2f);
		cap.setRadius(0.5f);
		loadStructureDefaults(cap.getStore());
		defaults.put(GolemsClassRepository.CAP_CLASS, cap.getStore());
		
		
		loadStructureDefaults(cyl.getStore());
		defaults.put(GolemsClassRepository.CYL_CLASS, cyl.getStore());
 */
}

