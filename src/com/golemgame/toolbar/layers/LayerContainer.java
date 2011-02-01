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
package com.golemgame.toolbar.layers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.ScrollBar;
import org.fenggui.ScrollContainer;
import org.fenggui.ToggableGroup;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.ISizeChangedListener;
import org.fenggui.event.ISliderMovedListener;
import org.fenggui.event.SizeChangedEvent;
import org.fenggui.event.SliderMovedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.GridLayout;
import org.fenggui.layout.RowLayout;
import org.fenggui.util.Color;

import com.golemgame.mechanical.layers.Layer;
import com.golemgame.mechanical.layers.LayerRepository;
import com.golemgame.mechanical.layers.LayerRepositoryListener;
import com.golemgame.mvc.golems.LayerInterpreter;
import com.golemgame.states.GUILayer;
import com.golemgame.states.StateManager;
import com.golemgame.structural.Structural;
import com.golemgame.tool.ActionToolSettings;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionList;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.SetLayerAction;
import com.golemgame.tool.action.UndoManager;
import com.golemgame.tool.action.UndoManager.UndoProperties;
import com.golemgame.tool.action.mvc.AddComponentAction;
import com.golemgame.tool.action.mvc.CopyComponentAction;
import com.golemgame.tool.action.mvc.RemoveComponentAction;

