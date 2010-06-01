package com.golemgame.toolbar;


import com.golemgame.util.TransitionController;
import com.jme.image.Texture;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;

public class TransitionButton extends ButtonWidget {
	

	private ColorRGBA currentColor;

	private final TransitionController controller;
	private Node buttonNode;
	private Quad buttonQuad;

	private MaterialState colorState;
	
	public TransitionButton(int width, int height, ColorRGBA colorInactive,
			ColorRGBA colorActive, ColorRGBA colorHover, Texture texture , float timeHover, float timeActive, float timeInactive) {
		this(width, height, colorInactive, colorActive, colorHover, texture, null, timeHover,timeActive,timeInactive);
	}
	
	private static final int INACTIVE_STATE = 0;
	private static final int ACTIVE_STATE = 0;
	private static final int HOVER_STATE = 0;
	private static final int DEFAULT_STATE = 0;
	
	public TransitionButton(int width, int height, ColorRGBA colorInactive,
			ColorRGBA colorActive, ColorRGBA colorHover, Texture texture, Texture icon , float timeHover, float timeActive, float timeInactive) {
		super();
		super.setSize(width,height);	

		this.currentColor = new ColorRGBA();
		currentColor.set(colorInactive);
		
		buttonQuad = new Quad("button", 1, 1);
		buttonNode = new Node();
		buttonNode.attachChild(buttonQuad);
		buttonQuad.getLocalTranslation().set(1f/2f, 1f/2f,0);
		buttonQuad.updateGeometricState(0, true);
		
	       TextureState buttonTexture =  DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
	       buttonTexture.setTexture(texture,0);          
	       
	       if (icon != null)
	       {
	           icon.setApply(Texture.AM_DECAL);	       
		       buttonTexture.setTexture(icon, 1);
		       buttonQuad.copyTextureCoords(0, 0, 1); 
	       }	      
	       
	       buttonQuad.setRenderState(buttonTexture);
	       
	       colorState = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
	       colorState.setDiffuse(currentColor);
	       colorState.setAmbient(currentColor);
	       buttonQuad.setRenderState(colorState);
	       
	       buttonQuad.updateRenderState();
	       
	   
	       ColorRGBA[] colors = new ColorRGBA[4];
	       colors[INACTIVE_STATE] = colorInactive;
	       colors[ACTIVE_STATE] = colorActive;
	       colors[HOVER_STATE] = colorHover;
	       colors[DEFAULT_STATE] = ColorRGBA.black;
	       float[] times = new float[4];
	       times[INACTIVE_STATE] = timeInactive;
	       times[ACTIVE_STATE] = timeActive;
	       times[HOVER_STATE] = timeHover;
	       times[DEFAULT_STATE] = 0;
	       controller  = new TransitionController(colors, times, colorState);
	       
	       
	       addButtonListener(new ButtonListener()
	       {

			
			public void buttonStateChange(ButtonState state) {
				switch(state)
				{
					case INACTIVE:
						controller.transition(INACTIVE_STATE);
					break;			
					case HOVER:		
						controller.transition(HOVER_STATE);			
					break;	
					case ANTI_HOVER:			
						controller.transition(INACTIVE_STATE);
					break;
					case ACTIVE:
						controller.transition(ACTIVE_STATE);
					break;
  
				}
			}
	   	
	       });
	}
	


	
	public Spatial getSpatial() {
		return buttonNode;
	}
	
	
}
