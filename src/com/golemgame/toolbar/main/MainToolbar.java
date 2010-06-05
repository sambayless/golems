package com.golemgame.toolbar.main;


import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.golemgame.constructor.Updatable;
import com.golemgame.constructor.UpdateManager;
import com.golemgame.local.StringConstants;
import com.golemgame.mvc.golems.AxleInterpreter;
import com.golemgame.mvc.golems.BallAndSocketInterpreter;
import com.golemgame.mvc.golems.BatteryInterpreter;
import com.golemgame.mvc.golems.BoxInterpreter;
import com.golemgame.mvc.golems.CameraInterpreter;
import com.golemgame.mvc.golems.CapsuleInterpreter;
import com.golemgame.mvc.golems.ConeInterpreter;
import com.golemgame.mvc.golems.ContactInterpreter;
import com.golemgame.mvc.golems.CylinderInterpreter;
import com.golemgame.mvc.golems.DefaultGolemsProperties;
import com.golemgame.mvc.golems.DistanceSensorInterpreter;
import com.golemgame.mvc.golems.GearInterpreter;
import com.golemgame.mvc.golems.GeneralSensorInterpreter;
import com.golemgame.mvc.golems.GrappleInterpreter;
import com.golemgame.mvc.golems.HingeInterpreter;
import com.golemgame.mvc.golems.HydraulicInterpreter;
import com.golemgame.mvc.golems.InputInterpreter;
import com.golemgame.mvc.golems.ModifierInterpreter;
import com.golemgame.mvc.golems.OscilloscopeInterpreter;
import com.golemgame.mvc.golems.PyramidInterpreter;
import com.golemgame.mvc.golems.RackGearInterpreter;
import com.golemgame.mvc.golems.RocketInterpreter;
import com.golemgame.mvc.golems.SphereInterpreter;
import com.golemgame.mvc.golems.StructureInterpreter;
import com.golemgame.mvc.golems.TouchSensorInterpreter;
import com.golemgame.mvc.golems.TubeInterpreter;
import com.golemgame.mvc.golems.validate.GolemsValidator;
import com.golemgame.settings.SettingChangedEvent;
import com.golemgame.settings.SettingsListener;
import com.golemgame.states.GeneralSettings;
import com.golemgame.states.StateManager;
import com.golemgame.tool.ActionToolSettings;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.UndoManager;
import com.golemgame.tool.action.mvc.AddComponentAction;
import com.golemgame.toolbar.Button;
import com.golemgame.toolbar.ButtonAdapter;
import com.golemgame.toolbar.Toolbar;
import com.golemgame.toolbar.TooltipDisplayer;
import com.golemgame.toolbar.VerticalSquareGridLayout;
import com.golemgame.toolbar.tooltip.IconToolTip;
import com.golemgame.toolbar.tooltip.ToolTipData;
import com.golemgame.views.Viewable.ViewMode;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.light.DirectionalLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.SceneElement;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.simplemonkey.Container;
import com.simplemonkey.IMouseListener;
import com.simplemonkey.IResizeListener;
import com.simplemonkey.IWidget;
import com.simplemonkey.layout.BorderLayout;
import com.simplemonkey.layout.BorderLayoutData;
import com.simplemonkey.layout.CenteringLayout;
import com.simplemonkey.layout.RowLayout;
import com.simplemonkey.layout.CenteringLayout.CenterStyle;
import com.simplemonkey.tooltip.ToolTipWidget;
import com.simplemonkey.util.Dimension;
import com.simplemonkey.widgets.SimpleButton;
import com.simplemonkey.widgets.SimpleButtonListener;
import com.simplemonkey.widgets.TextWidget;
import com.simplemonkey.widgets.ToggleButton;
import com.simplemonkey.widgets.ToggleListener;


public class MainToolbar extends Toolbar {
	private Quad backpanelTexture;

	private MaterialState textureMaterial;
	private ColorRGBA textureColor;
	private ColorRGBA buttonColor;
	private MaterialState buttonMaterial;	
	private float minScale= 0.1f;

	private Container maximizedContainer;
	private Container minimizedContainer;
	private Container primaryContainer;
	private Container extensionContainer;
	
	private int openedWidth = 100;
	
	private boolean panelOpened = true;
	
	private Lock slideLock = new ReentrantLock();
	
	private TooltipDisplayer tooltipDisplayer = null;
	
	private int mainButtonWidth = 64;
	
 	ToolTipWidget toolTipWidget;
	ArrayList<IWidget> buttons = new ArrayList<IWidget>();
	
	public TooltipDisplayer getTooltipDisplayer() {
		return tooltipDisplayer;
	}

	public void setTooltipDisplayer(TooltipDisplayer tooltipDisplayer) {
		this.tooltipDisplayer = tooltipDisplayer;
	}

	private Container centerContainer;
	
	public MainToolbar() 
	{		
		this(null);
	}
	
