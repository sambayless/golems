package com.golemgame.menu;

import java.util.ArrayList;

import org.fenggui.event.key.Key;

import com.golemgame.platform.PlatformManager;
import com.jme.input.KeyInput;

public class KeyMappingConversion {

	
	
	public static int getJMEKey(int keyChar)
	{
		for (KeyConvert convert:keyList)
		{
			if(convert.getFengGUIkey()== keyChar)
			{
				return convert.getJMEKey();
			}
		}
		return -1;
	}
	
	public static int getJMEKey(org.fenggui.event.key.Key keyClass)
	{
		if (keyClass == org.fenggui.event.key.Key.UNDEFINED)
			return -1;
		
		for (KeyConvert convert:keyList)
		{
			if(convert.getFengGUIkeyClass() == keyClass)
			{
				return convert.getJMEKey();
			}
		}
		return -1;
	}
	
	public static int getJMEKey(org.fenggui.event.key.Key keyClass,int keyChar)
	{
		int jmeKey =getJMEKey(keyClass);
		int jmeCharKey = getJMEKey(keyChar);
		
		return Math.max(jmeKey, jmeCharKey);
	}
	public static String getKeyDescription(int jmeKey)
	{
		if (jmeKey<=0)
			return "(No Key)";
		for (KeyConvert convert:keyList)
		{
			if(convert.getJMEKey()== jmeKey)
			{
				return convert.getKeyName();
			}
		}
		return "(Unknown Key)";
	}
	
	
	public static String getKeyName(Key keyClass, char letter) {
		if (keyClass == Key.LETTER || keyClass == Key.DIGIT || keyClass == Key.UNDEFINED)
		{
			if (letter == ' ')
				return "SPACE";
			else
				return String.valueOf( letter);
		}
		return keyClass.toString();
	}

	
	private static java.util.List<KeyConvert> keyList;

