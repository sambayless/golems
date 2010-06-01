package com.golemgame.instrumentation;

import java.util.concurrent.CopyOnWriteArrayList;

import org.fenggui.composite.Window;

import com.jme.input.KeyInputListener;
import com.jme.scene.Node;

/**
 * A keybaord instrument is different from a standard instrument in that it doesnt have a standard graphics output
 * 
 * probably want to create a general keyboard instrument that handles assigning keys to keybarodcontrollers, and that also 
 * supplies a graphical display for the whole keyboard.
 * @author Sam
 *
 */
public class KeyboardController implements Instrument{
	private boolean visible = false;
	private String name = "Control";
	private int keyCode = 0;
	private boolean locked = false;
	private boolean userPositioned = false;
	public boolean isUserPositioned() {
		return userPositioned;
	}

	public void setUserPositioned(boolean userPositioned) {
		this.userPositioned = userPositioned;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isLocked() {
		return locked;
	}
	public int getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}

	private final CopyOnWriteArrayList<KeyboardControlListener> listeners = new CopyOnWriteArrayList<KeyboardControlListener>();
	public void setWindowed(boolean windowed) {
		
	}
	public KeyboardController(int keyCode) {
		// TODO Auto-generated constructor stub
	}

	public void attachSpatial(Node attachTo) {
		// TODO Auto-generated method stub
		
	}

	public Window getInstrumentInterface() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return name;
	}
	public boolean isWindowed() {
		return false;
	}
	public void setName(String name) {
		this.name = name;
	}

	
	public void update(float tpf) {
		
		
	}

	public void registerListener(KeyboardControlListener listener) {
		listeners.add(listener);
	}

	public void attach(InstrumentationLayer instrumentLayer) {
		instrumentLayer.attachKeyListener(new KeyInputListener(){
			private boolean toggle = false;
			public void onKey(char character, int keyCode, boolean pressed) {
				if(keyCode == KeyboardController.this.keyCode)
				{
					if(pressed)
						for(KeyboardControlListener l:listeners)
							l.keyPress();
					else
						for(KeyboardControlListener l:listeners)
							l.keyRelease();
				}
			}
			
		});
	}

}
