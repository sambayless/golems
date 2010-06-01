package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;

public class GroupManagerInterpreter extends StoreInterpreter{
	public static final String GROUPS = "groups";
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(GROUPS);

		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(GROUPS))
			return defaultCollection;

		return super.getDefaultValue(key);
	}
	
	public GroupManagerInterpreter() {
		this(new PropertyStore());
	}

	public GroupManagerInterpreter(PropertyStore store) {
		super(store);
		super.getStore().setClassName(GolemsClassRepository.GROUP_MANAGER_CLASS);
	}

	public CollectionType getGroups()
	{
		return getStore().getCollectionType(GROUPS);
	}
	
	public void addGroup(PropertyStore group)
	{
		getGroups().addElement(group);
	}
	
	public void removeGroup(PropertyStore group)
	{
		getGroups().removeElement(group);
	}
}
