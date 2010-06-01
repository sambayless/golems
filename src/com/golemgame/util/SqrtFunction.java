package com.golemgame.util;

import java.io.Serializable;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;

public class SqrtFunction  implements UnivariateRealFunction,Serializable {

	private static final long serialVersionUID = 1L;

	public double value(double arg0) throws FunctionEvaluationException {
		if(arg0<=0)
			return 0;
		return Math.sqrt(arg0);
	}
	
}
