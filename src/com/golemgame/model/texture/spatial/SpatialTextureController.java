package com.golemgame.model.texture.spatial;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

import com.golemgame.model.Model.ModelTypeException;
import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.model.texture.TextureController;
import com.golemgame.model.texture.TextureWrapper;
import com.jme.image.Texture;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueueManager;

public class SpatialTextureController extends TextureController
{
	private static final long serialVersionUID = 1L;
	public static final int MAX_SUPPORTED_TEXTURES = 10;
	private transient TextureState[] textureStates;
	private SpatialModel model;
	
	public SpatialTextureController(SpatialModel model) throws ModelTypeException {
		super();
		this.model= model;
		textureStates = new TextureState[model.getNumberOfPositions()];
		textureStates[0] = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();

	}

	

	

	@Override
	protected void resetTextures() {
		Iterator<TextureWrapper> iterator = super.getTextureWrappers();
		int unit = 1;		
		for(TextureState textureState:textureStates)
		{
			if(textureState!=null)
				textureState.clearTextures();
		}
		
		try{
		while(iterator.hasNext())
		{
			TextureWrapper wrapper = iterator.next();
			if (!(wrapper instanceof SpatialTexture))
			{
				throw new ModelTypeException("wrong texture type for model");
			}
			SpatialTexture spatialTexture = (SpatialTexture)wrapper;
			
			for (int element = 0;element<spatialTexture.getElements() && element<textureStates.length;element++)
			{
				Texture texture = spatialTexture.getTexture(element);
				if (texture != null)
				{
					if(textureStates[element]==null)
					{
						textureStates[element]= DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
					}
					//do some intelligent texture wrapping, based on the texture shape and the spatial...
					model.setTextureShape(spatialTexture.getTextureTypeKey().getShape(), unit);
					
					textureStates[element].setTexture(texture, unit);
				
					
				}
			}
			
			for(int position = spatialTexture.getElements();position<textureStates.length ;position++ )
			{//if the number of positions supported by the model is more than the provided texture elements, fill the remaining ones in with texture 0
				Texture texture = spatialTexture.getTexture(0);
				if (texture != null)
				{
					if(textureStates[position]==null)
					{
						textureStates[position]= DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
					}
					//do some intelligent texture wrapping, based on the texture shape and the spatial...
					model.setTextureShape(spatialTexture.getTextureTypeKey().getShape(), unit);
					
					textureStates[position].setTexture(texture, unit);
				
				}
			}
			unit++;
		}
		}catch(NoSuchElementException e)
		{
			
		}

	}

	@Override
	public void apply() {
		
		resetTextures();
		for (int i =0;i<textureStates.length;i++)
		{
			TextureState textureState = textureStates[i];			
			if(textureState!=null)
			{
				model.applyRenderState(textureState, i);
			}
		}
		model.getSpatial().updateRenderState();
		model.refreshLockedData();
	}




/*
	@Override
	protected void finalize() throws Throwable {
		GameTaskQueueManager.getManager().update(new Callable<Object>(){

			public Object call() throws Exception {
				for (int i =0;i<textureStates.length;i++)
				{
					TextureState textureState = textureStates[i];			
					if(textureState!=null)
					{
						textureState.deleteAll();
					}
				}
				return null;
			}
			
		});
		super.finalize();
	}*/


/*	@Override
	public void removeEffect(Model model) {
		if (! (model instanceof SpatialModel))
			throw new ModelTypeException();
		
		SpatialModel spatialModel = (SpatialModel) model;
	
		if (spatialModel.getSpatial().getRenderState(RenderState.RS_TEXTURE ) == textureState)
			spatialModel.getSpatial().clearRenderState(RenderState.RS_TEXTURE );
	}
*/
}
