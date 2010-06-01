package com.golemgame.mvc.golems.validate;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.CapsuleInterpreter;
import com.golemgame.mvc.golems.CylinderInterpreter;
import com.golemgame.mvc.golems.validate.requirement.FloatBoundRequirement;
import com.golemgame.mvc.golems.validate.requirement.Requirement;

public class GrappleValidator extends PhysicalValidator {

	public GrappleValidator() {
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
