package com.golemgame.toolbar.physics;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.golemgame.local.StringConstants;
import com.golemgame.toolbar.ButtonAdapter;
import com.golemgame.toolbar.ButtonGroup;
import com.golemgame.toolbar.StandardButton;
import com.golemgame.toolbar.tooltip.IconToolTip;
import com.golemgame.toolbar.tooltip.ToolTipData;
import com.jme.image.Texture;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.simplemonkey.Container;
import com.simplemonkey.layout.BorderLayout;
import com.simplemonkey.layout.BorderLayoutData;
import com.simplemonkey.layout.RowLayout;

/**
 * The plan is this:
 * The top right button in physics remains, but with a 'pause' icon.
 * When you click it, the toolbar expands into three buttons: play, stop, and (later) stop preserving.
 * @author Sam
 *
 */
public class PhysicsButtons extends Container {

	private Runnable pauseAction;
	private Runnable stopAction;
	private Runnable runAction;
	
	private ButtonGroup mainMenuGroup ;
	private StandardButton pauseButton; 
	private StandardButton runButton; 
	private StandardButton stopButton;

	
	private Lock stateLock = new ReentrantLock();
	
	private boolean isPaused = false;
	
	public Runnable getPauseAction() {
		return pauseAction;
	}



	public void setPauseAction(Runnable pauseAction) {
		this.pauseAction = pauseAction;
	}



	public Runnable getStopAction() {
		return stopAction;
	}



	public void setStopAction(Runnable stopAction) {
		this.stopAction = stopAction;
	}






	public Runnable getRunAction() {
		return runAction;
	}



	public void setRunAction(Runnable runAction) {
		this.runAction = runAction;
	}



