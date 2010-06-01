package com.golemgame.toolbar.tooltip;

import com.golemgame.constructor.UpdatableAdapter;
import com.golemgame.constructor.UpdateManager;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.simplemonkey.IWidget;
import com.simplemonkey.widgets.DelayedToolTipWidget;

public class FadingToolTip extends DelayedToolTipWidget {

	private IWidget fadeOwner = null;
	
	private FadeController fadeController ;
	private float fadeX;
	private float fadeY;
	private String fadeMessage;
	
	private boolean enabled = true;
	
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		
		if(!enabled)
			hideToolTip();
	}


	private enum FadeState
	{
		None(),In(),Out(),Show(),Hide();
	}
	
	private FadeState fadeState = FadeState.None;
	
	private MaterialState fadeMaterial;
	
	@Override
	public void forceHideToolTip(IWidget owner) {
		//this is the signal to begin showing the tooltip.
		
		//instead, we will fade it out over a given time period.
		
		//this fade out will handle the following conditions: a show command is issued for the same owner during this timer period, 
		//and... a show command is issued for a different owner during the same time period.
		fadeState = FadeState.Out;
		
	}

	@Override
	public void hideToolTip(IWidget owner) {
		if(enabled)
			super.hideToolTip(owner);
	}

	@Override
	public void showToolTip(IWidget owner, String toolTip, float x, float y) {
		if(enabled)
			super.showToolTip(owner, toolTip, x, y);
	}

	@Override
	public void forceShowToolTip(IWidget owner, String toolTip, float x, float y) {
		fadeController.hide();
		this.fadeState = FadeState.In;
		
		float buffer = 5;
		
		if (x + this.getWidth() >= DisplaySystem.getDisplaySystem().getWidth() -buffer)
		{
			x = DisplaySystem.getDisplaySystem().getWidth()-this.getWidth()-buffer;
		}
		
		if (x< buffer)
		{
			x = buffer;
		}
		
		if (y <buffer)
			y = buffer;
		
		if (y + this.getHeight() >= DisplaySystem.getDisplaySystem().getHeight() -buffer)
		{
			y = DisplaySystem.getDisplaySystem().getHeight()-this.getHeight()-buffer;
		}
		
		
		fadeX = x;
		fadeY = y;
		fadeOwner = owner;
		fadeMessage = toolTip;
		super.forceShowToolTip(fadeOwner,fadeMessage,fadeX,fadeY);
	}

	private void hideToolTip()
	{
		super.forceHideToolTip(fadeOwner);
	}
	

	
	public FadingToolTip() {
		this(750,0);
	
	}

	public FadingToolTip(long displayDelay, long hideDelay) {
		super(displayDelay, hideDelay);
		this.fadeController = new FadeController();
		UpdateManager.getInstance().add(fadeController);
		fadeMaterial = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		
		this.getSpatial().setRenderState(fadeMaterial);
		
		this.hideToolTip();
		
	}
	
	
	private class FadeController extends UpdatableAdapter
	{
		private static final long serialVersionUID = 1L;

		private final static float maxFade = 1f;
		private final static float minFade = 0f;
		
		private final static float increaseTime = 0.5f;
		private final static float decreaseTime = 0.25f;
		
		private float currentFadeState = minFade;
		
		public void hide()
		{
			currentFadeState = minFade;
		}
		
	

		@Override
		public void update(float time) {
			if (fadeOwner == null)
				return;
			
			switch(fadeState)
			{
				case In:
				{
					if (currentFadeState< minFade)
						currentFadeState = minFade;
					
					if (currentFadeState < maxFade)
					{
						currentFadeState += time/increaseTime;
					}
					
					if(currentFadeState>maxFade)
						currentFadeState = maxFade;
					
					setFade(currentFadeState);
				}
				break;
			
				case Out:
				{
					if(currentFadeState>maxFade)
						currentFadeState = maxFade;
					
					if (currentFadeState > minFade)
					{
						currentFadeState -= time/decreaseTime;
					}
					if (currentFadeState< minFade)
						currentFadeState = minFade;
				
					
					setFade(currentFadeState);
				}
				break;
				
				case Show:
				{
					currentFadeState = maxFade;										
					setFade(currentFadeState);
				}
				break;
				
				case Hide:
				{
					currentFadeState = minFade;										
					setFade(currentFadeState);
				}
				break;
				
				case None:
				default:	
			
			}
			
		}
		
		private ColorRGBA fadeColor= new ColorRGBA();
		
		private void setFade(float fade) {
			if (fade <= minFade)
			{
				hideToolTip();
				fadeOwner = null;
			}
			
			
			{
				fadeColor.set(1, 1, 1, fade);
				fadeMaterial.setAmbient(fadeColor);
				fadeMaterial.setDiffuse(fadeColor);
			}
			
		}
		
	}



}
