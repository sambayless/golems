package com.golemgame.audio.dummy;


import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.jphya.audio.AudioOutputStream;

public class DummyAudioOutputStream implements AudioOutputStream{
	public DummyAudioOutputStream() {
		super();

	}
	public void close() {

	}

	public void writeSamples(ByteBuffer start, int frames) {
		
/*		if(start.asFloatBuffer().get(64)!=0f)
		{
		float[] samples = new float[frames];
		start.asFloatBuffer().get(samples);
		System.out.println(Arrays.toString(samples));
		}*/
	}

	public void setSource(IntBuffer source) {
	
	}
	public int getSampleRate() {
		return 1;
	}
  
}
