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
package com.golemgame.structural;


import java.lang.ref.WeakReference;

import com.golemgame.model.Model;
import com.golemgame.model.Model.ModelTypeException;
import com.golemgame.model.effect.ModelEffect;
import com.golemgame.model.effect.ModelEffectImpl;
import com.golemgame.model.effect.TextureLayerEffect;
import com.golemgame.model.effect.TintableColorEffect;
import com.golemgame.model.texture.TextureServer;
import com.golemgame.model.texture.TextureTypeKey;
import com.golemgame.model.texture.TextureWrapper;
import com.golemgame.model.texture.TextureTypeKey.TextureShape;
import com.golemgame.model.texture.TextureWrapper.ApplyMode;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.AppearanceInterpreter;
import com.golemgame.mvc.golems.AppearanceInterpreter.ImageType;
import com.jme.renderer.ColorRGBA;

/**
 * This object is intended to hold the serializable, static appearance of a structure.
 * @author Sam
 *
 */
public class StructuralAppearanceEffect extends ModelEffectImpl implements SustainedView
{

	private static final long serialVersionUID = 1L;

	private static final ModelEffectType[] types = new ModelEffectType[]{ModelEffectType.TEXTURELAYER};//ModelEffectType.COLOR, dont report this, allow other colour effects to layer ontop of this. This lets us do cool selection effects.

	//private ColorRGBA highlightColor = new ColorRGBA(1f,1f,1f,1f);
	private TintableColorEffect colorEffect;
	private  TextureLayerEffect textureLayerEffect;
	private TextureLayerEffect textEffect;
	
	private TextureTypeKey.TextureShape preferedShape = TextureTypeKey.TextureShape.Plane;
	
	public TextureTypeKey.TextureShape getPreferedShape() {
		return preferedShape;
	}

	public void setPreferedShape(TextureTypeKey.TextureShape preferedShape) {
		this.preferedShape = preferedShape;
	}

	private AppearanceInterpreter interpreter;
	
	public AppearanceInterpreter getInterpreter() {
		return interpreter;
	}

	public StructuralAppearanceEffect(PropertyStore store) {
		super();
		this.interpreter = new AppearanceInterpreter(store);
		store.setSustainedView(this);
	}
	
/*	public StructuralAppearanceEffect(ColorRGBA baseColor, ColorRGBA highlight,
			TextureWrapper highlightTexture) {
		super();
		this.colorEffect = new TintableColorEffect();
		this.textureLayerEffect = new TextureLayerEffect(highlightTexture, 4);
		this.colorEffect.setColor(baseColor);
		this.textureLayerEffect.getTextureWrapper().setTint(highlight);
		textureLayerEffect.addUpdateListener(this);
		colorEffect.addUpdateListener(this);
	}

	public StructuralAppearanceEffect() {
		this( new ColorRGBA(1f,1f,1f,1f),  new ColorRGBA(0f,0f,0f,0f),TextureServer.getInstance().getTexture(ImageType.DEFAULT) );
		
	
	}
*/
	
	
	
	/*public void applyEffect(Model model) throws ModelTypeException {
		colorEffect.applyEffect(model);
		textureLayerEffect.applyEffect(model);
		if(textEffect!=null)
			textEffect.applyEffect(model);
		model.refreshLockedData();
	}
*/
	
	public ModelEffectType[] getEffectTypes() {
		return types;
	}

	
	@Override
	public void attachModel(Model model) throws ModelTypeException {
		super.attachModel(model);
		refresh();
	}

	@Override
	public void removeModel(Model model) {
		super.removeModel(model);
		colorEffect.removeModel(model);
		textureLayerEffect.removeModel(model);
		model.refreshLockedData();
		refresh();
	}



	public ColorRGBA getBaseColor() {
		return colorEffect.getColor();
	}

	public void setBaseColor(ColorRGBA baseColor) {
	
		this.interpreter.setBaseColor(baseColor);
	/*	
		colorEffect.setColor(baseColor);
		float r = getHighlight().r;
		float g = getHighlight().g;
		float b =  getHighlight().b;
		float a = this.getHighlight().a;
		
		
		r = baseColor.r * (1-a)+ r*a;
		g = baseColor.g * (1-a)+ g*a;
		b = baseColor.b * (1-a)+ b*a;
		
		this.textureLayerEffect.getTextureWrapper().setTint(new ColorRGBA(r,g,b,1f));*/
		refresh();
	}

