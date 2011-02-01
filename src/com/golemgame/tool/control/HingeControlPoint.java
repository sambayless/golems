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
import com.golemgame.mvc.golems.HingeInterpreter;
import com.golemgame.tool.ActionToolSettings;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.action.information.MoveRestrictedInformation;
import com.golemgame.tool.action.information.OrientationInformation;
import com.jme.bounding.BoundingSphere;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.SceneElement;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Sphere;

public class HingeControlPoint extends ControlPoint<HingeControlPoint.ControllableHinge> {

	private Geometry controlSphere;
	private Geometry facadeSphere;
	private OrientationInformation orientation;
	
	
	public static enum HingePosition
	{
		HINGE(), SCALE();		
	}
	
	public static final HingePosition HINGE = HingePosition.HINGE;
	public static final HingePosition SCALE = HingePosition.SCALE;
	
	public static final float SMALLEST_ANGLE = FastMath.HALF_PI;
	
	private final HingePosition type;

//	private Vector3f center = null;
//	private Quaternion startingRotation = new Quaternion();
	final float MIN_THICKNESS = 0.1f;
	private boolean controlLeft = false;

	


	public boolean isLeft() {
		return controlLeft;
	}

	public HingeControlPoint(HingePosition type, boolean controlLeft) {
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
	private Vector3f _product = new Vector3f();
	private Vector3f _product2 = new Vector3f();
	private final Vector3f hingeAxis = new Vector3f(0,0,1);
	private Vector3f _hingeVector = new Vector3f();
	private Vector3f _offsetVector = new Vector3f();
	
	@Override
	protected void move(ControllableHinge axle, Vector3f position) {

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
		if (type == HINGE)
		{
			//Get the direction that the mouse is from the center of the hinge
			Vector3f direction = _unit.set(position);
			
			if (controlLeft)
			{
				direction.multLocal(-1);
			}
			direction.normalizeLocal();
			
			//drop that direction onto the plane of movement
			Vector3f normalToMovement = _hingeVector.set(hingeAxis);//this should be the direction that the hinge lies along, in hinge local coordinates
			//whatever you do to the local rotation, you should STILL be moving around the local z axis here.
	
			//startingRotation.multLocal(normalToMovement);
			normalToMovement.normalize();
			
			//A || B = B * (A * B / |B|) / |B projection of a vector A onto a plane with normal B. NOTE: division happens AFTER crossing
			//See: http://www.euclideanspace.com/maths/geometry/elements/plane/lineOnPlane/index.htm
			Vector3f product = _product.set(direction);
			product = product.crossLocal(normalToMovement);
			_product2.set(normalToMovement);
			product = _product2.crossLocal(product);		
			direction.set(product);
			direction.normalizeLocal();
			
			//get the angle between these; if it is greater than the allowed angle, set it to the allowed angle
			Vector3f compareVector = new Vector3f(-1,0,0);
			coJoint.getLocalRotation().multLocal(compareVector);//multiply the comparison vector into the coJoint's rotation
			float angle = direction.angleBetween(compareVector);

			if (angle<SMALLEST_ANGLE)
			{
				//get which side of y axis the direction is on, and preserve that
				Vector3f perpendicular = new Vector3f(0,-1,0);
				coJoint.getLocalRotation().multLocal(perpendicular);
				float side = perpendicular.dot(direction);
				float sign = FastMath.sign(side);
				direction.set(compareVector);
				//rotate direction by smallest angle amount along the hinge axis
				Quaternion newDirection = new Quaternion().fromAngleNormalAxis(sign*SMALLEST_ANGLE, normalToMovement);
				newDirection.multLocal(direction);
			}
			
			
			float absoluteAngle;
			
			Vector3f angleProbe = new Vector3f(1,0,0);
		//	if (controlLeft)
		//	angleProbe.x = -1;
			
			
			absoluteAngle = angleProbe.angleBetween(direction) *FastMath.sign( Vector3f.UNIT_Y.dot(direction));
			
			axle.setAngle(absoluteAngle,controlLeft);
			
			/*//set rotation
			Quaternion desiredRotation = new Quaternion();
			
			Vector3f xAxis =_product2.set(normalToMovement).crossLocal(direction).normalizeLocal();
			//IMPORTANT: this must for a proper,RIGHT HANDED, orthogonal coordinate system
			desiredRotation.fromAxes(direction, xAxis, normalToMovement);
		
			joint.getLocalRotation().set(desiredRotation);//.multLocal(startingRotation) ;//(toControl.getWorldRotation().inverse());
			
			//Vector3f newCenter = _offsetVector;//.set(center); 
			//newCenter= joint.getLocalRotation().multLocal(newCenter).subtractLocal(center);
			joint.getLocalTranslation().set(axle.getNaturalTranslation(controlLeft));
			if (!controlLeft)
				joint.getLocalTranslation().x +=joint.getLocalScale().x/2f - 0.5f;
			else
				joint.getLocalTranslation().x += -joint.getLocalScale().x/2f + 0.5f;
			//get the difference in positions of the center (assuming 0 natural local rotation)
			joint.getLocalRotation().multLocal(joint.getLocalTranslation());*/
		//	joint.getLocalTranslation().subtractLocal(newCenter);//.addLocal(hinge.getCoJoint().getLocalTranslation());
		
			
		}else if (type == SCALE)
		{//Scale the joint
			//get the factor by which to scale
			Vector3f direction = _unit.set(position);
			direction.subtractLocal(joint.getLocalTranslation());
	  
			Vector3f normalToMovement = _hingeVector.set(hingeAxis);//this should be the direction that the hinge lies along, in hinge local coordinates
		//	joint.getLocalRotation().multLocal(normalToMovement);
			//normalToMovement.normalizeLocal();
			float amount = position.dot(normalToMovement);
			if (amount < 0)
				amount *= -1;
			
			amount -= CONTROL_DISTANCE;
			
			amount *= 2;
			
			if (amount<MIN_THICKNESS)
				amount = MIN_THICKNESS;
					
			{
				//hinge.getBox().getLocalScale().y = hinge.getNaturalBoxScale(null).y;
				joint.getLocalScale().x = amount;
				joint.getLocalScale().z = amount;				
				
				//hinge.getHinge().getLocalScale().set(hinge.getNaturalHingeScale(hinge.getHinge().getLocalScale()));
				//joint.getConnection().getLocalScale().z = amount;				
				
				//((HingeMaterialNode)hinge.getCoJoint()).getBox().getLocalScale().y = hinge.getNaturalBoxScale(null).y;
				coJoint.getLocalScale().x = amount;
				coJoint.getLocalScale().z = amount;
				joint.getLocalTranslation().set(axle.getNaturalTranslation(controlLeft));
				coJoint.getLocalTranslation().set(axle.getNaturalTranslation(!controlLeft));
				
				if (controlLeft)
				{
					joint.getLocalTranslation().x += -amount/2f + 0.5f;//0.5f to account for the initial size of the box
					coJoint.getLocalTranslation().x += amount/2f - 0.5f;
				}
				else
				{
					joint.getLocalTranslation().x += amount/2f - 0.5f;
					coJoint.getLocalTranslation().x += - amount/2f+ 0.5f;
				}
				joint.getLocalRotation().multLocal(joint.getLocalTranslation());
				coJoint.getLocalRotation().multLocal(coJoint.getLocalTranslation());
				
			}

	
		}
		axle.refresh();
		axle.updateModel();
		for (ControlPoint control:super.siblings)
		{
			control.updatePosition();
		}
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
		if (type == HINGE)
		{
			if (controlLeft)
			{
				control = new Vector3f(0,0,0);
				control.x -= CONTROL_DISTANCE;
				control.x -= joint.getLocalScale().x/2f;
				joint.getLocalRotation().multLocal(control);
				control.addLocal(joint.getLocalTranslation());
			}else
			{

				control = new Vector3f(0,0,0);
				control.x += CONTROL_DISTANCE;
				control.x += joint.getLocalScale().x/2f;
				joint.getLocalRotation().multLocal(control);
				control.addLocal(joint.getLocalTranslation());

			}
			this.getModel().getLocalTranslation().set(control);
		}else
		{
			control = new Vector3f();
			if (controlLeft)
			{
				control = new Vector3f(0,0,0);
				control.z -= CONTROL_DISTANCE;
				control.z  -= joint.getLocalScale().z/2f;
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
				control.z  += joint.getLocalScale().z/2f;
			}

			this.getModel().getLocalTranslation().set(control);

		}
		this.getModel().updateWorldData();
	}

	
	private static String[] keys = new String[]{HingeInterpreter.LEFT_JOINT_ANGLE,HingeInterpreter.LEFT_JOINT_LENGTH,HingeInterpreter.RIGHT_JOINT_ANGLE,HingeInterpreter.RIGHT_JOINT_LENGTH,HingeInterpreter.LOCALROTATION,HingeInterpreter.LOCALTRANSLATION,HingeInterpreter.LOCALSCALE};

	@Override
	protected String[] getKeys() {
		return keys;
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
		ControllableHinge toControl = null;
		
		@Override
		public Actionable getControlled() {
			return HingeControlPoint.this;
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
		//	grid.y/=2f;
			grid.z/=2f;
			pos.divideLocal(grid);
			pos.x = Math.round(pos.x);
			pos.y = Math.round(pos.y); //the rotation implementation is wrong, but it will do for now.
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
			if (type == HINGE)
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
	
	public interface ControllableHinge  extends ControllableModel
	{
		public void refresh();
		public Model getLeftJoint();
		public Model getRightJoint();
		public Vector3f getNaturalTranslation(boolean forLeft);
		public Vector3f getNaturalScale(boolean forLeft);
	//	public HingeInterpreter getInterpreter();
		public Vector3f getLocalCenter(Vector3f store, boolean forLeft);
		public void setAngle(float angle, boolean left);
	}
}
