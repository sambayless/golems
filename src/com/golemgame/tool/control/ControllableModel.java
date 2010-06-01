package com.golemgame.tool.control;

import java.io.Serializable;

import com.golemgame.model.Model;
import com.golemgame.mvc.PropertyState;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.SpatialInterpreter;
import com.jme.math.Vector3f;

public interface ControllableModel extends Serializable{
/*	*//**
	 * Notify the model that a period of adjustments has been completed, and the model should update itself, 
	 * and perform any collision checking, as needed.
	 */
	public void updateModel();
	
	public Model getParent();
	
	public Vector3f getWorldFromRelative(Vector3f localTranslation, Vector3f store);
	public Vector3f getRelativeFromWorld(Vector3f worldTranslation, Vector3f store);
	public void updateWorldData();
	public SpatialInterpreter getInterpreter();
	
	public PropertyStore getPropertyStore();
	
	public PropertyState getCurrentState();
}
