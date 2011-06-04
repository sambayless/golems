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

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GolemsClassRepository;

public class FunctionViewFactory {

	public static SustainedFunctionView createFunctionView(PropertyStore store)
	{
		String className = store.getClassName();
		if (className== null)
			className = "";
		if (className.equalsIgnoreCase(GolemsClassRepository.SINE_FUNCTION_CLASS))
		{
			return new SineFunctionView(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.POLYNOMIAL_FUNCTION_CLASS))
		{
			return new PolynomialFunctionView(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.KNOTTED_FUNCTION_CLASS))
		{
			return new KnottedFunctionView(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.COMBINED_FUNCTION_CLASS))
		{
			return new CombinedFunctionView(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.SQRT_FUNCTION_CLASS))
		{
			return new SqrtFunctionView(store);
		}else
		{
			return new PolynomialFunctionView(store);
		}
		
		
		//throw new IllegalArgumentException("Component not recognized: " + store.toString());
		
	}
	

	
}
