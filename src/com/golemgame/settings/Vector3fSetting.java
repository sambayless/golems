package com.golemgame.settings;

import com.jme.math.Vector3f;

public class Vector3fSetting extends Setting<Vector3f> {
	private static final long serialVersionUID = 1L;
	
	public static final String PREF_KEY_TYPE = "VECTOR_SETTING";
	
	private Vector3f value;
	
	protected Vector3fSetting(String name,Vector3f value) {
		super(name);
		this.value = value;
	}
	
	protected Vector3fSetting(String name)
	{
		this(name,new Vector3f(0,0,0));
	}

	/**
	 * Get the value of this setting. For performance reasons, this returns the internal instance of the Vector3f. 
	 * This instance should be considered immutable - changing its value will cause this setting to behave incorrectly.
	 * @return
	 */
	public Vector3f getValue() {
		return value;
	}

	/**
	 * Set this Vector3f to match the provided value.
	 * @param value
	 */
	public void setValue(Vector3f value) 
	{
		if(value == null)
			value = new Vector3f(0,0,0);
		if (!this.value.equals( value))
		{
			Vector3f oldValue = new Vector3f(this.value);
			this.value.set(value);
			super.notifyOfChange(new SettingChangedEvent<Vector3f>(this,value, oldValue));
		}		
	}

	@Override
	public String getStringRepresentation() {
		Vector3f value = this.getValue();
		return String.valueOf(value.getX()) + VALUE_SEPARATOR + String.valueOf(value.getY()) + VALUE_SEPARATOR + String.valueOf(value.getZ());

	}

	@Override
	public void loadFromString(String value) throws SettingLoadException {
		try{
			int separator1 = value.indexOf(VALUE_SEPARATOR);
			
			int separator2 = value.indexOf(VALUE_SEPARATOR,separator1+1);
			if (separator1 >=0 && separator2>=0)
			{
				float x = Float.parseFloat(value.substring(0,separator1));
				float y = Float.parseFloat(value.substring(separator1+VALUE_SEPARATOR.length(),separator2));
				float z = Float.parseFloat(value.substring(separator2+VALUE_SEPARATOR.length()));
				setValue( new Vector3f(x,y,z));
			}else
				throw new SettingLoadException();
		}catch(RuntimeException e)
		{
			throw new SettingLoadException(e);
		}
	}

	@Override
	protected SettingChangedEvent<Vector3f> generateChangedEvent() {

		return new SettingChangedEvent<Vector3f>(this,value, value);
	}
	

}
