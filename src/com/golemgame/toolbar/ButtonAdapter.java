package com.golemgame.toolbar;

import com.golemgame.toolbar.Button.ButtonState;

public class ButtonAdapter implements ButtonListener {

	
	public void buttonStateChange(ButtonState state) {
		switch(state)
		{
			case ACTIVE:
				activate();break;
			case INACTIVE:
				inactive();break;
			case HOVER:
				hover();break;
			case ANTI_HOVER:
				anti_hover();break;
				
		}

	}

	public void activate() {
		
	}
	public void inactive() {
		
	}
	public void hover() {
		
	}
	public void anti_hover() {
		
	}
	
}
