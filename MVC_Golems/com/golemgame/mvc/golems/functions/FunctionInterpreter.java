package com.golemgame.mvc.golems.functions;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GolemsClassRepository;
import com.golemgame.mvc.golems.StoreInterpreter;


public class FunctionInterpreter extends StoreInterpreter{
	

	
	public FunctionInterpreter() {
		this(new PropertyStore());
	}

	public FunctionInterpreter(PropertyStore store) {
		super(store);
		super.getStore().setClassName(GolemsClassRepository.FUNCTION_CLASS);
	}


	


}
