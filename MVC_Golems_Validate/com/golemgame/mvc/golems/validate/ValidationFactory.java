package com.golemgame.mvc.golems.validate;

import com.golemgame.mvc.PropertyStore;

public abstract class ValidationFactory {

	public abstract void isValid(PropertyStore store)throws ValidationFailureException;
	
	public abstract void isValid(PropertyStore store, String classType) throws ValidationFailureException;
	
	public abstract void makeValid(PropertyStore store);

	public abstract void makeValid(PropertyStore store, String classType);

	//add context mechanism to avoid repeats on recursive calls
	
}
