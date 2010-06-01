package com.golemgame.mvc.golems.functions;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GolemsClassRepository;

public class SqrtFunctionInterpreter extends FunctionInterpreter {

	public SqrtFunctionInterpreter() {
		this(new PropertyStore());
	}
	public SqrtFunctionInterpreter(PropertyStore store) {
		super(store);
		super.getStore().setClassName(GolemsClassRepository.SQRT_FUNCTION_CLASS);
		
	}
	

	
}
