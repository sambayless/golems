package com.golemgame.states.physics.ode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.golemgame.functional.component.BUpdateCallback;
import com.golemgame.instrumentation.Instrument;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.physical.EnvironmentListener;
import com.golemgame.physical.PhysicsEnvironment;
import com.golemgame.physical.ode.OdePhysicalStructure;
import com.golemgame.physical.ode.OdePhysicsInteractionManager;
import com.golemgame.physical.ode.OdePhysicsMachineSpace;
import com.golemgame.physical.ode.OdeSpringController;
import com.golemgame.physical.ode.compile.OdeCompiledPhysical;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.golemgame.physical.ode.sound.OdeSoundManager;
import com.golemgame.settings.SettingChangedEvent;
import com.golemgame.settings.SettingsListener;
import com.golemgame.states.GeneralSettings;
import com.golemgame.states.StateManager;
import com.golemgame.states.camera.CameraDelegate;
import com.golemgame.states.camera.OrderedCameraList;
import com.golemgame.states.physics.IllegalTransitionException;
import com.golemgame.util.input.InputLayer;
import com.golemgame.util.loading.ProgressObserver;
import com.jmex.physics.CollisionGroup;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsUpdateCallback;
import com.jmex.physics.impl.ode.OdePhysicsSpace;
import com.jphya.scene.Scene;

public class OdePhysicsController extends MVCFunctionalPhysicsController {

	private final OdePhysicsState machineState;
	private OdeCompiledPhysical compiledPhysical;
	private OrderedCameraList cameraSet = new OrderedCameraList();;
	private SettingsListener<Boolean>physicsSensorListener;
	public OdePhysicsController(OdePhysicsState machineState) {
		super(machineState);
		this.machineState = machineState;
	}

	@Override
	public synchronized void load() throws IllegalTransitionException {
		// TODO Auto-generated method stub
		super.load();
	}
	


