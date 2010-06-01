package com.golemgame.states;

import java.awt.Dimension;
import java.io.File;

import com.golemgame.settings.ActionSetting;
import com.golemgame.settings.BooleanSetting;
import com.golemgame.settings.DimensionSetting;
import com.golemgame.settings.FloatSetting;
import com.golemgame.settings.IntegerSetting;
import com.golemgame.settings.KeySetting;
import com.golemgame.settings.SettingChangedEvent;
import com.golemgame.settings.SettingsListener;
import com.golemgame.settings.SettingsManager;
import com.golemgame.settings.StringSetting;
import com.golemgame.util.input.InputLayer;
import com.golemgame.util.input.ListenerGroup;
import com.jme.input.KeyInput;
import com.jme.input.KeyInputListener;
import com.jme.system.DisplaySystem;

public class GeneralSettings {
	private static final GeneralSettings instance= new GeneralSettings();

	
	public static GeneralSettings getInstance() {
		return instance;
	}

	
	public static final String SETTING_NAME_RESOLUTION = "screen.resolution";
	public static final String SETTING_NAME_FULLSCREEN = "screen.fullscreen";
	public static final String SETTING_NAME_PHYSICS_SPEED = "physics.speed";
	private static final String SETTING_NAME_BIT_DEPTH = "screen.bitdepth";
	public static final String DESIGN_MODE_GRID = "design.grid.show";
	public static final String SHOW_LAYERS = "layers.show";
	private static final String MUTE_SOUND = "volume.mute";
	private static final String VOLUME = "volume";
	private static final String SOUND_ENABLED = "sound.enabled";
	private static final String SHOW_SOUND_CONTROLS = "sound.controls.show";
	private static final String MAX_MEMORY = "memory.max";
	private static final String USE_LOCALIZATION = "localization.enable";
	private static final String SHOW_SENSORS_PHYSICS = "sensors.show.physics";
	private static final String SHOW_INSTRUMENTS = "instrmnt.show.physics";//have to keep keys short here
	private static final String MACHINE_CHANGED = "machine.changed";
	
	
	
	public GeneralSettings() {
		super();
		
		
		
		
			cameraZoomKey.addKeyListener(new KeyInputListener()
			{
				
				public void onKey(char character, int keyCode, boolean pressed) {
					
						cameraZoom.setValue(pressed);
				}			
			});
			
			cameraDefaultKey.addKeyListener(new KeyInputListener()
			{
				
				public void onKey(char character, int keyCode, boolean pressed) {
					if (pressed)
						cameraDefault.initiateAction();
				}			
			});
		
			cameraForwardKey.addKeyListener(new KeyInputListener()
			{
				
				public void onKey(char character, int keyCode, boolean pressed) {
					if (pressed)
						cameraForward.initiateAction();
				}			
			});
		
				cameraBackwardKey.addKeyListener(new KeyInputListener()
				{
					
					public void onKey(char character, int keyCode, boolean pressed) {
						if (pressed)
							cameraBackward.initiateAction();
					}			
				});
			
		
			menuKey.addKeyListener(new KeyInputListener()
			{
				
				public void onKey(char character, int keyCode, boolean pressed) {
					if (pressed)
						menu.initiateAction();
				}			
			});
		
	
			
			pictureKey.addKeyListener(new KeyInputListener()
			{
				
				public void onKey(char character, int keyCode, boolean pressed) {
					if (pressed)
						picture.initiateAction();
				}			
			});
			
			picture.addSettingsListener(new SettingsListener<Object>()
					{

						
						public void valueChanged(SettingChangedEvent<Object> e) {
							
							File pictureRoot= new File(System.getProperty("user.home") + "/Machines/Screenshots/");
							pictureRoot.mkdirs();
							
							String filename = "screenshot";
							
							File file = new File(pictureRoot, filename + ".png");
							int i = 0;
							while (file.exists())
							{
								i ++;
								filename = "screenshot (" + i + ")";
								file = new File(pictureRoot, filename + ".png");
							}
				
								DisplaySystem.getDisplaySystem().getRenderer().takeScreenShot(new File(pictureRoot,filename).getPath());
		
						
						}
				
					});
		
		
			testKey.addKeyListener(new KeyInputListener()
			{
				
				public void onKey(char character, int keyCode, boolean pressed) {
					if (pressed)
						test.initiateAction();
				}			
			});
			
			showGrid.addSettingsListener(new SettingsListener<Boolean>()
			{

				public void valueChanged(SettingChangedEvent<Boolean> e) {
					StateManager.getDesignState().setGridEnabled(e.getNewValue());
				}
				
			});
			
			
			layerKey.addKeyListener(new KeyInputListener()
			{
				
				public void onKey(char character, int keyCode, boolean pressed) {
					if (pressed)
						showLayers.setValue(!showLayers.isValue());
				}			
			});
			
			
			//stateSettings.addKeyListener(menuKey);	
			
			InputLayer.get().addKeyListener(menuKey,InputLayer.MENU_LAYER);
			stateSettings.addKeyListener(layerKey);
			stateSettings.addKeyListener(pictureKey);
			stateSettings.addKeyListener(testKey);
			stateSettings.addKeyListener(cameraBackwardKey);
			stateSettings.addKeyListener(cameraForwardKey);
			stateSettings.addKeyListener(cameraDefaultKey);
			stateSettings.addKeyListener(cameraZoomKey);
			InputLayer.get().addKeyListener(stateSettings, InputLayer.SETTINGS_KEY_LAYER);
	
			this.physicsSpeed.setIsTransient(true);
	}
	
	
	private  final BooleanSetting autoCloseMainMenu = SettingsManager.getInstance().createBooleanSetting("autoCloseMainMenu",false);
	

