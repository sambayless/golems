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

import org.fenggui.FengGUI;
import org.fenggui.TextEditor;
import org.fenggui.layout.RowLayout;

import com.golemgame.local.StringConstants;
import com.golemgame.properties.fengGUI.TabAdapter;
import com.golemgame.states.GeneralSettings;

public class MemoryTab extends TabAdapter  {
	private TextEditor mem;
	public MemoryTab() {
		super(StringConstants.get("MAIN_MENU.DISPLAY.MEMORY" ,"Memory"));
		
	}

	@Override
	protected void buildGUI() {
		
		getTab().setLayoutManager(new RowLayout());
		FengGUI.createLabel(getTab(), "Maximum Memory (in MB)");
		mem = FengGUI.createTextEditor(getTab());
		
		
	}

	@Override
	public void close(boolean cancel) {
		
		try{
			int memory = Integer.valueOf(mem.getText());
			if(memory<64)
				memory = 64;
			GeneralSettings.getInstance().getMaxMemory().setValue(memory);
		}catch(NumberFormatException e){
			
		}
		
		super.close(cancel);
	}

	@Override
	public void open() {
		mem.setText(String.valueOf(GeneralSettings.getInstance().getMaxMemory().getValue()));
		super.open();
	}

}