public class LayerContainer extends Container {
	private final LayerRepository layers;
	private ArrayList<LayerBar> layerBars = new ArrayList<LayerBar>();
	private final Container barContainer;
	private final ToggableGroup<LayerBar> selectedGroup = new ToggableGroup<LayerBar>();
	private ScrollContainer scrollContainer;
	private final Container layerControls;
	private final ScrollBar verticalScroll;
	public LayerContainer(final LayerRepository layers) {
		super();
		this.layers = layers;
		
		this.setLayoutManager(new BorderLayout());
		
		
		
		Container outerControlContainer = new Container();
		outerControlContainer.setLayoutData(BorderLayoutData.WEST);
		outerControlContainer.setLayoutManager(new BorderLayout());
		this.addWidget(outerControlContainer);
		
		Container controlContainer = new Container();
		outerControlContainer.addWidget(controlContainer);
		controlContainer.setLayoutData(BorderLayoutData.NORTH);
		
		controlContainer.setLayoutManager(new RowLayout(false));
		
		Button addLayer = FengGUI.createButton(controlContainer,"New Layer");
		Button copyLayer = FengGUI.createButton(controlContainer,"Copy Layer");
		Button selectLayer = FengGUI.createButton(controlContainer,"Select Layer");		
		Button addToLayer = FengGUI.createButton(controlContainer,"Move To Layer");
		Button deleteLayer = FengGUI.createButton(controlContainer,"Delete Layer");

		
		
		addLayer.addButtonPressedListener(new IButtonPressedListener()
		{

			public void buttonPressed(ButtonPressedEvent e) {
				LayerInterpreter newLayer = new LayerInterpreter();
				newLayer.setLayerName(layers.generateUniqueName());
				try{
					AddComponentAction comp = (AddComponentAction) layers.getAction(Action.ADD_COMPONENT);
					comp.setComponent(newLayer.getStore());
					if(comp.doAction())
						UndoManager.getInstance().addAction(comp);
				}catch(ActionTypeException e1)
				{
					StateManager.logError(e1);
				}
			}
			
		});
		
		addToLayer.addButtonPressedListener(new IButtonPressedListener()
		{

			public void buttonPressed(ButtonPressedEvent e) {
				Layer layer = layers.getActiveLayer();
				
				Collection<Actionable> selection = StateManager.getToolManager().getSelectedActionables();
				
				ActionList actionList = new ActionList("Move To Layer");
				actionList.setProperties(UndoProperties.NORMAL);
				for(Actionable a:selection)
				{
					//move all the selected elements into the current layer,
					try{
						SetLayerAction setLayer = (SetLayerAction) a.getAction(Action.SET_LAYER);
						setLayer.setLayer(layer.getID());
						actionList.add(setLayer);
					}catch(ActionTypeException e1)
					{
						StateManager.logError(e1);
					}
				}
				if(actionList.doAction())
					UndoManager.getInstance().addAction(actionList);
			}
			
		});
		
		copyLayer.addButtonPressedListener(new IButtonPressedListener()
		{

			public void buttonPressed(ButtonPressedEvent e) {
				Layer layer = layers.getActiveLayer();
		
					//move all the selected elements into the current layer,
					try{
						CopyComponentAction copyLayer = (CopyComponentAction) layers.getAction(Action.COPY_COMPONENT);
						copyLayer.addComponent(layer.getStore());
						if(copyLayer.doAction())
							UndoManager.getInstance().addAction(copyLayer);
					}catch(ActionTypeException e1)
					{
						StateManager.logError(e1);
					}
			
				
			}
			
		});
		
		selectLayer.addButtonPressedListener(new IButtonPressedListener()
		{

			public void buttonPressed(ButtonPressedEvent e) {
				Layer layer = layers.getActiveLayer();
		
				//StateManager.getToolManager().forceDeselect();
				Collection<Actionable> selection = new ArrayList<Actionable>();
				for(Structural s:layer.getMembers())
					selection.add(s);//note: required because of generics issues
				ActionToolSettings.getInstance().getMultipleSelect().setValue(true);
				StateManager.getToolManager().selectActionables(selection);
				ActionToolSettings.getInstance().getMultipleSelect().setValue(false);
				GUILayer.getLoadedInstance().getDisplay().setFocusedWidget(null);//release focus so people can just hit enter to go to object properties after this
			}
			
		});
		
		deleteLayer.addButtonPressedListener(new IButtonPressedListener()
		{

			public void buttonPressed(ButtonPressedEvent e) {
			
				try{
					Layer layer = layers.getActiveLayer();
					RemoveComponentAction comp = (RemoveComponentAction) layer.getRepository().getAction(Action.REMOVE_COMPONENT);
					comp.setComponent(layer.getStore());
					if(comp.doAction())
						UndoManager.getInstance().addAction(comp);
				}catch(ActionTypeException e1)
				{
					StateManager.logError(e1);
				}
			}
			
		});
		
		Container centerContainer = new Container();
		centerContainer.setLayoutData(BorderLayoutData.CENTER);
		centerContainer.setLayoutManager(new BorderLayout());
		this.addWidget(centerContainer);
		
		layerControls = new Container();
		layerControls.setLayoutData(BorderLayoutData.NORTH);
		this.addWidget(layerControls);
		layerControls.setLayoutManager(new BorderLayout());
	
		Container innerLayerControls = new Container();
		innerLayerControls.setLayoutData(BorderLayoutData.EAST);
		innerLayerControls.setLayoutManager(new RowLayout());
		layerControls.addWidget(innerLayerControls);
		
		
		
		PlainBackground backgroundBGR = new PlainBackground();
		backgroundBGR.setColor(Color.GRAY);
		layerControls.getAppearance().add(backgroundBGR);
		
		final Button lockAll = FengGUI.createButton(innerLayerControls,"  Lock  ");
		final Button showAll = FengGUI.createButton(innerLayerControls," Hide ");
		lockAll.setExpandable(false);
		lockAll.setShrinkable(false);

		showAll.setExpandable(false);
		showAll.setShrinkable(false);
		
		lockAll.addButtonPressedListener(new IButtonPressedListener(){
			boolean unlock = false;
			public void buttonPressed(ButtonPressedEvent e) {
				if(unlock)
				{
					lockAll.setText("  Lock  ");
				}else{
					lockAll.setText("Unlock");
				}
				for (Layer l:layers.getLayers().toArray(new Layer[0]))//just in case there is a concurrent exception
				{
					l.setLocked(!unlock);
				}
				unlock = !unlock;
				
			}
			
		});
		
		showAll.addButtonPressedListener(new IButtonPressedListener(){
			boolean show = false;
			public void buttonPressed(ButtonPressedEvent e) {
				if(show)
				{
					showAll.setText("Hide");
				}else{
					showAll.setText("Show");
				}
				for (Layer l:layers.getLayers().toArray(new Layer[0]))//just in case there is a concurrent exception
				{
					l.setVisible(show);
				}
				show = !show;
				
			}
			
		});
		
		
		scrollContainer = FengGUI.createScrollContainer(centerContainer);
		scrollContainer.setShowScrollbars(true);
		scrollContainer.getVerticalScrollBar().setVisible(false);
		scrollContainer.getHorizontalScrollBar().setVisible(false);
		verticalScroll = FengGUI.createScrollBar(this, false);
	//	verticalScroll.getIncreaseButton().getMinSize().setWidth(32);
		//verticalScroll.getDecreaseButton().getMinSize().setWidth(32);
		
		verticalScroll.setLayoutData(BorderLayoutData.EAST);
	//	verticalScroll.setMinSize(32,64);
		verticalScroll.setVisible(true);
		verticalScroll.getSlider().setVisible(true);
		
		scrollContainer.getVerticalScrollBar().getSlider().addSliderMovedListener(new ISliderMovedListener(){

			public void sliderMoved(SliderMovedEvent sliderMovedEvent) {
				if(sliderMovedEvent.getPosition()!= verticalScroll.getSlider().getValue())
					verticalScroll.getSlider().setValue(sliderMovedEvent.getPosition());
			}
			
		});
		
		verticalScroll.getSlider().addSliderMovedListener(new ISliderMovedListener(){

			public void sliderMoved(SliderMovedEvent sliderMovedEvent) {
				if(sliderMovedEvent.getPosition()!= scrollContainer.getVerticalScrollBar().getSlider().getValue())
					scrollContainer.getVerticalScrollBar().getSlider().setValue(sliderMovedEvent.getPosition());
			}
			
		});
		
		scrollContainer.addSizeChangedListener(new ISizeChangedListener(){

			public void sizeChanged(SizeChangedEvent event) {
				double scrollHeight = scrollContainer.getAppearance().getContentHeight();
				
				verticalScroll.getSlider().setSize(scrollHeight/scrollContainer.getInnerWidget().getSize().getHeight());
				double jumpFactor = -(scrollContainer.getMinHeight() - scrollHeight);
				if(jumpFactor <= 1)
					jumpFactor = 1;
				verticalScroll.setButtonJump(10.0/jumpFactor);
			}
			
		});
		

		
	//	this.addWidget(verticalScroll);
		
	//	scrollContainer.getVerticalScrollBar().setEnabled(true);
		//scrollContainer.getHorizontalScrollBar().setVisible(false);
		
		scrollContainer.setLayoutData(BorderLayoutData.CENTER);
	//	centerContainer.addWidget(scrollContainer);
		
		verticalScroll.getIncreaseButton().setTraversable(false);
		verticalScroll.getDecreaseButton().setTraversable(false);
		verticalScroll.getSlider().getSliderButton().setTraversable(false);
/*		verticalScroll.getSlider().addSliderMovedListener(new ISliderMovedListener(){

			public void sliderMoved(SliderMovedEvent sliderMovedEvent) {
				
				scrollContainer.scrollVertical(sliderMovedEvent.getPosition());
			}
			
		});
		*/
		
		
		Container barOuterContainer = new Container();
		barOuterContainer.setLayoutManager(new BorderLayout());
		scrollContainer.setInnerWidget(barOuterContainer);
		
		barContainer = new Container();		
		
		barOuterContainer.addWidget(barContainer);
		
		barContainer.setLayoutData(BorderLayoutData.NORTH);
		
		barContainer.setLayoutManager(new GridLayout(1,1));
		barContainer.updateMinSize();
		
		scrollContainer.layout();
		
		layers.registerRepositoryListener(new LayerRepositoryListener()
		{

		
			public void layerAdded(Layer layer) {
				//refreshLayers();
				addLayer(layer);
			}

			
			public void layerRemoved(Layer layer) {
				removeLayer(layer);
				//refreshLayers();
			}


		});
		for(Layer layer:layers.getLayers())
		{
			addLayer(layer);
		}
		
		
		
		
	}
	
