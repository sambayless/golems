package com.golemgame.mvc.golems;

import com.golemgame.mvc.PropertyStore;

public class CapsuleInterpreter extends CylinderInterpreter {
	

	public CapsuleInterpreter(PropertyStore store) {
		super(store);
		store.setClassName(GolemsClassRepository.CAP_CLASS);
	}
	
	public CapsuleInterpreter() {
		this(new PropertyStore());

	}

	public float getHeight()
	{
		return this.getStore().getFloat(CYL_HEIGHT,1f);
	}
}
