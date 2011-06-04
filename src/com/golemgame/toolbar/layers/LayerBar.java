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



import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.RadioButton;
import org.fenggui.TextEditor;
import org.fenggui.ToggableGroup;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.FocusEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.IFocusListener;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.event.key.IKeyListener;
import org.fenggui.event.key.KeyPressedEvent;
import org.fenggui.event.key.KeyReleasedEvent;
import org.fenggui.event.key.KeyTypedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;
import org.fenggui.util.Color;

import com.golemgame.mechanical.layers.Layer;
import com.golemgame.mechanical.layers.LayerListener;

/**
 * Includes the controls for a single layer 
 * @author Sam
 *
 */
public class LayerBar extends Container implements Comparable<LayerBar>{
	public static int WIDTH = 400;
	public static int HEIGHT = 32;
	
	private final Layer layer;
	
	private TextEditor name;
	private Button visible;
	
	private Button lock;
	private Button background;
	private RadioButton<LayerBar> selected;
	private PlainBackground backgroundBGR;
	public LayerBar(final Layer layer, ToggableGroup<LayerBar> toggleSet) {
		super();
		this.layer = layer;
		//this.setShrinkable(false);
		//this.setExpandable(false);
		this.setSize(WIDTH, 32);
		this.setMinSize(WIDTH, 32);
		Container controlContainer = new Container();
		this.addWidget(controlContainer);
		
		this.getAppearance().add(new PlainBackground(Color.GRAY));
		
		this.setLayoutManager(new RowLayout());
		
		selected = FengGUI.<LayerBar>createRadioButton(controlContainer, "",toggleSet);
		selected.setExpandable(false);
	
	
		name = FengGUI.createTextEditor(controlContainer);
		
		lock = FengGUI.createButton(controlContainer,"  Lock  ");
		lock.setExpandable(false);
		lock.setShrinkable(false);
		visible = FengGUI.createButton(controlContainer," Hide ");
		visible.setExpandable(false);
		visible.setShrinkable(false);
		name.setText(layer.getName());
		
		background = new Button();
	//	this.addWidget(background);
		
		this.setLayoutManager(new BorderLayout());
		background.setLayoutData(BorderLayoutData.CENTER);
		controlContainer.setLayoutData(BorderLayoutData.CENTER);
		
		backgroundBGR = new PlainBackground();
		//background.getAppearance().add(backgroundBGR);
		this.getAppearance().add(backgroundBGR);
	//	selected.getAppearance().add(backgroundBGR);
	//	name.getAppearance().add(backgroundBGR);
		
		name.addFocusListener(new IFocusListener()
		{

			public void focusChanged(FocusEvent focusChangedEvent) {
				if(focusChangedEvent.isFocusGained())
				{
					layer.setActive();
				}else{
					name.setText(layer.getName());
				}
			}
			
		});
		
		name.addKeyListener(new IKeyListener(){

			public void keyPressed(KeyPressedEvent keyPressedEvent) {
				//if(keyPressedEvent.getKeyClass() == Key.ENTER)
				{
			
				}
			}

			public void keyReleased(KeyReleasedEvent keyReleasedEvent) {
				String newName = name.getText();
				if(newName.length()>0 &&! newName.equals(layer.getName()))
				{
					int pos = name.getAppearance().getData().getActivePositionIndex();
					layer.getInterpreter().setLayerName(newName);
					layer.refresh();
					 name.getAppearance().getData().setActivePositionIndex(pos);
				}
			}

			public void keyTyped(KeyTypedEvent keyTypedEvent) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		lock.addButtonPressedListener(new IButtonPressedListener(){

			public void buttonPressed(ButtonPressedEvent e) {
				layer.setLocked(!layer.isLocked());
				if(layer.isLocked())
				{
					lock.setText("Unlock");
				}else
					lock.setText(" Lock ");
			
			}
			
		});
		
		visible.addButtonPressedListener(new IButtonPressedListener(){

			public void buttonPressed(ButtonPressedEvent e) {
				layer.setVisible(!layer.isVisible());
				if(layer.isVisible())
				{
					visible.setText("Hide");
				}else
					visible.setText("Show");
			}
			
		});
		
		selected.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(
					SelectionChangedEvent e) {
				if(e.isSelected())
				{
					layer.setActive();
				}
			}
			
		});
		
		layer.registerListener(new LayerListener()
		{

			public void layerState(boolean isvisible, boolean islocked,
					boolean active) {
				if(isvisible)
				{
					visible.setText("Hide");
				}else
					visible.setText("Show");
				
				if(islocked)
				{
					lock.setText("Unlock");
				}else
					lock.setText(" Lock ");
				
				setActive(active);
			}



			public void refreshLayer() {
				name.setText(layer.getName());
			}
			
		});
		
		/*delete.addButtonPressedListener(new IButtonPressedListener()
		{

			public void buttonPressed(ButtonPressedEvent e) {
			
				try{
					RemoveComponentAction comp = (RemoveComponentAction) layer.getRepository().getAction(Action.REMOVE_COMPONENT);
					comp.setComponent(layer.getStore());
					if(comp.doAction())
						UndoManager.getInstance().addAction(comp);
				}catch(ActionTypeException e1)
				{
					StateManager.logError(e1);
				}
			}
			
		});*/
		
		this.setMinSize(WIDTH, 32);
		this.setActive(layer.isActive());
	}

	public Layer getLayer() {
		return layer;
	}

	public void showActive() {
		backgroundBGR.setColor(Color.WHITE);
	}

	public void showInactive() {
		backgroundBGR.setColor(Color.GRAY);
	}

	public int compareTo(LayerBar o) {
		return layer.getName().compareTo(o.getLayer().getName());
	}
	
	
	private void setActive(boolean active) {
		selected.setSelected(active);
		if(active)
		{
			
			showActive();
		}
		else
		{
			showInactive();
		}
	}
	
}
