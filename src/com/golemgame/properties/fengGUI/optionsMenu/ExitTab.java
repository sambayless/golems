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

import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.CenteringLayout;
import org.fenggui.layout.RowLayout;

import com.golemgame.local.StringConstants;
import com.golemgame.properties.fengGUI.ConfirmationBox;
import com.golemgame.properties.fengGUI.ImageTabAdapter;
import com.golemgame.properties.fengGUI.mainmenu.MainMenu;
import com.golemgame.states.GUILayer;
import com.golemgame.states.GeneralSettings;
import com.golemgame.states.StateManager;
import com.golemgame.tool.action.UndoManager;
import com.jme.image.Texture;


public class ExitTab extends ImageTabAdapter {

	private final MainMenu menu;
	
	private Texture hover;
	private Texture active; 
	private Texture inactive;
	private Texture icon;
	
	public ExitTab(MainMenu menu) {
		super(StringConstants.get("MAIN_MENU.EXIT","Exit"));
		this.menu = menu;	
		
		 hover = super.loadTexture("buttons/glass/Glass_Red.png");
		 active =super.loadTexture("buttons/solid/Solid_Red.png");
		 inactive =super.loadTexture("buttons/solid/Solid_Red.png");
		 icon =super.loadTexture("buttons/menu/Power.png");
		
	}

	
	@Override
	public Texture getActive() {
		return active;
	}


	@Override
	public Texture getHover() {
		return hover;
	}


	@Override
	public Texture getIcon() {
		return icon;
	}


	@Override
	public Texture getInactive() {
		return inactive;
	}

	private boolean userConfirm(String title, String message) {
		return ConfirmationBox.showBlockingConfirmBox(title, message, GUILayer.getInstance().getDisplay());
	}
	
	protected void buildGUI() {

		Container centeringContainer = FengGUI.createContainer(getTab());
		
		centeringContainer.setLayoutManager(new CenteringLayout());
		
		Container rowVertical = FengGUI.createContainer(centeringContainer);
		rowVertical.setLayoutManager(new RowLayout(false));
		
		FengGUI.createLabel(rowVertical,StringConstants.get("MAIN_MENU.EXIT.CHECK","Are you sure you want to exit?"));
		Container rowHorizontal = FengGUI.createContainer(rowVertical);
		rowHorizontal.setLayoutManager(new RowLayout(true));
		
		Button ok = FengGUI.createButton(rowHorizontal, StringConstants.get("MAIN_MENU.YES" ,"Yes"));
		Button cancel = FengGUI.createButton(rowHorizontal,StringConstants.get("MAIN_MENU.NO" ,"No") );
		
		ok.addButtonPressedListener(new IButtonPressedListener()
		{

			
			public void buttonPressed(ButtonPressedEvent e) {
				menu.close();
				StateManager.getThreadPool().execute(new Runnable(){

					public void run() {
						
						if(GeneralSettings.getInstance().getMachineChanged().isValue() && !userConfirm("Exit","Are you sure you want to exit without saving first?"))
							return;
						StateManager.exit();
					}
				});
				
			
				
			}
			
		});
		
		cancel.addButtonPressedListener(new IButtonPressedListener()
		{

			
			public void buttonPressed(ButtonPressedEvent e) {
				close(true);
				
			}
			
		});
	}

	
	public void close(boolean cancel) {
		{
			menu.showDefaultTab();
		}
	}
	
	

}
