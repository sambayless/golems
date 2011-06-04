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
package com.golemgame.mvc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BinaryType extends DataType{

	private static final long serialVersionUID = 1L;

	private final ByteBuffer buffer;
	
	public static final int FILE_VERSION = 1;
	@Override
	public DataType deepCopy() {
		return null;
	}

	public BinaryType() {
		this(ByteBuffer.allocate(0).order(ByteOrder.nativeOrder()));
	}
	
	public BinaryType(ByteBuffer buffer)
	{
		this.buffer = buffer;
	}

	@Override
	public Type getType() {
		return Type.BINARY;
	}

	public BinaryType(DataInputStream input,int[]versionMap) throws IOException
	{
		super();
		int size = input.readInt();
		buffer = ByteBuffer.allocate(size);

		int n = input.read(buffer.array());
		if (n != size)
			throw new IOException("Expected " + size + "bytes, read only "+ n);
	
	}
	
	public ByteBuffer getBuffer() {
		return buffer;
	}

	@Override
	public void write(DataOutputStream output) throws IOException {
		super.write(output);
		buffer.compact();
		if(buffer.hasArray())
		{
			output.writeInt(buffer.array().length);
			output.write(buffer.array());
		}
		else
		{
			output.writeInt(0);
		}
	}

	@Override
	public int getFileVersion() {
		return FILE_VERSION;
	}

	public byte[] getArray() {
		if(buffer.hasArray())
			return buffer.array();
		else
		{
			byte[] data = new byte[buffer.capacity()];
			buffer.get(data);
			return data;
		}
	}
	
}
