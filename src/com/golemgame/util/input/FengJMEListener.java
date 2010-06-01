package com.golemgame.util.input;

import org.fenggui.Display;
import org.fenggui.event.key.Key;
import org.fenggui.event.mouse.MouseButton;
import org.lwjgl.input.Keyboard;

import com.jme.input.KeyInputListener;
import com.jme.input.MouseInputListener;

/**
 * Listener version of the FengGUIInputHandler.
 * Subscribe this to KeyInput and MouseInput.
 * @author Joshua Keplinger
 */
public class FengJMEListener implements MouseInputListener, KeyInputListener {


	private Display disp;
	
	private boolean keyHandled;
	private boolean mouseHandled;
	
	public FengJMEListener(Display disp)
	{
		this.disp = disp;
	}
		
	public boolean wasKeyHandled()
	{
		return keyHandled;
	}
	
	public boolean wasMouseHandled()
	{
		return mouseHandled;
	}
		
	private int[] clickCount = new int[3];
	private boolean down;
	private int lastButton;
	private int lastX = -100;
	private int lastY = -100;
	
	public void onButton(int button, boolean pressed, int x, int y)
	{
		mouseHandled = false;
		down = pressed;
		lastButton = button;
		
		if (x == lastX && y == lastY)
		{
			if (pressed)
				clickCount[button]++;
		}else
		{
			lastX = x;
			lastY = y;
			
			
			for (int i = 0; i <clickCount.length;i++)
			{
				if (i== button && pressed)
					clickCount[button] = 1;
				else
					clickCount[i] = 0;
			}
		}
		
		
		if(pressed)
		{

			

			mouseHandled = disp.fireMousePressedEvent(x, y, getMouseButton(button), clickCount[button]);
		}
		else
		{
			
			mouseHandled = disp.fireMouseReleasedEvent(x, y, getMouseButton(button), clickCount[button]);
			
			if(clickCount[button]>0)
			{
		    	if (clickCount[button] % 2 == 0 )
				{
					boolean rtnVal = disp.fireMouseDoubleClickEvent(x, y, getMouseButton(button), clickCount[button]/2);
	
				}else    	
				{
					boolean rtnVal = disp.fireMouseClickEvent(x, y, getMouseButton(button), clickCount[button]);
					//display ALWAYS returns that this was handled...
				}
			}
		}
	}

	public void onMove(int xDelta, int yDelta, int newX, int newY)
	{
		mouseHandled = false;
		// If the button is down, the mouse is being dragged
		if(down)
			mouseHandled = disp.fireMouseDraggedEvent(newX, newY, getMouseButton(lastButton),0);
		else
			mouseHandled = disp.fireMouseMovedEvent(newX, newY,getMouseButton(lastButton),0);
	}

	public void onWheel(int wheelDelta, int x, int y)
	{
		mouseHandled = false;
		// wheelDelta is positive if the mouse wheel rolls up
		if(wheelDelta > 0)
			mouseHandled = disp.fireMouseWheel(x, y, true, wheelDelta,1);
		else
			mouseHandled = disp.fireMouseWheel(x, y, false, -wheelDelta,1);


	}
	
	/**
	 * Helper method that maps the mouse button to the equivalent
	 * FengGUI MouseButton enumeration.
	 * @param button The button pressed or released.
	 * @return The FengGUI MouseButton enumeration matching the
	 * button.
	 */
	private MouseButton getMouseButton(int button)
	{
		switch(button)
		{
			case 0:
				return MouseButton.LEFT;
			case 1:
				return MouseButton.RIGHT;
			case 2:
				return MouseButton.MIDDLE;
			default:
				return MouseButton.LEFT;
		}
	}

	
	public void onKey(char character, int keyCode, boolean pressed) {
			keyHandled = false;		
			if(character == '\r')//perhaps only on windows, enter is interpreted by lwjgl only as \n
				character = '\n';
			Key key = mapKeyEvent();
			if(pressed) 
			{
				keyHandled = disp.fireKeyPressedEvent(character, key);
				// Bug workaround see note after code - SAM: Apparently not needed
			//	if (key == Key.LETTER || key == Key.DIGIT)
					keyHandled = disp.fireKeyTypedEvent(character);
			} else
				keyHandled = disp.fireKeyReleasedEvent(character, key);
	}
	
