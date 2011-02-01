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
package com.golemgame.properties.fengGUI;

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.TextEditor;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;
import org.fenggui.util.Spacing;

import com.golemgame.mvc.golems.InputInterpreter;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;

/**
 * This tab lets users select the device type, and name the device.
 * The actual device settings are in a sub tab
 * @author Sam
 *
 */
public class InputStructureTab extends PropertyTabAdapter{

	public InputStructureTab() {
		super("Input");
		
	}

	private InputInterpreter interpreter;

	private Container deviceContainer;



	private TextEditor nameText;



	public void close(boolean cancel) {
		super.close(cancel);
		if(!cancel)
		{
		
			
			super.standardClosingBehaviour(nameText, InputInterpreter.NAME,Format.String);
	
			
			Action<?> apply = super.apply();
			
			UndoManager.getInstance().addAction(apply);
			
			
		}
		
	}
	


	protected void buildGUI()
	{
		

        getTab().setLayoutManager(new BorderLayout());
        Container nameContainer = new Container();
        nameContainer.setLayoutData(BorderLayoutData.NORTH);
        getTab().addWidget(nameContainer);
        nameContainer.setLayoutManager(new RowLayout());
        FengGUI.createLabel(nameContainer,"Input Device Name");
        nameText = FengGUI.createTextField(nameContainer);
      
        nameText.getAppearance().setPadding(new Spacing(0,0,55,0));
       nameText.getAppearance().getData().setMultiline(false);
        
        nameText.getAppearance().getData().setWordWarping(false);
        nameText.setText(".           .");
   

  
		
        getTab().layout();
		
	}


	
	public void open() {
		super.open();
		this.interpreter = new InputInterpreter(super.getPropertyStoreAdjuster().getPrototype());
		this.interpreter.loadDefaults();
		initializePrototype();
		super.setComparePoint();
		

		super.associateWithKey(nameText, InputInterpreter.NAME);

		super.standardOpeningBehaviour(nameText, InputInterpreter.NAME,Format.String);//this has to happen first

	}



	@Override
	public boolean embed(ITab tab) {
		if(tab instanceof InputDeviceTab)
		{
			super.getEmbeddedTabs().add(tab);
			tab.getTab().setLayoutData(BorderLayoutData.CENTER);
			getTab().addWidget(tab.getTab());
			getTab().layout();
			return true;
		}
		return super.embed(tab);
	}
	
}