	public MainToolbar(ToolTipWidget tooltip) 
	{		
		super();
	//	this.getSpacingAppearance().setMargin(new Spacing(0,0,100,0));
		super.setPermeable(true);
		
		this.toolTipWidget = tooltip;
		//StateManager.getLogger().fine("Main Toolbar: building root node");
		DisplaySystem display= DisplaySystem.getDisplaySystem();
		//DisplaySystem display = DisplaySystem.getDisplaySystem();
		
		getSpatial().setCullMode(SceneElement.CULL_NEVER);
		
		this.getSpatial().setLightCombineMode(LightState.REPLACE);
		
		DirectionalLight light = new DirectionalLight();
        light.setDiffuse( new ColorRGBA( 1f, 1f, 1f, 1f ) );
        light.setAmbient( new ColorRGBA(1f, 1f, 1f, 1.0f ) );
        light.setDirection(new Vector3f(0,0,-1) );
        light.setEnabled( true );
        
        LightState lightState = display.getRenderer().createLightState();
        lightState.setEnabled( true );
        lightState.attach( light );
        
        getSpatial().setRenderState(lightState);
		
	
	//	backpanelTexture.setZOrder(10);
		//backpanelColor.setZOrder(7);

		getSpatial().updateWorldVectors();
	
		//getSpatial().setZOrder(6);
    	getSpatial().updateGeometricState(0, true);
    	getSpatial().updateRenderState();
    	
    	

		Renderer renderer = DisplaySystem.getDisplaySystem().getRenderer();
    	
		   AlphaState as = renderer.createAlphaState();
	       as.setBlendEnabled(true);
	 
	       as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
	       as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
	       as.setTestEnabled(false);
	       as.setEnabled(true);
 	
	      getSpatial().setRenderState(as);
	       
    // buttonColor=new ColorRGBA(01f, 0.3f, 0.3f, 0f);
	    //  buttonColor=new ColorRGBA(0.3f, 1f, 0.3f, 0f);
	    //buttonColor=new ColorRGBA(0.2f, 0.3f, 0.7f, 0f);
	      buttonColor=new ColorRGBA(1f, 1f, 1f, 0f);  
     buttonMaterial = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
     buttonMaterial.setAmbient(buttonColor);
     buttonMaterial.setDiffuse(buttonColor);
     buttonMaterial.setSpecular(buttonColor);
     buttonMaterial.setMaterialFace(MaterialState.MF_FRONT);
     buttonMaterial.setShininess(0);
     buttonMaterial.setColorMaterial(MaterialState.CM_NONE );	
 	
     getSpatial().setRenderState(buttonMaterial);    

		//this.setRenderState(RenderPool.solidCullState);
     getSpatial().updateGeometricState(0, true);
     getSpatial().updateRenderState();
    // StateManager.getLogger().fine("Main Toolbar: resize listener");

		super.setHeight(DisplaySystem.getDisplaySystem().getHeight());
		super.setWidth(openedWidth/2);

					this.setLayoutManager(new BorderLayout());
					
					Texture openNormal = TextureManager.loadTexture(getClass().getClassLoader().getResource("com/golemgame/data/textures/menu/arrowbutton16dark.png"), true);
					
					Texture openPressed = TextureManager.loadTexture(getClass().getClassLoader().getResource("com/golemgame/data/textures/menu/arrowbutton16.png"), true);
					
					
					//-----build the minimized toolbar
					
					minimizedContainer = new Container("Minimized");
					minimizedContainer.setLayoutData(BorderLayoutData.NORTH);
					
					Container controlContainer = new Container("Control");
					controlContainer.setLayoutData(BorderLayoutData.EAST);
					controlContainer.setLayoutManager(new BorderLayout());
				
					minimizedContainer.addWidget(controlContainer);
					StateManager.getLogger().fine("Main Toolbar: building toolbar option buttons");
					SimpleButton openButton = new SimpleButton(getTooltip("TOOLBAR.SHOW_TOOLBAR","Show the Toolbar"),openNormal,openPressed,openNormal);
					openButton.setTooltipWidget(toolTipWidget);
					openButton.setMinSize(16,16);
					openButton.setExpandable(false);
					openButton.setShrinkable(false);
					minimizedContainer.addWidget(openButton);
					minimizedContainer.layout();
					openButton.setText("");
					openButton.addButtonListener(new SimpleButtonListener()
					{

						
						public void buttonPressed() {
							openPanel();
							
						}
						
					});
					
					//------Build the maximized toolbar;
					maximizedContainer = new Container("Maximized");
					maximizedContainer.setLayoutData(BorderLayoutData.CENTER);
					maximizedContainer.setExpandable(true);
					maximizedContainer.setLayoutManager(new BorderLayout());
					this.addWidget(maximizedContainer);
					maximizedContainer.setPermeable(true);
					
				
					primaryContainer = new Container("Main Menu Container");
					primaryContainer.setLayoutManager(new BorderLayout());
					primaryContainer.setLayoutData(BorderLayoutData.CENTER);
					primaryContainer.setPermeable(false);
					primaryContainer.setHeight(this.getHeight());
					primaryContainer.setShrinkable(false);
					primaryContainer.setExpandable(false);
					backpanelTexture = buildBackPanel();
					
					//backpanelColor = buildColorPanel();
					//backpanelColor.getLocalTranslation().z -= 1f;
					StateManager.getLogger().fine("Main Toolbar: building back ground");
					//this.attachChild(backpanelColor);
					StateManager.getGame().lock();
					try{
						primaryContainer.getSpatial().attachChild(backpanelTexture);
					}finally{
					StateManager.getGame().unlock();
					}
					
					primaryContainer.addResizeListener(new IResizeListener()
				     {
						
						public void resize(Dimension newSize) {
							backpanelTexture.getLocalScale().set(primaryContainer.getWidth(),primaryContainer.getHeight(),1);
							backpanelTexture.getLocalTranslation().set(primaryContainer.getWidth()/2f,primaryContainer.getHeight()/2f,0);
							primaryContainer.getSpatial().updateGeometricState(0, true);
						
						}
				     });
				     
			
					maximizedContainer.addWidget(primaryContainer);
					
				
					
					//------Build the buttons for controlling the toolbar.
				
					Container topContainer = new Container();
					topContainer.setLayoutData(BorderLayoutData.NORTH);
					primaryContainer.addWidget(topContainer);
					topContainer.setLayoutManager(new BorderLayout());
					
					Container topRow = new Container();
					topRow.setLayoutData(BorderLayoutData.EAST);
					topContainer.addWidget(topRow);
					topRow.setLayoutManager(new RowLayout());
					
					//intentially make several copies of the textures, so they can have different properties. 
					Texture arrowUp = TextureManager.loadTexture(getClass().getClassLoader().getResource("com/golemgame/data/textures/menu/arrowbutton16dark.png"), true);
					
					Texture arrowDown = TextureManager.loadTexture(getClass().getClassLoader().getResource("com/golemgame/data/textures/menu/arrowbutton16.png"), true);
					
					Texture xUp = TextureManager.loadTexture(getClass().getClassLoader().getResource("com/golemgame/data/textures/menu/xbutton16dark.png"), true);
					
					Texture xDown = TextureManager.loadTexture(getClass().getClassLoader().getResource("com/golemgame/data/textures/menu/xbutton16.png"), true);
					
				
					final SimpleButton close = new SimpleButton(getTooltip("TOOLBAR.HIDE" ,"Hide the Toolbar"),xUp,xDown,xUp);
					close.setTooltipWidget(toolTipWidget);
					close.setMinSize(16, 16);
					close.setExpandable(false);
					close.setShrinkable(false);
					topRow.addWidget(close);
					
					/*
					 * Intended behvaiour for the close button:
					 * when pressed, the main toolbar disappears.
					 * However, a single button remains visible: an out arrow (identical to the autoclose button).
					 * When pressed, the main toolbar slides out, and the out arrow is removed.
					 */
					close.addButtonListener(new SimpleButtonListener()
					{

						
						public void buttonPressed() {
							closePanel();
							
						}
						
					});
					close.setText("");
					final ToggleButton hold = new ToggleButton(	getTooltip("TOOLBAR.AUTOCLOSE", "Auto-Close the Toolbar"),arrowUp,arrowDown,arrowUp );
					hold.setTooltipWidget(toolTipWidget);
					hold.setMinSize(16, 16);
					topRow.addWidget(hold);
					hold.addToggleListener(new ToggleListener()
					{

						
						public void toggle(boolean value) {
						
							GeneralSettings.getInstance().getAutoCloseMainMenu().setValue(!value);
							
						}
						
					});
					
					GeneralSettings.getInstance().getAutoCloseMainMenu().addSettingsListener(new SettingsListener<Boolean>()
							{

								
								public void valueChanged(
										SettingChangedEvent<Boolean> e) {
									//if(hold.isValue() != e.getNewValue())
										hold.setValue(!e.getNewValue());
								}
						
							}, true);
					
					
					GeneralSettings.getInstance().getAutoCloseMainMenu().addSettingsListener(new SettingsListener<Boolean>()
							{

								
								public void valueChanged(
										SettingChangedEvent<Boolean> e) {
									UpdateManager.getInstance().add(fadeAnimation);
								
								}
						
							}, true);
					
					
					buttons.add(close);
					buttons.add(hold);
					buttons.add(openButton);
					//----------------Build the main toolbar containers:
					
					final Container centeringContainer = new Container("Centering");
					centeringContainer.setLayoutData(BorderLayoutData.CENTER);
					primaryContainer.addWidget(centeringContainer);
					
					final CenteringLayout centeringLayout = new CenteringLayout(CenteringLayout.CenterStyle.VERTICAL,true);
				//	centeringLayout.setMaximizeSize(true);
				
					centeringContainer.setLayoutManager(centeringLayout);//new RowLayout(false));
				
					
					extensionContainer = new Container("Extension");
					extensionContainer.setLayoutData(BorderLayoutData.EAST);
					//extensionContainer.setLayoutManager(new BorderLayout());
					maximizedContainer.addWidget(extensionContainer);
					extensionContainer.setPermeable(false);
				//	extensionContainer.setLayoutManager(new CenteringLayout());
					//extensionContainer.setLayoutManager(new AbsoluteLayout(new Dimension( openedWidth,100)));
					extensionContainer.setLayoutManager(new AlligningLayout());
					
					final Container menuButtonContainer = new Container();
					menuButtonContainer.setLayoutData(BorderLayoutData.SOUTH);
					menuButtonContainer.setLayoutManager(new CenteringLayout(CenterStyle.HORIZONTAL,true));
					menuButtonContainer.setHeight(15);
				//	menuButtonContainer.setWidth(openedWidth);
					menuButtonContainer.setShrinkable(false);
					//centeringLayout.setYOffset(topRow.getHeight()/2f);
					primaryContainer.addWidget(menuButtonContainer);
					
					topRow.addResizeListener(new IResizeListener()
					{

						public void resize(Dimension newSize) {
							centeringLayout.setYOffset(newSize.getHeight()/2- 16) ;
							//centeringContainer.setSize(openedWidth, getHeight());
							centeringContainer.layout();
						
						//	menuButtonContainer.layout();
						}
						
					});
					
					
					
					Container borderContainer = new Container();
					centeringContainer.addWidget(borderContainer);
					
					CenteringLayout borderCenter =new CenteringLayout(CenterStyle.BOTH, true);
					borderCenter.setYOffset(-topRow.getHeight()/2f + menuButtonContainer.getHeight());
					borderContainer.setLayoutManager(borderCenter);
				
					centerContainer = new Container();
					centerContainer.setLayoutData(BorderLayoutData.CENTER);
					
					centerContainer.setLayoutManager(new RowLayout(false));
				
				//	StateManager.getLogger().fine("Main Toolbar: building main tool buttons");
					buildTools(centerContainer);
					borderContainer.addWidget(centerContainer);
					layout();
				 
					centerContainer.getSpatial().updateRenderState();
					getSpatial().updateRenderState();//needed to prevent the tools from having black boxes initially.
				

				 	//collect all the buttons that were just created
			
			//		StateManager.getLogger().fine("Main Toolbar: building menu button");
					addMenuButton(menuButtonContainer);
					layout();
				 	//for each one, add a hover/button press listener that displays a tooltip
				 	
				 	for(final IWidget button:buttons)
				 	{
				 		button.addMouseListener(new IMouseListener()
				 		{

							
							public void mouseMove(float x, float y) {
								if(button instanceof MainButton)
								{
									displayTooltip(((MainButton)button).getName());
								}else if (button instanceof ToggleButton)
								{
									displayTooltip(((ToggleButton)button).getName());
								}else if (button instanceof SimpleButton)
								{
									displayTooltip(((SimpleButton)button).getName());
								}
								
								
							}

							
							public void mouseOff() {
							
							}

							
							public void mousePress(boolean pressed, int b,
									float x, float y) {
								if(button instanceof MainButton)
								{
									displayTooltip(((MainButton)button).getName());
								}else if (button instanceof ToggleButton)
								{
									displayTooltip(((ToggleButton)button).getName());
								}else if (button instanceof SimpleButton)
								{
									displayTooltip(((SimpleButton)button).getName());
								}
								
								
								
							}

						
				 		});
				 	}
				 	
				 	
				 	GeneralSettings.getInstance().getResolution().addSettingsListener(new SettingsListener<java.awt.Dimension>()
				 	{

						
						public void valueChanged(
								SettingChangedEvent<java.awt.Dimension> size) {
						
							
							
							//openedWidth = Math.max(64, size.getNewValue().width/10);
							//openedWidth = Math.min(openedWidth, 100);
							/*if(size.getNewValue().width<800)
								mainButtonWidth=64;
							else*/
								openedWidth = 100;
							
							primaryContainer.setHeight(size.getNewValue().height);
							
							slideLock.lock();//lock this to ensure that the slide animation doesn't continue accidentally after its turned off
							try{
								menuButtonContainer.setWidth(openedWidth);
								if (panelOpened)
								{
									setSize(openedWidth*3,size.getNewValue().height);
									primaryContainer.setWidth(openedWidth);
									
									maximizedContainer.setWidth(openedWidth + extensionContainer.getWidth());
									layout();
								}else
								{
									setHeight(size.getNewValue().height);
									layout();
								}
								primaryContainer.layout();
								primaryContainer.updateMinSize();
								primaryContainer.layout();
								maximizedContainer.layout();
								extensionContainer.updateMinSize();
								extensionContainer.layout();
								for(IWidget w:extensionContainer.getWidgets())
								{
									w.layout();
									w.updateMinSize();
									w.layout();
								}
							
								maximizedContainer.setWidth(openedWidth + extensionContainer.getWidth());
								layout();
								menuButtonContainer.setWidth(openedWidth);
								extensionContainer.updateMinSize();
								extensionContainer.layout();
								maximizedContainer.updateMinSize();
								maximizedContainer.layout();
								maximizedContainer.setWidth(openedWidth + extensionContainer.getWidth());
								layout();
								
								for(IWidget w:extensionContainer.getWidgets())
								{//yes... this is incredibly redundant. Some subset of these calls should work, I just dont care to find out which.
									w.layout();
									w.updateMinSize();
								
									w.layout();
							
									w.updateMinSize();
									extensionContainer.updateMinSize();
									extensionContainer.layout();
									w.layout();
								
									extensionContainer.setPermeable(false);
									maximizedContainer.layout();
									w.setSize(w.getMinSize());
									
						
									maximizedContainer.setWidth(openedWidth + extensionContainer.getMinWidth());
									
									w.layout();
									w.updateMinSize();
						
									w.layout();
							
									w.updateMinSize();
									extensionContainer.updateMinSize();
									extensionContainer.layout();
									w.layout();
						
									w.setSize(w.getMinSize());
								}
			/*
									primaryContainer.layout();
									primaryContainer.updateMinSize();
									primaryContainer.layout();
									maximizedContainer.layout();
									extensionContainer.updateMinSize();
									extensionContainer.layout();
									for(IWidget w:extensionContainer.getWidgets())
									{
										w.layout();
										w.updateMinSize();
										w.layout();
									}
									
									maximizedContainer.setWidth(openedWidth + extensionContainer.getWidth());
									layout();
									
									extensionContainer.updateMinSize();
									extensionContainer.layout();
									maximizedContainer.updateMinSize();
									maximizedContainer.layout();
									maximizedContainer.setWidth(openedWidth + extensionContainer.getWidth());
									layout();*/
									
				
					
							}finally
							{
								slideLock.unlock();
							}
							
							
							
							
								
					
							
						}
				 		
				 	}
				 	
				 	, true);
				 	
	}

