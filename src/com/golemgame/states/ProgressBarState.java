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
package com.golemgame.states;

import java.awt.Dimension;

import com.golemgame.settings.SettingChangedEvent;
import com.golemgame.settings.SettingsListener;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.game.state.GameState;

public class ProgressBarState extends GameState{
//	private static final ProgressBarState instance = new ProgressBarState();
	
//	
//	public static ProgressBarState getInstance() {
//		return instance;
//	}

	private Node progressNode;
	private Quad windowArea;
	private Quad progressBar;
	
	public static final float height=64;
	public static final float width= 256;
	
	private volatile float value = 0;
	private volatile boolean isLoaded = false;
	
	
	/*private float glintWidth = 32;
	private float glintSpeed = 1;

	private boolean glintEnabled = true;*/
	
	/**
	 * If true, this progress bar has no specific value - it just shows that it is working on something.
	 */
	private volatile boolean working = false;
	
	private TextureState barState;
//	private Texture glintTexture;
	private Texture barTexture;
	
	private volatile float delayedAppearance = 0;
	
	private volatile boolean fade = false;
	
	private float remainingDelay = 0;
	
	private float currentFade = 0;
	
	private MaterialState fadeState;
	
	//private float glintPosition=0;

	public ProgressBarState() {
		super();
		this.name = "Progress Bar";
		initState();
		setProgress(0);
	}
	
	

	private void initState()
	{
		Texture background = TextureManager.loadTexture(ProgressBarState.class.getClassLoader().getResource("com/golemgame/data/textures/progressBar/window2.png"));
		 barTexture = TextureManager.loadTexture(ProgressBarState.class.getClassLoader().getResource("com/golemgame/data/textures/progressBar/bar2.png"),true);
	//	 glintTexture = TextureManager.loadTexture(ProgressBarState.class.getClassLoader().getResource("com/golemgame/data/textures/progressBar/glint2.png"));
	//	glintTexture.setApply(Texture.AM_MODULATE);
	//	glintTexture.setBlendColor(ColorRGBA.white);
		//switch the colors of glint to work the way texture effect highlights do.
		
		windowArea = new Quad("progressBarBacking", width+ 32,height);
		progressBar = new Quad("progressBar",width, 32);
		

		//apply a texture to the window area to make it look cool
		TextureState backgroundState = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		barState  = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		barTexture.setWrap(Texture.WM_CLAMP_S_CLAMP_T);
		
		backgroundState.setTexture(background);
		
		barState.setTexture(barTexture,0);
	//	barState.setTexture(glintTexture, 1);
		
		windowArea.setRenderState(backgroundState);
		progressBar.setRenderState(barState);
		windowArea.updateRenderState();
		progressBar.updateRenderState();
		
		progressNode =  new Node();
		
		progressNode.setCullMode(SceneElement.CULL_NEVER);
		progressNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		progressNode.setLightCombineMode(LightState.OFF);
	
		ZBufferState buf = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.CF_LEQUAL);
        progressNode.setRenderState(buf);
        
	/*	DirectionalLight light = new DirectionalLight();
        light.setDiffuse( new ColorRGBA( 1f, 1f, 1f, 1f ) );
        light.setAmbient( new ColorRGBA( 1f, 1f, 1f, 1.0f ) );
        light.setDirection(new Vector3f(0,0,-1) );
        light.setEnabled( true );*/
		
		CullState cs = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
	    cs.setCullMode(CullState.CS_BACK);
	    progressNode.setRenderState(cs);
	/*       LightState lightState =  DisplaySystem.getDisplaySystem().getRenderer().createLightState();
	       lightState.setTwoSidedLighting(false);
	       lightState.attach(light);
	       progressNode.setRenderState(lightState);*/
	       
	       
	    	fadeState = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
	       progressNode.setRenderState(fadeState);
	       
			AlphaState semiTransparentAlphaState = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();

			// Blend between the source and destination functions.
			semiTransparentAlphaState.setBlendEnabled(true);
			// Set the source function setting.
			semiTransparentAlphaState.setSrcFunction(AlphaState.SB_SRC_ALPHA);
			// Set the destination function setting.
			semiTransparentAlphaState.setDstFunction(AlphaState.SB_ONE_MINUS_SRC_ALPHA);
			// Enable the test.
			semiTransparentAlphaState.setTestEnabled(true);		
			// Set the test function to TF_GREATER.
			semiTransparentAlphaState.setTestFunction(AlphaState.TF_GREATER);
			progressNode.setRenderState(semiTransparentAlphaState);
			
