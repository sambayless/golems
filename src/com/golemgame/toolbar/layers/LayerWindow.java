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
package com.golemgame.toolbar.layers;

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.composite.Window;

import com.golemgame.mechanical.StructuralMachine;
import com.golemgame.mechanical.layers.Layer;
import com.golemgame.states.StateManager;
import com.golemgame.states.machine.MachineEventListener;

public class LayerWindow{
	private Window window;
	private Container innerContainer;
	private LayerContainer layerContainer;
	public LayerWindow() {
		super();
		window = FengGUI.createWindow(true, false, false, true);
		window.setWidth(400);
		window.setHeight(32);
		window.setTitle("Layers");
		innerContainer = new Container();
		window.getContentContainer().addWidget(innerContainer);
		StateManager.getMachineManager().registerMachineListener(new MachineEventListener()
		{

			public void machineClosed(StructuralMachine machine) {
				if(layerContainer!=null)
					innerContainer.removeWidget(layerContainer);
			}

			public void newMachine(StructuralMachine machine) {
				StateManager.getGame().lock();
				try{
					if(layerContainer!=null)
						innerContainer.removeWidget(layerContainer);
					layerContainer = new LayerContainer(machine.getLayerRepository()){

						@Override
						public void addLayer(Layer layer) {
							super.addLayer(layer);
							window.updateMinSize();
							//window.setHeight(window.getMinHeight());
							window.layout();
						}

						@Override
						public void removeLayer(Layer layer) {
							super.removeLayer(layer);
							window.updateMinSize();
						//	window.setHeight(window.getMinHeight());
							window.layout();
						}
						
					};
					innerContainer.addWidget(layerContainer);
					window.layout();
				}finally{
					StateManager.getGame().unlock();
				}
			}
			
		});
		
		if(StateManager.getStructuralMachine()!=null)
		{
			layerContainer = new LayerContainer(StateManager.getStructuralMachine().getLayerRepository());
			innerContainer.addWidget(layerContainer);
		}
		
	}
	public Window getWindow() {
		return window;
	}
	public void setVisible(boolean value) {
		window.setVisible(value);
	}

	
	
}