	static
	{
		keyList = new ArrayList<KeyConvert>();
		keyList.add(new KeyConvert(' ',null, KeyInput.KEY_SPACE,"SPACE","Space"));
		
		keyList.add(new KeyConvert('a', KeyInput.KEY_A));
		keyList.add(new KeyConvert('b', KeyInput.KEY_B));
		keyList.add(new KeyConvert('c', KeyInput.KEY_C));
		keyList.add(new KeyConvert('d', KeyInput.KEY_D));
		
		keyList.add(new KeyConvert('e', KeyInput.KEY_E));
		keyList.add(new KeyConvert('f', KeyInput.KEY_F));
		keyList.add(new KeyConvert('g', KeyInput.KEY_G));
		keyList.add(new KeyConvert('h', KeyInput.KEY_H));
		
		keyList.add(new KeyConvert('i', KeyInput.KEY_I));
		keyList.add(new KeyConvert('j', KeyInput.KEY_J));
		keyList.add(new KeyConvert('k', KeyInput.KEY_K));
		keyList.add(new KeyConvert('l', KeyInput.KEY_L));
		
		keyList.add(new KeyConvert('m', KeyInput.KEY_M));
		keyList.add(new KeyConvert('n', KeyInput.KEY_N));
		keyList.add(new KeyConvert('o', KeyInput.KEY_O));
		keyList.add(new KeyConvert('p', KeyInput.KEY_P));
		
		keyList.add(new KeyConvert('q', KeyInput.KEY_Q));
		keyList.add(new KeyConvert('r', KeyInput.KEY_R));
		keyList.add(new KeyConvert('s', KeyInput.KEY_S));
		keyList.add(new KeyConvert('t', KeyInput.KEY_T));
		
		keyList.add(new KeyConvert('u', KeyInput.KEY_U));
		keyList.add(new KeyConvert('v', KeyInput.KEY_V));
		keyList.add(new KeyConvert('w', KeyInput.KEY_W));
		keyList.add(new KeyConvert('x', KeyInput.KEY_X));
		keyList.add(new KeyConvert('y', KeyInput.KEY_Y));
		keyList.add(new KeyConvert('z', KeyInput.KEY_Z));
		
		keyList.add(new KeyConvert('A', KeyInput.KEY_A));
		keyList.add(new KeyConvert('B', KeyInput.KEY_B));
		keyList.add(new KeyConvert('C', KeyInput.KEY_C));
		keyList.add(new KeyConvert('D', KeyInput.KEY_D));
		
		keyList.add(new KeyConvert('E', KeyInput.KEY_E));
		keyList.add(new KeyConvert('F', KeyInput.KEY_F));
		keyList.add(new KeyConvert('G', KeyInput.KEY_G));
		keyList.add(new KeyConvert('H', KeyInput.KEY_H));
		
		keyList.add(new KeyConvert('I', KeyInput.KEY_I));
		keyList.add(new KeyConvert('J', KeyInput.KEY_J));
		keyList.add(new KeyConvert('K', KeyInput.KEY_K));
		keyList.add(new KeyConvert('L', KeyInput.KEY_L));
		
		keyList.add(new KeyConvert('M', KeyInput.KEY_M));
		keyList.add(new KeyConvert('N', KeyInput.KEY_N));
		keyList.add(new KeyConvert('O', KeyInput.KEY_O));
		keyList.add(new KeyConvert('P', KeyInput.KEY_P));
		
		keyList.add(new KeyConvert('Q', KeyInput.KEY_Q));
		keyList.add(new KeyConvert('R', KeyInput.KEY_R));
		keyList.add(new KeyConvert('S', KeyInput.KEY_S));
		keyList.add(new KeyConvert('T', KeyInput.KEY_T));
		
		keyList.add(new KeyConvert('U', KeyInput.KEY_U));
		keyList.add(new KeyConvert('V', KeyInput.KEY_V));
		keyList.add(new KeyConvert('W', KeyInput.KEY_W));
		keyList.add(new KeyConvert('X', KeyInput.KEY_X));
		keyList.add(new KeyConvert('Y', KeyInput.KEY_Z));
		keyList.add(new KeyConvert('Z', KeyInput.KEY_Z));
		
		
		keyList.add(new KeyConvert('0', KeyInput.KEY_0));
		keyList.add(new KeyConvert(')', KeyInput.KEY_0));
		keyList.add(new KeyConvert('1', KeyInput.KEY_1));
		keyList.add(new KeyConvert('!', KeyInput.KEY_1));
		keyList.add(new KeyConvert('2', KeyInput.KEY_2));
		keyList.add(new KeyConvert('@', KeyInput.KEY_2));
		keyList.add(new KeyConvert('3', KeyInput.KEY_3));
		keyList.add(new KeyConvert('#', KeyInput.KEY_3));
		keyList.add(new KeyConvert('4', KeyInput.KEY_4));
		keyList.add(new KeyConvert('$', KeyInput.KEY_4));
		keyList.add(new KeyConvert('5', KeyInput.KEY_5));
		keyList.add(new KeyConvert('%', KeyInput.KEY_5));
		keyList.add(new KeyConvert('6', KeyInput.KEY_6));
		keyList.add(new KeyConvert('^', KeyInput.KEY_6));
		keyList.add(new KeyConvert('7', KeyInput.KEY_7));
		keyList.add(new KeyConvert('&', KeyInput.KEY_7));
		keyList.add(new KeyConvert('8', KeyInput.KEY_8));
		keyList.add(new KeyConvert('*', KeyInput.KEY_8));
		keyList.add(new KeyConvert('9', KeyInput.KEY_9));
		keyList.add(new KeyConvert('(', KeyInput.KEY_9));
		
		
		keyList.add(new KeyConvert('\'', KeyInput.KEY_APOSTROPHE,"Apostraphe"));
		keyList.add(new KeyConvert('\"', KeyInput.KEY_APOSTROPHE,"Apostraphe"));
		keyList.add(new KeyConvert('`', KeyInput.KEY_GRAVE, "Tilde"));
		keyList.add(new KeyConvert('~', KeyInput.KEY_GRAVE, "Tilde"));
		keyList.add(new KeyConvert('=', KeyInput.KEY_EQUALS, "Equals"));
		keyList.add(new KeyConvert('+', KeyInput.KEY_EQUALS, "Equals"));
		keyList.add(new KeyConvert('-', KeyInput.KEY_MINUS, "Minus"));
		keyList.add(new KeyConvert('_', KeyInput.KEY_MINUS, "Minus"));
		
		keyList.add(new KeyConvert('>', KeyInput.KEY_PERIOD, "Period"));
		keyList.add(new KeyConvert('.', KeyInput.KEY_PERIOD, "Period"));
		keyList.add(new KeyConvert('<', KeyInput.KEY_COMMA, "Comma"));
		keyList.add(new KeyConvert(',', KeyInput.KEY_COMMA, "Comma"));
		
		keyList.add(new KeyConvert('?', KeyInput.KEY_SLASH, "Slash"));
		keyList.add(new KeyConvert('/', KeyInput.KEY_SLASH, "Slash"));
		keyList.add(new KeyConvert('\\', KeyInput.KEY_BACKSLASH, "Back Slash"));
		keyList.add(new KeyConvert('|', KeyInput.KEY_BACKSLASH, "Back Slash"));
		
		keyList.add(new KeyConvert(']', KeyInput.KEY_RBRACKET, "Bracket (Right)"));
		keyList.add(new KeyConvert('}', KeyInput.KEY_RBRACKET, "Bracket (Right)"));
		keyList.add(new KeyConvert('{', KeyInput.KEY_LBRACKET, "Bracket (Left)"));
		keyList.add(new KeyConvert('[', KeyInput.KEY_LBRACKET, "Bracket (Left)"));
		
		
		keyList.add(new KeyConvert(Key.UNDEFINED, KeyInput.KEY_UNLABELED,"(Unknown Key)"));
		keyList.add(new KeyConvert(Key.ESCAPE, KeyInput.KEY_ESCAPE,"Escape"));
		keyList.add(new KeyConvert(Key.LEFT, KeyInput.KEY_LEFT,"Left"));
		keyList.add(new KeyConvert(Key.RIGHT, KeyInput.KEY_RIGHT,"Right"));
		keyList.add(new KeyConvert(Key.UP, KeyInput.KEY_UP,"Up"));
		keyList.add(new KeyConvert(Key.DOWN, KeyInput.KEY_DOWN,"Down"));
		keyList.add(new KeyConvert(Key.SHIFT_LEFT, KeyInput.KEY_LSHIFT,"Shift (L)"));
		keyList.add(new KeyConvert(Key.SHIFT_RIGHT, KeyInput.KEY_RSHIFT,"Shift (R)"));
		keyList.add(new KeyConvert(Key.CTRL_RIGHT, KeyInput.KEY_RCONTROL,"Control (R)"));
		keyList.add(new KeyConvert(Key.CTRL_LEFT, KeyInput.KEY_LCONTROL,"Control (L)"));
		keyList.add(new KeyConvert(Key.ALT_LEFT, KeyInput.KEY_LMENU,"Alt (L)"));
		keyList.add(new KeyConvert(Key.ALT_RIGHT, KeyInput.KEY_RMENU,"Alt (R)"));
		
		keyList.add(new KeyConvert(Key.ENTER, KeyInput.KEY_RETURN,"Enter"));
		keyList.add(new KeyConvert(Key.INSERT, KeyInput.KEY_INSERT,"Insert"));
		keyList.add(new KeyConvert(Key.DELETE, KeyInput.KEY_DELETE,"Delete"));
		keyList.add(new KeyConvert(Key.HOME, KeyInput.KEY_HOME,"Home"));
		keyList.add(new KeyConvert(Key.END, KeyInput.KEY_END,"End"));
		keyList.add(new KeyConvert(Key.PAGE_UP, KeyInput.KEY_PGUP,"Page Up"));
		keyList.add(new KeyConvert(Key.PAGE_DOWN, KeyInput.KEY_PGDN,"Page Down"));
		keyList.add(new KeyConvert(Key.BACKSPACE, KeyInput.KEY_BACK,"Backspace"));
		
		keyList.add(new KeyConvert('\t', Key.TAB, KeyInput.KEY_TAB,"Tab"));
		keyList.add(new KeyConvert(Key.META_LEFT, KeyInput.KEY_LWIN, PlatformManager.get().getNameForKey(KeyInput.KEY_LWIN).toUpperCase() + "_LEFT", PlatformManager.get().getNameForKey(KeyInput.KEY_LWIN) + " (L)"));
		keyList.add(new KeyConvert(Key.META_RIGHT, KeyInput.KEY_RWIN,PlatformManager.get().getNameForKey(KeyInput.KEY_RWIN).toUpperCase() + "_RIGHT",PlatformManager.get().getNameForKey(KeyInput.KEY_RWIN) + " (R)"));
		keyList.add(new KeyConvert(Key.CAPS, KeyInput.KEY_CAPITAL, "Caps Lock"));
		
		keyList.add(new KeyConvert(Key.F1, KeyInput.KEY_F1));
		keyList.add(new KeyConvert(Key.F2, KeyInput.KEY_F2));
		keyList.add(new KeyConvert(Key.F3, KeyInput.KEY_F3));
		keyList.add(new KeyConvert(Key.F4, KeyInput.KEY_F4));
		keyList.add(new KeyConvert(Key.F5, KeyInput.KEY_F5));
		keyList.add(new KeyConvert(Key.F6, KeyInput.KEY_F6));
		keyList.add(new KeyConvert(Key.F7, KeyInput.KEY_F7));
		keyList.add(new KeyConvert(Key.F8, KeyInput.KEY_F8));
		keyList.add(new KeyConvert(Key.F9, KeyInput.KEY_F9));
		keyList.add(new KeyConvert(Key.F10, KeyInput.KEY_F10));
		keyList.add(new KeyConvert(Key.F11, KeyInput.KEY_F11));
		keyList.add(new KeyConvert(Key.F12, KeyInput.KEY_F12));

		
	}
	

	
	public static class KeyConvert
	{
			