	private void addMenuButton(Container centerContainer) {
	
		
		

		//Container middleContainer = new Container();
		//middleContainer.setLayoutManager(new BorderLayout());
	//	StandardButton menuButton = new StandardButton(64, 64, crystalActive, jointInactive,jointHover, "Menu");
	
		TextWidget menuButton = new TextWidget("Menu");
		menuButton.setTooltipWidget(toolTipWidget);
		
		IconToolTip.setDescription(menuButton,  getTooltip("TOOLBAR.MENU.TOOLTIP","From the menu, you can load and save machines\n(as well as turn off these tooltips).\nYou can also press Escape to open the menu."));
		menuButton.setText(	getTooltip("TOOLBAR.MENU" ,"Menu"),new Color(0.3f,0.3f,0.3f,1.0f));
		menuButton.removeTexture(0);
		//menuButton.getTexture(1).setApply(Texture.AM_MODULATE);
		//	middleContainer.addWidget(menuButton);
	
		final ColorRGBA hoverColor=new ColorRGBA(1,1f,1f,0.9f);
		final ColorRGBA normalColor=new ColorRGBA(1,1f,1f,0.5f);
		final ColorRGBA pressedColor=new ColorRGBA(1,1f,1f,0.3f);
			final	MaterialState menuBackgroundMaterial;
				menuBackgroundMaterial = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
			//	menuBackgroundMaterial.setSpecular(new ColorRGBA(1f,1f,1f1f));
				menuBackgroundMaterial.setAmbient(normalColor);
				menuBackgroundMaterial.setDiffuse(normalColor);		
				menuBackgroundMaterial.setShininess(0);
				menuBackgroundMaterial.setColorMaterial(MaterialState.CM_NONE );
				menuBackgroundMaterial.setMaterialFace(MaterialState.MF_FRONT);
				
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
				menuButton.getSpatial().setRenderState(menuBackgroundMaterial);
				menuButton.getSpatial().setRenderState(semiTransparentAlpha);
			//	menuButton.getSpatial().setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
				menuButton.getSpatial().updateRenderState();
	
				
				menuButton.addMouseListener(new IMouseListener()
				{

					public void mouseMove(float x, float y) {
						menuBackgroundMaterial.setDiffuse(hoverColor);
						menuBackgroundMaterial.setAmbient(hoverColor);
						
					}

					public void mouseOff() {
						menuBackgroundMaterial.setDiffuse(normalColor);
						menuBackgroundMaterial.setAmbient(normalColor);
						
						
					}

					public void mousePress(boolean pressed, int button, float x,
							float y) {
						
						if(pressed)
						{
							GeneralSettings.getInstance().getMenu().initiateAction();
							
							menuBackgroundMaterial.setDiffuse(pressedColor);
							menuBackgroundMaterial.setAmbient(pressedColor);
						}else
						{
							menuBackgroundMaterial.setDiffuse(hoverColor);
							menuBackgroundMaterial.setAmbient(hoverColor);
						}
						
						
						
						
					}
					
				});
				
				
		centerContainer.addWidget(menuButton);
		
		centerContainer.layout();
	//	middleContainer.layout();
	}


	private void displayTooltip(String tooltip)
	{
		if(tooltipDisplayer!= null)
			tooltipDisplayer.displayTooltip(tooltip);
	}
	
	public void openPanel()
	{
		slideLock.lock();//lock this to ensure that the slide animation doesn't continue accidentally after its turned off
		try{
			panelOpened = true;
			UpdateManager.getInstance().remove(newSlideAnimation);
			//immediately open the panel
			setWidth(openedWidth*3);
			
			this.layout();
	
			removeWidget(minimizedContainer);
			
			addWidget(maximizedContainer);
		
		//	maximizedContainer.setHeight(this.getHeight());
			this.layout();
			primaryContainer.setWidth(openedWidth);
			maximizedContainer.setWidth(openedWidth + extensionContainer.getWidth());
			backpanelTexture.setCullMode(SceneElement.CULL_NEVER);
		}finally{
			slideLock.unlock();
		}
	}
	
	public void closePanel()
	{
		slideLock.lock();//lock this to ensure that the slide animation doesn't continue accidentally after its turned off
		try{
			panelOpened = false;
			this.setWidth(openedWidth);
			this.removeWidget(maximizedContainer);
		//	this.setWidth(minimizedContainer.getMinWidth());
			this.addWidget(minimizedContainer);
			
			this.layout();
			UpdateManager.getInstance().add(newSlideAnimation);
		}finally{slideLock.unlock();}
	}
	

	
	public boolean onButton(int button, boolean pressed, int x, int y) {
		
		super.onButton(button, pressed, x, y);
		if (engaged && pressed)
			return true;
		return false;
	}


	
	@Override
	public void mouseOff() {
		if (engaged)
		{
			engaged = false;
			engage(false);
		}
		super.mouseOff();
	}

	@Override
	public boolean onMove(int deltaX, int deltaY, int newX, int newY) {
		
		super.onMove(deltaX, deltaY, newX, newY);
		//if (engaged && deltaX < this.getWidth())
		//	return true;
		return false;
	}

	protected void engage(boolean engage) {
		if(panelOpened)
			UpdateManager.getInstance().add(fadeAnimation);	
	}
    protected float arrangeButtonGroups(Button[] buttons)
    {
    	//curY is the bottom of the previous entry
    	int y = 0;
    	for (IWidget button:this.getWidgets())
    	{

    		button.setY( y);
    		y -= button.getSize().getHeight();
    	}
    	return  Math.abs(y);
    }

	private Quad buildBackPanel() {
		Quad backpanel = new Quad("Panel", 1, 1);
		backpanel.getLocalScale().set(this.getWidth(),this.getHeight(),1);
		backpanel.getLocalTranslation().set(this.getWidth()/2f,this.getHeight()/2f,0);
		
			   AlphaState as = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
		       as.setBlendEnabled(true);
		 
		       as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		       as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
		       as.setTestEnabled(false);
		       as.setEnabled(true);
		       backpanel.setRenderState(as); 
		      
		       textureColor=new ColorRGBA(1f, 1f, 1f, 0f);
		       textureMaterial = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		       textureMaterial.setAmbient(textureColor);
		       textureMaterial.setDiffuse(textureColor);
		       textureMaterial.setSpecular(textureColor);
		       textureMaterial.setMaterialFace(MaterialState.MF_FRONT);
		       textureMaterial.setShininess(0);
		       textureMaterial.setColorMaterial(MaterialState.CM_NONE );	   
		    	   
		       backpanel.setRenderState(textureMaterial);
		    	   
		   /*    TextureState ts =  DisplaySystem.getDisplaySystem().getRenderer().createTextureState();

		       ts.setTexture(gradient,0);
		       
		       int textureWidth = ts.getTexture().getImage().getWidth();
		       int textureHeight = ts.getTexture().getImage().getHeight();
		       ts.setEnabled(true);
		 
		       FloatBuffer texCoords = BufferUtils.createVector2Buffer(4);
		       texCoords.put(getUForPixel(0,textureWidth)).put(getVForPixel(0,textureHeight));
		       texCoords.put(getUForPixel(0,textureWidth)).put(getVForPixel(height,textureHeight));
		       texCoords.put(getUForPixel(width,textureWidth)).put(getVForPixel(height,textureHeight));
		       texCoords.put(getUForPixel(width,textureWidth)).put(getVForPixel(0,textureHeight));
		       backpanel.setTextureBuffer(0, texCoords);
		       backpanel.setRenderState(ts);
		       */
			backpanel.updateRenderState();
			
			return backpanel;
	}