	public ColorRGBA getHighlight() {
		return this.interpreter.getHighlightColor();
	}

	public void setHighlight(ColorRGBA highlight) {
		this.interpreter.getHighlightColor().set(highlight);
		
		//simulate alpha in the texture by mixing it with the base color.
		
/*		float r = getHighlight().r;
		float g =  getHighlight().g;
		float b =   getHighlight().b;
		float a =  getHighlight().a;
		
		ColorRGBA baseColor = this.getBaseColor();
		r = baseColor.r * (1-a)+ r*a;
		g = baseColor.g * (1-a)+ g*a;
		b = baseColor.b * (1-a)+ b*a;
		
		 this.textureLayerEffect.getTextureWrapper().setTint(new ColorRGBA(r,g,b,1));*/
		 refresh();
	}

	public TextureWrapper getHighlightTexture() {
		return this.textureLayerEffect.getTextureWrapper();
	}

	public void setHighlightTexture(TextureWrapper highlightTexture) {
		
		//ColorRGBA tint= this.textureLayerEffect.getTextureWrapper().getTint();
		
		//highlightTexture.setTint(tint);
		//this.textureLayerEffect.setTextureWrapper(highlightTexture);
		this.interpreter.setTextureImage(highlightTexture.getTextureTypeKey(0).getImage());
		refresh();
	}

	

	
	
	public void set(ModelEffect setFrom) throws ModelEffectTypeException {
		super.set(setFrom);	
		if (!(setFrom instanceof StructuralAppearanceEffect))
				throw new ModelEffectTypeException("Wrong effect type");
		StructuralAppearanceEffect effect = (StructuralAppearanceEffect) setFrom;

			this.colorEffect.set(effect.colorEffect);
			this.getHighlight().set(effect.getHighlight());
			this.textureLayerEffect.set(effect.textureLayerEffect);
			
		//	this.colorEffect.setColor(effect.getBaseColor());
			
			this.setBaseColor(effect.getBaseColor());
			this.setHighlight(effect.getHighlight());
			
	}

	
	@Override
	protected void refreshEffect(Model model) {
/*		
		if(colorEffect!=null)
			colorEffect.refreshEffect();
		
		if(textEffect!=null)
			textEffect.refreshEffect();
		
		if(textureLayerEffect!=null)
			textureLayerEffect.refreshEffect();
		*/
	}

	public void refreshEffect() {
		
		if(colorEffect!=null)
			colorEffect.refreshEffect();
		
		if(textEffect!=null)
			textEffect.refreshEffect();
		
		if(textureLayerEffect!=null)
			textureLayerEffect.refreshEffect();
	}

	public ModelEffect copy() 
	{
		StructuralAppearanceEffect copy = new StructuralAppearanceEffect(interpreter.getStore().deepCopy());
		copy.refresh();
		//copy.set(this);
		return copy;
	}
	
