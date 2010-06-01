package com.golemgame.toolbar;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.simplemonkey.Container;



public class ButtonGroup extends Container implements Button{
	private List<ButtonListener> listeners = new CopyOnWriteArrayList<ButtonListener>();
	
	private ButtonState state = ButtonState.INACTIVE;
	
	public ButtonGroup() {
		super();
	}
	
	
	public void addButtonListener(ButtonListener listener) {
		listeners.add(listener);
	}

	
	public void removeButtonListener(ButtonListener listener) {
		listeners.remove(listener);
	}

	
	public boolean mouseMove(float x, float y) {
		if(this.state != ButtonState.HOVER && this.state != ButtonState.ACTIVE)
			setState(ButtonState.HOVER);
		return super.mouseMove(x, y);
	
	}

	
	public void mouseOff() {
		if(this.state == ButtonState.HOVER)
			setState(ButtonState.ANTI_HOVER);
		super.mouseOff();
	}

	
	public boolean mousePress(boolean pressed, int button, float x, float y) {
		if(pressed && button == 0)
		{
			this.setState( ButtonState.ACTIVE);
		}else if(button == 0)
		{
			this.setState(ButtonState.INACTIVE);
		}
		
		
		return super.mousePress(pressed, button, x, y);
	}	
	
	private void setState(ButtonState state)
	{
		this.state = state;
		for (ButtonListener listener:this.listeners)
			listener.buttonStateChange(state);
	}
}
