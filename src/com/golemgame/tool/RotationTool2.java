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
package com.golemgame.tool;


import com.golemgame.model.Model;
import com.golemgame.states.StateManager;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.MoveAction;
import com.golemgame.tool.action.RotateAction;
import com.golemgame.tool.action.information.ModelInformation;
import com.golemgame.tool.action.information.Orientation;
import com.golemgame.tool.action.information.OrientationInformation;
import com.jme.input.MouseInput;
import com.jme.math.FastMath;
import com.jme.math.Plane;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.system.DisplaySystem;

public class RotationTool2 extends MovementTool2 {

	/**
	 * This is the position, in world translation, that all objects are rotating about.
	 * The starting world position, relative to this translation, defines 0 degrees of rotation.
	 */
	private Vector3f centralWorldPivot = new Vector3f();
	private Quaternion zeroRotation;
	private Vector3f worldZeroVector;
	//private Vector3f worldZeroVectorPerp;
	


	public RotationTool2() {
		super();
		super.selectionEffect = new RotationSelectionEffect();
		super.requireRotation =true;	
	}
	
	

	@Override
	public void yzPlane(boolean value) {
		if (value)
		{
			curAxis = Axis.xAxis;
			configureOrientation(findOptimalLocalOrientation(OrientationInformation.YZ));
		}
		else
		{
			curAxis = Axis.yAxis;
			configureOrientation(null);
		}
	}



	@Override
	public void xyPlane(boolean value) {
		if (value)
		{
			curAxis = Axis.zAxis;
			configureOrientation(findOptimalLocalOrientation(OrientationInformation.XY));
		}
		else
		{
			curAxis = Axis.yAxis;
			configureOrientation(null);
		}

	}



