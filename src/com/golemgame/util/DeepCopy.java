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
package com.golemgame.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;
import java.util.concurrent.Executor;

public class DeepCopy {
	
	@SuppressWarnings("unchecked")
	public static <E extends Serializable> E  makeDeepCopy( E object)
	{
		//this is MUCH faster, but requires twice the memory
		try{

			ByteBufferOutputStream byteOutput = new ByteBufferOutputStream ();
			// byteOutput.getBuffer().mark();
			 ObjectOutputStream output = new ObjectOutputStream(byteOutput);
			
			
			output.writeObject(object);
		//	byteOutput.getBuffer().rewind();
			byteOutput.getBuffer().flip();
			
			ByteBufferInputStream byteInput = new ByteBufferInputStream(byteOutput.getBuffer());
			
			ObjectInputStream input = new ObjectInputStream(byteInput);
			
			return (E) input.readObject();
		}catch(IOException e)
		{
			return null;
		}catch(ClassNotFoundException e)
		{
			return null;
		}
	}
	
	public static Object makeDeepCopy(final Serializable object, Executor executor)
	{
		try{
			  PipedInputStream in = new PipedInputStream();
			  PipedOutputStream out = new PipedOutputStream(in);
			
			final ObjectOutputStream output = new ObjectOutputStream(out);
			
			executor.execute(new Runnable()
			{
	
				
				public void run() {
					try{
						output.writeObject(object);
					}catch(IOException e)
					{
						
					}
				}
				
			});
			
			ObjectInputStream input = new ObjectInputStream(in);
			return input.readObject();
		}catch(IOException e)
		{
			return null;
		}catch(ClassNotFoundException e)
		{
			return null;
		}
	}
}
