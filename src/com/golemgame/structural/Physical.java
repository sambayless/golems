package com.golemgame.structural;

import java.io.Serializable;


public interface Physical extends Serializable{

	
	/**
	 * Used mainly by cameras, and other objects that are not really physical, but need to determine 
	 * which physics object they are attached to.
	 * @return
	 */
	public boolean isPropagating();
	
	public boolean isStatic();
	public void setStatic(boolean isStatic);

}
