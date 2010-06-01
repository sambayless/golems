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