	public void addLayer(Layer layer) {
		
		LayerBar layerBar = new LayerBar(layer,selectedGroup);
		layerBars.add(layerBar);
		//barContainer.addWidget(layerBar);
		layer.setActive();
		updateGrid();
	}

	public void removeLayer(Layer layer) {
		LayerBar toRemove = null;
		for(LayerBar bar:layerBars)
		{
			if(bar.getLayer() == layer)
			{
				toRemove = bar;
				break;
			}
		}
		if(toRemove!=null)
		{
			layerBars.remove(toRemove);
			updateGrid();
		//	barContainer.removeWidget(toRemove);
		}
	}
	

	private void refreshLayers() {

		layerBars.clear();
		for(Layer layer:layers.getLayers())
		{
			addLayer(layer);
		}
		updateGrid();
	}

	
	private void updateGrid() {
		Collections.sort(layerBars);
		barContainer.removeAllWidgets();
		barContainer.setLayoutManager(null);
		barContainer.layout();
		
		int columns = barContainer.getWidth()/LayerBar.WIDTH;
		if(columns<1)
			columns = 1;
		int rows = layerBars.size()/columns;
		if(layerBars.size()% columns>0)
			rows+=1;
		
		barContainer.setLayoutManager(new GridLayout(rows,columns));
		
		for(LayerBar bar:layerBars)
			barContainer.addWidget(bar);
		
		barContainer.updateMinSize();
		barContainer.layout();
		layerControls.getMinSize().setWidth(barContainer.getMinWidth());
		this.updateMinSize();
		layerControls.getMinSize().setWidth(barContainer.getMinWidth());
		this.layout();
	
		
		scrollContainer.getVerticalScrollBar().getMinSize().setWidth(0);
		scrollContainer.layout();
	
	double scrollHeight = scrollContainer.getAppearance().getContentHeight();
		
		verticalScroll.getSlider().setSize(scrollHeight/scrollContainer.getInnerWidget().getSize().getHeight());
		double jumpFactor = -(scrollContainer.getMinHeight() - scrollHeight);
		if(jumpFactor <= 1)
			jumpFactor = 1;
		verticalScroll.setButtonJump(10.0/jumpFactor);
	//	if(this.getParent()!=null && this.getParent().getParent()!=null && this.getParent().getParent().getParent()!=null )
	//		this.getParent().getParent().getParent().layout();//update the window.... this is a hack.
	}



}
