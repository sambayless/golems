package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.jme.math.Vector3f;

public class PyramidInterpreter extends PhysicalStructureInterpreter {

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
	
	public PyramidInterpreter(PropertyStore store) {
		super(store);
		store.setClassName(GolemsClassRepository.PYRAMID_CLASS);
	}

	public PyramidInterpreter() {
		this(new PropertyStore());		
	}
	
	public void setExtent(Vector3f scale)
	{
		getStore().setProperty(PYRAMID_SCALE, scale);
	}
	
	public Vector3f getPyramidScale()
	{
		return getStore().getVector3f(PYRAMID_SCALE,new Vector3f(1,1,1));
	}
}