	public synchronized void load(PropertyStore store, ProgressObserver observer) throws IllegalTransitionException {
		lockState();
		try{
			//GeneralSettings.getInstance().getShowSensorsPhysics().setValue(false);
			
			//physics MUST be locked for this!
			OdePhysicsMachineSpace machineSpace = new OdePhysicsMachineSpace(store);
			PhysicsSpatialMonitor.getInstance().setPhysics(machineState.getPhysicsSpace());
			observer.setProgress(0.2f);
			Map<OdePhysicalStructure,PhysicsNode> physicalMap = new HashMap<OdePhysicalStructure,PhysicsNode>();
		
			if(StateManager.SOUND_ENABLED) {
				if (GeneralSettings.getInstance().getSoundEnabled().isValue()) {
					machineState.setSoundManager( new OdeSoundManager());
				}
			}
		
			final OdePhysicsEnvironment compilationEnvironment = new OdePhysicsEnvironment(){

				@Override
				public void registerInstrument(Instrument instrument) {
					super.registerInstrument(instrument);
					machineState.getInstruments().addInstrument(instrument);
				}
				
			};
			observer.setProgress(0.3f);
			
			if(StateManager.SOUND_ENABLED) {
				if (GeneralSettings.getInstance().getSoundEnabled().isValue()) {
					Scene soundScene = new Scene(StateManager.getAudioManager().createSoundStream(machineState.getAudioSource()));
					soundScene.setLimiter(0.005f, 0.015f, 0.075f);
//		soundScene.setFramesPerSecond(44100/2);
					compilationEnvironment.setSoundScene(soundScene);
				}
			}
			compilationEnvironment.setCameraManager(StateManager.getCameraManager());
/*			compilationEnvironment.setSensorCollisionGroup(machineState.getPhysicsSpace().createCollisionGroup("Sensors"));
			compilationEnvironment.getSensorCollisionGroup().collidesWith(((OdePhysicsSpace)machineState.getPhysicsSpace()).getDefaultCollisionGroup(), true);
			compilationEnvironment.getSensorCollisionGroup().collidesWith(((OdePhysicsSpace)machineState.getPhysicsSpace()).getStaticCollisionGroup(), true);
			*/
			//	compilationEnvironment.setSensorCollisionGroup(((OdePhysicsSpace)machineState.getPhysicsSpace()).getDefaultCollisionGroup());
			
	/*		machineSpace.getModel().refreshLockedData();
			machineSpace.getModel().updateWorldData();*/
			observer.setProgress(0.4f);
			System.gc();
			
			CollisionGroup group = machineState.getPhysicsSpace().createCollisionGroup("ray group");
			group.collidesWith(((OdePhysicsSpace)machineState.getPhysicsSpace()).getDefaultCollisionGroup(), true);
			group.collidesWith(((OdePhysicsSpace)machineState.getPhysicsSpace()).getStaticCollisionGroup(), true);
			compilationEnvironment.setRayGroup(group);
			//compilationEnvironment.setRayGroup(((OdePhysicsSpace)machineState.getPhysicsSpace()).getDefaultCollisionGroup());
			
			compiledPhysical =  machineSpace.compilePhysics(this.machineState.getPhysicsSpace(),physicalMap,compilationEnvironment);
			observer.setProgress(0.6f);
			System.gc();

			machineState.getPhysicsSpace().setDirectionalGravity(machineSpace.getDirectionalGravity());
			
			loadCompiledPhysical();
			
			machineState.physicsWorld = compiledPhysical.getPhysicsWorld();
			if(machineState.getSoundManager()!=null)
				machineState.getSoundManager().attach(compiledPhysical);
			
			InputLayer.get().addKeyListener(machineState.getInstruments().getListeners(),InputLayer.STATE_LAYER);
			InputLayer.get().addMouseListener(machineState.getInstruments().getListeners(),InputLayer.STATE_LAYER);
			
			machineState.interactionManager = new OdePhysicsInteractionManager(compiledPhysical.getPhysicsWorld());
			machineState.spring = new OdeSpringController(compiledPhysical.getPhysicsWorld());
			machineState.getPhysicsSpace().addToUpdateCallbacks(machineState.getSoundManager());
			//StateManager.getToolManager().setCurrentTool(new PhysicsTool(machineState.interactionManager,machineState.spring));
			StateManager.getToolManager().setListening(false);
		
			
			observer.setProgress(0.7f);
			System.gc();
			
		//	machineState.getPhysicsSpace().setDirectionalGravity(machineSpace.getMachineSpaceSettings().getGravitySettings().getGravity());
			compiledPhysical.getCompiledEnvironment().getContactManager().initialize(	compiledPhysical.getCompiledEnvironment().getContacts());
			machineState.getPhysicsSpace().addToUpdateCallbacks(new PhysicsUpdateCallback() {
				
				public void beforeStep(PhysicsSpace space, float time) {
					
				}
				
				public void afterStep(PhysicsSpace space, float time) {
					
								}
			});
			
			machineState.getBSpace().addUpdateCallback(new BUpdateCallback(){

				public void afterBUpdate() {		
					compiledPhysical.getCompiledEnvironment().getContactManager().reset();
				}

				public void beforeBUpdate() {
					
				
				}
				
			});
			
			super.load(machineSpace,physicalMap,compilationEnvironment,observer);
			
			physicsSensorListener = new SettingsListener<Boolean>(){

				public void valueChanged(SettingChangedEvent<Boolean> e) {
					compilationEnvironment.setShowSensorFields(e.getNewValue());
				}
				
			};
			GeneralSettings.getInstance().getShowSensorsPhysics().addSettingsListener(physicsSensorListener,true);
			
			machineState.getInstruments().layout();
			
			//has to be rechecked after clearing instruments it seems.
			machineState.getInstruments().setVisible(GeneralSettings.getInstance().getShowInstruments().isValue());

		}finally
		{
			unlockState();
		}
	}

	@Override
	public synchronized void preparePhysics() throws IllegalTransitionException {
		lockState();
		try{
			System.gc();
			super.preparePhysics();
			
			unloadCompiledPhysical();
			machineState.getInstruments().clear();
			PhysicsSpatialMonitor.getInstance().clear();
			
		}finally
		{
			unlockState();
		}
	}

