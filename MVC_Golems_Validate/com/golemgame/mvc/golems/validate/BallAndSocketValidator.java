package com.golemgame.mvc.golems.validate;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.BallAndSocketInterpreter;
import com.golemgame.mvc.golems.CapsuleInterpreter;
import com.golemgame.mvc.golems.validate.requirement.FloatBoundRequirement;
import com.golemgame.mvc.golems.validate.requirement.Requirement;

public class BallAndSocketValidator extends PhysicalValidator{

	public BallAndSocketValidator() {
		super();
		super.requireData(BallAndSocketInterpreter.LEFT_JOINT_LENGTH,  1f);
		super.requireData(BallAndSocketInterpreter.LEFT_JOINT_RADIUS,  0.5f);
		super.requireData(BallAndSocketInterpreter.RIGHT_JOINT_RADIUS,  0.5f);
		
		super.addRequirement( new FloatBoundRequirement(0f, Float.POSITIVE_INFINITY,BallAndSocketInterpreter.LEFT_JOINT_LENGTH));
		super.addRequirement( new FloatBoundRequirement(MIN_EXTENT, Float.POSITIVE_INFINITY,BallAndSocketInterpreter.LEFT_JOINT_RADIUS));
		super.addRequirement( new FloatBoundRequirement(MIN_EXTENT, Float.POSITIVE_INFINITY,BallAndSocketInterpreter.RIGHT_JOINT_RADIUS));
		super.addRequirement(new Requirement(){

			public void enfore(PropertyStore store) {		
				if(store.getBoolean(BallAndSocketInterpreter.IS_UNIVERSAL))
				{
					float height = store.getFloat(BallAndSocketInterpreter.LEFT_JOINT_LENGTH);
					if(height<MIN_EXTENT)
					{
						store.setProperty(BallAndSocketInterpreter.LEFT_JOINT_LENGTH,MIN_EXTENT);
					}
				}
					
			}

			public boolean test(PropertyStore store) {
				if(store.getBoolean(BallAndSocketInterpreter.IS_UNIVERSAL))
				{
					float height = store.getFloat(BallAndSocketInterpreter.LEFT_JOINT_LENGTH);
					if(height<MIN_EXTENT)
					{
						return false;
					}
				}
				return true;
			}
			
		});
	}

}
