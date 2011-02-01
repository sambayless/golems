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

import org.fenggui.CheckBox;
import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.IWidget;
import org.fenggui.Label;
import org.fenggui.ListItem;
import org.fenggui.TextEditor;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.event.key.IKeyListener;
import org.fenggui.event.key.Key;
import org.fenggui.event.key.KeyPressedEvent;
import org.fenggui.event.key.KeyReleasedEvent;
import org.fenggui.event.key.KeyTypedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.GridLayout;
import org.fenggui.layout.RowLayout;
import org.fenggui.util.Color;
import org.fenggui.util.Spacing;

import com.golemgame.menu.KeyMappingConversion;
import com.golemgame.mvc.golems.input.InputDeviceInterpreter;
import com.golemgame.mvc.golems.input.KeyboardInputDeviceInterpreter;
import com.golemgame.mvc.golems.input.SliderInputDeviceInterpreter;
import com.golemgame.mvc.golems.input.InputDeviceInterpreter.InputType;
import com.golemgame.mvc.golems.input.KeyboardInputDeviceInterpreter.KeyEventType;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;
import com.simplemonkey.Widget;

/**
 * This tab lets users select the device type, and name the device.
 * The actual device settings are in a sub tab
 * @author Sam
 *
 */
public class InputDeviceTab extends PropertyTabAdapter{

	public InputDeviceTab() {
		super("Input");
		
	}

	private InputDeviceInterpreter interpreter;
	
	private ComboBox<InputType> dropDown;
	private CheckBox<?> horizontal;
	private CheckBox<?> showArrows;
	private CheckBox<?> outputZero;
	private TextEditor initialValue;
	private TextEditor key;
	private int currentKey;
	private ComboBox<KeyEventType> keyEvent;
	private Container deviceContainer;
	private Container deviceCenterContainer;
	private KeyboardInputDeviceInterpreter keyInterpreter;
	
	private CheckBox<?> locked ;
	private CheckBox<?> windowed ;
	
	private TextEditor xPosition;
	private TextEditor yPosition;
	private TextEditor width;
	private TextEditor height;
	private Container windowControls ;
	public void close(boolean cancel) {
		if(!cancel)
		{
	/*		if(super.isAltered(dropDown))
			{
				InputType selected = dropDown.getSelectedItem().getValue();
				interpreter.setDeviceType(selected);
			}	
			*/
			super.standardClosingBehaviour(horizontal, SliderInputDeviceInterpreter.IS_HORIZONTAL);
			super.standardClosingBehaviour(showArrows, SliderInputDeviceInterpreter.SHOW_ARROWS);
			super.standardClosingBehaviour(initialValue, SliderInputDeviceInterpreter.INITIAL_VALUE, Format.Float);
			super.standardClosingBehaviour( dropDown,InputDeviceInterpreter.DEVICE_TYPE);
			super.standardClosingBehaviour(outputZero,  KeyboardInputDeviceInterpreter.OUTPUT_NEGATIVE);//this has to happen first

		//	super.standardClosingBehaviour(key, KeyboardDeviceInterpreter.KEY);
			super.standardClosingBehaviour(keyEvent, KeyboardInputDeviceInterpreter.KEY_EVENT);

			if(super.isAltered(key))
			{
							keyInterpreter.setKeyCode(currentKey);
				super.setValueAltered(KeyboardInputDeviceInterpreter.KEY,true);
			}
			
			
			if(super.isAltered(xPosition)||super.isAltered(yPosition) || super.isAltered(width) || super.isAltered(height))
			{
				interpreter.setInstrumentUserPositioned(true);
			}
			
			super.standardClosingBehaviour(xPosition, InputDeviceInterpreter.INSTRUMENT_X, Format.ProportionalX);
			super.standardClosingBehaviour(yPosition, InputDeviceInterpreter.INSTRUMENT_Y, Format.ProportionalY);
			super.standardClosingBehaviour(width, InputDeviceInterpreter.INSTRUMENT_WIDTH, Format.ProportionalX);
			super.standardClosingBehaviour(height, InputDeviceInterpreter.INSTRUMENT_HEIGHT, Format.ProportionalY);

			super.standardClosingBehaviour(locked, InputDeviceInterpreter.INSTRUMENT_LOCKED);
			super.standardClosingBehaviour(windowed, InputDeviceInterpreter.INSTRUMENT_WINDOWED);
			
			Action<?> apply = super.apply();
			
			UndoManager.getInstance().addAction(apply);

			
		}
		
	}
	