		private final int fengGUIkey;
		private final int jMEKey;
		private final org.fenggui.event.key.Key fengGUIkeyClass;
		private final String keyName;
		private final String niceName;
		
		private KeyConvert(int fengGUIkey, org.fenggui.event.key.Key fengGUIkeyClass, int jMEKey, String keyName, String niceName)
        {
        	this.fengGUIkeyClass = fengGUIkeyClass;
        	this.fengGUIkey = fengGUIkey;
        	this.jMEKey = jMEKey;
        	this.keyName = keyName;
        	if(niceName == "" || niceName == null)
        		this.niceName = keyName;
        	else
        		this.niceName = niceName;
        }
		
		public String getNiceName() {
			return niceName;
		}

		private KeyConvert(int fengGUIkey, org.fenggui.event.key.Key fengGUIkeyClass, int jMEKey, String keyName)
        {
        	this.fengGUIkeyClass = fengGUIkeyClass;
        	this.fengGUIkey = fengGUIkey;
        	this.jMEKey = jMEKey;
        	this.keyName = keyName;
        	
        	this.niceName = keyName;
        
        }
		
		 private KeyConvert(int fengGUIkey, int jMEKey, String niceName)
	        {
	        	this( fengGUIkey,null,jMEKey,  String.valueOf((char)fengGUIkey).toUpperCase(),niceName);
	        }
		 
