package com.golemgame.structural.structures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.golemgame.model.Model;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.shape.GearTooth;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GearInterpreter;
import com.golemgame.mvc.golems.RackGearInterpreter;
import com.golemgame.properties.Property;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;

public class RackGearStructure extends BoxStructure {


	private static final long serialVersionUID = 1L;


	private static final int MAX_IT = 128;


	private RackGearInterpreter interpreter ;
	
	private NodeModel toothModel;
	
	private List<GearTooth> teeth = new ArrayList<GearTooth>();
	
	private float height;
	private float width;
//	private float angle;
	private float curWidth = -1;
	private float curLength = -1;
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
	
	

	public RackGearStructure(PropertyStore store) {
		super(store);
		interpreter = new RackGearInterpreter(store);
		store.setSustainedView(this);
	
		//toothModel.getLocalTranslation().y = 0.5f;
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

		float angle = interpreter.getToothAngle();
		
	//	this.toothModel.getLocalTranslation().y = super.getInterpreter().getExtent().y;
		this.toothModel.getLocalScale().y = super.getInterpreter().getExtent().x*2f;
	
		toothModel.updateWorldData();
		boolean needsChange = false;
		
		needsChange = (height != interpreter.getToothHeight()) ||  (width != interpreter.getToothWidth()) 
		||  (angle != interpreter.getToothAngle())
		||  (teeth.size() != interpreter.getNumberOfTeeth())
		|| (curLength != super.getInterpreter().getExtent().x)
		|| (curWidth != super.getInterpreter().getExtent().z);
		
		if(needsChange)
		{
			height = interpreter.getToothHeight();
			width = interpreter.getToothWidth();
			//angle = interpreter.getToothAngle();
			curLength = super.getInterpreter().getExtent().x;
			curWidth = super.getInterpreter().getExtent().z;
			
			
			float deltaH = 0;
			if(!(width == 0 && angle == 0 ))
				deltaH = exactHeight(height,width,angle, super.getInterpreter().getExtent().y);
	
			height += deltaH;
			float fullWidth = width*2f  + (float)Math.tan(angle/2f)*height*2f;
			
			
		//	approximateHeight(height,width,angle,);		
			int numberOfTeeth;
			if(width == 0 || height == 0 )
			{
				numberOfTeeth = 0;	//remove all teeth
			
			}else
			{
				numberOfTeeth = (int) Math.floor((curWidth*2f + width)/ (fullWidth));
				//numberOfTeeth =(int)Math.floor( 2f*FastMath.PI*super.getInterpreter().getRadius()/fullWidth);//interpreter.getNumberOfTeeth();
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
				//float radiansPerTooth = FastMath.TWO_PI/((float)numberOfTeeth);
				
				GearTooth firstTooth = teeth.get(0);
				firstTooth.rebuild(height, width, angle);
			
				//disperse the teeth along the length, starting from the left.
				float spacing = fullWidth;// numberOfTeeth>1? curWidth*2f/((float)(numberOfTeeth-1)):0f;
				float x = 0;
				for(int i = 0;i<numberOfTeeth;i++)
				{
					GearTooth tooth = teeth.get(i);
					tooth.setHasSlopes(angle>0f);
					tooth.setHasWidth(width>0f);
					tooth.copyFrom(firstTooth);

					tooth.getLocalTranslation().set( super.getInterpreter().getExtent().getY(), 0,x - curWidth + (fullWidth-width)/2f);
					x+= spacing;
					tooth.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Y);
					
					tooth.updateWorldData();
				}
				
			/*	float radius = super.getInterpreter().getRadius()-deltaH*1.3f;
				
				//evenly disperse the teeth around the circle.
				
				for (int i = 0; i < teeth.size();i++)
				{
					GearTooth tooth = teeth.get(i);
					tooth.setHasSlopes(angle>0f);
					tooth.setHasWidth(width>0f);
					tooth.copyFrom(firstTooth);
					
					float curAngle = radiansPerTooth*i;
					float x = FastMath.sin(curAngle)*radius;//sin and cos mixed up? seems to work...
					float y = FastMath.cos(curAngle)*radius;
					tooth.getLocalTranslation().set( x,0,y);
					
					tooth.getLocalRotation().fromAngleNormalAxis(curAngle, Vector3f.UNIT_Y);
					
					tooth.updateWorldData();
				}*/
			}			
		}
	}
	
	public static float exactHeight(float height, float width, float angle,
			float radius) {
		float tanA = FastMath.tan(angle/2f);
		float tanA2 = tanA*tanA;
		float w2 = width/2f;
		
		float a = (1f- tanA2);
		float b = 2f*(radius-height*tanA2 - (w2)*tanA);
		float c = -(w2*w2 + 2f*w2*height*tanA);
		
		float ans =  quadraticFormula(a,b,c);
		if (!Float.isNaN(ans))
			return ans;
		return approximateHeight(height,width,angle,radius);
		//return 0;
	}
	
	public static float quadraticFormula(float a, float b, float c) {
		//positive only, if it exists
		float det = (b*b)- 4f*a*c;
		if(det<0)
			return Float.NaN;
		
		return (-b + FastMath.sqrt(det))/(2f*a);
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