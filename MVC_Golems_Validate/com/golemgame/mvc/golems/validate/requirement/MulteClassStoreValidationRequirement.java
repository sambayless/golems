package com.golemgame.mvc.golems.validate.requirement;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.validate.ValidationFactory;
import com.golemgame.mvc.golems.validate.ValidationFailureException;

/**
 * Like the single store validation req, except that you may designate a set of acceptable classes
 * @author Sam
 *
 */
public class MulteClassStoreValidationRequirement extends DataTypeRequirement {
	private final ValidationFactory target;
	private final String[] classTypes;
	
	/**
	 * The first supplied class type is default
	 * @param storeKey
	 * @param classTypes
	 * @param target
	 */
	public MulteClassStoreValidationRequirement(String storeKey,String[] classTypes, ValidationFactory target) {
		super(DataType.Type.PROPERTIES,storeKey);
		if(classTypes.length==0)
			throw new IllegalStateException();
		this.classTypes = classTypes;
		this.target = target;
	}

	public void enforeValue(DataType value) {
		
		PropertyStore store = (PropertyStore) value;
		if(store.getClassName()==null || store.getClassName().length() == 0)
		{
			store.setClassName(classTypes[0]);
		}else
		{
			String name = store.getClassName();
			boolean correct = false;
			for(String allowed:classTypes)
			{
				if(allowed.equals(name))
				{
					correct = true;
				}
			}
			if(!correct)
				store.setClassName(classTypes[0]);
		}
		
		
		target.makeValid((PropertyStore)store, store.getClassName());
	
			
		
	}

	public boolean testValue(DataType value) throws ValidationFailureException {
		PropertyStore store = (PropertyStore) value;
		if(store.getClassName()==null || store.getClassName().length() == 0)
		{
			return false;
		}else
		{
			String name = store.getClassName();
			boolean correct = false;
			for(String allowed:classTypes)
			{
				if(allowed.equals(name))
				{
					correct = true;
				}
			}
			if(!correct)
				return false;
		
			 target.isValid(store,store.getClassName());
		}

		 return true;
	}
}
