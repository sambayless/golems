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

import com.golemgame.local.StringConstants;
import com.golemgame.properties.fengGUI.HorizontalTabbedTab;
import com.golemgame.properties.fengGUI.ImageTab;
import com.golemgame.states.StateManager;
import com.jme.image.Texture;

public class SettingsTab extends HorizontalTabbedTab implements ImageTab{
	
	
	private final Texture icon;
	private RecordingOptionsTab recordingTab;
	public SettingsTab() {
		super( StringConstants.get("MAIN_MENU.SETTINGS" ,"Settings"));
	
		 icon =super.loadTexture("buttons/menu/Display.png");
		 recordingTab = new  RecordingOptionsTab();
		 //add subtabs:
		 DisplayModeTab disp = new DisplayModeTab();
		 this.addTab(disp);
		 if(StateManager.SOUND_ENABLED)
			 this.addTab(new SoundOptionsTab());
		 this.addTab(new KeyMappingsTab());
		 this.addTab(recordingTab);
		 
	//	 MemoryTab mem = new MemoryTab();
		
		
		// disp.embed(mem);
	}
	


	@Override
	public Texture getIcon() {
		return icon;
	}



	public void showRecordingTab(){
		this.displayTab(recordingTab);
	}
}