	private Texture loadTexture(String path, boolean mipmaps)
	{
	/*	if(mipmaps)
		{
			System.out.println();
		}*/
		
		Image textureImage;
		
		
		if (mipmaps)
		{
			textureImage= TextureManager.loadImage(getClass().getClassLoader().getResource( "com/golemgame/data/textures/size_56/" + path), !mipmaps);
		}else
		{
			textureImage= TextureManager.loadImage(getClass().getClassLoader().getResource( "com/golemgame/data/textures/" + path), !mipmaps);
		}
		Texture texture = new Texture();
		texture.setImageLocation(path);
		textureImage.setWidth(64);
		textureImage.setHeight(64);
		
		if(mipmaps)
		{
		
			textureImage.setMipMapSizes(new int[]{64*64*4,32*32*4,16*16*4,8*8*4,4*4*4,2*2*4,1*4});
	

			float scale = 56f/64f;
			texture.setTranslation(new Vector3f(4f/64f,4f/64f,0));
		  	texture.setScale(new Vector3f().set(scale,scale,scale));
		}
		
		 texture.setAnisoLevel(0);
		 texture.setMipmapState( Texture.MM_LINEAR);
		 texture.setFilter(Texture.FM_LINEAR);
		 texture.setImage(textureImage);
		 
		 return texture;
	}

