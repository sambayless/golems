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
package com.golemgame.tool.control;

import java.util.concurrent.Callable;

import com.golemgame.mvc.golems.CylinderInterpreter;
import com.golemgame.tool.ActionToolSettings;
import com.golemgame.tool.CameraTool;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.action.information.MoveRestrictedInformation;
import com.golemgame.tool.action.information.Orientation;
import com.golemgame.tool.action.information.OrientationInformation;
import com.jme.bounding.BoundingSphere;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.SceneElement;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;

public class CylinderControlPoint extends ControlPoint<CylinderControlPoint.ControllableCylinder>{
	private Spatial controlSphere;
	private Geometry facadeSphere;
	//boolean axis;
	//private Vector3f direction = new Vector3f(1,0,0);//the axis along which this node can move
	
	public static enum CylinderPoint
	{
	
		LEFT(new Vector3f(0,0,1),true), RIGHT(new Vector3f(0,0,1)), RADIUS(new Vector3f(1,0,0));
		public Vector3f direction;
		public Vector3f inverse;
		public int flip;
		
		private CylinderPoint(Vector3f direction)
		{
			this(direction,false);
		}
		
		private CylinderPoint(Vector3f direction, boolean flip)
		{
			this.flip=flip?-1:1;
			this.direction = direction;
			this.inverse = new Vector3f(1,1,1).subtractLocal(Math.abs(direction.x), Math.abs(direction.y), Math.abs(direction.z));
			
		}
	}
	
	public static CylinderPoint LEFT = CylinderPoint.LEFT;
	public static CylinderPoint RIGHT = CylinderPoint.RIGHT;
	public static CylinderPoint RADIUS = CylinderPoint.RADIUS;
	
	private static Callable updateCallback = null;
	private final CylinderPoint point;
	private OrientationInformation orientation;
	public CylinderControlPoint(CylinderPoint point) 
	{
		super();
		this.point = point;
		controlSphere = new SharedMesh("ControlSphereCollision",baseCollisionSphere);
		controlSphere.setCullMode(SceneElement.CULL_ALWAYS);
		facadeSphere = new SharedMesh("ControlSphereFacade",baseSphere);
		facadeSphere.setIsCollidable(false);
		
		this.visualNode.getNode().attachChild(controlSphere);
		this.visualNode.getNode().attachChild(facadeSphere);
		//tie these together
		facadeSphere.setLocalRotation(controlSphere.getLocalRotation());
		facadeSphere.setLocalScale(controlSphere.getLocalScale());
		facadeSphere.setLocalTranslation(controlSphere.getLocalTranslation());
		this.visualNode.getSpatial().updateGeometricState(0, true);
		this.visualNode.getSpatial().updateRenderState();		
		this.visualNode.getSpatial().setModelBound(new BoundingSphere());
		this.visualNode.getSpatial().updateModelBound();
		this.visualNode.getSpatial().updateWorldBound();
		this.orientation = new OrientationInfo();

	}
	
	final float MIN_THICKNESS = 0.1f;
	private static final float CONTROL_DISTANCE=1f;
	
	private static final Vector3f control = new Vector3f(CONTROL_DISTANCE,CONTROL_DISTANCE,CONTROL_DISTANCE);
	@Override
	protected Vector3f getControlDistance() {
		return control;
		
	}

