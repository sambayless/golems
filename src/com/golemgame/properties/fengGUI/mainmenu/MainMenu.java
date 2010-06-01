package com.golemgame.properties.fengGUI.mainmenu;




import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.fenggui.Container;
import org.fenggui.Display;
import org.fenggui.FengGUI;
import org.fenggui.IToggable;
import org.fenggui.Label;
import org.fenggui.RadioButton;
import org.fenggui.StatefullWidget;
import org.fenggui.ToggableGroup;
import org.fenggui.binding.render.ITexture;
import org.fenggui.binding.render.Pixmap;
import org.fenggui.binding.render.lwjgl.LWJGLTexture;
import org.fenggui.decorator.PixmapDecorator;
import org.fenggui.decorator.background.Background;
import org.fenggui.decorator.background.PixmapBackground;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;
import org.fenggui.util.Alignment;
import org.fenggui.util.Color;
import org.fenggui.util.Spacing;
import org.fenggui.util.Span;

import com.golemgame.local.StringConstants;
import com.golemgame.properties.fengGUI.HorizontalTabbedWindow;
import com.golemgame.properties.fengGUI.ITab;
import com.golemgame.properties.fengGUI.ImageTab;
import com.golemgame.properties.fengGUI.optionsMenu.ExitTab;
import com.golemgame.properties.fengGUI.optionsMenu.MachineEnvironmentTab;
import com.golemgame.properties.fengGUI.optionsMenu.MachinePropertiesTab;
import com.golemgame.properties.fengGUI.optionsMenu.MainTab;
import com.golemgame.properties.fengGUI.optionsMenu.SettingsTab;
import com.golemgame.properties.fengGUI.optionsMenu.SkyTab;
import com.golemgame.settings.SettingChangedEvent;
import com.golemgame.settings.SettingsListener;
import com.golemgame.states.GUILayer;
import com.golemgame.states.GeneralSettings;
import com.golemgame.states.StateManager;
import com.jme.image.Texture;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueueManager;

public class MainMenu extends HorizontalTabbedWindow{
	
	private ExitTab exitTab;
	private MainTab mainTab;
	private SettingsTab settingsTab;
	
	
	private Container outerContainer  = null;
	private ArrayList<IToggable<ITab>> toggleButtons = new ArrayList<IToggable<ITab>>();
	private ToggableGroup<ITab> toggleGroup;
	
	private Background selectedBackground;
	private Background unselectedBackground;

	
	public final static int MAIN_MENU_WIDTH = 380;
	public final static int MAIN_MENU_HEIGHT = 400;
	
	@Override
	protected void setCurrentButton(ITab tab) {
		if(this.imageButtonMap.containsKey(tab))
		{
			super.setCurrentButton(null);
			Container curButton = imageButtonMap.get(tab);
			Container oldButton = imageButtonMap.get(getCurrentTab());
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
		}else
			super.setCurrentButton(tab);
	}


