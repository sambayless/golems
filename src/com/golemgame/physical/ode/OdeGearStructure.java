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
package com.golemgame.physical.ode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.shape.GearTooth;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GearInterpreter;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.structural.collision.CollisionMember;
import com.golemgame.structural.structures.GearStructure;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.geometry.PhysicsBox;

public class OdeGearStructure extends OdeCylinderStructure{
	private GearInterpreter interpreter;
	
	public OdeGearStructure(PropertyStore store) {
		super(store);
		interpreter = new GearInterpreter(store);
	}
	

	private transient List<GearTooth> collidableTeeth=null;

	@Override
	public void buildCollidable(CollisionMember collidable) {
		super.buildCollidable(collidable);
		
		float height = interpreter.getToothHeight();
		float width = interpreter.getToothWidth();
		float angle = interpreter.getToothAngle();
	//	int numberOfTeeth = interpreter.getNumberOfTeeth();
		float radius = super.getInterpreter().getRadius();
		float deltaH = 0;
		if(!(width == 0 && angle == 0 ))
			deltaH = GearStructure.exactHeight(height,width,angle,radius);

		
		
		float totalHeight = height + deltaH;
	//	float fullWidth = width*2f + (float)Math.tan(angle/2f)*totalHeight*2f;
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

			//	System.out.println(radius + "\t" + baseWidth + "\t" + width + "\t" + numberOfTeeth + "(" +  2.0*Math.PI/radiansPerToothCur + ")");
		}
			
		
		
		if(numberOfTeeth <= 0)
			return;
		collidableTeeth= new ArrayList<GearTooth>();
		
