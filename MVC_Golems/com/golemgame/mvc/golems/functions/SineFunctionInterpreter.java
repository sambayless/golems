/*******************************************************************************
 * Copyright 2008, 2009, 2010 Sam Bayless.
 * 
 *     This file is part of Golems.
 * 
 *     Golems is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Golems is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Golems. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
