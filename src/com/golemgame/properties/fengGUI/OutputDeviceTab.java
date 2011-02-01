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

import java.util.concurrent.Callable;

import org.fenggui.CheckBox;
import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.ListItem;
import org.fenggui.TextEditor;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.event.mouse.MouseAdapter;
import org.fenggui.event.mouse.MousePressedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.GridLayout;
import org.fenggui.layout.RowLayout;
import org.fenggui.util.Color;
import org.fenggui.util.Spacing;

import com.golemgame.menu.color.ColorPatch;
import com.golemgame.menu.color.ColorWindow;
import com.golemgame.menu.color.ColorDialog.ColorDialogListener;
import com.golemgame.mvc.golems.AppearanceInterpreter;
import com.golemgame.mvc.golems.OscilloscopeInterpreter;
import com.golemgame.mvc.golems.input.InputDeviceInterpreter;
import com.golemgame.mvc.golems.input.InputDeviceInterpreter.InputType;
import com.golemgame.mvc.golems.output.ColorOutputDeviceInterpreter;
import com.golemgame.mvc.golems.output.OutputDeviceInterpreter;
import com.golemgame.mvc.golems.output.OutputDeviceInterpreter.OutputType;
import com.golemgame.properties.fengGUI.PropertyTabAdapter.Format;
import com.golemgame.states.GUILayer;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;
import com.jme.util.GameTaskQueueManager;

/**
 * This tab lets users select the device type, and name the device.
 * The actual device settings are in a sub tab
 * @author Sam
 *
 */
public class OutputDeviceTab extends PropertyTabAdapter{

	public OutputDeviceTab() {
		super("Output");
		
	}

	private OutputDeviceInterpreter interpreter;
	
	private ComboBox<OutputType> dropDown;
	private ColorPatch positiveColorPatch;
	private ColorPatch negativeColorPatch;
	private ColorPatch neutralColorPatch;
	
	private Container deviceContainer;
	private Container deviceCenterContainer;
	private Container innerControls;
	private CheckBox<?> locked ;
	private CheckBox<?> windowed ;
	
	private TextEditor xPosition;
	private TextEditor yPosition;
	private TextEditor width;
	private TextEditor height;
	
