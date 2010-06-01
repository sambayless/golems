package com.golemgame.toolbar;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.jme.math.Vector3f;
import com.simplemonkey.IResizeListener;
import com.simplemonkey.Widget;
import com.simplemonkey.util.Dimension;

public abstract class ButtonWidget extends Widget implements Button {
	private List<ButtonListener> listeners = new CopyOnWriteArrayList<ButtonListener>();
	
	private ButtonState state = ButtonState.INACTIVE;
	
	
	
	public ButtonWidget() {
		super();
		super.addResizeListener(new IResizeListener()
		{

			
			public void resize(Dimension newSize) {
				getSpatial().getLocalScale().set(getWidth(),getHeight(),1);
		
				getSpatial().updateGeometricState(0, true);
				getSpatial().updateRenderState();
			}
			
		});
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
		super.mouseMove(x, y);
		return true;
	}

	
	public void mouseOff() {
		
		
		if(this.state == ButtonState.HOVER)
			setState(ButtonState.ANTI_HOVER);
		else if (this.state == ButtonState.ACTIVE)
		{
			this.setState(ButtonState.INACTIVE);
			this.setState(ButtonState.ANTI_HOVER);
		}
		super.mouseOff();
	}

	
	public boolean mousePress(boolean pressed, int button, float x, float y) {
		if(pressed && button == 0)
		{
			this.setState( ButtonState.ACTIVE);
		}else if(button == 0)
		{
			if(super.testCollision(new Vector3f(x,y,0)))
			{
				this.setState(ButtonState.INACTIVE);
				this.setState(ButtonState.ANTI_HOVER);
			}else
				this.setState(ButtonState.INACTIVE);
			
		}
		
		super.mousePress(pressed, button, x, y);
		return true;
	}	
	
	private void setState(ButtonState state)
	{
		this.state = state;
		for (ButtonListener listener:this.listeners)
			listener.buttonStateChange(state);
	}

	



}
