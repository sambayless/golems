package com.golemgame.mvc.golems;

import com.golemgame.mvc.PropertyStore;
import com.jme.math.Vector3f;

public class GhostInterpreter extends StructureInterpreter{
	public GhostInterpreter(PropertyStore store) {
		super(store);
	
	}
	
	public GhostInterpreter() {
		this(new PropertyStore());		
	}

	@Override
	public Vector3f getLocalTranslation() {
		return super.getLocalTranslation(new Vector3f(1,0,0));
	}
	
	
}
