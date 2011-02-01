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
package com.golemgame.util;

import org.apache.commons.math.analysis.PolynomialFunction;

public class LinearUtils {
	public static PolynomialFunction generateTranslatedFunction(double[] coefficients, double translation)
	{
	
		if(coefficients.length == 1 || translation == 0)
		{
			return new PolynomialFunction(coefficients);
		}
		translation = -translation;
		for (int power = 1; power<coefficients.length; power++)
		{
			if (coefficients[power]!=0)
			{
				double[] expandedCoefficients = getBinomialTerms(power,1,translation);
				for (int i = 0; i<power;i++)
				{
					expandedCoefficients[i] *=coefficients[power]; 
					coefficients[i] += expandedCoefficients[i] * Math.pow(translation, power-i);
				}
			}
		}
		
		return new PolynomialFunction(coefficients);
	}
	private static double[] getBinomialTerms(int power, double x, double y)
	{
		double [] coefficients = new double[power+1];
		
		//terms are in order of power: x going from largest power to 0, y going from 0 to largest.
		
		coefficients[0] = 1;

		
		for (int i = 1; i<=power; i++)
		{
			double last = coefficients[i-1];

			
			coefficients[i] = last * (power-i+1)/i;

			
		}
		
		return coefficients;
	}

}
