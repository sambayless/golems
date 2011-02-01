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
package com.golemgame.toolbar.option;


import com.golemgame.toolbar.ButtonAdapter;
import com.golemgame.toolbar.ButtonWidget;
import com.jme.image.Texture;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;

/**
 * This creates an option button.
 * @author Sam
 *
 */
public class OptionWidget extends ButtonWidget {

	private Node spatial;
	
	private boolean value = false;

	private Quad buttonQuad;


	public OptionWidget(Texture down, Texture up, Texture hover, Texture textBacking, String text) {
		super();
		buttonQuad = new Quad("button", 1, 1);
		spatial = new Node();
		spatial.attachChild(buttonQuad);
		buttonQuad.getLocalTranslation().set(1f/2f, 1f/2f,0);
		
		super.addButtonListener(new ButtonAdapter()
		{

			
			public void activate() {
				value = !value;
				super.activate();
			}
			
		});
		
	}

	
	public boolean isValue() {
		return value;
	}



	public void setValue(boolean value) {
		this.value = value;
	}

	
	public Node getSpatial() {

		return spatial;
	}

}
