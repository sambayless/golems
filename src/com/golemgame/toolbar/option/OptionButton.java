package com.golemgame.toolbar.option;


import java.awt.Color;

import com.golemgame.toolbar.ButtonAdapter;
import com.golemgame.toolbar.ButtonGroup;
import com.jme.image.Texture;
import com.jme.util.TextureManager;
import com.simplemonkey.layout.RowLayout;
import com.simplemonkey.widgets.TextWidget;
import com.simplemonkey.widgets.TextureWidget;


public class OptionButton extends ButtonGroup {
	//add this to a standard button holder that is set to fade in/fade out on mouse over.
	//control key pressing maps to a settings entry, which has a list of listeners (including tools, and the options button).
	//the tools turn on control mode when the settings listener is called, NOT on directly catching the key press.
	//the restrict movement key setting will be moved to a global settings repository (or atleast, to a gamestate dependant settings repository).


	//new transition class: generalized transition button that can operate on any provided set of material states
	//transition button will use this (internal field) to operate; the states are mapped to calls to transitionclass


	private boolean value = false;


	private TextureWidget checkBox;
	private TextWidget textButton;
	
	private Texture activeTexture;
	private Texture inactiveTexture;
	
	public OptionButton(String text) {
		super();
		
		inactiveTexture = TextureManager.loadTexture(
				getClass().getClassLoader().getResource(
	            "com/golemgame/data/textures/buttons/glass/Crystal.png"),
	            Texture.MM_LINEAR_NEAREST,
	            Texture.FM_LINEAR);
		
		activeTexture = TextureManager.loadTexture(
				getClass().getClassLoader().getResource(
	            "com/golemgame/data/textures/buttons/glass/Glass_Blue.png"),
	            Texture.MM_LINEAR_NEAREST,
	            Texture.FM_LINEAR);		
		
		inactiveTexture.setApply(Texture.AM_DECAL);
		activeTexture.setApply(Texture.AM_DECAL);
		
		checkBox = new TextureWidget();
		textButton = new TextWidget(text);
		textButton.setText(text,Color.LIGHT_GRAY);
		checkBox.setMinSize(textButton.getMinHeight(), textButton.getMinHeight());
		super.setLayoutManager(new RowLayout());
		super.addWidget(checkBox);
		super.addWidget(textButton);
		this.setExpandable(false);
		this.setShrinkable(false);
		this.setSize(checkBox.getWidth()+textButton.getWidth(),checkBox.getHeight());
		
		textButton.setExpandable(false);
		checkBox.setExpandable(false);
		super.layout();
		
		textButton.getTextTexture().setApply(Texture.AM_DECAL);
	
		this.setValue(this.isValue());

		
		super.addButtonListener(new ButtonAdapter()
		{

			@Override
			public void activate() {
				setValue(!isValue());
				super.activate();
				
			
			}
			
		});
	}


	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {

			this.value = value;
			if(value)
			{
				checkBox.setTexture(activeTexture,1);
			}else
			{
				checkBox.setTexture(inactiveTexture,1);
			}
	
	}


	public void setTexture(Texture texture, int unit)
	{
		textButton.setTexture(texture, unit);
		checkBox.setTexture(texture, unit);
	}

}