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
package com.golemgame.model.effect;

import java.lang.ref.WeakReference;

import com.golemgame.model.Model;
import com.golemgame.model.Model.ModelTypeException;
import com.golemgame.model.spatial.SpatialModel;
import com.jme.math.FastMath;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;

public class TintableColorEffect extends ModelEffectImpl {
	private static final long serialVersionUID = 1L;
	private static final ModelEffectType[] types = new ModelEffectType[]{ModelEffectType.COLOR};

	private static CullState opaqueCullState ;
	private static AlphaState opaqueAlphaState ;
	private static AlphaState semiTransparentAlphaState ;
	private static CullState semiTransparentCullState ;
	
	private ColorRGBA color; 
	private boolean isOpaque;
	private float shininess;
	
	private transient MaterialState material;
	private transient AlphaState alpha;
	private transient CullState cull;
	
	
	public TintableColorEffect() {
		this( new ColorRGBA(ColorRGBA.white),true,128);

	}
	
	public TintableColorEffect(ColorRGBA color,boolean useSets) {
		this(color,color.a < 1-FastMath.FLT_EPSILON,128,useSets);
	}
	
	public TintableColorEffect(ColorRGBA color) {
		this(color,false);
	}

	public TintableColorEffect(ColorRGBA color, boolean isOpaque,
			float shininess) {
		this(color,isOpaque,shininess,false);
	}

	public TintableColorEffect(ColorRGBA color, boolean isOpaque,
			float shininess,boolean useSets) {
		super(useSets);
		this.color = color;
		this.isOpaque = isOpaque;
		this.shininess = shininess;
		material = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
	//	material.setSpecular(new ColorRGBA(0.2f,0.2f,0.7f,0.3f));
		material.setShininess(this.shininess);
		material.setColorMaterial(MaterialState.CM_NONE );
	
		
		this.setColor(color);
		
	}

	
	
	protected static CullState getOpaqueCullState()
	{
		if (opaqueCullState == null)
		{
			opaqueCullState = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
			opaqueCullState.setCullMode(CullState.CS_BACK);
		}
		return opaqueCullState;
	}
	
	protected static AlphaState getOpaqueAlphaState()
	{
		if (opaqueAlphaState == null)
		{
			opaqueAlphaState = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
			opaqueAlphaState.setBlendEnabled(false);
			opaqueAlphaState.setEnabled(false);
			opaqueAlphaState.setTestEnabled(false);	
		}
		return opaqueAlphaState;
	
	}

	protected static CullState getSemiTransparentCullState()
	{
		if (semiTransparentCullState == null)
		{
			semiTransparentCullState = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
			semiTransparentCullState.setCullMode(CullState.CS_NONE);
		}
		return semiTransparentCullState;
	}
	
	protected static AlphaState getSemiTransparentAlphaState()
	{
		if (semiTransparentAlphaState == null)
		{
			semiTransparentAlphaState = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();

			// Blend between the source and destination functions.
			semiTransparentAlphaState.setBlendEnabled(true);
			// Set the source function setting.
			semiTransparentAlphaState.setSrcFunction(AlphaState.SB_SRC_ALPHA);
			// Set the destination function setting.
			semiTransparentAlphaState.setDstFunction(AlphaState.SB_ONE_MINUS_SRC_ALPHA);
			// Enable the test.
			semiTransparentAlphaState.setTestEnabled(true);		
			// Set the test function to TF_GREATER.
			semiTransparentAlphaState.setTestFunction(AlphaState.TF_ALWAYS);		
			
			//DisplaySystem.getDisplaySystem().getRenderer().getQueue().setTwoPassTransparency(true);
		}
		return semiTransparentAlphaState;
	
	}
	
	public ModelEffectType[] getEffectTypes() {
		return types;
	}
	
	

	

	protected void refreshEffect(Model model) throws ModelTypeException {
			if (!(model instanceof SpatialModel))
				throw new ModelTypeException();
			
			SpatialModel sModel = (SpatialModel) model;
			Spatial spatial = sModel.getSpatial();
			
			if (isOpaque)
				spatial.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
			else
				spatial.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
			
				spatial.setRenderState(material);
				spatial.setRenderState(alpha);
				spatial.setRenderState(cull);		
		
				
				spatial.updateRenderState();
				sModel.refreshLockedData();
	}
	
	

	@Override
	public void removeModel(Model model) {
		super.removeModel(model);
		if (!(model instanceof SpatialModel))
			throw new ModelTypeException();
		
		SpatialModel sModel = (SpatialModel) model;
		Spatial spatial = sModel.getSpatial();
		//FIXME: Why could this NPE.
		if (spatial.getRenderState(material.getType()) == material)
			spatial.clearRenderState(material.getType());
		if (spatial.getRenderState(cull.getType()) == cull)
			spatial.clearRenderState(cull.getType());
		if (spatial.getRenderState(alpha.getType()) == alpha)
		{
			spatial.clearRenderState(alpha.getType());
			spatial.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
		}
		model.refreshLockedData();
	}



	public ColorRGBA getColor() {
		return color;
	}

	public void setColor(ColorRGBA color) {
		this.color.set(color);
		boolean oldOpaque = this.isOpaque;
		this.isOpaque = color.a > 1f - FastMath.FLT_EPSILON;
		
		if (isOpaque())
		{
			cull = getOpaqueCullState();
			alpha = getOpaqueAlphaState();
			material.setMaterialFace(MaterialState.MF_FRONT);
			
		}else
		{
			cull = getSemiTransparentCullState();
			alpha = getSemiTransparentAlphaState();
			material.setMaterialFace(MaterialState.MF_FRONT_AND_BACK);
		}
		material.getAmbient().set(this.color);
		material.getDiffuse().set(this.color);
		material.getSpecular().set(this.color);
		if (oldOpaque!=this.isOpaque)
			refreshEffect();
		
	}

	public boolean isOpaque() {
		return isOpaque;
	}
	
	
	public void set(ModelEffect setFrom) throws ModelEffectTypeException {
		super.set(setFrom);	
		if (!(setFrom instanceof TintableColorEffect))
				throw new ModelEffectTypeException("Wrong effect type");
		TintableColorEffect effect = (TintableColorEffect) setFrom;
		
		this.setShininess(effect.getShininess());
		
		this.setColor(effect.getColor());
		
		refreshEffect();
	}



	public float getShininess() {
		return shininess;
	}



	public void setShininess(float shininess) {
		this.shininess = shininess;
	}
	
	
	public ModelEffect copy() 
	{
		ModelEffect copy = new TintableColorEffect();
		copy.set(this);
		return copy;
	}






	

	
}
