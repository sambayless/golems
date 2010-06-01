package com.golemgame.mvc.golems.validate;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.TubeInterpreter;
import com.golemgame.mvc.golems.validate.requirement.FloatBoundRequirement;
import com.golemgame.mvc.golems.validate.requirement.Requirement;
import com.jme.math.FastMath;

public class TubeValidator extends CylinderValidator {
	private static final float MIN_RADIUS_DIF = 0.01f;
	private static final int MAX_BOXES = 40;
	public static float accuracy = 0.975f;
	
	public TubeValidator() {
		super();
		super.requireData(TubeInterpreter.RADIUS_INNER,  0.25f);
		super.addRequirement( new FloatBoundRequirement(0.01f, FastMath.TWO_PI, TubeInterpreter.ARC));

		super.addRequirement( new FloatBoundRequirement(0f, Float.POSITIVE_INFINITY, TubeInterpreter.RADIUS_INNER));
		super.addRequirement(new Requirement(){

			public boolean test(PropertyStore store) {
				float innerRadius = store.getFloat(TubeInterpreter.RADIUS_INNER);
				float radius = store.getFloat(TubeInterpreter.CYL_RADIUS);
				if(innerRadius>radius-MIN_RADIUS_DIF)
					return false;
				return true;
			}

			public void enfore(PropertyStore store)		{

				float innerRadius = store.getFloat(TubeInterpreter.RADIUS_INNER);
				float radius = store.getFloat(TubeInterpreter.CYL_RADIUS);
				if(innerRadius>radius-MIN_RADIUS_DIF)
					store.setProperty(TubeInterpreter.RADIUS_INNER, radius - MIN_RADIUS_DIF);
			}
		});
		
		super.addRequirement(new Requirement(){

			public void enfore(PropertyStore store) {
				
				store.setProperty(TubeInterpreter.RADIUS_INNER, maxInnerRadius(FastMath.PI,store.getFloat(TubeInterpreter.RADIUS_INNER),store.getFloat(TubeInterpreter.CYL_RADIUS)));

				
			}

			public boolean test(PropertyStore store)
					throws ValidationFailureException {
				
				return numBoxes(FastMath.PI,store.getFloat(TubeInterpreter.RADIUS_INNER),store.getFloat(TubeInterpreter.CYL_RADIUS))<MAX_BOXES;
			}
			
		});
	}
	
	
	public static int numBoxes(float theta, float innerRadius, float radius)
	{

		if(innerRadius>radius)
			innerRadius = radius;
		//innerRadius/=(interpreter.getRadius()*2f);
		
		float maxInnerRadius = maxRadius(radius,0.05f,0.05f);
		if(innerRadius>maxInnerRadius)
			innerRadius = maxInnerRadius;

		float boxHeight = accuracy*(radius - innerRadius);
		float boxTheta = boxTheta(boxHeight,innerRadius,radius);
		float arcToCover = FastMath.PI*1f;
		
		float numBoxesTotal = (arcToCover/boxTheta);
		return (int) numBoxesTotal;
	}
	
	public static float maxInnerRadius(float theta, float innerRadius, float radius)
	{
		float modifiedInnerRadius = innerRadius;
		int numBoxes = numBoxes(theta,modifiedInnerRadius,radius);
		while(numBoxes>MAX_BOXES)
		{
			modifiedInnerRadius/=2f;
			numBoxes = numBoxes(theta,modifiedInnerRadius,radius);
		}
		int totalIterations = 0;
		while(numBoxes<MAX_BOXES-1 && modifiedInnerRadius<innerRadius && (totalIterations++)<50)//limit on computation to keep this from slowing down
		{
			//now slowly increase until at limit
			float dif = innerRadius - modifiedInnerRadius;
			modifiedInnerRadius+= dif/2f;
			numBoxes = numBoxes(theta,modifiedInnerRadius,radius);
		}
		return modifiedInnerRadius;
	}
	
	public static float boxWidth(float theta, float rOuter)
	{
		return rOuter*FastMath.sin(theta)*2f;
	}
	
	public static float boxHeight(float theta, float rOuter, float rInner)
	{
		return rOuter*FastMath.cos(theta) - rInner;
	}
	
	public static float boxTheta(float boxHeight,float rInner, float rOuter)
	{
		return FastMath.acos((rInner + boxHeight)/(rOuter));
	}
	public static float minTheta(float minWidth, float rOuter)
	{
		return FastMath.asin(minWidth/rOuter);
	}
	
	public static float maxRadius(float rOuter, float minWidth, float minHeight)
	{
		float res = FastMath.sqrt(rOuter*rOuter - (minWidth/2f)*(minWidth/2f)) - minHeight;
		if(Float.isNaN(res))
			return 0;
		else
			return res;
	}
		
	
	
}
