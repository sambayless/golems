package com.golemgame.mechanical;

import java.io.Serializable;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.machineSettings.MachineSpaceSettingsInterpreter;
import com.jme.math.Vector3f;

public class GravitySettings  implements SustainedView, Serializable{

	private static final long serialVersionUID = 1L;
	private MachineSpaceSettingsInterpreter interpreter;

	
	public GravitySettings() {
		super();
	}
	
	public Vector3f getGravity()
	{
		return getDirectionOfGravity().mult(getMagnitude());
	}
	
	public Vector3f getDirectionOfGravity() {
		return interpreter.getGravityDirection();
	}
	public void setDirectionOfGravity(Vector3f directionOfGravity) {
	
		interpreter.getGravityDirection().set(directionOfGravity);
	

	}
	public float getMagnitude() {
		return interpreter.getGravityMagnitude();
	}
	public void setMagnitude(float magnitude) {
		
		interpreter.setGravityMagnitude(magnitude);

	}

	public void setStore(PropertyStore store) {
		interpreter = new  MachineSpaceSettingsInterpreter(store);
		
	}

	public void invertView(PropertyStore store) {

		
	}
	
	public void remove() {

	}
	public void refresh() {

	}

	public PropertyStore getStore() {
		return interpreter.getStore();
	}
	
	

	
	
}
