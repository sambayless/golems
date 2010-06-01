package com.golemgame.mvc.golems.functions;

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.DoubleType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GolemsClassRepository;

public class PolynomialFunctionInterpreter extends FunctionInterpreter {
	public final static String COEFFICIENTS = "coefficients";

	public PolynomialFunctionInterpreter() {
		this(new PropertyStore());
		
	}

	public PolynomialFunctionInterpreter(PropertyStore store) {
		super(store);
		super.getStore().setClassName(GolemsClassRepository.POLYNOMIAL_FUNCTION_CLASS);
	}
	
	
	public CollectionType getCoefficients()
	{
		return super.getStore().getCollectionType(COEFFICIENTS);
	}
	
	public void setCoefficient(int pos, DataType value)
	{
		getCoefficients().setElement(value,pos);
	}
	
	public double getCoefficient(int pos)
	{
		 DataType value = getCoefficients().getElement(pos);
		 if (value == null || value.getType()!= DataType.Type.DOUBLE)
		 {
			 return 0;
		 }else
			 return ((DoubleType)value).getValue();
	}
	

	
	
}
