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
package com.golemgame.audio.al;

import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import com.golemgame.audio.AudioSource;
import com.jme.math.Vector3f;

public class ALAudioSource implements AudioSource {
	/**
	 * This int buffer is acting as a c-style pointer to the audio source
	 */
	private final int sourcePointer;
	public int getSourcePointer() {
		return sourcePointer;
	}

	private Vector3f location = new Vector3f();
	private AtomicBoolean playing = new AtomicBoolean(false);
	private final ALAudioManager audioManager;
	
	ALAudioSource( ALAudioManager audioManager) {
		super();
		this.audioManager = audioManager;
		
		AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE);
		IntBuffer sourcePointer = BufferUtils.createIntBuffer(1);
		AL10.alGenSources(sourcePointer);//should add error catching later.
		this.sourcePointer = sourcePointer.get(0);
		// AL10.alSourcei(alSources.get(0), AL10.AL_BUFFER, buffers.get(0));

	
	}
	
	/* (non-Javadoc)
	 * @see com.golemgame.states.audio.AudioSource#setPlaying(boolean)
	 */
	public void setPlaying(boolean playing)
	{
		if(this.playing.compareAndSet(!playing, playing))
		{
			if(playing)
			{
				AL10.alSourcePlay(sourcePointer);
			}else{
				AL10.alSourcePause(sourcePointer);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.golemgame.states.audio.AudioSource#setLocation(com.jme.math.Vector3f)
	 */
	public void setLocation(Vector3f location)
	{
		this.location.set(location);
		update();
	}

	private void update() {
		//later
	}
	
	

}