	private Quaternion cacheRotation = null;
	private Quaternion compareRotation = null;
	private Vector3f localDirection = null;
	
	
	@Override
	protected void move(ControllableCylinder toControl, Vector3f position) {
		if (toControl == null )
			return;

		if (!toControl.getLocalRotation().equals(compareRotation))
		{
			compareRotation = new Quaternion().set(toControl.getLocalRotation());
			cacheRotation = toControl.getLocalRotation().inverse();
			localDirection = compareRotation.mult(point.direction);
			orientation.updateOrientation();
		}		
		
		Vector3f scale = toControl.getLocalScale();
		Vector3f old = new Vector3f(scale);

		Vector3f pos = new Vector3f( position);

		float stretch;

		Vector3f bottom = toControl.getLocalTranslation().subtract(compareRotation.mult(scale.mult(point.direction)).multLocal(0.5f*point.flip));
		
	
		if (point != RADIUS)
			pos.subtractLocal(bottom);
		
		//stretch= Math.abs((pos.dot(point.direction)- CONTROL_DISTANCE*point.flip)  );

	
		stretch= pos.dot(localDirection)*point.flip - CONTROL_DISTANCE;
		
		if (stretch < MIN_THICKNESS/2f)
			stretch = MIN_THICKNESS/2f;
		
		 Vector3f mod = new Vector3f();
			
	 	 
	 	 if (point==RADIUS)
	 	 {
	 		stretch = controllable.getValidatedScale(stretch,RADIUS);
			 mod.set(1,1,0);
	
		 	 mod.multLocal(stretch);
		 	 
	 		 scale.multLocal(new Vector3f(0,0,1)).addLocal(mod.multLocal(2f));	
	 	 }else
	 	 {
	 		 if (point == CylinderPoint.LEFT)
	 			 stretch = controllable.getValidatedScale(stretch,CylinderPoint.LEFT);
	 		 else if (point == CylinderPoint.RIGHT)
	 			 stretch = controllable.getValidatedScale(stretch,CylinderPoint.RIGHT);
	 		 
			  mod.set(point.direction);
		 	 mod.multLocal(stretch);
		 	 scale.multLocal(point.inverse).addLocal(mod);//.add(0f,+CONTROL_DISTANCE/2f,0f));		
		 	
			 toControl.getLocalTranslation().addLocal(toControl.getLocalRotation().mult(scale.subtract(old)).divideLocal(2f).multLocal(point.flip));
	 	 }
	 	 
	 	 toControl.updateModel();
		 
		 if (point==RADIUS)
		 {
			 this.updatePosition();
		 }else
		 {	 
			 if (super.siblings != null)
			 {	
				for (ControlPoint control:siblings)
					control.updatePosition();
			 }else
				 this.updatePosition();
		 }
	}
	
	

	
	@Override
	public void updatePosition()
	{			
		if (controllable == null)
			return;
		if (!controllable.getLocalRotation().equals(compareRotation))
		{
			compareRotation = new Quaternion().set(controllable.getLocalRotation());
			cacheRotation = controllable.getLocalRotation().inverse();
			localDirection = compareRotation.mult(point.direction);
			orientation.updateOrientation();
		}
		Vector3f control = new Vector3f(CONTROL_DISTANCE,CONTROL_DISTANCE,CONTROL_DISTANCE).multLocal(localDirection);

		if (point == RADIUS)
		{
				this.getModel().getLocalTranslation().set((controllable.getLocalRotation().mult(controllable.getLocalScale().mult(point.direction)).multLocal(0.5f).addLocal(control).add(controllable.getLocalTranslation())));

		}
		else
		{
			this.getModel().getLocalTranslation().set((controllable.getLocalRotation().mult(controllable.getLocalScale().mult(point.direction)).multLocal(0.5f).addLocal(control)).multLocal(point.flip).add(controllable.getLocalTranslation()));
			
			//this.getLocalTranslation().set(toControl.getLocalScale().mult(point.direction).multLocal(0.5f).addLocal(control.multLocal(1f)).add(toControl.getLocalTranslation()));
		}
		
	}

	private static String[] keys = new String[]{CylinderInterpreter.CYL_RADIUS,CylinderInterpreter.CYL_HEIGHT,CylinderInterpreter.LOCALTRANSLATION};

	@Override
	protected String[] getKeys() {
		return keys;
	}
	
