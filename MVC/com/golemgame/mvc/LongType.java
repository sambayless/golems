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
