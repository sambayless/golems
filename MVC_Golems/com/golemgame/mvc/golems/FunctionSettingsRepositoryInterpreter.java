package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;

public class FunctionSettingsRepositoryInterpreter extends StoreInterpreter {

	public static final String FUNCTIONS = "functions";
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(FUNCTIONS);
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(FUNCTIONS))
			return defaultCollection;
		return super.getDefaultValue(key);
	}
	
	public FunctionSettingsRepositoryInterpreter() {
		this( new PropertyStore());
	}

	public FunctionSettingsRepositoryInterpreter(PropertyStore store) {
		super(store);
		super.getStore().setClassName(GolemsClassRepository.FUNCTION_REPOSITORY_CLASS);
	}
	
	public CollectionType getFunctions()
	{
		return getStore().getCollectionType(FUNCTIONS);
	}

	public void addFunction(PropertyStore store)
	{
		getFunctions().addElement(store);
	}
	
	public void removeFunction(PropertyStore store)
	{
		getFunctions().removeElement(store);
	}
	
}
