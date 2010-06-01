package com.golemgame.mechanical;

import java.io.Serializable;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.machineSettings.MachineSpaceSettingsInterpreter;



public class MachineSpaceSettings implements SustainedView, Serializable{

	private static final long serialVersionUID = 1L;

	private MachineSpaceSettingsInterpreter interpreter;
	private GravitySettings gravitySettings = new GravitySettings();
	private GridSettings gridSettings = new GridSettings();
	private BuoyancySettings buoyancySettings = new BuoyancySettings();

	
	public BuoyancySettings getBuoyancySettings() {
		return buoyancySettings;
	}
	public GravitySettings getGravitySettings() {
		return gravitySettings;
	}
	public GridSettings getGridSettings() {
		return gridSettings;
	}

	public MachineSpaceSettings(PropertyStore store) {
		super();
		interpreter = new MachineSpaceSettingsInterpreter(store);
		gravitySettings.setStore(store);
		gridSettings.setStore(store);
	}
	public void invertView(PropertyStore store) {
		store.set(getStore());
		
	}
	public void refresh() {
		gravitySettings.refresh();
		gridSettings.refresh();
		
	}
	
	public void remove() {

	}
	public PropertyStore getStore() {

		return interpreter.getStore();
	}
	
}
