package com.golemgame.mechanical;

import java.io.Serializable;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.machineSettings.MachineSpaceSettingsInterpreter;
import com.jme.math.Vector3f;

public class GridSettings  implements SustainedView, Serializable{

	private static final long serialVersionUID = 1L;
	
	private MachineSpaceSettingsInterpreter interpreter;
	public Vector3f getGridSpacing() {
		return interpreter.getGrids();
	}

	public void setGridSpacing(Vector3f gridSpacing) {
		this.interpreter.getGrids().set(gridSpacing);
	
	}
	
	public void setStore(PropertyStore store) {
		interpreter = new  MachineSpaceSettingsInterpreter(store);
		
	}
	
	public void remove() {

	}
	public GridSettings() {
		super();
	}

	public void invertView(PropertyStore store) {
		
		
	}

	public void refresh() {

	}

	public PropertyStore getStore() {
		return interpreter.getStore();
	}
	
	
}