	@Override
	public synchronized void start() throws IllegalTransitionException {
		lockState();
		try{
				super.start();
				
				if(StateManager.SOUND_ENABLED) {
					if (machineState.getSoundManager()!=null) {
						Thread soundThread = new Thread(new Runnable(){

							public void run() {
								final int sleepTime = (int) Math.max(1,0.85f* compiledPhysical.getCompiledEnvironment().getSoundScene().getNFrames() * 1f/compiledPhysical.getCompiledEnvironment().getSoundScene().getFPS());//leave 15% atleast for processing time;
								while(((getCurrentState()==State.Started)|| (getCurrentState()==State.Running)) && compiledPhysical!=null)
								{
									//long start = System.nanoTime();
									machineState.getSoundManager().update();
									//System.out.println(System.nanoTime() - start);
									try {
										Thread.sleep(sleepTime);
									} catch (InterruptedException e) {
										StateManager.logError(e);
									}
								}
							}

						},"Sound Scene");
						soundThread.setDaemon(true);
						soundThread.setPriority(Thread.MAX_PRIORITY);//to avoid audio skipping
						soundThread.start();
					}
				}
			}finally
			{
				unlockState();
			}
		
	}

	@Override
	public synchronized void stop() throws IllegalTransitionException {

		
		lockState();
		try{
			super.stop();
		}finally
		{
			unlockState();
		}
	}

	@Override
	public synchronized void unload() throws IllegalTransitionException {
		lockState();
		try{
	
			
			if(this.compiledPhysical != null)
			{
				GeneralSettings.getInstance().getShowSensorsPhysics().removeSettingsListener(physicsSensorListener);
				
		//	System.out.println(((OdePhysicsSpace) machineState.getPhysicsSpace()).getWorld().getBodies().size());
				final Scene scene=this.compiledPhysical.getCompiledEnvironment().getSoundScene();
				
				if(scene!=null)
				{
					StateManager.getAudioManager().executeInAudio(new Callable<Object>(){
	
						public Object call() throws Exception {
							scene.getOutputStream().close();
							return null;
						}
						
					});
			
				}
			}
			unloadCompiledPhysical();
			machineState.setSoundManager(null);
			
			
			//before we clear the instrument layer,save the changes to the chart positions
			//machineState.getInstruments().saveChanges();
			
			if(machineState.getInstruments()!=null)
				machineState.getInstruments().clear();
			if(machineState.physicsWorld!=null)
				machineState.physicsWorld.clear();
			machineState.physicsWorld = null;
			machineState.interactionManager = null;
		
			machineState.spring = null;
			PhysicsSpatialMonitor.getInstance().clear();	
			super.unload();
			StateManager.getToolManager().setListening(true);
			System.gc();
		
			//System.out.println(((OdePhysicsSpace) machineState.getPhysicsSpace()).getWorld().getBodies().size());
		}finally
		{
			unlockState();
		}
	
	}
	private void unloadCompiledPhysical()
	{
		cameraSet.clear();
		machineState.acquireGraphicsAndPhysicsLocks();
		try{//this proceeds much faster locked
			machineState.getRootModel().detachAllChildren();
		}finally{
			machineState.releaseGraphicsAndPhysicsLocks();
		}
		
		if (compiledPhysical != null)
		{//shouldn't be reached, but just in case...
			InputLayer.get().removeKeyListener(compiledPhysical.getCompiledEnvironment().getInteractionServer());
			InputLayer.get().removeMouseListener(compiledPhysical.getCompiledEnvironment().getInteractionServer());
			
			machineState.acquireGraphicsAndPhysicsLocks();
			//StateManager.getGame().lock();
			try{
			
			//compiledPhysical.detach();
			compiledPhysical.delete();
			}finally{
				machineState.releaseGraphicsAndPhysicsLocks();
			//	StateManager.getGame().unlock();
			}
	
			compiledPhysical = null;
		}	
	}
	
	  private void loadCompiledPhysical() 
	  {		
		StateManager.getGame().lock();
		try{//this proceeds much faster locked
		compiledPhysical.attachToModel(machineState.getRootModel());
		}finally{
			StateManager.getGame().unlock();
		}
		machineState.getRootModel().setUpdateLocked(false);
		
		List<CameraDelegate> cameras = compiledPhysical.getCompiledEnvironment().getCameras();
		cameraSet.clear();
		for (CameraDelegate delegate:cameras)
			cameraSet.addOrderedDelegate(delegate);
		
		compiledPhysical.getCompiledEnvironment().addEnvironmentListener(new EnvironmentListener()
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
		
		//do this here, after the cameras are compiled, so that the camera view is preserved.
		StateManager.getCameraManager().setOrderedCameraList(cameraSet);
		
		InputLayer.get().addMouseListener(compiledPhysical.getCompiledEnvironment().getInteractionServer());

		InputLayer.get().addKeyListener(compiledPhysical.getCompiledEnvironment().getInteractionServer());
	}
}
