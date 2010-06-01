package com.golemgame.properties;

import com.golemgame.mvc.PropertyStore;


public class Property {
	//properties are pairs of strings and property stores
	//they can be handed to a property window factory,
	//which will create appropriate tabs so that all properties can be handled
	//and which will be able to handle an arbitrary number of properties of each type.
	
	public static enum PropertyType
	{
		
		FUNCTION_TIMED("Timed Function"),OSCILLOSCOPE("Oscilloscope"),
		FUNCTION_APPLIED("Applied Function"),BATTERY("Battery"),MODIFIER("Function"),
		PHYSICAL("Physical"),MATERIAL("Material"),POSITION("Position"),APPEARANCE("Appearance"),ROCKET("Rocket"),
		PARTICLE_EFFECTS("Rocket Effects"), ROCKET_EFFECTS("Rocket Effects"),MOTOR("Motor"),
		HYDRAULICS("Hydraulics"),GEAR("Gear"),
		CAMERA("Camera"), SCALE("Scale"),SCALE_BOX("Box Scale"),SCALE_SPHERE("Sphere Scale"),
		SCALE_ELLIPSOID("Ellipsoid Scale"),	SCALE_PYRAMID("Pyramid Scale"),	SCALE_CYLINDER("Cylinder Scale"),
		SCALE_TUBE("Tube Scale"),SCALE_GEAR("Gear Scale"),SCALE_CAPSULE("Capsule Scale"),SCALE_CONE("Cone Scale"),
		SCALE_HINGE("Hinge Scale"),SCALE_AXLE("Axle Scale"),SCALE_HYDRAULICS("Hydraulics Scale"),SCALE_BALL_SOCKET("Ball And Socket Scale"),
		PID("PID"),SOUND("Sound"),LINE_EFFECTS("Line Effects"), BEAM_EFFECTS("Beam Effects"),GRAPPLE("Grapple"), INPUT("Input"),
		INPUT_DEVICE("Input Device Type"), OUTPUT_DEVICE("Output Device Type");
		;
		
		private String name;
		private PropertyType(String name)
		{
			this.name = name;
		}
		@Override
		public String toString() {
			return name;
		}
		
	}
	
	private final PropertyType propertyType;
	private final PropertyStore propertyStore;
	public Property(PropertyType propertyType, PropertyStore propertyStore) {
		super();
		this.propertyStore = propertyStore;
		this.propertyType = propertyType;
	}
	public PropertyStore getPropertyStore() {
		return propertyStore;
	}

	public PropertyType getPropertyType() {
		return propertyType;
	}
	@Override
	public String toString() {
		return propertyType.toString() + "[" + propertyStore.toString() + "]";
	}
	
}
