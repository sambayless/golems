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
import org.fenggui.ListItem;
import org.fenggui.TextEditor;
import org.fenggui.decorator.border.TitledBorder;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.GridLayout;
import org.fenggui.layout.RowLayout;

import com.golemgame.local.StringConstants;
import com.golemgame.mvc.golems.MotorPropertiesInterpreter;
import com.golemgame.mvc.golems.MotorPropertiesInterpreter.IOType;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;


public class MotorTab extends PropertyTabAdapter {

	private MotorPropertiesInterpreter interpreter;
	
	private Container settings;

	private TextEditor velocity;
	private TextEditor acceleration;
	protected Container customOptions;
	
	private ComboBox<MotorPropertiesInterpreter.IOType> inputType;
	private ComboBox<MotorPropertiesInterpreter.IOType> outputType;
	
	private CheckBox<Boolean> isSpring;
	private TextEditor kValue;
	private TextEditor  springFriction;
	private Container pid;
	private boolean hasPID = false;
	//private final static String ACCELERATION_TOOLTOP = "By default, motors interpret their input as the velocity they should attempt to aim for (by applying up to a certain maximum amount of acceleration). However, they can instead interpret their input as the acceleration they should attempt to apply (up to a maximum velocity)";

	private CheckBox<?> showPID ;
	
	public MotorTab() {
		super(StringConstants.get("PROPERTIES.MOTOR","Motor"));
	}

	public void close(boolean cancel) {
		super.close(cancel);
		if(!cancel)
		{
			super.standardClosingBehaviour(kValue, MotorPropertiesInterpreter.SPRING_CONSTANT);
			super.standardClosingBehaviour(springFriction, MotorPropertiesInterpreter.SPRING_FRICTION);
			super.standardClosingBehaviour(isSpring, MotorPropertiesInterpreter.IS_SPRING);
			super.standardClosingBehaviour(inputType, MotorPropertiesInterpreter.INPUT_TYPE);
			super.standardClosingBehaviour(outputType, MotorPropertiesInterpreter.OUTPUT_TYPE);
			super.standardClosingBehaviour(acceleration, MotorPropertiesInterpreter.ACCELERATION);
			super.standardClosingBehaviour(velocity, MotorPropertiesInterpreter.VELOCITY);
			Action<?> apply = super.apply();
			
			UndoManager.getInstance().addAction(apply);
			//motorProperties.setInterpretAsAcceleration(useAccel.isSelected());
/*			try{
				float accel = Float.valueOf(acceleration.getText());
				motorProperties.setMaxAcceleration(accel);
			}catch(NumberFormatException e)
			{
				
			}
			
			try{
				float vel = Float.valueOf(velocity.getText());
				motorProperties.setMaxVelocity(vel);
			}catch(NumberFormatException e)
			{
				
			}
			
			motorProperties.setSpring(isSpring.isSelected());
			
			try{
				motorProperties.setSpringConstant(Float.valueOf(kValue.getText()));
			}catch(NumberFormatException e)
			{
				
			}

			
			try{
				motorProperties.setSpringFriction(Float.valueOf(springFriction.getText()));
			}catch(NumberFormatException e)
			{
				
			}
			
			motorProperties.setInputType(this.inputType.getSelectedItem().getValue());
			motorProperties.setOutputType(this.outputType.getSelectedItem().getValue());
			
			motorProperties.refresh();*/
		}
	}

	

	
	public void open() {
		super.open();
		interpreter = new MotorPropertiesInterpreter(super.getPrototype());
		
		super.associateWithKey(kValue, MotorPropertiesInterpreter.SPRING_CONSTANT);
		super.associateWithKey(springFriction, MotorPropertiesInterpreter.SPRING_FRICTION);
		super.associateWithKey(isSpring, MotorPropertiesInterpreter.IS_SPRING);
		super.associateWithKey(inputType, MotorPropertiesInterpreter.INPUT_TYPE);
		super.associateWithKey(outputType, MotorPropertiesInterpreter.OUTPUT_TYPE);
		super.associateWithKey(acceleration, MotorPropertiesInterpreter.ACCELERATION);
		super.associateWithKey(velocity, MotorPropertiesInterpreter.VELOCITY);
		
		
		super.standardOpeningBehaviour(kValue, MotorPropertiesInterpreter.SPRING_CONSTANT);
		super.standardOpeningBehaviour(springFriction, MotorPropertiesInterpreter.SPRING_FRICTION);
		super.standardOpeningBehaviour(isSpring, MotorPropertiesInterpreter.IS_SPRING);
		super.standardOpeningBehaviour(inputType, MotorPropertiesInterpreter.INPUT_TYPE);
		super.standardOpeningBehaviour(outputType, MotorPropertiesInterpreter.OUTPUT_TYPE);
		super.standardOpeningBehaviour(acceleration, MotorPropertiesInterpreter.ACCELERATION);
		super.standardOpeningBehaviour(velocity, MotorPropertiesInterpreter.VELOCITY);
		

		//refreshMotorData();
/*		kValue.setText(String.valueOf(motorProperties.getSpringConstant()));
		springFriction.setText(String.valueOf(motorProperties.getSpringFriction()));
		isSpring.setSelected(motorProperties.isSpring());
		inputType.setSelected(this.motorProperties.getInputType().toString());
		
		outputType.setSelected(this.motorProperties.getOutputType().toString());*/
	}
	
