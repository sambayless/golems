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
package com.golemgame.constructor;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.MemoryHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;

import org.odejava.World;

import com.golemgame.audio.AudioManager;
import com.golemgame.audio.al.ALAudioManager;
import com.golemgame.audio.dummy.DummyAudioManager;
import com.golemgame.constructor.LoadingMonitor.LoadingMonitorListener;
import com.golemgame.local.StringConstants;
import com.golemgame.menu.FileWindow;
import com.golemgame.model.quality.QualityManager;
import com.golemgame.mvc.golems.Golems;
import com.golemgame.mvc.golems.validate.GolemsValidator;
import com.golemgame.platform.PlatformManager;
import com.golemgame.properties.fengGUI.mainmenu.MainMenu;
import com.golemgame.settings.SettingChangedEvent;
import com.golemgame.settings.SettingsListener;
import com.golemgame.settings.SettingsManager;
import com.golemgame.states.DesignState;
import com.golemgame.states.GUILayer;
import com.golemgame.states.GeneralSettings;
import com.golemgame.states.LoadingBackgroundState;
import com.golemgame.states.MemoryManager;
import com.golemgame.states.SaveManager;
import com.golemgame.states.ShutdownManagerState;
import com.golemgame.states.StateManager;
import com.golemgame.states.camera.CameraManager;
import com.golemgame.states.camera.skybox.OverlayGridSkyBox;
import com.golemgame.states.construct.ConstructionManager;
import com.golemgame.states.construct.DefaultConstructionLocation;
import com.golemgame.states.physics.ode.OdePhysicsState;
import com.golemgame.states.record.RecordingListener;
import com.golemgame.states.record.RecordingManager;
import com.golemgame.states.record.RecordingSession;
import com.golemgame.tool.ActionToolSettings;
import com.golemgame.tool.ToolManager;
import com.golemgame.tool.ToolPool;
import com.golemgame.util.input.InputLayer;
import com.golemgame.util.loading.Loadable;
import com.golemgame.util.loading.LoadableSequence;
import com.golemgame.util.loading.Loader;
import com.golemgame.util.loading.LoadingExceptionHandler;
import com.golemgame.util.pass.HudManager;
import com.golemgame.util.pass.PassingGameStateManager;
import com.golemgame.views.ObservableViewManager.ViewListener;
import com.golemgame.views.Viewable.ViewMode;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.lwjgl.records.TextureStateRecord;
import com.jme.system.DisplaySystem;
import com.jme.system.GameSettings;
import com.jme.system.JmeException;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import com.jmex.game.StandardGame;
import com.jmex.game.StandardGame.GameType;
import com.jmex.game.state.GameStateManager;
import com.jmex.physics.PhysicsSpace;


//import com.jme.util.LoggingSystem;

public class GolemsDriver {
	
	/*private static final String RESOLUTION_EXCEPTION = "Invalid resolution values";
	private static final String PIXEL_EXCEPTION = "Invalid pixel depth";
*/	
	
	private static final long MAXIMUM_LOG_LENGTH = 32768; //256 kb

