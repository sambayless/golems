package com.golemgame.properties.fengGUI.optionsMenu;


import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.IContainer;
import org.fenggui.decorator.background.GradientBackground;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;
import org.fenggui.util.Color;

import com.golemgame.properties.fengGUI.HorizontalTabbedWindow;
import com.golemgame.properties.fengGUI.ITab;


public class VerticalTabbedWindow extends HorizontalTabbedWindow{

	
	
	public VerticalTabbedWindow() {
		super();
		Container tabRowContainer = FengGUI.createContainer((IContainer)tabRow.getParent());
		tabRowContainer.setLayoutManager(new BorderLayout());
		tabRowContainer.setLayoutData(BorderLayoutData.WEST);
		((IContainer)tabRow.getParent()).removeWidget(tabRow);
		tabRowContainer.addWidget(tabRow);
		 tabRow.setLayoutData(BorderLayoutData.NORTH);
		 tabRow.setLayoutManager(new RowLayout(false));
	
		 Color leftColor = new Color(211, 211, 211, 90);
		 
		 tabRowContainer.getAppearance().add(new GradientBackground(leftColor, Color.DARK_GRAY, Color.DARK_GRAY,leftColor));
		 tabRowContainer.getParent().layout();
		 tabRow.layout();
	}


	
	protected void insertTabButton(ITab tab) {
		Button button = FengGUI.createButton(tabRow);
		final ITab fTab = tab;
		button.setText(tab.getTitle());
		button.addButtonPressedListener(new IButtonPressedListener()
		{

			
			public void buttonPressed(ButtonPressedEvent e) {
				displayTab(fTab);
				
			}
			
		});

		tabRow.layout();
	}

	
}
