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
package com.golemgame.menu.color;

import java.util.ArrayList;

import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.TextEditor;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.key.KeyAdapter;
import org.fenggui.event.key.KeyReleasedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.GridLayout;
import org.fenggui.layout.RowLayout;
import org.fenggui.util.Color;

public class ColorDialog extends Container {
	private ColorSquare colorSquare;
	private LightnessMeter meter;
	private AlphaMeter alphaMeter;
	private ColorPatch patch;
	private ColorPatch secondaryPatch;
	private TextEditor hue ;
	private TextEditor saturation ;
	private TextEditor lightness;
	
	private TextEditor red;
	private TextEditor green;
	private TextEditor blue;
	
	private TextEditor alpha;
	
	private Object locked = null;
	
	private ColorData color = new ColorData();
	
	private ArrayList<ColorDialogListener> listeners = new ArrayList<ColorDialogListener>();
	
	public ColorDialog() {
		super();

		colorSquare = new ColorSquare(256,128);
		meter = new LightnessMeter(4,128);
		meter.setMinSize(32,0);
		alphaMeter = new AlphaMeter(4,128);
		alphaMeter.setMinSize(32,0);
		patch = new ColorPatch();
		
		Container colorContainer = FengGUI.createContainer(this);
		colorContainer.setLayoutManager(new BorderLayout());
		colorContainer.setLayoutData(BorderLayoutData.CENTER);
		
		//colorContainer.getAppearance().add(new PlainBackground(Color.GRAY));
		
		colorContainer.addWidget(colorSquare);
		
		colorContainer.addWidget(patch);
		
		Container meterContainer = FengGUI.createContainer(colorContainer);
		meterContainer.setLayoutData(BorderLayoutData.EAST);
		meterContainer.setLayoutManager(new RowLayout());
		meterContainer.addWidget(meter);
		meterContainer.addWidget(alphaMeter);
		
		
		this.setLayoutManager(new BorderLayout());
		colorSquare.setLayoutData(BorderLayoutData.CENTER);

	
		patch.setLayoutData(BorderLayoutData.NORTH);
		colorSquare.addColorListener(new ColorListener()
		{

			
			public void colorSelected(ColorData color) {
				setColor(color);
			}
			
		});
		
		
		meter.addColorListener(new ColorListener()
		{

			
			public void colorSelected(ColorData color) {
				setColor(color);
			}
			
		});
		
		alphaMeter.addColorListener(new ColorListener()
		{

			
			public void colorSelected(ColorData color) {
				setColor(color);
			}
			
		});
		
		
		
		//Build the bottom half of the dialog, with text boxes for setting the colors manually.
		
		Container settings = FengGUI.createContainer(this);
		settings.setLayoutData(BorderLayoutData.SOUTH);
		settings.setLayoutManager(new BorderLayout());
		
		Container westContainer = FengGUI.createContainer(settings);
		westContainer.setLayoutData(BorderLayoutData.WEST);
		westContainer.setLayoutManager(new RowLayout(true));
		
		 secondaryPatch = new ColorPatch();
		 westContainer.addWidget(secondaryPatch);

		secondaryPatch.setMinSize(64,64);
		
		Container textBoxes = FengGUI.createContainer(westContainer);

		textBoxes.setLayoutManager(new GridLayout(4,4));
		

		

		 FengGUI.createLabel(textBoxes,"Hue:");
	
		hue = FengGUI.createTextEditor(textBoxes);
	
		hue.setMinSize(50, hue.getMinSize().getHeight());
		FengGUI.createLabel(textBoxes,"Red:");
		
			red = FengGUI.createTextEditor(textBoxes);

			red.setMinSize(50, red.getMinSize().getHeight());

		



		FengGUI.createLabel(textBoxes,"Sat:");
	
		saturation = FengGUI.createTextEditor(textBoxes);
		saturation.setMinSize(50, saturation.getMinSize().getHeight());

		FengGUI.createLabel(textBoxes,"Green:");

		green = FengGUI.createTextEditor(textBoxes);
	
		green.setMinSize(50, green.getMinSize().getHeight());



		FengGUI.createLabel(textBoxes,"Lum:");
	
		lightness = FengGUI.createTextEditor(textBoxes);
		lightness.setMinSize(50, lightness.getMinSize().getHeight());

		FengGUI.createLabel(textBoxes,"Blue:");

		blue = FengGUI.createTextEditor(textBoxes);
	
		FengGUI.createLabel(textBoxes);
		FengGUI.createLabel(textBoxes);
		FengGUI.createLabel(textBoxes,"Alpha");
		alpha = FengGUI.createTextEditor(textBoxes);
		//blue.setMinSize(50, blue.getMinSize().getHeight());

		//lightness.addKeyListener(
		KeyAdapter hslListener = new KeyAdapter()
		{

			@Override
			public void keyReleased(KeyReleasedEvent keyReleasedEvent) {
				try{
					locked = keyReleasedEvent.getSource();
				
					double h = ((double)Integer.valueOf(hue.getText()))/100.0;
					double s = ((double)Integer.valueOf(saturation.getText()))/100.0;
					double l = ((double)Integer.valueOf(lightness.getText()))/100.0;
					
					if (h > 1.0)
						h = 1.0;
					else if (h<0)
						h = 0;
					
					if (s > 1.0)
						s = 1.0;
					else if (s<0)
						s = 0;
					
					if (l > 1.0)
						l = 1.0;
					else if (l<0)
						l = 0;
					
					ColorData color = new ColorData();
					color.setHue(h);
					color.setSaturation(s);
					color.setLightness(l);
					color.copyHSLtoRGB();
					setColor(color);
					locked= null;
				}catch(NumberFormatException e)
				{
					
				}finally{
					keyReleasedEvent.setUsed();
				}
			}

			
	
			
		};
		hue.addKeyListener(hslListener);
		saturation.addKeyListener(hslListener);
		lightness.addKeyListener(hslListener);
		

		KeyAdapter rgbListener = new KeyAdapter()
		{

			@Override
			public void keyReleased(KeyReleasedEvent keyReleasedEvent) {
				try{
					locked = keyReleasedEvent.getSource();
					int r = Integer.valueOf(red.getText());
					int g = Integer.valueOf(green.getText());
					int b = Integer.valueOf(blue.getText());
				
					float a = Float.valueOf(alpha.getText());
					
					if (r > 255)
						r = 255;
					else if (r<0)
						r= 0;
			
					if (g > 255)
						g = 255;
					else if (g<0)
						g= 0;
					if (b > 255)
						b = 255;
					else if (b<0)
						b= 0;
					
					if(a>100f)
						a = 100f;
					if (a<0f)
						a = 0;
					
					ColorData color = new ColorData();
					color.setR(r);
					color.setG(g);
					color.setB(b);
					color.setAlpha(a/100f);
					color.copyRGBtoHSL();
					setColor(color);
					locked= null;
				}catch(NumberFormatException e)
				{
					
				}finally
				{
					keyReleasedEvent.setUsed();
				}
			}

			
			
		};
		red.addKeyListener(rgbListener);
		green.addKeyListener(rgbListener);
		blue.addKeyListener(rgbListener);
		alpha.addKeyListener(rgbListener);
		
		
		Container buttonContainer = FengGUI.createContainer(settings);
		buttonContainer.setLayoutData(BorderLayoutData.EAST);
		buttonContainer.setLayoutManager(new BorderLayout());
		Container secondButtonContainer = FengGUI.createContainer(buttonContainer);
		secondButtonContainer.setLayoutData(BorderLayoutData.SOUTH);
		secondButtonContainer.setLayoutManager(new RowLayout(true));
		
		Button ok = FengGUI.createButton(secondButtonContainer,"OK");
		Button cancel = FengGUI.createButton(secondButtonContainer,"Cancel");
		
		
		ok.addButtonPressedListener(new IButtonPressedListener()
		{

			
			public void buttonPressed(ButtonPressedEvent e) {
				//Convert the listeners to an array first, to prevent a concurrent modification error if the listeners remove themselves
				int alpha = (int)Math.round((color.getAlpha() * 255.0));
				if (alpha >255)
					alpha = 255;
				if (alpha <0)
					alpha = 0;
				for (ColorDialogListener listener:listeners.toArray(new ColorDialogListener[listeners.size()]))
				{
					
					listener.colorChosen(color.getR(),color.getG(),color.getB(),alpha);
				}
			}
			
		});
		
		cancel.addButtonPressedListener(new IButtonPressedListener()
		{

			
			public void buttonPressed(ButtonPressedEvent e) {
				//Convert the listeners to an array first, to prevent a concurrent modification error if the listeners remove themselves
				for (ColorDialogListener listener:listeners.toArray(new ColorDialogListener[listeners.size()]))
				{
					listener.cancel();
				}
			}
			
		});
		
		
		
	
		
		
		
		this.layout();
	}
	



