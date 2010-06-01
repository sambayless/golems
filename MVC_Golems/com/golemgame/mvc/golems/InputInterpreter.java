package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;

public class InputInterpreter  extends StandardFunctionalInterpreter{


	public static final String INPUT_DEVICE = "input.device";
	public static final String NAME = "input.name";
	public static final String INSTRUMENT_X = "instrument.x";
	public static final String INSTRUMENT_Y = "instrument.y";
	public static final String INSTRUMENT_WIDTH = "instrument.width";
	public static final String INSTRUMENT_HEIGHT = "instrument.height";
	public static final String INSTRUMENT_WINDOWED= "instrument.windowed";
	public static final String INSTRUMENT_LOCKED= "instrument.locked";
	public static final String INSTRUMENT_USER_POSITIONED= "instrument.userPositioned";
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(NAME);
		keys.add(INPUT_DEVICE);
		keys.add(INSTRUMENT_X);
		keys.add(INSTRUMENT_Y);
		keys.add(INSTRUMENT_WIDTH);
		keys.add(INSTRUMENT_HEIGHT);
		keys.add(INSTRUMENT_WINDOWED);
		keys.add(INSTRUMENT_LOCKED);
		keys.add(INSTRUMENT_USER_POSITIONED);
		

		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(NAME))
			return defaultString;
		if(key.equals(INPUT_DEVICE))
			return defaultStore;
		return super.getDefaultValue(key);
	}
	
	
	
	public String getName()
	{
		return getStore().getString(NAME,"(Unnamed)");
	}
	
	public void setName(String name)
	{
		getStore().setProperty(NAME,name);
	}
	
	public InputInterpreter(PropertyStore store) {
		super(store);
	}

	public InputInterpreter() {
		this(new PropertyStore());		
		getStore().setClassName(GolemsClassRepository.INPUT_CLASS);
	}
	
	public PropertyStore getInputDevice()
	{
		return getStore().getPropertyStore(INPUT_DEVICE);
	}
	
	public void setInputDevice(PropertyStore key)
	{
		getStore().setProperty(INPUT_DEVICE, key);
	}
	
}
