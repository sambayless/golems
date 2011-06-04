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
package com.golemgame.audio;

import java.util.concurrent.Callable;

import com.jphya.audio.AudioOutputStream;

public abstract class AudioManager {

	public AudioManager() {
		super();
		
	}
	
	public abstract void setEnabled(boolean enabled);
	public abstract boolean isEnabled();
	
	public abstract AudioSource createAudioSource();

	public abstract void setVolume(float vol);
	public abstract float getVolume();
	
	public void close() {
	}
	public abstract AudioOutputStream createSoundStream(AudioSource source);

	public abstract void setMute(Boolean mute);
	public abstract boolean isMute();
	
	/**
	 * Blocking method to execute in the AL stream.
	 * @param <E>
	 * @param r
	 * @return
	 */
	public abstract <E> E executeInAudio(Callable<E> r);
}