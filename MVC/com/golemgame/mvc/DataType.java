package com.golemgame.mvc;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

public abstract class DataType implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public static enum Type
	{
		NULL(0), STRING(1),INT(2),LONG(3),FLOAT(4),BOOL(5),VECTOR3(6),VECTOR2(7),QUATERNION(8),COLLECTION(9),ENUM(10),PROPERTIES(11), COLOR(12),REFERENCE(13),DOUBLE(14),GUID(15),BINARY(16),OBJECT(17);//IMAGE?
		private final byte numericRepresentation;
		/**
		 * -1 is reserved to signify an expanded data type (just in case)
		 * @return
		 */
		private Type(int numericRepresentation)
		{
			if (numericRepresentation == -1)
				throw new IllegalArgumentException();
			this.numericRepresentation = (byte) numericRepresentation;
		}
		/**
		 * -1 is reserved to signify an expanded data type (just in case)
		 * @return
		 */
		public byte getNumericRepresentation() {
			return numericRepresentation;
		}
		
	}
	
	public abstract int getFileVersion();

	public DataType() {
		super();
	}


	public abstract Type getType();
	
	public abstract DataType deepCopy();
	
	/**
	 * Produce a deep copy, but make all references 'new'
	 * @param makeUnique
	 * @return
	 */
	public DataType uniqueDeepCopy(ReferenceMap referenceMap)
	{
		return deepCopy();//default implementation ignores this parameter
	}
	

	//public void setValue(Object value) throws IncompatibleValueException;
	public void write(DataOutputStream output)throws IOException 
	{
		output.writeByte(getType().getNumericRepresentation());
	}
	
	public static boolean sameType(DataType v1, DataType v2)
	{
		if(v1 == null && v2 == null)
			return true;
		else if (v1 == null || v2==null)
			return false;
		
		return v1.getType() == v2.getType();		
	}
	public static boolean isType(DataType v, Type t)
	{
		if (v == null && t == null)
			return true;
		return v.getType()==t;
	}

	public static DataType infer(Object value) {
		if(value instanceof DataType)
			return (DataType) value;
		Type t = inferType(value);
		switch(t)
		{
			case FLOAT:
				return new FloatType((Float)value);
			case LONG:
				return new LongType((Long)value);
			case INT:
				return new IntType((Integer)value);
			case DOUBLE:
				return new DoubleType((Double)value);
			case STRING:
				return new StringType((String)value);
			case BOOL:
				return new BoolType((Boolean)value);
			case REFERENCE:
				return new ReferenceType((Reference)value);
			case GUID:
				return new GUIDType((UUID)value);
			case VECTOR2:
				return new Vector2Type((Vector2f)value);
			case VECTOR3:
				return new Vector3Type((Vector3f)value);
			case QUATERNION:
				return new QuaternionType((Quaternion)value);
			case ENUM:
				return new EnumType((Enum<?>)value);
		}
		return null;
	}
	public static DataType.Type inferType(Object value) {
		if (value instanceof DataType)
			return ((DataType)value).getType();
		if (value instanceof Float)
		{
			return Type.FLOAT;
		}else if (value instanceof Double)
		{
			return Type.DOUBLE;
		}else if (value instanceof Integer)
		{
			return Type.INT;
		}else if (value instanceof String)
		{
			return Type.STRING;
		}else if (value instanceof Boolean)
		{
			return Type.BOOL;
		}else if (value instanceof Long)
		{
			return Type.LONG;
		}else if (value instanceof ColorRGBA)
		{
			return Type.COLOR;
		}else if (value instanceof Vector3f)
		{
			return Type.VECTOR3;
		}else if (value instanceof Vector2f)
		{
			return Type.VECTOR2;
		}else if (value instanceof Quaternion)
		{
			return Type.QUATERNION;
		}else if (value instanceof Enum<?>)
		{
			return Type.ENUM;
		}else if (value instanceof Reference)
		{
			return Type.REFERENCE;
		}else if (value instanceof UUID)
		{
			return Type.GUID;
		}//else if (value instanceof ArrayList)
		//{
		//	return Type.COLLECTION;
		//}
	/*	else if (value instanceof Boolean[])
		{
			return Type.BINARY;
		}*/ 
		return Type.NULL;
	}
}
