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
package com.golemgame.toolbar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.golemgame.util.TextTextureBuilder;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.simplemonkey.widgets.TextureWidget;

public class MenuButton extends TextureWidget implements Button{

	
	
	private String text;
	private Texture textTexture;
	private Texture activeTexture;
	private Texture hoverTexture;
	private Texture inactiveTexture;
	
	public MenuButton(String text) {
		super();
		this.text = text;
		Dimension size = new Dimension();
		textTexture = TextTextureBuilder.buildTextTexture(text,Font.decode("Sans-Serif").deriveFont(18f),Color.LIGHT_GRAY,size);
		this.setMinSize(size.width, size.height);
		super.setExpandable(false);
		super.setShrinkable(false);
		super.setSizeToMinSize();
		super.setTexture(textTexture,1);
		
	//	textTexture = TextureManager.loadTexture(getClass().getClassLoader().getResource(""))
		
		this.addButtonListener(new ButtonAdapter()
		{

			
			public void activate() {
				setTexture(activeTexture,0);
			}

			
			public void anti_hover() {
				setTexture(inactiveTexture,0);
			}

			
			public void hover() {
				setTexture(hoverTexture,0);
			}

			
			public void inactive() {
				setTexture(inactiveTexture,0);
			}
			
		});
		
	}
	public Texture getTextTexture() {
		return textTexture;
	}
	
	private List<ButtonListener> listeners = new CopyOnWriteArrayList<ButtonListener>();
	
	private ButtonState state = ButtonState.INACTIVE;
	
	
	public void addButtonListener(ButtonListener listener) {
		listeners.add(listener);
	}

	
	public void removeButtonListener(ButtonListener listener) {
		listeners.remove(listener);
	}


	
	public void mouseMove(int x, int y) {
		if(this.state != ButtonState.HOVER && this.state != ButtonState.ACTIVE)
			setState(ButtonState.HOVER);
		super.mouseMove(x, y);
	}

	
	public void mouseOff() {
		if(this.state == ButtonState.HOVER)
			setState(ButtonState.ANTI_HOVER);
		else if (this.state == ButtonState.ACTIVE)
		{
			this.setState(ButtonState.INACTIVE);
			this.setState(ButtonState.ANTI_HOVER);
		}
		super.mouseOff();
	}

	
	public void mousePress(boolean pressed, int button, int x, int y) {
		if(pressed && button == 0)
		{
			this.setState( ButtonState.ACTIVE);
		}else if(button == 0)
		{
			if(super.testCollision(new Vector3f(x,y,0)))
			{
				this.setState(ButtonState.INACTIVE);
				this.setState(ButtonState.ANTI_HOVER);
			}else
				this.setState(ButtonState.INACTIVE);
			
		}
		
		super.mousePress(pressed, button, x, y);
	}	
	
	private void setState(ButtonState state)
	{
		this.state = state;
		for (ButtonListener listener:this.listeners)
			listener.buttonStateChange(state);
	}

	

	
}