	public void refresh() {
		boolean changed = false;
		
		if(this.colorEffect == null)
		{
			this.colorEffect = new TintableColorEffect();
			changed = true;
		}
		if(!interpreter.getBaseColor().equals(this.colorEffect.getColor()))
		{
			this.colorEffect.setColor(interpreter.getBaseColor());
			changed = true;
		}

		ImageType image = interpreter.getImage();
	
		if (this.textureLayerEffect == null)
		{
			TextureTypeKey requestedTexture =  new TextureTypeKey(image,512,512, TextureWrapper.TextureFormat.RGBA,true,getPreferedShape());
			TextureWrapper texture =	TextureServer.getInstance().getTexture(requestedTexture);
		 	texture.setApplyMode(ApplyMode.BLEND);
			this.textureLayerEffect = new TextureLayerEffect(texture, 4);
		
			//this.textureLayerEffect.addUpdateListener(this);
			changed = true;
		}else if (this.textureLayerEffect.getTextureWrapper() == null || this.textureLayerEffect.getTextureWrapper().getTextureTypeKey(0).getImage() != image)
		{
			//this.textureLayerEffect.removeUpdateListener(this);
			TextureTypeKey requestedTexture =  new TextureTypeKey(image,512,512, TextureWrapper.TextureFormat.RGBA,true,getPreferedShape());
			TextureWrapper texture =	TextureServer.getInstance().getTexture(requestedTexture);
			texture.setApplyMode(ApplyMode.BLEND);
			textureLayerEffect.setTextureWrapper(texture);
			changed = true;
			//this.textureLayerEffect.addUpdateListener(this);
		}
			String text = interpreter.getText();// "TEST\nThis is a test.";//interpreter.getText();
		if(this.textEffect == null)
		{
			TextureTypeKey requestedTexture =  new TextureTypeKey(ImageType.TEXT,256,256, TextureWrapper.TextureFormat.RGBA,false,TextureShape.Plane);
			requestedTexture.setText(text);
			TextureWrapper texture;
		
			texture =	TextureServer.getInstance().getTexture(requestedTexture);
			
		
		 	texture.setApplyMode(ApplyMode.BLEND);
			this.textEffect = new TextureLayerEffect(texture, 4);
			textEffect.setPrecedence(4);
			//this.textEffect.addUpdateListener(this);
			changed = true;
		}else if (this.textEffect.getTextureWrapper() == null || this.textEffect.getTextureWrapper().getTextureTypeKey(0).getText()== null  || !this.textEffect.getTextureWrapper().getTextureTypeKey(0).getText().equals(text))
		{
			TextureTypeKey requestedTexture =  new TextureTypeKey(ImageType.TEXT,256,256, TextureWrapper.TextureFormat.RGBA,false,TextureShape.Plane);
			requestedTexture.setText(text);
			TextureWrapper texture =	TextureServer.getInstance().getTexture(requestedTexture);
			texture.setApplyMode(ApplyMode.BLEND);
			textEffect.setTextureWrapper(texture);
			changed = true;
		}
		
	
	
		
	
		
		/*else if ((text==null || text.length() == 0) && textEffect!=null)
		{//remove text effect
			
			textEffect = null;
			changed = true;
		}*/
		
		float r = getHighlight().r;
		float g =  getHighlight().g;
		float b =   getHighlight().b;
		float a =  getHighlight().a;
		
		ColorRGBA baseColor = this.getBaseColor();
		r = baseColor.r * (1-a)+ r*a;
		g = baseColor.g * (1-a)+ g*a;
		b = baseColor.b * (1-a)+ b*a;
		ColorRGBA tint = new ColorRGBA(r,g,b,1);
		if(!this.textureLayerEffect.getTextureWrapper().getTint().equals(tint))
		{
			 this.textureLayerEffect.getTextureWrapper().setTint(new ColorRGBA(r,g,b,1));
			
			 changed = true;
		}
		
			if(this.textEffect!=null)
		{
			ColorRGBA textColor = interpreter.getTextColor();
			
	
			if(!this.textEffect.getTextureWrapper().getTint().equals(textColor))
			{
				this.textEffect.getTextureWrapper().setTint(textColor);
				 changed = true;
			}
		}
		 
			for(WeakReference<Model> m:super.getModels())
			{
				Model model = m.get();
				if(model!=null)
				{
					if(textEffect!=null)
						textEffect.attachModel(model);
					if(textureLayerEffect !=null)
						textureLayerEffect.attachModel(model);
					if(colorEffect!=null)
						colorEffect.attachModel(model);
				}
			}
			
	//	if(changed){
		//	refreshEffect();
	//	}
	//	 this.requestUpdate();
	//	textureLayerEffect.getTextureWrapper().setTint(interpreter.getHighlightColor());
		//System.out.println(interpreter.getHighlightColor());
	
	}

	public PropertyStore getStore() {
		return interpreter.getStore();
	}
	
	public void remove() {
		// TODO Auto-generated method stub
		
	}

	public String getText() {
		return interpreter.getText();
	}
}
