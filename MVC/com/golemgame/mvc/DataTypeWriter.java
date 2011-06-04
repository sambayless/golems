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

import java.io.DataOutputStream;
import java.io.IOException;
import com.golemgame.mvc.DataType.Type;

public class DataTypeWriter {
	
	
	
	public static final int FILE_VERSION = 2;
	
	public static void write(DataType data, DataOutputStream output) throws IOException
	{
		writeHeader(output);
		
		data.write(output);
		
	}
	
	private static void writeHeader(DataOutputStream output)throws IOException
	{
	//write a header, to describe the version of each data type
		
		//this header is in the following format:
		//first, an int describing the HEADER file version:		
		
		output.writeInt(FILE_VERSION);
		
		//File version 1 follows:
		
		//first, an integer for the total number of data types
		output.writeInt(Type.values().length);
		
		//then for each type, the type code followed by the file version for that data type, in no specific order
		//	NULL(0), STRING(1),INT(2),LONG(3),FLOAT(4),BOOL(5),VECTOR3(6),VECTOR2(7),QUATERNION(8),COLLECTION(9),ENUM(10),PROPERTIES(11), COLOR(12);

		
		for(Type type:Type.values())
		{
			output.writeInt(type.getNumericRepresentation());
			output.writeInt(DataTypeFactory.getFileVersion(type));
		}
		
		/*output.writeInt(Type.NULL.getNumericRepresentation());
		output.writeInt(NullType.FILE_VERSION);
		
		output.writeInt(Type.STRING.getNumericRepresentation());
		output.writeInt(StringType.FILE_VERSION);
		
		output.writeInt(Type.INT.getNumericRepresentation());
		output.writeInt(IntType.FILE_VERSION);
		
		output.writeInt(Type.LONG.getNumericRepresentation());
		output.writeInt(LongType.FILE_VERSION);
		
		output.writeInt(Type.FLOAT.getNumericRepresentation());
		output.writeInt(FloatType.FILE_VERSION);
		
		output.writeInt(Type.BOOL.getNumericRepresentation());
		output.writeInt(BoolType.FILE_VERSION);
		
		output.writeInt(Type.VECTOR3.getNumericRepresentation());
		output.writeInt(Vector3Type.FILE_VERSION);
		
		output.writeInt(Type.VECTOR2.getNumericRepresentation());
		output.writeInt(Vector2Type.FILE_VERSION);
		
		output.writeInt(Type.QUATERNION.getNumericRepresentation());
		output.writeInt(QuaternionType.FILE_VERSION);
		
		output.writeInt(Type.COLLECTION.getNumericRepresentation());
		output.writeInt(CollectionType.FILE_VERSION);
		
		output.writeInt(Type.ENUM.getNumericRepresentation());
		output.writeInt(EnumType.FILE_VERSION);		
		
		output.writeInt(Type.PROPERTIES.getNumericRepresentation());
		output.writeInt(PropertyStore.FILE_VERSION);
		
		output.writeInt(Type.COLOR.getNumericRepresentation());
		output.writeInt(ColorType.FILE_VERSION);
		
		output.writeInt(Type.REFERENCE.getNumericRepresentation());
		output.writeInt(ReferenceType.FILE_VERSION);
		
		output.writeInt(Type.DOUBLE.getNumericRepresentation());
		output.writeInt(DoubleType.FILE_VERSION);*/
		
		
		//these version numbers represent the storage format of each datatype.
		
		
		
	}
}
