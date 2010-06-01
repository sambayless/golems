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

