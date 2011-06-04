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
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;

import com.golemgame.local.StringConstants;
import com.golemgame.mvc.golems.GeneralSensorInterpreter.SensorType;
import com.golemgame.structural.structures.MultimeterStruct;


public class GeneralSensorTab extends PropertyTabAdapter {

	private MultimeterStruct sensor;
	private ComboBox<SensorType> combo;
	private Container optionsContainer;
	private ITab settingsTab;
	public GeneralSensorTab() {
		super(StringConstants.get("TOOLBAR.MULTIMETER","Multimeter"));
		
		
	}
	
	public MultimeterStruct getGeneralSensor() {
		return sensor;
	}

	public void setGeneralSensor(MultimeterStruct sensor) {
		this.sensor = sensor;
		setSelectedSensorType(getGeneralSensor().getSensor());
	}
	
	

	
	protected void buildGUI() {
		
		getTab().setLayoutManager(new BorderLayout());
		Container row = FengGUI.createContainer(getTab());
		row.setLayoutData(BorderLayoutData.NORTH);
		row.setLayoutManager(new RowLayout(false));
		
		combo = FengGUI.<SensorType>createComboBox(row);
		
		for(SensorType type:SensorType.values())
		{
			combo.addItem(new ListItem<SensorType>(type.getName(),type));
		}
		optionsContainer = FengGUI.createContainer(row);
		optionsContainer.setLayoutManager(new BorderLayout());
		
		combo.addSelectionChangedListener(new ISelectionChangedListener()
		{

			
			public void selectionChanged(
					SelectionChangedEvent selectionChangedEvent) {
			
				if(combo.getSelectedItem()!= null)
					setSelectedSensorType(combo.getSelectedItem().getValue());
			}
			
		});
	}

	
	public void close(boolean cancel) {
		if(!cancel)
		{
			if(combo.getSelectedItem()!= null)
				this.sensor.setSensor(combo.getSelectedItem().getValue() );
			
		}
		if(this.settingsTab!= null)
			this.settingsTab.close(cancel);
	}

	
	public void open() {
		setSelectedSensorType(getGeneralSensor().getSensor());
	}

	private void setSelectedSensorType(SensorType type)
	{
		if(combo.getSelectedItem().getValue() != type)
		{
			for(ListItem<SensorType> item:combo.getList().getItems())
			{
				if(item.getValue().equals(type))
				{
					combo.setSelected(item);
					break;
				}
			}
		}
	
		this.optionsContainer.removeAllWidgets();
		
		this.settingsTab =  getGeneralSensor().getSettingsTab(type);
		
		settingsTab.getTab().setLayoutData(BorderLayoutData.NORTH);
		this.optionsContainer.addWidget(settingsTab.getTab());
		this.settingsTab.open();
		this.getTab().layout();
	}
	

}
