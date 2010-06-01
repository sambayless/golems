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