	/**
	 * Subclasses may override this class to customize this tab
	 */
	protected void buildCustomGUI()
	{
		
	}
	
	private void updatePID()
	{
		if(hasPID)
		{
			if (inputType.getSelectedItem().getValue()==IOType.POSITION )
			{
				this.showPID.setVisible(true);
			}else{
				this.showPID.setVisible(false);
			}
			
			
			if (inputType.getSelectedItem().getValue()==IOType.POSITION  && showPID.isSelected())
			{
				pid.setVisible(true);
				pid.updateMinSize();
				pid.layout();
				getTab().updateMinSize();
				getTab().setSizeToMinSize();
				getTab().layout();
				getTab().getParent().getParent().getParent().layout();//a minor hack...
			}else{
				pid.setVisible(false);
				pid.updateMinSize();
				pid.layout();
				
				
				getTab().updateMinSize();
				getTab().setSizeToMinSize();
				//getTab().layout();
				getTab().getParent().getParent().getParent().updateMinSize();//a minor hack...
				getTab().getParent().getParent().getParent().getSize().setHeight(getTab().getParent().getParent().getParent().getMinSize().getHeight());
				getTab().getParent().getParent().getParent().layout();
			}
		}
	}
	
	protected void buildGUI()
	{

		
			Container tabFrame = super.getTab();
			tabFrame.setLayoutManager(new BorderLayout());
	        
	 
			//build one frame for accel properties, and one for velocity...

			Container ioContainer = FengGUI.createContainer(tabFrame);
			Container subIOContainer = FengGUI.createContainer(ioContainer);
			ioContainer.setLayoutManager(new BorderLayout());
			subIOContainer.setLayoutData(BorderLayoutData.WEST);
			subIOContainer.setLayoutManager(new GridLayout(2,2));
			ioContainer.setLayoutData(BorderLayoutData.NORTH);
			
			showPID = FengGUI.createCheckBox(ioContainer, StringConstants.get("PROPERTIES.MOTOR.ADVANCED","Advanced Options"));
			showPID.setLayoutData(BorderLayoutData.EAST);
			showPID.setVisible(false);
			showPID.addSelectionChangedListener(new ISelectionChangedListener()
			{

				public void selectionChanged(
						SelectionChangedEvent selectionChangedEvent) {
					updatePID();
				}
				
			});
			
			FengGUI.createLabel(subIOContainer,StringConstants.get("PROPERTIES.MOTOR.MOTOR_DESCRIPTION_INPUT","This motor should interpret input as") +":");
			inputType = FengGUI.<MotorPropertiesInterpreter.IOType>createComboBox(subIOContainer);
			FengGUI.createLabel(subIOContainer,StringConstants.get("PROPERTIES.MOTOR.MOTOR_DESCRIPTION_OUTPUT","This motor should interpret output as") + ":");
			outputType = FengGUI.<MotorPropertiesInterpreter.IOType>createComboBox(subIOContainer);
			
			inputType.addItem(new ListItem<MotorPropertiesInterpreter.IOType>(MotorPropertiesInterpreter.IOType.POSITION.toString(),MotorPropertiesInterpreter.IOType.POSITION));
			inputType.addItem(new ListItem<MotorPropertiesInterpreter.IOType>(MotorPropertiesInterpreter.IOType.VELOCITY.toString(),MotorPropertiesInterpreter.IOType.VELOCITY));
			inputType.addItem(new ListItem<MotorPropertiesInterpreter.IOType>(MotorPropertiesInterpreter.IOType.ACCELERATION.toString(),MotorPropertiesInterpreter.IOType.ACCELERATION));
			
			outputType.addItem(new ListItem<MotorPropertiesInterpreter.IOType>(MotorPropertiesInterpreter.IOType.POSITION.toString(),MotorPropertiesInterpreter.IOType.POSITION));
			outputType.addItem(new ListItem<MotorPropertiesInterpreter.IOType>(MotorPropertiesInterpreter.IOType.VELOCITY.toString(),MotorPropertiesInterpreter.IOType.VELOCITY));
			outputType.addItem(new ListItem<MotorPropertiesInterpreter.IOType>(MotorPropertiesInterpreter.IOType.ACCELERATION.toString(),MotorPropertiesInterpreter.IOType.ACCELERATION));
			inputType.setSelectedIndex(0,false);
			inputType.setSelectedIndex(2, true);
			inputType.addSelectionChangedListener(new ISelectionChangedListener()
			{

				public void selectionChanged(
						SelectionChangedEvent selectionChangedEvent) {
					updatePID();
				}
				
			});
			
		
			Container mainSettings = FengGUI.createContainer(tabFrame);
			mainSettings.setLayoutData(BorderLayoutData.CENTER);
			mainSettings.setLayoutManager(new RowLayout(false));
			
			settings = FengGUI.createContainer(mainSettings);
		
			
		//	settings.setLayoutData(BorderLayoutData.CENTER);
	
			
			settings.setLayoutManager(new BorderLayout());

			Container rows = FengGUI.createContainer(settings);
			rows.setLayoutManager(new RowLayout(false));
			rows.setLayoutData(BorderLayoutData.NORTH);
					
			Container velRow = FengGUI.createContainer(rows);
			velRow.setLayoutManager(new  RowLayout(true));
	
			Container accelRow = FengGUI.createContainer(rows);
			accelRow.setLayoutManager(new  RowLayout(true));

			FengGUI.createLabel(velRow, StringConstants.get("PROPERTIES.MOTOR.MAX_VEL","Maximum velocity")+":");
			velocity = FengGUI.createTextEditor(velRow);
			
			FengGUI.createLabel(accelRow, StringConstants.get("PROPERTIES.MOTOR.MAX_FORCE","Maximum force") + ":");
			acceleration = FengGUI.createTextEditor(accelRow);
		
			pid =  FengGUI.createContainer(mainSettings);
			pid.getAppearance().add(new TitledBorder( StringConstants.get("PROPERTIES.MOTOR.PID","PID")));
			pid.setVisible(false);
			
			Container springContainer = FengGUI.createContainer(mainSettings);
			springContainer.setLayoutManager(new RowLayout(false));
			springContainer.getAppearance().add(new TitledBorder( StringConstants.get("PROPERTIES.MOTOR.SPRING","Spring")));
			
			isSpring = FengGUI.createCheckBox(springContainer, StringConstants.get("PROPERTIES.MOTOR.SPRING_DESCRIPTION","Treat as spring"));
			Container secondSpringRow = FengGUI.createContainer(springContainer);
			secondSpringRow.setLayoutManager(new RowLayout());
			
			FengGUI.createLabel(secondSpringRow, StringConstants.get("PROPERTIES.MOTOR.SPRING_CONSTANT","Spring Constant"));
			kValue = FengGUI.createTextEditor(secondSpringRow);
			
			
			Container thirdSpringRow = FengGUI.createContainer(springContainer);
			thirdSpringRow.setLayoutManager(new RowLayout());
			FengGUI.createLabel(thirdSpringRow, StringConstants.get("PROPERTIES.MOTOR.SPRING_FRICTION","Spring Friction"));
			springFriction = FengGUI.createTextEditor(thirdSpringRow);
			
			
//			/FengGUI.createLabel(springContainer, "Note: If this motor is a spring, then max force/velocity are ignored");

			
			/*
			 *This is an empty container for subclasses to populate.
			 */
			customOptions = FengGUI.createContainer(rows);
        tabFrame.layout();
        
      
		
	}

	@Override
	public boolean embed(ITab tab) {
		if(tab instanceof HydraulicsTab)
		{
			super.getEmbeddedTabs().add(tab);
			this.customOptions.addWidget(tab.getTab());
			this.getTab().layout();
			return true;
		}else if (tab instanceof PIDControlsTab)
		{
			super.getEmbeddedTabs().add(tab);
			this.pid.addWidget(tab.getTab());
			this.hasPID = true;
			
			if(this.inputType.getSelectedItem().getValue()==IOType.POSITION)
			{
				pid.setVisible(true);
				pid.updateMinSize();
				pid.layout();
				getTab().updateMinSize();
				getTab().layout();
			}
			return true;
		}
		return super.embed(tab);
	}
	


	
}
