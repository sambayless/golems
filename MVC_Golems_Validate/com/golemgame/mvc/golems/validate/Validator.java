package com.golemgame.mvc.golems.validate;

import java.util.ArrayList;
import java.util.Collection;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.validate.requirement.DataTypeRequirement;
import com.golemgame.mvc.golems.validate.requirement.Requirement;

public abstract class Validator {
	private Collection<Requirement> requirements = new ArrayList<Requirement>();
	
	public void isValid(PropertyStore store) throws ValidationFailureException
	{
		for(Requirement r:requirements)
			if(!r.test(store))
				throw new ValidationFailureException(r);
	
	}
	
	/**
	 * Note: If the supplied default value is used, a DEEP copy will be made of it, so it is safe
	 * to reuse the same default values
	 * @param key
	 * @param defaultValue
	 */
	public final void requireData(String key, Object defaultValue)
	{
		requireData(key,DataType.infer(defaultValue));
	}
	
	public final void requireData(String key, DataType.Type type)
	{
		addRequirement(new DataTypeRequirement(type,key));
	}
		
	public final void requireData(String key, DataType defaultValue)
	{
		addRequirement(new DataTypeRequirement(defaultValue.getType(),defaultValue,key));
	}
	
	public void makeValid(PropertyStore store) 
	{
		for(Requirement r:requirements)
			r.enfore(store);
			
	}
	
	public void addRequirement(Requirement r)
	{
		requirements.add(r);
	}
	
}
