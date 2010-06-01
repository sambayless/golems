package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.jme.math.Vector3f;

public class MaterialPropertiesInterpreter extends StoreInterpreter {
	public static final String MATERIAL = "material";
	public static final String DENSITY = "density";
	public static final String FRICTION = "friction";
	public static final String SPRING_PENETRATION = "springPenetrationDepth";
	public static final String SURFACE_MOTION = "surfaceMotion";
	public static final String MATERIAL_NAME = "material.name";
	
	public static final float MAX_DENSITY = 100f;
	public static final float MIN_DENSITY = 0.00001f;
	
	public static enum MaterialClass
	{
		DEFAULT("Default"),IRON("Iron"),WOOD("Wood"),CONCRETE("Concrete"),GRANITE("Granite"),GLASS("Glass"),PLASTIC("Plastic"),RUBBER("Rubber"),ICE("Ice"),SPONGE("Sponge"),HELIUM_BALLOON("Helium");
		private final String name;
		
		private MaterialClass(String name) {
			this.name = name;
		}

		public String getName()
		{
			return name;
		}

		@Override
		public String toString() {
			return name;
		}
		
		
	}
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
	
		keys.add(MATERIAL);
		keys.add(DENSITY);
		keys.add(FRICTION);
		
		keys.add(SPRING_PENETRATION);
		keys.add(SURFACE_MOTION);
		keys.add(MATERIAL_NAME);
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(MATERIAL))
			return defaultEnum;
		if(key.equals(DENSITY))
			return defaultFloat;
		if(key.equals(FRICTION))
			return defaultFloat;
		if(key.equals(SPRING_PENETRATION))
			return defaultFloat;
		if(key.equals(SURFACE_MOTION))
			return defaultVector3;
		if(key.equals(MATERIAL_NAME))
			return defaultString;

		return super.getDefaultValue(key);
	}
	
	public MaterialPropertiesInterpreter() {
		this(new PropertyStore());
	}
	public MaterialPropertiesInterpreter(PropertyStore store) {
		super(store);
		super.getStore().setClassName(GolemsClassRepository.MATERIAL_PROPERTIES_CLASS);
	}
	
	public float getDensity()
	{
		return getStore().getFloat(DENSITY,1f);
	}
	public void setDensity(float density)
	{//turn these into requirements...

		getStore().setProperty(DENSITY,density);
	}
	
	public MaterialClass getMaterial()
	{
		return getStore().getEnum(MATERIAL, MaterialClass.DEFAULT);
	}
	
	public void setMaterial(MaterialClass material)
	{
		getStore().setProperty(MATERIAL, material);
	}


	public float getSpringPenetration()
	{
		return getStore().getFloat(SPRING_PENETRATION,1f);
	}
	public void setSpringPenetration(float density)
	{
		getStore().setProperty(SPRING_PENETRATION,density);
	}
	
	public float getFriction()
	{
		return getStore().getFloat(FRICTION,1f);
	}
	public void setFriction(float density)
	{
		getStore().setProperty(FRICTION,density);
	}
	
	public Vector3f getSurfaceMotion()
	{
		return getStore().getVector3f(SURFACE_MOTION);
	}
	public void setSurfaceMotion(Vector3f density)
	{
		getStore().setProperty(SURFACE_MOTION,density);
	}

	public String getName()
	{
		return getStore().getString(MATERIAL_NAME,getMaterial().getName());
	}
	public void setName(String name)
	{
		getStore().setProperty(MATERIAL_NAME,name);
	}

	
	
}
