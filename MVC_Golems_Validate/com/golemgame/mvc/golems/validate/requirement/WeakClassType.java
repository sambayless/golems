package com.golemgame.mvc.golems.validate.requirement;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.validate.ValidationFailureException;

/**
 * Enforce a class type on a property store, IF this data is a property store and IF it doesnt have a classtype already
 * @author Sam
 *
 */
public class WeakClassType extends DataTypeRequirement {
	private final String classType;
	//private final String key;
	
	public WeakClassType(String key,String classType) {
		super(DataType.Type.PROPERTIES,key);
		this.classType = classType;
		//this.key = key;
	}

	@Override
	protected void enforeValue(DataType value) {
		PropertyStore store = (PropertyStore) value;
		if(store.getClassName()==null || store.getClassName().length() == 0)
		{
			store.setClassName(classType);
		}
	}

	@Override
	protected boolean testValue(DataType value)
			throws ValidationFailureException {
		PropertyStore store = (PropertyStore) value;
		return store.getClassName()!= null && store.getClassName().length()>0;
	}



}
