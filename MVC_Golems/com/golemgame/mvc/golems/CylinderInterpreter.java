package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;


public class CylinderInterpreter extends PhysicalStructureInterpreter {

	public final static String CYL_RADIUS = "radius";
	public final static String CYL_HEIGHT = "height";
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(CYL_RADIUS);
		keys.add(CYL_HEIGHT);
	
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(CYL_RADIUS))
			return defaultFloat;
		if(key.equals(CYL_HEIGHT))
			return defaultFloat;	

		return super.getDefaultValue(key);
	}
	
	public CylinderInterpreter() {
		this(new PropertyStore());		
	}

	public CylinderInterpreter(PropertyStore store) {
		super(store);
		store.setClassName(GolemsClassRepository.CYL_CLASS);
	}

	public void setRadius(float radius)
	{
		this.getStore().setProperty(CYL_RADIUS, radius);
	
	}
	
	public void setHeight(float height)
	{
		this.getStore().setProperty(CYL_HEIGHT, height);
	}
	
	public float getRadius()
	{
		return this.getStore().getFloat(CYL_RADIUS,0.5f);
	}
	
	public float getHeight()
	{
		return this.getStore().getFloat(CYL_HEIGHT,1f);
	}
	
	
}
