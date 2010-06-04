package com.golemgame.structural.structures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.golemgame.model.Model;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.shape.GearTooth;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GearInterpreter;
import com.golemgame.properties.Property;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;

public class GearStructure extends CylinderStructure {


	private static final long serialVersionUID = 1L;

	private static final int MAX_IT = 128;

	private GearInterpreter interpreter ;
	
	private NodeModel toothModel;
	
	private List<GearTooth> teeth = new ArrayList<GearTooth>();
	
/*	private float height;
	private float width;
	private float angle;
	private float curRadius = -1;*/
	private Model[] appearanceModels;
	@Override
	public Model[] getAppearanceModels() {
		if(appearanceModels == null)
		{
			toothModel = new NodeModel();//this is AWFUL, redesign this!
			appearanceModels = new Model[super.getAppearanceModels().length+1];
			for(int i = 0; i < super.getAppearanceModels().length;i++)
				appearanceModels[i]=super.getAppearanceModels()[i];
			appearanceModels[appearanceModels.length-1] = toothModel;
		}
		return appearanceModels;
	}
	
	

	public GearStructure(PropertyStore store) {
		super(store);
		interpreter = new GearInterpreter(store);
		store.setSustainedView(this);
	
		toothModel.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Z);
		super.getModel().addChild(toothModel);
		
	
	
		
	}

	@Override
	public Collection<Property> getPropertySet() {
		 Collection<Property> properties = super.getPropertySet();
		 properties.add(new Property(Property.PropertyType.GEAR,this.interpreter.getStore()));		 
		 return properties;
	}

	@Override
	public void refresh() {
		super.refresh();
		
		//this is a relatively expensive refresh, so were going to cache as much data about the current state as we can, to
		//avoid unneeded updates.

		this.toothModel.getLocalScale().y = super.getInterpreter().getHeight();
		toothModel.updateWorldData();
		//boolean needsChange = false;
		
	/*	needsChange = (height != interpreter.getToothHeight()) ||  (width != interpreter.getToothWidth()) 
		||  (angle != interpreter.getToothAngle())// ||  (teeth.size() != interpreter.getNumberOfTeeth())
		|| (curRadius != super.getInterpreter().getRadius());
		
		if(needsChange)*/
		{
			float height = interpreter.getToothHeight();
			float width = interpreter.getToothWidth();
			float angle = interpreter.getToothAngle();
			
			float radius = super.getInterpreter().getRadius();
			
			float deltaH = 0;
			if(!(width == 0 && angle == 0 ))
				deltaH = exactHeight(height,width,angle,radius);
	
			float toothHeight = height+ deltaH;
			
			double numberOfTeeth = 0;
			//double fullWidth =  width*2.0 + (float)Math.tan(angle/2.0)*height*2.0;
			{	
				double R = (float) Math.sqrt( (radius + height)*(radius + height) + width*width/4.0) ;
				
				//equation for the line of the gear edge
				double m = Math.tan(angle/2.0);
				double b = m*R;//y intersect is slope times BIG R.
				
				double xIntersect = quadraticFormulaMinus(m*m+1.0,2.0*m*b,b*b-radius*radius);
				if(Double.isNaN(xIntersect))
					xIntersect = 0.0;				
				double radiansPerToothAngle = Math.abs(Math.acos(Math.abs(xIntersect)/radius));
				double radiansPerToothWidth = 2.0*Math.asin(width/(2.0*radius));

				double radiansPerToothCur = radiansPerToothAngle*2.0 + radiansPerToothWidth*2.0;
				 numberOfTeeth =Math.floor( FastMath.TWO_PI/radiansPerToothCur);
				 
				 
					double t =  FastMath.TWO_PI/numberOfTeeth - radiansPerToothAngle*2.0 - radiansPerToothWidth;

					double baseWidth = 2.0*(radius)*Math.sin(t/2f);
				//	System.out.println(radius + "\t" + baseWidth + "\t" + width + "\t" + numberOfTeeth + "(" +  2.0*Math.PI/radiansPerToothCur + ")");
			}
			
			
			//create or remove teeth as needed
			while (numberOfTeeth>teeth.size())
			{
				GearTooth tooth = new GearTooth();
				toothModel.addChild(tooth);
				tooth.setActionable(this);
				teeth.add(tooth);
			}
			while(numberOfTeeth< teeth.size() &! teeth.isEmpty())
			{				
				GearTooth tooth = teeth.get(teeth.size()-1);
				teeth.remove(teeth.size()-1);
				tooth.delete();
			}
			
			if (!teeth.isEmpty())
			{
				//update all the teeth...
				float radiansPerTooth = FastMath.TWO_PI/((float)numberOfTeeth);
				
				GearTooth firstTooth = teeth.get(0);
				firstTooth.rebuild(toothHeight, width, angle);
			
				
				float radiusL = super.getInterpreter().getRadius()-deltaH*1.3f;
				
				//evenly disperse the teeth around the circle.
				
				for (int i = 0; i < teeth.size();i++)
				{
					GearTooth tooth = teeth.get(i);
					tooth.setHasSlopes(angle>0f);
					tooth.setHasWidth(width>0f);
					tooth.copyFrom(firstTooth);
					
					float curAngle = radiansPerTooth*i;
					float x = FastMath.sin(curAngle)*radiusL;//sin and cos mixed up? seems to work...
					float y = FastMath.cos(curAngle)*radiusL;
					tooth.getLocalTranslation().set( x,0,y);
					
					tooth.getLocalRotation().fromAngleNormalAxis(curAngle, Vector3f.UNIT_Y);
					
					tooth.updateWorldData();
				}
			}			
		}
	}
	
	/**
	 * The 'height' of the gear tooth in the property store is the height from the edge of the cylinder to the top of the center of the gear tooth.
	 * The height of the gear tooth at the edges is slightly more; that difference is calculated by this function
	 */
	public static float exactHeight(float height, float width, float angle,
			float radius) {
		float tanA = FastMath.tan(angle/2f);
		float tanA2 = tanA*tanA;
		float w2 = width/2f;
		
		float a = (1f- tanA2);
		float b = 2f*(radius-height*tanA2 - (w2)*tanA);
		float c = -(w2*w2 + 2f*w2*height*tanA);
		
		float ans = (float)  quadraticFormulaPlus(a,b,c);
		if (!Float.isNaN(ans))
			return ans;
		return approximateHeight(height,width,angle,radius);
		//return 0;
	}
	
	public static double quadraticFormulaPlus(double a, double b, double c) {
		//positive only, if it exists
		double det = (b*b)- 4.0*a*c;
		if(det<0)
			return Double.NaN;
		
		return (-b + Math.sqrt(det))/(2.0*a);
	}

	public static double quadraticFormulaMinus(double a, double b, double c) {
		//positive only, if it exists
		double det = (b*b)- 4.0*a*c;
		if(det<0)
			return Double.NaN;
		
		return (-b - Math.sqrt(det))/(2.0*a);
	}


	public static float approximateHeight(float height, float width, float angle,
			float radius) {
		
		
		
		
		//goal: find the larger height that the tooth should be, so that it overlaps the radius propperly.
		
		//the intersection point is (radius + height - h, w/2 + tan(theta)*h).
		//so, find the smallest value of h that we can use, greater than the starting height and less than h+radius
		
		//delta h is the amount we are increasing height by; ie, its h - height
		
		//second constraint: the intersection point is in the circle..
		float halfw = width/2f;
		float tAng = FastMath.tan(angle/2f);
		float sX = radius;
		float sY =  halfw+ tAng*height;
		
		if(inCircle(radius + FastMath.FLT_EPSILON,sX,sY))
			return 0;//don't bother adding anything
		
		//radius = halfw + tAng*(deltaH + height)
		
		
		float largestHeight = (radius- halfw)/ tAng - height;
		float deltaH = Math.min(radius, largestHeight);
		
		//initialize the delta h... put it inside hte circle
		int i;
		for ( i = 0; i < MAX_IT;i++)
		{
			float x = radius -deltaH;
			float y = halfw + tAng*(height + deltaH);
			
			if(inCircle(radius, x,y))
				break;//if this breaks on the very first time.. then the point is NOT guaranteed to be in the circle.
			deltaH/=2f;
		}
		if (i>=MAX_IT)
			return 0;//couldnt find it
		
		for ( i = 0; i < MAX_IT;i++)
		{
			float x = radius -deltaH;
			float y = halfw + tAng*(height + deltaH);
			
			if(!inCircle(radius, x,y))
				break;//if this breaks on the very first time.. then the point is NOT guaranteed to be in the circle.
			deltaH/=2f;
		}
		if (i>=MAX_IT)
			return 0;//couldnt find it
		
		return deltaH;
		
	}
	
	private static boolean inCircle(float radius, float x, float y)
	{
		return x*x+y*y <= radius*radius;
		
	}

	

}