		 private KeyConvert(int fengGUIkey, int jMEKey, String properName, String niceName)
	        {
	        	this( fengGUIkey,null,jMEKey, properName,niceName);
	        }
		 
        private KeyConvert(int fengGUIkey, int jMEKey)
        {
        	this( fengGUIkey,null,jMEKey,  String.valueOf((char)fengGUIkey).toUpperCase());
        }
		
        private KeyConvert(org.fenggui.event.key.Key fengGUIkeyClass, int jMEKey, String niceName)
        {
        	this(-1,fengGUIkeyClass,jMEKey,fengGUIkeyClass.toString(),niceName);
        }
        
        private KeyConvert(org.fenggui.event.key.Key fengGUIkeyClass, int jMEKey, String properName, String niceName)
        {
        	this(-1,fengGUIkeyClass,jMEKey,properName,niceName);
        }
        
        
        private KeyConvert(org.fenggui.event.key.Key fengGUIkeyClass, int jMEKey)
        {
        	this(-1,fengGUIkeyClass,jMEKey,fengGUIkeyClass.toString());
        }

		public int getFengGUIkey() {
			return fengGUIkey;
		}

		public int getJMEKey() {
			return jMEKey;
		}

		public org.fenggui.event.key.Key getFengGUIkeyClass() {
			return fengGUIkeyClass;
		}
		
	      public String getKeyName() {
				return keyName;
			}
        
        
	}

	/**
	 * Nicer, easier to read key descriptions (but sometimes with less information)
	 * @param keyCode
	 * @return
	 */
	public static String getSimpleKeyDescription(int jmeKey) {
			if (jmeKey<=0)
				return "(No Key)";
			for (KeyConvert convert:keyList)
			{
				if(convert.getJMEKey()== jmeKey)
				{
					return convert.getNiceName();
				}
			}
			return "(Unknown Key)";
		}
}
