package com.golemgame.util;

import org.fenggui.Display;

import com.jme.bounding.BoundingVolume;
import com.jme.image.Texture;
import com.jme.intersection.CollisionResults;
import com.jme.intersection.PickResults;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;

/**
 * This is a hack to display fengGUI in the right render queue
 * @author Schabby
 *
 */
public class FengGUISpatial extends Quad
{
	private static final long serialVersionUID = 1; 
	private Display display;
	private TextureState defaultTextureState ;
//	private TextureState providedDefaultTextureState;
	public FengGUISpatial(Display display) {
		super();
		this.display = display;
		this.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		this.setCullMode(Spatial.CULL_NEVER);
		this.setLightCombineMode(LightState.OFF);
		
		
		Texture defTex = TextureState.getDefaultTexture().createSimpleClone();
		defTex.setScale(new Vector3f(1, 1, 1));
		defaultTextureState = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		defaultTextureState.setTexture(defTex);
		
	/*	for(RenderState defaultState:DisplaySystem.getDisplaySystem().getRenderer().defaultStateList)
		{
			if(defaultState.getType()== RenderState.RS_TEXTURE)
			{

				providedDefaultTextureState = (TextureState) defaultState;
				break;
			}
		}*/
	}

	@Override
	public void draw(Renderer r)
	{

		if(!r.isProcessingQueue())
		{
			r.checkAndAdd(this);
			return;
		}
		
		// set a default TextureState, this is needed to not let FengGUI inherit
		// wrong Texture coordinates and stuff.
		//DisplaySystem.getDisplaySystem().getRenderer().clearBuffers();
		//
		//for VERY mysterious reasons, applying both of these fixes fenggui's appearance...
		for (RenderState rs: Renderer.defaultStateList){
		    rs.apply();
		}
		//providedDefaultTextureState.apply();
		defaultTextureState.apply();
		// drawing FengGUI
		display.display();
	 
	}

	public void onDraw(Renderer r)
	{
		super.onDraw(r);
	}
	
	@Override
	public void findCollisions(Spatial scene, CollisionResults results)
	{
	}

	@Override
	public void findPick(Ray toTest, PickResults results)
	{
	}

	@Override
	public boolean hasCollision(Spatial scene, boolean checkTriangles)
	{
		return false;
	}

	@Override
	public void setModelBound(BoundingVolume modelBound)
	{
	}

	@Override
	public void updateModelBound()
	{
	}

	@Override
	public int getType()
	{
		return 0;
	}

	@Override
	public void updateWorldBound()
	{
	}

}