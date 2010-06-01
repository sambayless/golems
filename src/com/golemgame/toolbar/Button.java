package com.golemgame.toolbar;


public interface Button {

	public enum ButtonState{HOVER,ACTIVE, INACTIVE, ANTI_HOVER};
	
	public void addButtonListener(ButtonListener listener);
	public void removeButtonListener(ButtonListener listener);

	
}
