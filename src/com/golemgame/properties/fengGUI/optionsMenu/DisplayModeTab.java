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
package com.golemgame.properties.fengGUI.optionsMenu;

import java.awt.Dimension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.fenggui.CheckBox;
import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.ListItem;
import org.fenggui.RadioButton;
import org.fenggui.TextEditor;
import org.fenggui.ToggableGroup;
import org.fenggui.decorator.border.TitledBorder;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.GridLayout;
import org.fenggui.layout.RowLayout;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.Display;

import com.golemgame.local.StringConstants;
import com.golemgame.properties.fengGUI.ITab;
import com.golemgame.properties.fengGUI.ImageTabAdapter;
import com.golemgame.properties.fengGUI.MessageBox;
import com.golemgame.states.GUILayer;
import com.golemgame.states.GeneralSettings;
import com.golemgame.states.StateManager;
import com.jme.image.Texture;
import com.jme.system.DisplaySystem;
import com.jme.system.GameSettings;

public class DisplayModeTab extends ImageTabAdapter {
	

	
	private List<Dimension> resolutions;
	private Map<Dimension, List<DisplayMode>> resolutionMap ;

	private CheckBox<Object> vsyncBox;
	private CheckBox<Object> fullScreenBox;
	private TextEditor frameRateBox;
	private ToggableGroup<?> qualityGroup;
	private RadioButton<?> lowQ;
	private RadioButton<?> normalQ;
	private RadioButton<?> highQ;
	private Container memoryContainer;
	
	private CheckBox<Object> tooltips;
	private CheckBox<Object> useLocalization;
	
	private volatile boolean refreshLock = false;


	
	public Dimension getSelectedResolution() {
		return resolutionBox.getSelectedItem().getValue();
	}


	public void setSelectedResolution(Dimension selectedResolution) 
	{
		refreshFrequency();
		refreshBits();
	
	}


	public int getSelectedFrequency() {
		if( frequencyBox.getSelectedItem()!= null)
		{		
			return 	frequencyBox.getSelectedItem().getValue();
		}return -1;
	}


	public void setSelectedFrequency(int selectedFrequency) {
		refreshBits();
	}


	public int getSelectedBitsPerPixel() {
		if( bitsBox.getSelectedItem()!= null)
		{		
			return 	bitsBox.getSelectedItem().getValue();
		}return -1;
	}


	public void setSelectedBitsPerPixel(int selectedBitsPerPixel) {
		refreshFrequency();
	}

	private String name = StringConstants.get("MAIN_MENU.DISPLAY" , "Display");
	
	


	
	public void open() {
		super.open();
			tooltips.setSelected(GeneralSettings.getInstance().getShowToolTips().isValue());
		
			useLocalization.setSelected(GeneralSettings.getInstance().getUseLocalization().isValue());
			
	
		
		DisplayMode mode = org.lwjgl.opengl.Display.getDisplayMode();
		showMode(mode);
		
		int quality = GeneralSettings.getInstance().getQuality().getValue();
		
		if (quality> -3)
		{
			this.highQ.setSelected(true);
		}else if (quality>-7)
		{
			this.normalQ.setSelected(true);
		}else
			this.lowQ.setSelected(true);
		
	
	}
	
	private void showMode(DisplayMode mode)
	{
		Dimension resolution = new Dimension(mode.getWidth(),mode.getHeight());
		for (ListItem<Dimension> res:resolutionBox.getList().getItems() )
		{
			if(res.getValue().equals(resolution))
			{
				resolutionBox.setSelected(res);
				break;
			}
		}
		int depth = mode.getBitsPerPixel();
		if(depth == 0)//if no bit depth was reported, you are probably in windowed mode. just report the users preference, since this has no impact in windowed mode?
			depth = GeneralSettings.getInstance().getBitDepth().getValue();
		for (ListItem<Integer> dep:bitsBox.getList().getItems() )
		{
			if(dep.getValue().equals(depth))
			{
				bitsBox.setSelected(dep);
				break;
			}
		}
		
		
		setSelectedResolution(resolution);
		setSelectedFrequency(mode.getFrequency());
	
		setSelectedBitsPerPixel(mode.getBitsPerPixel());
		GameSettings settings = StateManager.getGame().getSettings();
		frameRateBox.setText(String.valueOf( settings.getFramerate()));
		vsyncBox.setSelected(settings.isVerticalSync());
		//fullScreenBox.setSelected(settings.isFullscreen());
		fullScreenBox.setSelected(GeneralSettings.getInstance().getFullscreen().isValue());
	}
	
	
	@Override
	public boolean embed(ITab tab) {
		if(tab instanceof MemoryTab)
		{
			memoryContainer.setVisible(true);
			memoryContainer.addWidget(tab.getTab());
			super.getEmbeddedTabs().add(tab);
			return true;
		}
		return super.embed(tab);
	}


