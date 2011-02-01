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
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import com.golemgame.mvc.DataType.Type;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

/**
 * A model holds a map of property names to values.
 * Subclasses may provide explicit access to certain values, or more convenient access to those values, but will not on its own 
 * act as as a controller or view of those models.
 * 
 * One may attach listeners to a property store (those listeners are not themselves a part of the model).
 * 
 * Properties may be primitive values, or they may be property stores themselves.
 * @author Sam
 *
 */
public class PropertyStore extends DataType{
	

	private static final long serialVersionUID = 1L;
	public static final int FILE_VERSION = 1;
	public final Type getType() {
		return Type.PROPERTIES;
	}
	
	private static final Random random = new Random();
	
	private final int hashcode;

	public final static String NAME_KEY = "name";
	public final static String CLASS_KEY = "class";
	private static final String NULL_KEY = " ";
	
	
	
	public static final PropertyStore defaultStore = new PropertyStore();
	public static final CollectionType defaultCollection = new CollectionType();
	public static final ReferenceType defaultReference = new ReferenceType();
	public static final Vector3Type defaultVector3 = new Vector3Type();
	public static final Vector2Type defaultVector2 = new Vector2Type();
	public static final ColorType defaultColor = new ColorType();
	public static final QuaternionType defaultQuat = new QuaternionType();
	public static final BoolType defaultBool = new BoolType();
	public static final LongType defaultLong = new LongType();
	public static final FloatType defaultFloat = new FloatType();
	public static final StringType defaultString = new StringType();
	public static final DoubleType defaultDouble = new DoubleType();
	public static final IntType defaultInt = new IntType();
	public static final EnumType defaultEnum = new EnumType();
	
	public DataType getDefaultValue(DataType.Type type)
	{
		switch(type)
		{
			case BOOL:
				return defaultBool;
			case PROPERTIES:
				return defaultStore;
			case COLLECTION:
				return defaultCollection;
			case REFERENCE:
				return defaultReference;
			case VECTOR3:
				return defaultVector3;
			case VECTOR2:
				return defaultVector2;
			case COLOR:
				return defaultColor;
			case QUATERNION:
				return defaultQuat;
			case LONG:
				return defaultLong;
				
			case FLOAT:
				return defaultFloat;
				
			case STRING:
				return defaultString;
				
			case DOUBLE:
				return defaultDouble;
				
			case INT:
				return defaultInt;
				
			case ENUM:
				return defaultEnum;
		}
		return NullType.get();
	}
	
	private final static SustainedView dummyView = new SustainedView()
	{

		public void remove() {
			// TODO Auto-generated method stub
			
		}

		public void invertView(PropertyStore store) {
			//do nothing for now
			
		}

		public void refresh() {
			//do nothing for now
		}

		public PropertyStore getStore() {
			return null;
		}
		
	};
	
	//for now, only supporting having a single sustained view... can expand this in the future.
	private SustainedView sustainedView = dummyView;
	
	private HashMap<String,DataType> propertyMap = new HashMap<String,DataType>();
	
	public void setClassName(String className)
	{
		setProperty(CLASS_KEY,className);
	}
	
	public void setSustainedView(SustainedView view)
	{
		if(view == null)
			this.sustainedView = dummyView;
		else
			this.sustainedView = view;
		
	}
	
	public void refresh()
	{
		this.getSustainedView().refresh();
	}
	
	public SustainedView getSustainedView() {
		return sustainedView;
	}

	public PropertyStore() {
		super();
		this.hashcode = random.nextInt();
	}

	public PropertyStore(PropertyStore copyFrom) {
		this();
		this.propertyMap = copyFrom.deepCopy().propertyMap;
	}

	public PropertyStore(DataInputStream input,int[]versionMap) throws IOException
	{
		this();
		int size = input.readInt();
		for (int i = 0; i < size;i++)
		{
			try{
			String key = input.readUTF();
			DataType value = DataTypeReader.read(input,versionMap);		
			
			propertyMap.put(key, value);
			}catch(EOFException e)
			{
				throw (e);
			}
		}
	}
	public String getClassName()
	{
		return getString(CLASS_KEY,"Unknown");
	}
	
/*	public void addView(SustainedView view)
	{
		this.sustainedViews.add(view);
	}
	
	public void removeView(SustainedView view)
	{
		this.sustainedViews.remove(view);
	}
	

	public void refresh()
	{
		for(SustainedView view:sustainedViews)
			view.refresh();
	}
	*/
	
