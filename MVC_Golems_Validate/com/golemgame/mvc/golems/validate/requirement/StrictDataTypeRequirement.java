package com.golemgame.mvc.golems.validate.requirement;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.FloatType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.validate.ValidationFailureException;

public class StrictDataTypeRequirement extends DataTypeRequirement {
	private final DataType requiredValue;
	private final String key;
	public StrictDataTypeRequirement( String key, DataType requiredValue) {
		super(requiredValue.getType(), requiredValue, key);
		this.key = key;
		this.requiredValue = requiredValue;
	}
	@Override
	protected boolean testValue(DataType value) throws ValidationFailureException {
		FloatType v =(FloatType) value;
		
		return requiredValue.equals(v);
	}
	@Override
	public void enfore(PropertyStore store) {
		
		 store.setProperty(key,requiredValue.deepCopy());
		
	}
}