	public String getTitle() {

		return name;
	}

	private Texture icon;
	public DisplayModeTab() {
		super(StringConstants.get("MAIN_MENU.DISPLAY" ,"Display Mode"));
		createDisplayModes();
	
		buildGUIInternal();
		refresh();
		 icon =super.loadTexture("buttons/menu/Display.png");
	}

	@Override
	public Texture getIcon() {
		return icon;
	}

	private void createDisplayModes()
	{
		DisplayMode[] allModes;
		try {
			allModes = Display.getAvailableDisplayModes();
			
		} catch (LWJGLException e) {
			allModes = new DisplayMode[]{};
			StateManager.logError(e);
		}
		
		resolutions = new ArrayList<Dimension>();
		resolutionMap = new HashMap<Dimension, List<DisplayMode>>();
		
		/*
		 * Divide up the available display modes by resolution.
		 */
		for (DisplayMode mode:allModes)
		{
			Dimension res = new Dimension( mode.getWidth(),mode.getHeight());
			
			if (!resolutions.contains(res) && res.width>= 640 && res.getHeight()>=480)
			{
				resolutions.add(res);
			}
			List<DisplayMode> resolutionModes = resolutionMap.get(res);
			
			if (resolutionModes == null)
			{
				resolutionModes = new ArrayList<DisplayMode>();
				resolutionMap.put(res, resolutionModes);
			}
			
			resolutionModes.add(mode);
			
		}
		
		Collections.sort(resolutions, new Comparator<Dimension>()
				{

					
					public int compare(Dimension o1, Dimension o2) {
						
						if (o1.width>o2.width)
						{
							return 1;
						}else if (o1.width<o2.width)
						{
							return -1;
						}
						
						if (o1.height>o2.height)
							return 1;
						else if (o1.height<o2.height)
							return -1;
						
						return 0;
						
					}
			
				});
			
		
	
		
		
	}

	
	
	private ComboBox<Dimension> resolutionBox;
	private ComboBox<Integer> frequencyBox ;
	private ComboBox<Integer> bitsBox;
	