	private static String initialFileLoad = "";

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length>0)
		{
			
			initialFileLoad = args[0];
		//	JOptionPane.showMessageDialog(null,Arrays.toString(args));
		}
		
		System.out.println("Golems, Version: " + Golems.getVersion());
		
		//System.out.println(System.getProperty("java.library.path"));
		try{
					boolean loggingEngaged = false;
					boolean loadingMonitor = true;
					//boolean showSettings = false;
					if(args.length>=1)
					{
						for (String switchStr: args)
						{
						/*	if(switchStr.equalsIgnoreCase("s") || switchStr.equalsIgnoreCase("-s"))
							{
								showSettings = true;
							}else */if (switchStr.equalsIgnoreCase("l") ||  (switchStr.equalsIgnoreCase("-l")))
							{
								loggingEngaged = true;
							}else if (switchStr.equalsIgnoreCase("b") ||  (switchStr.equalsIgnoreCase("-b")))
							{
								loadingMonitor = false;
							}
						}
					}
					SettingsManager.loadSettings();
					
					 StateManager.setLogger( java.util.logging.Logger.getLogger(StateManager.LOGGER_NAME));
			
					 StateManager.getLogger().setUseParentHandlers(false);
					 StateManager.getLogger().addHandler(new ConsoleHandler());
					 
					 
					 try{
						 File logRoot= new File(System.getProperty("user.home") + "/Machines/Logs/");
						 logRoot.mkdirs();
						 File logFile = new File(logRoot,"log.txt");
						 logFile.createNewFile();
						

						 if (! logFile.canWrite())
							throw new Exception("Can't write");
						
						 OutputStream logOut = new BufferedOutputStream(new FileOutputStream(logFile,logFile.length()<MAXIMUM_LOG_LENGTH),8192);
						 StreamHandler streamHandle = new StreamHandler(logOut,new SimpleFormatter());
						 MemoryHandler memHandle = new MemoryHandler( streamHandle,8192,Level.SEVERE);
						 StateManager.getLogger().addHandler(memHandle);
					 }catch(Exception e)
					 {
						 e.printStackTrace();//print, because the logging system isn't working here!
					 }
				
				
					 
						if(loggingEngaged)
						{
							   java.util.logging.Logger.getLogger("com.jme").setLevel(Level.WARNING);
							    java.util.logging.Logger.getLogger("com.jmex").setLevel(Level.WARNING);
							    java.util.logging.Logger.getLogger( PhysicsSpace.LOGGER_NAME ).setLevel(Level.WARNING);
							    java.util.logging.Logger.getLogger( World.LOGGER_NAME ).setLevel(Level.WARNING);
							    java.util.logging.Logger.getLogger("org.odejava.Odejava").setLevel(Level.WARNING);
							    
							    StateManager.getLogger().setLevel(Level.WARNING);
							    for(Handler handler:StateManager.getLogger().getHandlers())
							    	handler.setLevel(Level.WARNING);
						}else
						{
							   java.util.logging.Logger.getLogger("com.jme").setLevel(Level.SEVERE);
							    java.util.logging.Logger.getLogger("com.jmex").setLevel(Level.SEVERE);
							    java.util.logging.Logger.getLogger("org.odejava.Odejava").setLevel(Level.SEVERE);
							    java.util.logging.Logger.getLogger( PhysicsSpace.LOGGER_NAME ).setLevel(Level.SEVERE);
							    java.util.logging.Logger.getLogger( World.LOGGER_NAME ).setLevel(Level.SEVERE);
							    
							    StateManager.getLogger().setLevel(Level.SEVERE);
							    
							    for(Handler handler:StateManager.getLogger().getHandlers())
							    	handler.setLevel(Level.SEVERE);
						}
					 
					 StateManager.getLogger().log(Level.FINE, "Init States");
				    //attempt to load game settings.
					 //call this after settings loaded.
					 System.out.println("Maximum Memory (General,Graphics,Physics) " +  Runtime.getRuntime().maxMemory()/ MemoryManager.MEGABYTE +"MB, "  + MemoryManager.getDirectMemory() + "MB, " + MemoryManager.getPhysicsMemory() + "MB");
						
					 
					 
					 Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler()
					 {

						
						public void uncaughtException(Thread t, Throwable e) {
					
							StateManager.logError(e);					
							GUILayer.displayMessage("Error", "An error occured in the program: " + e);
							
						}
						 
						 
					 });
				 
					 System.out.println("Localization: " + StringConstants.getSupportedLanguage());
					 
		
				 //  final  GameSettings settings = new PreferencesGameSettings(Preferences.userRoot().node(StateManager.PREFERENCES_ROOT_NAME + "/SystemPrefs" ));
					//use this to load the settings... but: its inneficient for standard game to be refering to the system registry all the time
				   
				   //so: cache the settings in a custom game settings class, then put them back into game settings when changes are made.
				   
				   final GameSettings settings = new CachedGameSettings(Preferences.userRoot().node(SettingsManager.PREFERENCES_ROOT_NAME + "/SystemPrefs" ));
				   
				   
				   settings.setFullscreen(GeneralSettings.getInstance().getFullscreen().isValue());//load this from settings manager, not settings file.
					settings.setDepth(GeneralSettings.getInstance().getBitDepth().getValue()); 
					settings.setFramerate(settings.getFrequency());//default framerate.
					settings.setVerticalSync(GeneralSettings.getInstance().getVsync().isValue());
					
					settings.setWidth((int) GeneralSettings.getInstance().getResolution().getValue().getWidth());
					settings.setHeight((int) GeneralSettings.getInstance().getResolution().getValue().getHeight());
				
					
					 if (settings.getWidth()<640 || settings.getHeight() < 480)
					 {	
						 settings.setWidth(640);
						 settings.setHeight(480);
					 }
					/* if(showSettings)
					 {	
						 GameSettingsPanel.prompt(settings);
				    	 Thread.sleep(100);
					 }*/
					 settings.setMusic(false);
					 settings.setSFX(false);
				   
					 
					//monitor loading from this point on.
					 
					 LoadingMonitor.getInstance().setDelayTolerance(14000);
					 LoadingMonitor.getInstance().setLoadingMonitorListener(new LoadingMonitorListener()
					 {

						
						public void loadFailure() {
							try{
							System.err.println("Program failed to load");
							String message = "An unexpected error has caused the program to crash:\nThe program failed to load.";
							
							String title = "Unexpected Error";
							ErrorNotification.getInstance().generateExternalErrorMessage(message, title);
							}finally{
							Runtime.getRuntime().exit(0);
							}
						}
						 
					 });
					 LoadingMonitor.getInstance().update();
					 
					 if(loadingMonitor)
						 LoadingMonitor.getInstance().beginMonitoring();
					 
					 LoadingMonitor.getInstance().update();
					
					   final  StandardGame game = new StandardGame("Golems",GameType.GRAPHICAL,settings,new UncaughtExceptionHandler()
					   
					    {

							
							public void uncaughtException(Thread t, Throwable e) {
								 
								if (e instanceof JmeException)
								{
									try{
									settings.clear();
									}catch(Exception ex)
									{
										//default to common values.
										settings.setWidth(640);
										settings.setHeight(480);
										settings.setDepth(16);
										settings.setFullscreen(false);
										settings.setVerticalSync(false);
										GeneralSettings.getInstance().getResolution().getValue().setSize(640, 480);
										GeneralSettings.getInstance().getVsync().setValue(false);
										GeneralSettings.getInstance().getBitDepth().setValue(16);
										GeneralSettings.getInstance().getFullscreen().setValue(false);
										settings.setFrequency(60);
									}
								}
								StateManager.logError(e);
								if (StateManager.getGame() != null)
									StateManager.getGame().shutdown();
								
							}					    	
					    });
					    StateManager.setGame(game);
				        
					    LoadingMonitor.getInstance().update();
					    
					    loadIcons(game);
					    
						game.start();
						 LoadingMonitor.getInstance().update();
						  //Force the opengl to load before continuing
						game.delayForUpdate();
						 LoadingMonitor.getInstance().update();
					    game.setUncaughtExceptionHandler(new UncaughtExceptionHandler()
					    {

							
							public void uncaughtException(Thread t, Throwable e) {
								StateManager.getGame().shutdown();
								String message = "An unexpected error has caused the program to crash:\n"+ e + "\n"+e.getLocalizedMessage();
								
								String title = "Unexpected Error";
								ErrorNotification.getInstance().generateExternalErrorMessage(message, title);

							}
					    	
					    });
					   
					    LoadingMonitor.getInstance().update();
					    
					  GameTaskQueueManager.getManager().update(new Callable<Object>()
					    		{

									
									public Object call() throws Exception {
										//possibly this has to be in the opengl thread?
								   		MouseInput.get().setCursorVisible(true);
								   	    LoadingMonitor.getInstance().update();
										return null;
									}
	    		
					    		});
					   LoadingMonitor.getInstance().update();
					    //init display.
					   	
					   // DimensionSetting resolution = SettingsManager.getInstance().createDimensionSetting(GeneralSettings.SETTING_NAME_RESOLUTION, new Dimension(1024,768));
					    GeneralSettings.getInstance().getResolution().setValue(new Dimension(DisplaySystem.getDisplaySystem().getWidth(),DisplaySystem.getDisplaySystem().getHeight() - (DisplaySystem.getDisplaySystem().isFullScreen()? 0:0)));
					    LoadingMonitor.getInstance().update();
					    StateManager.getThreadPool().execute(new Runnable()
					    {

					    	
					    	public void run() {
					    		 LoadingMonitor.getInstance().update();
					    		initStates();
					    	}

					    });



			}catch(Exception e)
			{
				StateManager.logError(e);
			
			}catch(Error e)
			{
				StateManager.logError(e);
				throw e;
			}
			
		
	}
	private static void loadIcons(StandardGame game) {
		
		ArrayList<com.jme.image.Image> images = new ArrayList<com.jme.image.Image>();
		
		String[] imageSizes = new String[]{"16","24","32","64","128","256"};
		for (String size:imageSizes)
		{
			try{
				String resource = "/com/golemgame/data/app/icons/golemIcon" + size + ".png" ;
				InputStream  stream = GolemsDriver.class.getResourceAsStream(resource);
				BufferedImage image = ( ImageIO.read(stream));	
				images.add( TextureManager.loadImage(image, false)); 
			}catch(Exception e)
			{
				StateManager.getLogger().log(Level.WARNING,"Icon " + size + "Failed to load");
			}
		}
		    
	    game.setIcons(images.toArray(new com.jme.image.Image[0]));
	   
		
	}
	private static void initStates()
	{
		try{
			
	
			 LoadingMonitor.getInstance().update();

			 String appValue = GeneralSettings.getInstance().getAppVersion().getValue();
	
			 if (! appValue.equals(Golems.getVersion()) && Golems.getMajor(appValue)<=0 && Golems.getMinor(appValue) <= 52 & Golems.getRevision(appValue)< 2)
			 {//ensure macs get updated from their old, bad default settings.
				 ActionToolSettings.getInstance().getMultipleSelectKey().setKeyCode(PlatformManager.get().getDefaultKeyBindings().getMultipleSelectKey());
			 }
			 
		
			 LoadingMonitor.getInstance().update();
			 
			 GeneralSettings.getInstance().getAppVersion().setValue(Golems.getVersion());
			 
			 GolemsValidator.getInstance().build();//instantiate this early - its fast, and may need to be used at any time
			 
				StateManager.getThreadPool().execute(new Runnable()
				{
					
					public void run() {
						try{
							DemoMachines.ensureIsCopied();
						}catch(Exception e)
						{
							StateManager.getLogger().log(Level.WARNING,"Demos failed to copy: " + e);
						}
					}
				});
			 
			 LoadingMonitor.getInstance().update();
			 StateManager.getGame().lock();
			 try{
				 GameStateManager.getInstance().attachChild( PassingGameStateManager.getInstance());
				 PassingGameStateManager.getInstance().attachChild(ShutdownManagerState.getInstance());
				 PassingGameStateManager.getInstance().getPassManager().add(HudManager.getInstance());
			 }finally{
			 StateManager.getGame().unlock();
			 }
			 StateManager.getLogger().log(Level.FINE, "Attach Game State Managers");
			 LoadingMonitor.getInstance().update();
			 ShutdownManagerState.getInstance().addShutdownHook(new Runnable()
			 {

				
				public void run() {
					SettingsManager manager = SettingsManager.getInstance();
					if (manager != null)
						manager.save();
					
				}
				 
			 });
			 LoadingMonitor.getInstance().update();
			 StateManager.getLogger().log(Level.FINE, "Attach Shutdown Managers");
			 LoadingMonitor.getInstance().update();
			KeyInput.get().addListener(InputLayer.get());
			MouseInput.get().addListener(InputLayer.get());
			StateManager.getLogger().log(Level.FINE, "Attach Input Layer");
			 LoadingMonitor.getInstance().update();
			
			 StateManager.setQualityManager(new QualityManager());
			 GeneralSettings.getInstance().getQuality().addSettingsListener(new SettingsListener<Integer>()
			 {

				
				public void valueChanged(SettingChangedEvent<Integer> e) {
					StateManager.getQualityManager().setQuality(e.getNewValue());					
				}
			 },true);
			 
			final DesignState designState = DesignState.createLoadableDesignState();
			
			final OdePhysicsState testingState = OdePhysicsState.createLoadableTestingState();
			//final MenuState menuState = MenuState.createLoadableMenuState();
			StateManager.getLogger().log(Level.FINE, "Create Loadable States");
			 LoadingMonitor.getInstance().update();
			StateManager.setCurrentGameState(designState);
				
			StateManager.setDesignState(designState);
			StateManager.setFunctionalState(testingState);

		
			GeneralSettings.getInstance().getMenu().addSettingsListener(new SettingsListener<Object>(){

					
					public void valueChanged(
							SettingChangedEvent<Object> e) {
							MainMenu.toggleMenu();
					}
					
				},InputLayer.MAX_PRIORITY);
			 
			
			Loader.getInstance().setDelayTime(0);
			StateManager.getLogger().log(Level.FINE, "Initiate Loader");
			 LoadingMonitor.getInstance().update();
			//for unknown reasons, loading progress bar before FengGUI causes it to appear white.
			//apparently this is resolved: loading texture states in the fenggui texture state rendered invalidates jme's texture state records for texture2d enabled.
			LoadableSequence sequence = new LoadableSequence(false);
			
			final LoadingBackgroundState loadingState = new LoadingBackgroundState();
			
	
			
			sequence.queueLoadable(new Loadable<Object>("Loading FengGUI")
					{
						
						public void load() throws Exception {
							StateManager.getGame().executeInGL(new Callable<Object>()
									{

										
										public Object call() throws Exception {
											GUILayer.loadInstance();
											 LoadingMonitor.getInstance().update();
											HudManager.getInstance().attachChild(GUILayer.getLoadedInstance()); 
										       com.jme.renderer.RenderContext context = DisplaySystem.getDisplaySystem()
										        .getCurrentContext();
												TextureStateRecord record = (TextureStateRecord) context
												        .getStateRecord(RenderState.RS_TEXTURE);
												record.invalidate();
											//this keeps the textures from being screwed up during loading.
	
											return null;
										}
								
									});
						}
					});
			
			sequence.queueLoadable(new Loadable<Object>("Initiate Loading")
					{
						
						public void load() throws Exception {
							loadingState.load();
							loadingState.setActive(true);
							StateManager.getGame().lock();
							try{
								PassingGameStateManager.getInstance().attachChild(loadingState);
							}finally{
								StateManager.getGame().unlock();
							}
						}
					});
			
			sequence.queueLoadable(new Loadable<Object>("Initiate Sound System")
					{
						
						public void load() throws Exception {
							AudioManager audio;
						
							if(StateManager.SOUND_ENABLED)
							{
								try{
									audio = new ALAudioManager();
								}catch(Exception e)
								{
									audio =  new DummyAudioManager();
								}
							}else{
								audio =  new DummyAudioManager();
							}
						
							StateManager.setAudioManager(audio);	
						
							
							GeneralSettings.getInstance().getMuteSound().addSettingsListener(new SettingsListener<Boolean>()
									{
										public void valueChanged(SettingChangedEvent<Boolean> e) {
											StateManager.getAudioManager().setMute(e.getNewValue());
											
										}
								
									},true);
							
							GeneralSettings.getInstance().getVolume().addSettingsListener(new SettingsListener<Float>()
									{

										public void valueChanged(SettingChangedEvent<Float> e) {
											StateManager.getAudioManager().setVolume(e.getNewValue());
										}
								
									},true);
							
							
						}
					});
			
			sequence.queueLoadable(new Loadable<Object>("Loading Constructor")
			{
				
				public void load() throws Exception {
					
					StateManager.setRecordingManager(new RecordingManager());

					 LoadingMonitor.getInstance().update();
					StateManager.getLogger().info("Setting up cameras");
					 StateManager.setCameraManager(new CameraManager());
					 //StateManager.getCameraManager().getSkyBoxManager().setSkyBox(new SolidColorSkyBox());
			/*		 StateManager.getCameraManager().getSkyBoxManager().setSkyBox(new ImageSkyBox(ImageIO.read(new File("skybox/up.png")),
							 ImageIO.read(new File("skybox/down.png")),ImageIO.read(new File("skybox/west.png")),
							 ImageIO.read(new File("skybox/east.png")),ImageIO.read(new File("skybox/north.png")),
							 ImageIO.read(new File("skybox/south.png"))));*/
					 
/*					 StateManager.getCameraManager().getSkyBoxManager().setSkyBox(new ImageSkyBox(ImageIO.read(new File("../jme/src/jmetest/data/skybox1/1.jpg")),
							 ImageIO.read(new File("../jme/src/jmetest/data/skybox1/2.jpg")),ImageIO.read(new File("../jme/src/jmetest/data/skybox1/3.jpg")),
							 ImageIO.read(new File("../jme/src/jmetest/data/skybox1/4.jpg")),ImageIO.read(new File("../jme/src/jmetest/data/skybox1/5.jpg")),
							 ImageIO.read(new File("../jme/src/jmetest/data/skybox1/6.jpg"))))*/;
					 
					 StateManager.getCameraManager().getSkyBoxManager().setOverlay(new OverlayGridSkyBox());
					 LoadingMonitor.getInstance().update();
					 StateManager.getLogger().info("Setting up tools");
					 StateManager.setToolPool(new ToolPool());
					 LoadingMonitor.getInstance().update();
					 StateManager.getLogger().info("Setting up tool manager");
					 StateManager.setToolManager(new ToolManager());
				
					 LoadingMonitor.getInstance().update();
					 StateManager.getLogger().info("Setting up construction manager");
					 StateManager.setConstructionManager(new ConstructionManager(new DefaultConstructionLocation()));
					
					 
					 StateManager.getLogger().info("Setting up design state");
					 designState.load();
					 LoadingMonitor.getInstance().update();
					 
					 StateManager.getRecordingManager().registerListener(new RecordingListener()
					 {
							
						 	private static final long MEGABYTE = 1048576L ;
							private static final long KILOBYTE = 1024L ;
							private static final long GIGABYTE = 1073741824L ;
							private String sessionLength = "0";
							private String sessionSize = "0";
						
							private boolean recording = false;
							private boolean paused = false;
							public void frameDrawn(RecordingSession session,
									RecordingManager source) {
								long msDif = new Date().getTime() - session.getRecordingStart().getTime();
								long hours = msDif/(1000 * 3600);
								
								long minutes =  msDif/(1000 * 60) - hours*60;
								long seconds =  msDif/(1000)- hours*60*60 - minutes*60;
							//	System.out.println(hours + ":" + minutes + ":" + seconds);
								 sessionLength = (hours + ":" + minutes + ":" + seconds );
								
								long length = session.getDestination().length();
								if(length <KILOBYTE)
								{
									sessionSize=(length + " bytes");
								}if(length < MEGABYTE)
								{
									sessionSize=(length/KILOBYTE + " KB");
								}else if  (length<GIGABYTE)
								{
									sessionSize=(length/MEGABYTE + " MB");
								}else{
									
									long lengthGB = length/GIGABYTE;
									long lengthRemainer = length%GIGABYTE;
									double percent =Math.round(10.0* ( ((double) lengthRemainer) /( (double)GIGABYTE)))/10.0;
									sessionSize=(lengthGB + "." + percent + " GB");
								}
								updateTitle();
							}
							
							public void recordingState(boolean recording, boolean paused) {
								this.recording = recording;
								this.paused = paused;
								updateTitle();
							}
							
							private void updateTitle()
							{
								String title = "Golems";
								if(recording)
								{
									title += " - Recording";
									if(paused)
										title += "[PAUSED]";
									title += " - " + sessionLength + " - " + sessionSize;
								}
								DisplaySystem.getDisplaySystem().setTitle(title);
							}
					 });
					 
				}
			});
			
		
			
			sequence.queueLoadable(new Loadable<Object>("Attaching Constructor")
			{
				
				public void load() throws Exception {
					
					 LoadingMonitor.getInstance().update();
					 designState.init(); 
					 
					 LoadingMonitor.getInstance().update();
						
					 //dont register the machine directly, to avoid references
					 StateManager.getViewManager().registerListener(new ViewListener()
					 {

						public void viewChanged(
								Collection<ViewMode> views) {
							if (StateManager.getStructuralMachine() !=null)
							{
								//StateManager.getStructuralMachine().clearViews();
								for (ViewMode view:ViewMode.values())
								{
									if(views.contains(view))
										StateManager.getStructuralMachine().addViewMode(view);
									else
										StateManager.getStructuralMachine().removeViewMode(view);
						
								}
								StateManager.getStructuralMachine().refreshView();
								
							}
							
						}
						 
					 });
				     StateManager.getMachineManager().setEnabled(true);
					 SaveManager.getInstance().newMachine();
					
						StateManager.getGame().lock();
						try{
							PassingGameStateManager.getInstance().detachChild(loadingState);
							PassingGameStateManager.getInstance().attachChild(designState);
						}finally{
				        StateManager.getGame().unlock();
						}
						
						
				        StateManager.getToolManager().setListening(true);
			
				        LoadingMonitor.getInstance().endMonitoring();
	
				   
			/*	        GeneralSettings.getInstance().getTest().addSettingsListener(new ActionSettingsListener()
				        {
				        	private boolean t = false;
							public void valueChanged(
									SettingChangedEvent<Object> e) {
								
								
						//		StateManager.getStructuralMachine().addViewMode(ViewMode.MATERIAL);
								
							 	BooleanSetting groupMode = ActionToolSettings.getInstance().getAssignedGroupSelectionMode();
								groupMode.setValue(! groupMode.isValue());
								t = ! t;
								if(t)
									StateManager.getStructuralMachine().addViewMode(ViewMode.GHOST);
								else
									StateManager.getStructuralMachine().removeViewMode(ViewMode.GHOST);
							}
				        	
				        });*/
				}
			});

	
			
			final File initialFile = new File(initialFileLoad);
			if (initialFileLoad.length()>1 && initialFile.exists())
			{
				sequence.queueLoadable(new Loadable<Object>("Loading File")
						{
							
							public void load() throws Exception {
								
						
								
									SaveManager.getInstance().loadAndSetMachine(initialFile);
								
							        
							}
						});
			
			}
			
			
				
			sequence.setHandler(new LoadingExceptionHandler()
			{

				
				public void handleException(Exception e, Loadable<?> l)
						throws Exception {
					
					StateManager.logError(e);
					StateManager.getGame().shutdown();
					
				}
				
			});

			sequence.queueLoadable(new Loadable<Object>("Loading ASync Items")
			{

				
				public void load() throws Exception {
			
					//load these asynchronously, but require them to BEGIN loading after the sequence loads.
					Loader.getInstance().loadAsynch(new Loadable<Object>("Loading Main Menu")
							{
								
								public void load() throws Exception {
									
									StateManager.getGame().executeInGL(new Callable<Object>()
											{

												
												public Object call()
														throws Exception {
													MainMenu.getMainMenu();
													return null;
												}
									
											});
								}
							});
					
					Loader.getInstance().loadAsynch(new Loadable<Object>("Loading File Window")
							{
								
								public void load() throws Exception {
								
									FileWindow.getInstance().load();
								//	System.out.println(FileWindow.getInstance().getFileChooser().getSelectedFile());
								}
							});
					
			
					
					
					Loader.getInstance().loadAsynch(new Loadable<Object>("Loading Physics State")
					{
						
						public void load() throws Exception {
							
							testingState.load();
							
						
							
							StateManager.getGame().lock();
							try{
							PassingGameStateManager.getInstance().attachChild(testingState);
							}finally{
					        StateManager.getGame().unlock();
					        }
							
							GeneralSettings.getInstance().getPhysicsSpeed().addSettingsListener(new SettingsListener<Float>()
									{

										
										public void valueChanged(
												SettingChangedEvent<Float> e) {
											setPhysicsSpeed(e.getNewValue());
										}
								
									}
									, true);	
							
							GeneralSettings.getInstance().getPhysicsOverideSpeed().addSettingsListener(new SettingsListener<Float>()
									{

										
										public void valueChanged(
												SettingChangedEvent<Float> e) {
											setPhysicsSpeed(GeneralSettings.getInstance().getPhysicsSpeed().getValue());
											
										}
									
									});

							
							}
						
						
						
						
					});
					
				
				}
				
			});
			
			Loader.getInstance().queueLoadable(sequence);
			StateManager.getLogger().log(Level.FINE, "Queue Loader");
		}catch(Exception e)
		{
			StateManager.logError(e);
			
		}catch(Error e)
		{
			StateManager.logError(e);
			throw (e);
		}finally
		{
		
		}
	}
	
	private static void setPhysicsSpeed(float speed)
	{
		StateManager.getFunctionalState().waitForLoad();
		
		float overrideValue = GeneralSettings.getInstance().getPhysicsOverideSpeed().getValue();
		float newSpeed;
	
		if(Float.isNaN(overrideValue) )
		{//positive infinity override value is ignored
			newSpeed =speed;
		}else
		{
			newSpeed = overrideValue;
		}
		
		if (!Float.isInfinite(newSpeed))
		{
			StateManager.getFunctionalState().setUpdatesPerSecond(((long) (newSpeed* 120f)));
		}
		else
		{
			StateManager.getFunctionalState().setUpdatesPerSecond(Long.MIN_VALUE);
		}
		
	}
	
}