	@Override
	public Action<?> getAction(Type type) throws ActionTypeException {
		if (type == Action.ORIENTATION)
			return orientation;
		else if (type == Action.MOVE_RESTRICTED)
			return moveRestricted;
		else
			return super.getAction(type);
	}
	
	
	
	private final MoveRestrictedInfo moveRestricted = new MoveRestrictedInfo();
	private final Vector3f cacheUnit = new Vector3f();
	protected class MoveRestrictedInfo extends MoveRestrictedInformation
	{
		@Override
		public Actionable getControlled() {
			return CylinderControlPoint.this;
		}
		
		@Override
		public Vector3f getRestrictedPosition(Vector3f from) {
			//Get the closest grid node to the provided position;
			if (controllable == null)
				return from;
			
			if (point == RADIUS)
			{
			
				controllable.getParent().updateWorldData();
				controllable.updateWorldData();

				from = controllable.getParent().worldToLocal(from,from);
		
				cacheUnit.set(getControlDistance());
				from.subtractLocal(cacheUnit.divideLocal(2f));//Have to account for control distance...
				
				cacheUnit.set(ActionToolSettings.getInstance().getGridUnits().getValue()).divideLocal(2f);

				from.divideLocal(cacheUnit);
				from.x = Math.round(from.x);
				from.y = Math.round(from.y);
				from.z = Math.round(from.z);
				from.multLocal(cacheUnit);			
				
				cacheUnit.set(getControlDistance());
				from.addLocal(cacheUnit.divideLocal(2f));//Have to account for control distance...

				from = controllable.getParent().localToWorld(from,from);
				return from;
			}else
			{
				
				controllable.getParent().updateWorldData();
				controllable.updateWorldData();

				from = controllable.getParent().worldToLocal(from,from);
				
				Vector3f scale = controllable.getLocalScale();
				if (!controllable.getLocalRotation().equals(compareRotation))
				{
					compareRotation = new Quaternion().set(controllable.getLocalRotation());
					cacheRotation = controllable.getLocalRotation().inverse();
					localDirection = compareRotation.mult(point.direction);
					orientation.updateOrientation();
				}		
	

				Vector3f bottom = controllable.getLocalTranslation().subtract(compareRotation.mult(scale.mult(point.direction)).multLocal(0.5f*point.flip));
				
				from.subtractLocal(bottom);

				cacheUnit.set(getControlDistance());
				from.subtractLocal(cacheUnit);//Have to account for control distance...
				
				cacheUnit.set(ActionToolSettings.getInstance().getGridUnits().getValue());
				
				from.divideLocal(cacheUnit);
				from.x = Math.round(from.x);
				from.y = Math.round(from.y);
				from.z = Math.round(from.z);
				from.multLocal(cacheUnit);			
				
				cacheUnit.set(getControlDistance());

				from.addLocal(cacheUnit);//Have to account for control distance...

				from.addLocal(bottom);
				from = controllable.getParent().localToWorld(from,from);
				return from;
			}			
		}	
	}
	
	
	// = new OrientationInfo();

	private class OrientationInfo extends ControlPoint.OrientationInfoImpl
	{
		public OrientationInfo()
		{		

			orientation = new Orientation(CameraTool.getCameraDirection().cross(point.direction).cross(point.direction),point.direction);
			super.useAxis = true;
		}

		@Override
		public Vector3f getAxis() {
			
			return point.direction;
		}



	}


	public static interface ControllableCylinder extends ControllableModel
	{

		public Vector3f getLocalScale();
		
		/**
		 * If this shape restricts certain components of its scale, beyond just that they can't go below zero (or the minimum thickness),
		 * the shape can override the calculated scale here.
		 * @param stretch
		 * @param type
		 * @return
		 */
		public float getValidatedScale(float stretch, CylinderPoint type);
		public Vector3f getLocalTranslation();
		public Quaternion getLocalRotation();
		public Vector3f getWorldTranslation();
		public Quaternion getWorldRotation();
		public void updateWorldData();
	}
	
}
