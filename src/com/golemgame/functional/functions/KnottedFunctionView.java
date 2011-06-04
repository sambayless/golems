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

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.DoubleType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.functions.KnottedFunctionInterpreter;
import com.golemgame.util.LinearInterpolator;

public class KnottedFunctionView  extends SustainedFunctionView {

	private final KnottedFunctionInterpreter interpreter;
	public KnottedFunctionView(PropertyStore store) {
		super(store);
		interpreter = new KnottedFunctionInterpreter(store);
	}

	@Override
	public UnivariateRealFunction getFunction() {
		CollectionType xKnots = interpreter.getXKnots();
		CollectionType yKnots = interpreter.getYKnots();
		int size = Math.min(xKnots.getValues().size(),yKnots.getValues().size());
		double[] xs = new double[size];
		double[] ys = new double[size];
		
		for (int i = 0; i < size;i++)
		{
			DataType x = xKnots.getElement(i);
			DataType y = yKnots.getElement(i);
			
			if(y.getType() == DataType.Type.DOUBLE && x.getType() == DataType.Type.DOUBLE)
			{
				xs[i] = ((DoubleType)x).getValue();
				ys[i] = ((DoubleType)y).getValue();
				
			}else
				xs[i] = Double.NEGATIVE_INFINITY;
		}
		return LinearInterpolator.getInstance().interpolate(xs, ys);
	}
	

}
