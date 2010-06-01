package com.golemgame.states.camera.skybox;

import java.util.concurrent.Callable;

import com.golemgame.states.StateManager;
import com.golemgame.states.camera.CameraManager;
import com.golemgame.states.camera.skybox.SkyBoxData.Face;
import com.jme.scene.SceneElement;
import com.jme.scene.Skybox;
import com.jme.scene.Spatial;
import com.jme.scene.state.RenderState;
import com.jme.system.DisplaySystem;

public class SkyBoxManager {

	private SkyBoxData skyboxdata = SkyBoxData.getNullBox();
	private SkyBoxData skyboxoverlay = SkyBoxData.getNullBox();
	private Skybox skybox;
	private CameraManager cameraManager;
	private boolean enabled = false;
	private boolean overlayEnabled = false;
	/**
	 * Set this to true for special handling for rendering to textures
	 */
	private boolean textureRenderMode = false;
	public boolean isTextureRenderMode() {
		return textureRenderMode;
	}

	public void setTextureRenderMode(boolean textureRenderMode) {
		this.textureRenderMode = textureRenderMode;
	}

	public SkyBoxManager(CameraManager cameraManager) {
		super();
			this.cameraManager = cameraManager;
			skybox = new Skybox("skybox", 10, 10, 10);
			skybox.setCullMode(SceneElement.CULL_NEVER);
			setSkyBox(skyboxdata);
	}
	
	public void setEnabled(boolean enabled)
	{

		StateManager.getGame().lock();
		try{
			if(enabled == this.enabled)
				return;
			this.enabled = enabled;
			if(enabled)
			{
				skybox.removeFromParent();
				cameraManager.getCameraLocationNode().attachChild(skybox);
				cameraManager.update();
			}else
				skybox.removeFromParent();
		}finally{
			StateManager.getGame().unlock();
		}
	}
	public void setOverlayEnabled(boolean enabled)
	{
		if(enabled == this.overlayEnabled)
			return;
		this.overlayEnabled = enabled;
		applySkybox();
	}
	public void setOverlay(SkyBoxData s)
	{
		if (s == null)
			this.skyboxoverlay = SkyBoxData.getNullBox();
		else
			this.skyboxoverlay = s;
		applySkybox();
	}
	
	public void setSkyBox(SkyBoxData s)
	{
		if (s == null)
			this.skyboxdata = SkyBoxData.getNullBox();
		else
			this.skyboxdata = s;
		applySkybox();
	}
	
	private void applySkybox() {
		skyboxdata.build(DisplaySystem.getDisplaySystem().getWidth(), DisplaySystem.getDisplaySystem().getHeight());
		skyboxoverlay.build(DisplaySystem.getDisplaySystem().getWidth(), DisplaySystem.getDisplaySystem().getHeight());
		skybox.clearRenderState(RenderState.RS_TEXTURE);
		try {
			StateManager.getGame().executeInGL(new Callable<Object>()
					{

						public Object call() throws Exception {
							for(Face face:SkyBoxData.Face.values())
							{
								skyboxdata.apply(face,skybox,0);
							
								
								if(overlayEnabled)
									skyboxoverlay.apply(face,skybox,1);
								else
									SkyBoxData.getNullBox().apply(face,skybox,1);
							}
					
							if(!textureRenderMode)
							{
								skybox.preloadTextures();
								
							}
							skybox.updateRenderState();
							return null;
						}
				
					});
		} catch (Exception e) {
			StateManager.logError(e);
		}

	}

	public SkyBoxData getSkyBox()
	{
		return skyboxdata;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isOverlayEnabled() {
		return overlayEnabled;
	}

	public Spatial getSkyBoxSpatial() {
		return skybox;
	}

}
