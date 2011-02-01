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
