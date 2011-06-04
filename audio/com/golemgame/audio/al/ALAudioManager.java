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

import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;

import com.golemgame.audio.AudioManager;
import com.golemgame.audio.AudioSource;
import com.golemgame.audio.AudioStateListener;
import com.golemgame.states.GeneralSettings;
import com.golemgame.states.StateManager;
import com.golemgame.util.BlockingExecutorService;
import com.golemgame.util.ManualExecutorService;
import com.jphya.audio.AudioOutputStream;
import com.jphya.lwjgl.LWGJLStream;

/**
 * This class replaces jme's audio system
 * 
 * @author Sam
 * 
 */
public class ALAudioManager extends AudioManager {
	private CopyOnWriteArrayList<AudioStateListener> audioStateListeners = new CopyOnWriteArrayList<AudioStateListener>();
	private AtomicBoolean closed = new AtomicBoolean(false);
	private ManualExecutorService exec = new BlockingExecutorService();
	private boolean mute = false;
	private Thread soundThread;
	public ALAudioManager() {
		try {
			soundThread = new Thread(new Runnable(){

				public void run() {
					while(!closed.get() &&! exec.isShutdown())
					{
						exec.manualExecuteAll();
					}
				}
				
			},"Main Audio Thread");
			soundThread.setDaemon(true);
			soundThread.start();
			init();
		} catch (LWJGLException e) {
			StateManager.logError(e);
		}
	}
	private float tempVol = 0f;
	
	@Override
	public float getVolume() {
		this.executeInAudio(new Callable<Float>(){
			public Float call() throws Exception {
				tempVol = 	AL10.alGetListenerf(AL10.AL_GAIN);
				return null;
			}});
		
		return tempVol;//avoid object creation
	}

	@Override
	public void setVolume(final float vol) {
		this.executeInAudio(new Callable<Object>(){
			public Object call() throws Exception {
				if(!mute)
				 	AL10.alListenerf(AL10.AL_GAIN, vol*vol);//make this nonlinear (sounds more natural)
				else
					AL10.alListenerf(AL10.AL_GAIN, 0f);
				return null;
			}
		});
	}

	private void init() throws LWJGLException {
		this.executeInAudio(new Callable<Object>(){

	
			public Object call() throws Exception {
				try {
					AL.create();
				} catch (LWJGLException e) {
					StateManager.logError(e);
				}
				return null;
			}
			
		});
	
	}

	public void close() {
		if(!closed.compareAndSet(false, true))
			return;
		
		try{
			for(AudioStateListener listener:audioStateListeners)
			{
				try{
					listener.close();
				}catch(Exception e)
				{
					StateManager.logError(e);
				}
			}
		}finally{
			this.exec.shutdown();
			
		    if(AL.isCreated()) {
			      AL.destroy();
			 }
		}
	}

	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	
	@Override
	public ALAudioSource createAudioSource() {
		
		return new ALAudioSource(this);
	}
	
	@Override
	public AudioOutputStream createSoundStream(final AudioSource source) {
	/*	return this.executeInAudio(new Callable<AudioOutputStream>(){
			public AudioOutputStream call() throws Exception {
				return new LWGJLStream(((ALAudioSource)source).getSourcePointer()){
					
					private ByteBuffer toWrite;
					private int frames;
					
						private Callable<Object> writeSamples = new Callable<Object>(){
	
							public Object call() throws Exception {
								privateWriteSamples(toWrite, frames);
								return null;
							}
							
						};
						
					private void privateWriteSamples(ByteBuffer start, int frames) 
					{
						super.writeSamples(start, frames);
					}
					
					@Override
					public void writeSamples(ByteBuffer start, int frames) {
						toWrite = start;
						this.frames =frames;
						
						StateManager.getAudioManager().executeInAudio(writeSamples);
						
					}
					
			
				};
			}
		});*/
	
		//lets assume writing to the buffer is thread safe
		
		return this.executeInAudio(new Callable<AudioOutputStream>(){
			public AudioOutputStream call() throws Exception {
				LWGJLStream stream = new LWGJLStream(((ALAudioSource)source).getSourcePointer());
				stream.setSamplerate(8000);
				return stream;
				}
			});
	}
	

	@Override
	public void setMute(Boolean mute) {
		this.mute = mute;
		if(mute)
		{
			setVolume(0);
		}else
			setVolume(GeneralSettings.getInstance().getVolume().getValue());
	}

	@Override
	public boolean isMute() {
		return mute;
	}
	

	@Override
	public boolean isEnabled() {
		return AL.isCreated();
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.executeInAudio(new Callable<Object>(){
			public Object call() throws Exception {
				if(!AL.isCreated())
					try {
						AL.create();
					} catch (LWJGLException e) {
						StateManager.logError(e);
					}
				return null;
			}			
		});
	}
	

	@Override
	public <E> E executeInAudio(Callable<E> r) {
		try {
			return this.exec.submit(r).get();
		} catch (InterruptedException e) {
			StateManager.logError(e);
		} catch (ExecutionException e) {
			StateManager.logError(e);
		} 
		/*try {
			return r.call();
		} catch (Exception e) {
			StateManager.logError(e);
		}*/
		return  null;
	}
}
