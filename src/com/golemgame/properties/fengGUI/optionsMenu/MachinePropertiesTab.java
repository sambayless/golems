package com.golemgame.properties.fengGUI.optionsMenu;

import com.golemgame.local.StringConstants;
import com.golemgame.properties.fengGUI.HorizontalTabbedTab;
import com.jme.image.Texture;

public class MachinePropertiesTab extends HorizontalTabbedTab {


	private final Texture icon;
	
	public MachinePropertiesTab() {
		super(StringConstants.get("MAIN_MENU.MACHINE" ,"Machine"));
	
		 icon =super.loadTexture("buttons/special/Gear.png");
		 
		 //add subtabs:
		 this.addTab(new MachineDescriptionTab());
		// this.addTab(new MachinePictureTab());
	}
	
	@Override
	public Texture getIcon() {
		return icon;
	}
}
