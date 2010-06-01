package com.golemgame.states.camera.skybox;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.Skybox;
import com.jme.util.TextureManager;

public class ImageSkyBox extends SkyBoxData{
	private Map<Face,Texture> textures = new HashMap<Face,Texture>();
	
	
	public ImageSkyBox(String name, BufferedImage... img) {
		super(name);
		int i = 0;
		for(Face face:Face.values())
		{
			BufferedImage image = img[i++];
			Texture imageTex= new Texture(); 
			imageTex.setApply(Texture.AM_DECAL);

			
			imageTex.setFilter(Texture.FM_LINEAR);
			imageTex.setImage(TextureManager.loadImage(image,true));
			imageTex.setMipmapState(Texture.MM_NONE);
	        
			//imageTex.setWrap(Texture.WM_WRAP_S_WRAP_T);

			//imageTex.setScale(new Vector3f(1.00001f,1.00001f,1.00001f));//this is to force the scaling to be refreshed...
			imageTex.setTranslation(new Vector3f());
			textures.put(face, imageTex);
			
		}
		 
	
	}
	

	public void apply(Face face,Skybox skybox, int unit)
	{
		skybox.setTexture(face.getSkyboxEnum(), textures.get(face),unit);
	}
	
	
}
