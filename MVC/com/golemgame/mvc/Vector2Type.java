package com.golemgame.mvc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;

public class Vector2Type extends DataType {
	private static final long serialVersionUID = 1L;

	public static final int FILE_VERSION = 1;

	private Vector2f value;
	
	
	
	public Vector2Type() {
		super();
		value = new Vector2f();
	}

	public Vector2Type(Vector2f value) {
		super();
		this.value = value;
	}	
	
	public Vector2Type(DataInputStream input,int[]versionMap) throws IOException
	{
		this();
		value.x = input.readFloat();
		value.y = input.readFloat();
	}
	
	public Type getType() {
		return Type.VECTOR2;
	}

	public Vector2f getValue() {
		return value;
	}

	public void setValue(Vector2f value) {
		this.value = value;
	}
	public void setValue(Object value) throws IncompatibleValueException {
		if(value instanceof Vector2f)
			this.setValue((Vector2f) value);
		else
			throw new IncompatibleValueException();
	}
	public DataType deepCopy() {
		return new Vector2Type(new Vector2f(this.value));
	}
	public void write(DataOutputStream output) throws IOException {
		super.write(output);
		if(value == null)
		{
			output.writeFloat(0);
			output.writeFloat(0);
		}else
		{
			output.writeFloat(value.x);
			output.writeFloat(value.y);
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
		Vector2Type other = (Vector2Type) obj;
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
