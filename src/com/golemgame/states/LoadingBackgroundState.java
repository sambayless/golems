package com.golemgame.states;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.golemgame.mvc.golems.Golems;
import com.golemgame.util.pass.PassingGameStateManager;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.font2d.Font2D;
import com.jmex.font2d.Text2D;
import com.jmex.game.state.GameState;

public class LoadingBackgroundState extends GameState {

	private Quad titleQuad;
	private Quad bgQuad;
	//private Quad infoBar;
	private Text2D textBar;
	private Node generalNode;
	@Override
	public void cleanup() {
		
	}

	public LoadingBackgroundState() {
		super();
		
	//	infoBar =new Quad("Info bar",1,1);
	}

	@Override
	public void render(float tpf) {
		DisplaySystem.getDisplaySystem().getRenderer().draw(generalNode);
	//	DisplaySystem.getDisplaySystem().getRenderer().draw(textBar);
	}

	float curTime = 0f;
	float maxTime = 1f;
	
	private boolean finish = false;
	
	public boolean isFinish() {
		return finish;
	}

	public void setFinish(boolean finish) {
		if(finish)
		{
			finishAlpha = bgColor.a;
			curTime= 0;
		}
		this.finish = finish;
	}

	private float finishAlpha = 1f;
	private float flickerTime = -1f;
	private static final float finishTime = 0.25f;
	@Override
	public void update(float tpf) {
		if(!finish){
			if(curTime<1f)
			{
				bgColor.a = curTime/maxTime;
				ms.setAmbient(bgColor);
				ms.setDiffuse(bgColor);
			}else{
				if(flickerTime == -1f)
					flickerTime = curTime;
				
				bgColor.a = 1f -  FastMath.sin(curTime-flickerTime)*.25f;
				ms.setAmbient(bgColor);
				ms.setDiffuse(bgColor);
			}
		}else{
			if(curTime<finishTime)
			{
				bgColor.a =(1f-( curTime/finishTime))*finishAlpha;
				//System.out.println(curTime/finishTime + "\t" + (1f-( curTime/finishTime)));
				ms.setAmbient(bgColor);
				ms.setDiffuse(bgColor);
				
			}else{
				PassingGameStateManager.getInstance().detachChild(this);
			}
		}
		curTime+= tpf;
	}
	
	
	