	protected void buildGUIInternal() {
		
		getTab().setLayoutManager(new BorderLayout());
		
		Container allDisplaySettings= FengGUI.createContainer(getTab());
		allDisplaySettings.setLayoutData(BorderLayoutData.NORTH);
		allDisplaySettings.setLayoutManager(new RowLayout(false));
		
		Container qualitySettings = FengGUI.createContainer(allDisplaySettings);
		qualitySettings.getAppearance().add(new TitledBorder(StringConstants.get("MAIN_MENU.DISPLAY.QUALITY" ,"Display Quality")));
		qualitySettings.setLayoutManager(new RowLayout());
		/*Button Low = FengGUI.createButton(qualitySettings, "Low");
		Button normalQ = FengGUI.createButton(qualitySettings, "Normal");
		Button highQ = FengGUI.createButton(qualitySettings, "High");*/
		
		this.qualityGroup = new ToggableGroup();
		 lowQ = FengGUI.createRadioButton(qualitySettings, StringConstants.get("MAIN_MENU.DISPLAY.QUALITY_LOW" ,"Low"), qualityGroup);
		 normalQ = FengGUI.createRadioButton(qualitySettings, StringConstants.get("MAIN_MENU.DISPLAY.QUALITY_NORMAL" ,"Normal"), qualityGroup);
		 highQ = FengGUI.createRadioButton(qualitySettings, StringConstants.get("MAIN_MENU.DISPLAY.QUALITY_HIGH" ,"High"), qualityGroup);
		
		normalQ.setSelected(true);
		
		
		Container displaySettings = FengGUI.createContainer(allDisplaySettings);
		displaySettings.getAppearance().add(new TitledBorder(StringConstants.get("MAIN_MENU.DISPLAY.SETTINGS" ,"Display Settings")));
		displaySettings.setLayoutManager(new GridLayout(4,2));
		FengGUI.createLabel(displaySettings,StringConstants.get("MAIN_MENU.DISPLAY.RESOLUTION" ,"Resolution"));
		
		Container resContainer = FengGUI.createContainer(displaySettings);
		resolutionBox = FengGUI.<Dimension>createComboBox(resContainer);
		fullScreenBox = FengGUI.createCheckBox(resContainer,StringConstants.get("MAIN_MENU.DISPLAY.FULL_SCREEN" ,"Full Screen"));
		
		//get the available resolutions
		for (Dimension res:getResolutions())
		{
			resolutionBox.addItem(new ListItem<Dimension>(res.width+"x"+res.height,res));
		}
		FengGUI.createLabel(displaySettings,StringConstants.get("MAIN_MENU.DISPLAY.FREQ" ,"Frequency"));
		 frequencyBox = FengGUI.<Integer>createComboBox(displaySettings);
		 frequencyBox.addSelectionChangedListener(new ISelectionChangedListener()
		 {

			
			public void selectionChanged(
					SelectionChangedEvent selectionChangedEvent) {
				
				if (!refreshLock || Math.random() < .001)//just incase refresh lock gets stuck for some reason...
					refreshBits();
				
			}
			 
		 });
		FengGUI.createLabel(displaySettings,StringConstants.get("MAIN_MENU.DISPLAY.COLOR_DEPTH" ,"Colour Depth"));
		bitsBox = FengGUI.<Integer>createComboBox(displaySettings);
		bitsBox.addSelectionChangedListener(new ISelectionChangedListener()
		 {

			
			public void selectionChanged(
					SelectionChangedEvent selectionChangedEvent) {
				if (!refreshLock || Math.random() < .001)//just incase refresh lock gets stuck for some reason...
					refreshFrequency();
				
			}
			 
		 });
		displaySettings.layout();
		
		FengGUI.createLabel(displaySettings,StringConstants.get("MAIN_MENU.DISPLAY.FPS" ,"Frame Rate"));
		
		Container frameRateContainer = FengGUI.createContainer(displaySettings);
		
		frameRateBox = FengGUI.createTextEditor(frameRateContainer);
		vsyncBox = FengGUI.createCheckBox(frameRateContainer,StringConstants.get("MAIN_MENU.DISPLAY.VSYNC" ,"Use VSync"));
		
		Container tooltipContainer = FengGUI.createContainer(allDisplaySettings);
		tooltipContainer.getAppearance().add(new TitledBorder(StringConstants.get("MAIN_MENU.DISPLAY.TOOLTIPS" ,"Tooltips")));
		
		tooltips = FengGUI.createCheckBox(tooltipContainer,StringConstants.get("MAIN_MENU.DISPLAY.SHOW_TOOLTIPS" ,"Show Tooltips"));
		
		Container languageContainer = FengGUI.createContainer(allDisplaySettings);
		languageContainer.getAppearance().add(new TitledBorder(StringConstants.get("MAIN_MENU.DISPLAY.LANGAUGE" ,"Language Settings")));
		
		useLocalization = FengGUI.createCheckBox(languageContainer,StringConstants.get("MAIN_MENU.DISPLAY.LANGAUGE.ENABLE" ,"Use OS language if available (requires restart)"));
		
		
		memoryContainer = FengGUI.createContainer(allDisplaySettings);
		memoryContainer.getAppearance().add(new TitledBorder(StringConstants.get("MAIN_MENU.DISPLAY.MEMORY" ,"Memory Options")));
		memoryContainer.setVisible(false);
		
	}
	
	private void refresh()
	{
		refreshFrequency();
		refreshBits();
	}
	
	private void refreshFrequency()
	{
		refreshLock = true;
	 	List<Integer> freqs = getFrequencies();
	 	
	 	Integer selected = getSelectedFrequency();
	 	
	 	frequencyBox.getList().clear();
	 	
	 	if (freqs.isEmpty())
	 	{
	 		return;
	 	}
	 	
	 	for(Integer freq:freqs)
	 	{
	 		frequencyBox.addItem(new ListItem<Integer>(freq.toString(),freq));
	 	}
	 
	 	if (!freqs.contains(selected))
	 		selected = freqs.get(0);
	 	
	 	for (ListItem<Integer> item:frequencyBox.getList().getItems())
	 	{
	 		if (item.getValue() == selected)
	 		{
	 			frequencyBox.setSelected(item);
	 	
	 			break;
	 		}
	 	}

	 	refreshLock = false;
	 	
	}
	
	private void refreshBits()
	{
		refreshLock = true;
		List<Integer> bits = getBits();
	 	
	 	Integer selected = getSelectedBitsPerPixel();
	 	
	 	bitsBox.getList().clear();
	 	
	 	if (bits.isEmpty())
	 	{
	 		return;
	 	}
	 	
	 	for(Integer freq:bits)
	 	{
	 		bitsBox.addItem(new ListItem<Integer>(freq.toString(),freq));
	 	}
	 
	 	if (!bits.contains(selected))
	 		selected = bits.get(0);
	 	
	 	for (ListItem<Integer> item:bitsBox.getList().getItems())
	 	{
	 		if (item.getValue() == selected)
	 		{
	 			bitsBox.setSelected(item);
	 	
	 			break;
	 		}
	 	}

	 	refreshLock = false;
	}
	
	
	