	public void setColor(ColorData color)
	{
		this.color = color;
		meter.setColor(color);
		meter.refresh();
		alphaMeter.setColor(color);
		alphaMeter.refresh();
		colorSquare.setColor(color);
		colorSquare.refresh();
		patch.setColor(new Color(color.getR(),color.getG(), color.getB(),(int)Math.round(255.0*color.getAlpha())));
		secondaryPatch.setColor(new Color(color.getR(),color.getG(), color.getB(),(int)Math.round(255.0*color.getAlpha())));

		if(hue != locked)
			hue.setText(String.valueOf(Math.round( color.getHue()*100.0)));
		
		if(saturation != locked)
			saturation.setText(String.valueOf(Math.round(color.getSaturation()*100.0)));
		
		if(lightness != locked)
			lightness.setText(String.valueOf(Math.round(color.getLightness()*100.0)));
		
		if(red != locked)
			red.setText(String.valueOf(color.getR()));
		if(green != locked)
			green.setText(String.valueOf(color.getG()));
		if(blue != locked)
			blue.setText(String.valueOf(color.getB()));
		
		if(alpha != locked)
			alpha.setText(String.valueOf(Math.round( color.getAlpha()*1000f)/10f));

	}
	
	public void addColorListener(ColorDialogListener listener)
	{
		listeners.add(listener);
	}
	
	public void removeColorListener(ColorDialogListener listener)
	{
		listeners.remove(listener);
	}
	
	public static interface ColorDialogListener
	{
		public void colorChosen(int red, int green, int blue, int alpha);
		public void cancel();
	}
}