	public PhysicsButtons(IconToolTip toolTip) {
		super();
	

		
		
	  	Texture runInactive = TextureManager.loadTexture(
	  			getClass().getClassLoader().getResource(
	            "com/golemgame/data/textures/buttons/solid/Solid_Green.png"),
	            Texture.MM_LINEAR_NEAREST,
	            Texture.FM_LINEAR);

	  	Texture stopInactive = TextureManager.loadTexture(
	  			getClass().getClassLoader().getResource(
	            "com/golemgame/data/textures/buttons/solid/Solid_Red.png"),
	            Texture.MM_LINEAR_NEAREST,
	            Texture.FM_LINEAR);
	  	
	  	Texture pauseInactive = TextureManager.loadTexture(
	  			getClass().getClassLoader().getResource(
	            "com/golemgame/data/textures/buttons/solid/Solid_Yellow.png"),
	            Texture.MM_LINEAR_NEAREST,
	            Texture.FM_LINEAR);

	  	
	  	Texture runActive = TextureManager.loadTexture(
	  			getClass().getClassLoader().getResource(
	            "com/golemgame/data/textures/buttons/glass/Crystal.png"),
	            Texture.MM_LINEAR_NEAREST,
	            Texture.FM_LINEAR);
	  	
	  	Texture runHover = TextureManager.loadTexture(
	            getClass().getClassLoader().getResource(
	            "com/golemgame/data/textures/buttons/glass/Glass_Green.png"),
	            Texture.MM_LINEAR_NEAREST,
	            Texture.FM_LINEAR);
	  	
	  	Texture stopHover = TextureManager.loadTexture(
	            getClass().getClassLoader().getResource(
	            "com/golemgame/data/textures/buttons/glass/Glass_Red.png"),
	            Texture.MM_LINEAR_NEAREST,
	            Texture.FM_LINEAR);
	  	
	  	Texture pauseHover = TextureManager.loadTexture(
	  			getClass().getClassLoader().getResource(
	            "com/golemgame/data/textures/buttons/glass/Glass_Yellow.png"),
	            Texture.MM_LINEAR_NEAREST,
	            Texture.FM_LINEAR);
	  	
	  	Texture stopIcon = TextureManager.loadTexture(
	            getClass().getClassLoader().getResource(
	            "com/golemgame/data/textures/buttons/tools/Stop.png"),
	            Texture.MM_LINEAR_NEAREST,
	            Texture.FM_LINEAR);
	  	
	  	Texture pauseIcon = TextureManager.loadTexture(
	            getClass().getClassLoader().getResource(
	            "com/golemgame/data/textures/buttons/tools/Pause.png"),
	            Texture.MM_LINEAR_NEAREST,
	            Texture.FM_LINEAR);
	  	
	  	Texture runIcon = TextureManager.loadTexture(
	            getClass().getClassLoader().getResource(
	            "com/golemgame/data/textures/buttons/tools/Play.png"),
	            Texture.MM_LINEAR_NEAREST,
	            Texture.FM_LINEAR);
	  	
	  	
	  	 mainMenuGroup = new ButtonGroup();
		 pauseButton = new StandardButton(32, 32,  runActive, pauseInactive, pauseHover,pauseIcon);
		runButton = new StandardButton(32, 32, runActive, runInactive, runHover,runIcon);
		 stopButton = new StandardButton(32, 32, runActive, stopInactive, stopHover,stopIcon);

			runButton.setTooltipWidget(toolTip);
			IconToolTip.setTooltipData(runButton,new ToolTipData(runIcon,runHover,""));		
			IconToolTip.setDescription(runButton,StringConstants.get("STATUSBAR.RESUME","Resume physics"));
			
			
			pauseButton.setTooltipWidget(toolTip);
			IconToolTip.setTooltipData(pauseButton,new ToolTipData(pauseIcon,pauseHover,""));	
			IconToolTip.setDescription(pauseButton,StringConstants.get("STATUSBAR.PAUSE","Pause physics"));
			
			stopButton.setTooltipWidget(toolTip);
			IconToolTip.setTooltipData(stopButton,new ToolTipData(stopIcon,stopHover,""));	
			IconToolTip.setDescription(stopButton,StringConstants.get("STATUSBAR.STOP","Stop Physics\n(Return to Design Mode)"));
		
		{
			MaterialState white;
			white = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
			white.setSpecular(new ColorRGBA(1f,1f,0.2f,1));
			white.setAmbient(new ColorRGBA(1f,1f,1f,1f));
			white.setDiffuse(new ColorRGBA(1f,1f,1,1f));		
			white.setShininess(100);
			white.setColorMaterial(MaterialState.CM_NONE );
			white.setMaterialFace(MaterialState.MF_FRONT_AND_BACK);
			
			AlphaState semiTransparentAlpha = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
	
			// Blend between the source and destination functions.
			semiTransparentAlpha.setBlendEnabled(true);
			// Set the source function setting.
			semiTransparentAlpha.setSrcFunction(AlphaState.SB_SRC_ALPHA);
			// Set the destination function setting.
			semiTransparentAlpha.setDstFunction(AlphaState.SB_ONE_MINUS_SRC_ALPHA);
			// Enable the test.
			semiTransparentAlpha.setTestEnabled(true);		
			// Set the test function to TF_GREATER.
			semiTransparentAlpha.setTestFunction(AlphaState.TF_GREATER);		
			runButton.getSpatial().setRenderState(white);
			runButton.getSpatial().setRenderState(semiTransparentAlpha);
		
			runButton.getSpatial().updateRenderState();
			
			
			pauseButton.getSpatial().setRenderState(white);
			pauseButton.getSpatial().setRenderState(semiTransparentAlpha);
		
			pauseButton.getSpatial().updateRenderState();
			
			stopButton.getSpatial().setRenderState(white);
			stopButton.getSpatial().setRenderState(semiTransparentAlpha);
		
			stopButton.getSpatial().updateRenderState();
		}

		runButton.getSpatial().updateWorldVectors();
	//	mainMenuGroup.addWidget(runButton);
		mainMenuGroup.addWidget(pauseButton);
		
		this.addWidget(mainMenuGroup);
		mainMenuGroup.setLayoutData(BorderLayoutData.EAST);
		this.setLayoutData(BorderLayoutData.NORTH);
		this.setLayoutManager(new BorderLayout());
		mainMenuGroup.setLayoutManager(new RowLayout());

		this.layout();
		
		
		pauseButton.addButtonListener(new ButtonAdapter()
		{

			@Override
			public void activate() {
				pausePressed();
			}
			
		});
		
		runButton.addButtonListener(new ButtonAdapter()
		{

			@Override
			public void activate() {
				runPressed();
			}
			
		});
		
		stopButton.addButtonListener(new ButtonAdapter()
		{

			@Override
			public void activate() {
				stopPressed();
			}
			
		});
/*		
		StateManager.getResolution().addSettingsListener(new SettingsListener<Dimension>()
				{
					@Override
					public void valueChanged(SettingChangedEvent<Dimension> e) {
							setSize(e.getNewValue().width, e.getNewValue().height);
							setHeight(e.getNewValue().height);
							runButton.getSpatial().getLocalTranslation().set(e.getNewValue().width-runButton.getWidth()/2f,e.getNewValue().height - runButton.getHeight()/2f,0);
		    				runButton.getSpatial().updateWorldVectors();
		    				
		    				PhysicsToolbar.this.getSpatial().updateGeometricState(0, true);
							layout();
					}
				},true);*/

	}



	protected void stopPressed() {
		stateLock.lock();
		try{
		if (isPaused){
			if(stopAction!=null)
				stopAction.run();
			
			isPaused = false;
			
			this.mainMenuGroup.removeAllWidgets();
			this.mainMenuGroup.addWidget(pauseButton);
			this.mainMenuGroup.layout();
			if(getParent()!=null)
			{
				getParent().getParent().getParent().layout();
			}
	
		}
		}finally
		{
			stateLock.unlock();
		}	
	}



	protected void runPressed() {
		stateLock.lock();			
		try{
		if (isPaused){
			if(runAction!=null)
				runAction.run();
			
			isPaused = false;
			
			this.mainMenuGroup.removeAllWidgets();
			this.mainMenuGroup.addWidget(pauseButton);
			this.mainMenuGroup.layout();
			if(getParent()!=null)
			{
				getParent().getParent().getParent().layout();
			}
			}
		}finally
		{
			stateLock.unlock();
		}	
	}



	protected void pausePressed() {
		stateLock.lock();
		try{
		if(!isPaused){
			if(pauseAction!=null)
				pauseAction.run();
			
			isPaused = true;
			
			this.mainMenuGroup.removeAllWidgets();
			this.mainMenuGroup.addWidget(runButton);
			this.mainMenuGroup.addWidget(stopButton);
			this.mainMenuGroup.layout();
			if(getParent()!=null)
			{
				getParent().getParent().getParent().layout();
			}
		}
		}finally
		{
			stateLock.unlock();
		}		
	}




}
