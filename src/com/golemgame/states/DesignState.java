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



import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.fenggui.event.IWindowClosedListener;
import org.fenggui.event.WindowClosedEvent;

import com.golemgame.constructor.UpdateManager;
import com.golemgame.local.StringConstants;
import com.golemgame.mechanical.MachineSpace;
import com.golemgame.mechanical.StructuralMachine;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.physical.EnvironmentListener;
import com.golemgame.physical.PhysicsEnvironment;
import com.golemgame.platform.PlatformManager;
import com.golemgame.properties.fengGUI.MessageBox;
import com.golemgame.settings.SettingChangedEvent;
import com.golemgame.settings.SettingsListener;
import com.golemgame.states.camera.CameraDelegate;
import com.golemgame.states.camera.OrderedCameraList;
import com.golemgame.tool.ActionToolSettings;
import com.golemgame.tool.ToolSelectionEffectManager;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.CycleAction;
import com.golemgame.tool.action.UndoManager;
import com.golemgame.tool.action.CycleAction.Direction;
import com.golemgame.toolbar.ButtonAdapter;
import com.golemgame.toolbar.ButtonGroup;
import com.golemgame.toolbar.StandardButton;
import com.golemgame.toolbar.StandardToolbar;
import com.golemgame.toolbar.layers.LayerWindow;
import com.golemgame.toolbar.main.MainToolbar;
import com.golemgame.toolbar.option.OptionButton;
import com.golemgame.toolbar.recording.RecordingButtons;
import com.golemgame.toolbar.tooltip.IconToolTip;
import com.golemgame.toolbar.tooltip.ToolTipData;
import com.golemgame.util.PaintableImage;
import com.golemgame.util.input.InputLayer;
import com.golemgame.util.input.ListenerGroup;
import com.golemgame.util.loading.ConcurrentLoadable;
import com.golemgame.util.loading.Loadable;
import com.golemgame.util.loading.LoadableSequence;
import com.golemgame.util.loading.Loader;
import com.golemgame.util.loading.LoadingExceptionHandler;
import com.golemgame.views.ObservableViewManager.ViewListener;
import com.golemgame.views.Viewable.ViewMode;
import com.jme.bounding.CollisionTree;
import com.jme.bounding.CollisionTreeManager;
import com.jme.image.Texture;
import com.jme.input.KeyInput;
import com.jme.input.KeyInputListener;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.game.state.BasicGameStateNode;
import com.simplemonkey.Container;
import com.simplemonkey.layout.BorderLayout;
import com.simplemonkey.layout.BorderLayoutData;
import com.simplemonkey.layout.RowLayout;
import com.simplemonkey.tooltip.ToolTipWidget;
import com.simplemonkey.widgets.SimpleButton;
import com.simplemonkey.widgets.SimpleButtonListener;
import com.simplemonkey.widgets.TextWidget;
import com.simplemonkey.widgets.ToggleButton;
import com.simplemonkey.widgets.ToggleListener;


//update structure tools (and schema)
//update machine node for phsyics creation

public class DesignState extends BasicGameStateNode implements Controllable, ConcurrentLoadable
{
	private Node hudNode;
	private Renderer renderer;


	private MainToolbar toolbar;
	private StandardToolbar mainInterface;
	private StandardToolbar viewModeToolbar;
	//private Camera camera;
	private OrderedCameraList cameraSet;
	
	
	private ListenerGroup listeners;
	
	//private Spatial[] toRecord = new Spatial[2];

	private NodeModel rootModel;
	private StandardToolbar groupModeToolbar;
	private LayerWindow layerWindow;
	
	public DesignState() {
		super("design");

		this.setActive(false);
		this.cameraSet = new OrderedCameraList();
		try{
		initState();	
		}catch(Exception e)
		{
			StateManager.logError(e);
		}
		listeners = new ListenerGroup();
		listeners.addKeyListener(undoListen);

		rootModel = new NodeModel();
		this.getRootNode().attachChild(rootModel.getSpatial());


	}
	
	

