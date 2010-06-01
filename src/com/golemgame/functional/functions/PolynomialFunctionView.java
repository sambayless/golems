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
