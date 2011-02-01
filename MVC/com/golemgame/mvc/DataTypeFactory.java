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

import com.golemgame.mvc.DataType.Type;

public class DataTypeFactory {
	public static DataType buildDataType(Type type)
	{
		switch(type)
		{
		case NULL:
			return NullType.get();
		case STRING:
			return new StringType();
		case INT:
			return new IntType();
		case LONG:
			return new LongType();
		case FLOAT:
			return new FloatType();
		case BOOL:
			return new BoolType();
		case VECTOR3:
			return new Vector3Type();
		case VECTOR2:
			return new Vector2Type();
		case QUATERNION:
			return new QuaternionType();
		case COLLECTION:
			return new CollectionType();
		case ENUM:
			return new EnumType();
		case PROPERTIES:
			return new PropertyStore();
		case COLOR:
			return new ColorType();
		case REFERENCE:
			return new ReferenceType();
		case DOUBLE:
			return new DoubleType();
		case GUID:
			return new GUIDType();
		case BINARY:
			return new BinaryType();
		case OBJECT:
			return new ObjectType();
		default:
			throw new IncompatibleValueException();
		
		}
	}

	public static int getFileVersion(Type type) {
		switch(type)
		{
		case NULL:
			return NullType.FILE_VERSION;
		case STRING:
			return StringType.FILE_VERSION;
		case INT:
			return  IntType.FILE_VERSION;
		case LONG:
			return  LongType.FILE_VERSION;
		case FLOAT:
			return  FloatType.FILE_VERSION;
		case BOOL:
			return  BoolType.FILE_VERSION;
		case VECTOR3:
			return  Vector3Type.FILE_VERSION;
		case VECTOR2:
			return  Vector2Type.FILE_VERSION;
		case QUATERNION:
			return  QuaternionType.FILE_VERSION;
		case COLLECTION:
			return  CollectionType.FILE_VERSION;
		case ENUM:
			return  EnumType.FILE_VERSION;
		case PROPERTIES:
			return  PropertyStore.FILE_VERSION;
		case COLOR:
			return  ColorType.FILE_VERSION;
		case REFERENCE:
			return  ReferenceType.FILE_VERSION;
		case DOUBLE:
			return  DoubleType.FILE_VERSION;
		case GUID:
			return  GUIDType.FILE_VERSION;
		case BINARY:
			return  BinaryType.FILE_VERSION;
		case OBJECT:
			return  ObjectType.FILE_VERSION;
		default:
			return Integer.MIN_VALUE;
		
		}
	}
}
