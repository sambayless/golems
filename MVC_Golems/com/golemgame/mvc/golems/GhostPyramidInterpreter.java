package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.jme.math.Vector3f;

public class GhostPyramidInterpreter extends GhostInterpreter {

	public final static String PYRAMID_SCALE = "extent";

	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(PYRAMID_SCALE);
	
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(PYRAMID_SCALE))
			return defaultVector3;

		return super.getDefaultValue(key);
	}
	
	public GhostPyramidInterpreter(PropertyStore store) {
		super(store);
		store.setClassName(GolemsClassRepository.GHOST_PYRAMID_CLASS);
	}

	public GhostPyramidInterpreter() {
		this(new PropertyStore());		
	}
	
	public void setPyramidScale(Vector3f scale)
	{
		getStore().setProperty(PYRAMID_SCALE, scale);
	}
	
	public Vector3f getPyramidScale()
	{
		return getStore().getVector3f(PYRAMID_SCALE,new Vector3f(1,1,1));
	}
}
