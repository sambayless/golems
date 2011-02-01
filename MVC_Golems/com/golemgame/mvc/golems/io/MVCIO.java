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
package com.golemgame.mvc.golems.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.golemgame.mvc.DataTypeReader;
import com.golemgame.mvc.DataTypeWriter;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.Golems;
import com.golemgame.mvc.golems.transform.SaveTransformer;


public class MVCIO {
	private static final int DIRECT_FORMAT = 1;
	private static final int MVC_FORMAT = 2;
	private static final int HEADER_MVC_FORMAT = 3;
	
	private static final int GOLEMS_FORMAT_CODE = HEADER_MVC_FORMAT;
	
	public static  byte[] GOLEMS_HEADER;
	static
	{		
		try{//Note: this has an S, for GolemSsaveFile.
			GOLEMS_HEADER= "GolemsSaveFile".getBytes("US-ASCII");
		}catch(UnsupportedEncodingException e)
		{
			GOLEMS_HEADER = "GolemsSaveFile".getBytes();
		}
	}
	public static final String HEADER = "GolemSaveFile";
	
	private static final int GZIP_HEADER_1 = 0x1f;
	private static final int GZIP_HEADER_2 = 0x8b;
	
	public static void save(File file, PropertyStore store, boolean compress) throws IOException
	{
		OutputStream baseOutput = new BufferedOutputStream( new FileOutputStream(file));

		DataOutputStream data = new DataOutputStream(baseOutput);
		data.write(GOLEMS_HEADER);
		data.writeInt(GOLEMS_FORMAT_CODE);
		
		//header section contains x bytes
		
		ByteArrayOutputStream headerStream = new ByteArrayOutputStream();
		DataOutputStream headerData = new DataOutputStream(headerStream);
		writeHeader(store, headerData);
		
		byte[] headerArray = headerStream.toByteArray();
		
		data.writeInt(headerArray.length);
		data.write(headerArray);
		
		OutputStream stream;

		if (compress)
			stream= (new GZIPOutputStream (baseOutput));
		else
			stream= baseOutput;
		
			//need to change this to write a golems style save file header first.
			DataOutputStream output = new DataOutputStream(stream);
			DataTypeWriter.write(store, output);
	
			output.close();
			stream.close();//important to close the gzip stream, specifically.
	

	}

	
	private static void writeHeader(PropertyStore store,
			DataOutputStream headerData) throws IOException {
		headerData.writeInt(Golems.getMajor());
		headerData.writeInt(Golems.getMinor());
		headerData.writeInt(Golems.getRevision());
	}


	public static  byte[] HEADERBYTES;
	static
	{		
		try{
			HEADERBYTES= HEADER.getBytes("US-ASCII");
		}catch(UnsupportedEncodingException e)
		{
			HEADERBYTES = HEADER.getBytes();
		}
	}

	public static PropertyStore load(File file) throws FailedToLoadException
	{
		return load(file,true);
	}
	
