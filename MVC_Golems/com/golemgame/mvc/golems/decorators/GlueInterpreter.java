package com.golemgame.mvc.golems.decorators;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GolemsClassRepository;

public class GlueInterpreter extends PhysicalDecoratorInterpreter{

	public GlueInterpreter() {
		this(new PropertyStore());
	}

	public GlueInterpreter(PropertyStore store) {
		super(store);
		getStore().setClassName(GolemsClassRepository.GLUE_DECORATOR_CLASS);
	}


}
