package com.golemgame.mvc.golems.validate.requirement;

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.validate.ValidationFactory;
import com.golemgame.mvc.golems.validate.ValidationFailureException;

public class RecursiveValidationRequirement extends DataTypeRequirement{

	private final String collectionKey;
	private final ValidationFactory target;
	
	public RecursiveValidationRequirement(String collectionKey, ValidationFactory target) {
		super(DataType.Type.COLLECTION,collectionKey);
		this.collectionKey = collectionKey;
		this.target = target;
	}

	public void enfore(PropertyStore store) {
		CollectionType c = store.getCollectionType(collectionKey);
		for (DataType t:c.getValues())
		{
			if (t != null && t.getType() == DataType.Type.PROPERTIES)
			{
				target.makeValid((PropertyStore)t);
			}
		}
	}

	public boolean test(PropertyStore store) throws ValidationFailureException {
		CollectionType c = store.getCollectionType(collectionKey);
		for (DataType t:c.getValues())
		{
			if (t != null && t.getType() == DataType.Type.PROPERTIES)
			{
				target.isValid((PropertyStore)t);
			}
		}
		return true;
	}

}