	@Override
	public void mouseMovementAction(Vector2f mousePos, boolean left,
			boolean right) {
		if(!left || primarySelection==null)
		{
			StateManager.getToolPool().getCameraTool().mouseMovementAction(mousePos, left, right);
			return;
		}
		//first, create the rotation that we need from these positions:
		
		
		Orientation orientation = primarySelection.getOrientationInfo().getOrientation();
		orientation.getDirection().normalizeLocal();
	
		if (!orientation.equals(cacheOrientation))
		{
			cacheOrientation = new Orientation(orientation);
			configureOrientation();
		}
		
		
		Ray mouseRay = new Ray();
		Vector3f mouseIntersection = new Vector3f();
		DisplaySystem.getDisplaySystem().getPickRay(mousePos,  StateManager.IS_AWT_MOUSE, mouseRay);
		boolean mouseIntersects = mouseRay.intersectsWherePlane(areaPlane, mouseIntersection);//if the mouse ray intersects at all
		if(!mouseIntersects)
			return;

		
		


		//project the intersection onto the plane
	
		
		Vector3f relativeStartingPosition = new Vector3f(this.startLocation).subtractLocal(centralWorldPivot).normalizeLocal();
		relativeStartingPosition.set( orientation.getDirection().cross(relativeStartingPosition.cross(orientation.getDirection()))).normalizeLocal();
		
		
		
		Vector3f relativeNewPosition =  mouseIntersection.subtract(this.centralWorldPivot).normalizeLocal();
		relativeNewPosition.set( orientation.getDirection().cross(relativeNewPosition.cross(orientation.getDirection()))).normalizeLocal();
		
		
		Quaternion primaryRotation;
		
		
		{//alter the relative new position to be on the grid
			//System.out.println(relativeNewPosition + "\t" +  relativeNewPosition.subtract(worldZeroVector));
		//	
			//explanation: normally, the rotation is between the starting mouse position, and the new mouse position
			//and that relative rotation is applied to the object
			
			
			
		/*	Vector3f basePosition = relativeStartingPosition;//new Vector3f( worldZeroVector).addLocal(relativeStartingPosition).normalizeLocal();
		//	System.out.println(angle);
			//relativeNewPosition.addLocal(relativeStartingPosition).normalizeLocal();
			float angle =  relativeNewPosition.angleBetween(basePosition )*-FastMath.sign( relativeNewPosition.dot( basePosition.cross(orientation.getDirection())));//selectedNode.getLocalRotation().mult(worldZeroVector));  //projection.angleBetween(worldZeroVector)*-FastMath.sign(projection.dot(worldZeroVector));
*/			
			float step = FastMath.PI/ActionToolSettings.getInstance().getRotationSnapStep().getValue();
			//add or remove from angle so that, when combined with the current primary rotation...
			
			
			
			Vector3f currentRot = primarySelection.getStartingWorldRotation().mult(worldZeroVector).normalizeLocal();
			
			currentRot = orientation.getDirection().cross(currentRot.cross(orientation.getDirection()));
			currentRot.normalizeLocal();
			
			//get the distance between the starting click and the current rotation
			float relativeAngle = relativeStartingPosition.angleBetween(currentRot)*-FastMath.sign( relativeStartingPosition.dot( currentRot.cross(orientation.getDirection())));
			
			float angle = relativeNewPosition.angleBetween(worldZeroVector )*-FastMath.sign( relativeNewPosition.dot( worldZeroVector.cross(orientation.getDirection())));
			float currentAngle = currentRot.angleBetween(worldZeroVector )*-FastMath.sign( currentRot.dot( worldZeroVector.cross(orientation.getDirection())));
		
			angle -= relativeAngle;//correct for the starting click position
			if (ActionToolSettings.getInstance().isRestrictMovement())
			{
				angle = Math.round(angle/step)*step;
			}
			
		//	System.out.println(angle + "\t" + currentAngle + "\t"+ currentRot + "\t" + worldZeroVector);
			
		
			Quaternion rotation = new Quaternion().fromAngleNormalAxis(angle-currentAngle, orientation.getDirection());
		//	relativeNewPosition = rotation.mult(worldZeroVector);
			
			//relativeNewPosition = basePosition;
			primaryRotation =rotation.mult( primarySelection.getStartingWorldRotation());

			
			
		}
	

		RotateAction rotate = primarySelection.getRotate().copy();
	
		
		rotate.setRotation(primaryRotation);
		
		
		if(rotate.doAction())
		{
				needsUndo = true;
				//primarySelection.getActionList().mergeAction(rotate);
		}
	//	primarySelection.getSelectedModel().updateWorldData();
		for(SelectionData data:selectedItems)
		{
	
			if(data.equals(primarySelection))
			{
		
			}else
			{
				
				//		toAttach.getLocalRotation().set(attachTo.getWorldRotation().inverse()).multLocal(worldRotation);
				
				Quaternion finalNewRotation;
				 rotate = data.getRotate().copy();
				Quaternion newRotation =primaryRotation;//.mult( data.getStartingWorldRotation());
				Quaternion relativeRotation = primarySelection.getStartingWorldRotation().inverse().mult(data.getStartingWorldRotation());
				finalNewRotation = newRotation.mult(relativeRotation);
				rotate.setRotation(finalNewRotation);
				rotate.doAction();
				
				
				Vector3f relativeLocalPosition = data.getStartingWorldTranslation().subtract(centralWorldPivot);
				//	Quaternion newRotation =primaryRotation;//.mult( data.getStartingWorldRotation());
					Quaternion relativeNewRotation = primaryRotation.mult(primarySelection.getStartingWorldRotation().inverse());
					relativeNewRotation.multLocal(relativeLocalPosition);
					MoveAction move= data.getMove().copy();
					move.setPosition(relativeLocalPosition.addLocal(centralWorldPivot));
					
					
					if(move.doAction())
					{
							needsUndo = true;
						//	ActionList minor = new ActionList("Rotate");
						//	minor.add(rotate);
						//	minor.add(move);
						//	primarySelection.getActionList().mergeAction(minor);
							
					}
					

			}
	
		}
		
		//this should be the final, total rotation.
	}

	@Override
	public void deselect() {
		// TODO Auto-generated method stub
		super.deselect();
	}

	@Override
	public void select(Actionable toSelect, Model selectedModel,boolean primary)
			throws FailedToSelectException {

		super.select(toSelect, selectedModel,primary);
		
		if(primary)
		{
			if(super.primarySelection!=null)
			{
				primarySelection.getSelectedModel().updateWorldData();
				centralWorldPivot.set(primarySelection.getSelectedModel().getWorldTranslation());
			
			}else
			{
				throw new FailedToSelectException();
			}
			configureOrientation();
		}
	}