	private MainMenu() {
		super();
		super.setTitle(StringConstants.get("MAIN_MENU","Main Menu"));
		toggleGroup = new ToggableGroup<ITab>();
		toggleGroup.addSelectionChangedListener(new ISelectionChangedListener()
		{

			
			public void selectionChanged(
					SelectionChangedEvent selectionChangedEvent) {
				
				ITab selected = toggleGroup.getSelectedValue();
				if(selected != null)
					displayTabInteral(selected);
			}
			
		});
		
		window.getContentContainer().setMinSize(400,400);
		window.getContentContainer().setSizeToMinSize();
		window.getContentContainer().setShrinkable(false);
		window.getContentContainer().setExpandable(false);

		selectedBackground = new PlainBackground(new Color(0.9f,0.9f,0.9f,1f));
		unselectedBackground = new PlainBackground(Color.TRANSPARENT);
		
		
		PlainBackground background = new PlainBackground(new Color(0f,0f,0f,0.7f));
		background.setSpan(Span.MARGIN);
		this.getWindow().getAppearance().add("", background, Span.MARGIN);
		
		GeneralSettings.getInstance().getResolution().addSettingsListener(new SettingsListener<Dimension>()
				{

					
					public void valueChanged(SettingChangedEvent<Dimension> e) {
						
						int width = (int) (e.getNewValue().getWidth());
						int height = (int) (e.getNewValue().getHeight());
						getWindow().getAppearance().setMargin(new Spacing(height/2,width/2,width/2,height/2));
				
					
						getWindow().getPosition().setXY((width/2) - getWindow().getWidth()/2,(height/2) - getWindow().getHeight()/2);
				/*		if (window.getDisplayY() + window.getAppearance().getContentHeight()>  e.getNewValue().height)
						{
							//window.getPosition().setY(0);
							int dif = e.getNewValue().height -( outerContainer.getDisplayY() + outerContainer.getAppearance().getContentHeight());
							window.getPosition().setY(e.getNewValue().width - window.getAppearance().getContentHeight() + dif/2);
						}
			*/
					}
			
				},true);
		
		PlainBackground mainBackground = new PlainBackground(Color.DARK_GRAY);
		mainBackground.setSpan(Span.BORDER);
		this.getWindow().getAppearance().add(mainBackground);
		
		
		buildTabs();
		this.window.layout();
	}
	
	
	public void close() {
		if(outerContainer!=null && this.getWindow().getDisplay() != null)
		{
			
			this.getWindow().getDisplay().removeWidget(outerContainer);
			outerContainer= null;
			
		}
		super.close();
	}

	
	public void display(final Display display,final int priority) {
		
		 cancel=false;
			try{
			StateManager.getGame().executeInGL(new Callable<Object>()
					{
		
						
						public Object call() throws Exception {	
							if(outerContainer!=null)
							{
								display.removeWidget(outerContainer);
								outerContainer=null;
							}
						
							outerContainer = new Container();
							int width =DisplaySystem.getDisplaySystem().getWidth()/2;
							int height =DisplaySystem.getDisplaySystem().getHeight()/2;
							getWindow().getAppearance().setMargin(new Spacing(height,width,width,height));
					
							getWindow().setExpandable(false);
							getWindow().setShrinkable(false);
							getWindow().getContentContainer().setExpandable(true);
							getWindow().getContentContainer().setShrinkable(false);
							getWindow().getContentContainer().setLayoutManager(new BorderLayout());
						//	getWindow().getAppearance().setMargin(new Spacing(height/2,width/2,width/2,height/2));
							//getWindow().getContentContainer().setMinSize(calcMinSize());
							getWindow().getContentContainer().getMinSize().setSize(MAIN_MENU_WIDTH,MAIN_MENU_HEIGHT);
						//	System.out.println(calcMinSize());
							getWindow().getContentContainer().setSize(calcMinSize());
							////getWindow().getContentContainer().setSizeToMinSize();
							
							getWindow().updateMinSize();
						//	getWindow().setSize(340,512);
							getWindow().setSizeToMinSize();
							getWindow().layout();
							outerContainer.addWidget(window);
							outerContainer.updateMinSize();
							outerContainer.setSizeToMinSize();
							display.addWidget(outerContainer,priority);
							display.bringToFront(outerContainer);
							display.setFocusedWidget(window);
						
						
							display.layout();
							displayTab(getCurrentTab());//refresh the current tab.
							
							//outerContainer.layout();
							try{
								getWindow().setMovable(false);
								getWindow().setResizable(false);
							
							
								getWindow().getPosition().setXY(width - getWindow().getWidth()/2,height - getWindow().getHeight()/2);
								getWindow().layout();
							//	outerContainer.layout();
							}catch(IllegalStateException e)
							{
								StateManager.logError(e);
							}
							if (window.getDisplayY() + window.getAppearance().getContentHeight()> display.getHeight())
							{
								//window.getPosition().setY(0);
								int dif = display.getHeight() -( outerContainer.getDisplayY() + outerContainer.getAppearance().getContentHeight());
								window.getPosition().setY(display.getHeight() - window.getAppearance().getContentHeight() + dif/2);
							}
				
							
							return null;
						}

					
				
					});
			}catch(Exception e)
			{
				StateManager.logError(e);
			}
			

	}
	private org.fenggui.util.Dimension calcMinSize() {
		int minWidth = 0;
		int minHeight = 0;
		for(ITab tab:this.getTabs())
		{
			tab.getTab().updateMinSize();
			minWidth = Math.max(minWidth, tab.getTab().getMinWidth());
			minHeight = Math.max(minHeight, tab.getTab().getMinHeight());
		}
		return new org.fenggui.util.Dimension(Math.max(Math.max(minWidth, tabRow.getMinWidth()),super.bottomArea.getWidth()),minHeight + tabRow.getHeight() + super.bottomArea.getHeight());
	}
	
	public void display(Display display) {
	
		super.display(display);
		try{
			this.getWindow().setResizable(false);
			this.getWindow().setMovable(false);
			int width =DisplaySystem.getDisplaySystem().getWidth()/2;
			int height =DisplaySystem.getDisplaySystem().getHeight()/2;
			
			this.getWindow().getPosition().setXY(width - this.getWindow().getWidth()/2,height - this.getWindow().getHeight()/2);
		
		}catch(IllegalStateException e)
		{
			StateManager.logError(e);
		}
	}

