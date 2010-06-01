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
