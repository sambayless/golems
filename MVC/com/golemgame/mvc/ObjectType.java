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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

public class ObjectType extends DataType {

	private static final long serialVersionUID = 1L;

	public static final int FILE_VERSION = 1;
	
	private Object object;
	
	public ObjectType(Object object) {
		super();
		this.object = object;
	}

	public ObjectType() {
		super();
		object = null;
	}

	@Override
	public DataType deepCopy() {
		
		return null;
	}

	@Override
	public Type getType() {
		return Type.OBJECT;
	}

	@Override
	public void write(DataOutputStream output) throws IOException {
		super.write(output);
		//first, write the object size - why? so that we can skip the object, if there is no matching type.
		
		ByteArrayOutputStream tempOutput = new ByteArrayOutputStream();
		
		
		
		ObjectOutputStream objOut = new ObjectOutputStream(tempOutput);
		
		objOut.writeObject(object);
		objOut.flush();
		
		objOut.close();
		
		output.writeInt(tempOutput.size());
		
		byte[] b = tempOutput.toByteArray();
		output.write(b);
		
	}
	public ObjectType(DataInputStream input,int[]versionMap) throws IOException
	{
		super();
	
		int size = input.read();
		
		//read the data first into a byte array, so that we can guarantee exactly how many bytes were read (to avoid possible security breaches - no reading onto data outside the allowed size)
		
		byte[] buf = new byte[size];
		input.read(buf);
		ByteArrayInputStream byteIn = new ByteArrayInputStream(buf);
		
		ObjectInputStream objIn = new ObjectInputStream(byteIn);
		try {
			object = objIn.readObject();
		} catch (ClassNotFoundException e) {
			object = null;
			//for all of these - fail silently, and just nullify the object
		} catch(InvalidClassException e)
		{
			object = null;
		}catch(StreamCorruptedException e)
		{
			object = null;
		}catch(IOException e)
		{
			object = null;
		}
	}
	

	@Override
	public int getFileVersion() {
		return FILE_VERSION;
	}
	
}
