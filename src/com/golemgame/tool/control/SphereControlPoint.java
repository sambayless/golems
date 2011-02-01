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


import com.golemgame.mvc.golems.SphereInterpreter;
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

public class SphereControlPoint extends ControlPoint<SphereControlPoint.ControllableSphere>
{


	private Spatial controlSphere;
	private Geometry facadeSphere;
	
	public static enum SpherePoint
	{
		SPHERE(new Vector3f(1,1,1)), X(new Vector3f(1,0,0)), Y(new Vector3f(0,1,0)), Z(new Vector3f(0,0,1));
		
		public Vector3f direction;
		public Vector3f inverse;
		private SpherePoint(Vector3f direction)
		{
			this.direction = direction;//.normalize();
			this.inverse = new Vector3f(1,1,1).subtractLocal(direction);
		}
	}
	
	public static SpherePoint SPHERE = SpherePoint.SPHERE;
	public static SpherePoint X = SpherePoint.X;
	public static SpherePoint Y = SpherePoint.Y;
	public static SpherePoint Z = SpherePoint.Z;
	
	private SpherePoint point;
	
	public SphereControlPoint(SpherePoint point) 
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

	
	//private Vector3f direction = new Vector3f(1,0,0);//the axis along which this node can move

	@Override
	protected void move(ControllableSphere toControl, Vector3f position) {
		if (toControl == null )
			return;
		

			Vector3f pos = position;
			
			//this.getParent().worldToLocal(moveTo, pos);		
			Vector3f scale = toControl.getLocalScale();
			
			
			float stretch;
			if (point == SPHERE)
				stretch= pos.dot(new Vector3f(1,0,0)) - CONTROL_DISTANCE;
			else	
				stretch= pos.dot(point.direction) - CONTROL_DISTANCE;
			
			if (stretch < MIN_THICKNESS)
				stretch = MIN_THICKNESS;
			 Vector3f mod = new Vector3f();
			 mod.set(point.direction);
		 	 mod.multLocal(stretch);			

			 scale.multLocal(point.inverse).addLocal(mod.multLocal(2f));			 
			 
			 toControl.updateModel();
			 this.updatePosition();

	
	}


	@Override
	public void updatePosition()
	{			
			
			if (controllable == null)
				return;
			
			
		//	Vector3f pos = new Vector3f();
									
			Vector3f control = new Vector3f(CONTROL_DISTANCE,CONTROL_DISTANCE,CONTROL_DISTANCE).multLocal(point.direction);
			
			if (point == SPHERE)
			{
				this.getModel().getLocalTranslation().set((controllable.getLocalScale().mult(new Vector3f(1,0,0)).multLocal(0.5f).addLocal(control.multLocal(new Vector3f(1,0,0)))));

			}else
			{//pos.set(point.direction);
				this.getModel().getLocalTranslation().set((controllable.getLocalScale().mult(point.direction).multLocal(0.5f).addLocal(control)));
			}
			//this.getLocalTranslation().set(pos);
		
	}

	@Override
	public Action getAction(Type type) throws ActionTypeException {
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
			return SphereControlPoint.this;
		}
		
		@Override
		public Vector3f getRestrictedPosition(Vector3f from) {
			//Get the closest grid node to the provided position;
			if (controllable == null)
				return from;


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
			
		}	
	}
	
	private static String[] keys = new String[]{SphereInterpreter.RADII};

	@Override
	protected String[] getKeys() {
		return keys;
	}

	
	private OrientationInformation orientation;

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

	};

	
	public static interface ControllableSphere extends ControllableModel
	{

		public Vector3f getLocalScale();
		public Vector3f getLocalTranslation();
		public Quaternion getLocalRotation();
		public Vector3f getWorldTranslation();
		public Quaternion getWorldRotation();
		public void updateWorldData();
		//public SphereInterpreter getInterpreter();
	}


}
