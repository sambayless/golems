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
