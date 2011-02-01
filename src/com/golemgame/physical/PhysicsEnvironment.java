/*******************************************************************************
 * Copyright 2008, 2009, 2010 Sam Bayless.
 * 
 *     This file is part of Golems.
 * 
 *     Golems is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Golems is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Golems. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.golemgame.physical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.golemgame.mvc.Reference;
import com.golemgame.states.camera.CameraDelegate;
import com.jphya.scene.Scene;


/**
 * This contains relevant information about the non-physical environment, 
 * and mediates between the (strictly physical) environment, and other aspects such as
 * cameras, or auxiliary information, such as the set of cameras, or the set of machines,
 * in the current environment.
 * For example, the current camera set. 
 * The machine space holds a reference to the design environment, 
 * and on compilation a compilation environment is made available.
 * @author Sam
 *
 */
public class PhysicsEnvironment {

	private static final long serialVersionUID = 1L;
	
	private  LinkedHashMap<Reference,CameraDelegate> cameraMap = new LinkedHashMap<Reference,CameraDelegate>();
	
	private Collection<EnvironmentListener> environmentListeners = new CopyOnWriteArrayList<EnvironmentListener>();
	
	private Scene soundScene;
	
	private boolean showSensorFields = false;

	public void setSoundScene(Scene soundScene) {
		this.soundScene = soundScene;
	}

	public Scene getSoundScene() {
		return soundScene;
	}

	public PhysicsEnvironment() {
		super();

	}

	
	public void removeCameraDelegate(CameraDelegate delegate) {
		if(this.cameraMap.values().remove(delegate))
		{
			changeOccured();
		}
		
	}

	private void changeOccured() {
		for(EnvironmentListener listener:this.environmentListeners)
			listener.environmentChanged(this);
	}

	
	public void addCameraDelegate(CameraDelegate delegate,Reference reference) {
		if(!cameraMap.containsKey(reference))	
		{
			cameraMap.put(reference, delegate);
			changeOccured();
		}
		
	}

	public void setCameraDelegate(CameraDelegate camera)
	{
		
	}

	
	public void addEnvironmentListener(EnvironmentListener listener) {
		this.environmentListeners.add(listener);
		
	}

	
	public List<CameraDelegate> getCameras() {
		ArrayList<CameraDelegate> list = new ArrayList<CameraDelegate>();
		for(Reference key:cameraMap.keySet())
			list.add(cameraMap.get(key));
		return list;
	}

	
	
	public boolean isShowSensorFields() {
		return showSensorFields;
	}

	public void setShowSensorFields(boolean showSensorFields) {
		if(showSensorFields!=this.showSensorFields)
		{
			this.showSensorFields = showSensorFields;
			changeOccured();
		}
		
	}

	
}
