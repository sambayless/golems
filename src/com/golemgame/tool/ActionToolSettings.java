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
package com.golemgame.tool;


import java.util.Collection;

import com.golemgame.platform.PlatformManager;
import com.golemgame.settings.ActionSetting;
import com.golemgame.settings.BooleanSetting;
import com.golemgame.settings.FloatSetting;
import com.golemgame.settings.KeySetting;
import com.golemgame.settings.SettingChangedEvent;
import com.golemgame.settings.SettingsListener;
import com.golemgame.settings.SettingsManager;
import com.golemgame.settings.Vector3fSetting;
import com.golemgame.states.StateManager;
import com.golemgame.util.input.InputLayer;
import com.golemgame.util.input.ListenerGroup;
import com.golemgame.views.ObservableViewManager.ViewListener;
import com.golemgame.views.Viewable.ViewMode;
import com.jme.input.KeyInput;
import com.jme.input.KeyInputListener;
import com.jme.math.Vector3f;

public class ActionToolSettings 
{
	private static final ActionToolSettings instance = new ActionToolSettings();
	private static final int GROUP_SELECT = 0;
	private static final int GROUP_CREATE = 1;
	private static final int GROUP_DELETE = 2;
	
	public static ActionToolSettings getInstance() {
		return instance;
	}
	/*
	 * Changes: First off, tools no longer deal with attaching listeners and such.
	 * Instead, there is a tool layer in the input layer system.
	 * 
	 * Get rid of action selection tool, change it to an interface.
	 * Get rid of the transfer tool system, completely redo it. The main purpose of this system
	 * is to ensure that the mouse is still on the object when you try and select it.
	 * 
	 * One option: Just add, to the tool layer, listeners for the shift and control shortcuts. Then 
	 * select from scratch at that point.
	 * 
	 * The Tool maintains a single selected actionable at any one time, and a single employed tool.
	 * The tool enforces selection and DESELECTION at tool changes and selected actionable changes.
	 * It also distributes the listened events to the current tools.
	 * 
	 * 
	 * 
	 */
	private ActionToolSettings() {
		super();
		XYAxisActionKey.addKeyListener(new KeyInputListener()
		{
			
			public void onKey(char character, int keyCode, boolean pressed) {
					XYAxisAction.setValue(pressed);
			}			
		});
		
		YZAxisActionKey.addKeyListener(new KeyInputListener()
		{
			
			public void onKey(char character, int keyCode, boolean pressed) {
					YZAxisAction.setValue(pressed);
			}			
		});
		
		XZAxisActionKey.addKeyListener(new KeyInputListener()
		{
			
			public void onKey(char character, int keyCode, boolean pressed) {
					XZAxisAction.setValue(pressed);
			}			
		});
		
		
		snap.addSettingsListener(new SettingsListener<Boolean>()
				{
					
					public void valueChanged(SettingChangedEvent<Boolean> e) 
					{		
						setRestrictMovement(e.getNewValue());			
					}				
				},true);	
		
		snapKey.addKeyListener(new KeyInputListener()
		{

			
			public void onKey(char character, int keyCode, boolean pressed) {
				if (pressed)
					snap.setValue(!snap.isValue());
			}
			
		});
		
		focusKey.addKeyListener(new KeyInputListener()
		{
			
			public void onKey(char character, int keyCode, boolean pressed) {
				if (pressed)
					focus.initiateAction();
			}			
		});
		
		deleteKey.addKeyListener(new KeyInputListener()
		{
			
			public void onKey(char character, int keyCode, boolean pressed) {
				if (pressed)
					delete.initiateAction();
			}			
		});
		
		secondaryDeleteKey.addKeyListener(new KeyInputListener()
		{
			
			public void onKey(char character, int keyCode, boolean pressed) {
				if (pressed)
					delete.initiateAction();
			}			
		});
		
		copyKey.addKeyListener(new KeyInputListener()
		{
			
			public void onKey(char character, int keyCode, boolean pressed) {
				if (pressed)
					copy.initiateAction();
			}			
		});
		
		modifyKey.addKeyListener(new KeyInputListener()
		{
			
			public void onKey(char character, int keyCode, boolean pressed) 
			{
				modify.setValue(pressed);
			}			
		});
		
		rotateKey.addKeyListener(new KeyInputListener()
		{
			
			public void onKey(char character, int keyCode, boolean pressed) 
			{
				rotate.setValue(pressed);
			}			
		});
		
		propertiesKey.addKeyListener(new KeyInputListener()
		{
			
			public void onKey(char character, int keyCode, boolean pressed) {
				if (pressed)
					properties.initiateAction();
			}			
		});
		
		groupSelectionKey.addKeyListener(new KeyInputListener()
		{
			
			public void onKey(char character, int keyCode, boolean pressed) {
				if (pressed)
					groupSelectionMode.setValue(!groupSelectionMode.isValue());
			}			
		});
		
		multipleSelectKey.addKeyListener(new KeyInputListener()
		{
			
			public void onKey(char character, int keyCode, boolean pressed) {
				
					multipleSelect.setValue(pressed);
			}			
		});
		
		singleSnapKey.addKeyListener(new KeyInputListener()
		{
			
			public void onKey(char character, int keyCode, boolean pressed) {
				if(pressed)
					useSnapSingleAxis.setValue(!useSnapSingleAxis.isValue());
			}			
		});
		
		staticsKey.addKeyListener(new KeyInputListener()
		{
			
			public void onKey(char character, int keyCode, boolean pressed) {
				if(pressed)
					staticsSelectable.setValue(!staticsSelectable.isValue());
			}			
		});
		
		assignGroupKey.addKeyListener(new KeyInputListener()
		{

			public void onKey(char character, int keyCode, boolean pressed) {
				if (pressed)
					assignGroup.initiateAction();
				
			}
			
		});
		
		wireModeKey.addKeyListener(new KeyInputListener()
		{

			public void onKey(char character, int keyCode, boolean pressed) {
				if (pressed)
					wireMode.setValue(!wireMode.isValue());
				
			}
			
		});
		
		this.gridUnits.setIsTransient(true);
		this.gridOrigin.setIsTransient(true);
		this.snapSingleAxis.setIsTransient(true);	
		this.useSnapSingleAxis.setIsTransient(true);
		this.useSnapSingleAxis.setValue(false);
		this.staticsSelectable.setIsTransient(true);
		multipleSelect.setIsTransient(true);
		wireMode.setIsTransient(true);
		groupSelectionMode.setIsTransient(true);
		rotate.setIsTransient(true);
		modify.setIsTransient(true);
		this.getXYAxisAction().setIsTransient(true);
		this.getXZAxisAction().setIsTransient(true);
		this.getYZAxisAction().setIsTransient(true);
		
		getActionSettings().addKeyListener(multipleSelectKey);
		getActionSettings().addKeyListener(groupSelectionKey);
		actionSettings.addKeyListener(getSnapKey());
		actionSettings.addKeyListener(getModifyKey());
		actionSettings.addKeyListener(getRotateKey());	
		actionSettings.addKeyListener(getXYAxisActionKey());
		actionSettings.addKeyListener(getYZAxisActionKey());
		actionSettings.addKeyListener(getXZAxisActionKey());
		actionSettings.addKeyListener(this.singleSnapKey);
		actionSettings.addKeyListener(getFocusKey());		
		actionSettings.addKeyListener(getPropertiesKey());
		actionSettings.addKeyListener(getCopyKey());
		actionSettings.addKeyListener(getDeleteKey());
		actionSettings.addKeyListener(getSecondaryDeleteKey());
		actionSettings.addKeyListener(getStaticsKey());
		actionSettings.addKeyListener(assignGroupKey);
		actionSettings.addKeyListener(wireModeKey);
		
		InputLayer.get().addKeyListener(actionSettings, InputLayer.SETTINGS_KEY_LAYER);
		
		groupMode.addSettingsListener(new SettingsListener<Boolean>()
				{

					public void valueChanged(SettingChangedEvent<Boolean> e) {
						if (e.getNewValue())
						{					
							ActionToolSettings.getInstance().getGroupSelectionMode().setValue( false); 
							
								StateManager.getViewManager().addViewMode(ViewMode.GROUPMODE);	
								StateManager.getViewManager().removeViewMode(ViewMode.MATERIAL);
						}else
						{
							StateManager.getViewManager().addViewMode(ViewMode.MATERIAL);
							StateManager.getViewManager().removeViewMode(ViewMode.GROUPMODE);
							
							StateManager.getViewManager().removeViewMode(ViewMode.FUNCTIONAL);
						}
					}
			
				});
		
		StateManager.getViewManager().registerListener(new ViewListener()
		{

			public void viewChanged(Collection<ViewMode> views) {
				if (views.contains(ViewMode.GROUPMODE))
				{
					groupMode.setValue(true);
				}else
					groupMode.setValue(false);
			}
			
		});
		
	}
	

