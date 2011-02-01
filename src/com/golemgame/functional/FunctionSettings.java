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
package com.golemgame.functional;

import java.io.Serializable;

import org.apache.commons.math.analysis.UnivariateRealFunction;

import com.golemgame.functional.functions.FunctionViewFactory;
import com.golemgame.functional.functions.SustainedFunctionView;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.functions.FunctionSettingsInterpreter;
import com.golemgame.mvc.golems.functions.FunctionSettingsInterpreter.FunctionType;



public class FunctionSettings implements Serializable,SustainedView {
	private final static long serialVersionUID = 1;
	private boolean periodic = false;
	private float maxX;
	private float minX;
	private float maxY;
	private float minY;
	private float scaleX = 1;
	private float scaleY = 1;
	
	private FunctionSettingsInterpreter interpreter;
	
	private SustainedFunctionView function = null;
	
	public FunctionSettings(PropertyStore store)
	{
		this.interpreter = new FunctionSettingsInterpreter(store);
		this.interpreter.getStore().setSustainedView(this);
		this.refresh();
	}
	
	
	/**
	 * Restrict a value to between minX and maxX.
	 * @param x
	 * @return
	 */
	public float clampX(float x)
	{
		if (x>getMaxX())
			return getMaxX();
		else if (x<getMinX())
			return getMinX();
		else return x;
	}
	
	/**
	 * Restrict a value to between minY and maxY.
	 * @param y
	 * @return
	 */
	public float clampY(float y)
	{
		if (y>getMaxY())
			return getMaxY();
		else if (y<getMinY())
			return getMinY();
		else return y;
	}
	
	public boolean isPeriodic() {
		return periodic;
	}
	public void setPeriodic(boolean periodic) {
		interpreter.setPeriodic(periodic);
		interpreter.refresh();
	}
	public float getMaxX() {
		return maxX;
	}
	public void setMaxX(float maxX) {
		interpreter.setMaxX(maxX);
		interpreter.refresh();
	}
	public float getMinX() {
		return minX;
	}
	public void setMinX(float minX) {
		interpreter.setMinX(minX);
		interpreter.refresh();
	}
	public float getMaxY() {
		return maxY;
	}
	public void setMaxY(float maxY) {
		interpreter.setMaxY(maxY);
		interpreter.refresh();
	}
	public float getMinY() {
		return minY;
	}
	public void setMinY(float minY) {
		interpreter.setMinY(minY);
		interpreter.refresh();
	}

	public float getScaleX() {
		return scaleX;
	}

	public void setScaleX(float scaleX) {
		interpreter.setScaleX(scaleX);
		interpreter.refresh();
	}

	public float getScaleY() {
		return scaleY;
	}

	public void setScaleY(float scaleY) {
		interpreter.setScaleY(scaleY);
		interpreter.refresh();
	}
	
	public void set(FunctionSettings setFrom)
	{
		this.maxX = setFrom.maxX;
		this.maxY = setFrom.maxY;
		this.minX = setFrom.minX;
		this.minY = setFrom.minY;
		this.scaleX = setFrom.scaleX;
		this.scaleY = setFrom.scaleY;
		this.periodic = setFrom.periodic;
	
	}
	
	public PropertyStore getFunction()
	{
		return interpreter.getFunction();
	}
	
	public void setFunction(PropertyStore store)
	{
		interpreter.setFunction(store);
	}

	public void loadInitialize() {
		
	}


	public void invertView(PropertyStore store) {

	}


	public void refresh() {
		this.minX = interpreter.getMinX();
		this.maxX = interpreter.getMaxX();
		this.minY = interpreter.getMinY();
		this.maxY = interpreter.getMaxY();
		this.scaleX = interpreter.getScaleX();
		this.scaleY = interpreter.getScaleY();
		this.periodic = interpreter.isPeriodic();
		
		this.function = FunctionViewFactory.createFunctionView(interpreter.getFunction());
	}

	
	public void remove() {

	}
	public PropertyStore getStore() {
		return interpreter.getStore();
	}


	public UnivariateRealFunction buildFunction() {
		return function.getFunction();
	}


	public FunctionType getFunctionType() {
		return interpreter.getFunctionType();
	}


	public void setFunctionType(FunctionType functionType) {
		this.interpreter.setFunctionType(functionType);
		
	}
	
}