	private Texture loadTexture(String path)
	{
	 	
		return loadTexture(path,true);
	}

	
	private String getTooltip(String tooltip, String def)
	{
		return StringConstants.get(tooltip,def,false);

	}
	
	
	private void buildTools(final Container container)
	{
		
		

 	    	
  	//Hover textures
  	Texture crystalActive =loadTexture("buttons/glass/Crystal.png");
 	Texture toolHover =loadTexture("buttons/glass/Glass_White.png");
 	Texture jointHover =loadTexture("buttons/glass/Glass_Red.png");
	Texture functionalHover =loadTexture("buttons/glass/Glass_Green.png");
	Texture specialHover =loadTexture("buttons/glass/Glass_Yellow.png");
	Texture shapeHover =loadTexture("buttons/glass/Glass_Blue.png");
	
	//inactive textures
	Texture toolInactive =loadTexture("buttons/solid/Solid_White.png");
	Texture shapeInactive =loadTexture("buttons/solid/Solid_Blue.png");
	Texture jointInactive =loadTexture("buttons/solid/Solid_Red.png");
	Texture functionalInactive =loadTexture("buttons/solid/Solid_Green.png");
	Texture specialInactive =loadTexture("buttons/solid/Solid_Yellow.png");
	
	 //Icon Textures
	Texture selectTexture =loadTexture("buttons/tools/SelectOne.png");
	Texture groupSelectTexture =loadTexture("buttons/tools/SelectTwo.png");
	Texture wireModeTexture =loadTexture("buttons/tools/Wire.png");	
	Texture controlModeTexture =loadTexture("buttons/tools/ControlPointsOn.png");	
	Texture rotateModeTexture =loadTexture("buttons/tools/RotationOn.png");	
	
	
	
	Texture axleTexture =loadTexture("buttons/joints/Axle.png");
	Texture hingeTexture =loadTexture("buttons/joints/Hinge.png");
	Texture ballAndSocket =loadTexture("buttons/joints/BallSocket.png");
	
	Texture hydraulicTexture =loadTexture("buttons/joints/Hydraulic.png");
	
	Texture gearTexture =loadTexture("buttons/special/Gear.png");
	Texture rackGearTexture =loadTexture("buttons/special/RackGear.png");
	Texture rocketTexture =loadTexture("buttons/special/Rocket.png");
	Texture sourceTexture =loadTexture("buttons/functionals/Battery.png");
	Texture diffTexture = loadTexture("buttons/functionals/Slope.png");
	Texture modifierTexture = loadTexture("buttons/functionals/Math.png");
	Texture lightTexture =loadTexture("buttons/special/LightBulb.png");
    Texture touchTexture =loadTexture("buttons/functionals/Push.png");
	Texture distanceTexture =loadTexture("buttons/functionals/Distance.png");
	Texture lightSensorTexture =loadTexture("buttons/functionals/Eye.png");
	Texture generalSensorTexture =loadTexture("buttons/functionals/Multimeter.png");
	Texture oscilloscopeTexture =loadTexture("buttons/functionals/Oscilloscope.png");
	Texture inputTexture =loadTexture("buttons/functionals/Input.png");
	
	
	Texture squareTexture =loadTexture("buttons/polyhedrons/Cube.png");
	Texture sphereTexture =loadTexture("buttons/polyhedrons/Sphere.png");
	Texture cylinderTexture =loadTexture("buttons/polyhedrons/Cylinder.png");
	Texture pyramidTexture =loadTexture("buttons/polyhedrons/Pyramid.png");
	Texture coneTexture =loadTexture("buttons/polyhedrons/Cone.png");
	Texture ellipsoidTexture =loadTexture("buttons/polyhedrons/Ellipse.png");
	Texture capsuleTexture =loadTexture("buttons/polyhedrons/Capsule.png");
	Texture tubeTexture =loadTexture("buttons/polyhedrons/Tube.png");
	Texture cameraTexture =loadTexture("buttons/special/Camera.png");	
	
	Texture grappleTexture =loadTexture("buttons/special/Laser.png");	
	Texture contactTexture =loadTexture("buttons/functionals/Contact3.png");	
	
	
	
	final Texture expand =loadTexture("buttons/menu/Plus.png");
	final Texture shrink =loadTexture("buttons/menu/Minus.png");


	expand.setApply(Texture.AM_MODULATE);
	shrink.setApply(Texture.AM_MODULATE);
	
	
	
 	MainButtonGroup toolGroup = new MainButtonGroup(getWidth(), toolInactive, toolHover, crystalActive);    	
 	//container.addWidget(toolGroup);
 	
 	MainButtonGroup shapeGroup = new MainButtonGroup(getWidth(), shapeInactive, shapeHover, crystalActive);    	
 	//container.addWidget(shapeGroup);
 	
 	MainButtonGroup jointGroup = new MainButtonGroup(getWidth(), jointInactive, jointHover, crystalActive);    	
 	//container.addWidget(jointGroup);
 	
 	MainButtonGroup functionalGroup = new MainButtonGroup(getWidth(),functionalInactive, functionalHover, crystalActive);    	
 	//container.addWidget(functionalGroup);
 
 	MainButtonGroup specialGroup = new MainButtonGroup(getWidth(),specialInactive, specialHover, crystalActive);    	
 	//container.addWidget(specialGroup);
 	
 	final ExtendedToolbarSection extraShapes = new ExtendedToolbarSection();
 //	extraShapes.setLayoutData(BorderLayoutData.EAST);
 //	extraShapes.setLayoutManager(new RowLayout(false));
 	extraShapes.setVisible(false);
 	
 	final ExtendedToolbarSection extraFunctionals = new ExtendedToolbarSection();
 //	extraFunctionals.setLayoutData(BorderLayoutData.EAST);
 //	extraFunctionals.setLayoutManager(new RowLayout(false));
 	extraFunctionals.setVisible(false);
 	
	final ExtendedToolbarSection extraJoints = new ExtendedToolbarSection();
	//extraJoints.setLayoutData(BorderLayoutData.EAST);
	//extraJoints.setLayoutManager(new RowLayout(false));
	extraJoints.setVisible(false);
	
	final ExtendedToolbarSection extraActives = new ExtendedToolbarSection();
	//extraActives.setLayoutData(BorderLayoutData.EAST);
	//extraActives.setLayoutManager(new RowLayout(false));
	extraActives.setVisible(false);
 	
	extensionContainer.addWidget(extraShapes);
	extensionContainer.addWidget(extraFunctionals);
	extensionContainer.addWidget(extraJoints);
	extensionContainer.addWidget(extraActives);
	
 	 AlphaState as = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
     as.setBlendEnabled(true);

     as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
     as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
     as.setTestEnabled(false);
     as.setEnabled(true);
     
     
     extraShapes.getBackgroundSpatial().setRenderState(as); 
     extraFunctionals.getBackgroundSpatial().setRenderState(as); 
     extraActives.getBackgroundSpatial().setRenderState(as); 
     extraJoints.getBackgroundSpatial().setRenderState(as); 
     
     extraShapes.getBackgroundSpatial().setRenderState(textureMaterial);
     extraJoints.getBackgroundSpatial().setRenderState(textureMaterial);
     extraActives.getBackgroundSpatial().setRenderState(textureMaterial);
     extraFunctionals.getBackgroundSpatial().setRenderState(textureMaterial);
     
 	
 	final MainButton select = new MainButton(getTooltip("TOOLBAR.SELECT","Select"),selectTexture, toolGroup);
 	IconToolTip.setDescription(select,getTooltip("TOOLBAR.SELECT.TOOLTIP","Click and hold on an object to select it.\nWhile selected, press enter to change its properties.\nTo change the plane of movement, hold down $Z_AXIS_KEY or $X_AXIS_KEY."));
 	select.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				ActionToolSettings.getInstance().getAssignedGroupSelectionMode().setValue(false);
				ActionToolSettings.getInstance().setGroupSelectionMode(false); 
				ActionToolSettings.getInstance().getWireMode().setValue(false);
				ActionToolSettings.getInstance().getRotate().setValue(false);
				ActionToolSettings.getInstance().getModify().setValue(false);
				 StateManager.getViewManager().setViewMode(ViewMode.MATERIAL );
				 StateManager.getToolManager().setPrimaryTool(StateManager.getToolPool().getMovementTool());
				 StateManager.getToolManager().forceDeselect();
				 // StateManager.getStructuralMachine().removeViewMode(ViewMode.GROUPMODE);
			//	 StateManager.getStructuralMachine().removeViewMode(ViewMode.FUNCTIONAL);
			} 		
 	});
 	container.addWidget(select);   	

 	
 	
 	final MainToggleButton groupSelect = new MainToggleButton(getTooltip("TOOLBAR.ISLAND","Island Select"),crystalActive,toolInactive ,toolHover,0);
 	
 	Texture tooltipToolHover = loadTexture(toolHover.getImageLocation(),false);
	tooltipToolHover.setApply(Texture.AM_MODULATE);
	
	Texture groupSelectTextureIcon = loadTexture(groupSelectTexture.getImageLocation(),false);	
	groupSelectTextureIcon.setApply(Texture.AM_DECAL);

 	IconToolTip.setTooltipData(groupSelect,new ToolTipData(groupSelectTextureIcon, tooltipToolHover,  groupSelect.getName()));//" + ActionToolSettings.getInstance().getMultipleSelectKey().getKeyName() + "
 	IconToolTip.setDescription(groupSelect, 	getTooltip("TOOLBAR.ISLAND.TOOLTIP","Select connected things all at once.\nYou can also select multiple things by holding down $MULTIPLE_SELECT_KEY."));

 	groupSelectTexture.setApply(Texture.AM_DECAL);
 	groupSelect.setTexture(groupSelectTexture, 1);
 	
 	groupSelect.addToggleListener(new ToggleListener()
 	{
		
		public void toggle(boolean value) {
		//	StateManager.getToolManager().forceDeselect();
			ActionToolSettings.getInstance().getGroupSelectionMode().setValue( value); 
			ActionToolSettings.getInstance().getWireMode().setValue(false);
			 //StateManager.getToolManager().setPrimaryTool(StateManager.getToolPool().getMovementTool());
	
			if(StateManager.getViewManager().getViews().contains(ViewMode.FUNCTIONAL))
			{
					StateManager.getToolManager().forceDeselect();
					StateManager.getViewManager().setViewMode(ViewMode.MATERIAL );	
			}
	
				
				ActionToolSettings.getInstance().getAssignedGroupSelectionMode().setValue(false);
			
				// StateManager.getStructuralMachine().removeViewMode(ViewMode.GROUPMODE);
				// StateManager.getViewManager().removeViewMode(ViewMode.FUNCTIONAL);
				 
			
			
		}
 	});
 	
 	container.addWidget(groupSelect);   	
 	
 	ActionToolSettings.getInstance().getGroupSelectionMode().addSettingsListener(new SettingsListener<Boolean>()
 			{
				
				public void valueChanged(SettingChangedEvent<Boolean> e) {
					if(groupSelect.isValue()!=e.getNewValue())
						groupSelect.setValue(e.getNewValue());
				}
 			},true);
 		

 	
 	final MainToggleButton rotate = new MainToggleButton(getTooltip("TOOLBAR.ROTATE","Rotate"),crystalActive,toolInactive ,toolHover,0);
	
	Texture rotateModeTextureIcon = loadTexture(rotateModeTexture.getImageLocation(),false);	
	rotateModeTextureIcon.setApply(Texture.AM_DECAL);
	
 	IconToolTip.setTooltipData(rotate,new ToolTipData(rotateModeTextureIcon, tooltipToolHover,  rotate.getName()));
 	//StringConstants.replace("Select something, then click and drag around it to rotate.\nTo change the axis of rotation, hold down " + xy + " or " + yz + ".\n" +"You can also rotate things by holding down " + ActionToolSettings.getInstance().getRotateKey().getKeyName()
	IconToolTip.setDescription(rotate, getTooltip("TOOLBAR.ROTATE.TOOLTIP","Select something, then click and drag around it to rotate.\nTo change the axis of rotation, hold down $Z_AXIS_KEY or $$X_AXIS_KEY.\n You can also rotate things by holding down $ROTATE_KEY." ));

	
 	rotateModeTexture.setApply(Texture.AM_DECAL);
 	rotate.setTexture(rotateModeTexture, 1);

 	rotate.addToggleListener(new ToggleListener()
 	{
		
		public void toggle(boolean value) {
			 StateManager.getToolManager().setPrimaryTool(StateManager.getToolPool().getMovementTool());
			ActionToolSettings.getInstance().getRotate().setValue(value);
		}
 	});
 	container.addWidget(rotate);   
 	ActionToolSettings.getInstance().getRotate().addSettingsListener(new SettingsListener<Boolean>()
 			{
				
				public void valueChanged(SettingChangedEvent<Boolean> e) {
					if(rotate.isValue()!=e.getNewValue())
						rotate.setValue(e.getNewValue());
				}
 			},true);
 	
 	final MainToggleButton control = new MainToggleButton( getTooltip("TOOLBAR.STRETCH","Stretch"),crystalActive,toolInactive ,toolHover,0);
	
	Texture controlModeTextureIcon = loadTexture(controlModeTexture.getImageLocation(),false);	
	controlModeTextureIcon.setApply(Texture.AM_DECAL);
 	IconToolTip.setTooltipData(control,new ToolTipData(controlModeTextureIcon, tooltipToolHover,  control.getName()));
 	// + ActionToolSettings.getInstance().getModifyKey().getKeyName() +
	IconToolTip.setDescription(control,  getTooltip("TOOLBAR.STRETCH.TOOLTIP", "Select something, then drag one of the white control spheres\nto change its shape.\n" +"You can also stretch things by holding down $CONTRL_POINT_KEY."));

 	controlModeTexture.setApply(Texture.AM_DECAL);
 	control.setTexture(controlModeTexture, 1);

 	control.addToggleListener(new ToggleListener()
 	{
		
		public void toggle(boolean value) {
			 StateManager.getToolManager().setPrimaryTool(StateManager.getToolPool().getMovementTool());
			ActionToolSettings.getInstance().getModify().setValue(value);

		}
 	});
 	container.addWidget(control);   
 	ActionToolSettings.getInstance().getModify().addSettingsListener(new SettingsListener<Boolean>()
 			{
				
				public void valueChanged(SettingChangedEvent<Boolean> e) {
					if(control.isValue()!=e.getNewValue())
						control.setValue(e.getNewValue());
				}
 			},true);
 	
 	
	final MainToggleButton wire = new MainToggleButton(	getTooltip("TOOLBAR.WIRE_MODE" , "Show Wires"),crystalActive,toolInactive ,toolHover,0);
	IconToolTip.setTooltipData(wire,new ToolTipData(wireModeTexture, toolHover,  wire.getName()));
	IconToolTip.setDescription(wire,  getTooltip("TOOLBAR.WIRE_MODE.TOOLTIP","Wire mode lets you see the wires connecting different parts of your machine,\nas well as functional components like batteries.\nComponents that can be connected with wires have green or yellow ports."));

	wireModeTexture.setApply(Texture.AM_DECAL);
 	wire.setTexture(wireModeTexture, 1);

 	wire.addToggleListener(new ToggleListener()
 	{
		
		public void toggle(boolean value) {
			StateManager.getToolManager().forceDeselect();
			 StateManager.getToolManager().setPrimaryTool(StateManager.getToolPool().getMovementTool());
			if(value)
			{
				ActionToolSettings.getInstance().setGroupSelectionMode(false); 
				ActionToolSettings.getInstance().getWireMode().setValue(true);
				ActionToolSettings.getInstance().getRotate().setValue(false);
				ActionToolSettings.getInstance().getModify().setValue(false);
			}
			if(StateManager.getStructuralMachine()!= null){
				if(value)
				{
					//StateManager.getViewManager().setViewMode(ViewMode.FUNCTIONAL );	
					//StateManager.getStructuralMachine().remove
				//	 StateManager.getStructuralMachine().removeViewMode(ViewMode.MATERIAL);
				//	StateManager.getStructuralMachine().addViewMode(ViewMode.FUNCTIONAL );
					StateManager.getViewManager().setViewMode(ViewMode.FUNCTIONAL  );	
				
				}else
				{
					StateManager.getViewManager().setViewMode(ViewMode.MATERIAL  );	
					//StateManager.getStructuralMachine().removeViewMode(ViewMode.FUNCTIONAL);
					//StateManager.getViewManager().setViewMode(ViewMode.MATERIAL );	
				//	StateManager.getStructuralMachine().addViewMode(ViewMode.MATERIAL );			
				
				}
			}
		}
 	});
 	ActionToolSettings.getInstance().getWireMode().addSettingsListener(new SettingsListener<Boolean>()
 			{
				
				public void valueChanged(SettingChangedEvent<Boolean> e) {
					if(wire.isValue()!=e.getNewValue())
						wire.setValue(e.getNewValue());
				}
 			},true);
 	container.addWidget(wire);   
 	
 	
 	//Shape buttons
 	final MainButton square = new MainButton(	getTooltip("TOOLBAR.BOX", "Box"),squareTexture, shapeGroup);
	IconToolTip.setDescription(square, getTooltip("TOOLBAR.BOX.TOOLTIP","Create a box."));

 	square.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
			//	BoxStructure box = new BoxStructure(StateManager.getStructuralMachine());
			//	preparePhysicalStructure(box);
				preparePhysicalStructure(new BoxInterpreter());
				
			}    		
 	});    	
 	extraShapes.addMainWidget(square);
 	container.addWidget(square);   	

 	final MainButton sphere = new MainButton(	getTooltip("TOOLBAR.SPHERE" ,"Sphere"),sphereTexture, shapeGroup);
	IconToolTip.setDescription(sphere,  getTooltip("TOOLBAR.SPHERE.TOOLTIP","Create a sphere."));

 	sphere.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				preparePhysicalStructure(new SphereInterpreter());
			
				
			}    		
 	});    
 	extraShapes.addMainWidget(sphere);
 	container.addWidget(sphere); 
 	
 	final MainButton cylinder = new MainButton(	getTooltip("TOOLBAR.CYLINDER","Cylinder"),cylinderTexture, shapeGroup);
	IconToolTip.setDescription(cylinder,  getTooltip("TOOLBAR.CYLINDER.TOOLTIP","Create a cylinder."));

 	cylinder.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				preparePhysicalStructure(new CylinderInterpreter());
			
				
			}    		
 	});    	
 	extraShapes.addMainWidget(cylinder);
 	container.addWidget(cylinder); 
 	
 	final MainButton tube = new MainButton(	getTooltip("TOOLBAR.TUBE", "Tube"),tubeTexture, shapeGroup);
	IconToolTip.setDescription(tube,  getTooltip("TOOLBAR.TUBE.TOOLTIP","Create a tube."));

	tube.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				preparePhysicalStructure(new TubeInterpreter());
			
				
			}    		
 	});    	
	//extraShapes.addMainWidget(tube);
	container.addWidget(tube); 

 	final MainButton pyramid = new MainButton(	getTooltip("TOOLBAR.PYRAMID", "Pyramid"),pyramidTexture, shapeGroup);
	IconToolTip.setDescription(pyramid,  getTooltip("TOOLBAR.PYRAMID.TOOLTIP","Create a pyramid."));

 	pyramid.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				preparePhysicalStructure(new PyramidInterpreter());
						
			}    		
 	});    	
 	extraShapes.addMainWidget(pyramid);
 	container.addWidget(pyramid);   	
 	
 	final MainButton cone = new MainButton(	getTooltip("TOOLBAR.CONE","Cone"),coneTexture, shapeGroup);
	IconToolTip.setDescription(cone, getTooltip("TOOLBAR.CONE.TOOLTIP","Create a cone."));

 	cone.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				preparePhysicalStructure(new ConeInterpreter());
				
				
			}    		
 	});    	
 	//extraShapes.addMainWidget(cone);
 	container.addWidget(cone);   
 	
 	final MainButton ellipsoid = new MainButton(	getTooltip("TOOLBAR.ELLIPSOID","Ellipsoid"),ellipsoidTexture, shapeGroup);
	IconToolTip.setDescription(ellipsoid,  getTooltip("TOOLBAR.ELLIPSOID.TOOLTIP","Create an ellipsoid."));

 	ellipsoid.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				SphereInterpreter sphere = new SphereInterpreter();
				sphere.setEllispoid(true);
				preparePhysicalStructure(sphere);
				
			}    		
 	});    	
 	//extraShapes.addMainWidget(ellipsoid);
 	container.addWidget(ellipsoid); 
 	
 
 

 	
 	
	final MainButton capsule = new MainButton(	getTooltip("TOOLBAR.CAPSULE","Capsule"),capsuleTexture, shapeGroup);
 	IconToolTip.setDescription(capsule,  getTooltip("TOOLBAR.CAPSULE.TOOLTIP","Create a capsule."));

 	capsule.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				CapsuleInterpreter component = new CapsuleInterpreter();
	
				preparePhysicalStructure(component);
				
			}    		
 	});    	
