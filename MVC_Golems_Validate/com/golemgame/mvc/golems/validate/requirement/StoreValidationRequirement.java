package com.golemgame.mvc.golems.validate.requirement;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.validate.ValidationFactory;
import com.golemgame.mvc.golems.validate.ValidationFailureException;

public class StoreValidationRequirement extends WeakClassType {
	private final ValidationFactory target;
	private final String classType;
	public StoreValidationRequirement(String storeKey,String classType, ValidationFactory target) {
		super(storeKey,classType);
		this.classType = classType;
		this.target = target;
	}

	public void enforeValue(DataType store) {
		
		target.makeValid((PropertyStore)store, classType);
	//	target.makeValid((PropertyStore)store);
			
		
	}

	public boolean testValue(DataType store) throws ValidationFailureException {
		 target.isValid((PropertyStore)store);	
		 return true;//is this behaviour correct? Yes, isValid will throw an exception if needed.
	}
}
