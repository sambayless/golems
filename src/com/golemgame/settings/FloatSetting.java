package com.golemgame.settings;

public class FloatSetting extends Setting<Float> {
	private static final long serialVersionUID = 1L;
	
	public static final String PREF_KEY_TYPE = "FLOAT_SETTING";
	
	private float value;
	
	public FloatSetting(String name, float value) {
		super(name);
		this.value = value;
	}
	
	public FloatSetting(String name) {
		this(name,0);
	}
	

	public float getValue() {
		return value;
	}

	public void setValue(float value) 
	{
		if (this.value != value)
		{
			float oldValue = this.value;
			this.value = value;
			super.notifyOfChange(new SettingChangedEvent<Float>(this,value, oldValue));
		}		
	}

	@Override
	public String getStringRepresentation() {
		return String.valueOf(this.getValue());
	}

	@Override
	public void loadFromString(String value) throws SettingLoadException {
		try{
			this.setValue(Float.parseFloat(value));
		}catch(RuntimeException e)
		{
			throw new SettingLoadException(e);
		}
	}

	@Override
	protected SettingChangedEvent<Float> generateChangedEvent() {
		
		return new SettingChangedEvent<Float>(this,value, value);

	}
	
}
