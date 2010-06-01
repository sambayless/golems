package com.golemgame.mvc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.jme.math.Quaternion;

public class QuaternionType extends DataType {
	private static final long serialVersionUID = 1L;
	public static final int FILE_VERSION = 1;
	private static final Quaternion DEFAULT_VALUE = new Quaternion();

	private Quaternion value;
	
	
	
	public QuaternionType() {
		super();
		value = new Quaternion();
	}

	public QuaternionType(Quaternion value) {
		super();
		this.value = value;
	}
	public QuaternionType(DataInputStream input,int[]versionMap) throws IOException
	{
		this();
		value.w = input.readFloat();
		value.x = input.readFloat();
		value.y = input.readFloat();
		value.z = input.readFloat();
	}
	public Type getType() {
		return Type.QUATERNION;
	}

	public Quaternion getValue() {
		return value;
	}

	public void setValue(Quaternion value) {
		this.value = value;
	}
	public void setValue(Object value) throws IncompatibleValueException {
		if(value instanceof Quaternion)
			this.setValue((Quaternion) value);
		else
			throw new IncompatibleValueException();
	}
	public DataType deepCopy() {
		return new QuaternionType(new Quaternion(this.value));
	}
	public void write(DataOutputStream output) throws IOException {
		super.write(output);
		if(value == null)
		{
			output.writeFloat(1);
			output.writeFloat(0);
			output.writeFloat(0);
			output.writeFloat(0);	
		}else
		{
			output.writeFloat(value.w);
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
		QuaternionType other = (QuaternionType) obj;
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
