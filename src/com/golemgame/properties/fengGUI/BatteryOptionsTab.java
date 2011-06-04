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
package com.golemgame.properties.fengGUI;


import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.ListItem;
import org.fenggui.TextEditor;
import org.fenggui.decorator.border.TitledBorder;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;

import com.golemgame.local.StringConstants;
import com.golemgame.mvc.golems.BatteryInterpreter;
import com.golemgame.mvc.golems.BatteryInterpreter.SwitchType;
import com.golemgame.mvc.golems.BatteryInterpreter.ThresholdType;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;


public class BatteryOptionsTab extends PropertyTabAdapter {

	private BatteryInterpreter interpreter;
	
	//private CheckBox<?> invert;

	private TextEditor threshold;
	//private CheckBox<Object> interactionEnabled;
	//private TextEditor key;
	//private int currentKey;
	//private ComboBox<KeyboardInteractionType> interactionMode;
	private ComboBox<SwitchType> switchMode;
	private ComboBox<ThresholdType> thresholdType;

	
	
	
	public BatteryOptionsTab() {
		super(StringConstants.get("PROPERTIES.BATTERY","Battery Options"));
	}

	
	protected void buildGUI() {
		
		getTab().setLayoutManager(new BorderLayout());
		
		Container innerContainer = FengGUI.createContainer(getTab());
		innerContainer.setLayoutData(BorderLayoutData.NORTH);
		innerContainer.setLayoutManager(new RowLayout(false));
		
		final Container thresholdOptions = FengGUI.createContainer(innerContainer);
		thresholdOptions.getAppearance().add(new TitledBorder(StringConstants.get("PROPERTIES.FUNCTION.SWITCH","Battery Switch")));
		
		
		thresholdOptions.setLayoutManager(new RowLayout(false));
		FengGUI.createLabel(thresholdOptions,StringConstants.get("PROPERTIES.FUNCTION.THRESHOLD_DESCRIPTION",   "Set the minimum signal needed to activate the switch."));
		
		final Container innerThresholdOptions = FengGUI.createContainer(thresholdOptions);
		innerThresholdOptions.setLayoutManager(new RowLayout());
		
		
		FengGUI.createLabel(innerThresholdOptions, StringConstants.get("PROPERTIES.FUNCTION.THRESHOLD","Switch Threshold: "));
		
		threshold = FengGUI.createTextEditor(innerThresholdOptions, "0");
		//invert = FengGUI.createCheckBox(innerThresholdOptions, "Invert Threshold");
		FengGUI.createLabel(innerThresholdOptions,StringConstants.get("PROPERTIES.FUNCTION.THRESHOLD_DESCRIPTION_2",  " Input must be"));
		
		
		thresholdType = FengGUI.<ThresholdType>createComboBox(innerThresholdOptions);
		for (ThresholdType type:ThresholdType.values())
		{
			thresholdType.addItem(new ListItem<ThresholdType>(type.getDescription(), type));
		}
		FengGUI.createLabel(innerThresholdOptions, StringConstants.get("PROPERTIES.FUNCTION.THRESHOLD_DESCRIPTION_3", " the threshold to activate"));
		
		
		final Container switchOptions = FengGUI.createContainer(thresholdOptions);
		switchOptions.setLayoutManager(new RowLayout());
		FengGUI.createLabel(switchOptions, StringConstants.get("PROPERTIES.FUNCTION.SWITCH_ACTION", "Switch Action: "));
		switchMode = FengGUI.<SwitchType>createComboBox(switchOptions);
		
		for (SwitchType type:SwitchType.values())
		{
			switchMode.addItem(new ListItem<SwitchType>(type.getDescription(), type));
		}
		
		FengGUI.createLabel(thresholdOptions,"Example: if Threshold is 0.5, and the operation is >=, and the action is 'Off/On'");
		FengGUI.createLabel(thresholdOptions,"then the function will output 0 unless the switch recieves 0.5 or more input.");

				
		
		
		 
		/* Container interactionOptions = FengGUI.createContainer(innerContainer);
		 interactionOptions.getAppearance().add(new TitledBorder("Keyboard Activation"));
		 interactionOptions.setLayoutManager(new RowLayout(false));
		 
		 Container innerInteractionOptions = FengGUI.createContainer(interactionOptions);
		 innerInteractionOptions.setLayoutManager(new RowLayout());*/
		// interactionEnabled = FengGUI.createCheckBox(innerInteractionOptions,"Activate With Keypress");
		 
		/* interactionEnabled.addSelectionChangedListener(new ISelectionChangedListener()
		 {

			
			public void selectionChanged(
					SelectionChangedEvent selectionChangedEvent) {
			
			//	threshold.setEnabled(!interactionEnabled.isSelected());
			//	invert.setEnabled(!interactionEnabled.isSelected());
			}
			 
		 });
		 
		 key = FengGUI.createTextEditor(innerInteractionOptions, "");
		
		 key.addKeyListener(new IKeyListener()
		 {

			
			public void keyPressed(KeyPressedEvent keyPressedEvent) {
				keyPressedEvent.setUsed();
			
				//if (keyPressedEvent.getKeyClass() != Key.LETTER && keyPressedEvent.getKeyClass()!= Key.DIGIT && keyPressedEvent.getKeyClass() != Key.UNDEFINED )
					key.setText( KeyMappingConversion.getKeyName( keyPressedEvent.getKeyClass(),keyPressedEvent.getKey()).toUpperCase());
				//else
			//		key.setText("");
				setCurrentKey(keyPressedEvent.getKeyClass(),keyPressedEvent.getKey());
			}

			
			public void keyReleased(KeyReleasedEvent keyReleasedEvent) {
				keyReleasedEvent.setUsed();
				//if (keyReleasedEvent.getKeyClass() != Key.LETTER && keyReleasedEvent.getKeyClass()!= Key.DIGIT && keyReleasedEvent.getKeyClass() != Key.UNDEFINED)
				//	key.setText(KeyMappingConversion.getKeyName( keyReleasedEvent.getKeyClass(),keyReleasedEvent.getKey()));
				key.setText( KeyMappingConversion.getKeyDescription(currentKey));
		//		setCurrentKey(keyReleasedEvent.getKeyClass(),keyReleasedEvent.getKey());
		
			}

			
			public void keyTyped(KeyTypedEvent keyTypedEvent) {
			//	key.setText(getKeyName( keyTypedEvent.getKeyClass()));
			//	setCurrentKey(keyTypedEvent.getKeyClass());
				key.setText( KeyMappingConversion.getKeyDescription(currentKey));
			}

		
			 
		 });
		 
		 interactionMode = FengGUI.createComboBox(innerInteractionOptions);
		 
		 
		 interactionMode.addItem(new ListItem<KeyboardInteractionType>("Hold Down",KeyboardInteractionType.HeldDown));
		 interactionMode.addItem(new ListItem<KeyboardInteractionType>("Toggle",KeyboardInteractionType.Toggle));
		 */
	}

/*	protected void setCurrentKey(Key keyClass, char key) {
		this.currentKey = KeyMappingConversion.getJMEKey(keyClass, key);
		
	}

*/
	
