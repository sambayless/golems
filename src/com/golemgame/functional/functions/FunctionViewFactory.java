package com.golemgame.functional.functions;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GolemsClassRepository;

public class FunctionViewFactory {

	public static SustainedFunctionView createFunctionView(PropertyStore store)
	{
		String className = store.getClassName();
		if (className== null)
			className = "";
		if (className.equalsIgnoreCase(GolemsClassRepository.SINE_FUNCTION_CLASS))
		{
			return new SineFunctionView(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.POLYNOMIAL_FUNCTION_CLASS))
		{
			return new PolynomialFunctionView(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.KNOTTED_FUNCTION_CLASS))
		{
			return new KnottedFunctionView(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.COMBINED_FUNCTION_CLASS))
		{
			return new CombinedFunctionView(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.SQRT_FUNCTION_CLASS))
		{
			return new SqrtFunctionView(store);
		}else
		{
			return new PolynomialFunctionView(store);
		}
		
		
		//throw new IllegalArgumentException("Component not recognized: " + store.toString());
		
	}
	

	
}
