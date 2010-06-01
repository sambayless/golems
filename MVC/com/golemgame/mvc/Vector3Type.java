package com.golemgame.mvc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;

public class Vector3Type extends DataType {
	private static final long serialVersionUID = 1L;
	public static final int FILE_VERSION = 1;
	private Vector3f value;
	
	
	
	public Vector3Type() {
		super();
		value = new Vector3f();
	}

	public Vector3Type(Vector3f value) {
		super();
		this.value = value;
	}
	public Vector3Type(DataInputStream input,int[]versionMap) throws IOException
	{
		this();
		value.x = input.readFloat();
		value.y = input.readFloat();
		value.z = input.readFloat();
	
	}
	public Type getType() {
		return Type.VECTOR3;
	}

	public Vector3f getValue() {
		return value;
	}

	public void setValue(Vector3f value) {
		this.value = value;
	}

	public void setValue(Object value) throws IncompatibleValueException {
		if(value instanceof Vector3f)
			this.setValue((Vector3f) value);
		else
			throw new IncompatibleValueException();
	}
	public DataType deepCopy() {
		return new Vector3Type(new Vector3f(this.value));
	}
	public void write(DataOutputStream output) throws IOException {
		super.write(output);
		if(value == null)
		{
			output.writeFloat(0);
			output.writeFloat(0);
			output.writeFloat(0);
		}else
		{
			output.writeFloat(value.x);
			output.writeFloat(value.y);
			output.writeFloat(value.z);
		}
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
		Vector3Type other = (Vector3Type) obj;
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
