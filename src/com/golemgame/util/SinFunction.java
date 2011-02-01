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

import org.apache.commons.math.analysis.UnivariateRealFunction;
public class SinFunction implements UnivariateRealFunction,Serializable {
	static final long serialVersionUID =1;
	private double amplitude;
	private double lambda;
	private double phase;
	
	public SinFunction(double amplitude, double lambda, double phase) {
		super();
		this.amplitude = amplitude;
		this.lambda = lambda;
		this.phase = phase;
	}

	
	public double value(double x){
		return Math.sin((float)x/lambda + phase) *amplitude;
	}

	
	
	
	public double getAmplitude() {
		return amplitude;
	}


	public void setAmplitude(double amplitude) {
		this.amplitude = amplitude;
	}


	public double getLambda() {
		return lambda;
	}


	public void setLambda(double lambda) {
		this.lambda = lambda;
	}


	public double getPhase() {
		return phase;
	}


	public void setPhase(double phase) {
		this.phase = phase;
	}


}