	private  final ActionSetting assignGroup = new ActionSetting();
	private  final KeySetting assignGroupKey = SettingsManager.getInstance().createKeySetting("group.assign.key",KeyInput.KEY_G);
	
	
	private final BooleanSetting groupMode = new BooleanSetting("Group Mode", false);
	
	public BooleanSetting getAssignedGroupSelectionMode() {
		return groupMode;
	}
	public ActionSetting getAssignGroup() {
		return assignGroup;
	}

	public KeySetting getAssignGroupKey() {
		return assignGroupKey;
	}
	
	private  final BooleanSetting XYAxisAction =  SettingsManager.getInstance().createBooleanSetting("XYAxisAction",false);
	private  final BooleanSetting YZAxisAction =  SettingsManager.getInstance().createBooleanSetting("YZAxisAction",false);
	private  final BooleanSetting XZAxisAction =  SettingsManager.getInstance().createBooleanSetting("XZAxisAction",true);
	
	private  final KeySetting XYAxisActionKey =  SettingsManager.getInstance().createKeySetting("XYAxisActionKey",KeyInput.KEY_Z);

	private  final KeySetting YZAxisActionKey =  SettingsManager.getInstance().createKeySetting("YZAxisActionKey",KeyInput.KEY_X);

	private  final KeySetting XZAxisActionKey =  SettingsManager.getInstance().createKeySetting("XZAxisActionKey", KeyInput.KEY_CAPITAL);




