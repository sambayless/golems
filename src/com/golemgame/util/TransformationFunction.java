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

import java.io.Serializable;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;



public class TransformationFunction implements UnivariateRealFunction, KnottedFunction,Serializable {
	static final long serialVersionUID =1;
	private UnivariateRealFunction function;
	private double translateX = 0;
	private double translateY = 0;
	private double scaleX = 1;
	private double scaleY = 1;

	public TransformationFunction(UnivariateRealFunction toChain) {

			this.function = toChain;

	}

	
	public TransformationFunction() {
		super();
	}


	public double value(double x) throws FunctionEvaluationException
	{
		
			return transformValue(x,function);
	
	}
	
	protected double transformValue(double x, UnivariateRealFunction function) throws FunctionEvaluationException
	{
		double tempY = scaleY;
		if(tempY == 0)//avoid division by zero.
			tempY = Double.MIN_VALUE;
		return (function.value((x-translateX)*scaleX)/scaleY+translateY);
	}

	public double getTranslateX() {
		return translateX;
	}

	public void setTranslateX(double translateX) {
		this.translateX = translateX;
	}

	public double getTranslateY() {
		return translateY;
	}

	public void setTranslateY(double translateY) {
		this.translateY = translateY;
	}

	public double getScaleX() {
		return scaleX;
	}

	public void setScaleX(double scaleX) {
		this.scaleX = scaleX;
	}

	public double getScaleY() {
		return scaleY;
	}

	public void setScaleY(double scaleY) {
		this.scaleY = scaleY;
	}

	public void resetTransform()
	{
		translateX = 0;
		translateY = 0;
		scaleX =1;
		scaleY = 1;
	}

	public void setTranslate(double translateX, double translateY)
	{
		this.translateX = translateX;
		this.translateY = translateY;
	}
	public void setScale(double scaleX, double scaleY)
	{
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}
	
	public void setTransform(double translateX, double translateY, double scaleX, double scaleY)
	{
		this.translateX = translateX;
		this.scaleX = scaleX;
		this.translateY = translateY;
		this.scaleY = scaleY;
	}

	public void setFunction(UnivariateRealFunction function)
	{
		this.function = function;
	}
	
	public double invertY(double y)
	{
		return (y - translateY)*scaleY;
	}
	
	public double invertX(double x)
	{
		return (x - translateX)*scaleX;
	}
	
	public double transformX(double x)
	{
		return (x/scaleX)+ translateX;
	}
	
	public double transformY(double y)
	{
		return (y/scaleY)+ translateY;
	}

	public UnivariateRealFunction getFunction() {
		return function;
	}

	
	public double[] getXKnots() 
	{
		if (this.getFunction() instanceof KnottedFunction)
		{
			KnottedFunction knotted = ((KnottedFunction) this.getFunction());
			if (knotted.isKnotted())
			{
				return knotted.getXKnots();				
			}			
		}
		return null;
	}

	public int count() {
		if (this.getFunction() instanceof KnottedFunction)
		{
			KnottedFunction knotted = ((KnottedFunction) this.getFunction());
			if (knotted.isKnotted())
			{
				return knotted.count();				
			}			
		}
		return 0;
	}


	public double[] getYKnots() 
	{
		if (this.getFunction() instanceof KnottedFunction)
		{
			KnottedFunction knotted = ((KnottedFunction) this.getFunction());
			if (knotted.isKnotted())
			{
				return knotted.getYKnots();				
			}			
		}
		return null;
	}
	
	public boolean isKnotted() {
		if (this.getFunction() instanceof KnottedFunction)
		{
			return ((KnottedFunction) this.getFunction()).isKnotted();
		}else
			return false;		
	}


	
	
}