	/**
	 * Set all properties to deep copies of the properties provided
	 * @param setFrom 
	 */
	public void set(PropertyStore setFrom)
	{
		for(Entry<String,DataType> entry:setFrom.propertyMap.entrySet())
		{
			this.propertyMap.put(entry.getKey(), entry.getValue().deepCopy());
		}
	}
	
	public void setValue(Object value) throws IncompatibleValueException {
			throw new IncompatibleValueException();
	}
	
	
	public PropertyStore getPropertyStore(String key)
	{
		return getPropertyStore(key, defaultStore);
	}
	
	public PropertyStore getPropertyStore(String key, PropertyStore defaultStore)
	{
		DataType value = propertyMap.get(key);
		if(value == null || value.getType()!=Type.PROPERTIES)
		{
			value = defaultStore.deepCopy();
			propertyMap.put(key, value);
			
		}
		return (PropertyStore) value;
	}
	
	/**
	 * Warning: this method can return null if there is no key (not just NullType)
	 * @param key
	 * @return
	 */
	public DataType getProperty(String key)
	{
		DataType value = propertyMap.get(key);
		
			
		return value;
	}
	
	public DataType getProperty(String key,DataType defaultValue)
	{
		DataType value = propertyMap.get(key);
		if(value == null && defaultValue != null)
		{
			DataType defValue = defaultValue.deepCopy();
			propertyMap.put(key, defValue);
			return defValue;
		}
		return value;
	}
	
	public String getString(String key,String defaultValue)
	{
		return asString(getProperty(key,new StringType(defaultValue)));
	}
	
	public double getDouble(String key)
	{
		return getDouble(key,0.0);
	}
	
	public double getDouble(String key,double defaultValue)
	{
		DataType data = getProperty(key,new DoubleType(defaultValue));
		return asDouble(data,defaultValue);
	}
	
	public float getFloat(String key,float defaultValue)
	{
		DataType data = getProperty(key,new FloatType(defaultValue));
		return asFloat(data);
	}
	
	public long getLong(String key,long defaultValue)
	{
		return asLong(getProperty(key,new LongType(defaultValue)));
	}
	
	public boolean getBoolean (String key,boolean defaultValue)
	{
		return asBoolean(getProperty(key,new BoolType(defaultValue)));
	}

	public int getInt(String key,int defaultValue)
	{
		return asInteger(getProperty(key,new IntType(defaultValue)));
	}
	
	public ColorRGBA getColor(String key,ColorRGBA defaultValue)
	{
		return asColorRGBA(getProperty(key,new ColorType(defaultValue)));
	}
	
	public Quaternion getQuaternion(String key,Quaternion defaultValue)
	{
		return asQuaternion(getProperty(key,new QuaternionType(defaultValue)));
	}
	
	public Vector3f getVector3f(String key,Vector3f defaultValue)
	{
		return asVector3f(getProperty(key,new Vector3Type(defaultValue)));
	}
	
	public Vector2f getVector2f(String key,Vector2f defaultValue)
	{
		return asVector2f(getProperty(key,new Vector2Type(defaultValue)));
	}
	

	/**
	 * The default value is required here, to find the enum to return.
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public <T extends Enum<?>> T getEnum(String key,T defaultValue)
	{
		DataType enumData = getProperty(key,new EnumType( defaultValue));
		return asEnum(enumData,defaultValue);
	}
	
	/**
	 * Default value is null reference!
	 * @param key
	 * @return
	 */
	public Reference getReference(String key)
	{
		return asReference(getProperty(key,new ReferenceType(Reference.createUniqueReference())));
	}
	
	public Reference getReference(String key,Reference defaultValue)
	{
		return asReference(getProperty(key,new ReferenceType(defaultValue)));
	}
	


	public String getString(String key)
	{
		return asString(getProperty(key,defaultString));
	}
	
	public float getFloat(String key)
	{
		return asFloat(getProperty(key,defaultFloat));
	}
	
	public long getLong(String key)
	{
		return asLong(getProperty(key,defaultLong));
	}
	
	public boolean getBoolean (String key)
	{
		return asBoolean(getProperty(key,defaultBool));
	}

