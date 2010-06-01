package com.golemgame.settings;

public class BooleanSetting extends Setting<Boolean> {
	private static final long serialVersionUID = 1L;
	
	public static final String PREF_KEY_TYPE = "BOOLEAN_SETTING";
	
	private boolean value;
	
	public BooleanSetting(String name, boolean value) {
		super(name);
		this.value = value;
	}
	
	public BooleanSetting(String name) {
		this(name,false);
	}
	

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) 
	{
		if (this.value != value)
		{

			this.value = value;
			super.notifyOfChange(new SettingChangedEvent<Boolean>(this,value, !value));
		}		
	}

	@Override
	public String getStringRepresentation() {
		return String.valueOf(this.isValue());
	}

	@Override
	public void loadFromString(String value) throws SettingLoadException {
		try{
			this.setValue(Boolean.parseBoolean(value));
		}catch(RuntimeException e)
		{
			throw new SettingLoadException(e);
		}
	}

	@Override
	protected SettingChangedEvent<Boolean> generateChangedEvent() {
		
		return new SettingChangedEvent<Boolean>(this,value, value);

	}
	
}
