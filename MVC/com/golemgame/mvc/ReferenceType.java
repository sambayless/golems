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
