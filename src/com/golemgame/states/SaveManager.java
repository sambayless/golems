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
package com.golemgame.states;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.Callable;

import com.golemgame.mechanical.MachineSpace;
import com.golemgame.mechanical.StructuralMachine;
import com.golemgame.mvc.GUIDType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.BoxInterpreter;
import com.golemgame.mvc.golems.LayerInterpreter;
import com.golemgame.mvc.golems.LayerRepositoryInterpreter;
import com.golemgame.mvc.golems.MachineInterpreter;
import com.golemgame.mvc.golems.MachineSpaceInterpreter;
import com.golemgame.mvc.golems.io.MVCIO;
import com.golemgame.mvc.golems.io.MVCIO.FailedToLoadException;
import com.golemgame.mvc.golems.validate.GolemsValidator;
import com.golemgame.properties.fengGUI.MessageBox;
import com.golemgame.structural.DesignViewFactory;
import com.golemgame.tool.ActionToolSettings;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionDependencySet;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.UndoManager;
import com.golemgame.tool.action.mvc.AddComponentAction;
import com.golemgame.tool.action.mvc.MergeComponentAction;
import com.golemgame.util.loading.Loadable;
import com.golemgame.util.loading.Loader;
import com.golemgame.util.loading.LoadingExceptionHandler;
import com.golemgame.views.Viewable.ViewMode;
import com.jme.math.Vector3f;

public class SaveManager{

	private final static SaveManager instance = new SaveManager();


	public static SaveManager getInstance() {
		return instance;
	}
	
	public void clearMachine()
	{
		StructuralMachine currentMachine = StateManager.getStructuralMachine();
		StateManager.getDesignState().clearMachine();
		UndoManager.getInstance().clear();//can't undo this...
		StateManager.getMachineManager().closeMachine(currentMachine);
		System.gc();
		
		GeneralSettings.getInstance().getMachineChanged().setValue(false);
	}

	public void newMachine()
	{
		UndoManager.getInstance().clear();//can't undo this...

		
		MachineSpace machineSpace = DesignViewFactory.constructMachineSpace();
		StateManager.setMachineSpace(machineSpace);
	
	//	StructuralMachine newMachine = machineSpace.createMachine();
	//	
		
		
		
		AddComponentAction addComponent;
		try {
			addComponent = (AddComponentAction) machineSpace.getAction(Action.ADD_COMPONENT);
			addComponent.setComponent(new MachineInterpreter().getStore());
			addComponent.doAction();
		} catch (ActionTypeException e1) {
			StateManager.logError(e1);
		}
		GolemsValidator.getInstance().makeValid(machineSpace.getStore());
		StateManager.setStructuralMachine( machineSpace.getMachines().get(0));
		

		try {
			StructuralMachine machine = machineSpace.getMachines().get(0);
			
			LayerRepositoryInterpreter layerReposInterp = new LayerRepositoryInterpreter( machine.getInterpreter().getLayerRepository());
			
			LayerInterpreter layer ;
			
			if (layerReposInterp.getLayers().getValues().isEmpty())
			{
				layer= new LayerInterpreter();
				layer.setLayerName("Layer 1");
				layerReposInterp.addLayer(layer.getStore());
			}else{				
				layer =  new LayerInterpreter((PropertyStore) layerReposInterp.getLayers().getValues().iterator().next());
			}
			
		
			
			BoxInterpreter floor = new BoxInterpreter();
			floor.setStatic(true);
			floor.setExtent(new Vector3f(20,1,20));
			 
	
			
			
			LayerInterpreter groundLayer = new LayerInterpreter();
			groundLayer.setLayerName("Ground");
			layerReposInterp.addLayer(groundLayer.getStore());
			floor.setLayer(groundLayer.getID());
			
			machine.getLayerRepository().refresh();
			
			machine.getLayerRepository().setActiveLayer(machine.getLayerRepository().getLayer(layer.getID()));
			
			addComponent = (AddComponentAction) machine.getAction(Action.ADD_COMPONENT);
			addComponent.setComponent(floor.getStore());
			addComponent.doAction();
		} catch (ActionTypeException e1) {
			StateManager.logError(e1);
		}
	
		
/*		try{
			CreateAction create = (CreateAction) floor.getAction(Action.CREATE);
			create.doAction();

		}catch(ActionTypeException e)
		{
			StateManager.logError(e);
		}
		*/
/*		try{
			ScaleAction scale = (ScaleAction) floor.getAction(Action.SCALE);
			scale.setScale(new Vector3f(20,1,20));
			scale.doAction();

		}catch(ActionTypeException e)
		{
			StateManager.logError(e);
		}*/
		
	
		
		//((Physical)floor).setStatic(true);
		
		
		
		StateManager.getDesignState().setMachineSpace(machineSpace);
		StateManager.getDesignState().registerMachine(StateManager.getStructuralMachine());
		StateManager.getMachineManager().newMachine(StateManager.getStructuralMachine());
		ActionToolSettings.getInstance().getWireMode().setValue(false);
		StateManager.getStructuralMachine().addViewMode(ViewMode.MATERIAL);
		
		GeneralSettings.getInstance().getMachineChanged().setValue(false);
	}
	
	
	
