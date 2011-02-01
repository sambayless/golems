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
package com.golemgame.properties.fengGUI.optionsMenu;

import org.fenggui.CheckBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.TextEditor;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.ITextChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.event.TextChangedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;

import com.golemgame.local.StringConstants;
import com.golemgame.properties.fengGUI.TabAdapter;
import com.golemgame.settings.SettingChangedEvent;
import com.golemgame.settings.SettingsListener;
import com.golemgame.states.GeneralSettings;

public class SoundOptionsTab  extends TabAdapter {

	private  CheckBox<?> soundEnabled;
	private TextEditor volume;
	
	public SoundOptionsTab() {
		super(StringConstants.get("MAIN_MENU.SOUND" ,"Sound"));
		
	}
	
	@Override
	protected void buildGUI() {
		getTab().setLayoutManager(new BorderLayout());
		
		Container northContainer = FengGUI.createContainer(getTab());
		northContainer.setLayoutData(BorderLayoutData.NORTH);
		northContainer.setLayoutManager(new RowLayout(false));
		
		 soundEnabled = FengGUI.createCheckBox(northContainer,StringConstants.get("MAIN_MENU.SOUND.ENABLED" , "Sound Enabled"));
		//soundEnabled.setLayoutData(BorderLayoutData.NORTH);
		Container volumeRow= FengGUI.createContainer(northContainer);
		volumeRow.setLayoutManager(new RowLayout());
		GeneralSettings.getInstance().getSoundEnabled().addSettingsListener(new SettingsListener<Boolean>()
				{

					public void valueChanged(SettingChangedEvent<Boolean> e) {
						if(soundEnabled.isSelected()!=e.getNewValue())
							soundEnabled.setSelected(e.getNewValue());
					}
			
				},true);
		
		volume = FengGUI.createTextEditor(volumeRow);
	
		GeneralSettings.getInstance().getVolume().addSettingsListener(new SettingsListener<Float>()
				{

					public void valueChanged(SettingChangedEvent<Float> e) {
						volume.setText(String.valueOf(e.getNewValue()*100));
					}
			
				},true);
		
		volume.addTextChangedListener(new ITextChangedListener()
		{

			public void textChanged(TextChangedEvent textChangedEvent) {
				try{
					float vol = Float.valueOf(volume.getText())/100f;
					if(vol>1f)
						vol = 1f;
					if(vol<0f)
						vol = 0f;
					GeneralSettings.getInstance().getVolume().setValue(vol);
				}catch(NumberFormatException  n)
				{
					
				}
			}
			
		});
		
		FengGUI.createButton(volumeRow,StringConstants.get("MAIN_MENU.SOUND.MUTE" ,"Mute")).addButtonPressedListener(new IButtonPressedListener()
		{

			public void buttonPressed(ButtonPressedEvent e) {
				volume.setText("0");
			}
			
		});
		
		final CheckBox<?> showControls = FengGUI.createCheckBox(northContainer,StringConstants.get("MAIN_MENU.SOUND.SHOW_CONTROLS" ,"Show Volume Controls"));
		showControls.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent selectionChangedEvent) {
				GeneralSettings.getInstance().getShowSoundControls().setValue(selectionChangedEvent.isSelected());
			}
			
		});
		
		GeneralSettings.getInstance().getShowSoundControls().addSettingsListener(new SettingsListener<Boolean>(){

			public void valueChanged(SettingChangedEvent<Boolean> e) {
				if(e.getNewValue()!=showControls.isSelected())
					showControls.setSelected(e.getNewValue());
			}
			
		},true);
	}

	@Override
	public void close(boolean cancel) {
		GeneralSettings.getInstance().getSoundEnabled().setValue(soundEnabled.isSelected());
		super.close(cancel);
	}

	@Override
	public void open() {
		volume.setText(String.valueOf(GeneralSettings.getInstance().getVolume().getValue()*100f));//set it here, in case the user left a non-number in the text box.
		super.open();
	}
	
	
}
