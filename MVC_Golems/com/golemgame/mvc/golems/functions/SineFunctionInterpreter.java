package com.golemgame.mvc.golems.functions;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GolemsClassRepository;

public class SineFunctionInterpreter extends FunctionInterpreter {
	public static final String LAMBDA = "lambda";
	public static final String PHASE = "phase";
	public static final String AMPLITUDE = "amplitude";
	public SineFunctionInterpreter() {
		this(new PropertyStore());
	}
	public SineFunctionInterpreter(PropertyStore store) {
		super(store);
		super.getStore().setClassName(GolemsClassRepository.SINE_FUNCTION_CLASS);
		
	}
	
	public double getLambda()
	{
		return getStore().getDouble(LAMBDA);
	}
	
	public void setLambda(double lambda)
	{
		getStore().setProperty(LAMBDA, lambda);
	}
	
	public double getAmplitude()
	{
		return getStore().getDouble(AMPLITUDE);
	}
	
	public void setAmplitude(double amplitude)
	{
		getStore().setProperty(AMPLITUDE, amplitude);
	}
	
	public double getPhase()
	{
		return getStore().getDouble(PHASE);
	}
	
	public void setPhase(double phase)
	{
		getStore().setProperty(PHASE, phase);
	}

	
}
