package com.golemgame.functional.functions;

import org.apache.commons.math.analysis.UnivariateRealFunction;

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.DoubleType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.functions.KnottedFunctionInterpreter;
import com.golemgame.util.LinearInterpolator;

public class KnottedFunctionView  extends SustainedFunctionView {

	private final KnottedFunctionInterpreter interpreter;
	public KnottedFunctionView(PropertyStore store) {
		super(store);
		interpreter = new KnottedFunctionInterpreter(store);
	}

	@Override
	public UnivariateRealFunction getFunction() {
		CollectionType xKnots = interpreter.getXKnots();
		CollectionType yKnots = interpreter.getYKnots();
		int size = Math.min(xKnots.getValues().size(),yKnots.getValues().size());
		double[] xs = new double[size];
		double[] ys = new double[size];
		
		for (int i = 0; i < size;i++)
		{
			DataType x = xKnots.getElement(i);
			DataType y = yKnots.getElement(i);
			
			if(y.getType() == DataType.Type.DOUBLE && x.getType() == DataType.Type.DOUBLE)
			{
				xs[i] = ((DoubleType)x).getValue();
				ys[i] = ((DoubleType)y).getValue();
				
			}else
				xs[i] = Double.NEGATIVE_INFINITY;
		}
		return LinearInterpolator.getInstance().interpolate(xs, ys);
	}
	

}
