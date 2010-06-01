package com.golemgame.mvc;

import java.io.DataInputStream;
import java.io.IOException;

import com.golemgame.mvc.DataType.Type;

public class DataTypeReader {
	
	private final static Type[] typeMap;
	static
	{
		int maxValue = -1;
		for(Type type:Type.values())
		{
			if (type.getNumericRepresentation()>maxValue)
				maxValue = type.getNumericRepresentation();
		}
		typeMap = new Type[maxValue+1];
		for (Type type:Type.values())
			typeMap[type.getNumericRepresentation()] = type;
	}
	
	public static Type getType(int numberRepresentation)
	{
		return typeMap[numberRepresentation];
	}
	
	public static DataType readWithHeader(DataInputStream input)throws IOException
	{
		
		int headerVersion= input.readInt();
		if(headerVersion>2)
			throw new IOException("Unrecognized file format");
		else if (headerVersion<1)
			throw new IOException("Unrecognized file format");
		
		int dataTypes = input.readInt();
		
		int[] versionMap = new int[typeMap.length];
		
		for (int i = 0; i<dataTypes;i++)
		{
			int typeCode = input.readInt();
			int version =  input.readInt();
			if (typeCode>=typeMap.length || typeCode<0)
				throw  new IOException("Unrecognized file format");
			
			versionMap[typeCode] =	version;
			
			if (! isFileVersionSupported(getType(typeCode),version))
				throw new IOException("Unrecognized file format");
		}//in the future, this information may be used...
		
		for (Type type:Type.values())
		{//any unspecified versions are defaulted to 1.
			if (versionMap[type.getNumericRepresentation()] == 0)
				versionMap[type.getNumericRepresentation()] = 1;
		}
		
		
		return read(input,versionMap);
	}
	

	private static boolean isFileVersionSupported(Type type, int version) {
		return getSupportedFileVersion(type) >= version;
	}
	
	private static int getSupportedFileVersion(Type type)
	{
		return DataTypeFactory.getFileVersion(type);
	}

	/**
	 * VersionMap: at the index of the numeric representation of each type is its file format version.
	 */
	public static DataType read(DataInputStream input, int[]versionMap) throws IOException
	{
		//this assumes any header or the like are already removed...
		
		byte typeCode = input.readByte();
		
		Type type = getType(typeCode);
		
		switch(type)
		{
			case BOOL:
				return new BoolType(input,versionMap);
			case NULL:
				return NullType.get();
			case FLOAT:
				return new FloatType(input,versionMap);
			case INT:
				return new IntType(input,versionMap);
			case LONG:
				return new LongType(input,versionMap);
			case QUATERNION:
				return new QuaternionType(input,versionMap);
			case COLOR:
				return new ColorType(input,versionMap);
			case ENUM:
				return new EnumType(input,versionMap);
			case PROPERTIES:
				return new PropertyStore(input,versionMap);
			case VECTOR2:
				return new Vector2Type(input,versionMap);
			case VECTOR3:
				return new Vector3Type(input,versionMap);
			case STRING:
				return new StringType(input,versionMap);
			case COLLECTION:
				return new CollectionType(input,versionMap);
			case REFERENCE:
				return new ReferenceType(input,versionMap);
			case DOUBLE:
				return new DoubleType(input,versionMap);
			case GUID:
				return new GUIDType(input,versionMap);
			case OBJECT:
				return new ObjectType(input,versionMap);
			case BINARY:
				return new BinaryType(input,versionMap);
			default:
				throw new IOException("Unknown data type " + typeCode);
		}
		
	}
}