	public static PropertyStore load(File file, boolean allowOldFormats) throws FailedToLoadException
	{
		try{
			
			BufferedInputStream fileInput = new BufferedInputStream (new FileInputStream(file));
			
			DataInputStream dataInput = new DataInputStream(fileInput);
			
			fileInput.mark(100 + HEADERBYTES.length);
			
			byte[] golemsGeneralHeader = new byte[GOLEMS_HEADER.length];	
			
			byte[] golemDirectHeader = new byte[HEADERBYTES.length];
			
			boolean isGolemsGeneralFile = false;
			boolean isDirectFile = false;
			
			if ( fileInput.read(golemsGeneralHeader)==GOLEMS_HEADER.length)
			{
				if(Arrays.equals(GOLEMS_HEADER, golemsGeneralHeader))
					isGolemsGeneralFile = true;			
			}
			
			int generalFileFormat = -1;
			
			if(isGolemsGeneralFile)
			{
				generalFileFormat =dataInput.readInt();
			}else
			{
				//assume this is a Direct File
				fileInput.reset();
				generalFileFormat = DIRECT_FORMAT;
			}
			
			if (generalFileFormat == DIRECT_FORMAT)
			{
				if(!allowOldFormats)
					throw new FailedToLoadException("Unsupported save format");
				if ( fileInput.read(golemDirectHeader)==HEADERBYTES.length)
				{
					if(Arrays.equals(HEADERBYTES, golemDirectHeader))
						isDirectFile = true;					
				}
		
			}
			
			if(!isDirectFile && !isGolemsGeneralFile)
			{
				fileInput.reset();//reset and attempt to read the gzip header from the start if
				//there is no direct file header.
			}
		
			
			
			Object object ;
			
			int saveFileMajorVersion = -1;
			int saveFileMinorVersion = -1;
			int saveFileRevision = -1;
			
			
			if(generalFileFormat == DIRECT_FORMAT &! isDirectFile)
			{
				//no longer supporting the earliest versions of the mchn save format.
				throw new FailedToLoadException("This machine file is no longer supported.");
				//object = DirectSaveManager.getInstance().load(fileInput) ;
			}else if (generalFileFormat == DIRECT_FORMAT)
			{	
				throw new FailedToLoadException("This machine file is no longer supported.");
				//object = DirectSaveManager.getInstance().load(fileInput) ;
			}else if (generalFileFormat == MVC_FORMAT)
			{
				fileInput.mark(100);
				
				int header1 = fileInput.read();
				int header2 = fileInput.read();

				fileInput.reset();
				final DataInputStream input;
				if (header1 == GZIP_HEADER_1 && header2 == GZIP_HEADER_2)
				{//this is a gzip file
					//double buffering this - that is, buffering around the gzipping, massively improves speed.
					input = new DataInputStream(new BufferedInputStream (new GZIPInputStream(fileInput)));
				}else
				{
					input = new DataInputStream(fileInput);
				}
				object = DataTypeReader.readWithHeader( input);
			}else if (generalFileFormat == HEADER_MVC_FORMAT)
			{	
				//first read and interpreter the header.
				
				int headerBytes = dataInput.readInt();//the header is the next int bytes
				
				byte[] header = new byte[headerBytes];
				if(dataInput.read(header) != headerBytes)
					throw new FailedToLoadException("Save File Header Corrupted");
				
				ByteArrayInputStream headerInput = new ByteArrayInputStream(header);
				
				//read the header, which for now just begins with three ints indicating the current version.
				DataInputStream headerData = new DataInputStream(headerInput);
				
				saveFileMajorVersion = headerData.readInt();
				saveFileMinorVersion = headerData.readInt();
				saveFileRevision = headerData.readInt();
				
				//ok.
				final DataInputStream input;
				
				fileInput.mark(100);
				
				int header1 = fileInput.read();
				int header2 = fileInput.read();

				fileInput.reset();
				
				if (header1 == GZIP_HEADER_1 && header2 == GZIP_HEADER_2)
				{//this is a gzip file
					//double buffering this - that is, buffering around the gzipping, massively improves speed.
					input = new DataInputStream(new BufferedInputStream (new GZIPInputStream(fileInput)));
				}else
				{
					input = new DataInputStream(fileInput);
				}
				object = DataTypeReader.readWithHeader( input);
				
			}else
				throw new IOException("Not A Recognized Format");

			if (object instanceof PropertyStore)
			{
				PropertyStore store = (PropertyStore) object;
				new SaveTransformer().apply(store,saveFileMajorVersion,saveFileMinorVersion,saveFileRevision);
				return  store;
			}
			
		}catch (IOException e)
		{
			throw new FailedToLoadException(e);
		}catch(Exception e)
		{
			throw new FailedToLoadException(e);
		}
		throw new FailedToLoadException();
	}
	public static class FailedToLoadException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public FailedToLoadException() {
			super();
		}

		public FailedToLoadException(String message, Throwable cause) {
			super(message, cause);
		}

		public FailedToLoadException(String message) {
			super(message);
		}

		public FailedToLoadException(Throwable cause) {
			super(cause);
		}	
		
	}
}