	/**
	 * Helper method that maps LWJGL key events to FengGUI.
	 * @return The Key enumeration of the last key pressed.
	 */
	private Key mapKeyEvent()
	{
		Key keyClass;
		int key = Keyboard.getEventKey();
        switch(key) 
        {
	        case Keyboard.KEY_BACK:
	        	keyClass = Key.BACKSPACE;
	            break;
	        case Keyboard.KEY_RETURN:
	        	keyClass = Key.ENTER;
	            break;
	        case Keyboard.KEY_DELETE: 
	        	keyClass = Key.DELETE;
	            break;
	        case Keyboard.KEY_UP:
	        	keyClass = Key.UP;
	        	break;
	        case Keyboard.KEY_RIGHT:
	        	keyClass = Key.RIGHT;
	            break;
	        case Keyboard.KEY_LEFT:
	        	keyClass = Key.LEFT;
	            break;
	        case Keyboard.KEY_DOWN:
	        	keyClass = Key.DOWN;
	            break;
	        case Keyboard.KEY_SCROLL:
	        	keyClass = Key.SHIFT_LEFT;
	            break;
	        case Keyboard.KEY_LWIN:
	        	keyClass = Key.META_LEFT;
	        	break;
	        case Keyboard.KEY_RWIN:
	        	keyClass = Key.META_RIGHT;
	        	break;
	        case Keyboard.KEY_LMENU:
	        	keyClass = Key.ALT_LEFT;
	            break;
	        case Keyboard.KEY_RMENU:
	        	keyClass = Key.ALT_RIGHT;
	            break;
	        case Keyboard.KEY_LCONTROL:
	        	keyClass = Key.CTRL_LEFT;
	            break;
	        case Keyboard.KEY_RSHIFT:
	        	keyClass = Key.SHIFT_RIGHT;
	            break;     
	        case Keyboard.KEY_LSHIFT:
	        	keyClass = Key.SHIFT_LEFT;
	            break;              
	        case Keyboard.KEY_RCONTROL:
	        	keyClass = Key.CTRL_RIGHT;
	            break;
	        case Keyboard.KEY_INSERT:
	        	keyClass = Key.INSERT;
	            break;
	        case Keyboard.KEY_CAPITAL:
	        	keyClass = Key.CAPS;
	        	break;
	        case Keyboard.KEY_TAB:
	        	keyClass = Key.TAB;
	        	break;
	        case Keyboard.KEY_F12:
	        	keyClass = Key.F12;
	            break;
	        case Keyboard.KEY_F11:
	        	keyClass = Key.F11;
	            break;
	        case Keyboard.KEY_F10:
	        	keyClass = Key.F10;
	            break;
	        case Keyboard.KEY_F9:
	        	keyClass = Key.F9;
	            break;
	        case Keyboard.KEY_F8:
	        	keyClass = Key.F8;
	            break;
	        case Keyboard.KEY_F7:
	        	keyClass = Key.F7;
	            break;
	        case Keyboard.KEY_F6:
	        	keyClass = Key.F6;
	            break;
	        case Keyboard.KEY_F5:
	        	keyClass = Key.F5;
	            break;
	        case Keyboard.KEY_F4:
	        	keyClass = Key.F4;
	            break;
	        case Keyboard.KEY_F3:
	        	keyClass = Key.F3;
	            break;
	        case Keyboard.KEY_F2:
	        	keyClass = Key.F2;
	            break;
	        case Keyboard.KEY_F1:
	        	keyClass = Key.F1;
	            break;
	
	        case Keyboard.KEY_NUMPAD0:
	        	keyClass = Key.DIGIT;
	            break;
	        case Keyboard.KEY_NUMPAD1:
	        	keyClass = Key.DIGIT;
	            break;   
	        case Keyboard.KEY_NUMPAD2:
	        	keyClass = Key.DIGIT;
	            break;
	        case Keyboard.KEY_NUMPAD3:
	        	keyClass = Key.DIGIT;
	            break;
	        case Keyboard.KEY_NUMPAD4:
	        	keyClass = Key.DIGIT;
	            break;
	        case Keyboard.KEY_NUMPAD5:
	        	keyClass = Key.DIGIT;
	            break;
	        case Keyboard.KEY_NUMPAD6:
	        	keyClass = Key.DIGIT;
	            break;
	        case Keyboard.KEY_NUMPAD7:
	        	keyClass = Key.DIGIT;
	            break;
	        case Keyboard.KEY_NUMPAD8:
	        	keyClass = Key.DIGIT;
	            break;
	        case Keyboard.KEY_NUMPAD9:
	        	keyClass = Key.DIGIT;
	            break;
	            
	        case Keyboard.KEY_HOME:
	        	keyClass = Key.HOME;
	            break;
	        case Keyboard.KEY_END:
	        	keyClass = Key.END;
	            break;
	            
	        case Keyboard.KEY_NUMLOCK:
	        	keyClass = Key.NUMLOCK;
	            break;
	        
	        case Keyboard.KEY_PRIOR:
	        	keyClass = Key.PAGE_UP;
	            break;
	            
	        case Keyboard.KEY_NEXT:
	        	keyClass = Key.PAGE_DOWN;
	            break;
	            
	        default:
	        	if("1234567890".indexOf(Keyboard.getEventCharacter()) != -1) {
	        		keyClass = Key.DIGIT;
	        	} else { 
	        		// @todo must not necessarily be a letter!! #
	        		keyClass = Key.LETTER;
	        	}
	        	break;
    	}
		
        return keyClass;
	}
	
}
