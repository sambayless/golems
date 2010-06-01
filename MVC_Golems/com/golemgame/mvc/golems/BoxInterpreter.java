package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.jme.math.Vector3f;

public class BoxInterpreter extends PhysicalStructureInterpreter {
	
	
	public final static String BOX_EXTENT = "extent";
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {

		keys.add(BOX_EXTENT);	
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(BOX_EXTENT))
			return defaultVector3;	

		return super.getDefaultValue(key);
	}
	
	public BoxInterpreter(PropertyStore store) {
		super(store);
		store.setClassName(GolemsClassRepository.BOX_CLASS);
	}
	
	public BoxInterpreter() {
		this(new PropertyStore());		
	}

	public void setExtent(Vector3f extent)
	{
		super.getStore().setProperty(BOX_EXTENT,extent);
	}
	
	public Vector3f getExtent()
	{
		return super.getStore().getVector3f(BOX_EXTENT, new Vector3f(0.5f,0.5f,0.5f));
	}
}
