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
package com.golemgame.states.construct;

import com.golemgame.states.GeneralSettings;
import com.golemgame.states.StateManager;
import com.golemgame.tool.ActionToolSettings;
import com.jme.math.Vector3f;

public class CameraLocation implements ConstructionLocation {

	private float distanceFromCamera = 25;
	
	public void getBuildLocation(Vector3f store) {
		
		Vector3f camNormal = StateManager.getCameraManager().getCameraNormal();
	
		Vector3f camPosition = StateManager.getCameraManager().getCameraLocationNode().getWorldTranslation();
		//float zoom = StateManager.getCameraManager().getCameraZoom();

		float minDist = (ActionToolSettings.getInstance().getGridUnits().getValue().length());
		if(StateManager.getCameraManager().getCameraZoom() - distanceFromCamera>minDist)
		{
			store.set(camPosition).addLocal(camNormal.mult(-distanceFromCamera));
		}else{
			store.set(StateManager.getCameraManager().getCameraPosition()).addLocal(camNormal.mult(minDist));
		}
		
	}

}
