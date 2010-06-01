package com.golemgame.mvc.golems.validate.requirement;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.DataType.Type;
import com.golemgame.mvc.golems.validate.ValidationFailureException;

public class DataTypeRequirement implements Requirement {

	private final DataType.Type requiredType;
	private final DataType defaultValue;
	private final String key;
	

	public DataTypeRequirement(Type requiredType, DataType defaultValue,String key) {
		super();
		this.requiredType = requiredType;
		this.defaultValue = defaultValue;
		this.key = key;
	}
	
	public DataTypeRequirement(Type requiredType,String key) {
		super();
		this.requiredType = requiredType;
		this.defaultValue = null;
		this.key = key;
	}



	public void enfore(PropertyStore store) {
		DataType v;
		if(defaultValue == null)
		{
			v= store.getProperty(key,requiredType);
		}else
		{
			v = store.getProperty(key, defaultValue);
		}
		enforeValue(v);
	}

	public boolean test(PropertyStore store) throws ValidationFailureException {
		DataType v =  store.getProperty(key);
		if (v == null &! (requiredType == null))
			return false;
		if (v.getType() == requiredType)
			return true;
		return testValue(v);
	}

	protected boolean testValue(DataType value)throws ValidationFailureException 
	{
		return true;
	}
	
	protected void enforeValue(DataType value)
	{
		
	}

/*	public DataType enfore(DataType value) {
		if(!DataType.isType(value, requiredType))
			return defaultValue.deepCopy();
				
		return value;
	}*/

}