		NodeModel toothModel = new NodeModel();
		collidable.getModel().addChild(toothModel);
		//create or remove teeth as needed
		for (int i = 0; i <numberOfTeeth;i++)
		{
			GearTooth tooth = new GearTooth();
			collidable.registerCollidingModel(tooth);
			toothModel.addChild(tooth);			
			collidableTeeth.add(tooth);
		}
		for(GearTooth tooth:collidableTeeth)
		{
			super.getStructuralAppearanceEffect().attachModel(tooth);
			
		}
	//	this.getStructuralAppearanceEffect().applyEffect(toothModel);
		//this.getAppearance().makeCopy(collidableTeeth.toArray(new GearTooth[0]), true);
		toothModel.getLocalTranslation().set(super.physicsModel.getLocalTranslation());
		toothModel.getLocalRotation().set(super.physicsModel.getLocalRotation());
		toothModel.getLocalScale().setY(super.getInterpreter().getHeight());
		toothModel.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));//this rotation is to account for the extra rotation given to the parent cylinder model, that isnt in the design mode view
		
		toothModel.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
		
		{
			//update all the teeth...
			float radiansPerTooth = FastMath.TWO_PI/((float)numberOfTeeth);
			
			GearTooth firstTooth = collidableTeeth.get(0);
			firstTooth.rebuild(totalHeight, width, angle);
		
	
			
			float radiusL = super.getInterpreter().getRadius()-deltaH*1.3f;
			
			//evenly disperse the teeth around the circle.
			
			for (int i = 0; i < collidableTeeth.size();i++)
			{
				GearTooth tooth = collidableTeeth.get(i);
				tooth.setHasSlopes(angle>0f);
				tooth.setHasWidth(width>0f);
				tooth.copyFrom(firstTooth);
				
				float curAngle = radiansPerTooth*((float)i);
				float x = FastMath.sin(curAngle)*radiusL;
				float y = FastMath.cos(curAngle)*radiusL;
				tooth.getLocalTranslation().set( x,0,y);
				
			
				
				tooth.getLocalRotation().fromAngleNormalAxis(curAngle, Vector3f.UNIT_Y);
				
				//tooth.getLocalTranslation().addLocal(super.physicsModel.getLocalTranslation());

			}
			toothModel.updateWorldData();
		}			
	}


	public static double quadraticFormulaMinus(double a, double b, double c) {
		//positive only, if it exists
		double det = (b*b)- 4.0*a*c;
		if(det<0)
			return Double.NaN;
		
		return (-b - Math.sqrt(det))/(2.0*a);
	}


	@Override
	public float buildCollisionGeometries(PhysicsNode physicsNode,Collection<PhysicsComponent> components,
			Vector3f store) {
		if(collidableTeeth == null || collidableTeeth.isEmpty())
			return super.buildCollisionGeometries(physicsNode,components, store);
		float volume =  super.buildCollisionGeometries(physicsNode,components, store);
	//	float originalVolume = volume;
		float angle = interpreter.getToothAngle();
		float height = super.getInterpreter().getHeight();
		Collection<PhysicsCollisionGeometry> geoms = new ArrayList<PhysicsCollisionGeometry>();
	
		Node toothModel = new Node();
		toothModel.getLocalTranslation().set(super.physicsModel.getLocalTranslation());
		toothModel.getLocalRotation().set(super.physicsModel.getLocalRotation());
		toothModel.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));//this rotation is to account for the extra rotation given to the parent cylinder model, that isnt in the design mode view
		
		toothModel.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
		//toothModel.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));

		float density =  this.getMaterial().getDensity();
		
		toothModel.getLocalScale().setY(super.getInterpreter().getHeight());
		toothModel.updateWorldVectors();
		float toothHeight = interpreter.getToothHeight();
		float toothWidth = interpreter.getToothWidth();
		for (GearTooth tooth:collidableTeeth)
		{
			//build the three corresponding boxes
			/*if (angle <=0 || angle >= FastMath.PI)
			{*/
			
			
			
			Node tempNode = new Node();
			toothModel.attachChild(tempNode);
			tempNode.getLocalTranslation().set(tooth.getLocalTranslation());
			tempNode.getLocalRotation().set(tooth.getLocalRotation());
			tempNode.updateWorldVectors();
			
			if (angle<FastMath.HALF_PI || toothWidth>0f)
			{
				if (toothWidth>0)
				{
					PhysicsBox box=physicsNode.createBox("box");
					box.getLocalTranslation().set(tooth.getTopBox().getLocalTranslation());
	
					box.getLocalScale().set(tooth.getTopBox().getLocalScale());
				
					tempNode.localToWorld( box.getLocalTranslation(),  box.getLocalTranslation() );
				    tempNode.getWorldRotation().mult(box.getLocalRotation(),box.getLocalRotation());
		
					physicsNode.attachChild(box);
					geoms.add(box);
					volume += box.getVolume()*density;
				}
				//}else
				 
				if(angle>0 ){
					
					
	
					
					PhysicsBox leftBox=physicsNode.createBox("leftbox");
					leftBox.getLocalTranslation().set(tooth.getLeftBox().getLocalTranslation());
					leftBox.getLocalRotation().set(tooth.getLeftBox().getLocalRotation());
					leftBox.getLocalScale().set(tooth.getLeftBox().getLocalScale());
					physicsNode.attachChild(leftBox);
					tempNode.localToWorld( leftBox.getLocalTranslation(),  leftBox.getLocalTranslation() );
				    tempNode.getWorldRotation().mult(leftBox.getLocalRotation(),leftBox.getLocalRotation());
		
					geoms.add(leftBox);
					volume += leftBox.getVolume()*density;
					
					PhysicsBox rightBox=physicsNode.createBox("rightbox");
					rightBox.getLocalTranslation().set(tooth.getRightBox().getLocalTranslation());
					rightBox.getLocalRotation().set(tooth.getRightBox().getLocalRotation());
					rightBox.getLocalScale().set(tooth.getRightBox().getLocalScale());
					tempNode.localToWorld( rightBox.getLocalTranslation(),  rightBox.getLocalTranslation() );
				    tempNode.getWorldRotation().mult(rightBox.getLocalRotation(),rightBox.getLocalRotation());
		
					physicsNode.attachChild(rightBox);
					geoms.add(rightBox);
					volume += rightBox.getVolume()*density;

				}	
			}else
			{ //special case that can be represented by just one box per tooth
				
			
				float delta = GearStructure.exactHeight(toothHeight,0f,angle,super.getInterpreter().getRadius());
				
				PhysicsBox box=physicsNode.createBox("box");
				box.getLocalTranslation().set(tooth.getTopBox().getLocalTranslation());
				box.getLocalTranslation().z = 0f;

				float scale = FastMath.sqrt(2f*FastMath.sqr((toothHeight+delta)));
				
				box.getLocalScale().set(scale,1f,scale);
				box.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI/2f, Vector3f.UNIT_Y);
				
				tempNode.localToWorld( box.getLocalTranslation(),  box.getLocalTranslation() );
			    tempNode.getWorldRotation().mult(box.getLocalRotation(),box.getLocalRotation());
	
				physicsNode.attachChild(box);
				geoms.add(box);
				volume += box.getVolume()*density;
			}
		}
		
		for(PhysicsCollisionGeometry geom:geoms)
		{
			geom.getLocalScale().setY(height);
		//	geom.getLocalTranslation().addLocal(physicsModel.getLocalTranslation());
		//	geom.getLocalRotation().multLocal(physicsModel.getLocalRotation());
			//geom.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_X));
		}
		
	/*	if(collidableTeeth.size()==1)
		{//have to offcenter the center of mass
			store.zero();
			//store.divideLocal(originalVolume);
			Vector3f component = new Vector3f();
			System.out.println(store);
			for (PhysicsCollisionGeometry g:geoms)
			{
				g.getWorldScale().set(g.getLocalScale());//temporary
				component.set(g.getLocalTranslation()).multLocal(g.getVolume()*density);
				store.addLocal(component);
			}
			
			//store.divideLocal(geoms.size()+1f);
		}*/
	//	System.out.println(store);
		return volume;
	}


}
