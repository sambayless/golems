package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;

public class OscilloscopeInterpreter extends StandardFunctionalInterpreter{
	public static final String OSCILLOSCOPE_NAME = "oscilloscope.name";
	public static final String OUTPUT_DEVICE = "output.device";

	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(OSCILLOSCOPE_NAME);
		keys.add(OUTPUT_DEVICE);

		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(OSCILLOSCOPE_NAME))
			return defaultString;
		if(key.equals(OUTPUT_DEVICE))
			return defaultStore;
	
		return super.getDefaultValue(key);
	}
	
	
	public OscilloscopeInterpreter() {
		this(new PropertyStore());
	}
	public OscilloscopeInterpreter(PropertyStore store) {
		super(store);
		store.setClassName(GolemsClassRepository.OSCILLOSCOPE_CLASS);
	}
	public String getOscilloscopeName()
	{
		return getStore().getString(OSCILLOSCOPE_NAME,"(Unnamed)");
	}
	
	public void setOscilloscopeName(String name)
	{
		getStore().setProperty(OSCILLOSCOPE_NAME,name);
	}
	public PropertyStore getOutputDevice()
	{
		return getStore().getPropertyStore(OUTPUT_DEVICE);
	}
	
	public void setOutputDevice(PropertyStore key)
	{
		getStore().setProperty(OUTPUT_DEVICE, key);
	}
	

}