	private  final Vector3fSetting gridUnits =  SettingsManager.getInstance().createVectorSetting("gridUnits", new Vector3f(1,1,1));
	private  final Vector3fSetting gridOrigin =  SettingsManager.getInstance().createVectorSetting("gridOrigin",new Vector3f(0,0,0));
	
	private  final BooleanSetting staticsSelectable =  SettingsManager.getInstance().createBooleanSetting("staticsSelectable",false);

	
	public  BooleanSetting getStaticsSelectable() {
		return staticsSelectable;
	}


	private  final BooleanSetting snap =  SettingsManager.getInstance().createBooleanSetting("snap",false);

	private  final KeySetting snapKey =  SettingsManager.getInstance().createKeySetting("snap.key", KeyInput.KEY_CAPITAL);

	private  final ActionSetting focus = new ActionSetting();	
	private static final ActionSetting delete = new ActionSetting();	
	private static final ActionSetting copy = new ActionSetting();	
	private static final ActionSetting properties = new ActionSetting();
	
	private  final KeySetting focusKey =  SettingsManager.getInstance().createKeySetting("focus.key",KeyInput.KEY_F);

	

	private  final KeySetting secondaryDeleteKey =  SettingsManager.getInstance().createKeySetting("secondaryDelete.key",KeyInput.KEY_BACK);
	{

	}
	private  final KeySetting deleteKey =  SettingsManager.getInstance().createKeySetting("delete.key",KeyInput.KEY_DELETE);
	{

	}
	private  final KeySetting copyKey =  SettingsManager.getInstance().createKeySetting("copy.key",KeyInput.KEY_C);
	{

	}
	
	private  final FloatSetting rotationSnapStep =  SettingsManager.getInstance().createFloatSetting("snap.rotation.steo",12f);

	
	public FloatSetting getRotationSnapStep() {
		return rotationSnapStep;
	}

	private  final BooleanSetting wireMode =  SettingsManager.getInstance().createBooleanSetting("wires",false);
	private  final KeySetting wireModeKey=  SettingsManager.getInstance().createKeySetting("wires.key",KeyInput.KEY_GRAVE);

	
	public KeySetting getWireModeKey() {
		return wireModeKey;
	}
	public BooleanSetting getWireMode() {
		return wireMode;
	}

	private  final BooleanSetting multipleSelect =  SettingsManager.getInstance().createBooleanSetting("select.multiple",false);
	private  final KeySetting multipleSelectKey=  SettingsManager.getInstance().createKeySetting("select.multiple.key",PlatformManager.get().getDefaultKeyBindings().getMultipleSelectKey());

