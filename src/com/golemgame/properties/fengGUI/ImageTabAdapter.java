package com.golemgame.properties.fengGUI;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.util.TextureManager;

public class ImageTabAdapter extends TabAdapter implements ImageTab {
	
	private static final String DEFAULT_TEXTURE_HOVER = "buttons/glass/Glass_Blue.png";
	private static final String DEFAULT_TEXTURE_ACTIVE = "buttons/solid/Solid_White.png";
	private static final String DEFAULT_TEXTURE_INACTIVE = "buttons/solid/Solid_Blue.png";
	private static final String DEFAULT_TEXTURE_ICON = "buttons/polyhedrons/Sphere.png";
	
	public ImageTabAdapter(String title) {
		super(title);
	}

	public Texture getHover() {
		return loadTexture(DEFAULT_TEXTURE_HOVER);
		
	}

	public Texture getInactive() {
		return loadTexture(DEFAULT_TEXTURE_INACTIVE);
		
	}
	
	public Texture getActive() {
		return loadTexture(DEFAULT_TEXTURE_ACTIVE);
		
	}
	
	public Texture getIcon() {
		return loadTexture(DEFAULT_TEXTURE_ICON);
		
	}
	public Texture loadTexture(String path)
	{
		Image textureImage = TextureManager.loadImage(getClass().getClassLoader().getResource(
	            "com/golemgame/data/textures/" + path), false);
	
	
	
	Texture texture = new Texture();
	texture.setImageLocation(path);

	 texture.setAnisoLevel(0);
	 texture.setMipmapState( Texture.MM_LINEAR);
	 texture.setFilter(Texture.FM_LINEAR);
	 texture.setImage(textureImage);
	 
	 return texture;
	}
}