	public int getInt(String key)
	{
		return asInteger(getProperty(key,defaultInt));
	}
	
	public Quaternion getQuaternion(String key)
	{
		return asQuaternion(getProperty(key,defaultQuat));
	}
	
	public CollectionType getCollectionType(String key)
	{
		CollectionType collection = (CollectionType)(propertyMap.get(key));
		if(collection == null)
		{
			collection=new CollectionType();
			setProperty(key,collection);
			
		}
		return collection;
	}
	
	
	public Vector3f getVector3f(String key)
	{
		return asVector3f( getProperty(key, defaultVector3));
	}
	
	public Vector2f getVector2f(String key)
	{
		return asVector2f( getProperty(key, defaultVector2));
	}
	
	public void setProperty(String key, DataType value)
	{
		if (value == null || value.getType() == DataType.Type.NULL)
			propertyMap.remove(key);
		else
			propertyMap.put(key, value);
	}
		
	public void setProperty(String key, String value)
	{

		setProperty(key, new StringType(value));
	}
	
	
	public void setProperty(String key, double value)
	{

		setProperty(key, new DoubleType(value));
	}
	
	public void setProperty(String key, float value)
	{
	
		setProperty(key,new FloatType(value));
	}
	
	public void setProperty(String key, long value)
	{

		setProperty(key, new LongType(value));
	}
	
	public void setProperty(String key, int value)
	{
	
		setProperty(key, new IntType(value));
	}
	
	public void setProperty(String key, boolean value)
	{

			
		setProperty(key,new BoolType(value));
	}
	
	public void setProperty(String key, Quaternion value)
	{

		setProperty(key, new QuaternionType(value));
	}
	
	public void setProperty(String key, Vector2f value)
	{

		setProperty(key,new Vector2Type(value));
	}
	
	public void setProperty(String key, ColorRGBA value)
	{

		setProperty(key,new ColorType(value));
	}
	
	public void setProperty(String key, Enum<?> value)
	{
	
			
		setProperty(key,new EnumType(value));
	}
	
	
	public void setProperty(String key, Vector3f value)
	{
	
			
		setProperty(key, new Vector3Type(value));
	}
	
	public void setProperty(String key, Reference value)
	{

		setProperty(key,new ReferenceType(value));
	}
	
	public void setProperty(String key, Collection<?> value)
	{
		setProperty(key,asDataType(value));
	}
	
	public final String getName()
	{
		return asString( propertyMap.get(NAME_KEY));
	}
	
	/**
	 * Remove the given key from the property store
	 * @param key
	 */
	public void nullifyKey(String key)
	{
		this.propertyMap.remove(key);
	}
	
	public static final DataType asDataType(Object value)
	{
		if(value instanceof DataType)
			return (DataType)value;
		
		if (value instanceof String)
			return new StringType((String)value);
		else if (value instanceof Float)
			return new FloatType((Float)value);
		else if (value instanceof Long)
			return new LongType((Long)value);
		else if (value instanceof Boolean)
			return new BoolType((Boolean)value);
		else if (value instanceof Integer)
			return new IntType((Integer)value);
		else if (value instanceof Quaternion)
			return new QuaternionType((Quaternion)value);
		else if (value instanceof Vector3f)
			return new Vector3Type((Vector3f)value);
		else if (value instanceof Vector2f)
			return new Vector2Type((Vector2f)value);
		else if (value instanceof Collection)
		{
			CollectionType collection = new CollectionType();
			for(Object element:((Collection<?>)value))
				collection.addElement(asDataType(element));
		}
		
		return null;
	}
	
	
	
	public static final String asString(DataType dataType)
	{
		return asString(dataType,"");
	}
	
	public  static final  String asString(DataType dataType, String defaultValue)
	{
		if(dataType == null || dataType.getType() != DataType.Type.STRING)
			return defaultValue;
		else
			return ((StringType)dataType).getValue();
		
	}
	
	private Reference asReference(DataType dataType) {

		return ((ReferenceType)dataType).getID();
	}

	
	public static final float asFloat(DataType dataType)
	{
		return asFloat(dataType,0f);
	}
	