	private Node buildHudNode()
	{
		
		Node hudNode = new Node();
		hudNode.setZOrder(-1);
		
		final IconToolTip tooltip = new IconToolTip();
		
		GeneralSettings.getInstance().getShowToolTips().addSettingsListener(new SettingsListener<Boolean>()
				{

					public void valueChanged(SettingChangedEvent<Boolean> e) {
						tooltip.setEnabled(e.getNewValue());
						
					}
					
				},true);
		
		StateManager.getLogger().fine("Design state building main toolbar");
		toolbar = new MainToolbar(tooltip);
		
		
		StateManager.getLogger().fine("Design state building main interface");
		mainInterface = new StandardToolbar("Main Interface");
	
		tooltip.getSpatial().updateRenderState();
	
		//viewModeToolbar = buildViewModeToolbar(hudNode);//not building this right now.//to add it back, dont forget to uncomment this corresponding code in setListening()
		
		GeneralSettings.getInstance().getResolution().addSettingsListener(new SettingsListener<Dimension>()
				{
					
					public void valueChanged(SettingChangedEvent<Dimension> e) {
							mainInterface.setSize(e.getNewValue().width, e.getNewValue().height);
							mainInterface.layout();
					}
				},true);

		hudNode.attachChild(toolbar.getSpatial());//NOTE: In order for tooltips to be drawn properly ontop of the main menu, they have to be rendered last, which means the main interface (that they are attached to) must be attached to the HUD node after the main toolbar.
		hudNode.attachChild(mainInterface.getSpatial());
		StateManager.getLogger().fine("Design state building main menu");
		
		buildMainMenu(hudNode,tooltip);
		
		StateManager.getLogger().fine("Design state building status bar");
		buildStatusBar(hudNode,tooltip);
		
		StateManager.getLogger().fine("Design state setting up hud root");
	
		//have to add the tooltip last to render properly over top of other components... this is a minor hack.
		mainInterface.addWidget(tooltip);
		
		ZBufferState zbs = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
		zbs.setEnabled(true);
		zbs.setFunction( ZBufferState.CF_ALWAYS  );//apparently, using this or CF_ALWAYS will cause the hud to be drawn ontop of everything, properly
		zbs.setWritable(false);
		
		hudNode.setLightCombineMode(LightState.OFF);
		
		  CullState cs = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
	        cs.setCullMode(CullState.CS_BACK);
	        hudNode.setRenderState(cs);

		hudNode.setRenderState(zbs);
		hudNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		
        //hudNode.updateGeometricState(0, true);
		hudNode.updateRenderState();
	
		return hudNode;
	}
	
	
	
	
	/*private StandardToolbar buildViewModeToolbar(Node hudNode) {
		final StandardToolbar viewModeToolbar = new StandardToolbar("View Mode Toolbar");
		
		viewModeToolbar.setLayoutManager(new RowLayout());
		
		hudNode.attachChild(viewModeToolbar.getSpatial());
		
		{
			MaterialState white;
			white = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
			white.setSpecular(new ColorRGBA(1f,1f,0.2f,1));
			white.setAmbient(new ColorRGBA(1f,1f,1f,1f));
			white.setDiffuse(new ColorRGBA(1f,1f,1,1f));		
			white.setShininess(100);
			white.setColorMaterial(MaterialState.CM_NONE );
			white.setMaterialFace(MaterialState.MF_FRONT_AND_BACK);
			
			AlphaState semiTransparentAlpha = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
	
			// Blend between the source and destination functions.
			semiTransparentAlpha.setBlendEnabled(true);
			// Set the source function setting.
			semiTransparentAlpha.setSrcFunction(AlphaState.SB_SRC_ALPHA);
			// Set the destination function setting.
			semiTransparentAlpha.setDstFunction(AlphaState.SB_ONE_MINUS_SRC_ALPHA);
			// Enable the test.
			semiTransparentAlpha.setTestEnabled(true);		
			// Set the test function to TF_GREATER.
			semiTransparentAlpha.setTestFunction(AlphaState.TF_GREATER);		
			
			
			viewModeToolbar.getSpatial().setRenderState(semiTransparentAlpha);
			viewModeToolbar.getSpatial().setRenderState(white);
			viewModeToolbar.getSpatial().updateRenderState();
		}
		
	
		//ImageGraphics graphics = ImageGraphics.createInstance(32,32,0);
		//for now, just draw a gradient
		//GradientPaint gradient = new GradientPaint(0, 0, new Color(100,100,100), 0, 32, new Color(110,120,110));
		//graphics.setPaint(gradient);
	
		int width = 32;
		int height = 32;
		
		PaintableImage image = new PaintableImage(width, height, true)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics2D g) {
				GradientPaint gradient = new GradientPaint(0, height, new Color(186,186,186,190), 0, 0, new Color(39,39,39,163));
				g.setPaint(gradient);
				g.fillRect(0, 0, width, height);
				
			}
			
		};
		
		PaintableImage hoverImage = new PaintableImage(width, height, true)
		{

	
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics2D g) {
				//GradientPaint gradient = new GradientPaint(0, height, new Color(44,133,222,237), 0, 0, new Color(39,39,39,163));
				GradientPaint gradient = new GradientPaint(0, height, new Color(34,215,56,237), 0, 0, new Color(10,65,10,163));

			//	GradientPaint gradient = new GradientPaint(0, height, new Color(186,186,186,237), 0, 0, new Color(39,39,39,163));
				g.setPaint(gradient);
				g.fillRect(0, 0, width, height);
				
			}
			
		};
		
		PaintableImage activeImage = new PaintableImage(width, height, true)
		{


			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics2D g) {
				GradientPaint gradient = new GradientPaint(0, height, new Color(44,133,222,237), 0, 0, new Color(10,37,65,163));

			//	GradientPaint gradient = new GradientPaint(0, height, new Color(186,186,186,237), 0, 0, new Color(39,39,39,163));
				g.setPaint(gradient);
				g.fillRect(0, 0, width, height);
				
			}
			
		};
		
		Texture normal = new Texture();
		
		normal.setApply(Texture.AM_MODULATE);
		normal.setImage(image);
		
		Texture hover = new Texture();
		
		hover.setApply(Texture.AM_MODULATE);
		hover.setImage(hoverImage);
		
		Texture active = new Texture();
		
		active.setApply(Texture.AM_MODULATE);
		active.setImage(activeImage);
		
		
		Texture wireModeTexture =loadTexture("buttons/tools/Wire.png");	
		wireModeTexture.setApply(Texture.AM_DECAL);
			
		Texture materialModeTexture =loadTexture("buttons/polyhedrons/Cube.png");	
		materialModeTexture.setApply(Texture.AM_DECAL);
		
		Texture groupModeTexture =loadTexture("buttons/tools/SelectTwo.png");	
		groupModeTexture.setApply(Texture.AM_DECAL);
		
		Texture sensorModeTexture =loadTexture("buttons/polyhedrons/Pyramid.png");	
		sensorModeTexture.setApply(Texture.AM_DECAL);
		
	final	ToggleButton materialView = new ToggleButton("Show Materials", active,normal,hover,0);
	final	ToggleButton wireView = new ToggleButton("Show Wires", active,normal,hover,0);
	final	ToggleButton sensorView = new ToggleButton("Show Sensors", active,normal,hover,0);
	final	ToggleButton groupView = new ToggleButton("Show Groups", active,normal,hover,0);
	
		materialView.setTexture(normal, 0);
		materialView.setTexture(materialModeTexture, 1);
		materialView.setSize(width, height);
		materialView.setShrinkable(false);
		viewModeToolbar.addWidget(materialView);
		
		wireView.setTexture(normal, 0);
		wireView.setTexture(wireModeTexture, 1);
		wireView.setSize(width, height);
		wireView.setShrinkable(false);
		viewModeToolbar.addWidget(wireView);
		
		sensorView.setTexture(normal, 0);
		sensorView.setTexture(sensorModeTexture, 1);
		sensorView.setSize(width, height);
		sensorView.setShrinkable(false);
		viewModeToolbar.addWidget(sensorView);
		
		groupView.setTexture(normal, 0);
		groupView.setTexture(groupModeTexture, 1);
		groupView.setSize(width, height);
		groupView.setShrinkable(false);
		viewModeToolbar.addWidget(groupView);
		
		viewModeToolbar.setHeight(height);
		viewModeToolbar.getSpatial().updateRenderState();
		
		viewModeToolbar.layout();
		viewModeToolbar.updateMinSize();
		
		GeneralSettings.getInstance().getResolution().addSettingsListener(new SettingsListener<Dimension>()
				{

					public void valueChanged(SettingChangedEvent<Dimension> e) {
						viewModeToolbar.getSpatial().getLocalTranslation().set(e.getNewValue().width/2f - viewModeToolbar.getWidth()/2f,e.getNewValue().height -viewModeToolbar.getHeight(),0);
						viewModeToolbar.getSpatial().updateWorldVectors();
	    				
						viewModeToolbar.getSpatial().updateGeometricState(0, true);
					}
				
				}

				,true);
		
		materialView.addToggleListener(new ToggleListener()
		{

			public void toggle(boolean value) {
				if (value)
				{
					StateManager.getViewManager().addViewMode(ViewMode.MATERIAL);
					StateManager.getViewManager().removeViewMode(ViewMode.FUNCTIONAL);
				}
				else
				{
					StateManager.getViewManager().setViewMode(ViewMode.FUNCTIONAL);
					StateManager.getViewManager().removeViewMode(ViewMode.MATERIAL);
				}
			}
			
		});
		sensorView.addToggleListener(new ToggleListener()
		{

			public void toggle(boolean value) {
				if (value)
					StateManager.getViewManager().addViewMode(ViewMode.GHOST);
				else
					StateManager.getViewManager().removeViewMode(ViewMode.GHOST);
			}
			
		});
		wireView.addToggleListener(new ToggleListener()
		{

			public void toggle(boolean value) {
				if (value)
				{
					StateManager.getViewManager().addViewMode(ViewMode.FUNCTIONAL);
					StateManager.getViewManager().removeViewMode(ViewMode.MATERIAL);
				}
				else
				{
					StateManager.getViewManager().setViewMode(ViewMode.MATERIAL);
					StateManager.getViewManager().removeViewMode(ViewMode.FUNCTIONAL);
				}
			}
			
		});
		groupView.addToggleListener(new ToggleListener()
		{

			public void toggle(boolean value) {
				if(value)
					ActionToolSettings.getInstance().getAssignedGroupSelectionMode().setValue(true);
				//	StateManager.getViewManager().addViewMode(ViewMode.GROUPMODE);
				else
					ActionToolSettings.getInstance().getAssignedGroupSelectionMode().setValue(false);
				//	StateManager.getViewManager().removeViewMode(ViewMode.GROUPMODE);
			}
			
		});
		
		StateManager.getViewManager().registerListener(new ViewListener()
		{

			public void viewChanged(Collection<ViewMode> views) {
				
				materialView.setValue (views.contains(ViewMode.MATERIAL));
				groupView.setValue (views.contains(ViewMode.GROUPMODE));
				sensorView.setValue (views.contains(ViewMode.GHOST));
				wireView.setValue (views.contains(ViewMode.FUNCTIONAL));
			}
			
		});
		
		return viewModeToolbar;
	}*/
	
