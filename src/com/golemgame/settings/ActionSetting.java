package com.golemgame.settings;

/**
 * This is a setting to signal that some instruction has been given, ie, delete. It is not associated with any persisting value.
 * For example, an action setting could be "Take Screen Shot" or "Open Menu."
 * @author Sam
 *
 */
public class ActionSetting extends Setting<Object> {
	
	private static final long serialVersionUID = 1L;
	
	public ActionSetting() {
		super("");
	}

	public void initiateAction()
	{
		super.notifyOfChange();
	}

	@Override
	protected SettingChangedEvent<Object> generateChangedEvent() {
		return new SettingChangedEvent<Object>(this, null, null);
	}
	
	
}
