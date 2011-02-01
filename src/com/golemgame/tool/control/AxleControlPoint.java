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


import com.golemgame.model.Model;
import com.golemgame.mvc.golems.AxleInterpreter;
import com.golemgame.tool.ActionToolSettings;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.action.information.MoveRestrictedInformation;
import com.golemgame.tool.action.information.OrientationInformation;
import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.SceneElement;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Sphere;

public class AxleControlPoint extends ControlPoint<AxleControlPoint.ControllableJoint> {
	
	private Geometry controlSphere;
	private Geometry facadeSphere;
	private OrientationInformation orientation;
	
	
	public static final float MIN_DISTANCE = 0.1f;




	public static enum AxlePosition
	{
		AXLE(), SCALE();		
	}
	
	public static final AxlePosition AXLE = AxlePosition.AXLE;
	public static final AxlePosition SCALE = AxlePosition.SCALE;
	
	final float MIN_THICKNESS = 0.2f;
	
	private AxlePosition type;
	private boolean controlLeft = false;

	
	public boolean isLeft() {
		return controlLeft;
	}

	public AxleControlPoint(AxlePosition type, boolean controlLeft) {
		super();
		this.controlLeft = controlLeft;
		this.type = type;
		controlSphere = new Sphere("ControlSphere", new Vector3f(), 6, 6, radius);
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
		
		orientation = new OrientationInfo();

	}

	@Override
	protected Vector3f getControlDistance() {
		// TODO Auto-generated method stub
		return null;
	}

	private Vector3f _unit = new Vector3f();

	private final Vector3f slideAxis = new Vector3f(0,0,1);
	private final Vector3f scaleAxis = new Vector3f(1,0,0);
	private Vector3f _slideVector = new Vector3f();

	
	@Override
	protected void move(ControllableJoint axle, Vector3f position) {

		axle.updateModel();
		Model joint;
		Model coJoint;
		
		if (controlLeft)
		{
			joint = axle.getLeftJoint();
			coJoint = axle.getRightJoint();
		}else
		{
			coJoint = axle.getLeftJoint();
			joint = axle.getRightJoint();
		}
		
		if (type == AXLE)
		{

			Vector3f direction = _unit.set(position);
			direction.subtractLocal(joint.getLocalTranslation());

			Vector3f normalToMovement = _slideVector.set(scaleAxis);//this should be the direction that the hinge lies along, in hinge local coordinates
			//joint.getLocalRotation().multLocal(normalToMovement);
		
			float amount = position.dot(normalToMovement);
			if (amount < 0)
				amount *= -1;
			
			amount -= CONTROL_DISTANCE;
			
		//	amount *= 2;
			
			if (amount<MIN_THICKNESS)
				amount = MIN_THICKNESS;
			
			Vector3f axisOfMovement = new Vector3f(1,0,0);
			if (controlLeft)
				axisOfMovement.multLocal(-1);
			

			float maximumWidth =Math.abs( joint.getLocalTranslation().dot(axisOfMovement) -coJoint.getLocalTranslation().dot(axisOfMovement));
			maximumWidth -= MIN_DISTANCE;//axle.getGeometry().getLocalScale().z/2f;
			
			//if (amount>maximumWidth)
			//	amount = maximumWidth;
			
			
			joint.getLocalScale().z = amount;
			
				
			joint.getLocalTranslation().x = -Math.signum(this.controllable.getNaturalTranslation(controlLeft).x)* joint.getLocalScale().z/2f;;
		
			//joint.getLocalScale().z = this.controllable.getNaturalScale(controlLeft).z;
			//coJoint.getLocalScale().z = this.controllable.getNaturalScale(!controlLeft).z;
			
			axle.updateModel();


			
			
		}
		else
		{//Scale the joint
		
			Vector3f direction = _unit.set(position);
			direction.subtractLocal(joint.getLocalTranslation());

			Vector3f normalToMovement = _slideVector.set(slideAxis);//this should be the direction that the hinge lies along, in hinge local coordinates
			//joint.getLocalRotation().multLocal(normalToMovement);
		
			float amount = position.dot(normalToMovement);
			if (amount < 0)
				amount *= -1;
			
			amount -= CONTROL_DISTANCE;
			
			amount *= 2;
			
			if (amount<MIN_THICKNESS)
				amount = MIN_THICKNESS;
			
			Vector3f axisOfMovement = new Vector3f(1,0,0);
			if (controlLeft)
				axisOfMovement.multLocal(-1);
			

			float maximumWidth =Math.abs( joint.getLocalTranslation().dot(axisOfMovement) -coJoint.getLocalTranslation().dot(axisOfMovement));
			maximumWidth -= MIN_DISTANCE;//axle.getGeometry().getLocalScale().z/2f;
			
		//	if (amount>maximumWidth)
		//		amount = maximumWidth;
			
			
			
			joint.getLocalScale().x = amount*this.controllable.getNaturalScale(controlLeft).x;
			joint.getLocalScale().y = amount*this.controllable.getNaturalScale(controlLeft).y;
		
			
			//axle.getConnection().getLocalScale().x = amount;
			//axle.getConnection().getLocalScale().y = amount;	
			
			
			coJoint.getLocalScale().x = amount*this.controllable.getNaturalScale(!controlLeft).x;
			coJoint.getLocalScale().y = amount*this.controllable.getNaturalScale(!controlLeft).y;
			axle.updateModel();

			
		}
		
		axle.refresh();
			for (ControlPoint<?> control:super.siblings)
			{
				control.updatePosition();
			}
		
	}
	
