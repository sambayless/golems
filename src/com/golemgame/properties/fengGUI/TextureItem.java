package com.golemgame.properties.fengGUI;

import com.golemgame.model.texture.Images;
import com.golemgame.model.texture.TextureTypeKey;
import com.golemgame.model.texture.fenggui.FengGUITexture;
import com.golemgame.mvc.golems.AppearanceInterpreter.ImageType;


public class TextureItem implements Comparable<TextureItem>
{
	private final TextureTypeKey TextureTypeKey;
	private final FengGUITexture fengGUItexture;
	private String name;
	
	public TextureItem(TextureTypeKey TextureTypeKey, FengGUITexture fengGUItexture) {
		super();
		this.TextureTypeKey = TextureTypeKey;
		this.fengGUItexture = fengGUItexture;
		name = Images.getInstance().getName(TextureTypeKey.getImage());
	}
	
	public TextureTypeKey getTextureTypeKey() {
		return TextureTypeKey;
	}

	public FengGUITexture getFengGUItexture() {
		return fengGUItexture;
	}

	
	public String toString() {
		return name;
	}

	
	public int compareTo(TextureItem o) {
		if (o.TextureTypeKey.getImage() == null || o.getTextureTypeKey().getImage() == ImageType.VOID)
		{
			return 1;
		}else if(this.TextureTypeKey.getImage() == null || this.getTextureTypeKey().getImage() == ImageType.VOID)
		{
			return -1;
		}else
		{
			return this.name.compareTo(o.name);
		}
	}
	
	
}