package com.golemgame.mvc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DoubleType extends DataType {
	private static final long serialVersionUID = 1L;
	public static final int FILE_VERSION = 1;

	private double value = 0;
	
	public DoubleType(double value) {
		super();
		this.value = value;
	}

	@Override
	public DataType deepCopy() {
		return new DoubleType(value);
	}

	@Override
	public Type getType() {
		return Type.DOUBLE;
	}
	
	public DoubleType() {
		super();
	}

	public DoubleType(DataInputStream input,int[]versionMap) throws IOException
	{
		this();
		value = input.readDouble();
	}
	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
	public void setValue(Object value) throws IncompatibleValueException {
		if(value instanceof Double)
			this.setValue(((Double)value).doubleValue());
		else
			throw new IncompatibleValueException();
	}

	
	public void write(DataOutputStream output) throws IOException {
		super.write(output);
		output.writeDouble(value);
	}
	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		DoubleType other = (DoubleType) obj;
		if (Double.doubleToLongBits(value) != Double
				.doubleToLongBits(other.value))
			return false;
		return true;
	}

	@Override
	public int getFileVersion() {
		return FILE_VERSION;
	}
	
}