	private static String[] keys = new String[]{AxleInterpreter.LEFT_JOINT_LENGTH,AxleInterpreter.LOCALTRANSLATION,AxleInterpreter.LEFT_JOINT_RADIUS,AxleInterpreter.RIGHT_JOINT_LENGTH,AxleInterpreter.RIGHT_JOINT_RADIUS};

	@Override
	protected String[] getKeys() {
		return keys;
	}
	
	private static final float CONTROL_DISTANCE=1f;
	
	@Override
	public void updatePosition() {
		if (controllable == null)
			return;
		Model joint;
		Model coJoint;
		
		if (controlLeft)
		{
			joint = controllable.getLeftJoint();
			coJoint = controllable.getRightJoint();
		}else
		{
			coJoint = controllable.getLeftJoint();
			joint = controllable.getRightJoint();
		}
		
		Vector3f control;
		if (type == AXLE)
		{
			if (controlLeft)
			{
				control = new Vector3f(joint.getLocalTranslation());
				control.x -= CONTROL_DISTANCE;
				control.x -= joint.getLocalScale().z/2f;
			}else
			{
				control = new Vector3f(joint.getLocalTranslation());
				control.x += CONTROL_DISTANCE;
				control.x += joint.getLocalScale().z/2f;
			}
			this.getModel().getLocalTranslation().set(control);
		}else
		{
			control = new Vector3f();
			if (controlLeft)
			{
				control = new Vector3f(0,0,0);
				control.z -= CONTROL_DISTANCE;
				control.z  -= joint.getLocalScale().x/2f/ controllable.getNaturalScale(controlLeft).x;
		
			//	control.set( axle.getConnection().getLocalTranslation());
			//	control.z -= CONTROL_DISTANCE;
			//	control.z -= axle.getGeometry().getLocalScale().z/2f;// -hinge.getNaturalBoxScale(new Vector3f()).z;

						
			}else
			{
			//	control.set( axle.getConnection().getLocalTranslation()).multLocal(-1);
			//	control.z += CONTROL_DISTANCE;
			//	control.z += axle.getGeometry().getLocalScale().z /2f;// -hinge.getNaturalBoxScale(new Vector3f()).z;
				control = new Vector3f(0,0,0);
				control.z += CONTROL_DISTANCE;
				control.z  += joint.getLocalScale().x/2f/ controllable.getNaturalScale(controlLeft).x;
	
			}
			control.x = 0;// (controllable.getLeftJoint().getLocalTranslation().x + controllable.getRightJoint().getLocalTranslation().x )/2f;
			control.y = 0;//(controllable.getLeftJoint().getLocalTranslation().y + controllable.getRightJoint().getLocalTranslation().y )/2f;


			this.getModel().getLocalTranslation().set(control);

		}
		this.getModel().updateWorldData();
	}

	@Override
	public Action<?> getAction(Type type) throws ActionTypeException {
		if (type == Action.ORIENTATION)
			return orientation;
		else if (type == Action.MOVE_RESTRICTED)		
			return new MoveRestrictedInfo();
		
		return super.getAction(type);
	}
	
	
	protected  class MoveRestrictedInfo extends MoveRestrictedInformation
	{
		AxleControlPoint.ControllableJoint toControl = null;
		
		
		
		@Override
		public Actionable getControlled() {
			return AxleControlPoint.this;
		}



		public MoveRestrictedInfo() {
			super();
			toControl = controllable;
		}



		public Vector3f getRestrictedPosition(Vector3f from) {
			//Get the closest grid node to the provided position;	
			
			
			Vector3f pos = toControl.getRelativeFromWorld(from, new Vector3f());
		//	System.out.println(pos);
			//pos.subtractLocal(ActionToolSettings.getInstance().getGridOrigin().getValue());
			Vector3f grid = new Vector3f( ActionToolSettings.getInstance().getGridUnits().getValue());
			grid.y/=2f;
			grid.z/=2f;
			pos.divideLocal(grid);
			pos.x = Math.round(pos.x);
			pos.y = Math.round(pos.y);
			pos.z = Math.round(pos.z);
			pos.multLocal(grid);
			return toControl.getWorldFromRelative(pos, from);
		}	
	}
	
	private class OrientationInfo extends ControlPoint.OrientationInfoImpl
	{
		Vector3f direction = new Vector3f();
		public OrientationInfo()
		{
			super.useAxis = true;
		}
		
		
		@Override
		public Vector3f getAxis() {
			Model joint;
			Model coJoint;
			
			if (controlLeft)
			{
				joint = controllable.getLeftJoint();
				coJoint = controllable.getRightJoint();
			}else
			{
				coJoint = controllable.getLeftJoint();
				joint = controllable.getRightJoint();
			}
			joint.updateWorldData();
			if (type == AXLE)
			{
				Vector3f dir =new Vector3f().set(1,0,0);
				joint.getWorldRotation().multLocal(dir);
				dir.normalizeLocal();
				return dir;
			}else
			{
				Vector3f dir =new Vector3f().set(0,0,1);
				joint.getWorldRotation().multLocal(dir);
				dir.normalizeLocal();
				return dir;
			}
		}

	}
	
	
	public interface ControllableJoint extends ControllableModel
	{
		public void refresh();
		public Model getLeftJoint();
		public Model getRightJoint();
		public Vector3f getNaturalTranslation(boolean forLeft);
		public Vector3f getNaturalScale(boolean forLeft);
	//	public AxleInterpreter getInterpreter();
	}
}