	public static class SaveContainer implements Serializable
	{
		private static final long serialVersionUID = 1L;	
		private final MachineSpace machineSpace;
		private final StructuralMachine primaryMachine;
		public MachineSpace getMachineSpace() {
			return machineSpace;
		}
		public StructuralMachine getPrimaryMachine() {
			return primaryMachine;
		}
		public SaveContainer(MachineSpace machineSpace,
				StructuralMachine primaryMachine) {
			super();
			this.machineSpace = machineSpace;
			this.primaryMachine = primaryMachine;
		}
		
	}
	
	public void save(File file, MachineSpace machineSpace, StructuralMachine primaryMachine) throws IOException 
	{
		StateManager.setMachineScreenshot();
	/*	JFrame t = new JFrame();
		t.add(new Panel()
		{

			@Override
			public void paint(Graphics g) {
				g.drawImage(StateManager.getMachineSpace().getImageView().getImage(), 0, 0, null);
				
			}

			
		});
		t.setVisible(true);
		*/
		
		
		try{
			save(file, machineSpace,primaryMachine,true);
			
		}catch(Exception e)
		{
			MessageBox.showMessageBox("Error", "Failed to save file: " + e.getLocalizedMessage(), GUILayer.getLoadedInstance().getDisplay());
			StateManager.logError(e);
			
			if(file.exists() && file.length() <= 16)
			{//destroy the file, so the user isn't confused later.
				file.delete();
			}
			
		}
		
	}
	
	public void save(File file, MachineSpace machineSpace, StructuralMachine primaryMachine, boolean compress) throws IOException
	{
		MVCIO.save(file, machineSpace.getStore(), compress);
		GeneralSettings.getInstance().getMachineChanged().setValue(false);
	}

	public PropertyStore load(File file) throws FailedToLoadException
	{
		PropertyStore store = MVCIO.load(file);
		GolemsValidator.getInstance().makeValid(store);
		return store;
	}
	
	public void loadAndMergeMachine(final File file)
	{
		Loadable<?> loadable = new Loadable<Object>()
		{

			public void load() throws Exception {
			
				try{

					mergeMachine(SaveManager.this.load(file));
					
				}catch(Error e)
				{
					StateManager.logError(e);
					MessageBox.showMessageBox("Error", "Failed to load file: " + e, GUILayer.getLoadedInstance().getDisplay());
					throw e;
				}
			}

		};
		loadable.setHandler(new LoadingExceptionHandler ()
		{

			public void handleException(Exception e, Loadable<?> l)
					throws Exception {
			
					MessageBox.showMessageBox("Error", "Failed to load file: " + e.getLocalizedMessage(), GUILayer.getLoadedInstance().getDisplay());
					StateManager.logError(e);

			}
			
		});
		Loader.getInstance().setDelayTime(250);
		Loader.getInstance().queueLoadable(loadable);
	}
	

