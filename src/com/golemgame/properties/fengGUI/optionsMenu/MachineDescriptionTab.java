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

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.ScrollContainer;
import org.fenggui.TextEditor;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;
import org.fenggui.util.Alignment;

import com.golemgame.local.StringConstants;
import com.golemgame.mechanical.MachineSpace;
import com.golemgame.properties.fengGUI.TabAdapter;
import com.golemgame.states.StateManager;

public class MachineDescriptionTab extends TabAdapter {

	private TextEditor description;
	private TextEditor name;
	private ScrollContainer desScroller;
	public MachineDescriptionTab() {
		super(StringConstants.get("MAIN_MENU.MACHINE","Description"));
		
	}
	
	@Override
	protected void buildGUI() {
		getTab().setLayoutManager(new BorderLayout());
		Container nameContainer = FengGUI.createContainer(getTab());
		nameContainer.setLayoutData(BorderLayoutData.NORTH);
		getTab().addWidget(nameContainer);
		nameContainer.setLayoutManager(new RowLayout());
		FengGUI.createLabel(nameContainer, StringConstants.get("MAIN_MENU.MACHINE.NAME" ,"Name: ")).setExpandable(false);
		name = FengGUI.createTextEditor(nameContainer);
		
		Container desContainer = FengGUI.createContainer(getTab());
		desContainer.setLayoutData(BorderLayoutData.CENTER);
		getTab().addWidget(desContainer);
		desContainer.setLayoutManager(new BorderLayout());
		Container labContainer = FengGUI.createContainer(desContainer);
		labContainer.setLayoutData(BorderLayoutData.NORTH);
		labContainer.setShrinkable(false);
		//labContainer.setExpandable(false);
		labContainer.setLayoutManager(new BorderLayout());
		Label desLab = FengGUI.createLabel(labContainer,StringConstants.get("MAIN_MENU.MACHINE.DESCRIPTION", "Description: "));
		desLab.setLayoutData(BorderLayoutData.WEST);
		//desLab.setExpandable(false);
		desScroller = FengGUI.createScrollContainer(desContainer);
		desScroller.setLayoutData(BorderLayoutData.CENTER);
		description = FengGUI.createTextEditor(desScroller);
		description.getAppearance().getData().setWordWarping(true);
		description.getAppearance().getData().setMultiline(true);
		description.setText("test\ntest");
		description.setHeight(300);
		description.getAppearance().setAlignment(Alignment.TOP_LEFT);
	}

	@Override
	public void close(boolean cancel) {
		if(!cancel){
			MachineSpace machine =StateManager.getMachineSpace();
			if(machine== null)
			{
				return;
			}else{
				machine.getInterpreter().setDescription(description.getText());
				machine.getInterpreter().setMachineSpaceName(name.getText());
			}
		}
		super.close(cancel);
	}

	@Override
	public void open() {
		super.open();
		MachineSpace machine =StateManager.getMachineSpace();
		if(machine== null)
		{
			description.setText("");
			name.setText("");
			return;
		}else{
			String descriptiont = machine.getInterpreter().getDescription();
			String namet = machine.getInterpreter().getMachineSpaceName();
			name.setText(namet);
			description.setText(descriptiont);
		}
	}

}
