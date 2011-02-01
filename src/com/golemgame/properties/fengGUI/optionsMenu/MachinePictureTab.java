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
package com.golemgame.properties.fengGUI.optionsMenu;

import java.awt.image.BufferedImage;

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.ScrollContainer;
import org.fenggui.binding.render.Binding;
import org.fenggui.binding.render.ITexture;
import org.fenggui.binding.render.Pixmap;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;

import com.golemgame.mechanical.MachineSpace;
import com.golemgame.mvc.golems.ImageInterpreter;
import com.golemgame.properties.fengGUI.TabAdapter;
import com.golemgame.states.StateManager;

public class MachinePictureTab extends TabAdapter {

	private Label picture;
	private ScrollContainer desScroller;
	public MachinePictureTab() {
		super("Picture");
		
	}
	
	@Override
	protected void buildGUI() {
		getTab().setLayoutManager(new BorderLayout());


		Container desContainer = FengGUI.createContainer(getTab());
		desContainer.setLayoutData(BorderLayoutData.CENTER);
		getTab().addWidget(desContainer);
		desContainer.setLayoutManager(new BorderLayout());
		Container labContainer = FengGUI.createContainer(desContainer);
		labContainer.setLayoutData(BorderLayoutData.NORTH);
		labContainer.setShrinkable(false);
		//labContainer.setExpandable(false);
		labContainer.setLayoutManager(new BorderLayout());
		Label desLab = FengGUI.createLabel(labContainer, "Picture: ");
		desLab.setLayoutData(BorderLayoutData.WEST);
		//desLab.setExpandable(false);
		desScroller = FengGUI.createScrollContainer(desContainer);
		desScroller.setLayoutData(BorderLayoutData.CENTER);
		
		
		picture = FengGUI.createLabel(desScroller);
	}

	@Override
	public void close(boolean cancel) {
		if(!cancel){
		
		}
		super.close(cancel);
	}

	@Override
	public void open() {
		super.open();
		MachineSpace machine =StateManager.getMachineSpace();
		if(machine== null)
		{
			picture.setVisible(false);
			return;
		}else{
			picture.setVisible(true);
			ImageInterpreter interp = new ImageInterpreter(machine.getInterpreter().getImage());
			BufferedImage img = interp.getImage();
			if(img!=null)
			{	ITexture texture = Binding.getInstance().getTexture(interp.getImage() );
				Pixmap pixmap = new Pixmap(texture);
				picture.setPixmap(pixmap);
			}else
				picture.setPixmap(null);
		}
	}

}
