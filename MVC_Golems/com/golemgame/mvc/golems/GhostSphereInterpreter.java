package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.jme.math.Vector3f;

public class GhostSphereInterpreter extends GhostInterpreter {
	public static final String ELLIPSOID = "ellipsoid";
	public static final String RADII = "extent";
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(RADII);
		keys.add(ELLIPSOID);
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(RADII))
			return defaultVector3;
		if(key.equals(ELLIPSOID))
			return defaultBool;

		return super.getDefaultValue(key);
	}
	
	public GhostSphereInterpreter(PropertyStore store) {
		super(store);
		store.setClassName(GolemsClassRepository.GHOST_SPHERE_CLASS);
	}

	public GhostSphereInterpreter() {
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
