package com.golemgame.settings;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.golemgame.states.StateManager;
import com.jme.math.Vector3f;

/**
 * This singleton acts as a repository (and factory) for all settings in use.
 * It collects them all under one package, for loading/saving purposes.
 * @author Sam
 *
 */
public class SettingsManager {
	private static final SettingsManager instance = new SettingsManager();
	public static final String PREFERENCES_ROOT_NAME = "Golems.prefs";
	private static final String SETTINGS_PREFERENCES_ROOT_NAME = SettingsManager.PREFERENCES_ROOT_NAME + "/PersistentSettings";
	private static final String SETTINGS_KEY_ROOT = "Setting.";
	private static final String SETTINGS_NAME_ROOT = "Settingname.";

	


	/**
	 * This is a map of all created or loaded settings
	 */
	private Map<String, ISetting<?>> settingsMap = new HashMap<String,ISetting<?>>();
	
	public static void loadSettings() throws IOException
	{
		//SettingsManager manager = new SettingsManager();
		//instance = manager;
		Preferences settingsPrefs =  Preferences.userRoot().node(SETTINGS_PREFERENCES_ROOT_NAME);
		String[] keys ;
		try {
			keys = settingsPrefs.keys();
		} catch (BackingStoreException e) {
			keys = new String[]{};
		}
		
		
		List<String> settingKeys = new ArrayList<String>();
		//find the keys that are persistent settings
		for (String key:keys)
		{
			if (key.startsWith(SETTINGS_KEY_ROOT))
				settingKeys.add(key);
		}

		
		final String NO_KEY = "NO_KEY" + Double.toHexString( Math.random());
		for (String key:settingKeys)
		{
			//attempt to load each key (no need to provide a default value if it can't load - the settings already provide that when they are accessed later
			String settingPref = settingsPrefs.get(key, NO_KEY);
			if (settingPref.equals(NO_KEY))
				continue;
			
			instance.loadSetting(settingPref, key);
			
		}
		
	}
	
