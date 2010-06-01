package com.golemgame.functional.functions;

import org.apache.commons.math.analysis.UnivariateRealFunction;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.functions.FunctionFactory;
import com.golemgame.mvc.golems.functions.FunctionInterpreter;

public abstract class SustainedFunctionView implements SustainedView {

	private FunctionInterpreter interpreter;
	
	public void remove() {

	}
	
	
	public SustainedFunctionView(PropertyStore store) {
		super();
		interpreter = FunctionFactory.createFunctionInterpreter(store);
		interpreter.getStore().setSustainedView(this);
	}

	public void invertView(PropertyStore store) {
		store.set(interpreter.getStore());
		//FunctionFactory.invertView(store, function);
	}

	
	public abstract UnivariateRealFunction getFunction();

	public void refresh() {
		
		
	}

	public PropertyStore getStore() {
		return interpreter.getStore();
	}



}
