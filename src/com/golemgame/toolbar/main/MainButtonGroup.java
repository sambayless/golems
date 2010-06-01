package com.golemgame.toolbar.main;






import com.golemgame.toolbar.ButtonGroup;
import com.jme.image.Texture;



public class MainButtonGroup extends ButtonGroup {
	private static final long serialVersionUID = 1L;
	private Texture active = null;
	private Texture inactive = null;
	private Texture hover = null;
	
	public MainButtonGroup(float width)
	{
		super();
		super.setWidth(width);
		//maxWidth = width;
		super.setLayoutManager(new MainLayout(2));
		super.setExpandable(true);
		super.setShrinkable(true);
	}
	
	public MainButtonGroup(float width, Texture inactive, Texture hover,Texture active) {
		this(width);
		this.active = active;
		this.inactive = inactive;
		this.hover = hover;
	}
	

	
	public Texture getActive() {
		return active;
	}

	public void setActive(Texture active) {
		this.active = active;
	}

	public Texture getInactive() {
		return inactive;
	}

	public void setInactive(Texture inactive) {
		this.inactive = inactive;
	}

	public Texture getHover() {
		return hover;
	}

	public void setHover(Texture hover) {
		this.hover = hover;
	}

	
}
