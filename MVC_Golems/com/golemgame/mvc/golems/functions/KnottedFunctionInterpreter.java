package com.golemgame.mvc.golems.functions;

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DoubleType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GolemsClassRepository;

public class KnottedFunctionInterpreter extends FunctionInterpreter{
	public static final String X_KNOTS = "knots.x";
	public static final String Y_KNOTS = "knots.y";
	public KnottedFunctionInterpreter() {
		this(new PropertyStore());
	}
	public KnottedFunctionInterpreter(PropertyStore store) {
		super(store);
		super.getStore().setClassName(GolemsClassRepository.KNOTTED_FUNCTION_CLASS);
	}
	
	
	public CollectionType getXKnots()
	{
		return getStore().getCollectionType(X_KNOTS);
	}
	
	public CollectionType getYKnots()
	{
		return getStore().getCollectionType(Y_KNOTS);
	}
	
	public void setKnot(int pos, double x, double y)
	{
		getXKnots().setElement(new DoubleType(x), pos);
		getYKnots().setElement(new DoubleType(y), pos);
		
	}

	
	
	
	
}
