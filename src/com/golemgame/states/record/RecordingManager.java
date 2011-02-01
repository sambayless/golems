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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.lwjgl.opengl.GL11;

import ch.randelshofer.media.avi.AVIOutputStream;
import ch.randelshofer.media.quicktime.QuickTimeOutputStream;

import com.golemgame.states.StateManager;
import com.golemgame.states.record.RecordingSession.Compression;
import com.golemgame.states.record.RecordingSession.Format;
import com.jme.image.Texture;
import com.jme.renderer.TextureRenderer;
import com.jme.system.DisplaySystem;
import com.jme.util.Timer;

/**
 * This class is responsible for managing video recordings. 
 * @author Sam
 *
 */
public class RecordingManager {

	
	private final Lock recordingLock = new ReentrantLock();
	
	public Lock getRecordingLock() {
		return recordingLock;
	}

	private Timer frameTimer;
	private float desiredPeriod;
	private IntBuffer buff;
	private TextureRenderer tRenderer;
	private int width;
	private int height;
	private Texture texture;
	private  BufferedImage img ;
	private  BufferedImage bufferImg;
	private boolean paused = false;
	private CopyOnWriteArrayList<RecordingListener> listeners = new CopyOnWriteArrayList<RecordingListener>();
	public CopyOnWriteArrayList<RecordingListener> getListeners() {
		return listeners;
	}
	public void registerListener(RecordingListener listener)
	{
		listeners.add(listener);
	}
	public void removeListener(RecordingListener listener)
	{
		listeners.remove(listener);
	}
	private int[] data;
	public RecordingManager() {
		super();
		frameTimer = Timer.getTimer(); 
	}

	private boolean isRecording = false;

	private RecordingSession currentSession;
	
	private QuickTimeOutputStream quickOut;
	private AVIOutputStream aviOut;
	
	private float lastFrameTime = 0L;
 

	public void newSession(RecordingSession session) throws IOException {
		if(currentSession ==session)
			return;
		
		recordingLock.lock();
		try{
			if(currentSession != null)
			{
				closeSession();
			}
			
			currentSession = session;
			session.setRecordingStart(new Date());
		
			
			//out = (new FileOutputStream(session.getDestination()));//, 1048576 * 2);//2 megabyte buffer

			desiredPeriod = 1f/session.getFps();
			
			switch(session.getFormat())
			{
				case AVI:
				{
					AVIOutputStream.VideoFormat format = AVIOutputStream.VideoFormat.JPG;
				//	if (session.getCompression() == Compression.PNG)
				//		format = AVIOutputStream.VideoFormat.PNG;
					if (session.getCompression() == Compression.JPG)
						format = AVIOutputStream.VideoFormat.JPG;
					/*if (session.getCompression() == Compression.BMP)
						format = AVIOutputStream.VideoFormat.BMP;*/
					
					  aviOut = new AVIOutputStream(session.getDestination(), format);
					  aviOut.setVideoCompressionQuality(session.getQuality());
			          
					  aviOut.setTimeScale(1); // 30 fps
					  aviOut.setFrameRate((int)session.getFps());
					break;
				}
				case MOV:
				{					
			            
			            QuickTimeOutputStream.VideoFormat format =QuickTimeOutputStream.VideoFormat.JPG;
			           // if (session.getCompression() == Compression.PNG)
					//		format = QuickTimeOutputStream.VideoFormat.PNG;
			            if (session.getCompression() == Compression.JPG)
							format = QuickTimeOutputStream.VideoFormat.JPG;
			        
						  quickOut = new QuickTimeOutputStream(session.getDestination(), format);
						  quickOut.setVideoCompressionQuality(session.getQuality());
				          
						  quickOut.setTimeScale((int)session.getFps()); // 30 fps
						 
					break;
				}
			}
		
			width= DisplaySystem.getDisplaySystem().getWidth();
			height = DisplaySystem.getDisplaySystem().getHeight();
						tRenderer  = DisplaySystem.getDisplaySystem().createTextureRenderer(width, height, TextureRenderer.RENDER_TEXTURE_2D);
			texture = new Texture();
	    	texture.setMipmapState(Texture.MM_NONE);
	    	texture.setFilter(Texture.FM_NEAREST);	  
	    	buff = ByteBuffer.allocateDirect(tRenderer.getWidth()*tRenderer.getHeight()*4).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
	    	//little endian or native?
	    	img = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
	    	bufferImg =  new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
	    	data = new int[width*height];
	    	tRenderer.setupTexture(texture);
	    	
			this.isRecording = true;
			lastFrameTime = frameTimer.getTimeInSeconds()-1f/getDesiredFPS();//so we dont skip the first frame
		}finally{
			recordingLock.unlock();
		}
		broadcaseState();
	}

