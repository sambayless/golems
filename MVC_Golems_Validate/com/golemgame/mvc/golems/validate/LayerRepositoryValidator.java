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