	public void close(boolean cancel) {
		super.close(cancel);
		if(!cancel)
		{
			try{
				GeneralSettings.getInstance().getShowToolTips().setValue(tooltips.isSelected());
				GeneralSettings.getInstance().getUseLocalization().setValue(useLocalization.isSelected());
				
				
				if(qualityGroup.getSelectedItem().equals(lowQ))
				{
					GeneralSettings.getInstance().getQuality().setValue(-10);
				}else if(qualityGroup.getSelectedItem().equals(highQ))
				{
					GeneralSettings.getInstance().getQuality().setValue(0);
				}else 
				{
					GeneralSettings.getInstance().getQuality().setValue(-5);
				}
				
				if (!DisplaySystem.getDisplaySystem().isValidDisplayMode(getSelectedResolution().width, getSelectedResolution().height, getSelectedBitsPerPixel(), getSelectedFrequency()))
					return;

				StateManager.getGame().executeInGL(new Callable<Object>()
						{

							
							public Object call() throws Exception {
								GameSettings settings = StateManager.getGame().getSettings();
								
								boolean changeRequired = false;
								boolean resolutionChangeRequired = false;
								
								resolutionChangeRequired |= settings.getWidth() != getSelectedResolution().width;
								resolutionChangeRequired |= settings.getHeight() != getSelectedResolution().height;
								changeRequired |= settings.getFrequency()!= getSelectedFrequency();
								//changeRequired |= settings.getDepth()!= getSelectedBitsPerPixel();
								changeRequired |= settings.isVerticalSync()!= vsyncBox.isSelected();
								
								int frameRate=settings.getFramerate();
								try{
									frameRate = Integer.valueOf( frameRateBox.getText());
									changeRequired |= settings.getFramerate()!= frameRate;
								}catch(NumberFormatException e)
								{
									
								}
								//put this into a custom setting instead of the standard game settings
								//because it seems fullscreen can't be changed while the window is created.
								GeneralSettings.getInstance().getFullscreen().setValue(fullScreenBox.isSelected());
								GeneralSettings.getInstance().getBitDepth().setValue(getSelectedBitsPerPixel());
								//GeneralSettings.getInstance().getResolution().setValue(new Dimension( getSelectedResolution().width,getSelectedResolution().height));
								GeneralSettings.getInstance().getVsync().setValue(vsyncBox.isSelected());
								
								//settings.setFullscreen( fullScreenBox.isSelected());
								if(fullScreenBox.isSelected() != settings.isFullscreen() || settings.getDepth()!= getSelectedBitsPerPixel())
								{
										
									//display pop-up window informing the user that the changes will be applied on restart
									MessageBox.showMessageBox("Display Mode","Some display settings changes will only be applied next time the game starts", GUILayer.getLoadedInstance().getDisplay(),getTab());
								}
						
								
								if (changeRequired|resolutionChangeRequired)
								{
									settings.setWidth(getSelectedResolution().width);
									settings.setHeight(getSelectedResolution().height);
									settings.setFrequency(getSelectedFrequency());
									//settings.setDepth(getSelectedBitsPerPixel());
							
									settings.setVerticalSync( vsyncBox.isSelected());
									settings.setFramerate(frameRate);
									
								
									//else
									StateManager.getGame().reinit();
									
									GeneralSettings.getInstance().getResolution().setValue(new Dimension(DisplaySystem.getDisplaySystem().getWidth(),DisplaySystem.getDisplaySystem().getHeight() - (DisplaySystem.getDisplaySystem().isFullScreen()? 0:0)));

									//StateManager.getResolution().setValue(new Dimension(DisplaySystem.getDisplaySystem().getWidth(),DisplaySystem.getDisplaySystem().getHeight()));
								}
								return null;
							}
	
						});
			
			}catch(Exception e)
			{
				StateManager.logError(e);
			}
		}
	}
	
	private List<Dimension> getResolutions()
	{
		return resolutions;
	}
	
	private List<Integer> getFrequencies()
	{
		List<DisplayMode> modes = resolutionMap.get(getSelectedResolution());
		List<Integer> freqs = new ArrayList<Integer>();
		
		if (modes != null)
		{
			for (DisplayMode mode:modes)
			{
				if (getSelectedBitsPerPixel()<0 || mode.getBitsPerPixel() == getSelectedBitsPerPixel())
				{
					if (!freqs.contains(mode.getFrequency()))
						freqs.add(mode.getFrequency());
				}
			}
		}
		Collections.sort(freqs);
		return freqs;
	}
	
	private List<Integer> getBits()
	{
		List<DisplayMode> modes = resolutionMap.get(getSelectedResolution());
		List<Integer> bits = new ArrayList<Integer>();
		
		if (modes != null)
		{
			for (DisplayMode mode:modes)
			{
				if (getSelectedFrequency()<0 ||  mode.getFrequency() == getSelectedFrequency())
				{
					if (!bits.contains(mode.getBitsPerPixel()))
						bits.add(mode.getBitsPerPixel());
				}
			}
		}
		Collections.sort(bits);
		return bits;
	}
	

	
}
