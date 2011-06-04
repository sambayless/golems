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

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;

import com.golemgame.util.LinearInterpolator.LinearInterpolationFunction;



public class CombinedTransformationFunction extends TransformationFunction {
	static final long serialVersionUID =1;
	
	private UnivariateRealFunction baseFunction;
	private LinearInterpolationFunction knottedFunction;

	private double left = -1;
	private double right = 1;
	private double bottom = -1;
	private double top = 1;
	
	public UnivariateRealFunction getBaseFunction() {
		return baseFunction;
	}

	public void setBaseFunction(UnivariateRealFunction baseFunction) {
		this.baseFunction = baseFunction;
	}

	public LinearInterpolationFunction getKnottedFunction() {
		return knottedFunction;
	}

	public void setKnottedFunction(LinearInterpolationFunction knottedFunction) {
		this.knottedFunction = knottedFunction;
	}

	public CombinedTransformationFunction(LinearInterpolationFunction knottedFunction, UnivariateRealFunction baseFunction, float left, float right, float top, float bottom) {
		super(knottedFunction);
		this.knottedFunction = knottedFunction;
		this.baseFunction = baseFunction;
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}

	public CombinedTransformationFunction()
	{
		super();
	}
	
	@Override
	public double value(double x) throws FunctionEvaluationException {
	
		if (x <getLeft())
			x = getLeft();
		else if (x>getRight())
			x = getRight();
		
		double val ;
		

/*		if(! ( knottedFunction.isEmpty()  || knottedFunction.getXKnots().length<2))
		{
		boolean knot2 = invertX(x) < knottedFunction.getXKnots()[0];
		boolean knot3 = invertX(x) > knottedFunction.getXKnots()[knottedFunction.getXKnots().length-1];
		System.out.println(knot2 + "\t" + knot3);
		}
		*/
		if (knottedFunction.isEmpty() || knottedFunction.getXKnots().length<2 || invertX(x) < knottedFunction.getXKnots()[0] || invertX(x) > knottedFunction.getXKnots()[knottedFunction.getXKnots().length-1])
		{
			val= super.transformValue(x, baseFunction);
		}else
			val= super.transformValue(x, knottedFunction);
		

		if (val> getTop())
			val = getTop();
		else if (val < getBottom())
			val = getBottom();
		
		return val;
		

	}

	public double getLeft()
	{
		return left;
	}
	public double getRight()
	{
		return right;
	}
	public double getTop(){
		return top;
	}
	public double getBottom(){
		return bottom;
	}

	public void setLeft(double left) {
		this.left = left;
	}

	public void setRight(double right) {
		this.right = right;
	}

	public void setBottom(double bottom) {
		this.bottom = bottom;
	}

	public void setTop(double top) {
		this.top = top;
	}

	
	
	


}
