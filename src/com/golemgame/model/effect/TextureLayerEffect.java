package com.golemgame.model.effect;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

import com.golemgame.model.Model;
import com.golemgame.model.Model.ModelTypeException;
import com.golemgame.model.texture.TextureServer;
import com.golemgame.model.texture.TextureWrapper;
import com.golemgame.model.texture.TextureServer.NoTextureException;
import com.golemgame.states.StateManager;
import com.jme.renderer.ColorRGBA;

public class TextureLayerEffect extends ModelEffectImpl implements Comparable<TextureLayerEffect>{

	
	
	private static final long serialVersionUID = 1L;
	
	
	
	protected int maximumAllowedLayer = 0;
	private TextureWrapper textureWrapper;
	
	private int precedence = 1;
	
	public int getPrecedence() {
		return precedence;
	}

	public void setPrecedence(int precedence) {
		this.precedence = precedence;
	}

	protected final static ModelEffectType[] types = new ModelEffectType[]{ModelEffectType.TEXTURELAYER};
		
	public TextureLayerEffect(
			TextureWrapper textureWrapper,int maximumAllowedLayer) {
		super();
		this.maximumAllowedLayer = maximumAllowedLayer;
		this.setTextureWrapper(textureWrapper);
		textureWrapper.setAssociatedEffect(this);
	}
	
	public TextureLayerEffect()
	{
		this.maximumAllowedLayer = 0;
		
	}




	protected void refreshEffect(Model model) throws ModelTypeException {

		if(this.getTextureWrapper() ==null)
			return;
		
		if( this.getTextureWrapper().isLoaded())
		{
			model.getTextureController().addTextureLayerEffect(this);
			
		}else 
		{
			/*Callable<Object> callback = new Callable<Object>()
			{

			
				public Object call() throws Exception {
					
					requestUpdate();
					
					return null;
				}
				
			};*/
	/*		try {
			
				//TextureServer.getInstance().addTextureLoadingCallback(this.getTextureWrapper());
				TextureServer.getInstance().c
				TextureServer.getInstance().loadTextureWrapper(this.getTextureWrapper());
			} catch (NoTextureException e) {
	
				StateManager.logError(e);
			}*/
		}
	}


	public ModelEffectType[] getEffectTypes() {
		return types;
	}




	@Override
	public void removeModel(Model model) {
		super.removeModel(model);
		model.getTextureController().removeTextureLayerEffect(this);
		model.refreshLockedData();
	}


	public TextureWrapper getTextureWrapper() {
		return textureWrapper;
	}

	public void setTextureWrapper(TextureWrapper textureWrapper) {
		
		if (textureWrapper!= this.textureWrapper)
		{
			if(this.textureWrapper!=null)
				this.textureWrapper.clearAssociatedEffect(this);
			
			this.textureWrapper = textureWrapper;
			textureWrapper.setAssociatedEffect(this);
			refreshEffect();
		}

	}

	public int getMaximumAllowedLayer() {
		return maximumAllowedLayer;
	}



	public void set(ModelEffect setFrom) throws ModelEffectTypeException {
		super.set(setFrom);	
		if (!(setFrom instanceof TextureLayerEffect))
				throw new ModelEffectTypeException("Wrong effect type");
		TextureLayerEffect effect = (TextureLayerEffect) setFrom;
		TextureWrapper copy = TextureServer.getInstance().getTexture(effect.getTextureWrapper().getTextureTypeKey(0));
		copy.setApplyMode(effect.getTextureWrapper().getApplyMode());
		copy.setTint(new ColorRGBA(effect.getTextureWrapper().getTint()));
		this.maximumAllowedLayer = effect.getMaximumAllowedLayer();
		this.setTextureWrapper(copy);
		

		
		
	
	}

	public void setMaximumAllowedLayer(int maximumAllowedLayer) {
		this.maximumAllowedLayer = maximumAllowedLayer;
	}

	public ModelEffect copy() 
	{
		ModelEffect copy = new TextureLayerEffect();
		copy.set(this);
		return copy;
	}

	public int compareTo(TextureLayerEffect o) {
		return this.precedence-o.precedence;
	}
	
	

}
