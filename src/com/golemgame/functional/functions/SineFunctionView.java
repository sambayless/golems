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