	public void closeSession() throws IOException {
		recordingLock.lock();
		try{
			if(currentSession != null)
			{
				currentSession.close();	
				if(currentSession.getFormat() == Format.AVI)
				{
					if(aviOut != null)
						aviOut.close();
					
				}else if (currentSession.getFormat() == Format.MOV){
					if(quickOut != null)
						quickOut.close();
				}
				
				quickOut = null;
				aviOut = null;
				this.isRecording = false;
				currentSession = null;
			}
		}catch(IndexOutOfBoundsException e){
			StateManager.logError(e);
		}finally{
			recordingLock.unlock();
		}
		broadcaseState();
	}
	
	public float getTimeSinceLastFrame()
	{
		float time =  (frameTimer.getTimeInSeconds())-lastFrameTime;
		return time;
	}
	
	public float getDesiredFPS()
	{
		//should lock this?
		if(currentSession!=null)
			return currentSession.getFps();
		return 0f;
	}
	public float getDesiredPeriod()
	{
		return desiredPeriod;
	}
	
	
	
	/**
	 * Could improve performance by having a buffer of images, and writing them out in a separate thread...
	 * @param toRecord
	 */
	public void drawFrame()
	{	
		this.getRecordingLock().lock();
		try{
		if(!isRecording() || currentSession == null)
			return;
		if(isPaused())
			return;
		
		BufferedImage img = captureScreenRender();
		
		if(aviOut!=null)
		{	
			/*if(currentSession.getCompression()==Compression.PNG)
			{
				try {	
					long time = System.nanoTime();
					aviOut.setVideoDimension(width, height);
		
					aviOut.writeFrame(new PngEncoder(img).pngEncode(false));
					System.out.println( System.nanoTime()-time);
				} catch (IOException e) {
					StateManager.logError(e);
					try {
						closeSession();
					} catch (IOException e1) {
						StateManager.logError(e1);
					}
				}
			}else{*/
				try {		
					
				
						aviOut.writeFrame(img);
					} catch (IOException e) {
						StateManager.logError(e);
						try {
							closeSession();
						} catch (IOException e1) {
							StateManager.logError(e1);
						}
					}
			//}
		/*	try {
				
				long time = System.nanoTime();
				Sanselan.writeImageToBytes(img,
						ImageFormat.IMAGE_FORMAT_PNG, null);
				//Sanselan.writeImage(img, null, null, null)
				System.out.println( System.nanoTime()-time);
			} catch (ImageWriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//aviOut.writeFrame( n)
			//aviOut.writeFrame(img);
		} catch (IOException e) {
			StateManager.logError(e);
			try {
				closeSession();
			} catch (IOException e1) {
				StateManager.logError(e1);
			}
		}*/
			
		/*	try {
				PngEncoder encoder = new PngEncoder();
				encoder.setImage(img);
				encoder.setCompressionLevel(0);
				long time = System.nanoTime();
				byte[] b = encoder.pngEncode(false);
				System.out.println( System.nanoTime()-time);
				aviOut.writeFrame( new ByteArrayInputStream(b));
				//aviOut.writeFrame(img);
			} catch (IOException e) {
				StateManager.logError(e);
				try {
					closeSession();
				} catch (IOException e1) {
					StateManager.logError(e1);
				}
				
			}*/
		}
		if(quickOut!=null)
		{
			try {
				quickOut.writeFrame(img,1);
			} catch (IOException e) {
				StateManager.logError(e);
				try {
					closeSession();
				} catch (IOException e1) {
					StateManager.logError(e1);
				}
				
			}
		}
		lastFrameTime = frameTimer.getTimeInSeconds();
		for(RecordingListener l:listeners)
		{
			l.frameDrawn(currentSession, this);
		}
		}finally{
			this.getRecordingLock().unlock();
		}
	}
	
