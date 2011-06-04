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

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GolemsClassRepository;

public class OdeViewFactory {

	public static OdePhysicalStructure createStructure(PropertyStore store) {
	
		String className = store.getClassName();
		
		if (className.equalsIgnoreCase(GolemsClassRepository.BOX_CLASS))
		{
			return new OdeBoxStructure(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.CYL_CLASS))
		{
			return new OdeCylinderStructure((store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.SPHERE_CLASS))
		{
			return new OdeSphereStructure((store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.CONE_CLASS))
		{
			return new OdeConeStructure((store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.PYRAMID_CLASS))
		{
			return new OdePyramidStructure((store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.CAP_CLASS))
		{
			return new OdeCapsuleStructure((store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.ROCKET_CLASS))
		{
			return new OdeRocketStructure(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.HINGE_CLASS))
		{
			return new OdeHingeStructure(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.AXLE_CLASS))
		{
			return new OdeAxleStructure(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.HYDRAULIC_CLASS))
		{
			return new OdeHydraulicStructure(store);
		}/*else if (className.equalsIgnoreCase(GolemsClassRepository.BALL_SOCKET_CLASS_OLD))
		{
			return new OdeOldBallAndSocketStructure(store);
		}*/else if (className.equalsIgnoreCase(GolemsClassRepository.GEAR_CLASS))
		{
			return new OdeGearStructure(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.BATTERY_CLASS))
		{
			return new OdeBattery(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.MODIFIER_CLASS))
		{
			return new OdeModifier(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.GENERAL_SENSOR_CLASS))
		{
			return new OdeMultimeter(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.CAMERA_CLASS))
		{
			return new OdeCamera(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.TOUCH_SENSOR_CLASS))
		{
			return new OdeTouchSensor(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.DISTANCE_SENSOR_CLASS))
		{
			return new OdeDistanceSensor(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.TUBE_CLASS))
		{
			return new OdeTubeStructure(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.OSCILLOSCOPE_CLASS))
		{
			return new OdeOscilloscope(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.INPUT_CLASS))
		{
			return new OdeInputStructure(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.BALL_SOCKET_CLASS))
		{
			return new OdeBallAndSocketStructure(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.GRAPPLE_CLASS))
		{
			return new OdeGrappleStructure(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.INPUT_CLASS))
		{
			return new OdeInputStructure(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.CONTACT_CLASS))
		{
			return new OdeContactStructure(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.RACK_GEAR_CLASS))
		{
			return new OdeRackGearStructure(store);
		}
		
		
		
		
		
		
		
		throw new IllegalArgumentException("Ode component not recognized: " + store.toString());

		
	}
	//like the design view factory, this factory provides generic methods for constructing concrete views for the model

	public static OdeGhostStructure constructGhost(PropertyStore store) {
		String className = store.getClassName();
		
		if (className.equalsIgnoreCase(GolemsClassRepository.GHOST_BOX_CLASS))
		{
			return new OdeGhostBox(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.GHOST_CONE_CLASS))
		{
			return new OdeGhostCone(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.GHOST_CYL_CLASS))
		{
			return new OdeGhostCylinder(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.GHOST_PYRAMID_CLASS))
		{
			return new OdeGhostPyramid(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.GHOST_SPHERE_CLASS))
		{
			return new OdeGhostSphere(store);
		}
		
		throw new IllegalArgumentException("Ode component not recognized: " + store.toString());

	}
	
	
}