	private ColorRGBA bgColor;
	private 	MaterialState ms;
	public void load()
	{
		titleQuad = new Quad("Title",1,1);
		bgQuad = new Quad("Background",1,1);
		
		generalNode = new Node();
		Texture title = new Texture();
		Texture bg = new Texture();
		bgQuad.setCullMode(SceneElement.CULL_NEVER);
		titleQuad.setCullMode(SceneElement.CULL_NEVER);
		generalNode.attachChild(bgQuad);
		bgQuad.getLocalTranslation().z = 100f;
		generalNode.attachChild(titleQuad);
		final URL textureURL = StateManager.loadResource("com/golemgame/data/textures/misc/title.png");
		
		try {
			BufferedImage titleImage;
			titleImage = ImageIO.read(textureURL.openStream());
			BufferedImage backImage;
			try{
				final URL backgroundURL = StateManager.loadResource("com/golemgame/data/textures/misc/titleBackground.png");
				
				backImage = ImageIO.read(backgroundURL.openStream());
				
			}catch(Exception e)
			{
				//any exception just lose the background image
				//StateManager.logError(e); Dont log; this is expected behaviour
				backImage = new BufferedImage(1,1,BufferedImage.TYPE_4BYTE_ABGR);
			}
			
			final Image titleTextureImage =  TextureManager.loadImage(titleImage, true);
			final Image backTextureImage =  TextureManager.loadImage(backImage, true);
	 			
				titleTextureImage.setWidth(titleImage.getWidth());
				titleTextureImage.setHeight(titleImage.getHeight());
		
				backTextureImage.setWidth(backImage.getWidth());
				backTextureImage.setHeight(backImage.getHeight());
					
				
					//if(textureClass.useMipmaps())
					//	
						//if(!textureClass.useMipmaps())
						//	texture.setScale(new Vector3f(1,-1,1));
								
				title.setAnisoLevel(0);
				title.setMipmapState( Texture.MM_NONE);
				title.setFilter(Texture.FM_NEAREST);
				title.setImage(titleTextureImage);
				
				bg.setAnisoLevel(0);
				bg.setMipmapState( Texture.MM_NONE);
				bg.setFilter(Texture.FM_NEAREST);
				bg.setImage(backTextureImage);
				
				LightState ls =  DisplaySystem.getDisplaySystem().getRenderer().createLightState();
				ls.setEnabled(true);
				ls.setGlobalAmbient(ColorRGBA.white);
				
				bgColor = new ColorRGBA(1f,1f,1f,0f);
				
				ms  =  DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				ms.setAmbient(bgColor);
				ms.setDiffuse(bgColor);
				ms.setColorMaterial(MaterialState.CM_NONE);
				
				AlphaState as = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();	
				as.setBlendEnabled(true);				
				as.setSrcFunction(AlphaState.SB_SRC_ALPHA);				
				as.setDstFunction(AlphaState.SB_ONE_MINUS_SRC_ALPHA);	
				as.setTestEnabled(true);				
				as.setTestFunction(AlphaState.TF_GREATER);	
				
				TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
				ts.setTexture(title,0);
				
				TextureState backTs = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
				backTs.setTexture(bg,0);
				
				CullState cs = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
				cs.setCullMode(CullState.CS_BACK);
				
				generalNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
				generalNode.setRenderState(cs);
				generalNode.setRenderState(ls);
				generalNode.setRenderState(as);
				
				titleQuad.setRenderState(ts);				
				titleQuad.setRenderState(ms);
				titleQuad.updateRenderState();
				
				bgQuad.setRenderState(backTs);				
				bgQuad.setRenderState(ms);
				bgQuad.updateRenderState();
				
				float maxHeight = Math.min(DisplaySystem.getDisplaySystem().getHeight()/2f, backTextureImage.getHeight());
				float maxWidth = Math.min(DisplaySystem.getDisplaySystem().getWidth()*0.8f,backTextureImage.getWidth());
				
				float aspectRatio = backTextureImage.getWidth()/backTextureImage.getHeight();
				
				if(maxHeight*aspectRatio>maxWidth)
				{
					maxHeight = maxWidth/aspectRatio;
				}else
				{
					maxWidth = maxHeight*aspectRatio;
				}
				bgQuad.getLocalScale().set(maxWidth,maxHeight,1);
				
		} catch (IOException e) {
			StateManager.logError(e);
		}	
		

		
		titleQuad.getLocalScale().set(512,128,1);
		float titleHeight = DisplaySystem.getDisplaySystem().getHeight()/2f + ProgressBarState.height/2f +titleQuad.getLocalScale().y/2f ;
	
		float bgHeight = DisplaySystem.getDisplaySystem().getHeight()/2f - bgQuad.getLocalScale().y/2f -  ProgressBarState.height/2f;
		
		
		titleQuad.getLocalTranslation().set(DisplaySystem.getDisplaySystem().getWidth()/2f,titleHeight,0);
		titleQuad.updateGeometricState(0, true);
		
		bgQuad.getLocalTranslation().set(DisplaySystem.getDisplaySystem().getWidth()/2f,bgHeight,0);		
		bgQuad.updateGeometricState(0, true);
		
		String data = "Golems Universal Constructor " + Golems.version + " ";
		
		Font2D font = new Font2D();
		
		textBar = font.createText(data, 12, 0);
		generalNode.attachChild(textBar);
		float textHeight = DisplaySystem.getDisplaySystem().getHeight()/2f - ProgressBarState.height/2f - titleQuad.getLocalScale().y + textBar.getHeight()/2f ;
		
		textBar.getLocalTranslation().set(DisplaySystem.getDisplaySystem().getWidth() - textBar.getWidth(),0 ,1);
		textBar.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		textBar.updateRenderState();
		
		generalNode.updateRenderState();
		generalNode.updateGeometricState(0, true);
		
	}

}
