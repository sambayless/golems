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
