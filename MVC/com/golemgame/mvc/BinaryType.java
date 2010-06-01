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
