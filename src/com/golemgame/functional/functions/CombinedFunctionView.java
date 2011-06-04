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
import com.golemgame.mvc.golems.functions.CombinedFunctionInterpreter;
import com.golemgame.util.CombinedTransformationFunction;
import com.golemgame.util.LinearInterpolator;
import com.golemgame.util.LinearInterpolator.LinearInterpolationFunction;

public class CombinedFunctionView extends SustainedFunctionView {

	private final CombinedFunctionInterpreter interpreter;
	public CombinedFunctionView(PropertyStore store) {
		super(store);
		interpreter = new CombinedFunctionInterpreter(store);
	}

	@Override
	public UnivariateRealFunction getFunction() {
		CombinedTransformationFunction f = new CombinedTransformationFunction();
		
		f.setScale(interpreter.getLocalScaleX(),interpreter.getLocalScaleY());
		f.setTranslate(interpreter.getTranslateX(), interpreter.getTranslateY());
		f.setBaseFunction(FunctionViewFactory.createFunctionView( interpreter.getFunction1()).getFunction());
		 
		UnivariateRealFunction function2 =FunctionViewFactory.createFunctionView( interpreter.getFunction2()).getFunction();
		if(function2 instanceof LinearInterpolationFunction)//incase its null
			f.setKnottedFunction((LinearInterpolationFunction)function2);
		else
		{
			f.setKnottedFunction(LinearInterpolator.getInstance().interpolate(new double[0], new double[0]));
		}
		f.setLeft(interpreter.getLeft());
		f.setRight(interpreter.getRight());
		f.setBottom(interpreter.getBottom());
		f.setTop(interpreter.getTop());
		
		
		return f;
	}
}