	  public BufferedImage captureScreenRender()
		{
/*		  long[] time = new long[8];
		  int t = 0;
		  time[t++] = System.nanoTime();
	    	Renderer r = DisplaySystem.getDisplaySystem().getRenderer();*/
	    	
	    /*	//set the texture renderers camera to match the worlds camera
	    	tRenderer.getCamera().setLocation(r.getCamera().getLocation());
	    	tRenderer.getCamera().setAxes(r.getCamera().getLeft(),r.getCamera().getUp(),r.getCamera().getDirection() );
	    	
	    	tRenderer.getCamera().update();
	    	 time[t++] = System.nanoTime();
	    
	    	//tRenderer.render(StateManager.getMachineSpace().getSpatial(),texture);
	    	if(StateManager.getCameraManager().getSkyBoxManager().isEnabled())
	    	{
	    		boolean overlayenabled = StateManager.getCameraManager().getSkyBoxManager().isOverlayEnabled();
	    		StateManager.getCameraManager().getSkyBoxManager().setTextureRenderMode(true);
	    		StateManager.getCameraManager().getSkyBoxManager().setOverlayEnabled(false);
	    		//StateManager.getCameraManager().getSkyBoxManager().
	    		//something magical has to happen after the sky box has its texture reset before the render will succeed.
	    		//StateManager.getCameraManager().getSkyBoxManager().getSkyBoxSpatial().updateGeometricState(0, true);
	    		
	    		//StateManager.getCameraManager().getCameraLocationNode().updateRenderState();
	    		//StateManager.getDesignState().getRootNode().updateRenderState();
	    		
	    		//StateManager.getCameraManager().getSkyBoxManager().setOverlayEnabled(true);
	    	//	StateManager.getDesignState().getRootNode().updateRenderState();
	    	//	tRenderer.render(StateManager.getCameraManager().getSkyBoxManager().getSkyBoxSpatial(),texture);
	    		for(Spatial s:toRender)
	    		{
	    			if(s!=null)
	    				tRenderer.render(s,texture,true);
	    		}
	    		StateManager.getCameraManager().getSkyBoxManager().setTextureRenderMode(false);
	    		StateManager.getCameraManager().getSkyBoxManager().setOverlayEnabled(overlayenabled);
	    	}
	    	 time[t++] = System.nanoTime();
	 	    
	    		LWJGLTextureState.doTextureBind(texture.getTextureId(), 0);*/
	    	//	time[t++] = System.nanoTime();
	    	buff.rewind();
	    	
	    	if(StateManager.getGame().inGLThread())
	    	{
	    	//time[t++] = System.nanoTime();
	         // GL11.glGetTexImage(GL11.GL_TEXTURE_2D,0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buff);
	    		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buff);
	    	}else{
	    		try {
					StateManager.getGame().executeInGL(new Callable<Object>(){

						public Object call() throws Exception {
							GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buff);
							return null;
						}
						
					});
				} catch (Exception e) {
					StateManager.logError(e);
					
				}
	    	}
	         // time[t++] = System.nanoTime();
	  	    
	         
	          
	  	    
	          buff.get(data);
	        //  time[t++] = System.nanoTime();
	  	    
	         //bgr is important - thats the order that matches the gl
	          
	  	    
	          img.getRaster().setDataElements(0, 0, width, height, data);
	          
	          Graphics2D g = bufferImg.createGraphics();
		       g.drawImage(img, 0, height, width, 0, 0, 0, width, height, null);
		       return bufferImg;
	   
		  /*     BufferedImage img2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
		       Graphics2D g = img2.createGraphics();
		       g.drawImage(img, 0, width, height, 0, 0, 0, width, height, null);
		       return img2;*/
		}

	public boolean isRecording() {
		return isRecording;
	}

	public RecordingSession getCurrentSession(){
		return currentSession;
	}
	public boolean isPaused() {
		return paused;
	}
	public void setPaused(boolean paused) {
		if(paused!= this.paused)
		{
			this.paused = paused;
			broadcaseState();
		}
	}
	
	private void broadcaseState()
	{
		for(RecordingListener listener:listeners)
			listener.recordingState(isRecording, paused);
	}

	
	
	
}
