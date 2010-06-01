package com.golemgame.mvc.golems.validate;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.HydraulicInterpreter;
import com.golemgame.mvc.golems.validate.requirement.FloatBoundRequirement;
import com.golemgame.mvc.golems.validate.requirement.Requirement;

public class HydraulicValidator  extends PhysicalValidator {

	public HydraulicValidator() {
		super();
		super.requireData(HydraulicInterpreter.JOINT_DISTANCE, 1f);
		super.requireData(HydraulicInterpreter.JOINT_MAX_DISTANCE, 5f);
		super.requireData(HydraulicInterpreter.JOINT_MIN_DISTANCE, 0f);
		super.requireData(HydraulicInterpreter.JOINT_RADIUS, 0.5f);
	
		
		super.addRequirement( new FloatBoundRequirement(0f, Float.POSITIVE_INFINITY,HydraulicInterpreter.JOINT_DISTANCE));
		super.addRequirement( new FloatBoundRequirement(0f, Float.POSITIVE_INFINITY,HydraulicInterpreter.JOINT_MAX_DISTANCE));
		super.addRequirement( new FloatBoundRequirement(0f, Float.POSITIVE_INFINITY, HydraulicInterpreter.JOINT_MIN_DISTANCE));
		super.addRequirement( new FloatBoundRequirement(MIN_EXTENT, Float.POSITIVE_INFINITY,HydraulicInterpreter.JOINT_RADIUS));

		super.addRequirement(new Requirement(){

			public void enfore(PropertyStore store) {		
				float minDistance = store.getFloat( HydraulicInterpreter.JOINT_MIN_DISTANCE);
				float maxDistance = store.getFloat( HydraulicInterpreter.JOINT_MAX_DISTANCE);
				if(minDistance>maxDistance)
					store.setProperty(HydraulicInterpreter.JOINT_MAX_DISTANCE,minDistance);
			}

			public boolean test(PropertyStore store) {
				float minDistance = store.getFloat( HydraulicInterpreter.JOINT_MIN_DISTANCE);
				float maxDistance = store.getFloat( HydraulicInterpreter.JOINT_MAX_DISTANCE);
				if(minDistance>maxDistance)
					return false;
				return true;
			}
			
		});
		
		super.addRequirement(new Requirement(){

			public void enfore(PropertyStore store) {		
				float minDistance = store.getFloat( HydraulicInterpreter.JOINT_MIN_DISTANCE);
				float distance = store.getFloat( HydraulicInterpreter.JOINT_DISTANCE);
			
				if(distance<minDistance)
					store.setProperty(HydraulicInterpreter.JOINT_DISTANCE,minDistance);
			}

			public boolean test(PropertyStore store) {
				float minDistance = store.getFloat( HydraulicInterpreter.JOINT_MIN_DISTANCE);
				float distance = store.getFloat( HydraulicInterpreter.JOINT_DISTANCE);
				
				if(distance<minDistance)
					return false;
				return true;
			}
			
		});
		
		super.addRequirement(new Requirement(){

			public void enfore(PropertyStore store) {		
				float maxDistance = store.getFloat( HydraulicInterpreter.JOINT_MAX_DISTANCE);
				float distance = store.getFloat( HydraulicInterpreter.JOINT_DISTANCE);
				
				if(distance>maxDistance)
					store.setProperty(HydraulicInterpreter.JOINT_DISTANCE,maxDistance);
			}

			public boolean test(PropertyStore store) {
				float maxDistance = store.getFloat( HydraulicInterpreter.JOINT_MAX_DISTANCE);
				float distance = store.getFloat( HydraulicInterpreter.JOINT_DISTANCE);
				
				if(distance>maxDistance)
					return false;
				return true;
			}
			
		});
		
		

	}


}
