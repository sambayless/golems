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

import org.apache.commons.math.analysis.PolynomialFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.DoubleType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.functions.PolynomialFunctionInterpreter;

public class PolynomialFunctionView extends SustainedFunctionView {

	private final PolynomialFunctionInterpreter interpreter;
	public PolynomialFunctionView(PropertyStore store) {
		super(store);
		interpreter = new PolynomialFunctionInterpreter(store);
	}
	
	@Override
	public UnivariateRealFunction getFunction() {
		
		CollectionType coefficients = interpreter.getCoefficients();
		int i =0;
		double[] co = new double[coefficients.getValues().size()];
		for (DataType data:coefficients.getValues())
		{
			if (data.getType() == DataType.Type.DOUBLE)
			{
				co[i] = ((DoubleType)data).getValue();
			}
			i++;
		}
		if (co.length <= 0)
			co = new double[]{0};
		return new PolynomialFunction(co);
	}
	

}
