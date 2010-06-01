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
