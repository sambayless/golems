package com.golemgame.functional.functions;

import org.apache.commons.math.analysis.UnivariateRealFunction;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.functions.SineFunctionInterpreter;
import com.golemgame.util.SinFunction;

public class SineFunctionView extends SustainedFunctionView {

	private final SineFunctionInterpreter interpreter;
	public SineFunctionView(PropertyStore store) {
		super(store);
		interpreter = new SineFunctionInterpreter(store);
	}

	@Override
	public UnivariateRealFunction getFunction() {
		return new SinFunction(interpreter.getAmplitude(), interpreter.getLambda(), interpreter.getPhase());
	}

}
