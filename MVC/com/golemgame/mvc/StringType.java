package com.golemgame.mvc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.golemgame.mvc.DataType.Type;
import com.jme.math.Quaternion;

public final class StringType extends DataType{
	private static final long serialVersionUID = 1L;
	public static final int FILE_VERSION = 1;
	private static final String NULL_STRING = "";

	private String value;

	public final Type getType() {
		return Type.STRING;
	}

	public StringType() {
		super();
	}

	public StringType(String value) {
		super();
		this.value = value;
	}
	public StringType(DataInputStream input,int[]versionMap) throws IOException
	{
		this();
		value = input.readUTF();
	}
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	public void setValue(Object value) throws IncompatibleValueException {
		if(value instanceof String)
			this.setValue((String) value);
		else
			throw new IncompatibleValueException();
	}
	
	public DataType deepCopy() {
		return new StringType(this.value);
	}
	public void write(DataOutputStream output) throws IOException {
		super.write(output);
		if(value == null)
			output.writeUTF(NULL_STRING);
		else
			output.writeUTF(value);
	}
	
	@Override
	public String toString() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		StringType other = (StringType) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public int getFileVersion() {
		return FILE_VERSION;
	}
	
}
