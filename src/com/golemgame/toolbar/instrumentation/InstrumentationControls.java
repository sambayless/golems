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
package com.golemgame.toolbar.instrumentation;

import com.golemgame.instrumentation.InstrumentationLayer;
import com.golemgame.toolbar.ButtonListener;
import com.golemgame.toolbar.StandardButton;
import com.golemgame.toolbar.Button.ButtonState;
import com.jme.image.Texture;
import com.jme.util.TextureManager;
import com.simplemonkey.Container;
import com.simplemonkey.layout.BorderLayout;
import com.simplemonkey.layout.BorderLayoutData;
import com.simplemonkey.layout.RowLayout;

public class InstrumentationControls extends Container {

	private final InstrumentationLayer instruments;


	public InstrumentationControls(final InstrumentationLayer instruments) {
		super();
		this.instruments = instruments;
		this.setVisible(false);
		this.setShrinkable(false);
		this.setSize(32,32);
		Texture runInactive = TextureManager.loadTexture(
	  			getClass().getClassLoader().getResource(
	            "com/golemgame/data/textures/buttons/glass/Crystal.png"),
	            Texture.MM_LINEAR_NEAREST,
	            Texture.FM_LINEAR);

	  	Texture runActive = TextureManager.loadTexture(
	  			getClass().getClassLoader().getResource(
	            "com/golemgame/data/textures/buttons/glass/Glass_White.png"),
	            Texture.MM_LINEAR_NEAREST,
	            Texture.FM_LINEAR);
	  	
	  	Texture runHover = TextureManager.loadTexture(
	            getClass().getClassLoader().getResource(
	            "com/golemgame/data/textures/buttons/glass/Glass_White.png"),
	            Texture.MM_LINEAR_NEAREST,
	            Texture.FM_LINEAR);
	  	
	  	Texture icon = TextureManager.loadTexture(
	            getClass().getClassLoader().getResource(
	            "com/golemgame/data/textures/buttons/functionals/Oscilloscope.png"),
	            Texture.MM_LINEAR_NEAREST,
	            Texture.FM_LINEAR);
	
	  	final StandardButton pause= new StandardButton(32,32,runActive,runInactive,runHover,icon,"Toggle Instruments");
	  	pause.setShrinkable(false);
/*	 	{
	 		MaterialState white;
			white = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
	
			white.setColorMaterial(MaterialState.CM_NONE );
			white.setMaterialFace(MaterialState.MF_FRONT);
			
			AlphaState semiTransparentAlpha = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
	
			semiTransparentAlpha.setBlendEnabled(true);
			semiTransparentAlpha.setSrcFunction(AlphaState.SB_SRC_ALPHA);
			semiTransparentAlpha.setDstFunction(AlphaState.SB_ONE_MINUS_SRC_ALPHA);
			semiTransparentAlpha.setTestEnabled(true);		
			semiTransparentAlpha.setTestFunction(AlphaState.TF_GREATER);		
			
			pause.getSpatial().setRenderState(white);
	
			pause.getSpatial().setRenderState(semiTransparentAlpha);
	
			Texture backgroundStripes = TextureManager.loadTexture(getClass().getClassLoader().getResource("com/golemgame/data/textures/menu/semi.png"),Texture.MM_LINEAR_NEAREST,Texture.MM_LINEAR);
			Texture backgroundStripes2 = backgroundStripes.createSimpleClone();
			Texture backgroundStripes3 = backgroundStripes.createSimpleClone();


			pause.getSpatial().updateRenderState();
	
	}*/

		pause.getSpatial().updateWorldVectors();
		
		Container buttonContainer = new Container();
		

		buttonContainer.addWidget(pause);

		this.addWidget(buttonContainer);
		buttonContainer.setLayoutData(BorderLayoutData.CENTER);
		this.setLayoutData(BorderLayoutData.NORTH);
		this.setLayoutManager(new BorderLayout());
		buttonContainer.setLayoutManager(new RowLayout());
		
		this.layout();
	
		pause.addButtonListener(new ButtonListener(){		


			public void buttonStateChange(ButtonState state) {
				if(state==ButtonState.ACTIVE)
				{
					instruments.setVisible(!instruments.isVisible());
				}
			}
		});

	
	

	}


}
