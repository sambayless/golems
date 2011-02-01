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
package com.golemgame.tool;

import com.golemgame.model.ParentModel;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.SpatialModelImpl;
import com.jme.math.FastMath;
import com.jme.math.Vector2f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;

public class BoxSelectionEffect implements ToolSelectionEffect {

	private boolean engaged = false;//new AtomicBoolean(false);

	private NodeModel model ;
	
	public BoxSelectionEffect() {
		super();
		model = new NodeModel();
		model.setUpdateLocked(false);
		model.getSpatial().setRenderQueueMode(Renderer.QUEUE_ORTHO);
		model.getSpatial().setCullMode(SceneElement.CULL_NEVER);
		model.getSpatial().setLightCombineMode(LightState.REPLACE);
		model.addChild( new SpatialModelImpl(false)
		{
			private static final long serialVersionUID = 1L;
			@Override
			protected Spatial buildSpatial() {
			//	super.setLoadable(false);
			
				Quad box = new Quad("",1,1);	
				//Box box = new Box("", new Vector3f(), 10,10,10);
				box.setCullMode(SceneElement.CULL_NEVER);
				CullState cs = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
				cs.setCullMode(CullState.CS_BACK);
				box.setRenderState(cs);
				
				AlphaState alpha = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
				alpha.setBlendEnabled(true);
				alpha.setSrcFunction(AlphaState.SB_SRC_ALPHA);
				alpha.setDstFunction(AlphaState.SB_ONE_MINUS_SRC_ALPHA);
				alpha.setTestEnabled(true);		
				alpha.setTestFunction(AlphaState.TF_GREATER);
				box.setRenderState(alpha);
				//box.setRenderQueueMode(Renderer.QUEUE_ORTHO);
				

				
				MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				ms.setAmbient(new ColorRGBA(0.6f,1,0.6f,0.5f));
				ms.setDiffuse(new ColorRGBA(0.6f,1,0.6f,0.5f));
				ms.setMaterialFace(MaterialState.MF_FRONT);
				box.setRenderState(ms);
				
				LightState ls = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
				ls.setGlobalAmbient(new ColorRGBA(0.5f,0.5f,0.5f,1f));
	/*			DirectionalLight light = new DirectionalLight();
				light.setDirection(new Vector3f(0,0,1));
				light.setDiffuse(new ColorRGBA(0.5f,0.5f,0.5f,1f));
				light.setAmbient(new ColorRGBA(0.5f,0.5f,0.5f,1f));
				ls.attach(light);
				*/
				box.setRenderState(ls);
				
				
			//	box.setLightCombineMode(LightState.OFF);
				
		
				box.updateRenderState();
				
				
				return box;
			}
		});
		this.getModel().setVisible(false);
	}

	public void forceDisengage() {
		this.model.setVisible(false);
		this.model.detachFromParent();

	}

	public ParentModel getModel() {
		return model;
	}

	/**
	 * Engage this effect, if it is not already engaged.
	 */
	public void setEngage(boolean engaged)
	{
		this.engaged = engaged;
		//if(this.engaged.compareAndSet(!engaged, engaged))
		if(engaged){
			this.getModel().setVisible(true);
			doEngage(engaged);
		}else
		{
			this.getModel().setVisible(false);
		//	this.getModel().detachFromParent();
		}
		getModel().refreshLockedData();
	}
	
	protected void doEngage(boolean engage)
	{
	//	this.getModel().setLocalRotation(targetRotation);
		setBounds(new Vector2f(0,0), new Vector2f(0,0));
		ToolSelectionEffectManager.getInstance().setCurrentEffect(this);
		setBounds(new Vector2f(0,0), new Vector2f(0,0));

	}
	
	public void setBounds(Vector2f start, Vector2f end)
	{
		
		Vector2f topLeft = new Vector2f( (start.x < end.x)? start.x:end.x,(start.y > end.y)? start.y:end.y);
		Vector2f bottomRight = new Vector2f( (start.x > end.x)? start.x:end.x,(start.y < end.y)? start.y:end.y);
		if ((bottomRight.x - topLeft.x) <= FastMath.FLT_EPSILON ||(topLeft.y - bottomRight.y) <= FastMath.FLT_EPSILON )
		{
			model.setVisible(false);
			
		}else
			model.setVisible(engaged && true);
		model.getLocalScale().set(400,100,1);
		model.getLocalScale().set(FastMath.abs( topLeft.x - bottomRight.x),FastMath.abs( topLeft.y - bottomRight.y),1f);
		model.getLocalTranslation().setX(topLeft.x + model.getLocalScale().x/2f);
		model.getLocalTranslation().setY(topLeft.y- model.getLocalScale().y/2f);
		model.refreshLockedData();
		model.updateWorldData();
		//System.out.println(model.getSpatial().getParent() + "\t"+  engaged + "\t" + model.getWorldTranslation() + "\t" + model.getWorldScale()) ;
	}
}
