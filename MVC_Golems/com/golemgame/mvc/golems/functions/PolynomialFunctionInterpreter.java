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

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.DoubleType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GolemsClassRepository;

public class PolynomialFunctionInterpreter extends FunctionInterpreter {
	public final static String COEFFICIENTS = "coefficients";

	public PolynomialFunctionInterpreter() {
		this(new PropertyStore());
		
	}

	public PolynomialFunctionInterpreter(PropertyStore store) {
		super(store);
		super.getStore().setClassName(GolemsClassRepository.POLYNOMIAL_FUNCTION_CLASS);
	}
	
	
	public CollectionType getCoefficients()
	{
		return super.getStore().getCollectionType(COEFFICIENTS);
	}
	
	public void setCoefficient(int pos, DataType value)
	{
		getCoefficients().setElement(value,pos);
	}
	
	public double getCoefficient(int pos)
	{
		 DataType value = getCoefficients().getElement(pos);
		 if (value == null || value.getType()!= DataType.Type.DOUBLE)
		 {
			 return 0;
		 }else
			 return ((DoubleType)value).getValue();
	}
	

	
	
}
