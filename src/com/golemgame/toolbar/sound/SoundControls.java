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
package com.golemgame.toolbar.sound;

import java.awt.Color;

import com.golemgame.settings.SettingChangedEvent;
import com.golemgame.settings.SettingsListener;
import com.golemgame.states.GeneralSettings;
import com.jme.image.Texture;
import com.jme.scene.state.AlphaState;
import com.jme.system.DisplaySystem;
import com.simplemonkey.Container;
import com.simplemonkey.layout.BorderLayout;
import com.simplemonkey.layout.BorderLayoutData;
import com.simplemonkey.widgets.SimpleButton;
import com.simplemonkey.widgets.SimpleButtonListener;

public class SoundControls extends Container {

	private VolumeMeter volume;
	public SoundControls() {
		super();
	
		GeneralSettings.getInstance().getSoundEnabled().addSettingsListener(new SettingsListener<Boolean>()
				{

					public void valueChanged(SettingChangedEvent<Boolean> e) {
						SoundControls.this.setVisible(e.getNewValue() && GeneralSettings.getInstance().getShowSoundControls().isValue());
					}
			
				},true);
		
		GeneralSettings.getInstance().getShowSoundControls().addSettingsListener(new SettingsListener<Boolean>()
				{

					public void valueChanged(SettingChangedEvent<Boolean> e) {
						SoundControls.this.setVisible(e.getNewValue()  && GeneralSettings.getInstance().getSoundEnabled().isValue());
					}
			
				},true);
	//	this.setVisible(false);
	
		volume = new VolumeMeter();
	  	
	  
	 	
	 	/*final StandardButton pause= new StandardButton(32, 32, runActive, runInactive, pauseHover);
	 	final StandardButton resume= new StandardButton(32, 32, runActive, runInactive, pauseHover);
	 	final StandardButton stop = new StandardButton(32, 32, runActive, runInactive, runHover);
*/
		
	  	final SimpleButton mute= new SimpleButton(" Mute");

	  	mute.setTextUnit(0);
	  	mute.setTextTextureApplyMode(Texture.AM_MODULATE);
	
	 	mute.setTextColor(Color.gray);
	 	{
	/*	MaterialState white;
			white = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
			white.setAmbient(ColorRGBA.blue);
			white.setColorMaterial(MaterialState.CM_NONE );
			white.setMaterialFace(MaterialState.MF_FRONT);*/
			
			AlphaState semiTransparentAlpha = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
	
			semiTransparentAlpha.setBlendEnabled(true);
			semiTransparentAlpha.setSrcFunction(AlphaState.SB_SRC_ALPHA);
			semiTransparentAlpha.setDstFunction(AlphaState.SB_ONE_MINUS_SRC_ALPHA);
			semiTransparentAlpha.setTestEnabled(true);		
			semiTransparentAlpha.setTestFunction(AlphaState.TF_GREATER);		
			
			//mute.getSpatial().setRenderState(white);
			
			
			
			mute.getSpatial().setRenderState(semiTransparentAlpha);
			

			
			mute.getSpatial().updateRenderState();
		
	}

		mute.getSpatial().updateWorldVectors();
	
		Container buttonContainer = new Container();
		
		mute.setTextUnit(0);
		mute.setTexture(null, 1);
		
		buttonContainer.addWidget(mute);

		buttonContainer.addWidget(volume);
		volume.setMinSize(32, 128);
		volume.setExpandable(false);
		volume.setShrinkable(false);
		volume.setSizeToMinSize();
		this.addWidget(buttonContainer);
		mute.setLayoutData(BorderLayoutData.NORTH);
		buttonContainer.addWidget(mute);
		buttonContainer.addWidget(volume);
		volume.setLayoutData(BorderLayoutData.CENTER);
		buttonContainer.setLayoutData(BorderLayoutData.NORTH);
	
		this.setLayoutManager(new BorderLayout());
		buttonContainer.setLayoutManager(new BorderLayout());
		
		this.layout();
		
		mute.addButtonListener(new SimpleButtonListener(){

			public void buttonPressed() {
				GeneralSettings.getInstance().getMuteSound().setValue(!GeneralSettings.getInstance().getMuteSound().isValue());
			
				if(GeneralSettings.getInstance().getMuteSound().isValue())
				{
					mute.setText(" Play");
				}else
				{
					mute.setText(" Mute");
				}
				mute.setExpandable(true);
			  	mute.setTextUnit(0);
			  	mute.setTextTextureApplyMode(Texture.AM_MODULATE);
			
			 	mute.setTextColor(Color.gray);
				mute.layout();
				
			}
			
		});
		
		GeneralSettings.getInstance().getVolume().addSettingsListener(new SettingsListener<Float>()
				{

					public void valueChanged(SettingChangedEvent<Float> e) {					
							volume.setVolume(e.getNewValue());
					}
			
				},true);
		
		GeneralSettings.getInstance().getMuteSound().addSettingsListener(new SettingsListener<Boolean>()
				{

					public void valueChanged(SettingChangedEvent<Boolean> e) {
						volume.setMute(e.getNewValue());
						mute.setText(" Play");
					}

		
				},true);
	}


}