//	extraShapes.addMainWidget(capsule);
 	container.addWidget(capsule); 

 	final MainToggleButton extraShapeButton = new MainToggleButton(	getTooltip("TOOLBAR.MORE_SHAPES","More Shapes"),null,null ,null,0,expand,shrink,1);
	
	Texture extraShapeButtonTextureIcon = loadTexture(expand.getImageLocation(),false);	
	extraShapeButtonTextureIcon.setApply(Texture.AM_MODULATE);
	
 	IconToolTip.setTooltipData(extraShapeButton,new ToolTipData(null, extraShapeButtonTextureIcon,  extraShapeButton.getName()));
	IconToolTip.setDescription(extraShapeButton,  getTooltip("TOOLBAR.MORE_SHAPES.TOOLTIP","Show/Hide additional solids."));

 	extraShapeButton.addToggleListener(new ToggleListener()
 	{

		public void toggle(boolean value) {

	
				extraShapes.layout();
				extraShapes.updateMinSize();
				extraShapes.setVisible(value);
				extraShapes.layout();
		
				extraShapes.updateMinSize();
				extensionContainer.updateMinSize();
				extensionContainer.layout();
				extraShapes.layout();
			
				extensionContainer.setPermeable(false);
				maximizedContainer.layout();
				extraShapes.setSizeToMinSize();
				
	
				maximizedContainer.setWidth(openedWidth + extensionContainer.getMinWidth());
				
				extraShapes.layout();
				extraShapes.updateMinSize();
	
				extraShapes.layout();
		
				extraShapes.updateMinSize();
				extensionContainer.updateMinSize();
				extensionContainer.layout();
				extraShapes.layout();
	
				extraShapes.setSizeToMinSize();
	
			}    		
		
 	});    	
	extraShapes.addMainWidget(extraShapeButton);
 	//container.addWidget(extraShapeButton); 
 	
 	
 	
 	
 	
 	final MainButton axle = new MainButton(	getTooltip("TOOLBAR.AXLE","Axle"),axleTexture, jointGroup);
 	IconToolTip.setDescription(axle,  getTooltip("TOOLBAR.AXLE.TOOLTIP","The two ends of an axle are free to rotate around each other\nalong their main axis.\nAn axle will act as a motor if it is wired to a battery or other signal."));

 	axle.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				AxleInterpreter component = new AxleInterpreter();
				
				preparePhysicalStructure(component);
			
			}    		
 	});    	
 	extraJoints.addMainWidget(axle);
 	container.addWidget(axle);   	
 	
 	final MainButton hinge = new MainButton(	getTooltip("TOOLBAR.HINGE","Hinge"),hingeTexture, jointGroup);
	IconToolTip.setDescription(hinge, getTooltip("TOOLBAR.HINGE.TOOLTIP","The two ends of a hinge can rotate around each other\nalong the axis that runs perpendicular to their main axis.\nA hinge can be powered if it is wired to a battery or other signal."));

 	hinge.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				HingeInterpreter component = new HingeInterpreter();
				
				preparePhysicalStructure(component);
				
			}    		
 	});    	
 	extraJoints.addMainWidget(hinge);
 	container.addWidget(hinge);  
 	
 	final MainButton hydraulic = new MainButton(	getTooltip("TOOLBAR.HYDRAULIC","Hydraulic"), hydraulicTexture, jointGroup);
	IconToolTip.setDescription(hydraulic,  getTooltip("TOOLBAR.HYDRAULIC.TOOLTIP","The two ends of a hinge can move closer and farther apart,\nup to limits that you can set in properties.\nA hydraulic can be powered if it is wired to a battery or other signal."));

 	hydraulic.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				HydraulicInterpreter component = new HydraulicInterpreter();				
				preparePhysicalStructure(component);
				
			}    		
 	});    	
 	extraJoints.addMainWidget(hydraulic);
 	container.addWidget(hydraulic);  
 	
 	
 	final MainButton socket = new MainButton(	getTooltip("TOOLBAR.BALL_SOCKET","Ball And Socket"),ballAndSocket, jointGroup);
	IconToolTip.setDescription(socket,  getTooltip("TOOLBAR.BALL_SOCKET.TOOLTIP","The two ends of a ball and socket are free\nto rotate about each other on all three axes."));

 	socket.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				BallAndSocketInterpreter component = new BallAndSocketInterpreter();				
				preparePhysicalStructure(component);
				
			
			}    		
 	});    	
 	container.addWidget(socket);  
 	
	final MainButton universal = new MainButton(	getTooltip("TOOLBAR.UNIVERSAL","Universal Joint"),ballAndSocket, jointGroup);
	IconToolTip.setDescription(universal,  getTooltip("TOOLBAR.UNIVERSAL.TOOLTIP","A universal joint is a Ball and Socket that\nconstrains rotation on the 3rd axis."));

	universal.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				BallAndSocketInterpreter component = new BallAndSocketInterpreter();	
				component.setUniversalJoint(true);
				preparePhysicalStructure(component);
				
			
			}    		
 	});    	
 	extraJoints.addWidget(universal);  
 	
 	
 	final MainToggleButton extraJointButton = new MainToggleButton(	getTooltip("TOOLBAR.MORE_JOINTS","More Joints"),null,null ,null,0,expand,shrink,1);
	
	Texture extraJointButtonTextureIcon = loadTexture(expand.getImageLocation(),false);	
	extraJointButtonTextureIcon.setApply(Texture.AM_MODULATE);
	
 	IconToolTip.setTooltipData(extraJointButton,new ToolTipData(null, extraJointButtonTextureIcon,  extraJointButton.getName()));
	IconToolTip.setDescription(extraJointButton,  getTooltip("TOOLBAR.MORE_JOINTS.TOOLTIP","Show/Hide additional joints."));

	extraJointButton.addToggleListener(new ToggleListener()
 	{

		public void toggle(boolean value) {

	
				extraJoints.layout();
				extraJoints.updateMinSize();
				extraJoints.setVisible(value);
				extraJoints.layout();
		
				extraJoints.updateMinSize();
				extensionContainer.updateMinSize();
				extensionContainer.layout();
				extraJoints.layout();
			
				extensionContainer.setPermeable(false);
				maximizedContainer.layout();
				extraJoints.setSizeToMinSize();
				
			//	maximizedContainer.setPermeable(true);
				//maximizedContainer.setPermeable(false);
				maximizedContainer.setWidth(openedWidth + extensionContainer.getMinWidth());
				
				extraJoints.layout();
				extraJoints.updateMinSize();
	
				extraJoints.layout();
		
				extraJoints.updateMinSize();
				extensionContainer.updateMinSize();
				extensionContainer.layout();
				extraJoints.layout();
	
				extraJoints.setSizeToMinSize();
	
			}    		
		
 	});    	
	extraJoints.addMainWidget(extraJointButton);
 //	container.addWidget(extraJointButton); 
 	
 	
 	
 	MainButton source = new MainButton(	getTooltip("TOOLBAR.BATTERY","Battery"),sourceTexture, functionalGroup);
	IconToolTip.setDescription(source,  getTooltip("TOOLBAR.BATTERY.TOOLTIP","Batteries are functional componenets: In wire view, you can click and\ndrag on their top port to create a wire that can power motors.\nIn the properties window, you can set batteries to output a\nchanging signal over time."));

 	source.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				
			
				 preparePhysicalStructure(new BatteryInterpreter(),true);
				
				 
			}    		
 	});    
 	extraFunctionals.addMainWidget(source); 
 	container.addWidget(source); 
 	
 	/*MainButton diff = new MainButton("Differentiator",diffTexture, functionalGroup);
 	//IconToolTip.setDescription(diff, (Description.BATTERY));

 	diff.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				
				 Differentiator source = new Differentiator(StateManager.getStructuralMachine());
					
				 preparePhysicalStructure(source,true);
			
				 
			}    		
 	});    	*/
 //	container.addWidget(diff); 
 	
 	
 	
 	MainButton modifier = new MainButton(	getTooltip("TOOLBAR.FUNCTION","Function"),diffTexture, functionalGroup);
 	IconToolTip.setDescription(modifier,  getTooltip("TOOLBAR.FUNCTION.TOOLTIP","Takes a signal and changes it according to a function\nyou can design in the properties window.\nBy connecting a wire to the top input port, you can turn the\nfunction on and off, or have it remember its current signal."));

 	modifier.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				
			
				 preparePhysicalStructure(new ModifierInterpreter(),true);
				
				 
			}    		
 	});    	
	extraFunctionals.addMainWidget(modifier); 
 	container.addWidget(modifier); 

 	MainButton controller = new MainButton(	getTooltip("TOOLBAR.INPUT","Input"), inputTexture, functionalGroup);
	IconToolTip.setDescription(controller,  getTooltip("TOOLBAR.INPUT.TOOLTIP","When your machine is running, this allows\nthe user to control a signal with key presses\nor visual controls."));

	controller.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				
				 preparePhysicalStructure(new InputInterpreter(),true);
					
	
				 
			}    		
 	});    	
 	container.addWidget(controller); 
 	
 	MainButton oscilloscope = new MainButton(	getTooltip("TOOLBAR.OSCILLOSCOPE","Oscilloscope"), oscilloscopeTexture, functionalGroup);
	IconToolTip.setDescription(oscilloscope,  getTooltip("TOOLBAR.OSCILLOSCOPE.TOOLTIP","When your machine is running, oscilloscopes\nwill graph the signal they recieve."));

	oscilloscope.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				
				 preparePhysicalStructure(new OscilloscopeInterpreter(),true);
					
	
				 
			}    		
 	});    	
	//extraFunctionals.addMainWidget(oscilloscope); 
	container.addWidget(oscilloscope); 
 	
 	
 	MainButton touchSensor = new MainButton(	getTooltip("TOOLBAR.TOUCH","Touch Sensor"), touchTexture, functionalGroup);
 	IconToolTip.setDescription(touchSensor,  getTooltip("TOOLBAR.TOUCH.TOOLTIP","A touch sensor is a box that outputs\na signal when it contacts an object."));

 	touchSensor.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				
			
				 preparePhysicalStructure(new TouchSensorInterpreter());
					
				
				 
			}    		
 	});    	
 //	extraFunctionals.addMainWidget(oscilloscope); 
 	container.addWidget(touchSensor); 
 	
 	MainButton distanceSensor = new MainButton(	getTooltip("TOOLBAR.DISTANCE","Distance Sensor"), distanceTexture, functionalGroup);
	IconToolTip.setDescription(distanceSensor,  getTooltip("TOOLBAR.DISTANCE.TOOLTIP","A distance sensor detects when any object enters its 'view.'\nSelect the sensor to see its view shape.\nYou can move, stretch, and rotate the view shape or\nyou can choose a different shape in the properties window."));

 	distanceSensor.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				
				 
				 preparePhysicalStructure(new DistanceSensorInterpreter());
					
				
				 
			}    		
 	});    	
 	extraFunctionals.addMainWidget(distanceSensor);
 	container.addWidget(distanceSensor); 
 
 	MainButton generalSensor = new MainButton(	getTooltip("TOOLBAR.MULTIMETER","Multimeter"), generalSensorTexture, functionalGroup);
	IconToolTip.setDescription(generalSensor,  getTooltip("TOOLBAR.MULTIMETER.TOOLTIP","A box that can detect its position,\nrotation,velocity, or acceleration."));

 	generalSensor.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				
				 preparePhysicalStructure(new GeneralSensorInterpreter());
					
	
				 
			}    		
 	});    	
 	//extraFunctionals.addMainWidget(generalSensor);
 	container.addWidget(generalSensor); 
 	
 	
 	
 	MainButton contact = new MainButton(	getTooltip("TOOLBAR.CONTACT","Contact"), contactTexture, functionalGroup);
	IconToolTip.setDescription(contact,  getTooltip("TOOLBAR.CONTACT.TOOLTIP","Contacts act like electrical conductors - if they touch\nthen signals will pass through them."));

	contact.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				
				 preparePhysicalStructure(new ContactInterpreter());
					
	
				 
			}    		
 	});    	
 	//extraFunctionals.addMainWidget(generalSensor);
 	container.addWidget(contact); 
 	
 	
 	
 	final MainToggleButton extraFunctionalButton = new MainToggleButton(	getTooltip("TOOLBAR.MORE_FUNCTIONAL","More Functional Components"),null,null ,null,0,expand,shrink,1);
	
	Texture extraFunctionalButtonTextureIcon = loadTexture(expand.getImageLocation(),false);	
	extraFunctionalButtonTextureIcon.setApply(Texture.AM_MODULATE);
	
 	IconToolTip.setTooltipData(extraFunctionalButton,new ToolTipData(null,extraFunctionalButtonTextureIcon,  extraFunctionalButton.getName()));
	IconToolTip.setDescription(extraFunctionalButton,  getTooltip("TOOLBAR.MORE_FUNCTIONAL.TOOLTIP","Show/Hide additional functional components."));
	
 	//MainButton extraShapeButton = new MainButton("+",squareTexture,shapeGroup);
	extraFunctionalButton.addToggleListener(new ToggleListener()
 	{

		public void toggle(boolean value) {
				extraFunctionals.layout();
				extraFunctionals.updateMinSize();
				extraFunctionals.setVisible(value);
				extraFunctionals.layout();
		
				extraFunctionals.updateMinSize();
				extensionContainer.updateMinSize();
				extensionContainer.layout();
			
				extensionContainer.setPermeable(false);
				maximizedContainer.layout();
				extraFunctionals.setSizeToMinSize();
				
				maximizedContainer.setWidth(openedWidth + extensionContainer.getMinWidth());

				extraFunctionals.layout();
				extraFunctionals.updateMinSize();
	
				extraFunctionals.layout();
		
				extraFunctionals.updateMinSize();
				extensionContainer.updateMinSize();
				extensionContainer.layout();
				extraFunctionals.layout();
	
				extraFunctionals.setSizeToMinSize();
	
			}    		
 	});    	
 	extraFunctionals.addMainWidget(extraFunctionalButton);
 	//container.addWidget(extraFunctionalButton); 

 	/*
 	MainButton lightSensor = new MainButton("Light Sensor", lightSensorTexture, functionalGroup);
 	lightSensor.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				
				 LightSensor source = new LightSensor(StateManager.getStructuralMachine());
					
				 preparePhysicalStructure(source);
					
					
			}    		
 	});    	
 // container.addWidget(lightSensor); 
 	


 	final MainButton light = new MainButton("LightSource", 	lightTexture, specialGroup);
 	light.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				PhysicalStructure structure = new PointLightStructure(StateManager.getStructuralMachine());
				preparePhysicalStructure(structure);
				
			
			}    		
 	});    	
 	//container.addWidget(light); 
 	*/
 	MainButton rocket = new MainButton(	getTooltip("TOOLBAR.ROCKET","Rocket"),rocketTexture, specialGroup);
	IconToolTip.setDescription(rocket,  getTooltip("TOOLBAR.ROCKET.TOOLTIP","A rocket will output a force proportional to its signal.\n(Wire a battery to the rocket to create a signal.)\nYou can adjust the maximum force in properties."));

 	rocket.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				
				
				 preparePhysicalStructure(new RocketInterpreter());
					
			
				 
			}    		
 	});    	
 	container.addWidget(rocket); 
 	
 	MainButton gear = new MainButton(	getTooltip("TOOLBAR.GEAR","Gear"),gearTexture, specialGroup);
 	IconToolTip.setDescription(gear,  getTooltip("TOOLBAR.GEAR.TOOLTIP","Create a gear."));

	gear.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				
				
				 preparePhysicalStructure(new GearInterpreter());
					
			
				 
			}    		
 	});    	
 	container.addWidget(gear); 
 	
 	
 	MainButton rackGear = new MainButton(	getTooltip("TOOLBAR.RACK_GEAR","Rack Gear"),rackGearTexture, specialGroup);
 	IconToolTip.setDescription(rackGear,  getTooltip("TOOLBAR.RACK_GEAR.TOOLTIP","Create a linear gear."));

 	rackGear.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				
				
				 preparePhysicalStructure(new RackGearInterpreter());
					
			
				 
			}    		
 	});    	
 	container.addWidget(rackGear); 
 	
 	MainButton grapple = new MainButton(	getTooltip("TOOLBAR.FORCE_BEAM","Force Beam"),grappleTexture, specialGroup);
 	IconToolTip.setDescription(grapple,  getTooltip("TOOLBAR.FORCE_BEAM.TOOLTIP","Projects a beam of forces that can pull or push objects."));

 	grapple.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				
			
					
				 preparePhysicalStructure(new GrappleInterpreter(),false);
				
	
				 
			}    		
 	});    	
 	container.addWidget(grapple); 
 	
 	MainButton camera = new MainButton(	getTooltip("TOOLBAR.CAMERA","Camera"),cameraTexture, specialGroup);
 	IconToolTip.setDescription(camera,  getTooltip("TOOLBAR.CAMERA.TOOLTIP","Cameras let you see the world from their view.\nPress $CAMERA_NEXT to switch your current camera.\nCameras are only visible in wire mode."));

 	camera.addButtonListener(new ButtonAdapter()
 	{
			
			public void activate() {
				
			
					
				 preparePhysicalStructure(new CameraInterpreter(),true);
				
	
				 
			}    		
 	});    	
 	container.addWidget(camera); 
 	
	extraShapes.setChildTooltipWidget(toolTipWidget);
 	extraFunctionals.setChildTooltipWidget(toolTipWidget);
 	extraJoints.setChildTooltipWidget(toolTipWidget);
 	extraActives.setChildTooltipWidget(toolTipWidget);
 	
 	
 	//StateManager.getLogger().fine("Main Toolbar: arranging main buttons");
 	//this.addWidget(toolTipWidget);
	for(IWidget widget1:container.getWidgets())
 	{
 		if(widget1 instanceof MainButtonGroup)
 		{
		 	for (IWidget widget2:((MainButtonGroup)widget1).getWidgets())
		 	{
		 		
		 		buttons.add(widget2);
		 		widget2.setShowToolTip(true);
		 		widget2.setTooltipWidget(toolTipWidget);
		 		
		 	}
 		}else
 		{
 				
	 			buttons.add(widget1);
	 			widget1.setShowToolTip(true);
		 		widget1.setTooltipWidget(toolTipWidget);
 		}
 	}
 	container.setLayoutManager(new VerticalSquareGridLayout(){

		@Override
		public void doLayout(IWidget c, List<IWidget> content) {
			ArrayList<IWidget> mainButtons = new ArrayList<IWidget>();
		 
			for(IWidget widget1:container.getWidgets())
		 	{
				if(widget1 instanceof MainButton || widget1 instanceof MainToggleButton)
						mainButtons.add(widget1);
		 	}
		//	StateManager.getLogger().fine("Main Toolbar: building main button layout");
			int columns;
			int rows = 2;
			int buttonSize =(int) mainButtons.get(0).getHeight();
			float contentHeight = (float) GeneralSettings.getInstance().getResolution().getValue().getHeight()- 64; // container.getHeight();// (float)container.getSpacingAppearance().getContentHeight();
			int maxRows = (int)  Math.floor(contentHeight/(float)buttonSize) ;
			
			for(columns =2;columns<6;columns++ )
			{
				rows = (int)Math.ceil((mainButtons.size())/((float)columns));
				if(rows<=maxRows)
					break;		
			}
			super.setColumns(columns);
			super.setRows(rows);
			super.doLayout(c, content);
		}
 		
 	});
 	
 	container.layout();
 	/*layoutContent(container);
 	layoutContent(container);*/
 	StateManager.getLogger().fine("Main Toolbar: finish laying out main buttons");
	}
	
