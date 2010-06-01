package com.golemgame.mvc;

public class NullType extends DataType{

	private static final long serialVersionUID = 1L;
	public static final int FILE_VERSION = 1;
	private static final NullType instance = new NullType();
	private static final String NULL_DESCRIPTION = "NULL";
	
	
	
	public static NullType get() {
		return instance;
	}

	private NullType() {
		super();
	}

	@Override
	public DataType deepCopy() {
		return this;//all null types are equivalent.
	}

	@Override
	public Type getType() {
		return Type.NULL;
	}
	@Override
	public String toString() {
		return NULL_DESCRIPTION;
	}
	//DONT override equals.

	@Override
	public int getFileVersion() {
		return FILE_VERSION;
	}
	
}
