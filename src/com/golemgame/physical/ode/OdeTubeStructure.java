package com.golemgame.physical.ode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.golemgame.model.spatial.shape.TubeFacade;
import com.golemgame.model.spatial.shape.TubeModel;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.CylinderInterpreter;
import com.golemgame.mvc.golems.TubeInterpreter;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.structural.structures.decorators.PhysicalDecorator;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.geometry.PhysicsBox;

public class OdeTubeStructure extends OdePhysicalStructure {
//private static final int MAX_BOXES = 20;
private TubeInterpreter interpreter;
	public static float accuracy = 0.975f;

	public OdeTubeStructure(PropertyStore store) {
		super(store);
		interpreter = new TubeInterpreter(store);
	}

	protected TubeModel physicsModel = null;
	
	public CylinderInterpreter getInterpreter() {
		return interpreter;
	}


	public void buildCollidable(CollisionMember collidable) {
	
		
		
		//construct a shared visual model from the visual box
		 physicsModel = new TubeModel(true);

		 physicsModel.getLocalTranslation().set(interpreter.getLocalTranslation());
		 physicsModel.getLocalRotation().set(interpreter.getLocalRotation());
		 physicsModel.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI,Vector3f.UNIT_Z));

		 physicsModel.getLocalScale().set(interpreter.getRadius()*2f,interpreter.getHeight(),interpreter.getRadius()*2f);
		
		  Quaternion rotation = new Quaternion();
		    rotation.fromAngleNormalAxis(FastMath.PI,Vector3f.UNIT_X);
		    rotation.multLocal(new Quaternion().fromAngleNormalAxis(FastMath.PI/2f,Vector3f.UNIT_Y));
		   // rotation.fromAngleNormalAxis(FastMath.HALF_PI/2f - FastMath.HALF_PI/3f + FastMath.HALF_PI/6f, Vector3f.UNIT_Y);
		    physicsModel.getLocalRotation().multLocal(rotation);
		   
		 
			float innerRadius = interpreter.getInnerRadius();
			if(innerRadius> interpreter.getRadius()*0.98f)
				innerRadius = interpreter.getRadius()*0.98f;
			innerRadius/=(interpreter.getRadius()*2f);

		// physicsModel.setInnerRadius(innerRadius);
		 physicsModel.setParameters(innerRadius,interpreter.getArc());
		
		collidable.registerCollidingModel(physicsModel);
		collidable.getModel().addChild(physicsModel);
		physicsModel.getListeners().clear();
		physicsModel.updateModelData();
		physicsModel.updateWorldData();

		TubeFacade physicsFacade = new TubeFacade();
	//	physicsFacade.setInnerRadius(innerRadius);
		physicsFacade.setParameters(innerRadius,interpreter.getArc());
		physicsFacade.loadModelData(physicsModel);
		
		collidable.getModel().addChild(physicsFacade);
		
		super.getStructuralAppearanceEffect().attachModel(physicsFacade);
	
	}
	
	private Collection<PhysicsBox> physicsElements = new ArrayList<PhysicsBox>();

	

	@Override
	public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
			Vector3f store) {
	

		
		
		float radius = interpreter.getRadius();
		
		float innerRadius = interpreter.getInnerRadius();
		if(innerRadius> interpreter.getRadius())
			innerRadius = interpreter.getRadius();
		//innerRadius/=(interpreter.getRadius()*2f);
		
		float maxInnerRadius = maxRadius(radius,0.05f,0.05f);
		if(innerRadius>maxInnerRadius)
			innerRadius = maxInnerRadius;
		
		float length = interpreter.getHeight();

		float boxHeight = accuracy*(radius - innerRadius);
		float boxTheta = boxTheta(boxHeight,innerRadius,radius);
		float boxWidth = boxWidth(boxTheta,radius);
		float arcToCover = FastMath.PI*interpreter.getArc()/FastMath.TWO_PI; //this is HALF the actual arc of the tube
		
		float numBoxesTotal = (arcToCover/boxTheta);
	
		int numNormalBoxes = (int) numBoxesTotal;
		
		if(numNormalBoxes < 2)
		{
			boxTheta = arcToCover/2f;
			boxWidth  = boxWidth(boxTheta,radius);
			numBoxesTotal = 2;			
			numNormalBoxes = 2;
		}
		
		boolean capEnds = (arcToCover<FastMath.PI);
	//	capEnds =false;
	//	float boxRemainder = ((numBoxesTotal-(float)numNormalBoxes));
	//	float boxInnerTheta = FastMath.asin((boxWidth/2f)/innerRadius);
		
		float remainingBoxTheta = arcToCover- (numNormalBoxes*boxTheta);
		
		//NOTE: The begining and end caps are not the same as each other! The begining one ONLY has to cover the part of the outer rim missed by the first element,
		//the LAST one has to cover max of that outer rim missed, and the uncovered arc section. They are different!
		
		
		//The first and last boxes need to be thinner, and offset angularly (offset so that the only gab is on their outer edges where the cap is, thinner so that they dont exceed the inner radius).
	//	float lastBoxDif = ((numNormalBoxes*boxTheta) - boxTheta/2f)  ;//need to reduce the width of the last box, so that its inner section is not too big.
		
		
		float remainingBoxWidth=  boxWidth(remainingBoxTheta, radius);

		
		Quaternion rotation = new Quaternion( interpreter.getLocalRotation());
	//	rotation.multLocal(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI/2f - FastMath.HALF_PI/3f + FastMath.HALF_PI/6f, Vector3f.UNIT_X));
		Vector3f translation =  interpreter.getLocalTranslation();
		
		//if we are capping the ends, then split the box width in two,
		//and position each half box offcentered, so that it's outer edge lies along the radial line to the center of the tube.
		
		//numNormalBoxes = 1;
		//first, build the normal boxes
		
		float boxCenterRadius = innerRadius+(boxHeight/2f);
		Quaternion rotationCorrection = new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI,Vector3f.UNIT_Z);
		rotation.multLocal(rotationCorrection);
		//Random random = new Random();
		
		
		float leftM = 0f; float leftB = 0f;
		float rightM = FastMath.tan(arcToCover*2f); float rightB = 0f;
		
		
		if(numNormalBoxes>0)
		{//initial and last sections are offset, done separately.
			int i = 0;
			
			float wExtra = FastMath.tan(boxTheta) * boxHeight;
			if(!capEnds)
				wExtra = 0;
			
			PhysicsBox  box = physicsNode.createBox("TubeSection" + i);
			box.getLocalScale().set(boxWidth - wExtra,length ,boxHeight);//*(1f- random.nextFloat()/100f)
			
			//the translation of the box is...
			
			float theta = (boxTheta*2f)*i+boxTheta;
			
			float x = FastMath.sin(theta)*boxCenterRadius;
			float y = FastMath.cos(theta)*boxCenterRadius;
			
			box.getLocalTranslation().set(x,0,y);

			box.getLocalRotation().fromAngleNormalAxis(theta, Vector3f.UNIT_Y);
			Vector3f offset = new Vector3f(wExtra/2f,0,0);
			box.getLocalRotation().multLocal(offset);
			box.getLocalTranslation().addLocal(offset);
		
			if(capEnds)
			{
				float bLeft = (boxWidth)/2f;//the wExtra cancels out exactly, so it is just boxWidth (because although we reduce the width below boxwidth, we then move the box left to compensate)
				float thetaPrime = FastMath.atan(bLeft/innerRadius) + theta;
				
				if( arcToCover*2f-thetaPrime < 0)			
				{
					//solve this by making it shorter, not thinner	
			
					float newBottom = bLeft/ FastMath.tan(arcToCover*2f - theta);
					float verticalSquish = newBottom - innerRadius;
					box.getLocalScale().z = box.getLocalScale().z - verticalSquish;
					offset = new Vector3f(0,0,verticalSquish/2f);
					box.getLocalRotation().multLocal(offset);
					box.getLocalTranslation().addLocal(offset);
					//.out.println(i);
				}
			}
			
			//transform into world coords			
			rotation.multLocal(box.getLocalTranslation());
			box.getLocalRotation().set(rotation.mult(box.getLocalRotation()));
		
			box.getLocalTranslation().addLocal(translation);
			
			box.setMaterial(getMaterial());
			physicsElements.add(box);
			
			

			OdePhysicsComponent jointComp = new OdePhysicsComponent(box,physicsModel);
			jointComp.setMass( box.getVolume()*getMaterial().getDensity());
			components.add(jointComp);
			
			
		}
		if(numNormalBoxes>1)
		{//last section is offset, done separately.
			int i = numNormalBoxes-1;
			
			float wExtra = FastMath.tan(boxTheta) * boxHeight;
			if(!capEnds)
				wExtra = 0;
			
			PhysicsBox  box = physicsNode.createBox("TubeSection" + i);
			box.getLocalScale().set(boxWidth - wExtra,length ,boxHeight);//*(1f- random.nextFloat()/100f)
			
			//the translation of the box is...
			
			float theta = (boxTheta*2f)*i+boxTheta;
			
			float x = FastMath.sin(theta)*boxCenterRadius;
			float y = FastMath.cos(theta)*boxCenterRadius;
			
			box.getLocalTranslation().set(x,0,y);
			
			
			//set the rotation
			
			box.getLocalRotation().fromAngleNormalAxis(theta, Vector3f.UNIT_Y);
			Vector3f offset = new Vector3f(-wExtra/2f,0,0);
			box.getLocalRotation().multLocal(offset);
			box.getLocalTranslation().addLocal(offset);
			if(capEnds)
			{
				float bLeft = (boxWidth)/2f;//the wExtra cancels out exactly, so it is just boxWidth (because although we reduce the width below boxwidth, we then move the box left to compensate)
				float thetaPrime = FastMath.atan(bLeft/innerRadius);
			//	float bLeftRad = FastMath.sqrt(bLeft*bLeft + innerRadius*innerRadius);
				
				//float left = FastMath.cos(theta - thetaPrime) * bLeftRad ;
				
				//float bottom =FastMath.sin(theta - thetaPrime) * bLeftRad;
				if( theta-thetaPrime < 0)
				//if ((leftM * left + leftB) > bottom)
				{
				//solve this by making it shorter, not thinner	
				//	float maxTheta = thetaPrime - theta;
					
					float newBottom = bLeft/ FastMath.tan(theta);
					float verticalSquish = newBottom - innerRadius;
					box.getLocalScale().z = box.getLocalScale().z - verticalSquish;
					offset = new Vector3f(0,0,verticalSquish/2f);
					box.getLocalRotation().multLocal(offset);
					box.getLocalTranslation().addLocal(offset);
					//.out.println(i);
				}
			}
			//transform into world coords
			
			rotation.multLocal(box.getLocalTranslation());
			box.getLocalRotation().set(rotation.mult(box.getLocalRotation()));
		
			box.getLocalTranslation().addLocal(translation);
			
			box.setMaterial(getMaterial());
			physicsElements.add(box);
			
			

			OdePhysicsComponent jointComp = new OdePhysicsComponent(box,physicsModel);
			jointComp.setMass( box.getVolume()*getMaterial().getDensity());
			components.add(jointComp);
			
	
			
			
		}

		
		//for these inner sections, also have to calculate wExtra, but instead of making them thinner, make them shorter (keeping them on the outer rim)
		for (float i = 1; i < numNormalBoxes - 1;i++)
		{
			PhysicsBox  box = physicsNode.createBox("TubeSection" + i);
			box.getLocalScale().set(boxWidth,length ,boxHeight);//*(1f- random.nextFloat()/100f)
			
			//the translation of the box is...
			
			float theta = (boxTheta*2f)*i+boxTheta;
			
			float x = FastMath.sin(theta)*boxCenterRadius;
			float y = FastMath.cos(theta)*boxCenterRadius;
			
			box.getLocalTranslation().set(x,0,y);
			
			//set the rotation
			
			box.getLocalRotation().fromAngleNormalAxis(theta, Vector3f.UNIT_Y);
			
			float bLeft = (boxWidth)/2f;//the wExtra cancels out exactly, so it is just boxWidth (because although we reduce the width below boxwidth, we then move the box left to compensate)
			float thetaPrime = FastMath.atan(bLeft/innerRadius);
			
			if(capEnds)
			{
				if( arcToCover*2f-(thetaPrime + theta) < 0)			
				{//this checks to see if this segment is going outside of the patial pipe.
					//solve this by making it shorter, not thinner			
					float newBottom = bLeft/ FastMath.tan(arcToCover*2f - theta);
					float verticalSquish = newBottom - innerRadius;
					box.getLocalScale().z = box.getLocalScale().z - verticalSquish;
					Vector3f offset = new Vector3f(0,0,verticalSquish/2f);
					box.getLocalRotation().multLocal(offset);
					box.getLocalTranslation().addLocal(offset);
	
					 thetaPrime = FastMath.atan(bLeft/newBottom);//update theta for the next test
				}
				
				if( theta-thetaPrime < 0)
				{
						float newBottom = bLeft/ FastMath.tan(theta);
						float verticalSquish = newBottom - innerRadius;
						box.getLocalScale().z = box.getLocalScale().z - verticalSquish;
						Vector3f offset = new Vector3f(0,0,verticalSquish/2f);
						box.getLocalRotation().multLocal(offset);
						box.getLocalTranslation().addLocal(offset);
				}
			}
			//transform into world coords
			
			rotation.multLocal(box.getLocalTranslation());
			box.getLocalRotation().set(rotation.mult(box.getLocalRotation()));
		
			box.getLocalTranslation().addLocal(translation);
			
			box.setMaterial(getMaterial());
			physicsElements.add(box);
			
			

			OdePhysicsComponent jointComp = new OdePhysicsComponent(box,physicsModel);
			jointComp.setMass( box.getVolume()*getMaterial().getDensity());
			components.add(jointComp);
		}
			
		//now, add on the one or two boxes that cap this off...
		float wExtra = FastMath.tan(boxTheta) * boxHeight;
	
		{
			if(capEnds)
			{

				float innerTheta = FastMath.atan((boxWidth/2f)/radius);
				if(wExtra > 0.01f)
				{
					//create two boxes. one at theta = 0, the other at theta = arc 
					
					//the rotation of the box is 0
				
					//the caps are distorted so that they are not as high as the rest of the boxes, as a result of the angle difference.
					
				
					
				//	float remainingWidth =remainingBoxWidth;// remainingBoxTheta * innerRadius;  // FastMath.sin(innerTheta - boxTheta)*radius;
			/*	//	float remainingTheta = innerTheta - boxTheta;//FastMath.asin((remainingWidth/2f)/radius);
					float remainingBoxHeight = boxHeight( innerTheta-boxTheta,radius,innerRadius);
					box1.getLocalScale().set(remainingWidth, length,remainingBoxHeight);
							
					float remainingCenterPos = radius - (remainingBoxHeight/2f);*/
					
					float boxRad = FastMath.sqrt( (innerRadius + boxHeight)*(innerRadius + boxHeight) + (boxWidth/2f - wExtra)*(boxWidth/2f - wExtra) );
				
					
					float innerHeight1 =  FastMath.sqrt(boxRad*boxRad - wExtra*wExtra) - innerRadius;//() - (innerRadius/FastMath.cos(innerTheta));
				
					
					if(innerHeight1>FastMath.FLT_EPSILON)
					{
						PhysicsBox  box1 = physicsNode.createBox("TubeSection Cap 1");
						float remainingBoxHeight =innerHeight1;// boxHeight( innerTheta-boxTheta,radius,innerRadius);
						box1.getLocalScale().set(wExtra, length,innerHeight1);
								
						//float remainingCenterPos = (innerRadius/FastMath.cos(innerTheta))+(remainingBoxHeight/2f);
						
						/*float theta = boxTheta - innerTheta;
						
						float x = FastMath.sin(theta)*remainingCenterPos;
						float y = FastMath.cos(theta)*remainingCenterPos;
						box1.getLocalTranslation().set(x,0,y);*/
				
						box1.getLocalTranslation().set(wExtra/2f,0,innerHeight1/2f + innerRadius);
					//	box1.getLocalTranslation().z += wExtra/2f;
						
						//box1.getLocalRotation().fromAngleNormalAxis(theta, Vector3f.UNIT_Y);
						
					//	Vector3f translate = new Vector3f(0,0,wExtra/2f);
					//	box1.getLocalRotation().multLocal(translate);
					//	box1.getLocalTranslation().addLocal(translate);
						
						
						float bLeft = (wExtra);//the wExtra cancels out exactly, so it is just boxWidth (because although we reduce the width below boxwidth, we then move the box left to compensate)
						float thetaPrime = FastMath.atan(bLeft/innerRadius) ;
						
						if( arcToCover*2f-thetaPrime < 0)			
						{
							//solve this by making it shorter, not thinner	
					
							float newBottom = bLeft/ FastMath.tan(arcToCover*2f );
							float verticalSquish = newBottom - innerRadius;
							box1.getLocalScale().z = box1.getLocalScale().z - verticalSquish;
							Vector3f offset = new Vector3f(0,0,verticalSquish/2f);
							box1.getLocalRotation().multLocal(offset);
							box1.getLocalTranslation().addLocal(offset);
							//.out.println(i);
						}
						
						
						//transform into world coords
						box1.getLocalRotation().set(rotation.mult(box1.getLocalRotation()));
				
						rotation.multLocal(box1.getLocalTranslation());
						box1.getLocalTranslation().addLocal(translation);
						
						box1.setMaterial(getMaterial());
						physicsElements.add(box1);
						
						OdePhysicsComponent jointComp = new OdePhysicsComponent(box1,physicsModel);
						jointComp.setMass( box1.getVolume()*getMaterial().getDensity());
						components.add(jointComp);
					}
				}
				
				float wExtra2 = FastMath.tan(boxTheta) * boxHeight;
				if(wExtra2 + remainingBoxWidth > 0.01f )
				{
					
					float boxRad = FastMath.sqrt( (innerRadius + boxHeight)*(innerRadius + boxHeight) + (boxWidth/2f - wExtra2)*(boxWidth/2f - wExtra2) );
					float innerHeight2 =  FastMath.sqrt(boxRad*boxRad- (wExtra2+remainingBoxWidth)*(wExtra2+remainingBoxWidth)) - innerRadius;//() - (innerRadius/FastMath.cos(innerTheta));
					
					PhysicsBox  box2 = physicsNode.createBox("TubeSection Cap 2");
					box2.getLocalScale().set(wExtra2 + remainingBoxWidth, length,innerHeight2);
							
					//box2.getLocalTranslation().set(remainingBoxWidth/2f,0,boxCenterRadius);
					
					//float angleOfLastBox = numNormalBoxes*boxTheta*2f;
					
					//float remainingCenterPos = (innerRadius/FastMath.cos(innerTheta))+(innerHeight2/2f);
					float theta =arcToCover*2f;// angleOfLastBox + (innerTheta-boxTheta);
					
				/*	float x = FastMath.sin(theta)*remainingCenterPos;
					float y = FastMath.cos(theta)*remainingCenterPos;*/
					
					//box2.getLocalTranslation().set(x,0,y);
					
					//set the rotation				
					box2.getLocalRotation().fromAngleNormalAxis(theta, Vector3f.UNIT_Y);
	
					Vector3f translate = new Vector3f(-(wExtra2+remainingBoxWidth)/2f,0,innerHeight2/2f + innerRadius);
					box2.getLocalRotation().multLocal(translate);
					box2.getLocalTranslation().addLocal(translate);
					
					
					float bLeft = (wExtra2 + remainingBoxWidth);//the wExtra cancels out exactly, so it is just boxWidth (because although we reduce the width below boxwidth, we then move the box left to compensate)
					float thetaPrime = FastMath.atan(bLeft/innerRadius);
				
					if( theta-thetaPrime < 0)
					{
					//solve this by making it shorter, not thinner	
						float newBottom = bLeft/ FastMath.tan(theta);
						float verticalSquish = newBottom - innerRadius;
						box2.getLocalScale().z = box2.getLocalScale().z - verticalSquish;
						Vector3f offset = new Vector3f(0,0,verticalSquish/2f);
						box2.getLocalRotation().multLocal(offset);
						box2.getLocalTranslation().addLocal(offset);
					}
					
					
					//transform into world coords
					box2.getLocalRotation().set(rotation.mult(box2.getLocalRotation()));
			
					rotation.multLocal(box2.getLocalTranslation());
					box2.getLocalTranslation().addLocal(translation);
					
					box2.setMaterial(getMaterial());
					physicsElements.add(box2);
					OdePhysicsComponent jointComp2 = new OdePhysicsComponent(box2,physicsModel);
					jointComp2.setMass( box2.getVolume()*getMaterial().getDensity());
					components.add(jointComp2);
				}
			}else
			{
				//in a full tube, this fills in the gab between boxes
				//the rotation of the box is 0
				PhysicsBox  box = physicsNode.createBox("TubeSection Filler");
			
				
				
				float angleOfLastBox = numNormalBoxes*boxTheta*2f;
				
				float posX = FastMath.cos(angleOfLastBox)*radius;
				float posY = FastMath.sin(angleOfLastBox)*radius;
				
				float difX = radius-posX;
				float width = FastMath.sqrt(difX*difX + posY*posY);
				
				float theta = (FastMath.TWO_PI- angleOfLastBox)/2f;
				
				float height = boxHeight(theta,radius,innerRadius);
				
				box.getLocalScale().set(width, length,height);
				
				
				float thetaPos = -(FastMath.TWO_PI-angleOfLastBox)/2f;
				
				float centerRadius = innerRadius+height/2f;
				
				float x = FastMath.sin(thetaPos)*centerRadius;
				float y = FastMath.cos(thetaPos)*centerRadius;
				
				box.getLocalTranslation().set(x,0,y);
				
				//set the rotation
				
				box.getLocalRotation().fromAngleNormalAxis(thetaPos, Vector3f.UNIT_Y);
				
				//transform into world coords
				
				rotation.multLocal(box.getLocalTranslation());
				box.getLocalRotation().set(rotation.mult(box.getLocalRotation()));
			
				box.getLocalTranslation().addLocal(translation);
				
				box.setMaterial(getMaterial());
				physicsElements.add(box);
				OdePhysicsComponent jointComp2 = new OdePhysicsComponent(box,physicsModel);
				jointComp2.setMass( box.getVolume()*getMaterial().getDensity());
				components.add(jointComp2);
			}
		}
		
		float mass = (FastMath.PI*(radius*radius - innerRadius*innerRadius)/2f) * interpreter.getHeight() *this.getMaterial().getDensity();
		float[] cmPos = getCenterOfMass(innerRadius, radius, arcToCover*2f);
		Vector3f cm = new Vector3f(cmPos[1],0,cmPos[0]);
		rotation.multLocal(cm);
		cm.addLocal(translation);
		//interpreter.getLocalRotation().multLocal(cm);
		
		store.set(cm);
		return mass;
	//	store.set(interpreter.getLocalTranslation());//VERY WRONG for ARCS
	//	return volumeTube(length,radius,innerRadius);
	}

	public static float volumeCylinder(float height, float radius)
	{
		return FastMath.PI*radius*radius*height;
	}
	
	public static float volumeTube(float height, float outerRadius, float innerRadius)
	{
		return FastMath.PI*height*(outerRadius*outerRadius - innerRadius*innerRadius);	
	}
	
	public void buildRelationships( Map<OdePhysicalStructure, PhysicsNode> physicsMap, OdePhysicsEnvironment compiledEnvironment) 
	{
	
		
		PhysicsCollisionGeometry[] involved = physicsElements.toArray(new PhysicsCollisionGeometry[0]);
     
		                                                         
		
		for(PhysicalDecorator decorator:super.getPhysicalDecorators())
			decorator.attach(physicsMap.get(this), involved);

	}
	
/*	public static float boxHeight(float rInner, float rOuter, float theta)
	{
		
	}*/
	
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
	
	@Override
	public float getLinearResonanceScalar()
	{
		return interpreter.getHeight() + 4f *(interpreter.getRadius() - interpreter.getInnerRadius());//this is a VERY rough approximation
	}
		
	
	public static float[] getCenterOfMass(float rI, float rO, float theta)
	{
		float area = -(1f/2f) * (rI*rI - rO*rO) * theta;
		
		if(area == 0)
			return new float[]{0,0};
		
		float xPos = -(1f/3f)* (FastMath.pow(rI,3) - FastMath.pow(rO,3)) * FastMath.sin(theta);
		
		float yPos = (1f/3f) * (FastMath.pow(rI,3) - FastMath.pow(rO,3)) * (-1f + FastMath.cos(theta));
		
		return new float[]{xPos/area, yPos/area};
	}
	
}
