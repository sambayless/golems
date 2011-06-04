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
package com.golemgame.mvc.golems.validate;

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.DataType.Type;
import com.golemgame.mvc.golems.LayerInterpreter;
import com.golemgame.mvc.golems.LayerRepositoryInterpreter;
import com.golemgame.mvc.golems.validate.requirement.DataTypeRequirement;
import com.golemgame.mvc.golems.validate.requirement.RecursiveValidationRequirement;

/**
 * All repositories must have at least one layer
 * @author Sam
 *
 */
public class LayerRepositoryValidator extends Validator {

	public LayerRepositoryValidator() {
		super();
		super.requireData(LayerRepositoryInterpreter.LAYERS,new CollectionType());	
		super.addRequirement(new RecursiveValidationRequirement(LayerRepositoryInterpreter.LAYERS, GolemsValidator.getInstance()));
		
		
		
		super.addRequirement(new DataTypeRequirement(Type.COLLECTION, new CollectionType(), LayerRepositoryInterpreter.LAYERS)
		{

			@Override
			protected void enforeValue(DataType value) {
				CollectionType collection = (CollectionType) value;
				
				if(!collection.getValues().isEmpty())
					return;
				
				LayerInterpreter layer = new LayerInterpreter();
				layer.setLayerName("Layer 1");
				collection.addElement(layer.getStore());
			}

			@Override
			protected boolean testValue(DataType value)
					throws ValidationFailureException {
				CollectionType collection = (CollectionType) value;
				
				if(collection.getValues().isEmpty())
					return false;
				
				
				
				return true;
			}

		
			
		});
	}

}
