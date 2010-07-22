package com.golemgame.states.physics.ode;

import java.awt.Dimension;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.golemgame.audio.AudioSource;
import com.golemgame.constructor.Updatable;
import com.golemgame.constructor.UpdateManager;
import com.golemgame.constructor.UpdateManager.Stream;
import com.golemgame.instrumentation.InstrumentationLayer;
import com.golemgame.local.StringConstants;
import com.golemgame.model.spatial.GuardedNodeModel;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.physical.PhysicsInteractionManager;
import com.golemgame.physical.PhysicsWorld;
import com.golemgame.physical.SpringController;
import com.golemgame.physical.ode.sound.OdeSoundManager;
import com.golemgame.properties.fengGUI.MessageBox;
import com.golemgame.settings.SettingChangedEvent;
import com.golemgame.settings.SettingsListener;
import com.golemgame.states.Controllable;
import com.golemgame.states.GUILayer;
import com.golemgame.states.GeneralSettings;
import com.golemgame.states.ShutdownManagerState;
import com.golemgame.states.StateEventDispatch;
import com.golemgame.states.StateListener;
import com.golemgame.states.StateManager;
import com.golemgame.states.physics.IllegalTransitionException;
import com.golemgame.states.physics.PhysicsMonitorListener;
import com.golemgame.toolbar.ButtonAdapter;
import com.golemgame.toolbar.StandardToolbar;
import com.golemgame.toolbar.main.MainToolbar;
import com.golemgame.toolbar.option.OptionButton;
import com.golemgame.toolbar.physics.PhysicsButtons;
import com.golemgame.toolbar.recording.RecordingButtons;
import com.golemgame.toolbar.sound.SoundControls;
import com.golemgame.toolbar.tooltip.IconToolTip;
import com.golemgame.toolbar.tooltip.ToolTipData;
import com.golemgame.util.ManagedThread;
import com.golemgame.util.input.InputLayer;
import com.golemgame.util.input.ListenerGroup;
import com.golemgame.util.loading.ConcurrentLoadable;
import com.golemgame.util.loading.Loadable;
import com.golemgame.util.loading.Loader;
import com.golemgame.util.pass.PassingGameStateManager;
import com.jme.image.Texture;
import com.jme.input.MouseInputListener;
import com.jme.light.PointLight;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.physics.PhysicsDebugger;
import com.simplemonkey.Container;
import com.simplemonkey.layout.BorderLayout;
import com.simplemonkey.layout.BorderLayoutData;
import com.simplemonkey.layout.RowLayout;
import com.simplemonkey.tooltip.ToolTipWidget;

public class OdePhysicsState extends FunctionalPhysicsState  implements Controllable,ManagedThread,  ConcurrentLoadable {

	public OdePhysicsState() {
		super("functional");
		ShutdownManagerState.getInstance().addManagedThread(this);
	
		initState();

		rootModel = new NodeModel();
		this.getPhysicsRoot().attachChild(rootModel.getSpatial());
    	
		PassingGameStateManager.getInstance().attachChild(this);
		this.setActive(false);
	
	}
	
	
	protected PhysicsWorld physicsWorld= null;
	protected PhysicsInteractionManager interactionManager= null;
	protected SpringController spring = null;
	//private CameraTool cameraTool;
	private Node hudNode;
	private StandardToolbar mainInterface;
	private Renderer renderer;
	
	private ListenerGroup listeners = new ListenerGroup();

	private NodeModel rootModel;
	
	private InstrumentationLayer instruments;
//	private InstrumentationControls instrumentControls;
//	private SensorControls sensorControls;
	private OdePhysicsController controller;
	private OdeSoundManager soundManager;
	private AudioSource physicsSource;
	

	public void setSoundManager(OdeSoundManager odeSoundManager) {
		this.soundManager = odeSoundManager;
	}
	
	
	public OdePhysicsController getController() {
		return controller;
	}

	public NodeModel getRootModel() {
		
		return rootModel;
	}

