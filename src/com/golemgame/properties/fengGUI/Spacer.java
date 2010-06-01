package com.golemgame.properties.fengGUI;

import org.fenggui.StandardWidget;
import org.fenggui.appearance.DefaultAppearance;
import org.fenggui.appearance.IAppearance;
import org.fenggui.binding.render.Graphics;
import org.fenggui.binding.render.IOpenGL;
import org.fenggui.util.Dimension;

public class Spacer extends StandardWidget {
	private DefaultAppearance appearance = new DefaultAppearance(this);
	public Spacer() {
		super();
		
	}

	public Spacer(int width, int height) {
		super();
		this.setMinSize(width, height);
	}
	
	@Override
	public IAppearance getAppearance() {
		return appearance;
	}

	@Override
	public Dimension getMinContentSize() {
		return this.getMinSize();
	}

	@Override
	public void paintContent(Graphics g, IOpenGL gl) {
		// TODO Auto-generated method stub
		
	}

}
