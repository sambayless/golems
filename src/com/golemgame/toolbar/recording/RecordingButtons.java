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
package com.golemgame.toolbar.recording;

import java.awt.Color;
import java.io.IOException;
import java.util.Date;

import com.golemgame.states.StateManager;
import com.golemgame.states.record.RecordingListener;
import com.golemgame.states.record.RecordingManager;
import com.golemgame.states.record.RecordingSession;
import com.jme.image.Texture;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.simplemonkey.Container;
import com.simplemonkey.layout.BorderLayout;
import com.simplemonkey.layout.BorderLayoutData;
import com.simplemonkey.layout.RowLayout;
import com.simplemonkey.widgets.SimpleButton;
import com.simplemonkey.widgets.SimpleButtonListener;
import com.simplemonkey.widgets.TextWidget;

public class RecordingButtons extends Container {

	
	public RecordingButtons() {
		super();
	
		this.setVisible(false);
	
	  	
	  
	 	
	 	/*final StandardButton pause= new StandardButton(32, 32, runActive, runInactive, pauseHover);
	 	final StandardButton resume= new StandardButton(32, 32, runActive, runInactive, pauseHover);
	 	final StandardButton stop = new StandardButton(32, 32, runActive, runInactive, runHover);
*/
		
	  	final SimpleButton pause= new SimpleButton(" Pause ");
	 //	final SimpleButton resume= new SimpleButton(32, 32, runActive, runInactive, pauseHover);
	 	final SimpleButton stop =new SimpleButton(" Stop ");
		
		final TextWidget currentState = new TextWidget();
		currentState.setTextColor(Color.white);
		stop.setTextColor(Color.red);
	 	pause.setTextColor(Color.white);
	 	{
		MaterialState white;
			white = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
	
			white.setColorMaterial(MaterialState.CM_NONE );
			white.setMaterialFace(MaterialState.MF_FRONT);
			
			AlphaState semiTransparentAlpha = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
	
			semiTransparentAlpha.setBlendEnabled(true);
			semiTransparentAlpha.setSrcFunction(AlphaState.SB_SRC_ALPHA);
			semiTransparentAlpha.setDstFunction(AlphaState.SB_ONE_MINUS_SRC_ALPHA);
			semiTransparentAlpha.setTestEnabled(true);		
			semiTransparentAlpha.setTestFunction(AlphaState.TF_GREATER);		
			
			pause.getSpatial().setRenderState(white);
			stop.getSpatial().setRenderState(white);
			currentState.getSpatial().setRenderState(white);
			
			pause.getSpatial().setRenderState(semiTransparentAlpha);
			stop.getSpatial().setRenderState(semiTransparentAlpha);
			currentState.getSpatial().setRenderState(semiTransparentAlpha);
			
			Texture backgroundStripes = TextureManager.loadTexture(getClass().getClassLoader().getResource("com/golemgame/data/textures/menu/semi.png"),Texture.MM_LINEAR_NEAREST,Texture.MM_LINEAR);
			Texture backgroundStripes2 = backgroundStripes.createSimpleClone();
			Texture backgroundStripes3 = backgroundStripes.createSimpleClone();

			
			pause.setAllButtonTextues(backgroundStripes);
			stop.setAllButtonTextues(backgroundStripes2);
			currentState.setTexture(backgroundStripes3,0);
			
			pause.getSpatial().updateRenderState();
			stop.getSpatial().updateRenderState();
			currentState.getSpatial().updateRenderState();
	}

		pause.getSpatial().updateWorldVectors();
		stop.getSpatial().updateWorldVectors();
	//	resume.getSpatial().updateWorldVectors();
		Container buttonContainer = new Container();
		
	
		currentState.setLayoutData(BorderLayoutData.SOUTH);
		currentState.setText("Not Recording");
		buttonContainer.addWidget(currentState);
		
		buttonContainer.addWidget(pause);
	//	mainMenuGroup.addWidget(resume);
		buttonContainer.addWidget(stop);
		
		this.addWidget(buttonContainer);
		buttonContainer.setLayoutData(BorderLayoutData.CENTER);
		this.setLayoutData(BorderLayoutData.NORTH);
		this.setLayoutManager(new BorderLayout());
		buttonContainer.setLayoutManager(new RowLayout());
		
		this.layout();
	
		pause.addButtonListener(new SimpleButtonListener(){		

		
			public void buttonPressed() {
				StateManager.getRecordingManager().getRecordingLock().lock();
				try{
					if(StateManager.getRecordingManager().isRecording())
					{
						StateManager.getRecordingManager().setPaused(!StateManager.getRecordingManager().isPaused());
					}
				}finally{
					StateManager.getRecordingManager().getRecordingLock().unlock();
				}
			}
		});
		/*resume.addButtonListener(new ButtonListener(){		

			public void buttonStateChange(ButtonState state) {
				StateManager.getRecordingManager().getRecordingLock().lock();
				try{
					if(StateManager.getRecordingManager().isRecording())
					{
						StateManager.getRecordingManager().setPaused(false);
					}
				}finally{
					StateManager.getRecordingManager().getRecordingLock().unlock();
				}
			}
		});*/
		
		
		stop.addButtonListener(new SimpleButtonListener(){		

			
			public void buttonPressed() {
				StateManager.getRecordingManager().getRecordingLock().lock();
				try{
					try {
						StateManager.getRecordingManager().closeSession();						
					} catch (IOException e1) {
						StateManager.logError(e1);
					}
				}finally{
					StateManager.getRecordingManager().getRecordingLock().unlock();
				}
			}
		});
	
		
	//	this.addWidget(currentState);
		
		StateManager.getRecordingManager().registerListener(new RecordingListener(){
		 	private static final long MEGABYTE = 1048576L ;
			private static final long KILOBYTE = 1024L ;
			private static final long GIGABYTE = 1073741824L ;
			private String sessionLength = "0";
			private String sessionSize = "0";
		
			private boolean recording = false;
			private boolean paused = false;
			public void frameDrawn(RecordingSession session,
					RecordingManager source) {
				long msDif = new Date().getTime() - session.getRecordingStart().getTime();
				long hours = msDif/(1000 * 3600);
				
				long minutes =  msDif/(1000 * 60) - hours*60;
				long seconds =  msDif/(1000)- hours*60*60 - minutes*60;
			//	System.out.println(hours + ":" + minutes + ":" + seconds);
				 sessionLength = (hours + ":" + minutes + ":" + seconds );
				
				long length = session.getDestination().length();
				if(length <KILOBYTE)
				{
					sessionSize=(length + " bytes");
				}if(length < MEGABYTE)
				{
					sessionSize=(length/KILOBYTE + " KB");
				}else if  (length<GIGABYTE)
				{
					sessionSize=(length/MEGABYTE + " MB");
				}else{
					
					long lengthGB = length/GIGABYTE;
					long lengthRemainer = length%GIGABYTE;
					double percent =Math.round(10.0* ( ((double) lengthRemainer) /( (double)GIGABYTE)))/10.0;
					sessionSize=(lengthGB + "." + percent + " GB");
				}
				updateControls();
			}
			
			public void recordingState(boolean recording, boolean paused) {
				this.recording = recording;
				this.paused = paused;
				updateControls();
			}
			
			private void updateControls()
			{
				if(!recording)
				{
					RecordingButtons.this.setVisible(false);
					if(RecordingButtons.this.getParent()!=null)
					{
						RecordingButtons.this.getParent().getParent().getParent().layout();
					}
					//close the toolbar
				}else{
			
					currentState.setText(sessionLength + " - " + sessionSize);
					if(paused)
					{
						pause.setText(" Resume ");
						
					}else
						pause.setText(" Pause ");
					RecordingButtons.this.layout();
					RecordingButtons.this.setVisible(true);
					
					if(RecordingButtons.this.getParent()!=null)
					{
						RecordingButtons.this.getParent().getParent().getParent().layout();
					}
				}
			}
	 });

	}


}
