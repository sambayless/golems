package com.golemgame.mvc.golems.functions;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GolemsClassRepository;

public class FunctionFactory {

	public static FunctionInterpreter createFunctionInterpreter(PropertyStore store)
	{
		String className = store.getClassName();
		if (className== null)
			className = "";
		if (className.equalsIgnoreCase(GolemsClassRepository.SINE_FUNCTION_CLASS))
		{
			return new SineFunctionInterpreter(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.POLYNOMIAL_FUNCTION_CLASS))
		{
			return new PolynomialFunctionInterpreter(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.KNOTTED_FUNCTION_CLASS))
		{
			return new KnottedFunctionInterpreter(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.COMBINED_FUNCTION_CLASS))
		{
			return new CombinedFunctionInterpreter(store);
		}else if (className.equalsIgnoreCase(GolemsClassRepository.SQRT_FUNCTION_CLASS))
		{
			return new SqrtFunctionInterpreter(store);
		}
		
		return new FunctionInterpreter(store);
		
	}
	

	
}
