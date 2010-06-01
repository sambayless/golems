package com.golemgame.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferInputStream extends InputStream{
	private ByteBuffer buffer; 

	public ByteBufferInputStream(ByteBuffer buffer) {
		super();
		this.buffer = buffer;
	}

	@Override
	public int read() throws IOException {
		return buffer.get();
	}
}
