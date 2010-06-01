package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;

/**
 * This class holds a collection of layers, and possibly related information.
 * @author Sam
 *
 */
public class LayerRepositoryInterpreter extends StoreInterpreter{
	public static final String LAYERS = "layers";
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(LAYERS);

		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(LAYERS))
			return defaultString;

		return super.getDefaultValue(key);
	}
	

	public LayerRepositoryInterpreter() {
		this(new PropertyStore());
	}

	public LayerRepositoryInterpreter(PropertyStore store) {
		super(store);
		super.getStore().setClassName(GolemsClassRepository.LAYER_REPOSITORY_CLASS);
	}

	public CollectionType getLayers()
	{
		return getStore().getCollectionType(LAYERS);
	}
	
	public void addLayer(PropertyStore layer)
	{
		getLayers().addElement(layer);
	}
	
	public void removeLayer(PropertyStore layer)
	{
		getLayers().removeElement(layer);
	}

	
}
