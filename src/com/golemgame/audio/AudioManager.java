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