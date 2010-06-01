package com.golemgame.toolbar.sound;

import com.golemgame.states.GeneralSettings;
import com.golemgame.states.StateManager;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.simplemonkey.IMouseListener;
import com.simplemonkey.widgets.TextureWidget;

public class VolumeMeter extends TextureWidget{
	private Texture bottomLayer;
	private Texture topLayer;
	private Vector3f topTranslation = new Vector3f();
	private MaterialState color;
	private boolean isMute = false;
	private ColorRGBA volCol = new ColorRGBA(24f/255f,82f/255f,150f/255f,1f);
	public VolumeMeter() {
		super("Volume Meter");
		bottomLayer = TextureManager.loadTexture(StateManager.loadResource("com/golemgame/data/textures/menu/volume.png"));
		bottomLayer.setApply(Texture.AM_MODULATE);
		topLayer = TextureManager.loadTexture(StateManager.loadResource("com/golemgame/data/textures/menu/volume1.png"));

		//topLayer = new Texture();
/*		Image solidImage = new Image();
		solidImage.setWidth(1);
		solidImage.setHeight(1);
		topLayer.setImage(solidImage);*/
		
		//topLayer.setApply(Texture.AM_BLEND);
		topLayer.setBlendColor(volCol);
		
		topLayer.setApply(Texture.AM_BLEND);
		
		/*topLayer.setApply(Texture.AM_COMBINE);
	
		topLayer.setCombineFuncAlpha(Texture.ACF_REPLACE);
		topLayer.setCombineFuncRGB(Texture.ACF_REPLACE);
		
		topLayer.setCombineSrc0RGB(Texture.ACS_CONSTANT);
		//topLayer.setCombineOp1RGB(Texture.ACS_PREVIOUS);
		topLayer.setCombineSrc0Alpha(Texture.ACS_PREVIOUS);//use previous alpha
*/	
		topTranslation.y = 0f;
		topLayer.setTranslation(topTranslation);
		topLayer.setWrap(Texture.WM_BCLAMP_S_BCLAMP_T);
	/*	Vector3f scale = new Vector3f();
		scale.y = 0.5f;
		scale.x = 1f;
		topLayer.setScale(scale);*/
		//topLayer.setcom
		
		super.setTexture(bottomLayer, 0);
		super.setTexture(topLayer, 1);
		color = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		//color.setAmbient(new ColorRGBA(24f/255f,82f/255f,150f/255f,150f/255f));
		//color.setAmbient(new ColorRGBA(0,255,0,150f/255f));
		color.setDiffuse(new ColorRGBA(0,0,0,150f/255f));
		color.setColorMaterial(MaterialState.CM_NONE );
		color.setMaterialFace(MaterialState.MF_FRONT);
		
		AlphaState semiTransparentAlpha = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();

		semiTransparentAlpha.setBlendEnabled(true);
		semiTransparentAlpha.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		semiTransparentAlpha.setDstFunction(AlphaState.SB_ONE_MINUS_SRC_ALPHA);
		semiTransparentAlpha.setTestEnabled(true);		
		semiTransparentAlpha.setTestFunction(AlphaState.TF_GREATER);	
	
		super.getSpatial().setRenderState(semiTransparentAlpha);
		super.getSpatial().setRenderState(color);
		super.getSpatial().updateRenderState();
		
		super.addMouseListener(new IMouseListener()
		{
			private boolean pressed =false;
			public void mouseMove(float x, float y) {
				if(pressed&&! isMute)
				{
					float vol = y/getHeight();
					if(vol<0.05f)//snap
						vol = 0f;
					if (vol>0.95f)
						vol = 1f;
					GeneralSettings.getInstance().getVolume().setValue(vol);
				//	setVolume(vol);
				}
			}

			public void mouseOff() {
			//	pressed = false;
			}

			public void mousePress(boolean pressed, int button, float x, float y) {
				if(button == 0)
					this.pressed = pressed;
				else
					this.pressed = false;
				
				if(pressed&&! isMute)
				{
					float vol = y/getHeight();
					if(vol<0.05f)//snap
						vol = 0f;
					if (vol>0.95f)
						vol = 1f;
					GeneralSettings.getInstance().getVolume().setValue(vol);
					//setVolume(vol);
				}
			}
			
		});
	}

	@Override
	public void layout() {
		super.layout();

		topLayer.setTranslation(topTranslation);
		super.getSpatial().updateRenderState();
	}

	public void setVolume(float volume)
	{
	
		topTranslation.y = (1f - volume);
		topLayer.setTranslation(topTranslation);
		
	}

	public void setMute(Boolean mute) {
		this.isMute = mute;
		if(mute)
		{
			topLayer.setBlendColor(ColorRGBA.gray);
		}else{
			topLayer.setBlendColor(volCol);
		}
	}
	
	/*//private Quad volumeControl;
	
	private Texture bottomLayer;
	private Texture topLayer;
	private TextureState textureState;
	private MaterialState white;
	private VolumeMeter() {
		super();
		volumeControl = new Quad("Volume",1,1);
		volumeControl.copyTextureCoords(0, 0, 1);
		
		bottomLayer = TextureManager.loadTexture(StateManager.loadResource("com/golemgame/data/textures/menu/volume.png"));
		topLayer = TextureManager.loadTexture(StateManager.loadResource("com/golemgame/data/textures/menu/volume.png"));
		
		textureState = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		
		
		white = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		
		white.setColorMaterial(MaterialState.CM_NONE );
		white.setMaterialFace(MaterialState.MF_FRONT);
		
		AlphaState semiTransparentAlpha = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();

		semiTransparentAlpha.setBlendEnabled(true);
		semiTransparentAlpha.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		semiTransparentAlpha.setDstFunction(AlphaState.SB_ONE_MINUS_SRC_ALPHA);
		semiTransparentAlpha.setTestEnabled(true);		
		semiTransparentAlpha.setTestFunction(AlphaState.TF_GREATER);	
		
		
		volumeControl.setRenderState(white);
		volumeControl.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		volumeControl.setRenderState(semiTransparentAlpha);
		
		volumeControl.setRenderState(textureState);
		textureState.setTexture(bottomLayer, 0);
		textureState.setTexture(topLayer, 1);
		
		volumeControl.updateRenderState();
		

		
	}



	public void setVolume(float volume)
	{
		
	}*/
	
}
