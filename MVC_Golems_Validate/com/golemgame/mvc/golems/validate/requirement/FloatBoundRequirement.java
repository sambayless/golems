package com.golemgame.mvc.golems.validate.requirement;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.FloatType;
import com.golemgame.mvc.golems.validate.ValidationFailureException;

public class FloatBoundRequirement extends DataTypeRequirement {
	private final float max;
	private final float min;
	public FloatBoundRequirement(float min,float max, String key) {
		super(DataType.Type.FLOAT, new FloatType(0f),key);
		this.min = min;
		this.max = max;
		
	}
	
	@Override
	protected boolean testValue(DataType value) throws ValidationFailureException {
		FloatType v =(FloatType) value;
		if(v.getValue() < min)
			return false;
		if(v.getValue() > max)
			return false;
	
		return super.testValue(value);
	}
	@Override
	protected void enforeValue(DataType value) {
		FloatType v =(FloatType) value;
		
		if(v.getValue() < min)
			v.setValue(min);
		else if (v.getValue() > max)
			v.setValue(max);
	}
}