	protected void buildGUI()
	{
		
	
		getTab().setLayoutManager(new BorderLayout());
		Container upperControls = FengGUI.createContainer(getTab());
		upperControls.setLayoutManager(new RowLayout(false));
		upperControls.setLayoutData(BorderLayoutData.NORTH);
	      
	    Container comboFrame = FengGUI.createContainer(upperControls);
	   
		comboFrame.setLayoutData(BorderLayoutData.NORTH);
		comboFrame.setLayoutManager(new BorderLayout());
		dropDown = FengGUI.<InputType>createComboBox();// new ComboBox<Material>();
		
		dropDown.getAppearance().setPadding(new Spacing(0,5));
		dropDown.setLayoutData(BorderLayoutData.CENTER);
		dropDown.getAppearance().add(new PlainBackground(Color.WHITE));
		
		Label labelCombo = FengGUI.createLabel(comboFrame,"Input Type: ");
		labelCombo.setLayoutData(BorderLayoutData.WEST);
		comboFrame.addWidget(dropDown);
		comboFrame.layout();
		
		for (InputType inputType:InputType.values())
		{	
			dropDown.addItem(new ListItem<InputType>(inputType.toString(),inputType));			
		}
		
		dropDown.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent selectionChangedEvent) 
			{			
				if (selectionChangedEvent.isSelected())
				{
					InputType selected = dropDown.getSelectedItem().getValue();
					displayInputDialog(selected);
				}
			}
		});	

		deviceContainer = FengGUI.createContainer(upperControls);
		deviceContainer.setLayoutData(BorderLayoutData.CENTER);
		deviceContainer.setLayoutManager(new RowLayout(false));
		deviceCenterContainer = FengGUI.createContainer();
		
		windowControls = FengGUI.createContainer();
		windowControls.setLayoutManager(new RowLayout(false));
		Container checkContainer = FengGUI.createContainer(windowControls);
		checkContainer.setLayoutManager(new RowLayout());
		windowed = FengGUI.createCheckBox(checkContainer,"Show Window Frame");
		locked= FengGUI.createCheckBox(checkContainer,"Lock Instrument Window");
		Container positionControls = FengGUI.createContainer(windowControls);
		positionControls.setLayoutManager(new RowLayout(false));
		Container upperPos = FengGUI.createContainer(positionControls);
		upperPos.setLayoutManager(new RowLayout());
		Container lowerPos = FengGUI.createContainer(positionControls);
		lowerPos.setLayoutManager(new RowLayout());
		FengGUI.createLabel(upperPos,"X:").setExpandable(false);
		xPosition = FengGUI.createTextEditor(upperPos);
		FengGUI.createLabel(upperPos,"Y:").setExpandable(false);
		yPosition = FengGUI.createTextEditor(upperPos);
		FengGUI.createLabel(lowerPos,"Width:").setExpandable(false);
		width = FengGUI.createTextEditor(lowerPos);
		FengGUI.createLabel(lowerPos,"Height:").setExpandable(false);
		height = FengGUI.createTextEditor(lowerPos);
		
		
		
		horizontal = FengGUI.createCheckBox();
		horizontal.setText("Horizontal");
		showArrows = FengGUI.createCheckBox();
		showArrows.setText("Show Slider Arrows");
		initialValue = FengGUI.createTextEditor();
		
		outputZero = FengGUI.createCheckBox();
		outputZero.setText("Output -1 when unpressed (0 otherwise)");
		
		keyEvent = FengGUI.<KeyEventType>createComboBox();
		for (KeyEventType inputType:KeyEventType.values())
		{	
			keyEvent.addItem(new ListItem<KeyEventType>(inputType.toString(),inputType));			
		}
		
		key = FengGUI.createTextEditor();
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
        getTab().layout();
		
	}
	
	protected void setCurrentKey(Key keyClass, char key) {
		this.currentKey = KeyMappingConversion.getJMEKey(keyClass, key);
		
	}

	private void displayInputDialog(InputType selected) {
		deviceContainer.removeAllWidgets();
		deviceCenterContainer.removeAllWidgets();
		switch(selected)
		{		
			case SLIDER:{
				deviceContainer.addWidget(deviceCenterContainer);
				deviceCenterContainer.setLayoutManager(new RowLayout(false));
				deviceCenterContainer.setLayoutData(BorderLayoutData.NORTH);
				deviceCenterContainer.addWidget(horizontal);
				deviceCenterContainer.addWidget(showArrows);

				deviceContainer.addWidget(windowControls);
				break;}		
			case TEXT:{
				deviceContainer.addWidget(deviceCenterContainer);
				deviceCenterContainer.setLayoutManager(new RowLayout(false));
				deviceCenterContainer.setLayoutData(BorderLayoutData.NORTH);
				deviceCenterContainer.addWidget(FengGUI.createLabel(deviceCenterContainer,"A text box that users can use to set the output value directly."));
				

				deviceContainer.addWidget(windowControls);
				
				break;}		
			case KEYBOARD:{
				deviceContainer.addWidget(deviceCenterContainer);
				deviceCenterContainer.setLayoutManager(new GridLayout(3,2));
				deviceCenterContainer.setLayoutData(BorderLayoutData.NORTH);
			
				deviceCenterContainer.addWidget(FengGUI.createLabel(deviceCenterContainer,"Key Event Type"));
				deviceCenterContainer.addWidget(keyEvent);
			
				deviceCenterContainer.addWidget(FengGUI.createLabel(deviceCenterContainer,"Key"));
				deviceCenterContainer.addWidget(key);
				
				//deviceContainer.addWidget(windowControls);
				
				FengGUI.createLabel(deviceCenterContainer,"");
				deviceCenterContainer.addWidget(outputZero);
				break;}
		}
		windowControls.layoutToRoot();
		deviceCenterContainer.layoutToRoot();
		windowControls.layoutToRoot();
		deviceCenterContainer.layoutToRoot();
	

	}
	
	public void open() {
		this.interpreter = new InputDeviceInterpreter(super.getPropertyStoreAdjuster().getPrototype());
		this.keyInterpreter = new KeyboardInputDeviceInterpreter(interpreter.getStore());

		this.interpreter.loadDefaults();
		initializePrototype();
		super.setComparePoint();

		super.associateWithKey(dropDown, InputDeviceInterpreter.DEVICE_TYPE);
		super.associateWithKey(horizontal, SliderInputDeviceInterpreter.IS_HORIZONTAL);
		super.associateWithKey(showArrows, SliderInputDeviceInterpreter.SHOW_ARROWS);
		super.associateWithKey(initialValue, SliderInputDeviceInterpreter.INITIAL_VALUE);
		//super.associateWithKey(key, KeyboardDeviceInterpreter.KEY);
		super.associateWithKey(keyEvent, KeyboardInputDeviceInterpreter.KEY_EVENT);
		super.associateWithKey(outputZero, KeyboardInputDeviceInterpreter.OUTPUT_NEGATIVE);

		super.standardOpeningBehaviour(outputZero,  KeyboardInputDeviceInterpreter.OUTPUT_NEGATIVE);//this has to happen first

		super.standardOpeningBehaviour(keyEvent,  KeyboardInputDeviceInterpreter.KEY_EVENT);//this has to happen first
	//	super.standardOpeningBehaviour(key,  KeyboardDeviceInterpreter.KEY,Format.KeyCode);//this has to happen first
		super.standardOpeningBehaviour(initialValue,  SliderInputDeviceInterpreter.INITIAL_VALUE,Format.Float);//this has to happen first
		super.standardOpeningBehaviour(showArrows,  SliderInputDeviceInterpreter.SHOW_ARROWS);//this has to happen first
		super.standardOpeningBehaviour(horizontal,  SliderInputDeviceInterpreter.IS_HORIZONTAL);//this has to happen first
		super.standardOpeningBehaviour(dropDown,  InputDeviceInterpreter.DEVICE_TYPE);//this has to happen first

		this.key.setText(KeyMappingConversion.getKeyDescription( keyInterpreter.getKeyCode()));
		super.setUnaltered(key);

		super.associateWithKey(locked, InputDeviceInterpreter.INSTRUMENT_LOCKED);
		super.associateWithKey(xPosition, InputDeviceInterpreter.INSTRUMENT_X);
		super.associateWithKey(yPosition, InputDeviceInterpreter.INSTRUMENT_Y);
		super.associateWithKey(width, InputDeviceInterpreter.INSTRUMENT_WIDTH);
		super.associateWithKey(height, InputDeviceInterpreter.INSTRUMENT_HEIGHT);
		super.associateWithKey(windowed, InputDeviceInterpreter.INSTRUMENT_WINDOWED);
		super.standardOpeningBehaviour(windowed, InputDeviceInterpreter.INSTRUMENT_WINDOWED);
		super.standardOpeningBehaviour(xPosition, InputDeviceInterpreter.INSTRUMENT_X, Format.ProportionalX);
		super.standardOpeningBehaviour(yPosition, InputDeviceInterpreter.INSTRUMENT_Y, Format.ProportionalY);
		super.standardOpeningBehaviour(width, InputDeviceInterpreter.INSTRUMENT_WIDTH, Format.ProportionalX);
		super.standardOpeningBehaviour(height, InputDeviceInterpreter.INSTRUMENT_HEIGHT, Format.ProportionalY);
		super.standardOpeningBehaviour(locked, InputDeviceInterpreter.INSTRUMENT_LOCKED);
		
		
		displayInputDialog(interpreter.getDeviceType());
	}


}