	private void loadSetting(String value, String key)
	{
		//keys follow this format: <SETTINGS_KEY_ROOT><SETTING_CLASS_TYPE><SETTINGS_NAME_ROOT><SETTING_NAME>
		try{
			int keyRootPos = key.indexOf(SETTINGS_KEY_ROOT);
			
			int keyNamePos = key.indexOf(SETTINGS_NAME_ROOT);
			
			
			if (keyRootPos < 0 || keyNamePos<0)
				return;
			
			keyRootPos += SETTINGS_KEY_ROOT.length();
			
			String keyType = key.substring(keyRootPos, keyNamePos);
			String keyName = key.substring(keyNamePos + SETTINGS_NAME_ROOT.length());
			if (keyName == "" || keyName == "null" || keyName == null)
				return;
			
			ISetting<?> setting = null;
			
			if (keyType.equalsIgnoreCase(BooleanSetting.class.getCanonicalName()))
			{
				setting = new BooleanSetting(keyName);
			}else if (keyType.equalsIgnoreCase(DimensionSetting.class.getCanonicalName()))
			{
				setting = new DimensionSetting(keyName);
			}else if (keyType.equalsIgnoreCase(Vector3fSetting.class.getCanonicalName()))
			{
				setting = new Vector3fSetting(keyName);
			}else if (keyType.equalsIgnoreCase(KeySetting.class.getCanonicalName()))
			{
				setting = new KeySetting(keyName);
			}else if (keyType.equalsIgnoreCase(FloatSetting.class.getCanonicalName()))
			{
				setting = new FloatSetting(keyName);
			}else if (keyType.equalsIgnoreCase(IntegerSetting.class.getCanonicalName()))
			{
				setting = new IntegerSetting(keyName);
			}else if (keyType.equalsIgnoreCase(StringSetting.class.getCanonicalName()))
			{
				setting = new StringSetting(keyName);
			}else
				return;
			
		
				setting.loadFromString(value);
				
				registerSetting(setting);
			
		}catch(IndexOutOfBoundsException e)
		{
			
		}catch(SettingLoadException e)
		{
			
		}
	}
	
	
	/**
	 * Loads a settings manager from a given destination. 
	 * This also sets the current default settings manager to the most recently loaded.
	 * (If multiple settings managers are going to be used in the future, this behaviour will have to change)
	 * @param loadFrom
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static SettingsManager loadSettings(InputStream loadFrom) throws IOException
	{
		SettingsManager manager = new SettingsManager();
		try{
			ObjectInputStream objectInput = new ObjectInputStream(loadFrom);
			Object object= objectInput.readObject();
			Collection<Setting<?>> settingsList= (Collection<Setting<?>>) object;
			for (Setting<?> setting:settingsList)
			{
				if (setting.isTransient())
					continue;//don't load transient settings.
				String keyName = getKeyName(setting);
				manager.settingsMap.put(keyName, setting);
			}
		//read in a list of settings from this stream
		}catch(ClassNotFoundException e)
		{
			throw new IOException();
		}catch(ClassCastException e)
		{
			throw new IOException();
		}
		
		return manager;
	}
	
	public void save()
	{

		Preferences settingsPrefs =  Preferences.userRoot().node(SETTINGS_PREFERENCES_ROOT_NAME);

		for (ISetting<?> setting:this.settingsMap.values())
		{
			if (setting != null && !setting.isTransient())
				settingsPrefs.put(SETTINGS_KEY_ROOT + setting.getClass().getCanonicalName() +SETTINGS_NAME_ROOT +setting.getName(), setting.getStringRepresentation());
			else if(setting != null)
			{
				settingsPrefs.remove(SETTINGS_KEY_ROOT + setting.getClass().getCanonicalName() +SETTINGS_NAME_ROOT +setting.getName());
			}
		}
		
	}
	
	public void save(OutputStream output) throws IOException
	{
		ObjectOutputStream objectOutput = new ObjectOutputStream(output);
		
		//filter out any transient settings - they aren't saved.

		ArrayList<ISetting<?>> saveList = new ArrayList<ISetting<?>>();
		for (ISetting<?> setting:settingsMap.values())
		{
			if (setting != null && !setting.isTransient())
				saveList.add(setting);
		}
		
		objectOutput.writeObject(saveList);
	}
	
	public static SettingsManager getInstance() {

		return instance;
	}

	public KeySetting createKeySetting(String name)
	{
		return createKeySetting(name,KeySetting.NO_KEY);
	}

	/**
	 * Create a new setting. If this setting already exists in the manager,
	 * it will be returned. If this setting does not exist, a new one will be created,
	 * and it will be added to the manager.
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public KeySetting createKeySetting(String name, int defaultValue)
	{
		KeySetting toGet;
		try{
			toGet = (KeySetting) findSetting(name, KeySetting.class);
		}catch(NoSuchSettingException e)
		{
			toGet =  new KeySetting(name, defaultValue);
		}
		registerSetting(toGet);
		return toGet;
	}
	
	public Vector3fSetting createVectorSetting(String name, Vector3f defaultValue)
	{
		Vector3fSetting toGet;
		try{
			toGet = (Vector3fSetting) findSetting(name, Vector3fSetting.class);
		}catch(NoSuchSettingException e)
		{
			toGet =  new Vector3fSetting(name, defaultValue);
		}
		registerSetting(toGet);
		return toGet;
	}
	
	public DimensionSetting createDimensionSetting(String name, Dimension defaultValue)
	{
		DimensionSetting toGet;
		try{
			toGet = (DimensionSetting) findSetting(name, DimensionSetting.class);
		}catch(NoSuchSettingException e)
		{
			toGet =  new DimensionSetting(name, defaultValue);
		}
		registerSetting(toGet);
		return toGet;
	}
	
	public BooleanSetting createBooleanSetting(String name, Boolean defaultValue)
	{
		BooleanSetting toGet;
		try{
			toGet = (BooleanSetting) findSetting(name, BooleanSetting.class);
		}catch(NoSuchSettingException e)
		{
			toGet =  new BooleanSetting(name, defaultValue);
		}
		registerSetting(toGet);
		return toGet;
	}
	
	public FloatSetting createFloatSetting(String name, Float defaultValue)
	{
		FloatSetting toGet;
		try{
			toGet = (FloatSetting) findSetting(name, FloatSetting.class);
		}catch(NoSuchSettingException e)
		{
			toGet =  new FloatSetting(name, defaultValue);
		}
		registerSetting(toGet);
		return toGet;
	}
	
	public StringSetting createStringSetting(String name, String defaultValue)
	{
		StringSetting toGet;
		try{
			toGet = (StringSetting) findSetting(name, StringSetting.class);
		}catch(NoSuchSettingException e)
		{
			toGet =  new StringSetting(name, defaultValue);
		}
		registerSetting(toGet);
		return toGet;
	}
		
	public IntegerSetting createIntegerSetting(String name, Integer defaultValue)
	{
		IntegerSetting toGet;
		try{
			toGet = (IntegerSetting) findSetting(name, IntegerSetting.class);
		}catch(NoSuchSettingException e)
		{
			toGet =  new IntegerSetting(name, defaultValue);
		}
		registerSetting(toGet);
		return toGet;
	}
		
		
	private void registerSetting(ISetting<?> setting)
	{
		settingsMap.put(getKeyName(setting), setting);
	}
	
	private ISetting<?> findSetting(String name, Class<?> settingType) throws NoSuchSettingException
	{
		/*
		 * All names are stored by their settings type first, and then their actual name, as "<SettingsType>.<name>"
		 * This allows for there to be multiple settings of the same name, so long as they have different types
		 */
		
		ISetting<?> setting = settingsMap.get(getKeyName(name,settingType));
		
		if (setting != null)
		{
			if (setting.getName().equalsIgnoreCase(name) && settingType.isAssignableFrom(setting.getClass()) )
			{
				return setting;
			}
		}
		
		throw new NoSuchSettingException();
	}
	
	private static String getKeyName(String name, Class<?> settingType)
	{
		return settingType.getCanonicalName() + "." + name;
	}
	
	private static String getKeyName(ISetting<?> setting)
	{
		return getKeyName(setting.getName(),setting.getClass());
	}
	
	
	
}
