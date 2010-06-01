package com.golemgame.mvc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.jme.math.Vector2f;


public final class LongType extends DataType{
	private static final long serialVersionUID = 1L;
	public static final int FILE_VERSION = 1;
	private long value;

	public final Type getType() {
		return Type.LONG;
	}

	public LongType() {
		super();
	}

	public LongType(long value) {
		super();
		this.value = value;
	}
	public LongType(DataInputStream input,int[]versionMap) throws IOException
	{
		this();
		value = input.readLong();
	}
	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
	
	public void setValue(Object value) throws IncompatibleValueException {
		if(value instanceof Long)
			this.setValue(((Long)value).longValue());
		else
			throw new IncompatibleValueException();
	}
	public DataType deepCopy() {
		return new LongType(this.value);
	}
	public void write(DataOutputStream output) throws IOException {
		super.write(output);
		output.writeLong(value);
	}
	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (value ^ (value >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LongType other = (LongType) obj;
		if (value != other.value)
			return false;
		return true;
	}

	@Override
	public int getFileVersion() {
		return FILE_VERSION;
	}
	
}
