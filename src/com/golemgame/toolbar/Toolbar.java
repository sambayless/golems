package com.golemgame.toolbar;

import com.golemgame.util.input.LayeredKeyInputListener;
import com.golemgame.util.input.LayeredMouseInputListener;
import com.jme.renderer.Renderer;
import com.jme.scene.SceneElement;
import com.jme.scene.state.AlphaState;
import com.jme.system.DisplaySystem;
import com.simplemonkey.Container;
import com.simplemonkey.IWidget;


public abstract class Toolbar extends Container implements LayeredMouseInputListener,LayeredKeyInputListener
{

	protected boolean engaged = false;
	private IWidget activeWidget = null;
		
	
	public Toolbar() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Toolbar(String name) {
		super(name);
	getSpatial().setCullMode(SceneElement.CULL_NEVER);
		Renderer renderer = DisplaySystem.getDisplaySystem().getRenderer();
		/*DirectionalLight light = new DirectionalLight();
        light.setDiffuse( new ColorRGBA( 1f, 1f, 1f, 1f ) );
        light.setAmbient( new ColorRGBA(1f, 1f, 1f, 1.0f ) );
        light.setDirection(new Vector3f(0,0,-1) );
        light.setEnabled( true );
        
        LightState lightState = renderer.createLightState();
        lightState.setEnabled( true );
        lightState.attach( light );
        
        getSpatial().setRenderState(lightState);
		*/
		   AlphaState as = renderer.createAlphaState();
	       as.setBlendEnabled(true);
	 
	       as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
	       as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
	       as.setTestEnabled(false);
	       as.setEnabled(true);
	       getSpatial().setRenderState(as);
		      getSpatial().updateRenderState();
	   	/*       
	       ColorRGBA buttonColor=new ColorRGBA(1f, 1f, 1f, 0f);  
	     MaterialState   buttonMaterial = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
	       buttonMaterial.setAmbient(buttonColor);
	       buttonMaterial.setDiffuse(buttonColor);
	       buttonMaterial.setSpecular(buttonColor);
	       buttonMaterial.setMaterialFace(MaterialState.MF_FRONT);
	       buttonMaterial.setShininess(0);
	       buttonMaterial.setColorMaterial(MaterialState.CM_NONE );	
	   	
	       getSpatial().setRenderState(buttonMaterial); 
	      
	      getSpatial().setRenderQueueMode(Renderer.QUEUE_ORTHO);

	     */
	}

	public boolean onButton(int button, boolean pressed, int x, int y) {
		boolean caught = false;
		if (engaged )
		{
			
				IWidget collision = getInternalWidget(x-this.getWorldX(),y-this.getWorldY());
				if (collision != null)
					caught = collision.mousePress(pressed,button,x-collision.getWorldX(), y-collision.getWorldY());
				if (collision != activeWidget)
				{
					if (activeWidget != null)
					{
						activeWidget.mousePress(false, button, x-activeWidget.getWorldX(), y-activeWidget.getWorldY());
						activeWidget.mouseOff();
					}
					activeWidget = collision;
				}
			
				if (collision != null)
					return caught;
			
			
		}else
		{
			if (activeWidget != null)
			{
				activeWidget.mousePress(false, button, x- activeWidget.getWorldX(), y-activeWidget.getWorldY());
				activeWidget.mouseOff();
				activeWidget = null;
			}
		}
		
		return caught;
		
	}

	
	public boolean onMove(int deltaX, int deltaY, int newX, int newY) {
	//	Vector3f store = new Vector3f();
		boolean caught = false;
		
	
		
		if(newX >=super.getWorldX() && newX - super.getWorldX() <= super.getWidth() && newY >=super.getWorldY() && newY - super.getWorldY() <= super.getHeight())
		{
			if (!engaged)
			{
				engaged = true;
				engage(true);
			}
		}else
		{
/*			if (engaged)
			{
				engaged = false;
				engage(false);
				if (activeWidget != null)
					activeWidget.mouseOff();
			}*/
		}
		
		if (engaged)
		{
			
			IWidget collision = getInternalWidget(newX-getWorldX(),newY-getWorldY());
		//	if (collision != activeButton)
			//{
				if (collision!= null)
					caught = collision.mouseMove(newX - collision.getWorldX(), newY - collision.getWorldY());
				if (activeWidget != null && activeWidget != collision)
					activeWidget.mouseOff(); 
				activeWidget = collision;
				if(!caught)
					mouseOff();
				if(collision!= null)
					return caught;
			//}
			
		}else if(activeWidget != null)
		{
			activeWidget.mouseOff();
		}
		
		return caught;
	}

	
	@Override
	public void mouseOff() {
		super.mouseOff();
		
	}

	public boolean onWheel(int wheelDelta, int x, int y) {

		return false;
	}
	
	/**
	 * Called when mouse hover begins, and ends
	 * @param engage
	 */
	protected abstract void engage(boolean engage);

	
	public boolean onKey(char character, int keyCode, boolean pressed) {
		return false;
		
	}
    

   
	
}
