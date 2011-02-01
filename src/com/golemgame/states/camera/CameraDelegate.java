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
package com.golemgame.states.camera;

import java.io.Serializable;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

public interface CameraDelegate extends Serializable{
	public Vector3f getCameraTranslation();
	public Quaternion getCameraRotation();
	public Vector3f getStandardTranslation();
	public Quaternion getStandardRotation();
	public float getZoom();
	public void setZoom(float zoom);
	public void updateCamera();
	public void engageCamera(Camera toEngage);
	public void disengageCamera();
	public float getStandardZoom();
	
	/**
	 * Get the centroid that the camera rotates/moves around. The meaning of this depends on the camera.
	 * A camera may return null to indicate that it has no centroid.
	 * @return
	 */
	public Vector3f getCentroid();
	
	/**
	 * Set this delegates zoom, translation, etc, to match the given delegate
	 */
	public void set(CameraDelegate delegate);
	
	/**
	 * Determine if this camera should be considered a replacement for the given camera.
	 * (Always returns true if this camera equal to compareTo).
	 * This is mainly used to keep the view the same when switching between States.
	 * @param compareTo
	 * @return
	 */
	public boolean isSimilar(CameraDelegate compareTo);
}