	private final BooleanSetting useSnapSingleAxis =   SettingsManager.getInstance().createBooleanSetting("snap.single",false);
	private final Vector3fSetting snapSingleAxis =   SettingsManager.getInstance().createVectorSetting("snap.single.axis",new Vector3f(0,1,0));
	private  final KeySetting singleSnapKey =  SettingsManager.getInstance().createKeySetting("snap.single.key",KeyInput.KEY_TAB);

	
	public KeySetting getSingleSnapKey() {
		return singleSnapKey;
	}
	public BooleanSetting getUseSnapSingleAxis() {
		return useSnapSingleAxis;
	}
	public Vector3fSetting getSnapSingleAxis() {
		return snapSingleAxis;
	}
	public BooleanSetting getMultipleSelect() {
		return multipleSelect;
	}
	
	private  final BooleanSetting modify =  SettingsManager.getInstance().createBooleanSetting("modify",false);
	private  final KeySetting modifyKey=  SettingsManager.getInstance().createKeySetting("mmodify.key",KeyInput.KEY_LMENU);

	private  final KeySetting staticsKey=  SettingsManager.getInstance().createKeySetting("staticsSelectable.key",KeySetting.NO_KEY);

	
	private  final BooleanSetting rotate =  SettingsManager.getInstance().createBooleanSetting("rotate",false);
	private  final KeySetting rotateKey=  SettingsManager.getInstance().createKeySetting("rotate.key",KeyInput.KEY_LSHIFT);

	
	private  final KeySetting propertiesKey =  SettingsManager.getInstance().createKeySetting("properties.key",KeyInput.KEY_RETURN);

	
	private  final KeySetting groupSelectionKey = SettingsManager.getInstance().createKeySetting("groupSelectionMode.key");


	private  final BooleanSetting groupSelectionMode = SettingsManager.getInstance().createBooleanSetting("groupSelectionMode",false);
	public  void setGroupSelectionMode(boolean selectGroups) {
		groupSelectionMode.setValue(selectGroups);
	}
	public  boolean isGroupSelectionMode() {
		return groupSelectionMode.isValue();
	}

	
	public  KeySetting getGroupSelectionKey() {
		return groupSelectionKey;
	}

	public  BooleanSetting getGroupSelectionMode() {
		return groupSelectionMode;
	}

	
	public KeySetting getMultipleSelectKey() {
		return multipleSelectKey;
	}
	public KeySetting getStaticsKey() {
		return staticsKey;
	}

	/**
	 * A group of settings that should receive key and mouse input from any context in the main game interface.
	 * This is only a subset of all settings that apply to action tools.
	 */
	private final  ListenerGroup actionSettings = new ListenerGroup();
	


	public  BooleanSetting getSnap() {
		return snap;
	}

	public  KeySetting getSnapKey() {
		return snapKey;
	}

	public  Vector3fSetting getGridUnits() {
		return gridUnits;
	}

	public  Vector3fSetting getGridOrigin() {
		return gridOrigin;
	}

	public  KeySetting getFocusKey() {
		return focusKey;
	}

	public  KeySetting getDeleteKey() {
		return deleteKey;
	}

	public  KeySetting getCopyKey() {
		return copyKey;
	}

	public  BooleanSetting getModify() {
		return modify;
	}

	public  KeySetting getModifyKey() {
		return modifyKey;
	}

	public  ActionSetting getFocus() {
		return focus;
	}

	public  ActionSetting getDelete() {
		return delete;
	}

	public  ActionSetting getCopy() {
		return copy;
	}

	public  ActionSetting getProperties() {
		return properties;
	}

	public  KeySetting getPropertiesKey() {
		return propertiesKey;
	}

	public  BooleanSetting getRotate() {
		return rotate;
	}

	public  KeySetting getRotateKey() {
		return rotateKey;
	}

	/**
	 * Note - these settings are added to the input layer statically, they do not need to be added again
	 * @return
	 */
	public  ListenerGroup getActionSettings() {
		return actionSettings;
	}

	public  BooleanSetting getXYAxisAction() {
		return XYAxisAction;
	}

	public  BooleanSetting getYZAxisAction() {
		return YZAxisAction;
	}

	public  BooleanSetting getXZAxisAction() {
		return XZAxisAction;
	}

	public  KeySetting getXYAxisActionKey() {
		return XYAxisActionKey;
	}

	public  KeySetting getYZAxisActionKey() {
		return YZAxisActionKey;
	}

	public  KeySetting getXZAxisActionKey() {
		return XZAxisActionKey;
	}

	public  KeySetting getSecondaryDeleteKey() {
		return secondaryDeleteKey;
	}

	protected  void setRestrictMovement(boolean restrictMovement) {
		getSnap().setValue(restrictMovement);
	}

	public  boolean isRestrictMovement() {
		return getSnap().isValue();
	}
	
	
	
}
