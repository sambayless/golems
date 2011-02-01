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
package com.golemgame.instrumentation;

import org.fenggui.FengGUI;
import org.fenggui.Slider;
import org.fenggui.event.ISliderMovedListener;
import org.fenggui.event.IWindowChangedListener;
import org.fenggui.event.SliderMovedEvent;
import org.fenggui.event.mouse.MouseAdapter;
import org.fenggui.event.mouse.MouseDoubleClickedEvent;

public class SlideAxisController extends SingleAxisController {
	private Slider val;
	private float curVal = 0;
	private final static float CONVERSION = 0.5f;
	private boolean horizontal = false;
	private boolean locked = false;
	private boolean userPositioned = false;
	public boolean isUserPositioned() {
		return userPositioned;
	}

	public void setUserPositioned(boolean userPositioned) {
		this.userPositioned = userPositioned;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isLocked() {
		return locked;
	}
	public SlideAxisController(boolean horizontal, boolean showArrows) {
		super();
		this.horizontal = horizontal;
		super.setTypeName("Slider");
		if(horizontal)
		{			
			super.window.setSize(200, 25);			
		}else{
			super.window.getMinSize().setWidth(25);
			super.window.setTitle("");
			super.window.setSize(25, 200);
		}
		if(showArrows)
		{
			val = FengGUI.createScrollBar(controlContainer,horizontal).getSlider();
			
		}else
		{
			val = FengGUI.createSlider(controlContainer,horizontal);
		}
			
		val.setValue(curVal*CONVERSION + CONVERSION);
		
		val.addMouseListener(new MouseAdapter(){

			@Override
			public void mouseDoubleClicked(MouseDoubleClickedEvent mouseDoubleClickedEvent) {
				window.setWindowed(!window.isWindowed());
			}
			
		});
		window.addWindowChangedListener(new IWindowChangedListener(){

			public void userChanged() {
				userPositioned = true;
			}
			
		});
		
		super.registerListener(new AxisControlListener()
		{
			public void valueSet(float value) {
				if(curVal!=value)
				{
					curVal = value;
					float convertedVal = curVal*CONVERSION + CONVERSION;
					val.setValue(convertedVal);
				}
			}			
		});
		
		
		val.addSliderMovedListener(new ISliderMovedListener()
		{
			public void sliderMoved(SliderMovedEvent sliderMovedEvent) {
				SlideAxisController.this.setCurrentValue((float)(sliderMovedEvent.getPosition()/CONVERSION)-1f);
			}
		});
		
//		super.getInstrumentInterface().layout();
//		super.getInstrumentInterface().getContentContainer().layout();
	}
	public boolean isWindowed() {
		return window.isWindowed();
	}
	
	
	@Override
	public void setName(String name) {
		super.setName(name);
		if(!horizontal)
		{
			super.window.getTitleLabel().getMinSize().setWidth(0);
			super.window.getMinSize().setWidth(25);
			super.window.setTitle("");
			super.window.setSize(25, 200);
		
		}
	}
	
	
	
}