	public  static final  double asDouble(DataType dataType, double defaultValue)
	{
		if(dataType == null || dataType.getType() != DataType.Type.DOUBLE)
			return defaultValue;
		else
			return ((DoubleType)dataType).getValue();		
	}
	
	
	public  static final  float asFloat(DataType dataType, float defaultValue)
	{
		if(dataType == null || dataType.getType() != DataType.Type.FLOAT)
			return defaultValue;
		else
			return ((FloatType)dataType).getValue();		
	}
	
	public static final long asLong(DataType dataType)
	{
		return asLong(dataType,0L);
	}
	
	public  static final  long asLong(DataType dataType, long defaultValue)
	{
		if(dataType == null || dataType.getType() != DataType.Type.LONG)
			return defaultValue;
		else
			return ((LongType)dataType).getValue();		
	}
	
	public static final boolean asBoolean(DataType dataType)
	{
		return asBoolean(dataType,false);
	}
	
	public  static final  boolean asBoolean(DataType dataType, boolean defaultValue)
	{
		if(dataType == null || dataType.getType() != DataType.Type.BOOL)
			return defaultValue;
		else
			return ((BoolType)dataType).getValue();		
	}
	
	public static final Collection<DataType> asCollection(DataType dataType)
	{
		return asCollection(dataType,new ArrayList<DataType>());
	}
	
	public  static final Collection<DataType> asCollection(DataType dataType, Collection<DataType> defaultValue)
	{
		if(dataType == null || dataType.getType() != DataType.Type.COLLECTION)
			return defaultValue;
		else
			return ((CollectionType)dataType).getValues();		
	}
	
	
	
	public static final int asInteger(DataType dataType)
	{
		return asInteger(dataType,0);
	}
	
	public  static final int asInteger(DataType dataType, int defaultValue)
	{
		if(dataType == null || dataType.getType() != DataType.Type.INT)
			return defaultValue;
		else
			return ((IntType)dataType).getValue();		
	}
	
	

	
	@SuppressWarnings("unchecked")
	public  static final <T extends Enum<?>> T  asEnum(DataType dataType, T defaultValue)
	{
		if(dataType == null || dataType.getType() != DataType.Type.ENUM)
			return defaultValue;
		else
		{
			T enumVal = (T)((EnumType)dataType).getValue(defaultValue.getDeclaringClass());	
			if(enumVal != null && defaultValue.getDeclaringClass().equals(enumVal.getDeclaringClass()))
				return enumVal;
			else
				return defaultValue;
		}
	}
	
	
	public static final ColorRGBA asColorRGBA(DataType dataType)
	{
		return asColorRGBA(dataType,new ColorRGBA());
	}
	
	public static final ColorRGBA asColorRGBA(DataType dataType, ColorRGBA defaultValue)
	{
		if(dataType == null || dataType.getType() != DataType.Type.COLOR)
			return defaultValue;
		else
			return ((ColorType)dataType).getValue();		
	}
	
	public static final Quaternion asQuaternion(DataType dataType)
	{
		return asQuaternion(dataType,new Quaternion());
	}
	
	public  static final Quaternion asQuaternion(DataType dataType, Quaternion defaultValue)
	{
		if(dataType == null || dataType.getType() != DataType.Type.QUATERNION)
			return defaultValue;
		else
			return ((QuaternionType)dataType).getValue();		
	}
	
	public static final Vector3f asVector3f(DataType dataType)
	{
		return asVector3f(dataType,new Vector3f());
	}
	
	public  static final Vector3f asVector3f(DataType dataType, Vector3f defaultValue)
	{
		if(dataType == null || dataType.getType() != DataType.Type.VECTOR3)
			return defaultValue;
		else
			return ((Vector3Type)dataType).getValue();		
	}
	
	
	public static final Vector2f asVector2f(DataType dataType)
	{
		return asVector2f(dataType,new Vector2f());
	}
	
	public  static final Vector2f asVector2f(DataType dataType, Vector2f defaultValue)
	{
		if(dataType == null || dataType.getType() != DataType.Type.VECTOR2)
			return defaultValue;
		else
			return ((Vector2Type)dataType).getValue();		
	}

	public PropertyStore deepCopy() {
		PropertyStore copy = new PropertyStore();
		for (String key:this.propertyMap.keySet())
		{
			copy.propertyMap.put(key, this.propertyMap.get(key).deepCopy());
		}
		return copy;
	}