	private Texture loadTexture(String path)
	{
	 	
		return  TextureManager.loadTexture(
	  			MainToolbar.class.getClassLoader().getResource(
	  		            "com/golemgame/data/textures/" + path),
		            Texture.MM_LINEAR_NEAREST,
		            Texture.FM_LINEAR);
	}


	private void buildMainMenu(final Node hudNode,ToolTipWidget toolTip)
	{
		//Problem: buttonGroups do not automatically take on any particular position
		//this means that if you dont position them manually, then even if their sub-buttons are positions
		//collision detection will fail for the button group if there are multiple of them.
		
		Container topBar = new Container();
		mainInterface.addWidget(topBar);
		mainInterface.setLayoutManager(new BorderLayout());
		
		topBar.setLayoutData(BorderLayoutData.NORTH);
		topBar.setLayoutManager(new BorderLayout());
		Container topRight = new Container();
		topBar.addWidget(topRight);
		topRight.setLayoutData(BorderLayoutData.EAST);
		topRight.setLayoutManager(new BorderLayout());
		
		Container recordContainer = new Container();
		recordContainer.setLayoutManager(new BorderLayout());
		recordContainer.setLayoutData(BorderLayoutData.CENTER);
		topRight.addWidget(recordContainer);
		
		RecordingButtons recordingControls = new RecordingButtons();
		recordingControls.setLayoutData(BorderLayoutData.NORTH);
		recordContainer.addWidget(recordingControls);
		
		
		
		ButtonGroup mainMenuGroup = new ButtonGroup();
	  	Texture runInactive = TextureManager.loadTexture(
	  			getClass().getClassLoader().getResource(
	            "com/golemgame/data/textures/buttons/solid/Solid_Green.png"),
	            Texture.MM_LINEAR_NEAREST,
	            Texture.FM_LINEAR);
	  	



	  	Texture runActive = TextureManager.loadTexture(
	  			getClass().getClassLoader().getResource(
	            "com/golemgame/data/textures/buttons/glass/Crystal.png"),
	            Texture.MM_LINEAR_NEAREST,
	            Texture.FM_LINEAR);
	  	
	  	Texture runHover = TextureManager.loadTexture(
	            getClass().getClassLoader().getResource(
	            "com/golemgame/data/textures/buttons/glass/Glass_Green.png"),
	            Texture.MM_LINEAR_NEAREST,
	            Texture.FM_LINEAR);
	  	
	  	Texture runIcon = TextureManager.loadTexture(
	            getClass().getClassLoader().getResource(
	            "com/golemgame/data/textures/buttons/tools/Play.png"),
	            Texture.MM_LINEAR_NEAREST,
	            Texture.FM_LINEAR);
	  	
	  	
	 // 	StateManager.getLogger().fine("Main menu building run button");
		final StandardButton runButton = new StandardButton(32, 32, runActive, runInactive, runHover,runIcon);
	//	StateManager.getLogger().fine("Main menu setting tool tips");
		runButton.setTooltipWidget(toolTip);
		IconToolTip.setTooltipData(runButton,new ToolTipData(runIcon,runHover,""));
	//	StateManager.getLogger().fine("Main menu settings descriptions");
		IconToolTip.setDescription(runButton,StringConstants.get("STATUSBAR.ENGAGE","Engage physics!"));
	//	StateManager.getLogger().fine("Main menu setting up root node");
		{
			MaterialState white;
			white = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
			white.setSpecular(new ColorRGBA(1f,1f,0.2f,1));
			white.setAmbient(new ColorRGBA(1f,1f,1f,1f));
			white.setDiffuse(new ColorRGBA(1f,1f,1,1f));		
			white.setShininess(100);
			white.setColorMaterial(MaterialState.CM_NONE );
			white.setMaterialFace(MaterialState.MF_FRONT_AND_BACK);
			
			AlphaState semiTransparentAlpha = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
	
			// Blend between the source and destination functions.
			semiTransparentAlpha.setBlendEnabled(true);
			// Set the source function setting.
			semiTransparentAlpha.setSrcFunction(AlphaState.SB_SRC_ALPHA);
			// Set the destination function setting.
			semiTransparentAlpha.setDstFunction(AlphaState.SB_ONE_MINUS_SRC_ALPHA);
			// Enable the test.
			semiTransparentAlpha.setTestEnabled(true);		
			// Set the test function to TF_GREATER.
			semiTransparentAlpha.setTestFunction(AlphaState.TF_GREATER);		
			runButton.getSpatial().setRenderState(white);
			runButton.getSpatial().setRenderState(semiTransparentAlpha);
		
			runButton.getSpatial().updateRenderState();
		}
		GeneralSettings.getInstance().getResolution().addSettingsListener(new SettingsListener<Dimension>()
	    		{

	    			
	    			public void valueChanged(SettingChangedEvent<Dimension> e) {
	    				runButton.getSpatial().getLocalTranslation().set(e.getNewValue().width-runButton.getWidth()/2f,e.getNewValue().height - runButton.getHeight()/2f,0);
	    				runButton.getSpatial().updateWorldVectors();
	    				
	    				hudNode.updateGeometricState(0, true);
	    			}

	    		}, true);
		
		
		runButton.getSpatial().updateWorldVectors();
		mainMenuGroup.addWidget(runButton);
		topRight.addWidget(mainMenuGroup);
		//mainInterface.addWidget(mainMenuGroup);
		mainMenuGroup.setLayoutData(BorderLayoutData.EAST);
		runButton.setLayoutData(BorderLayoutData.EAST);
		/*mainInterface.setLayoutManager(new BorderLayout());*/
		mainMenuGroup.setLayoutManager(new BorderLayout());

		mainInterface.layout();

		//mainMenuGroup.layout();
		runButton.addButtonListener(new ButtonAdapter()
		{

			
			public void activate() 
			{
			
				 StateManager.getToolManager().forceDeselect();
				Loader.getInstance().queueLoadable(new Loadable()
				{
					
					public void load() throws Exception {
						final File file = new File(System.getProperty("user.home") + "/Machines/_autosave.mchn");
						SaveManager.getInstance().save(file, StateManager.getMachineSpace(), StateManager.getStructuralMachine());
					
					}
				});
				
				LoadableSequence sequence = new LoadableSequence(false);
				sequence.setHandler(new LoadingExceptionHandler()
				{

					
					public void handleException(Exception e, Loadable<?> l)
							throws Exception {
						StateManager.logError(e);
						MessageBox.showMessageBox("Error", "Failed to load file: " + e.getLocalizedMessage(), GUILayer.getLoadedInstance().getDisplay());
					}
					
				});
				sequence.queueLoadable(new Loadable()
				{
					
					public void load() throws Exception {
							remove();
					}
				});
				
				sequence.queueLoadable(new Loadable()
				{
					
					public void load() throws Exception {
						System.gc();
				
						StateManager.getFunctionalState().waitForLoad();
						super.setProgress(0.1f);
						StateManager.getGame().executeInGL(new Callable<Object>()
								{

									
									public Object call() throws Exception{
										setProgress(0.1f);
										StateManager.getGame().lock();
										try{	
											StateManager.setCurrentGameState(StateManager.getFunctionalState());
											StateManager.getFunctionalState().init();
											
											StateManager.getFunctionalState().getController().cleanup();
											setProgress(0.4f);

											StateManager.getFunctionalState().getController().preparePhysics();
											setProgress(0.6f);
						
										}finally
										{
											StateManager.getGame().unlock();
										}
			
										return null;
									}
							
								});
						
						}
				});
	
				sequence.queueLoadable(new Loadable()
				{
					
					public void load() throws Exception {
				
						StateManager.getFunctionalState().getController().load(StateManager.getMachineSpace().getStore(),this);
					
					}
				});
				

				sequence.queueLoadable(new Loadable()
				{
					
					public void load() throws Exception {
						StateManager.getFunctionalState().getController().start();
					}
				});
				Loader.getInstance().setDelayTime(250);
				Loader.getInstance().queueLoadable(sequence);
			}
			
		});
		
	}
	

	
	private void buildStatusBar(Node hudNode,ToolTipWidget tooltip)
	{
		
		
		Container southGroup = new Container();
		southGroup.setLayoutManager(new BorderLayout());
		southGroup.setLayoutData(BorderLayoutData.SOUTH);
				
		final Container statusGroup = new Container();
		
		statusGroup.setLayoutData(BorderLayoutData.EAST);

		southGroup.addWidget(statusGroup);
		
		statusGroup.setLayoutManager(new RowLayout());
		final OptionButton snapCheck = new OptionButton(StringConstants.get("STATUSBAR.SNAP" ,"Snap"));
		snapCheck.setTooltipWidget(tooltip);
		IconToolTip.setTooltipData(snapCheck,new ToolTipData(StringConstants.get("STATUSBAR.SNAP.LONG","Snap to Grid")));
		IconToolTip.setDescription(snapCheck,StringConstants.get("STATUSBAR.SNAP.TOOLTIP","Select this to make things snap to the grid.\nYou can change the grid size in the menu."));
		
		snapCheck.addButtonListener(new ButtonAdapter()
		{

			
			public void activate() {
				ActionToolSettings.getInstance().getSnap().setValue(snapCheck.isValue());
			}			
		});
		
		ActionToolSettings.getInstance().getSnap().addSettingsListener(new SettingsListener<Boolean>()
		{
			
			public void valueChanged(SettingChangedEvent<Boolean> e) {
				snapCheck.setValue(e.getNewValue());					
			}			
		},true);

		final OptionButton restrictAxis =  new OptionButton(StringConstants.get("STATUSBAR.AXIS","Axis"));
		restrictAxis.setTooltipWidget(tooltip);
		IconToolTip.setTooltipData(restrictAxis,new ToolTipData(StringConstants.get("STATUSBAR.AXIS.LONG","Move on Axis")));
		IconToolTip.setDescription(restrictAxis,StringConstants.get("STATUSBAR.AXIS.TOOLTIP","Only move things along one axis at a time."));
		
		
		restrictAxis.addButtonListener(new ButtonAdapter()
		{

			
			public void activate() {
				ActionToolSettings.getInstance().getUseSnapSingleAxis().setValue(restrictAxis.isValue());
			}			
		});
		
		ActionToolSettings.getInstance().getUseSnapSingleAxis().addSettingsListener(new SettingsListener<Boolean>()
				{
					
					public void valueChanged(SettingChangedEvent<Boolean> e) {
						restrictAxis.setValue(e.getNewValue());					
					}			
				},true);
		


		
		final OptionButton showSensors =  new OptionButton(StringConstants.get("STATUSBAR.SENSORS","Sensors"));
		showSensors.setTooltipWidget(tooltip);
		Texture sensorTexture = loadTexture("buttons/functionals/Distance.png");
		sensorTexture.setApply(Texture.AM_MODULATE);
		IconToolTip.setTooltipData(showSensors,new ToolTipData(sensorTexture,StringConstants.get("STATUSBAR.SENSORS.LONG","Show Sensors")));
		IconToolTip.setDescription(showSensors,StringConstants.get("STATUSBAR.SENSORS.TOOLTIP","Show all the sensor fields in your machine."));
		
		
		showSensors.addButtonListener(new ButtonAdapter()
		{
			public void activate() {
				if(showSensors.isValue())
				{
					StateManager.getViewManager().addViewMode(ViewMode.GHOST);
				}else
				{
					StateManager.getViewManager().removeViewMode(ViewMode.GHOST);
				}
			}			
		});
		
		StateManager.getViewManager().registerListener(new ViewListener()
		{

			public void viewChanged(Collection<ViewMode> views) {
				if (views.contains(ViewMode.GHOST))
				{
					showSensors.setValue(true);
				}else
					showSensors.setValue(false);
			}
			
		});
		
	
		final OptionButton staticSelectable =  new OptionButton(StringConstants.get("STATUSBAR.STATIC","Static"));
		staticSelectable.setTooltipWidget(tooltip);
		IconToolTip.setTooltipData(staticSelectable,new ToolTipData(StringConstants.get("STATUSBAR.STATIC.LONG","Unlock Static Objects")));
		IconToolTip.setDescription(staticSelectable,StringConstants.get("STATUSBAR.STATIC.TOOLTIP","By default, static objects (like the floor) are\nlocked so you can't select them."));
		
		final OptionButton layers =  new OptionButton(StringConstants.get("STATUSBAR.LAYERS","Layers"));
		layers.setTooltipWidget(tooltip);
		IconToolTip.setTooltipData(layers,new ToolTipData(StringConstants.get("STATUSBAR.LAYERS.LONG","Show Layers Window")));
		IconToolTip.setDescription(layers,StringConstants.get("STATUSBAR.LAYERS.TOOLTIP","Layers let you organize the components of your machine."));
		
		layers.addButtonListener(new ButtonAdapter()
		{

			
			public void activate() {
				//layerWindow.setVisible(layers.isValue());
				//layerWindow.getWindow().close()
				GeneralSettings.getInstance().getShowLayers().setValue(layers.isValue());
			}			
		});
		
		GeneralSettings.getInstance().getShowLayers().addSettingsListener(new SettingsListener<Boolean>(){

			public void valueChanged(SettingChangedEvent<Boolean> e) {
				if(DesignState.this.isListening.get())
				{
					layers.setValue(e.getNewValue());
					StateManager.getGame().lock();
					try{
						layerWindow.getWindow().close();
						if(layers.isValue())
						{
							GUILayer.getInstance().getDisplay().addWidget(layerWindow.getWindow());
						}
					}finally{
						StateManager.getGame().unlock();
					}
				}
			}
			
		},true);
		
		//Not currently used
		final TextWidget message = new TextWidget("Welcome");
		
		statusGroup.addWidget(message);
		statusGroup.addWidget(layers);
		statusGroup.addWidget(showSensors);
		//statusGroup.addWidget(showGroups);
		statusGroup.addWidget(restrictAxis);
		statusGroup.addWidget(snapCheck);
		
		statusGroup.addWidget(staticSelectable);
	
		mainInterface.addWidget(southGroup);
		mainInterface.layout();
		staticSelectable.addButtonListener(new ButtonAdapter()
		{
			public void activate() {
				ActionToolSettings.getInstance().getStaticsSelectable().setValue(staticSelectable.isValue());
			}			
		});
		
		ActionToolSettings.getInstance().getStaticsSelectable().addSettingsListener(new SettingsListener<Boolean>()
		{
			
			public void valueChanged(SettingChangedEvent<Boolean> e) {
				staticSelectable.setValue(e.getNewValue());					
			}			
		},true);
	
		message.getSpatial().setCullMode(SceneElement.CULL_ALWAYS);
		message.setExpandable(false);
		message.setShrinkable(false);
		message.setSizeToMinSize();
		//might put this back sometime in the future.
		/*TooltipDisplayer displayer = new TooltipDisplayer()
		{
			private TooltipUpdate updatable = new TooltipUpdate();
			
			public void displayTooltip(String sTooltip) {
				if(sTooltip != "" && sTooltip != null)
				{
					tooltip.setText(sTooltip);
					tooltip.setSizeToMinSize();
					
					statusGroup.updateMinSize();
						
					mainInterface.layout();
			
					UpdateManager.getInstance().add(updatable);
				}
			}
			
			class TooltipUpdate extends UpdatableAdapter
			{
				private float time = 0;			
				private final float startTime = 0.25f;
		
				private final float closeTime = 2f;
				private final float closeLength = 0.5f;
				
				public void reset() {
					time = 0;
				}

				
				public void update(float time) throws RemoveUpdateException {
					if (this.time<closeTime)
					{
						tooltip.getSpatial().setCullMode(SceneElement.CULL_NEVER);
					}else if (this.time> closeTime)
					{
						tooltip.getSpatial().setCullMode(SceneElement.CULL_ALWAYS);
						reset();
						throw new RemoveUpdateException();
					}
					
					
					this.time+= time;
				}
				
			}
			
		};
		toolbar.setTooltipDisplayer(displayer);*/
		
		{
			
			MaterialState white;
			white = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();

			white.setColorMaterial(MaterialState.CM_NONE );
			white.setMaterialFace(MaterialState.MF_FRONT);
			
			AlphaState semiTransparentAlpha = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
	
			semiTransparentAlpha.setBlendEnabled(true);
			semiTransparentAlpha.setSrcFunction(AlphaState.SB_SRC_ALPHA);
			semiTransparentAlpha.setDstFunction(AlphaState.SB_ONE_MINUS_SRC_ALPHA);
			semiTransparentAlpha.setTestEnabled(true);		
			semiTransparentAlpha.setTestFunction(AlphaState.TF_GREATER);		
			
			restrictAxis.getSpatial().setRenderState(white);
			snapCheck.getSpatial().setRenderState(white);
			staticSelectable.getSpatial().setRenderState(white);
			message.getSpatial().setRenderState(white);
			showSensors.getSpatial().setRenderState(white);
			//showGroups.getSpatial().setRenderState(white);
			layers.getSpatial().setRenderState(white);
			
			
			restrictAxis.getSpatial().setRenderState(semiTransparentAlpha);
			staticSelectable.getSpatial().setRenderState(semiTransparentAlpha);
			staticSelectable.getSpatial().setRenderState(semiTransparentAlpha);
			showSensors.getSpatial().setRenderState(semiTransparentAlpha);
			//showGroups.getSpatial().setRenderState(semiTransparentAlpha);
			message.getSpatial().setRenderState(semiTransparentAlpha);
			layers.getSpatial().setRenderState(semiTransparentAlpha);
			
			
			Texture backgroundStripes = TextureManager.loadTexture(getClass().getClassLoader().getResource("com/golemgame/data/textures/menu/semi.png"),Texture.MM_LINEAR_NEAREST,Texture.MM_LINEAR);
			Texture backgroundStripes2 = backgroundStripes.createSimpleClone();
			Texture backgroundStripes3 = backgroundStripes.createSimpleClone();
			//Texture backgroundStripes4 = backgroundStripes.createSimpleClone();
			Texture backgroundStripes5 = backgroundStripes.createSimpleClone();
			Texture backgroundStripes6 = backgroundStripes.createSimpleClone();
			
			restrictAxis.setTexture(backgroundStripes,0);
			snapCheck.setTexture(backgroundStripes,0);
			staticSelectable.setTexture(backgroundStripes2,0);
		//	showGroups.setTexture(backgroundStripes4,0);
			showSensors.setTexture(backgroundStripes5,0);
			message.setTexture(backgroundStripes3,0);
			layers.setTexture(backgroundStripes6, 0);
			
			//tooltip.getTextTexture().setApply(Texture.AM_DECAL);
		//	tooltip.setTexture(tooltip.getTextTexture(), 1);
			restrictAxis.getSpatial().updateRenderState();
			snapCheck.getSpatial().updateRenderState();
			staticSelectable.getSpatial().updateRenderState();
			showSensors.getSpatial().updateRenderState();
		//	showGroups.getSpatial().updateRenderState();
			layers.getSpatial().updateRenderState();
			
			message.getSpatial().updateRenderState();
			
		}
		
	}

	

	
	public void cleanup() {

	}

	
	
