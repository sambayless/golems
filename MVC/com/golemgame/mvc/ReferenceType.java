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


public class ReferenceType extends DataType {

	private static final long serialVersionUID = 1L;

	public static final int FILE_VERSION = 1;

	private Reference id;
	
	public Reference getID() {
/*		if (id == null)
			StateManager.getLogger().warning("null reference");*/
		return id;
	}

	
	public ReferenceType() {
		super();		
		this.id = Reference.createUniqueReference();
	}

	public ReferenceType(Reference id) {
		super();
		this.id = id;
	}

	public ReferenceType(DataInputStream input, int[] versionMap) throws IOException {
		long val = input.readLong();
		this.id = new Reference(val);
	}


	@Override
	public ReferenceType deepCopy() {
		return new ReferenceType(id);
	}
	
	@Override
	public ReferenceType uniqueDeepCopy(ReferenceMap referenceMap) {
		ReferenceType newReferenceType = new ReferenceType(Reference.getNullReference());
		Reference newRef = referenceMap.createUniqueReference(id,newReferenceType);
		newReferenceType.setValue(newRef);
		return newReferenceType;
	}

	@Override
	public Type getType() {
		return Type.REFERENCE;
	}

	public void setValue(Reference value) {
		this.id = value;
		
	}


	@Override
	public void write(DataOutputStream output) throws IOException {
		super.write(output);
		output.writeLong(id.getValue());
	}

	@Override
	public String toString() {
		return id.toString();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		ReferenceType other = (ReferenceType) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public int getFileVersion() {
		return FILE_VERSION;
	}
	
}