	private  final DimensionSetting menuSize =  SettingsManager.getInstance().createDimensionSetting("menuSize",new Dimension(100,150));
	
	private  final ActionSetting menu = new ActionSetting();
	
	private  final KeySetting menuKey =  SettingsManager.getInstance().createKeySetting("menuKey",KeyInput.KEY_ESCAPE);

	private  final BooleanSetting useLocalization = SettingsManager.getInstance().createBooleanSetting(USE_LOCALIZATION, true);
	



	public BooleanSetting getUseLocalization() {
		return useLocalization;
	}


	private  final DimensionSetting resolution = SettingsManager.getInstance().createDimensionSetting(SETTING_NAME_RESOLUTION, new Dimension(1024,768));
	private  final BooleanSetting fullscreen = SettingsManager.getInstance().createBooleanSetting(SETTING_NAME_FULLSCREEN, false);
	private  final BooleanSetting vsync = SettingsManager.getInstance().createBooleanSetting("screen.vsync", false);
	private  final BooleanSetting showGrid = SettingsManager.getInstance().createBooleanSetting(DESIGN_MODE_GRID, true);
	private  final BooleanSetting showLayers = new BooleanSetting(SHOW_LAYERS,false);
	private  final KeySetting layerKey =  SettingsManager.getInstance().createKeySetting("layers.key",KeyInput.KEY_L);
	private  final BooleanSetting muteSound = SettingsManager.getInstance().createBooleanSetting(MUTE_SOUND,false);
	private  final FloatSetting volume =  SettingsManager.getInstance().createFloatSetting(VOLUME,1f);
	private  final BooleanSetting soundEnabled = SettingsManager.getInstance().createBooleanSetting(SOUND_ENABLED,true);
	private  final BooleanSetting showSoundControls = SettingsManager.getInstance().createBooleanSetting(SHOW_SOUND_CONTROLS,true);
	private  final BooleanSetting showInstruments = new BooleanSetting(SHOW_INSTRUMENTS,true);

	private  final BooleanSetting machineChanged = new BooleanSetting(MACHINE_CHANGED,false);

	
	public BooleanSetting getMachineChanged() {
		return machineChanged;
	}

	public BooleanSetting getShowInstruments() {
		return showInstruments;
	}


	private  final IntegerSetting maxMemory =  SettingsManager.getInstance().createIntegerSetting(MAX_MEMORY,256 + 128);
	
	private  final BooleanSetting showSensorsPhysics = new BooleanSetting(SHOW_SENSORS_PHYSICS,false);

	
	public BooleanSetting getShowSensorsPhysics() {
		return showSensorsPhysics;
	}

	public IntegerSetting getMaxMemory() {
		return maxMemory;
	}

	public BooleanSetting getShowSoundControls() {
		return showSoundControls;
	}

	public BooleanSetting getSoundEnabled() {
		return soundEnabled;
	}

	public BooleanSetting getMuteSound() {
		return muteSound;
	}

	public FloatSetting getVolume() {
		return volume;
	}

	public BooleanSetting getShowLayers() {
		return showLayers;
	}

	public BooleanSetting getShowGrid() {
		return showGrid;
	}

	public BooleanSetting getVsync() {
		return vsync;
	}



	private  final StringSetting appVersion = SettingsManager.getInstance().createStringSetting("app.version", "(Unknown)");