	public void render(float tpf) {
		//super.render(arg0);
		UpdateManager.getInstance().update(tpf,UpdateManager.Stream.GL_RENDER);
		renderer.clearBuffers();
        
        // Execute renderQueue item
      //  GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER).execute();
				//passManager.renderPasses(renderer);
		//renderer.draw(rootNode);
		renderer.draw(hudNode);
	
		super.render(tpf);
		
	
		
		//Debugger.drawBounds(rootNode, renderer);
		//if (StateManager.getHudNode()!= null)
		//renderer.draw(hudNode);
	}


	
	public void update(float time) {

		//StandardCameraTool.update(time);
		
		if(StateManager.getRecordingManager().isRecording())// && )
		{
			if(StateManager.getRecordingManager().getTimeSinceLastFrame()>StateManager.getRecordingManager().getDesiredPeriod())
				StateManager.getRecordingManager().drawFrame();
			
		}
		
		UpdateManager.getInstance().update(time,UpdateManager.Stream.GL_UPDATE);

		super.update(time);
		
		//skybox.updateGeometricState(time, true);
		//hudNode.updateGeometricState(time, true);
	}
	
	public Node getHUD() {

		return hudNode;
	}

	private void initState()  throws Exception
	{		
		DisplaySystem display = DisplaySystem.getDisplaySystem();
		renderer = display.getRenderer();
		
	  	layerWindow = new LayerWindow();
    	//GUILayer.getLoadedInstance().getDisplay().addWidget(layerWindow.getWindow());
    	layerWindow.getWindow().addWindowClosedListener(new IWindowClosedListener()
    	{

			public void windowClosed(WindowClosedEvent windowClosedEvent) {
				GeneralSettings.getInstance().getShowLayers().setValue(false);
			}
    		
    	});
	//	camera = display.getRenderer().getCamera();	    	
	/*	
		Box pool = new Box("Pool", new Vector3f(), 200,7.5f, 200);
		pool.getLocalTranslation().y = -7.5f;		
		
		{
			
				MaterialState semiBlueMaterial;
				semiBlueMaterial = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				semiBlueMaterial.setSpecular(new ColorRGBA(0.2f,0.2f,0.7f,0.3f));
				semiBlueMaterial.setAmbient(new ColorRGBA(0.2f,0.2f,0.7f,0.3f));
				semiBlueMaterial.setDiffuse(new ColorRGBA(0.2f,0.2f,0.7f,0.3f));		
				semiBlueMaterial.setShininess(30);
				semiBlueMaterial.setColorMaterial(MaterialState.CM_NONE );
				semiBlueMaterial.setMaterialFace(MaterialState.MF_FRONT_AND_BACK);
				
				AlphaState semiTransparentAlpha = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
		
				// Blend between the source and destination functions.
				semiTransparentAlpha.setBlendEnabled(true);
				// Set the source function setting.
				semiTransparentAlpha.setSrcFunction(AlphaState.SB_SRC_ALPHA);
				// Set the destination function setting.
				semiTransparentAlpha.setDstFunction(AlphaState.SB_ONE_MINUS_SRC_ALPHA);
				// Enable the test.
				semiTransparentAlpha.setTestEnabled(true);		
				// Set the test function to TF_GREATER.
				semiTransparentAlpha.setTestFunction(AlphaState.TF_GREATER);		
				pool.setRenderState(semiBlueMaterial);
				pool.setRenderState(semiTransparentAlpha);
				pool.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
				pool.updateRenderState();
				
			
		}
		
		
		rootNode.attachChild(pool);
		rootNode.updateRenderState();
		pool.updateRenderState();*/
		StateManager.getLogger().fine("Design state building hud");
		hudNode = buildHudNode();
		StateManager.getLogger().fine("Design state setting up root node");
        CullState cs = display.getRenderer().createCullState();
        cs.setCullMode(CullState.CS_BACK);
        rootNode.setRenderState(cs);
        
 
        PointLight light = new PointLight();
        light.setDiffuse( new ColorRGBA(0.75f, 0.75f, 0.75f, 1.0f  ) );
        light.setAmbient( new ColorRGBA( 0.75f, 0.75f, 0.75f, 1.0f ) );
        light.setLocation( new Vector3f( -10, 20, 10 ) );
      
        light.setEnabled( true );


        LightState lightState = display.getRenderer().createLightState();
        lightState.setEnabled( true );
        
        lightState.attach( light );

        lightState.setTwoSidedLighting(false);
        rootNode.setRenderState( lightState );
        rootNode.updateWorldBound();     

		ZBufferState buf = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.CF_LEQUAL);
        
