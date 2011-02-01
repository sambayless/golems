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

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.event.mouse.MouseAdapter;
import org.fenggui.event.mouse.MousePressedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.GridLayout;

public class CustomColorPallete extends Container{

	private Collection<ColorListener> listeners = new CopyOnWriteArrayList<ColorListener>();
	
	private final int rows;
	private final int columns;
	public CustomColorPallete(int rows, int columns) {
		super();
		this.rows = rows;
		this.columns = columns;
		buildGUI();
	}

	private ColorPatch[][] palleteSquares;
	
	
	private void buildGUI()
	{
		
		Container mainContainer = this;
		mainContainer.setLayoutManager(new BorderLayout());
		
		Container palleteContainer = FengGUI.createContainer(mainContainer);
		palleteContainer.setLayoutData(BorderLayoutData.CENTER);
		palleteContainer.setLayoutManager(new GridLayout(rows,columns));
	
		 palleteSquares = new ColorPatch[rows][columns];
		
		for (int r = 0; r<rows;r++)
		{
			for(int c = 0; c<columns;c++)
			{
				ColorPatch square = new ColorPatch();
				palleteSquares[r][c] = square;
				final int rF = r;
				final int cF = c;
				square.addMouseListener(new MouseAdapter()
				{

					@Override
					public void mousePressed(MousePressedEvent mousePressedEvent) {
						squarePressed(rF,cF);
					}
					
				});
			}
		}
		
		
		
	}
	
	private void squarePressed(int r, int c)
	{
		ColorData color = null;//get the color from somewhere...
		for(ColorListener listener:listeners)
		{
			listener.colorSelected(color);
		}
		
	}
	
	
}
