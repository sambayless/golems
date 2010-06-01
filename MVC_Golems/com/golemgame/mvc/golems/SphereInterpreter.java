package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.jme.math.Vector3f;

public class SphereInterpreter extends PhysicalStructureInterpreter {
	
	public static final String ELLIPSOID = "ellipsoid";
	public static final String RADII = "extent";
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(ELLIPSOID);
		keys.add(RADII);	
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(ELLIPSOID))
			return defaultBool;
		if(key.equals(RADII))
			return defaultVector3;
		return super.getDefaultValue(key);
	}
	
	public SphereInterpreter(PropertyStore store) {
		super(store);
		store.setClassName(GolemsClassRepository.SPHERE_CLASS);
	}

	public SphereInterpreter() {
		this(new PropertyStore());		
	}

	public void setEllispoid(boolean ellipsoid)
	{
		getStore().setProperty(ELLIPSOID, ellipsoid);
	}
	
	public boolean isEllipsoid()
	{
		return getStore().getBoolean(ELLIPSOID);
	}
	
	public void setRadius(float radius)
	{
		getStore().getVector3f(RADII, new Vector3f(radius,radius,radius)).set(radius,radius,radius);
	}
	
	public void setExtent(Vector3f extent)
	{
		getStore().setProperty(RADII,extent);
	}
	
	public float getRadius()
	{
		return getStore().getVector3f(RADII, new Vector3f(0.5f,0.5f,0.5f)).x;
	}
	
	public Vector3f getExtent()
	{
		return getStore().getVector3f(RADII, new Vector3f(0.5f,0.5f,0.5f));
	}
	
}