	private  final IntegerSetting demoVersion = SettingsManager.getInstance().createIntegerSetting("demo.version", -1);

	private  final IntegerSetting bitDepth = SettingsManager.getInstance().createIntegerSetting(SETTING_NAME_BIT_DEPTH, 32);

	private  final FloatSetting physicsSpeed= SettingsManager.getInstance().createFloatSetting(SETTING_NAME_PHYSICS_SPEED, 1f);
		
	private  final FloatSetting physicsOverideSpeed= new FloatSetting("Overide Speed", Float.NaN);
	
	private  final IntegerSetting quality =  SettingsManager.getInstance().createIntegerSetting("display.quality",-5);

	private  final ActionSetting picture = new ActionSetting();
	
	private  final KeySetting pictureKey =  SettingsManager.getInstance().createKeySetting("pictureKey",KeyInput.KEY_F5);
	private  final ActionSetting test = new ActionSetting();
	
	private  final KeySetting cameraForwardKey =  SettingsManager.getInstance().createKeySetting("camera.forward.key",KeyInput.KEY_PGUP);
	private  final ActionSetting cameraForward = new ActionSetting();
	
	private  final KeySetting cameraBackwardKey=  SettingsManager.getInstance().createKeySetting("camera.backward.key",KeyInput.KEY_PGDN);
	private  final ActionSetting cameraBackward = new ActionSetting();
	
	
	private  final ActionSetting cameraDefault = new ActionSetting();
	private  final KeySetting cameraDefaultKey = SettingsManager.getInstance().createKeySetting("camera.default.key",KeyInput.KEY_HOME);
	
	private  final BooleanSetting cameraZoom = new BooleanSetting("Zoom", false);
	private  final KeySetting cameraZoomKey = SettingsManager.getInstance().createKeySetting("camera.zoom.key",KeyInput.KEY_Q);
	private  final StringSetting lastFile =SettingsManager.getInstance().createStringSetting("file.last", "_autosave.mchn");


	public StringSetting getLastFile() {
		return lastFile;
	}


	private final BooleanSetting showToolTips =SettingsManager.getInstance().createBooleanSetting("tooltips.visible",true);
	

	private  final KeySetting testKey =  SettingsManager.getInstance().createKeySetting("testKey",KeyInput.KEY_F10);
	private final  ListenerGroup stateSettings = new ListenerGroup();




	public BooleanSetting getShowToolTips() {
		return showToolTips;
	}

	/**
	 * Note - these settings are added to the input layer statically, they do not need to be added again
	 * @return
	 */
	public  ListenerGroup getStateSettings() {
		return stateSettings;
	}

	public BooleanSetting getAutoCloseMainMenu() {
		return autoCloseMainMenu;
	}


	public DimensionSetting getMenuSize() {
		return menuSize;
	}

	public ActionSetting getMenu() {
		return menu;
	}

	public KeySetting getMenuKey() {
		return menuKey;
	}


	public DimensionSetting getResolution() {
		return resolution;
	}

	public BooleanSetting getFullscreen() {
		return fullscreen;
	}







	public StringSetting getAppVersion() {
		return appVersion;
	}







	public IntegerSetting getDemoVersion() {
		return demoVersion;
	}







	public IntegerSetting getBitDepth() {
		return bitDepth;
	}







	public FloatSetting getPhysicsSpeed() {
		return physicsSpeed;
	}







	public FloatSetting getPhysicsOverideSpeed() {
		return physicsOverideSpeed;
	}







	public IntegerSetting getQuality() {
		return quality;
	}







	public ActionSetting getPicture() {
		return picture;
	}







	public KeySetting getPictureKey() {
		return pictureKey;
	}







	public ActionSetting getTest() {
		return test;
	}







	public KeySetting getCameraForwardKey() {
		return cameraForwardKey;
	}







	public ActionSetting getCameraForward() {
		return cameraForward;
	}







	public KeySetting getCameraBackwardKey() {
		return cameraBackwardKey;
	}







	public ActionSetting getCameraBackward() {
		return cameraBackward;
	}







	public ActionSetting getCameraDefault() {
		return cameraDefault;
	}



	public KeySetting getLayerKey() {
		return layerKey;
	}




	public KeySetting getCameraDefaultKey() {
		return cameraDefaultKey;
	}







	public BooleanSetting getCameraZoom() {
		return cameraZoom;
	}







	public KeySetting getCameraZoomKey() {
		return cameraZoomKey;
	}







	public KeySetting getTestKey() {
		return testKey;
	}


}