	public void close(boolean cancel) {
		if(!cancel)
		{
			super.standardClosingBehaviour( dropDown,OutputDeviceInterpreter.DEVICE_TYPE);	
			super.standardClosingBehaviour(positiveColorPatch, ColorOutputDeviceInterpreter.COLOR_POSITIVE);
			super.standardClosingBehaviour(negativeColorPatch, ColorOutputDeviceInterpreter.COLOR_NEGATIVE);
			super.standardClosingBehaviour(neutralColorPatch, ColorOutputDeviceInterpreter.COLOR_NEUTRAL);
			if(super.isAltered(xPosition)||super.isAltered(yPosition) || super.isAltered(width) || super.isAltered(height))
			{
				interpreter.setInstrumentUserPositioned(true);
			}
			
			super.standardClosingBehaviour(xPosition, OutputDeviceInterpreter.INSTRUMENT_X, Format.ProportionalX);
			super.standardClosingBehaviour(yPosition, OutputDeviceInterpreter.INSTRUMENT_Y, Format.ProportionalY);
			super.standardClosingBehaviour(width, OutputDeviceInterpreter.INSTRUMENT_WIDTH, Format.ProportionalX);
			super.standardClosingBehaviour(height, OutputDeviceInterpreter.INSTRUMENT_HEIGHT, Format.ProportionalY);

			super.standardClosingBehaviour(locked, OutputDeviceInterpreter.INSTRUMENT_LOCKED);
			super.standardClosingBehaviour(windowed, OutputDeviceInterpreter.INSTRUMENT_WINDOWED);
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
		dropDown = FengGUI.<OutputType>createComboBox();// new ComboBox<Material>();
		
		dropDown.getAppearance().setPadding(new Spacing(0,5));
		dropDown.setLayoutData(BorderLayoutData.CENTER);
		dropDown.getAppearance().add(new PlainBackground(Color.WHITE));
		
		Label labelCombo = FengGUI.createLabel(comboFrame,"Input Type: ");
		labelCombo.setLayoutData(BorderLayoutData.WEST);
		comboFrame.addWidget(dropDown);
		comboFrame.layout();
		
		for (OutputType outputType:OutputType.values())
		{	
			dropDown.addItem(new ListItem<OutputType>(outputType.toString(),outputType));			
		}
		
		dropDown.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent selectionChangedEvent) 
			{			
				if (selectionChangedEvent.isSelected())
				{
					OutputType selected = dropDown.getSelectedItem().getValue();
					displayOutputDialog(selected);
				}
			}
		});	
		
	
		deviceContainer = FengGUI.createContainer(upperControls);
		deviceContainer.setLayoutData(BorderLayoutData.CENTER);
		deviceContainer.setLayoutManager(new BorderLayout());
		deviceCenterContainer = FengGUI.createContainer();
		
		Container checkContainer = FengGUI.createContainer(upperControls);
		checkContainer.setLayoutManager(new RowLayout());
		windowed = FengGUI.createCheckBox(checkContainer,"Show Window Frame");
		locked= FengGUI.createCheckBox(checkContainer,"Lock Instrument Window");
		Container positionControls = FengGUI.createContainer(upperControls);
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
		
		
		positiveColorPatch = new ColorPatch();
		negativeColorPatch = new ColorPatch();
		neutralColorPatch = new ColorPatch();
		
		positiveColorPatch.addMouseListener(new MouseAdapter()
	       {
	    	   
				
				public void mousePressed(MousePressedEvent event) {
					event.setUsed();
					final ColorWindow window = ColorWindow.getInstance();
					//have to call this at a later time, otherwise this window stays ontop
					
					
					
					if (window.getOwner() == positiveColorPatch)
					{
						window.close();
						
					}else
					{
						
						GameTaskQueueManager.getManager().update(new Callable<Object>()
								{

									
									public Object call() throws Exception {
										window.setOwner(positiveColorPatch);
										GUILayer layer = GUILayer.getLoadedInstance();
										Color color = positiveColorPatch.getColor();
										window.setInitialColor(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha());
										window.display(layer.getDisplay());
										window.addColorListener(new ColorDialogListener()
										{

											
											public void cancel() {
											}

											
											public void colorChosen(int red, int green, int blue,int alpha) {
												if (window.getOwner() != positiveColorPatch)
													return;
			
												
												positiveColorPatch.setColor(new Color(red,green,blue,alpha));								}
											
										});
										return null;
									}

								});
					}
				}
		    	   
	       });
	       
		
		negativeColorPatch.addMouseListener(new MouseAdapter()
	       {
	    	   
				
				public void mousePressed(MousePressedEvent event) {
					event.setUsed();
					final ColorWindow window = ColorWindow.getInstance();
					//have to call this at a later time, otherwise this window stays ontop
					
					
					
					if (window.getOwner() == negativeColorPatch)
					{
						window.close();
						
					}else
					{
						
						GameTaskQueueManager.getManager().update(new Callable<Object>()
								{

									
									public Object call() throws Exception {
										window.setOwner(negativeColorPatch);
										GUILayer layer = GUILayer.getLoadedInstance();
										Color color = negativeColorPatch.getColor();
										window.setInitialColor(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha());
										window.display(layer.getDisplay());
										window.addColorListener(new ColorDialogListener()
										{

											
											public void cancel() {
											}

											
											public void colorChosen(int red, int green, int blue,int alpha) {
												if (window.getOwner() != negativeColorPatch)
													return;
			
												
												negativeColorPatch.setColor(new Color(red,green,blue,alpha));								}
											
										});
										return null;
									}

								});
					}
				}
		    	   
	       });
		
		neutralColorPatch.addMouseListener(new MouseAdapter()
	       {
	    	   
				
				public void mousePressed(MousePressedEvent event) {
					event.setUsed();
					final ColorWindow window = ColorWindow.getInstance();
					//have to call this at a later time, otherwise this window stays ontop
					
					
					
					if (window.getOwner() == neutralColorPatch)
					{
						window.close();
						
					}else
					{
						
						GameTaskQueueManager.getManager().update(new Callable<Object>()
								{

									
									public Object call() throws Exception {
										window.setOwner(neutralColorPatch);
										GUILayer layer = GUILayer.getLoadedInstance();
										Color color = neutralColorPatch.getColor();
										window.setInitialColor(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha());
										window.display(layer.getDisplay());
										window.addColorListener(new ColorDialogListener()
										{

											
											public void cancel() {
											}

											
											public void colorChosen(int red, int green, int blue,int alpha) {
												if (window.getOwner() != neutralColorPatch)
													return;
			
												
												neutralColorPatch.setColor(new Color(red,green,blue,alpha));								}
											
										});
										return null;
									}

								});
					}
				}
		    	   
	       });
		
        getTab().layout();
		
	}
	


	private void displayOutputDialog(OutputType selected) {
		deviceContainer.removeAllWidgets();
		deviceCenterContainer.removeAllWidgets();
		switch(selected)
		{		
			case GRAPH:{
				deviceContainer.addWidget(deviceCenterContainer);
				deviceCenterContainer.setLayoutManager(new RowLayout(false));
				deviceCenterContainer.setLayoutData(BorderLayoutData.NORTH);
				deviceCenterContainer.addWidget(FengGUI.createLabel(deviceCenterContainer,"A graph showing the input this device has recieved over the last little while."));
				
				//no widgets
				break;}		
			case TEXT:{
				deviceContainer.addWidget(deviceCenterContainer);
				deviceCenterContainer.setLayoutManager(new RowLayout(false));
				deviceCenterContainer.setLayoutData(BorderLayoutData.NORTH);
				deviceCenterContainer.addWidget(FengGUI.createLabel(deviceCenterContainer,"A text box that displays the current signal."));
				
				break;}		
			case COLOR:{
				deviceContainer.addWidget(deviceCenterContainer);
				deviceCenterContainer.setLayoutManager(new GridLayout(3,2));
				deviceCenterContainer.setLayoutData(BorderLayoutData.NORTH);
			
				
				deviceCenterContainer.addWidget(FengGUI.createLabel(deviceCenterContainer,"+1 Signal Color"));
				deviceCenterContainer.addWidget(positiveColorPatch);
			
				deviceCenterContainer.addWidget(FengGUI.createLabel(deviceCenterContainer,"O Signal Color"));
				deviceCenterContainer.addWidget(neutralColorPatch);
				
				deviceCenterContainer.addWidget(FengGUI.createLabel(deviceCenterContainer,"-1 Signal Color"));
				deviceCenterContainer.addWidget(negativeColorPatch);
				break;}
		}
		
		deviceCenterContainer.layoutToRoot();
		deviceCenterContainer.updateMinSize();
	}
	
	public void open() {
		this.interpreter = new OutputDeviceInterpreter(super.getPropertyStoreAdjuster().getPrototype());
		
		this.interpreter.loadDefaults();
		initializePrototype();
		super.setComparePoint();

		super.associateWithKey(dropDown, OutputDeviceInterpreter.DEVICE_TYPE);
		super.standardOpeningBehaviour(dropDown,  OutputDeviceInterpreter.DEVICE_TYPE);//this has to happen first
		
		super.associateWithKey(positiveColorPatch, ColorOutputDeviceInterpreter.COLOR_POSITIVE);
		super.associateWithKey(neutralColorPatch, ColorOutputDeviceInterpreter.COLOR_NEUTRAL);
		super.associateWithKey(negativeColorPatch, ColorOutputDeviceInterpreter.COLOR_NEGATIVE);
		
		super.standardOpeningBehaviour(positiveColorPatch,  ColorOutputDeviceInterpreter.COLOR_POSITIVE);//this has to happen first
		super.standardOpeningBehaviour(neutralColorPatch,  ColorOutputDeviceInterpreter.COLOR_NEUTRAL);//this has to happen first
		super.standardOpeningBehaviour(negativeColorPatch,  ColorOutputDeviceInterpreter.COLOR_NEGATIVE);//this has to happen first

		super.associateWithKey(locked, OutputDeviceInterpreter.INSTRUMENT_LOCKED);
		super.associateWithKey(xPosition, OutputDeviceInterpreter.INSTRUMENT_X);
		super.associateWithKey(yPosition, OutputDeviceInterpreter.INSTRUMENT_Y);
		super.associateWithKey(width, OutputDeviceInterpreter.INSTRUMENT_WIDTH);
		super.associateWithKey(height, OutputDeviceInterpreter.INSTRUMENT_HEIGHT);
		super.associateWithKey(windowed, OutputDeviceInterpreter.INSTRUMENT_WINDOWED);
		super.standardOpeningBehaviour(windowed, OutputDeviceInterpreter.INSTRUMENT_WINDOWED);
		super.standardOpeningBehaviour(xPosition, OutputDeviceInterpreter.INSTRUMENT_X, Format.ProportionalX);
		super.standardOpeningBehaviour(yPosition, OutputDeviceInterpreter.INSTRUMENT_Y, Format.ProportionalY);
		super.standardOpeningBehaviour(width, OutputDeviceInterpreter.INSTRUMENT_WIDTH, Format.ProportionalX);
		super.standardOpeningBehaviour(height, OutputDeviceInterpreter.INSTRUMENT_HEIGHT, Format.ProportionalY);
		super.standardOpeningBehaviour(locked, OutputDeviceInterpreter.INSTRUMENT_LOCKED);

		
		displayOutputDialog(interpreter.getDeviceType());
	}


}