	public void mergeMachine(final PropertyStore machineSpace) {
		try{
			StateManager.getGame().executeInGL(new Callable<Object>()
					{

						
						public Object call() throws Exception {
						
							
							//Collection<Structural> structures = new ArrayList<Structural>(primaryMachine.getStructures());
							//add each applicable layer
							
							MachineSpaceInterpreter interp = new MachineSpaceInterpreter(machineSpace);
							
							
							
							GUIDType guid = interp.getGUID();
							StateManager.getStructuralMachine().getSpace().getInterpreter().recordImport(guid);
							if(interp.getMachines().getValues().isEmpty())
								return null;
							
							PropertyStore machineStore = (PropertyStore) interp.getMachines().getElement(0);
							if(machineStore == null)
								return null;
							
							ActionDependencySet dep = new ActionDependencySet("Import");
							
							MergeComponentAction mergeSpace = (MergeComponentAction) StateManager.getMachineSpace().getAction(Action.MERGE_COMPONENT);
							mergeSpace.setComponent(machineSpace);
							mergeSpace.setDependencySet(dep);
							
							MergeComponentAction merge = (MergeComponentAction) StateManager.getStructuralMachine().getAction(Action.MERGE_COMPONENT);
							merge.setDependencySet(dep);
							merge.setComponent(machineStore);
							
							//Take all these structures, and select them.
							//disabled for now due to problem with multiple select on imported models
							StateManager.getToolManager().forceDeselect();
							if (mergeSpace.doAction())
								UndoManager.getInstance().addAction(merge);
							if (merge.doAction())
								UndoManager.getInstance().addAction(merge);
						/*	
							Collection<Actionable> actionables = new ArrayList<Actionable>();
							for (Structural structure:structures)
							{
								if (structure != null && structure.getActionable() != null  &! structure.getModel().isDeleted() &! (structure.getModel().getParent() == null))
									actionables.add(structure.getActionable());
							}
							
							StateManager.getToolManager().selectActionables(actionables);*/
							StateManager.getStructuralMachine().clearViews();
							
							ActionToolSettings.getInstance().getWireMode().setValue(false);
							StateManager.getStructuralMachine().addViewMode(ViewMode.MATERIAL);
							GeneralSettings.getInstance().getMachineChanged().setValue(true);
						return null;
					}
					
				});
				}catch(Exception e)
				{
					StateManager.logError(e);
				}
		
	}
	
	private SaveContainer loadMachineSpaceContainer(final File file) throws FailedToLoadException
	{
		
		MachineSpace machineSpace = new MachineSpace(new MachineSpaceInterpreter((PropertyStore)load(file)));
		machineSpace.refresh();
		SaveContainer container = new SaveContainer(machineSpace,machineSpace.getMachines().get(0));
		return container;
	
	
	}
	
	
	public void loadAndSetMachine(final File file)
	{
		Loadable<?> loadable = new Loadable<Object>()
		{

			
			public void load() throws Exception {
			
				try{
						SaveManager.getInstance().clearMachine();
						SaveContainer container = loadMachineSpaceContainer(file);
						setMachineSpace(container);
						UndoManager.getInstance().clear();//can't undo this...
					//	System.out.println(container.getPrimaryMachine().getStructures().size() + "\t" + container.getPrimaryMachine().getViewables().size());
						GeneralSettings.getInstance().getMachineChanged().setValue(false);
				
				}catch(Error e)
				{
					StateManager.logError(e);
					MessageBox.showMessageBox("Error", "Failed to load file: " + e, GUILayer.getLoadedInstance().getDisplay());
					throw e;
				}
			}

		};
		loadable.setHandler(new LoadingExceptionHandler ()
		{

			
			public void handleException(Exception e, Loadable<?> l)
					throws Exception {
				StateManager.logError(e);
					MessageBox.showMessageBox("Error", "Failed to load file: " + e.getLocalizedMessage(), GUILayer.getLoadedInstance().getDisplay());
				
				
				
			}
			
		});
		Loader.getInstance().setDelayTime(250);
		Loader.getInstance().queueLoadable(loadable);
		
		
	}

	public void setMachineSpace(final MachineSpace space, final StructuralMachine primaryMachine)
	{
		try{
			StateManager.getGame().executeInGL(new Callable<Object>()
					{

						
						public Object call() throws Exception {
							//space.clearDeletedElements();
							StateManager.setMachineSpace(space);
							
							StateManager.setStructuralMachine(primaryMachine);

							StateManager.getMachineManager().newMachine(StateManager.getStructuralMachine());
							StateManager.getDesignState().setMachineSpace(space);
							
							StateManager.getDesignState().registerMachine(primaryMachine);
							
							space.refresh();
							ActionToolSettings.getInstance().getWireMode().setValue(false);
							//StateManager.getStructuralMachine().removeViewMode(ViewMode.FUNCTIONAL);
						//	StateManager.getStructuralMachine().addViewMode(ViewMode.FUNCTIONAL);//this is temporary, to force the machine to init view mode.
							StateManager.getStructuralMachine().addViewMode(ViewMode.MATERIAL);
							
							StateManager.getStructuralMachine().refreshView();
						//	StateManager.getMachineSpace().update();
							//StateManager.getStructuralMachine().getStructuralManager().resolveAll();
							return null;
						}
						
					});
			}catch(Exception e)
			{
				StateManager.logError(e);
			}
	}
	
	public void setMachineSpace(final SaveContainer container)
	{
		this.setMachineSpace(container.getMachineSpace(), container.getPrimaryMachine());
	}
	
	

}