        rootNode.setRenderState(buf);
    
     	rootNode.updateRenderState();
    	rootNode.updateGeometricState(0f,true);    	

  
	}

	private AtomicBoolean isInit = new AtomicBoolean(false);
	
	public void init() 
	{
		if(isInit.compareAndSet(false, true))
		{
			 StateManager.getToolManager().setSelectionEnabled(true);
			this.setActive(true);
			GUILayer.releaseFocus();
			StateManager.setRootModel(rootModel);
			setListening(true);
			StateManager.getCameraManager().getSkyBoxManager().setEnabled(true);
			StateManager.getCameraManager().getSkyBoxManager().setOverlayEnabled(GeneralSettings.getInstance().getShowGrid().isValue());
			StateManager.getCameraManager().setOrderedCameraList(cameraSet);
			
			//skybox.setLocalTranslation(StateManager.getCameraManager().getCamera().getLocation());
	        StateManager.getCameraManager().setRootNode(rootNode);
	    
	        CollisionTreeManager.getInstance().clear();
			CollisionTreeManager.getInstance().setTreeType(CollisionTree.AABB_TREE);
			CollisionTreeManager.getInstance().setGenerateTrees(true);
			CollisionTreeManager.getInstance().updateCollisionTree(rootNode);
	
	        rootNode.updateRenderState();
	 
	 		CollisionTreeManager.getInstance().setTreeType(CollisionTree.AABB_TREE);
	 		
	 	
 		//
 		
		}
 		//register standard tool listeners

	}
	
	

	
	public void remove() {
		isInit.set(false);
		this.setActive(false);
		GeneralSettings.getInstance().getShowLayers().setValue(false);
		setListening(false);

 	
	}
	
	private AtomicBoolean isListening = new AtomicBoolean(false);
	
	
	public void setListening(boolean listening) 
	{
		if(isListening.compareAndSet(!listening, listening))
		{
			
			if (listening)
			{
				InputLayer.get().addKeyListener(listeners,InputLayer.STATE_LAYER);
				InputLayer.get().addMouseListener(listeners,InputLayer.STATE_LAYER);
				//InputLayer.get().addMouseListener(viewModeToolbar,InputLayer.STATE_LAYER - 4);
				
				InputLayer.get().addMouseListener(toolbar, InputLayer.STATE_LAYER - 4);
				InputLayer.get().addMouseListener(mainInterface,InputLayer.STATE_LAYER - 4);
				InputLayer.get().addKeyListener(toolbar,InputLayer.STATE_LAYER - 4);
				InputLayer.get().addKeyListener(mainInterface,InputLayer.STATE_LAYER - 4);
				//InputLayer.get().addKeyListener(viewModeToolbar,InputLayer.STATE_LAYER - 4);
				
				InputLayer.get().addKeyListener(groupModeToolbar,InputLayer.STATE_LAYER - 4);
				InputLayer.get().addMouseListener(groupModeToolbar,InputLayer.STATE_LAYER - 4);
				
			//	ToolManager.getInstance().setCurrentTool(null);
		       
			//	ToolManager.getInstance().setCurrentTool(ToolPool.getInstance().getMovementTool());//Have to remove the tool, otherwise (if dialog mode for example is entered) then tools might not be managed properly
				StateManager.getToolManager().forceDeselect();
				ToolSelectionEffectManager.getInstance().forceDisengage();
			}else
			{
				InputLayer.get().removeMouseListener(listeners,InputLayer.STATE_LAYER);
				InputLayer.get().removeKeyListener(listeners,InputLayer.STATE_LAYER);
				
				InputLayer.get().removeMouseListener(toolbar,InputLayer.STATE_LAYER - 4);
				InputLayer.get().removeMouseListener(mainInterface,InputLayer.STATE_LAYER - 4);
				InputLayer.get().removeKeyListener(toolbar,InputLayer.STATE_LAYER - 4);
				InputLayer.get().removeKeyListener(mainInterface,InputLayer.STATE_LAYER - 4);
				InputLayer.get().removeKeyListener(groupModeToolbar,InputLayer.STATE_LAYER - 4);
				InputLayer.get().removeMouseListener(groupModeToolbar,InputLayer.STATE_LAYER - 4);
				
			//	InputLayer.get().removeKeyListener(viewModeToolbar,InputLayer.STATE_LAYER - 4);
			//	InputLayer.get().removeMouseListener(viewModeToolbar,InputLayer.STATE_LAYER - 4);
				
			//	ToolManager.getInstance().forceDeselect();
		//		ToolSelectionEffectManager.getInstance().forceDisengage();
				//ToolManager.getInstance().setCurrentTool(null);
			}
		}
	}

	

	private KeyInputListener undoListen = new KeyInputListener()
	{

		
		
		public void onKey(char character, int keyCode, boolean pressed) {
			if (pressed)
			{
			
				
				//if(MouseInput.get().isButtonDown(0))
				//	return;
				if (keyCode == KeyInput.KEY_Z)
				{
					if (KeyInput.get().isKeyDown(PlatformManager.get().getDefaultKeyBindings().getUndoRedoSecondKey())) //AWTInputManager.getInstance().isKeyDown(AWTInputManager.Key.LCONTROL))
					{
						if(StateManager.getToolManager().getCurrentTool().isSelected())
						{
							StateManager.getToolManager().forceDeselect();
						}
						UndoManager.getInstance().undo();
					}
				}else if (keyCode == KeyInput.KEY_Y)
				{
					if(KeyInput.get().isKeyDown(PlatformManager.get().getDefaultKeyBindings().getUndoRedoSecondKey()))
					{
						if(StateManager.getToolManager().getCurrentTool().isSelected())
						{
							StateManager.getToolManager().forceDeselect();
						}
						UndoManager.getInstance().redo();
					}
				}
			}
		}		
	};


	
	private StateEventDispatch dispatch = new StateEventDispatch();
	
	public void addListener(StateListener listener) {
		dispatch.addSettingListener(listener);		
	}

	
	public void removeListener(StateListener listener) {
		dispatch.removeSettingListener(listener);		
	}
	
	
	public static DesignState createLoadableDesignState()
	{
		return new DesignState(true);
	}	

	private DesignState(boolean load)
	{
		super("design");
	}
	

	private boolean loaded = false;
	protected ReentrantLock loadingLock = new ReentrantLock();
	protected Condition loadingCondition = loadingLock.newCondition();
	
	public boolean isLoaded() {
		return loaded;
	}

	
	public void waitForLoad() {
		while(!isLoaded())
		{
			try{
				loadingCondition.await();
			}catch(InterruptedException e)
			{
			}
		}
	}

	
	public void load() throws Exception {
		
		loadingLock.lock();
		try {
			this.setActive(false);
			this.cameraSet = new OrderedCameraList();
			
		
			StateManager.getLogger().fine("Design state initiating");
				initState();	
		
			listeners = new ListenerGroup();
			listeners.addKeyListener(undoListen);
			
			rootModel = new NodeModel();
			StateManager.getLogger().fine("Design state locking GL thread");
			StateManager.getGame().lock();
			try{
				StateManager.getLogger().fine("Design state GL thread locked");
				this.getRootNode().attachChild(rootModel.getSpatial());
			}finally
			{
				StateManager.getGame().unlock();
			}
			StateManager.setRootModel(rootModel);
			
			loaded = true;
			loadingCondition.signalAll();
		} finally {
			loadingLock.unlock();
		}
	}

	public NodeModel getRootModel() {
		return rootModel;
	}
	

	

	public void setMachineSpace(MachineSpace machineSpace)
	{
		rootModel.addChild(machineSpace.getModel());
	}
	
	
	public void registerMachine(StructuralMachine machine)
	{
		List<CameraDelegate> cameras = machine.getEnvironment().getCameras();
		cameraSet.clear();
		for (CameraDelegate delegate:cameras)
			cameraSet.addOrderedDelegate(delegate);
		
		machine.getEnvironment().addEnvironmentListener(new EnvironmentListener()
		{
			
			public void environmentChanged(PhysicsEnvironment source) {
				List<CameraDelegate> cameras = source.getCameras();

				cameraSet.clear();
				for (CameraDelegate delegate:cameras)
					cameraSet.addOrderedDelegate(delegate);
				
				if(! cameraSet.getOrderedDelegates().contains(StateManager.getCameraManager().getCameraDelegate()))
						StateManager.getCameraManager().setCameraDelegate(StateManager.getCameraManager().getDefaultDelegate());
			}
		});
	}



	public void clearMachine() {
		rootModel.detachAllChildren();
		
	}



	public void setGridEnabled(Boolean newValue) {
		if(this.isActive())
		{
			StateManager.getCameraManager().getSkyBoxManager().setOverlayEnabled(newValue);
		}
		
	}

	
	
}
