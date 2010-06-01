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
