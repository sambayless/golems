package com.golemgame.properties.fengGUI;

import java.util.HashMap;
import java.util.Map;

import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.decorator.background.Background;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;
import org.fenggui.util.Color;

public class HorizontalTabbedTab extends TabbedTab {
	public HorizontalTabbedTab(String title) {
		super(title);
	}

	protected Container tabRow;
	protected Container mainArea;

	//static final Dimension MIN_TAB_SIZE = new Dimension(350,200);

	
	private Button getTabButton(ITab tab)
	{
		return buttonMap.get(tab);
	}
	private Background selectedBackground;
	private Background unselectedBackground;
	protected void setCurrentButton(ITab tab)
	{
		Button curButton = getTabButton(tab);
		Button oldButton = getTabButton(super.getCurrentTab());
		if(oldButton!=null)
		{
			oldButton.getAppearance().remove(selectedBackground);
			oldButton.getAppearance().add(unselectedBackground);
		}
		if(curButton!=null)
		{
			curButton.getAppearance().remove(unselectedBackground);
			curButton.getAppearance().add(selectedBackground);
		}
	}
	
	public void displayTab(ITab tab) {
		setCurrentButton(tab);
		mainArea.removeAllWidgets();
		mainArea.addWidget(tab.getTab());
		tabRow.setVisible(super.getTabs().size() > 1);
	
		//mainArea.pack();
		mainArea.updateMinSize();
		getTab().updateMinSize();
		
	/*	if (getTab().getMinSize().getWidth()>getTab().getSize().getWidth())
		{
			getTab().getSize().setWidth(getTab().getMinSize().getWidth());
		}
		if (getTab().getMinSize().getHeight()>getTab().getSize().getHeight())
		{
			getTab().getSize().setHeight(getTab().getMinSize().getHeight());
		}
		
		if (getTab().getSize().getWidth() < MIN_TAB_SIZE.getWidth())
			getTab().getSize().setWidth( MIN_TAB_SIZE.getWidth());
		
		if (getTab().getSize().getHeight() < MIN_TAB_SIZE.getHeight())
			getTab().getSize().setHeight( MIN_TAB_SIZE.getHeight());
		*/
		getTab().layout();
		/*if (getTab().getParent() != null)
		{
			
			Spacing border =  getTab().getAppearance().getBorder();
			//.getTop() + getTab().getAppearance().getBorder().getTop() + getTab().getAppearance().getContentHeight();
			Spacing padding = getTab().getAppearance().getPadding();
			
			Spacing margin = getTab().getAppearance().getMargin();
			
			int top = getTab().getDisplayY() + getTab().getAppearance().getContentHeight()+border.getBottomPlusTop() + padding.getBottomPlusTop() +margin.getBottom();
			
			
			if (top> getTab().getParent().getSize().getHeight())
			{
				getTab().getPosition().setY(getTab().getParent().getSize().getHeight() - top);
			}
		}*/
		super.displayTab(tab);
		//if (getTab().getDisplayY()<0)
	//		getTab().getPosition().setY(0);
	}

	protected Map<ITab,Button> buttonMap = new HashMap<ITab, Button>();
	
	protected void insertTabButton(ITab tab)
	{
		Button button = FengGUI.createButton(tabRow);
		buttonMap.put(tab, button);
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
	
	protected void buildGUI()
	{
		selectedBackground = new PlainBackground(Color.WHITE);
		unselectedBackground = new PlainBackground(Color.TRANSPARENT);
		
	    	getTab().setLayoutManager(new BorderLayout());
	        
	        mainArea = FengGUI.createContainer(getTab());
	        tabRow = FengGUI.createContainer(getTab());
	        mainArea.setLayoutData(BorderLayoutData.CENTER);
	        tabRow.setLayoutData(BorderLayoutData.NORTH);
	    
	        
	        tabRow.setLayoutManager(new RowLayout());
	        mainArea.setLayoutManager(new RowLayout());
	        
			  

	        
	   
	      
	}

}
