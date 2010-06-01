package com.golemgame.audio;

import com.jme.math.Vector3f;

public interface AudioSource {

	public abstract void setPlaying(boolean playing);

	public abstract void setLocation(Vector3f location);

}