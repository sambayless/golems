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
package com.golemgame.tool.action.information;



import com.golemgame.model.Model;
import com.golemgame.tool.CameraTool;
import com.golemgame.tool.action.Action;
import com.jme.math.Vector3f;
import com.jme.system.DisplaySystem;

public abstract class OrientationInformation extends ActionInformation {
	private static final float MIN_ANGLE = 0.35f;
	
	
	
	protected Orientation orientation;
	
	protected boolean useAxis = false;
	
	
	
	public static final Orientation XY = new Orientation( new Vector3f(0,0,1), new Vector3f(1,0,0));
	public static final Orientation YZ= new Orientation( new Vector3f(1,0,0), new Vector3f(0,0,1));
	public static final Orientation XZ= new Orientation( new Vector3f(0,1,0), new Vector3f(1,0,0));	
	public static final Orientation CAMERA = new CameraOrientation();
	
	public void setSelectedModel(Model model)
	{
		
	}
	
	public static class CameraOrientation extends Orientation
	{

		@Override
		public Vector3f getDirection() {
			
			return super.getDirection();// DisplaySystem.getDisplaySystem().getRenderer().getCamera().getDirection();
			
		}

		@Override
		public Vector3f getHorizontal() {
			return super.getHorizontal();// DisplaySystem.getDisplaySystem().getRenderer().getCamera().getLeft();
			
		}

		@Override
		public Vector3f getVertical() {
			return super.getVertical();//return DisplaySystem.getDisplaySystem().getRenderer().getCamera().getUp();
			
		}
		private CameraOrientation()
		{		
			super(new Vector3f(), new Vector3f());	
		}
	}
	
	@Override
	public Type getType() {
		return Action.ORIENTATION;
	}
	

	
	public Vector3f getAxis()
	{
		return null;
	}
	
	/**
	 * The axis in local units
	 * @return
	 */
	public Vector3f getLocalAxis()
	{
		return null;
	}


	public Orientation getOrientation()
	{
		if (orientation == null)
			orientation = new Orientation(XZ);
		return orientation;
	}
		
	//public abstract Vector3f setOrientation(Vector3f prefferedOrientation);

	public Vector3f getHorizontal() 
	{
		if (orientation == null)
			orientation = new Orientation(XZ);
		return orientation.getHorizontal();
	}
	
	public Vector3f getDirection()
	{
		if (orientation == null)
			orientation = new Orientation(XZ);
		return orientation.getDirection();
	}
	
	
	
	public Vector3f getVertical()
	{
		if (orientation == null)
			orientation = new Orientation(XZ);
		return orientation.getVertical();
	}
	
	public static Orientation getOptimumOrientation(Vector3f direction)
	{
		float XZcomp = Math.abs(direction.dot(XZ.getDirection()));
		float XYcomp =  Math.abs(direction.dot(XY.getDirection()));
		float YZcomp = Math.abs( direction.dot(YZ.getDirection()));
		if (XZcomp >= XYcomp)
		{
			if (XZcomp>=YZcomp)
				return XZ;
			else
				return YZ;
		}else
		{
			if (XYcomp>=YZcomp)
				return XY;
			else
				return YZ;
		}
			
		
	/*	if (testOrientation(XZ, direction))	
			return XZ;
		else if (testOrientation(XY, direction))	
			return XY;
		else
			return YZ;
			*/	
	}
	
	public static boolean testOrientation(Orientation orientation, Vector3f direction)
	{
		
		return( Math.abs(direction.x) >= (orientation.getDirection().x * MIN_ANGLE) &&
		Math.abs(direction.y) >= (orientation.getDirection().y * MIN_ANGLE) &&
		Math.abs(direction.z) >= (orientation.getDirection().z * MIN_ANGLE));
	
	}
	
	public boolean updateOrientation() 
	{

		Orientation tempOrientation = getOptimumOrientation (CameraTool.getCameraDirection());
		boolean result = !tempOrientation.equals(this.orientation);
		this.orientation = tempOrientation;
		return result;
	}


	public boolean updateOrientation(Orientation orientation) {
		Orientation tempOrientation = orientation;// getOptimumOrientation (orientation.getDirection());
		boolean result = !tempOrientation.equals(this.orientation);
		this.orientation = tempOrientation;
		return result;
	}

	
	public boolean updateOrientation(Vector3f direction, Vector3f horizontal) {
		Orientation tempOrientation = new Orientation(direction,horizontal);//getOptimumOrientation (direction);
		boolean result = !tempOrientation.equals(orientation);
		orientation = tempOrientation;
		return result;
	}

	/**
	 * If this is true, then tools should use the axis vector if available, and a plane will be formed with it from the current camera angle.
	 * @return
	 */
	public boolean useAxis() {
		return useAxis;
	}

	public void setUseAxis(boolean useAxis) {
		this.useAxis = useAxis;
	}

	public boolean use2DCameraPlane()
	{
		return false;
	}
	
	/**
	 * Override this to return the origin if use2DCameraPlane is set.
	 * @return
	 */
	public Vector3f getOrigin()
	{
		return Vector3f.ZERO;
	}


}