	private final AtomicBoolean physicsDied = new AtomicBoolean();
	private void forcePhysicsEnd(boolean endedOnError)
	{
		if(physicsDied.compareAndSet(false, true))
		{
			//something fails with this locking, that allows for a null pointer exception from physics update.
			OdePhysicsState replacement;
			StateManager.lockFunctional();
			try{//Note: All this locking is needed, to prevent the opengl thread from calling physics
				//nodes update or render cycles while they are being deleted.
				//it might also work if the physics root node is disconnected from its parent.
			returnToDesign();
			StateManager.getGame().lock();
				try{
				super.delete();
				}finally
				{
					StateManager.getGame().unlock();
				}
				replacement = OdePhysicsState.createLoadableTestingState();
		//	StateManager.setFunctionalState(replacement);
			}finally
			{
				StateManager.unlockFunctional();
			}
		
			
			
			
			
			if (endedOnError)
				MessageBox.showMessageBox("Error", "It seems that the physics engine crashed. This is usually caused by having a very complicated machine, or one with lots of joints."  , GUILayer.getLoadedInstance().getDisplay());
			
			try {
				replacement.load();
				
				StateManager.getGame().lock();
				try{
				PassingGameStateManager.getInstance().attachChild(replacement);
				}finally{
		        StateManager.getGame().unlock();}
			} catch (Exception e) {
				
				StateManager.logError(e);	
				GUILayer.displayMessage("Error", "Physics may not have restarted properly: " + e);
	
				
			}
		}else
		{
			StateManager.logError(new RuntimeException("physics killed twice"));
		}
	}
	
	private void returnToDesign()
	{
		//this fails to return to design mode. (perhaps now it works?)
		remove();
		StateManager.setCurrentGameState(StateManager.getDesignState());
		StateManager.getDesignState().init();
	}
	
	
	public void update(float time) {

		
		super.update(time);
		UpdateManager.getInstance().update(time,UpdateManager.Stream.GL_UPDATE);
		hudNode.updateGeometricState(time, true);
		if(instruments != null)
			instruments.update(time);
		
	}

	
	public void render(float tpf) {
		UpdateManager.getInstance().update(tpf,UpdateManager.Stream.GL_RENDER);
		super.render(tpf);
		

	//	PhysicsDebugger.drawPhysics( getPhysicsSpace(), DisplaySystem.getDisplaySystem().getRenderer());
		if(instruments != null)
			instruments.render(tpf);
		renderer.draw(hudNode);
	}


	
	public void init() 
	{		

	
		
	
		{

				
				OdePhysicsState.this.setActive(true);
				OdePhysicsState.this.lock();
				try{
	
					//new: all locking in this class will try() timeout and throw an exception.
				StateManager.getGame().lock();//this can get stuck on locking... why?
					try{
						GUILayer.releaseFocus();
					StateManager.getToolManager().setSelectionEnabled(false);
					StateManager.getCameraManager().setRootNode(rootNode);
					StateManager.getCameraManager().getSkyBoxManager().setEnabled(true);
					StateManager.getCameraManager().getSkyBoxManager().setOverlayEnabled(false);
					//rootNode.attachChild(StateManager.getToolPool().getCameraTool().getPivotNode());
			  
					UpdateManager.getInstance().add(new Updatable(){

						public void update(float time) {
							StateManager.getCameraManager().update();
						}
						
						
					}, Stream.PHYISCS_UPDATE);
					
					UpdateManager.getInstance().add(new Updatable(){
						private int updateCount = 0;
						public void update(float time) {
							if(StateManager.getRecordingManager().isRecording())// && )
							{
								try {
									StateManager.getGame().executeInGL(new Callable<Object>(){
										
										public Object call() throws Exception {
											//if(StateManager.getRecordingManager().getTimeSinceLastFrame()>StateManager.getRecordingManager().getDesiredPeriod())
											updateCount++;
											updateCount %= OdePhysicsState.this.getUpdatesPerSecond();
											int updateRatio =(int)  OdePhysicsState.this.getUpdatesPerSecond()/(int)StateManager.getRecordingManager().getDesiredFPS();
											if((updateCount%updateRatio == 0) )											
												StateManager.getRecordingManager().drawFrame();
											return null;
										}
									

									});
								} catch (Exception e) {
									StateManager.logError(e);
								}
							}
						}
						
					}, Stream.PHYISCS_UPDATE);
			        //camera.update();

			        StateManager.setRootModel(rootModel);
					setListening(true);
					mainInterface.layout();
					}catch(Exception e)
					{
						StateManager.logError(e);
					}finally
					{
						StateManager.getGame().unlock();
					}
				}catch(Exception e)
				{
					StateManager.logError(e);
				}
				finally{					
					OdePhysicsState.this.unlock();					
				}
				
 
			}
	
	
	}
	
	
	public void setListening(boolean listening) 
	{
		
		if (listening)
		{
	
			InputLayer.get().addKeyListener(listeners,InputLayer.STATE_LAYER);
			InputLayer.get().addMouseListener(listeners,InputLayer.STATE_LAYER);
			
			InputLayer.get().addMouseListener(mainInterface,InputLayer.STATE_LAYER - 4);
			InputLayer.get().addKeyListener(mainInterface,InputLayer.STATE_LAYER - 4);
			
		}else
		{
		
			InputLayer.get().removeKeyListener(listeners,InputLayer.STATE_LAYER);
			InputLayer.get().removeMouseListener(listeners,InputLayer.STATE_LAYER);
			
			InputLayer.get().removeMouseListener(mainInterface,InputLayer.STATE_LAYER - 4);
			InputLayer.get().removeKeyListener(mainInterface,InputLayer.STATE_LAYER - 4);

		}

	}
	

