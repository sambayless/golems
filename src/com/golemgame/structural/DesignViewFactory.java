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
package com.golemgame.structural;

import com.golemgame.functional.Wire;
import com.golemgame.mechanical.MachineSpace;
import com.golemgame.mechanical.MachineSpaceSettings;
import com.golemgame.mechanical.StructuralMachine;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.AxleInterpreter;
import com.golemgame.mvc.golems.GolemsClassRepository;
import com.golemgame.mvc.golems.HingeInterpreter;
import com.golemgame.mvc.golems.HydraulicInterpreter;
import com.golemgame.mvc.golems.MachineInterpreter;
import com.golemgame.mvc.golems.MachineSpaceInterpreter;
import com.golemgame.mvc.golems.WireInterpreter;
import com.golemgame.structural.group.GroupManager;
import com.golemgame.structural.group.StructureGroup;
import com.golemgame.structural.structures.AxleStructure;
import com.golemgame.structural.structures.BallAndSocketStructure;
import com.golemgame.structural.structures.BatteryStruct;
import com.golemgame.structural.structures.BoxStructure;
import com.golemgame.structural.structures.CameraStructure;
import com.golemgame.structural.structures.CapsuleStructure;
import com.golemgame.structural.structures.ConeStructure;
import com.golemgame.structural.structures.ContactStructure;
import com.golemgame.structural.structures.CylinderStructure;
import com.golemgame.structural.structures.DistanceSensor;
import com.golemgame.structural.structures.GearStructure;
import com.golemgame.structural.structures.GrappleStructure;
import com.golemgame.structural.structures.HingeStructure;
import com.golemgame.structural.structures.HydraulicStructure;
import com.golemgame.structural.structures.InputStructure;
import com.golemgame.structural.structures.ModifierStruct;
import com.golemgame.structural.structures.MotorPropertiesImpl;
import com.golemgame.structural.structures.MultimeterStruct;
import com.golemgame.structural.structures.OscilloscopeStructure;
import com.golemgame.structural.structures.PyramidStructure;
import com.golemgame.structural.structures.RackGearStructure;
import com.golemgame.structural.structures.RocketStructure;
import com.golemgame.structural.structures.SphereStructure;
import com.golemgame.structural.structures.TouchSensor;
import com.golemgame.structural.structures.TubeStructure;
import com.golemgame.structural.structures.decorators.Glue;
import com.golemgame.structural.structures.ghost.GhostBox;
import com.golemgame.structural.structures.ghost.GhostCone;
import com.golemgame.structural.structures.ghost.GhostCylinder;
import com.golemgame.structural.structures.ghost.GhostPyramid;
import com.golemgame.structural.structures.ghost.GhostSphere;

public class DesignViewFactory {
	
	public static SustainedView constructView(PropertyStore store)
	{
		String className = store.getClassName();
		

		return constructView(className,store);
	}
	
	public static SustainedView constructView(String className)
	{
		return constructView(className, new PropertyStore());
	}
	
	public static SustainedView constructView(String className,PropertyStore store)
	{
		if (className.equalsIgnoreCase(GolemsClassRepository.BOX_CLASS))
		{
			return new BoxStructure(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.CYL_CLASS))
		{
			return new CylinderStructure((store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.SPHERE_CLASS))
		{
			return new SphereStructure((store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.CONE_CLASS))
		{
			return new ConeStructure((store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.PYRAMID_CLASS))
		{
			return new PyramidStructure((store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.CAP_CLASS))
		{
			return new CapsuleStructure((store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.ROCKET_CLASS))
		{
			return new RocketStructure((store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.HINGE_CLASS))
		{
			return new HingeStructure(new HingeInterpreter(store));
		}/*else if (className.equalsIgnoreCase(GolemsClassRepository.BALL_SOCKET_CLASS_OLD))
		{
			return new OldBallAndSocketStructure(store);
		}*/else if (className.equalsIgnoreCase(GolemsClassRepository.BALL_SOCKET_CLASS))
		{
			return new BallAndSocketStructure(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.AXLE_CLASS))
		{
			return new AxleStructure(new AxleInterpreter(store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.HYDRAULIC_CLASS))
		{
			return new HydraulicStructure(new HydraulicInterpreter(store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.MACHINE_CLASS))
		{
			return new StructuralMachine(new MachineInterpreter(store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.MACHINE_SPACE_CLASS))
		{
			return new MachineSpace(new MachineSpaceInterpreter(store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.CAMERA_CLASS))
		{
			return new CameraStructure(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.WIRE_CLASS))
		{
			return new Wire(new WireInterpreter(store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.GEAR_CLASS))
		{
			return new GearStructure((store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.TUBE_CLASS))
		{
			return new TubeStructure(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.INPUT_CLASS))
		{
			return new InputStructure(store);
		}/*else if (className.equalsIgnoreCase(WIRE_STORE_CLASS))
		{
			return new Wires(new WireStoreInterpreter(store));
		}*//*else if (className.equalsIgnoreCase(WIRE_PORT_CLASS))
		{
			return new WirePort(store);
		}*/else if (className.equalsIgnoreCase(GolemsClassRepository.DISTANCE_SENSOR_CLASS))
		{
			return new DistanceSensor((store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.GENERAL_SENSOR_CLASS))
		{
			return new MultimeterStruct(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.OSCILLOSCOPE_CLASS))
		{
			return new OscilloscopeStructure(store);
		}
		else if (className.equalsIgnoreCase(GolemsClassRepository.TOUCH_SENSOR_CLASS))
		{
			return new TouchSensor(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.BATTERY_CLASS))
		{
			return new BatteryStruct((store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.MODIFIER_CLASS))
		{
			return new ModifierStruct((store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.GHOST_BOX_CLASS))
		{
			return new GhostBox((store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.GHOST_CONE_CLASS))
		{
			return new GhostCone((store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.GHOST_CYL_CLASS))
		{
			return new GhostCylinder( (store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.GHOST_PYRAMID_CLASS))
		{
			return new GhostPyramid( (store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.GHOST_SPHERE_CLASS))
		{
			return new GhostSphere( (store));
		}else if (className.equalsIgnoreCase(GolemsClassRepository.MOTOR_PROPERTIES_CLASS))
		{
			return new MotorPropertiesImpl(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.GLUE_DECORATOR_CLASS))
		{
			return new Glue(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.GROUP_CLASS))
		{
			return new StructureGroup(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.GROUP_MANAGER_CLASS))
		{
			return new GroupManager(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.MACHINE_SPACE_SETTINGS_CLASS))
		{
			return new MachineSpaceSettings(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.GRAPPLE_CLASS))
		{
			return new GrappleStructure(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.CONTACT_CLASS))
		{
			return new ContactStructure(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.RACK_GEAR_CLASS))
		{
			return new RackGearStructure(store);
		}
		
		
		
		
		
		
		throw new IllegalArgumentException("Component not recognized: " + store.toString());
	}


	
	
	
	public static MachineSpace constructMachineSpace() {
		return new MachineSpace(new MachineSpaceInterpreter(new PropertyStore()));
	}
}