	private void displayTabInteral(ITab tab)
	{
		//avoid re-selecting the toggle button
		super.displayTab(tab);
	}
	
	private void buildTabs()
	{
		
		this.tabRow.getAppearance().add(new PlainBackground(Color.WHITE));
		
		
		
		exitTab = new ExitTab(this);
		

		settingsTab=new SettingsTab();

		
	
		
		mainTab = new MainTab(this);
		this.addTab(mainTab);
		this.addTab(settingsTab);
		//this.addTab(new KeyMappingsTab());
		
		this.addTab(new MachinePropertiesTab());
		MachineEnvironmentTab machineOptions = new MachineEnvironmentTab();
		machineOptions.embed(new SkyTab());
		this.addTab(machineOptions);
		
		this.addTab(exitTab);
	}
	
	public void showDefaultTab()
	{
		this.displayTab(mainTab);
	}
	

	public void showExitTab()
	{
		this.displayTab(exitTab);
	}
	
	protected void insertTabButton(final ITab tab) {
		if (tab instanceof ImageTab)
		{
			insertTabButton((ImageTab)tab);		
		}
		else
			super.insertTabButton(tab);
	}

	protected Map<ITab,Container> imageButtonMap = new HashMap<ITab, Container>();
	

	protected void insertTabButton(final ImageTab tab) {
		Container buttonContainer = new Container();
		imageButtonMap.put(tab, buttonContainer);
		buttonContainer.setLayoutManager(new RowLayout(false));
		RadioButton<ITab> button = new RadioButton<ITab>(toggleGroup);
		button.setValue(tab);
		button.setText("");
		
		toggleButtons.add(button);
		
		
		
		button.getAppearance().add(RadioButton.STATE_SELECTED,new PixmapBackground(getPixmap(tab.getActive())) );
		button.getAppearance().add(RadioButton.STATE_DESELECTED,new PixmapBackground(getPixmap(tab.getInactive())) );
		button.getAppearance().add(StatefullWidget.STATE_HOVERED,new PixmapBackground(getPixmap(tab.getHover())) );
		button.getAppearance().add(new PixmapDecorator("",getPixmap(tab.getIcon())) );
		
		
		button.setSelected(false);
		button.setMinSize(64,64);
		button.setSize(64,64);
		button.setShrinkable(false);
		button.setExpandable(false);
		
		buttonContainer.addWidget(button);
		Label label =  FengGUI.createLabel(buttonContainer,tab.getTitle());
		label.setExpandable(false);
		label.getAppearance().setAlignment(Alignment.MIDDLE);
		tabRow.addWidget(buttonContainer);
		tabRow.layout();



		this.window.layout();
	}
	
	
	public void displayTab(ITab tab) {
		tab.getTab().setLayoutData(BorderLayoutData.CENTER);
		super.displayTab(tab);
		if(tab != null)
		{
			for(IToggable<ITab> toggleButton:toggleButtons)
			{
				if(toggleButton.getValue()==tab)
				{
					if(!toggleButton.isSelected())
					{
						toggleButton.setSelected(true);
					}
					break;
				}
			}
		}

	}

	private static MainMenu instance = null;
	public static synchronized MainMenu getMainMenu()
	{
		if(instance == null)
		{
			GUILayer.getLoadedInstance();
			instance = new MainMenu();
		}
		return instance;
	}
	
	public static void toggleMenu()
	{
		GUILayer.getLoadedInstance();//dont block in the game task queue...
		GameTaskQueueManager.getManager().update(new Callable<Object>()
				{

					
					public Object call() throws Exception {
						if(GUILayer.getInstance().isLoaded())
						{
							if(getMainMenu().getWindow().getDisplay()==null)
							{
								getMainMenu().display(GUILayer.getInstance().getDisplay(), Container.RAISED_PRIORITY);
							}else
							{
								getMainMenu().setCancel(true);
								getMainMenu().close();
							}
					
						
						
						}

						return null;
					}
			
				});
		
	
	}
	
	private Pixmap getPixmap(Texture texture)
	{
		TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		ts.setTexture(texture);
		ts.apply();
		
		ITexture onTexture = new LWJGLTexture(texture.getTextureId(),64,64,64,64);
		return new Pixmap(onTexture,0,0,64,64);
	}



	public void showRecordingTab() {
		this.displayTab(settingsTab);
		settingsTab.showRecordingTab();
	}
}
