package com.golemgame.mvc.golems;

import com.golemgame.mvc.PropertyStore;

public class GhostConeInterpreter extends GhostCylinderInterpreter {
	public GhostConeInterpreter(PropertyStore store) {
		super(store);
		store.setClassName(GolemsClassRepository.GHOST_CONE_CLASS);
	}

	public GhostConeInterpreter() {
		this(new PropertyStore());		
	}

}