	private void initState()
	{    	
		
		super.addMonitorListener(new PhysicsMonitorListener()
		{
			private ReentrantLock frozenLock = new ReentrantLock();
			private Condition frozenCondition = frozenLock.newCondition();
			private volatile boolean frozen = false;
			private Loadable frozenLoadable = new Loadable(true)
			{

				
				public void load() throws Exception {
				
					frozenLock.lock();
					try{
					if (frozen)
						frozenCondition.await();
					}finally
					{
						frozenLock.unlock();
					}
				}
				
			};

			
			public void frozen() {
				System.out.println("Physics frozen");
				frozenLock.lock();
				try{
					frozen = true;
				}finally
				{
					frozenLock.unlock();
				}
				
				Loader.getInstance().queueLoadable(frozenLoadable);
			
			}

			
			public void physicsDied(Throwable cause) {
				System.out.println("Physics died");
				frozenLock.lock();
				try{
					frozen = false;
					frozenCondition.signalAll();
					forcePhysicsEnd(true);
				}finally
				{
					frozenLock.unlock();
				}
				
			}

			
			public void unfrozen() {
				frozenLock.lock();
				try{
					frozen = false;
					frozenCondition.signalAll();
				}finally
				{
					frozenLock.unlock();
				}
				
			}
			
		});
		controller = new OdePhysicsController(this);
		instruments = new InstrumentationLayer();
		/*instrumentControls = new InstrumentationControls(instruments);
		sensorControls = new SensorControls();*/
		
		PhysicsSpatialMonitor.getInstance().setPhysics(super.getPhysicsSpace());
		
		buildHudNode();
		renderer = DisplaySystem.getDisplaySystem().getRenderer();

		CullState cs = display.getRenderer().createCullState();
		cs.setCullMode(CullState.CS_BACK);
		rootNode.setRenderState(cs);

		PointLight light;
		
		LightState lightState;

		light = new PointLight();
        light.setDiffuse( new ColorRGBA(0.75f, 0.75f, 0.75f, 1.0f  ) );
        light.setAmbient( new ColorRGBA( 0.75f, 0.75f, 0.75f, 1.0f ) );
        light.setLocation( new Vector3f( -10, 20, 10 ) );
		light.setEnabled( true );

		
		lightState = display.getRenderer().createLightState();
		lightState.setEnabled( true );
		 lightState.setTwoSidedLighting(false);
		lightState.attach( light );
		rootNode.setRenderState( lightState );
		rootNode.updateWorldBound();

		rootNode.updateRenderState();
		rootNode.updateGeometricState(0f,true);


		buildPhysicsRoot();
	
		
		this.listeners.addMouseListener(new MouseInputListener()
		{
			boolean isLeft;
			boolean isRight;
			public void onButton(int button, boolean pressed, int x, int y) {
				if(button == 0)
					isLeft = pressed;
				else if (button ==1)
					isRight = pressed;
				if(button == 0 && pressed && (spring != null))
				{
					
					PhysicsComponent comp = interactionManager.pickDynamicPhysics(new Vector2f(x,y));
					if(comp!=null && !comp.getParent().isStatic())
					{
						spring.setTarget(comp.getParent());
						 StateManager.getToolPool().getCameraTool().mouseButton(button, false, x, y);
					}else
					{
						spring.setTarget(null);
						 StateManager.getToolPool().getCameraTool().mouseButton(button, pressed, x, y);
					}
				}else if(button ==0 &! pressed)
				{
					if(spring != null)
						spring.setTarget(null);
					
					 StateManager.getToolPool().getCameraTool().mouseButton(button, pressed, x, y);
				}else
				{
					 StateManager.getToolPool().getCameraTool().mouseButton(button, pressed, x, y);
				}
			}

			public void onMove(int delta, int delta2, int newX, int newY) {
				if(spring!=null)
					spring.setPosition(newX,newY);
				 StateManager.getToolPool().getCameraTool().mouseMovementAction(new Vector2f(newX,newY), isLeft, isRight);
			}

			public void onWheel(int wheelDelta, int x, int y) {
				if(spring!=null)
					spring.setPosition(x,y);
				StateManager.getToolPool().getCameraTool().scrollMove(wheelDelta, x, y);
			}
			
		});
		
	
		physicsSource = StateManager.getAudioManager().createAudioSource();
		
		GeneralSettings.getInstance().getShowInstruments().addSettingsListener(new SettingsListener<Boolean>(){

			public void valueChanged(SettingChangedEvent<Boolean> e) {
				instruments.setVisible(e.getNewValue());
			}
			
		}, true);

	}
	
