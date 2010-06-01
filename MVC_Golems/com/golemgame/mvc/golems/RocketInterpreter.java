package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;

public class RocketInterpreter extends CylinderInterpreter {
	
	public static final String MAX_ACCELERATION = "acceleration.max";
	
	public static final String PROPELLENT_PROPERTIES = "propellant.properties";
	
	private final StandardFunctionalInterpreter portInterpreter;
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(MAX_ACCELERATION);
		keys.add(PROPELLENT_PROPERTIES);	
		portInterpreter.enumerateKeys(keys);
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
/*		DataType defVal = DefaultProperties.getInstance().getDefaultStore(DesignViewFactory.ROCKET_CLASS).getProperty(key);
		if (defVal!= null && defVal != NullType.get())
			return defVal;*/
		if(key.equals(PROPELLENT_PROPERTIES))
			return defaultStore;
		if(key.equals(MAX_ACCELERATION))
			return defaultFloat;
		return super.getDefaultValue(key);
	}
	
	public RocketInterpreter(PropertyStore store) {
		super(store);
		portInterpreter = new StandardFunctionalInterpreter(store);
		store.setClassName(GolemsClassRepository.ROCKET_CLASS);
	}

	public RocketInterpreter() {
		this(new PropertyStore());		
	}
	
	//echo the port interpreters functions here... in case we ever need them
	public PropertyStore getOutput()
	{
		return portInterpreter.getOutput();
	}
	
/*	public void setOutput(PropertyStore reference)
	{
		portInterpreter.setOutput(reference);
	}*/
	
	public PropertyStore getInput()
	{
		return portInterpreter.getInput();
	}
	
/*	public void setInput(PropertyStore reference)
	{
		portInterpreter.setInput(reference);
	}*/
	
	public PropertyStore getAuxInput()
	{
		return portInterpreter.getAuxInput();
	}
	
/*	public void setAuxInput(PropertyStore reference)
	{
		portInterpreter.setAuxInput(reference);
	}*/
	
	public void setMaxAcceleration(float accel)
	{
		this.getStore().setProperty(MAX_ACCELERATION, accel);
	}
	
	public float getMaxAcceleration()
	{
		return getStore().getFloat(MAX_ACCELERATION,10f);
	}
	
	public PropertyStore getPropellantProperties()
	{
		return getStore().getPropertyStore(PROPELLENT_PROPERTIES);
	}
	
}