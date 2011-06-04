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
package com.golemgame.audio.dummy;

import java.util.concurrent.Callable;

import com.golemgame.audio.AudioManager;
import com.golemgame.audio.AudioSource;
import com.jphya.audio.AudioOutputStream;
import com.jphya.audio.DummyAudioOutputStream;

public class DummyAudioManager extends AudioManager{

	public DummyAudioManager() {
		super();
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public void setEnabled(boolean enabled) {
		
	}

	@Override
	public <E> E executeInAudio(Callable<E> r) {
		return null;
	}

	@Override
	public AudioSource createAudioSource() {
		return new DummySoundSource();
	}

	@Override
	public AudioOutputStream createSoundStream(AudioSource source) {
		return new DummyAudioOutputStream();
	}

	@Override
	public float getVolume() {
		return 0;
	}

	@Override
	public void setVolume(float vol) {
		
	}

	@Override
	public void setMute(Boolean mute) {

	}

	@Override
	public boolean isMute() {
		return true;
	}

}
