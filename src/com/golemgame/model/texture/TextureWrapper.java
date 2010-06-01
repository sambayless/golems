package com.golemgame.model.texture;

import java.io.Serializable;

import com.golemgame.model.effect.ModelEffect;
import com.golemgame.model.effect.TextureLayerEffect;
import com.jme.renderer.ColorRGBA;

public abstract class TextureWrapper implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public static enum TextureFormat
	{
		RGBA(), RGB();
	}
	
	
	
	/*public TextureTypeKey getTextureTypeKey()
	{
		return getTextureTypeKey(0);
	}*/
	public abstract TextureTypeKey getTextureTypeKey(int element);
//	public abstract void apply(Model model) throws ModelTypeException;
	public enum ApplyMode
	{
		BLEND,DECAL,MODULATE,COMBINE,ADD,REPLACE;
	}
	
	/**
	 * Create a copy of this textureWrapper. 
	 * This will create a light copy of the texture, allowing it to have different properties
	 * but not copying the underlying opengl texture.
	 * @return
	 */
	public abstract TextureWrapper copyTextureWrapper();
	

	public TextureWrapper() {
		super();
		// TODO Auto-generated constructor stub
	}

	public abstract void setTint(ColorRGBA tintColor);
	
	public abstract ColorRGBA getTint();
	
	public abstract void setApplyMode(ApplyMode mode);
	public abstract ApplyMode getApplyMode();
	

	public abstract boolean isLoaded();


	public int getElements() {
		return 1;
	}

	public abstract void load(TextureTypeKey loadedKey);

	protected ModelEffect associatedEffect = null;
	
	public synchronized void setAssociatedEffect(ModelEffect textureLayerEffect) {
		associatedEffect=textureLayerEffect;
		if(isLoaded())
			associatedEffect.refreshEffect();
	}
	public synchronized void clearAssociatedEffect(ModelEffect textureLayerEffect) {
		if(this.associatedEffect==textureLayerEffect)
			this.associatedEffect=null;//possible threading problem?
		
	}
	
	
}
