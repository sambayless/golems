package com.golemgame.mvc.golems.decorators;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.StoreInterpreter;

public class PhysicalDecoratorInterpreter extends StoreInterpreter {

	public PhysicalDecoratorInterpreter() {
		this(new PropertyStore());
	}

	public PhysicalDecoratorInterpreter(PropertyStore store) {
		super(store);
		// TODO Auto-generated constructor stub
	}

}
