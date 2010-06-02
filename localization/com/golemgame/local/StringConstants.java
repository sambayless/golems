package com.golemgame.local;

import java.text.BreakIterator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.golemgame.menu.KeyMappingConversion;
import com.golemgame.states.GeneralSettings;
import com.golemgame.states.StateManager;
import com.golemgame.tool.ActionToolSettings;

public class StringConstants {
	
	
	
	private static ResourceBundle lang;
	static
	{
		
		if(!GeneralSettings.getInstance().getUseLocalization().isValue())
		{
			lang= ResourceBundle.getBundle("com.golemgame.local.Language",Locale.ENGLISH);
		}else{
			lang= ResourceBundle.getBundle("com.golemgame.local.Language");
		}
		
	}
	
	/*
	 * $CAPITALS is replaced by certain variables
	 */
	
	private static Map<String, Keyword> keywords = new HashMap<String,Keyword>();
	
	public static void registerKeyword(String key, Keyword word)
	{
		keywords.put(key.toUpperCase(), word);
	}
	
	private static Pattern keywordPattern =   Pattern.compile("\\$[A-Z][A-Z_0-9]*");

	static
	{
		registerKeyword("$Z_AXIS_KEY", new Keyword(){

			public String get() {
				/*
				 * 		String xy  = KeyMappingConversion.getSimpleKeyDescription( ActionToolSettings.getInstance().getXYAxisActionKey().getKeyCode());
		String yz  = KeyMappingConversion.getSimpleKeyDescription( ActionToolSettings.getInstance().getYZAxisActionKey().getKeyCode());
		String nextCamera= KeyMappingConversion.getSimpleKeyDescription( GeneralSettings.getInstance().getCameraForwardKey().getKeyCode());
 ActionToolSettings.getInstance().getMultipleSelectKey().getKeyName() 
  + ActionToolSettings.getInstance().getRotateKey().getKeyName() 
   ActionToolSettings.getInstance().getModifyKey().getKeyName()
				 */
				return KeyMappingConversion.getSimpleKeyDescription( ActionToolSettings.getInstance().getYZAxisActionKey().getKeyCode());
			}
			
		});
		
		registerKeyword("$X_AXIS_KEY", new Keyword(){
			public String get() {
				return  KeyMappingConversion.getSimpleKeyDescription( ActionToolSettings.getInstance().getXYAxisActionKey().getKeyCode());
			}
		});
		
		registerKeyword("$NEXT_CAMERA", new Keyword(){
			public String get() {
				return KeyMappingConversion.getSimpleKeyDescription( GeneralSettings.getInstance().getCameraForwardKey().getKeyCode());
			}
		});
		registerKeyword("$PREV_CAMERA", new Keyword(){
			public String get() {
				return KeyMappingConversion.getSimpleKeyDescription( GeneralSettings.getInstance().getCameraBackwardKey().getKeyCode());
			}
		});
		
		registerKeyword("$OBJECT_MENU_KEY", new Keyword(){
			public String get() {				
				return KeyMappingConversion.getSimpleKeyDescription( ActionToolSettings.getInstance().getPropertiesKey().getKeyCode());
			}
		});
		
		registerKeyword("$MULTI_SELECT_KEY", new Keyword(){
			public String get() {
				return ActionToolSettings.getInstance().getMultipleSelectKey().getKeyName();
				}
		});
		
		registerKeyword("$WIRE_MODE_KEY", new Keyword(){
			public String get() {
				return ActionToolSettings.getInstance().getWireModeKey().getKeyName();
				}
		});
		
		registerKeyword("$ROTATE_KEY", new Keyword(){
			public String get() {
				return ActionToolSettings.getInstance().getRotateKey().getKeyName();
				}
		});
		
		registerKeyword("$STRETCH_KEY", new Keyword(){
			public String get() {
				return    ActionToolSettings.getInstance().getModifyKey().getKeyName();
			}
		});

		registerKeyword("$MENU_KEY", new Keyword(){
			public String get() {
				return  KeyMappingConversion.getSimpleKeyDescription( GeneralSettings.getInstance().getMenuKey().getKeyCode());

			}
		});
	}
	
