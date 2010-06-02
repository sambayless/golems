package com.golemgame.settings;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import com.golemgame.menu.KeyMappingConversion;
import com.jme.input.KeyInputListener;


/**
 * This is a special setting type: its value is the identity of a key; its value is changed not by pressing or releasing that key
 * but by changing which key it should respond to. KeySetting has as a secondary function in that it will listen to key press events
 * and notify any attached listeners when actions corresponding to its key occur. 
 * The intended use of this is to provide customizable key actions, including actions that change other settings.
 * @author Sam
 *
 */
public class KeySetting extends Setting<Integer> implements KeyInputListener {
	private static final long serialVersionUID = 1L;

	public static final String PREF_KEY_TYPE = "KEY_SETTING";
	
	public final static int NO_KEY = -1;
	
	private int keyCode;
	
	private transient Collection<KeyInputListener> listeners = new CopyOnWriteArrayList<KeyInputListener>();
	
	/**
	 * Should be called by GameEventNotifiers each time a GameEvent is generated.
	 * @param event
	 */
	public void generateEvent(char character, int keyCode, boolean pressed)
	{
			
	        for (KeyInputListener listener:listeners)
	        {
	        	listener.onKey(character, keyCode, pressed);
	        }
	}
	
	public void addKeyListener(KeyInputListener listener)
	{
		
		listeners.add(listener);
	}
	public void removeKeyListener(KeyInputListener listener)
	{
		listeners.remove(listener);
	}
	
	protected KeySetting(String name, int keyCode) {
		super(name);
		this.keyCode = keyCode;
	}
	
	/**
	 * Constructs a key setting with no assigned key
	 */
	protected KeySetting(String name)
	{
		this(name,NO_KEY);
	}

	
	public final void onKey(char character, int keyCode, boolean pressed) {
		if (this.keyCode == NO_KEY)
			return;
		
		if (keyCode == this.keyCode)
		{
			generateEvent(character, keyCode, pressed);
		}		
	}

	public int getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(int keyCode) {
		if (this.keyCode != keyCode)
		{
			int oldCode = this.keyCode;
			this.keyCode = keyCode;
			super.notifyOfChange(new SettingChangedEvent<Integer>(this,keyCode, oldCode));
		}
	}

	
	public String getStringRepresentation() {
		return String.valueOf(this.getKeyCode());
	}

	
	public void loadFromString(String value) throws SettingLoadException {
		try{

			this.setKeyCode(Integer.parseInt(value));

			
		}catch(RuntimeException e)
		{
			throw new SettingLoadException(e);
		}
			
	}

	
	protected SettingChangedEvent<Integer> generateChangedEvent() {
		return new SettingChangedEvent<Integer>(this,keyCode, keyCode);
	}

	public String getKeyName() {
		return KeyMappingConversion.getSimpleKeyDescription(this.getKeyCode());
	}


	
	
}
