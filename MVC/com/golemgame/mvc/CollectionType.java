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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CollectionType extends DataType {
	private static final long serialVersionUID = 1L;
	public static final int FILE_VERSION = 1;
	private ArrayList<DataType> value = new ArrayList<DataType>();
	
	public CollectionType() {
		super();
	}
	public CollectionType(DataInputStream input,int[]versionMap) throws IOException
	{
		this();
		int size = input.readInt();
		ArrayList<DataType> values = new ArrayList<DataType>();
		for (int i = 0; i < size;i++)
		{
			DataType value = DataTypeReader.read(input,versionMap);		
			values.add(value);
		}
		value = new ArrayList<DataType>(values);//faster than adding them one by one if using COW arrays..
		
	}
	public final Type getType() {
		return Type.COLLECTION;
	}

	public void setValue(ArrayList<DataType> value) throws IncompatibleValueException {
		this.value = value;

	}

	
	
	public boolean addElement(DataType element)
	{
		return this.value.add(element);
	}
	
	public void setElement(DataType element, int position)
	{
		if(value.size()<=position)
		{
			value.ensureCapacity(position+1);
			while(value.size()<position+1)
			{
					value.add(NullType.get());
			
			}
		}
		this.value.set(position, element);
	}
	
	public DataType getElement(int position)
	{
		if(value.size()>position)
		{
			DataType val = value.get(position);
			if(val ==null)
				return NullType.get();
			else
				return val;
		}
		return NullType.get();
	}

	public void clear()
	{
		this.value.clear();
	}
	
	public boolean removeElement(DataType element)
	{
		return this.value.remove(element);
	}

	public Collection<DataType> getValues() {
		return value;
	}
	
	public CollectionType deepCopy() {
		CollectionType copy = new CollectionType();
		for (DataType data:value)
			copy.value.add(data.deepCopy());
		return copy;
	}
	public void write(DataOutputStream output) throws IOException {
		super.write(output);

		output.writeInt(value.size());
		for (int i = 0,size = value.size();i<size;i++)
	//	for(DataType data:value)
		{
			DataType data = value.get(i);
			if(data==null)
			{
				NullType.get().write(output);
			}else
				data.write(output);
		}
	}
	@Override
	public CollectionType uniqueDeepCopy(ReferenceMap referenceMap) {
		CollectionType copy = new CollectionType();
		for (DataType data:value)
			copy.value.add(data.uniqueDeepCopy(referenceMap));
		return copy;
	}
	public boolean contains(DataType element) {
		return this.value.contains(element);
	}
	@Override
	public String toString() {
		return String.valueOf(value);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		CollectionType other = (CollectionType) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	public void removeElement(int pos) {
		this.value.remove(pos);
	}
	

	@Override
	public int getFileVersion() {
		return FILE_VERSION;
	}
	

}