	   	progressNode.updateRenderState();
		
		progressNode.attachChild(windowArea);
		progressNode.attachChild(progressBar);
		
		progressBar.setZOrder(-8);
		windowArea.setZOrder(-7);
		
	//	progressBar.copyTextureCoords(0, 0, 1);
		
		progressBar.updateRenderState();
		windowArea.updateRenderState();
		
		progressNode.getLocalTranslation().set(DisplaySystem.getDisplaySystem().getWidth()/2f,DisplaySystem.getDisplaySystem().getHeight()/2f,0 );
		barTexture.setTranslation(new Vector3f());
		barTexture.setScale(new Vector3f(1f,1,1));
		//glintTexture.setTranslation(new Vector3f());
		//glintTexture.setScale(new Vector3f(glintWidth/width,1,1));
		
		progressNode.updateGeometricState(0, true);
		//progress bar effect: have a base texture, a 'glint texture', and 
		//some sort of alpha mask
		//the glint texture moves between the texture start and the alpha mask, repeatedly
	//	this.getRootNode().attachChild(progressNode);
		barTexture.getTranslation().x = 1-value ;
		isLoaded = true;
		
		GeneralSettings.getInstance().getResolution().addSettingsListener(new SettingsListener<Dimension>()
				{

					public void valueChanged(SettingChangedEvent<Dimension> e) {
						progressNode.getLocalTranslation().set(DisplaySystem.getDisplaySystem().getWidth()/2f,DisplaySystem.getDisplaySystem().getHeight()/2f,0 );

						progressNode.updateGeometricState(0, true);
						
					}
			
				},true);
		
	}
	
	
	/**
	 * Set the value of the progress bar.
	 * @param value between 0 and 1, inclusive.
	 */
    public synchronized void setProgress(float value) {
       
    	if (Math.abs(this.value - value)<FastMath.FLT_EPSILON)
    		return;
    	
    	if (value> 1f)
    		value = 1f;
    	else if (value < 0)
    		value = 0;
    		//restrict the value to fall between 0 and 1, inclusive.

    	this.value = value;
    	if (isLoaded &! isWorking())
    	{
    		barTexture.getTranslation().x = 1-value ;
    	}

   }
    
    
    public boolean isWorking() {
		return working;
	}



	public void setWorking(boolean working) {
		if (working &! this.working)
		{//if transitioning from not working to working, refresh the progress.
			

			barTexture.getScale().x = 5f;
			barTexture.getTranslation().x *= -5;
			this.working = working;
		}else if (!working && this.working)
		{

			barTexture.getScale().x = 1f;
			float progress = this.getProgress();
			this.value = -1;
			this.setProgress(progress);
			this.working = working;
			
		}
		this.working = working;
	}

	/**
	 * Set how long to delay before displaying the progress bar, in seconds.
	 * @param delayed
	 */
	public void setDelayedAppearance(float delayed)
	{
		this.delayedAppearance=delayed;
		this.remainingDelay = delayed;
		
	}
	public void setFade(boolean fade)
	{
		this.fade = fade;
		this.currentFade = 0;
	}


	@Override
	public void update(float tpf) {
		if (isWorking())//make this nicer in the future
		{
			barTexture.getScale().x = 5f;
			barTexture.getTranslation().x -= 0.1f ;
			if (barTexture.getTranslation().x <-6)
				barTexture.getTranslation().x = 1f;
		}else
			barTexture.getScale().x = 1f;
	}

	public float getProgress() {
		return value;
	}

	private Renderer r = DisplaySystem.getDisplaySystem().getRenderer();

	/**
	 * Draws the rootNode.
	 * 
	 * @see GameState#render(float)
	 */
	public void render(float tpf) {
		if (remainingDelay>0)
		{
			remainingDelay -= tpf;
			
		}else if (delayedAppearance<=0 || this.working || this.value < 1f)
		{
			r.draw(progressNode);
		}
	}
	
	/**
	 * Empty.
	 * 
	 * @see GameState#cleanup()
	 */
	public void cleanup() {	
        //do nothing
	}
	

	
}