/*	private void layoutContent(Container container) {
	
		
	 	container.setLayoutManager(new VerticalSquareGridLayout(rows,columns));
	 	StateManager.getLogger().fine("Main Toolbar: layout main buttons");
	 //	container.setHeight(Math.min(container.getSpacingAppearance().getContentHeight(), rows * container.getSpacingAppearance().getContentWidth()/2));
	 	container.layout();
	}
*/
	private void preparePhysicalStructure(StructureInterpreter interpreter)
	{
		preparePhysicalStructure(interpreter,false);
	}
 
	private void preparePhysicalStructure(StructureInterpreter interpreter, boolean functional)
	{
		StateManager.getStructuralMachine().getLayerRepository().validateActiveLayer();
		if(StateManager.getStructuralMachine().getLayerRepository().getActiveLayer().isLocked())
		{
			return;
		}
		
		DefaultGolemsProperties.getInstance().setToDefault(interpreter.getStore());
		GolemsValidator.getInstance().makeValid(interpreter.getStore());
		 StateManager.getToolManager().setPrimaryTool(StateManager.getToolPool().getMovementTool());
		// interpreter.getLocalTranslation().y += 2;
		 
		 interpreter.getLocalTranslation().set(StateManager.getConstructionManager().getBuildLocation());
		 
		 interpreter.setLayer(StateManager.getStructuralMachine().getLayerRepository().getActiveLayer().getID());
		 
		 try {
			AddComponentAction addComponent =( AddComponentAction) StateManager.getStructuralMachine().getAction(Action.ADD_COMPONENT);
			addComponent.setComponent(interpreter.getStore());
			if(addComponent.doAction())
				UndoManager.getInstance().addAction(addComponent);
		 } catch (ActionTypeException e) {
			StateManager.logError(e);
		}
		 
		 
		 ActionToolSettings.getInstance().getWireMode().setValue(functional);
		if (functional)
		{
			
			
			//StateManager.getViewManager().setViewMode(ViewMode.FUNCTIONAL );	
			//StateManager.getStructuralMachine().addViewMode(ViewMode.FUNCTIONAL );
			//StateManager.getStructuralMachine().removeViewMode(ViewMode.MATERIAL );
			//StateManager.getStructuralMachine().removeViewMode(ViewMode.GROUPMODE );
		}else
		{
			
			//StateManager.getViewManager().setViewMode(ViewMode.MATERIAL );	
			//StateManager.getStructuralMachine().addViewMode(ViewMode.MATERIAL );
			//StateManager.getStructuralMachine().removeViewMode(ViewMode.GROUPMODE );			
			//StateManager.getStructuralMachine().removeViewMode(ViewMode.FUNCTIONAL );			
		}
	//	prepare.addViewModes(StateManager.getViewManager().getViews());
	//	prepare.getModel().getLocalTranslation().y += 2.0f;
/*		try{
			CreateAction create = (CreateAction) prepare.getAction(Action.CREATE);
			create.doAction();
			UndoManager.getInstance().addAction(create);
		}catch(ActionTypeException e)
		{
			StateManager.logError(e);
		}*/
		
	}
	
	
    
    private Updatable fadeAnimation = new Updatable()
	{
    	
    	private float maxAlpha =0.2f;
    	private float minAlpha = 0f;
    	private float hideAlpha = 0;
    	private float speed = 0.2f;
    	private boolean hidden = false;
    	

		public void update(float time) 
		{
			ColorRGBA color = textureColor;
			MaterialState state = textureMaterial;
			
			if (engaged || !GeneralSettings.getInstance().getAutoCloseMainMenu().isValue())
			{
				buttonColor.a = 1f;
				color.a += 4f*time*speed;
				if(color.a >=maxAlpha)				
					color.a = maxAlpha;
				
				//state.setAmbient(color);
				state.setDiffuse(color);
				//state.setSpecular(color);
				
				//buttonMaterial.setAmbient(buttonColor);				
				//buttonMaterial.setSpecular(buttonColor);
				buttonMaterial.setDiffuse(buttonColor);
				if(color.a == maxAlpha)				
					UpdateManager.getInstance().remove(this);
				
			}else
			{
				
				color.a -= 2f*time*speed;
				if(color.a <minAlpha)				
					color.a = minAlpha;
				
				//state.setAmbient(color);
				state.setDiffuse(color);
				//state.setSpecular(color);
				buttonColor.a = color.a;
				buttonMaterial.setDiffuse(buttonColor);
				if(color.a == minAlpha)				
					UpdateManager.getInstance().remove(this);
			}
			
		
			
			
			
			if (color.a <= hideAlpha)
			{
				if (! hidden)
				{
					hidden = true;
					backpanelTexture.setCullMode(SceneElement.CULL_ALWAYS);
					getSpatial().setCullMode(SceneElement.CULL_ALWAYS);
					//updateRenderState();
				}
			}else if (hidden)
			{
				hidden = false;
				//attachChild(backpanelTexture);
				backpanelTexture.setCullMode(SceneElement.CULL_NEVER);
				getSpatial().setCullMode(SceneElement.CULL_NEVER);
				//updateRenderState();
			}
			
		}
	};
	
	private Updatable newSlideAnimation = new Updatable()
	{
		private float slideSpeed = 1000;
		
	
		
		public void update(float time) {
			
			slideLock.lock();
			try{
				if(panelOpened == true)
				{
	
					UpdateManager.getInstance().remove(this);
				}else
				{
					if(getWidth()> minimizedContainer.getMinWidth())
					{
						
						float newWidth  = (float) (getWidth()-slideSpeed*time);
						if(newWidth < minimizedContainer.getMinWidth())
							newWidth =minimizedContainer.getMinWidth();
						
						setWidth(newWidth);
					}else
					{
						setWidth(minimizedContainer.getMinWidth());
						backpanelTexture.setCullMode(SceneElement.CULL_ALWAYS);
						UpdateManager.getInstance().remove(this);
					}
				}
			}finally
			{
				slideLock.unlock();
			}
		}
		
	};
			
	private Updatable slideAnimation = new Updatable()
	{

		
		public void update(float time) {
		Vector3f scale = MainToolbar.this.getSpatial().getLocalScale();
		Vector3f old = new Vector3f(scale);
		if (engaged || !GeneralSettings.getInstance().getAutoCloseMainMenu().isValue())
		{
			if (scale.x < 1)
				scale.x += 0.1f;
			
			if (scale.x >= 1)			
				scale.x = 1;
			 
			Vector3f diff = old.subtractLocal(scale).divideLocal(2f);
			diff.x *= getWidth();
			MainToolbar.this.getSpatial().getLocalTranslation().subtractLocal(diff);
			
			
			if (scale.x == 1)
			{
				//attachChild(getSpatial());//show getSpatial()		
				UpdateManager.getInstance().remove(this);
			}			
		}else
		{
			if (scale.x > minScale)
				scale.x -= 0.1f;
		
			if (scale.x <= minScale)			
				scale.x = minScale;
			
			Vector3f diff = old.subtractLocal(scale).divideLocal(2f);
			diff.x *= getWidth();
			MainToolbar.this.getSpatial().getLocalTranslation().subtractLocal(diff);
			
			if (scale.x <= minScale)
			{
				scale.x = minScale;
				
				
				
				UpdateManager.getInstance().remove(this);
			}
		}
			
		}
		
	};
    
}
