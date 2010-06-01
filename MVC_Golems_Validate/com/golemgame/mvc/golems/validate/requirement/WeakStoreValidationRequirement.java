package com.golemgame.mvc.golems.validate.requirement;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.validate.ValidationFactory;
import com.golemgame.mvc.golems.validate.ValidationFailureException;

/**
 * Will attempt to validate the given key. Enforces the requirement that the key is a 
 * property store, then validates if the supplied factory has a validator for the store's class type.
 * @author Sam
 *
 */
public class WeakStoreValidationRequirement extends DataTypeRequirement {
	private final ValidationFactory target;
	
	public WeakStoreValidationRequirement(String key, ValidationFactory target) {
		super(DataType.Type.PROPERTIES,key);
		this.target = target;
	}
	
	@Override
	protected void enforeValue(DataType value) {
		PropertyStore store = (PropertyStore) value;
		target.makeValid(store);
	}

	@Override
	protected boolean testValue(DataType value)
			throws ValidationFailureException {
		PropertyStore store = (PropertyStore) value;
		target.isValid((PropertyStore)store);	
		return true;
	}


}
