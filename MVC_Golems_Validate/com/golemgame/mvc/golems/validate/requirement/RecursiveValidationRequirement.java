/*******************************************************************************
 * Copyright 2008, 2009, 2010 Sam Bayless.
 * 
 *     This file is part of Golems.
 * 
 *     Golems is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Golems is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Golems. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