	public void close(boolean cancel) {
		super.close(cancel);
		if(!cancel)
		{
			super.standardClosingBehaviour(threshold, BatteryInterpreter.THRESHOLD);
			super.standardClosingBehaviour(thresholdType, BatteryInterpreter.THRESHOLD_TYPE);
		//	super.standardClosingBehaviour(interactionEnabled, BatteryInterpreter.INTERACT);
			super.standardClosingBehaviour(switchMode, BatteryInterpreter.SWITCH_TYPE);
		//	super.standardClosingBehaviour(interactionMode, BatteryInterpreter.INTERACTION_TYPE);
			
		/*	if(super.isAltered(key))
			{
				interpreter.setInteractionKey(currentKey);
				super.setValueAltered(BatteryInterpreter.KEY_CODE,true);
			}*/
			
			Action<?> apply = super.apply();
			
			UndoManager.getInstance().addAction(apply);	
		}

	}

	
	public void open() {
		
		this.interpreter = new BatteryInterpreter(super.getPropertyStoreAdjuster().getPrototype());
		this.interpreter.loadDefaults();
		initializePrototype();
		
		super.associateWithKey(threshold, BatteryInterpreter.THRESHOLD);
		super.associateWithKey(thresholdType, BatteryInterpreter.THRESHOLD_TYPE);
	//	super.associateWithKey(interactionEnabled, BatteryInterpreter.INTERACT);
		super.associateWithKey(switchMode, BatteryInterpreter.SWITCH_TYPE);
	//	super.associateWithKey(interactionMode, BatteryInterpreter.INTERACTION_TYPE);
		
		super.standardOpeningBehaviour(threshold, BatteryInterpreter.THRESHOLD);
		super.standardOpeningBehaviour(thresholdType, BatteryInterpreter.THRESHOLD_TYPE);
	//	super.standardOpeningBehaviour(interactionEnabled, BatteryInterpreter.INTERACT);
		super.standardOpeningBehaviour(switchMode, BatteryInterpreter.SWITCH_TYPE);
	//	super.standardOpeningBehaviour(interactionMode, BatteryInterpreter.INTERACTION_TYPE);
		
		
	//	this.key.setText(KeyMappingConversion.getKeyDescription( interpreter.getInteractionKey()));
	//	super.setUnaltered(key);

	}
	


}