	@Override
	public PropertyStore uniqueDeepCopy(ReferenceMap referenceMap) {
		PropertyStore copy = new PropertyStore();
		for (String key:this.propertyMap.keySet())
		{
			copy.propertyMap.put(key, this.propertyMap.get(key).uniqueDeepCopy(referenceMap));
		}
		return copy;
	}

	public void write(DataOutputStream output) throws IOException {
		super.write(output);

		if(this.getClassName().equals("Unknown"))
			System.out.println("");
		
		Set<Entry<String, DataType>> entrySet = propertyMap.entrySet();
	
		output.writeInt(entrySet.size());
	
		for(Entry<String,DataType> entry: propertyMap.entrySet())
		{
			
			if(entry.getKey() == null)
			{
				output.writeUTF(NULL_KEY);
			}else
			{
				//System.out.println(entry.getKey());
				//int cur = output.size();
				output.writeUTF(entry.getKey());
			//	System.out.println(output.size() - cur);
			}
			
			if(entry.getValue() == null)
				NullType.get().write(output);
			else
				entry.getValue().write(output);
		}
	

	}
	
	@Override
	public String toString() {
		return ( getName() != null? getName():"") + "(" + getClassName() + ")";
	}

	public boolean hasProperty(String function) {
		DataType val = this.propertyMap.get(function);
		if(val == null )//|| val.getType()==DataType.Type.NULL
			return false;
		return true;
	}
	
	/**
	 * Test if a key with a value of this type exists in the store
	 * @param function
	 * @param type
	 * @return
	 */
	public boolean hasProperty(String function,DataType.Type type) {
		DataType val = this.propertyMap.get(function);
		if(val == null || val.getType()!=type)
			return false;
		return true;
	}

	
	
	public Collection<String> getKeys() {
		return this.propertyMap.keySet();
	}

	
	/**
	 * Apply to this store each value in from with a matching key in keys
	 * (except for class name, which will not be applied).
	 * Note that the bahvaiour of this method is subtely different from set(Store)
	 * @param prototype
	 * @param keys
	 */
	public void apply(PropertyStore from, Collection<String> keys) {
		for(String key:keys)
		{
			if (key.equalsIgnoreCase(PropertyStore.CLASS_KEY))
				continue;
			
			if (from.hasProperty(key))
				this.setProperty(key,from.getProperty(key).deepCopy());
		}
	}
	
	/**
	 * Apply to this store each value in from with a matching key in keys
	 * (except for class name, which will not be applied).
	 * Note that the bahvaiour of this method is subtely different from set(Store)
	 * @param prototype
	 */
	public void apply(PropertyStore from) {
		apply(from, from.getKeys());
		
	}

	@Override
	public int hashCode() {
		return hashcode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyStore other = (PropertyStore) obj;
		if(other.hashcode!= this.hashcode)//this may break expected behaviour perhaps? But required for equals contract
			return false;
		if (propertyMap == null) {
			if (other.propertyMap != null)
				return false;
		} else if (!propertyMap.equals(other.propertyMap))
			return false;
		return true;
	}

	public void clear() {
		this.propertyMap.clear();
		
	}

	public String formatted() {
		String t = this.toString();
		
		
		
		
		return t+ this.propertyMap.toString();
	}


	public GUIDType getGUIDType(String key)
	{
		if(this.hasProperty(key,DataType.Type.GUID))
		{
			return (GUIDType)getProperty(key);
		}else
		{
			GUIDType val = new GUIDType();
			setProperty(key,val);
			return val;
		}
		
	
	}
	public GUIDType getGUIDType(String key,GUIDType defaultValue)
	{
		if(this.hasProperty(key,DataType.Type.GUID))
		{
			return (GUIDType)getProperty(key);
		}else
		{
			GUIDType val = new GUIDType(defaultValue.getValue());
			setProperty(key,val);
			return val;
		}
		
	
	}


	@Override
	public int getFileVersion() {
		return FILE_VERSION;
	}

	/**
	 * Return a value of the required type; if no such value exists or if the value has a different type,
	 *  create a new value and replace the old value, and return the new value
	 * @param key
	 * @param requiredType
	 * @return
	 */
	public DataType getProperty(String key, Type requiredType) {
	
		DataType t = getProperty(key);
		if (t== null || t.getType()!=requiredType)
		{
			t = getDefaultValue(requiredType).deepCopy();
			setProperty(key,t);
			
		}
		return t;
	}
	
}
