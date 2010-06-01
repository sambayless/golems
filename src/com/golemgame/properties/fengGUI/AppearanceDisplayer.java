package com.golemgame.properties.fengGUI;

import java.nio.FloatBuffer;

import org.fenggui.StatefullWidget;
import org.fenggui.appearance.DefaultAppearance;
import org.fenggui.binding.render.Graphics;
import org.fenggui.binding.render.IOpenGL;
import org.fenggui.binding.render.ITexture;
import org.fenggui.util.Dimension;
import org.fenggui.util.Rectangle;
import org.lwjgl.opengl.GL11;

import com.golemgame.model.texture.TextureServer.NoTextureException;
import com.golemgame.model.texture.fenggui.FengGUITexture;
import com.golemgame.model.texture.fenggui.FengGUITextureLoadListener;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.AppearanceInterpreter.ImageType;
import com.golemgame.structural.StructuralAppearanceEffect;
import com.jme.renderer.ColorRGBA;

public class AppearanceDisplayer extends StatefullWidget<DefaultAppearance> {
	
	private boolean flip;
	public boolean isFlip() {
		return flip;
	}



	public void setFlip(boolean flip) {
		this.flip = flip;
	}

	private StructuralAppearanceEffect appearance;
	private FengGUITexture texture;
	
	private FengGUITextureLoadListener loadListener = new FengGUITextureLoadListener(){

		public void textureLoaded(FengGUITexture texture) {
			
		}
		
	};
	
	public StructuralAppearanceEffect getStructuralAppearanceEffect() {
		return appearance;
	}



	public AppearanceDisplayer(boolean flip) {
		super();
		this.flip  = flip;
		this.setAppearance(new DefaultAppearance(this));
		this.appearance = new StructuralAppearanceEffect(new PropertyStore());
		this.appearance.refresh();
		this.setMinSize(256,256);
	}
	public AppearanceDisplayer(){
		 this(true);
	}
	@Override
	public Dimension getMinContentSize() {
		return this.getMinSize();
	}

	@Override
	public void paintContent(Graphics g, IOpenGL gl) {
		ColorRGBA c = appearance.getBaseColor();
		
		g.setColor(c.r, c.g, c.b, c.a);
		g.drawFilledRectangle(0, 0, this.getWidth(),this.getHeight());
		try{
			if (this.texture != null && texture.getTextureTypeKey().getImage() != ImageType.VOID && texture.getTexture()!= null && texture.getTexture().getID()>0)
			{
				if(flip)
					drawScaledTintedImage(g, gl, texture.getTexture(), appearance.getHighlightTexture().getTint(),0,this.getHeight(), this.getWidth(), -this.getHeight());
				else
					drawScaledTintedImage(g, gl, texture.getTexture(), appearance.getHighlightTexture().getTint(),0,0, this.getWidth(), this.getHeight());

			}
		}catch(NoTextureException e)
		{
			
		}
	}
	
	private FloatBuffer colorBuffer = FloatBuffer.allocate(4);
	
	
	
	
	private void drawScaledTintedImage(Graphics g, IOpenGL gl,ITexture texture, ColorRGBA color,int x, int y, int width, int height)
	{
		x += g.getTranslation().getX();
		y += g.getTranslation().getY();

		gl.enableTexture2D(true);

		{
			//gl.setTexEnvModeModulate();
		}
		 
        colorBuffer.clear();
         colorBuffer.put(color.r).put(color.g).put(color.b).put(color.a);
         colorBuffer.rewind();


	  	texture.bind();
		 GL11.glTexEnv(GL11.GL_TEXTURE_ENV,
                 GL11.GL_TEXTURE_ENV_COLOR, colorBuffer);
	      GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
                  GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);//inform gl that no mipmaps are being used.
		// GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
		 //  GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
        //           GL11.GL_TEXTURE_ENV_MODE, GL11.GL_DECAL);
		gl.startQuads();

		float startY = 0.0f; // top
		float startX = 0.0f; // left
		float endY = 1.0f; // bottom
		float endX = 1.0f; // right

		int rWidth = width;
		int rHeight = height;

		// fit into clip - both the polygon to render (x,y,x+w,y+h) AND the
		// texture (0,0->1,1)
		Rectangle clipSpace = g.getClipSpace();
		if (x < clipSpace.getX())
		{
			rWidth -= clipSpace.getX() - x;
			startX = (float) (clipSpace.getX() - x) / (float) width;
			x = clipSpace.getX();
		}

		if (x + rWidth > clipSpace.getX() + clipSpace.getWidth())
		{
			rWidth = clipSpace.getX() + clipSpace.getWidth() - x;
			endX = (float) rWidth / (float) width;
		}

		if (y < clipSpace.getY())
		{
			rHeight -= clipSpace.getY() - y;
			endY = (float) rHeight / (float) height;
			y = clipSpace.getY();
		}

		if (y + rHeight > clipSpace.getY() + clipSpace.getHeight())
		{
			rHeight = clipSpace.getY() + clipSpace.getHeight() - y;
			startY = (float) (height - rHeight) / (float) height;
		}

		gl.texCoord(startX, endY);
		gl.vertex(x, y);

		gl.texCoord(startX, startY);
		gl.vertex(x, rHeight + y);

		gl.texCoord(endX, startY);
		gl.vertex(rWidth + x, rHeight + y);

		gl.texCoord(endX, endY);
		gl.vertex(rWidth + x, y);
		gl.end();
		gl.enableTexture2D(false);
	}

	public FengGUITexture getTexture() {
		return texture;
	}

	public void setTexture(FengGUITexture texture) {
		if(this.texture!= texture && this.texture!=null)
			this.texture.setListener(null);
		this.texture = texture;
		texture.setListener(loadListener);
	}



	
}