	/**
	 * This also ensures loading of the language resources
	 */
	public static String getSupportedLanguage()
	{
		try{
			return lang.getString("LANGUAGE");
		}catch(MissingResourceException e)
		{
			//fail quietly
			StateManager.logError(e);
			return "";
		}
	}
	
	
	public static String wordwrap(String input, int width)
	{
		if(input.length()<=width)
			return input;
		
		BreakIterator lb = BreakIterator.getLineInstance();
		lb.setText(input);
		
		StringBuilder output = new StringBuilder();
		//StringBuilder curLine = new StringBuilder();
		int curLinePos = 0;
		int baseLine = 0;
		int nextPos = 0;
		while(nextPos!= BreakIterator.DONE)
		{
			while((nextPos = lb.next()) - baseLine <= width && nextPos!= BreakIterator.DONE)
			{
				
			}
			if(nextPos == BreakIterator.DONE)
				break;
			output.append(output.length()==0?"":"\n");
			output.append(input.substring(baseLine, nextPos).trim());//this is a whole new line
			baseLine = nextPos;
		}
		if(baseLine<input.length()){
			output.append(output.length()==0?"":"\n");
			output.append(input.substring(baseLine).trim());//this is a whole new line
		}
		baseLine = nextPos;
		
		
		return output.toString();
	}
	
	public static String replace(String input)
	{
		try{
			Matcher matcher  = keywordPattern.matcher(input);
			StringBuilder replacement = new StringBuilder();
			int prevend = 0;
			while(matcher.find())
			{
				int start = matcher.start();
				replacement.append(input.subSequence(prevend, start));
				String key = matcher.group();				
				Keyword word = keywords.get(key);
				if(word != null)
				{
					replacement.append(word.get());
				}else{
					StateManager.getLogger().warning("Missing key word: " + key);
				}
				prevend =matcher.end();
			}
			if(input.length()>prevend)
				replacement.append(input.substring(prevend));
			return replacement.toString();
		}catch(Exception e)
		{
			StateManager.logError(e);
			return input;//in case something goes wrong, fail quietly
		}
	}
	
	public static String get(String key, boolean replace)
	{
		try{
			String entry = lang.getString("GOLEMS." + key.toUpperCase());
			if(replace)
				entry = replace(entry);
			return entry;
			//return ResourceBundle.getBundle("com.golemgame.local.Language").getString("GOLEMS." + key.toUpperCase());
		}catch(MissingResourceException e)
		{
			StateManager.getLogger().severe("No translation for " + "GOLEMS." + key);
			//StateManager.logError(e);
			return "(No Translation)";
		}
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public static String get(String key)
	{
		return get(key, true);
	}

	
	public static String get(String key,String defaultEntry, boolean replace)
	{
		try{
			String entry =  lang.getString("GOLEMS." + key.toUpperCase());
			entry =format(entry);
			
			if(replace)
				entry = replace(entry);
		
			return entry;
			//return ResourceBundle.getBundle("com.golemgame.local.Language").getString("GOLEMS." + key.toUpperCase());
		}catch(MissingResourceException e)
		{
			StateManager.getLogger().warning("Using default translation for " + "GOLEMS." + key);
			//StateManager.logError(e);
			String entry=  replace? replace(defaultEntry) : defaultEntry;
			entry =format(entry);

			return entry;
		}
	}
	
	private static Pattern simpleLineFormat =   Pattern.compile("\\s+");
	
	private static String format(String input) {
		try{
			Matcher matcher  = simpleLineFormat.matcher(input);
			StringBuilder replacement = new StringBuilder();
			int prevend = 0;
			while(matcher.find())
			{
				int start = matcher.start();
				replacement.append(input.subSequence(prevend, start));
				//String key = matcher.group();				
				replacement.append(" ");
				prevend =matcher.end();
			}
			if(input.length()>prevend)
				replacement.append(input.substring(prevend));
			return replacement.toString();
		}catch(Exception e)
		{
			StateManager.logError(e);
			return input;//in case something goes wrong, fail quietly
		}
		
	}


	public static String get(String key,String defaultEntry)
	{
		return get(key, defaultEntry, true);
	}
}