	@Override
	protected void configureOrientation(Orientation preferedOrientation) {
		if (primarySelection==null)
			return;

		if (preferedOrientation == null){
			if (ActionToolSettings.getInstance().getYZAxisAction().isValue())			
			{
				curAxis = Axis.xAxis;
				worldZeroVector = new Vector3f(Vector3f.UNIT_X);
				preferedOrientation =(findOptimalLocalOrientation(OrientationInformation.YZ));			
			}
			else if (ActionToolSettings.getInstance().getXYAxisAction().isValue())
			{
				curAxis = Axis.zAxis;
				worldZeroVector = new Vector3f(Vector3f.UNIT_Z);
				preferedOrientation = (findOptimalLocalOrientation(OrientationInformation.XY));					
			}
			else //default 
			{
				curAxis = Axis.yAxis;
				
				preferedOrientation = (findOptimalLocalOrientation(OrientationInformation.XZ));								
			}
		}
		areaPlane = new Plane();
		
		OrientationInformation orientationInfo =(OrientationInformation) primarySelection.getOrientationInfo();
		orientationInfo.updateOrientation(preferedOrientation);
		Orientation orientation = preferedOrientation;//orientationInfo.getOrientation();
		Quaternion rotation = new Quaternion();

			  rotation.fromAxes(orientation.getHorizontal(), orientation.getDirection(), orientation.getHorizontal().cross(orientation.getDirection()));
				selectionEffect.setTargetRotation(rotation);
				if(usesEffect && MouseInput.get().isButtonDown(0))
					selectionEffect.setEngage(true);
				else
					selectionEffect.setEngage(false);
		primarySelection.getEffect().update();

		super.areaPlane.setNormal(preferedOrientation.getDirection());

		primarySelection.getSelectedModel().updateWorldData();
		super.areaPlane.setConstant(-areaPlane.normal.dot(centralWorldPivot));//move the area plane so that it the intersection point lies on it (constant is the negative of the distance from origin)
		

	//	zeroRotation.fromAngleNormalAxis(orientation.getHorizontal().angleBetween(projection)*side, invertParentWorldRotation.mult(orientation.getDirection()));
		
		/*worldZeroVector =  (OrientationInformation.XZ.getDirection().cross(orientation.getDirection()));
		if (worldZeroVector.length() <= Float.MIN_VALUE)
		{//if this is parallel to the world
			worldZeroVector =   (OrientationInformation.YZ.getDirection().cross(orientation.getDirection()));//orientation.getDirection().cross(OrientationInformation.YZ.getDirection());
		}*/
		//take the zero vector, project it into the plane, and normalize
		//A || B = B x (AxB / |B|) / |B| projection of a vector A onto a plane with normal B. NOTE: division happens AFTER crossing
		//See: http://www.euclideanspace.com/maths/geometry/elements/plane/lineOnPlane/index.htm

		worldZeroVector = new Vector3f(Vector3f.UNIT_Y);
	/*	
		if(orientation.getDirection().add(worldZeroVector).lengthSquared()<FastMath.FLT_EPSILON){
			//orientation is approx negative world zero vector...
			orientation.getDirection().multLocal(-1f);
			orientation.getHorizontal().multLocal(-1f);
			orientation.getVertical().multLocal(-1f);
		}*/
		
		if(worldZeroVector.subtract(orientation.getDirection()).lengthSquared()<FastMath.FLT_EPSILON || ((orientation.getDirection().cross( worldZeroVector).lengthSquared()<FastMath.FLT_EPSILON)))
			worldZeroVector = new Vector3f(Vector3f.UNIT_Z);
		worldZeroVector =(orientation.getDirection().cross( worldZeroVector));//.cross(orientation.getDirection()));
		worldZeroVector.set( orientation.getDirection().cross(worldZeroVector.cross(orientation.getDirection()))).normalizeLocal();
		
		worldZeroVector.normalizeLocal();
	//	System.out.println(worldZeroVector);
		//worldZeroVector.normalizeLocal();
		//worldZeroVectorPerp =  worldZeroVector.cross(orientation.getDirection());
		
		updateStartingPositions();
	}
	private Orientation findOptimalLocalOrientation(Orientation compare)
	{		

		if(primarySelection == null)//temporary fix
			return compare;
		primarySelection.getSelectedModel().updateWorldData();
		compare = compare.mult(primarySelection.getSelectedModel().getWorldRotation());
		//originalSelection.getWorldRotation().multLocal(compare.getDirection());
		compare.getDirection().normalizeLocal();
		return compare;

	}

	@Override
	protected void updateStartingPositions() {
		super.updateStartingPositions();
		for (SelectionData data:selectedItems)
		{
			
			try{
				ModelInformation modelInfo=  (ModelInformation)data.getActionable().getAction(Action.MODEL);
				data.getStartingWorldRotation().set(modelInfo.getCollisionModel().getLocalRotation());
				
			}catch(ActionTypeException e)
			{
				
			}
		
			
		}
	}
	
	
	
}
