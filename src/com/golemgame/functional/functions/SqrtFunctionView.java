package com.golemgame.functional.functions;

import org.apache.commons.math.analysis.UnivariateRealFunction;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.functions.SqrtFunctionInterpreter;
import com.golemgame.util.SqrtFunction;

public class SqrtFunctionView extends SustainedFunctionView {

	private final SqrtFunctionInterpreter interpreter;
	public SqrtFunctionView(PropertyStore store) {
		super(store);
		interpreter = new SqrtFunctionInterpreter(store);
	}

	@Override
	public UnivariateRealFunction getFunction() {
		return new SqrtFunction();
		//return new SinFunction(interpreter.getAmplitude(), interpreter.getLambda(), interpreter.getPhase());
	}

}
