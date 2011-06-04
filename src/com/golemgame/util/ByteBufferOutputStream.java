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
