package com.golemgame.instrumentation;

import org.fenggui.composite.Window;

import com.jme.scene.Node;

/**
 * A component that is viewable in the instrumentation layer.
 * 
 * It may define a fenggui component, and it may define a jme component. 
 * All components will be visible only when the instrumentation layer is visible.
 * 
 * @author Sam
 *
 */
public interface Instrument {
	public Window getInstrumentInterface();
	public void attachSpatial(Node attachTo);
	
	/**
	 * A unified update point for the instrument
	 * @param tpf
	 */
	public void update(float tpf);
	public String getName();
	public void attach(InstrumentationLayer instrumentLayer);
	public void setName(String name);
	/**
	 * Whether or not to display the window around the instrument 
	 * (as opposed to having the appearance of being windowless)
	 * 
	 * @param windowed
	 */
	public void setWindowed(boolean windowed);
	public boolean isWindowed();
	public boolean isLocked();
	public void setLocked(boolean locked);
	public boolean isUserPositioned();
	public void setUserPositioned(boolean positioned);
}
