package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.ColorType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;

public class ColorRepositoryInterpreter extends StoreInterpreter {

	public static final String COLORS = "colors";
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(COLORS);
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(COLORS))
			return defaultCollection;
		return super.getDefaultValue(key);
	}
	
	public ColorRepositoryInterpreter() {
		this( new PropertyStore());
	}

	public ColorRepositoryInterpreter(PropertyStore store) {
		super(store);
		super.getStore().setClassName(GolemsClassRepository.COLOR_REPOSITORY_CLASS);
	}
	
	public CollectionType getColors()
	{
		return getStore().getCollectionType(COLORS);
	}

	public void addColor(ColorType color)
	{
		getColors().addElement(color);
	}
	
	public void removeColor(ColorType color)
	{
		getColors().removeElement(color);
	}

}
