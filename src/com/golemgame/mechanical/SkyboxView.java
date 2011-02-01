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
package com.golemgame.mechanical;

import java.io.IOException;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.SkyboxInterpreter;
import com.golemgame.states.StateManager;
import com.golemgame.states.camera.skybox.SkyBoxData;
import com.golemgame.states.camera.skybox.SkyBoxDataFactory;

public class SkyboxView implements SustainedView {

	private SkyboxInterpreter interpreter;
	
	
	public SkyboxView(PropertyStore store) {
		super();
		interpreter = new SkyboxInterpreter(store);
		store.setSustainedView(this);
	}

	public void refresh() {
		SkyBoxData skyBox;
		try {
			skyBox = SkyBoxDataFactory.getInstance().construct(getStore());
			StateManager.getCameraManager().getSkyBoxManager().setSkyBox(skyBox);
		} catch (IOException e) {
			StateManager.logError(e);
		}
	}

	public PropertyStore getStore() {
		
		return interpreter.getStore();
	}

	public void remove() {
		
	}

}
