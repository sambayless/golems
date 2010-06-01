package com.golemgame.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ByteBufferOutputStream extends OutputStream{

	private ByteBuffer buffer; 

	public ByteBufferOutputStream(ByteBuffer buffer) {
		super();
		this.buffer = buffer;
	}

	public ByteBufferOutputStream() {
		super();
		this.buffer = ByteBuffer.allocate(64);
	}
	
	@Override
	public void write(int b) throws IOException {
		if (buffer.position()>= buffer.capacity())
		{
			ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity()*2);
			buffer.flip();
			newBuffer.put(buffer);
			
			buffer = newBuffer;
		}
		buffer.put((byte) b);
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

	
}
