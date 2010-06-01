package com.golemgame.mvc.golems.functions;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GolemsClassRepository;

public class CombinedFunctionInterpreter extends FunctionInterpreter {
	public static final String FUNCTION1 = "function1";
	public static final String FUNCTION2 = "function2";
	
	public static final String LEFT = "left";
	public static final String RIGHT = "right";
	public static final String BOTTOM = "botom";
	public static final String TOP = "top";
	
	public static final String TRANSLATE_X = "translateX";
	public static final String TRANSLATE_Y = "translateY";
	public static final String SCALE_X = "scaleX";
	public static final String SCALE_Y = "scaleY";
	
	public PropertyStore getFunction1()
	{
		return getStore().getPropertyStore(FUNCTION1);
	}
	public PropertyStore getFunction2()
	{
		return getStore().getPropertyStore(FUNCTION2);
	}
	
	public CombinedFunctionInterpreter() {
		this(new PropertyStore());
	}
	public CombinedFunctionInterpreter(PropertyStore store) {
		super(store);
		store.setClassName(GolemsClassRepository.COMBINED_FUNCTION_CLASS);
	}

	
	public double getLeft()
	{
		return getStore().getDouble(LEFT);
	}
	
	public void setLeft(double left)
	{
		getStore().setProperty(LEFT, left);
	}
	
	public double getRight()
	{
		return getStore().getDouble(RIGHT);
	}
	
	public void setRight(double left)
	{
		getStore().setProperty(RIGHT, left);
	}
	
	public double getBottom()
	{
		return getStore().getDouble(BOTTOM);
	}
	
	public void setBottom(double left)
	{
		getStore().setProperty(BOTTOM, left);
	}
	
	public double getTop()
	{
		return getStore().getDouble(TOP);
	}
	
	public void setTop(double left)
	{
		getStore().setProperty(TOP, left);
	}

	
	
	public double getTranslateX()
	{
		return getStore().getDouble(TRANSLATE_X);
	}
	
	public void setTranslateX(double left)
	{
		getStore().setProperty(TRANSLATE_X, left);
	}
	public double getTranslateY()
	{
		return getStore().getDouble(TRANSLATE_Y);
	}
	
	public void setTranslateY(double left)
	{
		getStore().setProperty(TRANSLATE_Y, left);
	}
	
	public double getLocalScaleX()
	{
		return getStore().getDouble(SCALE_X);
	}
	
	public void setLocalScaleX(double left)
	{
		getStore().setProperty(SCALE_X, left);
	}
	
	public double getLocalScaleY()
	{
		return getStore().getDouble(SCALE_Y);
	}
	
	public void setLocalScaleY(double left)
	{
		getStore().setProperty(SCALE_Y, left);
	}
}
