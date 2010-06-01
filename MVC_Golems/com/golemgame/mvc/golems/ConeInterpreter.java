package com.golemgame.mvc.golems;

import com.golemgame.mvc.PropertyStore;

public class ConeInterpreter extends CylinderInterpreter {
	public ConeInterpreter(PropertyStore store) {
		super(store);
		store.setClassName(GolemsClassRepository.CONE_CLASS);
	}

	public ConeInterpreter() {
		this(new PropertyStore());		
	}

	
}