	public OdeSoundManager getSoundManager() {
		return soundManager;
	}

	public InstrumentationLayer getInstruments() {
		return instruments;
	}


	private void buildPhysicsRoot()
	{

		CullState cs = display.getRenderer().createCullState();
        cs.setCullMode(CullState.CS_BACK);
        getPhysicsRoot().setRenderState(cs);
        
		ZBufferState buf = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.CF_LEQUAL);
        getPhysicsRoot().setRenderState(buf);
        
        
        PointLight light;

        LightState lightState;


		light = new PointLight();
        light.setDiffuse( new ColorRGBA(0.75f, 0.75f, 0.75f, 1.0f  ) );
        light.setAmbient( new ColorRGBA( 0.75f, 0.75f, 0.75f, 1.0f ) );
        light.setLocation( new Vector3f( -10, 20, 10 ) );
		light.setEnabled( true );

        /* Attach the light to a lightState and the lightState to rootNode. */
        lightState = display.getRenderer().createLightState();
        
        lightState.setEnabled( true );
        lightState.setTwoSidedLighting(false);
        lightState.attach( light );
        getPhysicsRoot().setRenderState( lightState );
       // getPhysicsRoot().updateWorldBound();

        getPhysicsRoot().updateRenderState();
   //     getPhysicsRoot().updateGeometricState(0f,true);
	}

	private void buildHudNode()
	{		
		hudNode = new Node();
		hudNode.setZOrder(-1);	
		
		
		final IconToolTip tooltip = new IconToolTip();
		
		GeneralSettings.getInstance().getShowToolTips().addSettingsListener(new SettingsListener<Boolean>()
				{

					public void valueChanged(SettingChangedEvent<Boolean> e) {
						tooltip.setEnabled(e.getNewValue());
						
					}
					
				},true);
		
		mainInterface = new StandardToolbar();//later: merge this with toolbar
		mainInterface.setWidth(DisplaySystem.getDisplaySystem().getWidth());
		mainInterface.setHeight(DisplaySystem.getDisplaySystem().getHeight());
	//	mainInterface.getLocalTranslation().set(DisplaySystem.getDisplaySystem().getWidth()/2f,DisplaySystem.getDisplaySystem().getHeight()/2f,0);
		hudNode.attachChild(mainInterface.getSpatial());		
		GeneralSettings.getInstance().getResolution().addSettingsListener(new SettingsListener<Dimension>()
				{
					
					public void valueChanged(SettingChangedEvent<Dimension> e) {
							mainInterface.setSize(e.getNewValue().width, e.getNewValue().height);
							mainInterface.layout();
					}
				},true);
		buildMainMenu(hudNode,tooltip);
		
		buildStatusBar(hudNode,tooltip);
		
		tooltip.getSpatial().updateRenderState();
		
		ZBufferState zbs = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
		zbs.setEnabled(true);
		zbs.setFunction( ZBufferState.CF_NOTEQUAL  );//apparently, using this or CF_ALWAYS will cause the hud to be drawn ontop of everything, properly
		zbs.setWritable(false);
		
		  CullState cs = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
	        cs.setCullMode(CullState.CS_BACK);
	        hudNode.setRenderState(cs);

		hudNode.setRenderState(zbs);
		hudNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		
        hudNode.updateGeometricState(0, true);
		hudNode.updateRenderState();
		
		//have to add the tooltip last to render properly over top of other components... this is a minor hack.
		mainInterface.addWidget(tooltip);
	}

	/*public void showSensorControls(boolean show)
	{
		try{
		sensorControls.setVisible(show);
		}catch(IndexOutOfBoundsException e)
		{
			//this is ok probably... some minor threading error.
		}
	}
	
	public void showInstrumentControls(boolean show)
	{
		try{
		instrumentControls.setVisible(show);
		}catch(IndexOutOfBoundsException e)
		{
			//this is ok probably... some minor threading error.
		}

	}*/
	
	private void buildMainMenu(Node hudNode,IconToolTip tooltip)
	{
		//Problem: buttonGroups do not automatically take on any particular position
		//this means that if you dont position them manually, then even if their sub-buttons are positions
		//collision detection will fail for the button group if there are multiple of them.
		mainInterface.setLayoutManager(new BorderLayout());
		Container topBar = new Container();
		mainInterface.addWidget(topBar);
	
		
		topBar.setLayoutData(BorderLayoutData.NORTH);
		topBar.setLayoutManager(new BorderLayout());
		Container topRight = new Container();
		topBar.addWidget(topRight);
		topRight.setLayoutData(BorderLayoutData.EAST);
		topRight.setLayoutManager(new BorderLayout());
		
		Container topRightBar = new Container();
		topRight.addWidget(topRightBar);
		topRightBar.setLayoutData(BorderLayoutData.CENTER);
		topRightBar.setLayoutManager(new RowLayout());
		
		
		
		Container recordContainer = new Container();
		recordContainer.setLayoutManager(new BorderLayout());
		recordContainer.setLayoutData(BorderLayoutData.CENTER);
		topRight.addWidget(recordContainer);
		
	
		
		RecordingButtons recordingControls = new RecordingButtons();
		recordingControls.setLayoutData(BorderLayoutData.NORTH);
		recordContainer.addWidget(recordingControls);
		
	//	lineContainer.addWidget(instrumentControls);
		
		
		//Back to design mode
		
		Container lineContainer = new Container();
		lineContainer.setLayoutData(BorderLayoutData.EAST);
	//	recordContainer.addWidget(lineContainer);
		lineContainer.setLayoutManager(new RowLayout());
		topRight.addWidget(lineContainer);
		
		/*instrumentControls.setLayoutData(BorderLayoutData.CENTER);
		lineContainer.addWidget(instrumentControls);
		
		sensorControls.setLayoutData(BorderLayoutData.CENTER);
		lineContainer.addWidget(sensorControls);*/
		
		PhysicsButtons physTools = new PhysicsButtons(tooltip);
		lineContainer.addWidget(physTools);
		physTools.setLayoutData(BorderLayoutData.EAST);
		
	//	mainInterface.setLayoutManager(new BorderLayout());
		//mainInterface.layout();
		
		
		physTools.setStopAction(new Runnable()
		{

			
			public void run() {
				StateManager.getThreadPool().execute(new Runnable()
				{
					
					public void run() {
						
						try {
							controller.cleanup();
							returnToDesign();
						} catch (IllegalTransitionException e) {
							StateManager.logError(e);
							forcePhysicsEnd(true);
						}
						GeneralSettings.getInstance().getPhysicsOverideSpeed().setValue( Float.NaN);
					
				
					
					//	forcePhysicsEnd(false);
					}					
				});	
			}
			
		});
		
		physTools.setRunAction(new Runnable()
		{

			
			public void run() {
				StateManager.getThreadPool().execute(new Runnable()
				{
					
					public void run() {
						GeneralSettings.getInstance().getPhysicsOverideSpeed().setValue( Float.NaN);

					}					
				});	
			}
			
		});
		
		physTools.setPauseAction(new Runnable()
		{

			
			public void run() {
				StateManager.getThreadPool().execute(new Runnable()
				{
					
					public void run() {
						GeneralSettings.getInstance().getPhysicsOverideSpeed().setValue( Float.NEGATIVE_INFINITY);
			
					}					
				});	
			}
			
		});
/*		
		Container eastBar = new Container();
	
		
		eastBar.setLayoutData(BorderLayoutData.NORTH);
		*/
		//SimpleButton controls= new SimpleButton("Mute");
		if(StateManager.SOUND_ENABLED)
		{
			SoundControls controls = new SoundControls();
			controls.setLayoutData(BorderLayoutData.EAST);
			mainInterface.addWidget(controls);
		}
	//	eastBar.addWidget(controls);

		mainInterface.layout();
	//	controls.layout();
	}
	private Texture loadTexture(String path)
	{
	 	
		return  TextureManager.loadTexture(
	  			MainToolbar.class.getClassLoader().getResource(
	  		            "com/golemgame/data/textures/" + path),
		            Texture.MM_LINEAR_NEAREST,
		            Texture.FM_LINEAR);
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
	
		
		final OptionButton showInstruments =  new OptionButton(StringConstants.get("STATUSBAR.INSTRUMENTS","Instruments"));
		showInstruments.setTooltipWidget(tooltip);
		Texture instrumentTexture = loadTexture("buttons/functionals/Oscilloscope.png");
		instrumentTexture.setApply(Texture.AM_MODULATE);
		IconToolTip.setTooltipData(showInstruments,new ToolTipData(instrumentTexture,StringConstants.get("STATUSBAR.INSTRUMENTS.LONG","Show Instruments")));
		IconToolTip.setDescription(showInstruments,StringConstants.get("STATUSBAR.INSTRUMENTS.TOOLTIP","Show machine instruments (oscilloscopes, input fields, etc)."));
		
		
		showInstruments.addButtonListener(new ButtonAdapter()
		{
			public void activate() {
				GeneralSettings.getInstance().getShowInstruments().setValue(showInstruments.isValue());
				//instruments.setVisible(showInstruments.isValue());
			}			
		});
		
		
		GeneralSettings.getInstance().getShowInstruments().addSettingsListener(new SettingsListener<Boolean>(){

			public void valueChanged(SettingChangedEvent<Boolean> e) {
				showInstruments.setValue(e.getNewValue());
			}
			
		}, true);
		
		final OptionButton showSensors =  new OptionButton(StringConstants.get("STATUSBAR.SENSORS","Sensors"));
		showSensors.setTooltipWidget(tooltip);
		Texture sensorTexture = loadTexture("buttons/functionals/Distance.png");
		sensorTexture.setApply(Texture.AM_MODULATE);
		IconToolTip.setTooltipData(showSensors,new ToolTipData(sensorTexture,StringConstants.get("STATUSBAR.SENSORS.LONG","Show Sensors")));
		IconToolTip.setDescription(showSensors,StringConstants.get("STATUSBAR.SENSORS.TOOLTIP","Show all the sensor fields in your machine."));
		
		
		showSensors.addButtonListener(new ButtonAdapter()
		{
			public void activate() {
				GeneralSettings.getInstance().getShowSensorsPhysics().setValue(showSensors.isValue());
			}			
		});
		
		GeneralSettings.getInstance().getShowSensorsPhysics().addSettingsListener(new SettingsListener<Boolean>(){

			public void valueChanged(SettingChangedEvent<Boolean> e) {
				showSensors.setValue(e.getNewValue());
			}
			
		}, true);
	
		
		statusGroup.addWidget(showSensors);
		statusGroup.addWidget(showInstruments);
		mainInterface.addWidget(southGroup);
		mainInterface.layout();

		
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
			
		
			
			showSensors.getSpatial().setRenderState(white);
			showInstruments.getSpatial().setRenderState(white);
			showSensors.getSpatial().setRenderState(semiTransparentAlpha);
			showInstruments.getSpatial().setRenderState(semiTransparentAlpha);
			
			Texture backgroundStripes = TextureManager.loadTexture(getClass().getClassLoader().getResource("com/golemgame/data/textures/menu/semi.png"),Texture.MM_LINEAR_NEAREST,Texture.MM_LINEAR);
			Texture backgroundStripes2 = backgroundStripes.createSimpleClone();
		
		
		
			showSensors.setTexture(backgroundStripes,0);
			showInstruments.setTexture(backgroundStripes2,0);

			showSensors.getSpatial().updateRenderState();
			showInstruments.getSpatial().updateRenderState();
			
		}
		
	}

	

	
	public void remove() {
/*		
		try{
		StateManager.getThreadPool().submit(new Runnable()
		{

			public void run() {*/
				OdePhysicsState.this.setActive(true);
				OdePhysicsState.this.lock();
				try{				//TODO:this can get deadlocked... (against the outer class that is calling it.
					StateManager.getGame().lock();
					try{						
						UpdateManager.getInstance().clearStream(Stream.PHYISCS_UPDATE);
						UpdateManager.getInstance().clearStream(Stream.PHYSICS_RENDER);
						UpdateManager.getInstance().clearStream(Stream.PHYISCS_POST_UPDATE);
						OdePhysicsState.this.setActive(false);
						setListening(false);						
					}finally
					{
						StateManager.getGame().unlock();
					}
				}finally{					
					OdePhysicsState.this.unlock();					
				}
				/*			}
	}).get();
		}catch(Exception e)
		{
			StateManager.logError(e);
		}*/
	}

	
	public Node getHUD() {
		// TODO Auto-generated method stub
		return null;
	}
	


	
	private StateEventDispatch dispatch = new StateEventDispatch();
	
	public void addListener(StateListener listener) {
		dispatch.addSettingListener(listener);		
	}

	
	public void removeListener(StateListener listener) {
		dispatch.removeSettingListener(listener);		
	}
	
	public static OdePhysicsState createLoadableTestingState()
	{
		return new OdePhysicsState(true);
	}

	

	private OdePhysicsState(boolean load)
	{
		super("functional");
		ShutdownManagerState.getInstance().addManagedThread(this);//have to add here, not later
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
				loadingLock.lock();
				loadingLock.unlock();
				
		
		}
	}

	
	public void load() throws Exception {
		
		loadingLock.lock();
		try {
			this.setActive(false);
			
			rootModel = new GuardedNodeModel(super.getUpdateLock());
			StateManager.getGame().lock();
			try{
				super.lock();
				try{
			//this.getRootNode().attachChild(rootModel.getSpatial());
					this.getPhysicsRoot().attachChild(rootModel.getSpatial());
				}finally{
					super.unlock();
				}
			}finally
			{
				StateManager.getGame().unlock();
			}
			initState();
		
			 

	
			
			loaded = true;
			loadingCondition.signalAll();
		} finally {
			loadingLock.unlock();
		}
	}


	
	public void shutdownThread() {
		
		cleanup();
		
	}

	
	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		if(instruments != null)
			instruments.setActive(active);
	}

	public void cleanup() {
		if(instruments != null)
			instruments.cleanup();
		ShutdownManagerState.getInstance().removeManagedThread(this);
		super.shutdown();
		super.cleanup();
	}

	public AudioSource getAudioSource() {
		return this.physicsSource;
	}





}
