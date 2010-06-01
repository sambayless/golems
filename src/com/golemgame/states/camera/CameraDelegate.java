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
