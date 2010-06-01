package com.golemgame.settings;

public class IntegerSetting extends Setting<Integer> {
	private static final long serialVersionUID = 1L;
	
	public static final String PREF_KEY_TYPE = "INT_SETTING";
	
	private int value;
	
	public IntegerSetting(String name, int value) {
		super(name);
		this.value = value;
	}
	
	public IntegerSetting(String name) {
		this(name,0);
	}
	

	public int getValue() {
		return value;
	}

	public void setValue(int value) 
	{
		if (this.value != value)
		{
			int oldValue = this.value;
			this.value = value;
			super.notifyOfChange(new SettingChangedEvent<Integer>(this,value, oldValue));
		}		
	}

	@Override
	public String getStringRepresentation() {
		return String.valueOf(this.getValue());
	}

	@Override
	public void loadFromString(String value) throws SettingLoadException {
		try{
			this.setValue(Integer.parseInt(value));
		}catch(RuntimeException e)
		{
			throw new SettingLoadException(e);
		}
	}

	@Override
	protected SettingChangedEvent<Integer> generateChangedEvent() {
		
		return new SettingChangedEvent<Integer>(this,value, value);

	}
	
}
