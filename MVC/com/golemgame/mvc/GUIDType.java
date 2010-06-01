package com.golemgame.mvc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class GUIDType extends DataType {

	private static final long serialVersionUID = 1L;

	public static final int FILE_VERSION = 1;
	
	private UUID value;

	public final Type getType() {
		return Type.GUID;
	}

	public GUIDType() {
		super();
		value = UUID.randomUUID();
	}
	
	public GUIDType(DataInputStream input,int[]versionMap) throws IOException
	{
		this();
		
		String val = input.readUTF();
		value = UUID.fromString(val);
	}

	public GUIDType(UUID value) {
		super();		
		this.value = UUID.fromString(value.toString());
	}

	public UUID getValue() {
		return value;
	}

	public void setValue(UUID value) {
		this.value = value;
	}

	public void setValue(Object value) throws IncompatibleValueException {
		if(value instanceof Boolean)
			this.setValue(((Boolean)value).booleanValue());
		else
			throw new IncompatibleValueException();
	}

	public DataType deepCopy() {
		return new GUIDType(this.value);
	}

	public void write(DataOutputStream output) throws IOException {
		super.write(output);
		output.writeUTF(value.toString());		
	}

	@Override
	public String toString() {
		return String.valueOf(value);
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
		GUIDType other = (GUIDType) obj;
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
