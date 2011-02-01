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
package com.golemgame.toolbar;

import com.jme.light.DirectionalLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.SceneElement;
import com.jme.scene.state.LightState;
import com.jme.system.DisplaySystem;

public class StandardToolbar extends Toolbar {
	
	public StandardToolbar()
	{
		this("Unnamed");
	}
	
	public StandardToolbar(String name) {
		super(name);
		super.setPermeable(true);
		getSpatial().setCullMode(SceneElement.CULL_NEVER);
	
		getSpatial().setLightCombineMode(LightState.REPLACE);
		
		DirectionalLight light = new DirectionalLight();
        light.setDiffuse( new ColorRGBA( 1f, 1f, 1f, 1f ) );
        light.setAmbient( new ColorRGBA( 1f, 1f, 1f, 1.0f ) );
        light.setDirection(new Vector3f(0,0,-1) );
        light.setEnabled( true );
        
        LightState lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
        lightState.setEnabled( true );
        lightState.attach( light );
        
        getSpatial().setRenderState(lightState);
        
	}
	
	@Override
	protected void engage(boolean engage) {		

	}

	
}
