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
package com.golemgame.states.record;

import java.io.File;
import java.util.Date;

public class RecordingSession {
	public static enum Format
	{
		AVI("AVI"),MOV("QuickTime");
		private final String name;

		private Format(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
		
	}
	
	public static enum Compression
	{
		JPG("Lossy");//,PNG("Lossless (Slow)");//,BMP("None");
		private final String name;

		private Compression(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
		
	}
	
	private Format format;
	private Compression compression;
	private float quality;
	private File destination;
	
	private Date recordingStart;
	private long currentSize;
	private boolean showMouse = true;
	private float fps = 30;
	public float getFps() {
		return fps;
	}
	public void setFps(float fps) {
		this.fps = fps;
	}
	public boolean isShowMouse() {
		return showMouse;
	}
	public void setShowMouse(boolean showMouse) {
		this.showMouse = showMouse;
	}
	private boolean closed = false;
	
	
	public boolean isClosed() {
		return closed;
	}
	public void close(){
		if(!closed)
		{
			
			this.closed = true;
		}
	}
	public Format getFormat() {
		return format;
	}
	
	
	
	public void setFormat(Format format) {
		this.format = format;
	}
	public Compression getCompression() {
		return compression;
	}
	public void setCompression(Compression compression) {
		this.compression = compression;
	}
	public float getQuality() {
		return quality;
	}
	public void setQuality(float quality) {
		this.quality = quality;
	}
	public File getDestination() {
		return destination;
	}
	public void setDestination(File destination) {
		this.destination = destination;
	}
	public Date getRecordingStart() {
		return recordingStart;
	}
	public void setRecordingStart(Date recordingStart) {
		this.recordingStart = recordingStart;
	}
	public long getCurrentSize() {
		return currentSize;
	}
	public void setCurrentSize(long currentSize) {
		this.currentSize = currentSize;
	}
	
	
}
