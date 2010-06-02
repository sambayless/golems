package com.golemgame.properties.fengGUI.optionsMenu;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.List;
import org.fenggui.ScrollContainer;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.decorator.border.TitledBorder;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.key.IKeyListener;
import org.fenggui.event.key.Key;
import org.fenggui.event.key.KeyPressedEvent;
import org.fenggui.event.key.KeyReleasedEvent;
import org.fenggui.event.key.KeyTypedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.GridLayout;
import org.fenggui.layout.RowLayout;
import org.fenggui.util.Alignment;
import org.fenggui.util.Color;

import com.golemgame.local.StringConstants;
import com.golemgame.menu.KeyMappingConversion;
import com.golemgame.properties.fengGUI.ImageTabAdapter;
import com.golemgame.settings.KeySetting;
import com.golemgame.states.GeneralSettings;
import com.golemgame.tool.ActionToolSettings;
import com.jme.image.Texture;
import com.jme.input.KeyInput;


public class KeyMappingsTab extends ImageTabAdapter {

	private List<KeyMapping> mapList;
	
	private Map<KeyMapping, Integer> changeMap;
	
	private KeyMapping  activeMapping = null;
	private Button currentButton = null;
	private Texture icon;
	public KeyMappingsTab() {
		super(StringConstants.get("MAIN_MENU.KEYBOARD" ,"Keyboard"));
		 icon =super.loadTexture("buttons/menu/Keyboard.png");
	}
	
	@Override
	public Texture getIcon() {
		return icon;
	}
	
	
	
	public void open() {
		
	}



	
	public void close(boolean cancel) 
	{
		
		if(!cancel)
		{
			for(KeyMapping mapping:changeMap.keySet())
			{
				Integer newKey = changeMap.get(mapping);
				if(newKey!= null)
					mapping.setKey(newKey);
			}
			
		}
		
		changeMap = new HashMap<KeyMapping, Integer>();
		
		setActiveMapping(null,null);
		super.close(cancel);
	}



	
	protected void buildGUI() {
		changeMap = new HashMap<KeyMapping, Integer>();
		java.util.List<KeyMapping> keyMappings = generateKeyMappings();
		
	//	mapList = FengGUI.<KeyMapping>createList(getTab());
		
		Container internal = FengGUI.createContainer(getTab());
		
		internal.getAppearance().add(new TitledBorder(StringConstants.get("MAIN_MENU.KEYBOARD.SHORTCUTS" ,"Keyboard Shortcuts")));
		
		ScrollContainer scrollContainer = FengGUI.createScrollContainer(internal);
		scrollContainer.setShowScrollbars(true);
		
		Container mapNorth = FengGUI.createContainer();
		//Label testLabel = FengGUI.createLabel("TTTTTTTT\n\n\n\n\n\n\n\n\n\n\n\n\nTTTTTTTTTT\n\n\n\n\n\n\n\n\nTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT\nTTTTTTTTTT\n\n\n\n\nTTTTTTTTTTTTTTTT");
		//testLabel.getAppearance().getData().setMultiline(true);
		//testLabel.getAppearance().getData().setWordWarping(true);
		scrollContainer.setInnerWidget(mapNorth);
		mapNorth.setLayoutManager(new BorderLayout());
		Container mappingContainer = FengGUI.createContainer(mapNorth);
		mappingContainer.setLayoutData(BorderLayoutData.NORTH);
		mappingContainer.setLayoutManager(new RowLayout(false));
		
		
		IKeyListener keyListener = new IKeyListener()
		{

			
			public void keyPressed(KeyPressedEvent keyPressedEvent) {
				int keyChar = (int) keyPressedEvent.getKey();
				Key keyClass = keyPressedEvent.getKeyClass();
				int jmeKey;
				if(keyChar <= 0)
					jmeKey = KeyMappingConversion.getJMEKey(keyClass);
				else
					jmeKey = KeyMappingConversion.getJMEKey(keyChar);
				if (jmeKey == -1)
					jmeKey = KeyInput.KEY_UNLABELED;
				setKeyMapping(jmeKey);
				
			}

			
			public void keyReleased(KeyReleasedEvent keyReleasedEvent) {
				// TODO Auto-generated method stub
				
			}

			
			public void keyTyped(KeyTypedEvent keyTypedEvent) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		
		IButtonPressedListener buttonListener = new IButtonPressedListener()
		{

			
			public void buttonPressed(ButtonPressedEvent e) {
				
				KeyMapping mapping;
				
				if(!(e.getSource().getCustomData() instanceof KeyMapping  ))
					return;
				
				Button source = (Button)e.getSource();
				
				
				
				mapping = (KeyMapping) e.getSource().getCustomData();
				
				setActiveMapping(mapping,source);
			}

		
		};
		
		
		for (KeyMapping mapping:keyMappings)
		{
			
			Container localContainer = FengGUI.createContainer(mappingContainer);
			localContainer.setLayoutManager(new BorderLayout());
			
			Container gridContainer = FengGUI.createContainer(localContainer);
			gridContainer.getAppearance().add(new PlainBackground(Color.WHITE));
			
			gridContainer.setLayoutManager(new GridLayout(1,2));
			gridContainer.setLayoutData(BorderLayoutData.NORTH);
			
			Label mapLabel = FengGUI.createLabel(gridContainer);
		
			mapLabel.setExpandable(false);
			//mapLabel.setShrinkable(false);
		//	mapLabel.setWidth(60);
		//	mapLabel.setMinSize(60, mapLabel.getMinHeight());
			mapLabel.setText(mapping.getDescription());
			
			Button keyButton = new Button() ;//FengGUI.createButton(gridContainer);
			keyButton.setTraversable(false);
			gridContainer.addWidget(keyButton);
			keyButton.getAppearance().setAlignment(Alignment.RIGHT);
			
			//keyButton.getAppearance().add(Button.STATE_HOVERED, new PlainBackground(Color.LIGHT_GRAY));
			
/*			
			keyButton.getAppearance().add(Button.STATE_DEFAULT, new PlainBackground(Color.WHITE));
			keyButton.getAppearance().add(Button.STATE_PRESSED, new PlainBackground(Color.GRAY));
			keyButton.getAppearance().add(Button.STATE_HOVERED, new PlainBackground(Color.LIGHT_GRAY));
			keyButton.getAppearance().add(Button.STATE_FOCUSED, new PlainBackground(Color.GRAY));
			keyButton.getAppearance().add(Button.STATE_NONE, new PlainBackground(Color.WHITE));*/
			
			
			keyButton.setText(mapping.getCurrentKeyDescription());
			keyButton.setCustomData(mapping);
			keyButton.setExpandable(false);
			//keyButton.setShrinkable(false);
		//	keyButton.setWidth(350);
			keyButton.setMinSize(50,keyButton.getMinHeight());
			
		//	keyButton.addKeyListener(keyListener);
			
			keyButton.addButtonPressedListener(buttonListener);
			keyButton.addKeyListener(keyListener);
			
		}
		
	
		mapNorth.layout();
		scrollContainer.layout();
		scrollContainer.scrollVertical(1);
	}
	
	
	public void setKeyMapping(int jmeKey)
	{
		if (activeMapping != null)
		{
			changeMap.put(activeMapping, jmeKey);
			currentButton.setText(activeMapping.getCurrentKeyDescription());
		}
		
	}
	




	private void setActiveMapping(KeyMapping mapping,Button button)
	{
		if (this.currentButton != null)
		{
			this.currentButton.setText(((KeyMapping)currentButton.getCustomData()).getCurrentKeyDescription());
			this.currentButton = null;
		}
		
		
		this.activeMapping = mapping;
		
		this.currentButton = button;
		if(button!=null)
			button.setText("(Press a key)");
	}
	
	private java.util.List<KeyMapping> generateKeyMappings() {
		
		java.util.List<KeyMapping> mapList = new ArrayList<KeyMapping>();
		
		mapList.add(new KeyMapping("Control Points", ActionToolSettings.getInstance().getModifyKey()));
		
		mapList.add(new KeyMapping("Rotate", ActionToolSettings.getInstance().getRotateKey()));
		
		mapList.add(new KeyMapping("XY-Plane", ActionToolSettings.getInstance().getXYAxisActionKey()));
		mapList.add(new KeyMapping("YZ-Plane", ActionToolSettings.getInstance().getYZAxisActionKey()));
		
		mapList.add(new KeyMapping("Snap", ActionToolSettings.getInstance().getSnapKey()));
		mapList.add(new KeyMapping("Axis-Lock", ActionToolSettings.getInstance().getSingleSnapKey()));
		
		mapList.add(new KeyMapping("Statics Selectable", ActionToolSettings.getInstance().getStaticsKey()));
		
		mapList.add(new KeyMapping("Show Layers", GeneralSettings.getInstance().getLayerKey()));
		
		mapList.add(new KeyMapping("Select Grouped Objects" , ActionToolSettings.getInstance().getGroupSelectionKey()));

		mapList.add(new KeyMapping("Toggle Wire Mode", ActionToolSettings.getInstance().getWireModeKey()));
		
		mapList.add(new KeyMapping("Select Multiple Objects", ActionToolSettings.getInstance().getMultipleSelectKey()));
		
		mapList.add(new KeyMapping("Show Object Properties", ActionToolSettings.getInstance().getPropertiesKey()));
		
		mapList.add(new KeyMapping("Copy", ActionToolSettings.getInstance().getCopyKey()));
		
		
		
		
		mapList.add(new KeyMapping("Delete", ActionToolSettings.getInstance().getSecondaryDeleteKey()));
		
		mapList.add(new KeyMapping("Focus", ActionToolSettings.getInstance().getFocusKey()));
		
		mapList.add(new KeyMapping("Zoom",GeneralSettings.getInstance().getCameraZoomKey()));
		
		
		mapList.add(new KeyMapping("Next Camera", GeneralSettings.getInstance().getCameraForwardKey()));
		
		mapList.add(new KeyMapping("Previous Camera",GeneralSettings.getInstance().getCameraBackwardKey()));
		
		mapList.add(new KeyMapping("Default Camera",GeneralSettings.getInstance().getCameraDefaultKey()));
		
		
		
		mapList.add(new KeyMapping("Menu", GeneralSettings.getInstance().getMenuKey()));
		
		mapList.add(new KeyMapping("Screen Shot", GeneralSettings.getInstance().getPictureKey()));
		
		
	
		return mapList;
	}

	private class KeyMapping
	{
		private KeySetting keySetting;
		private String description;
		
		
		
		public KeyMapping(String description, KeySetting keySetting) {
			super();
			this.description = description;
			this.keySetting = keySetting;
		}

		public void setKey(int key)
		{
			keySetting.setKeyCode(key);
		}
		
		public String getDescription() {
		
			return description;
		}

		public String getCurrentKeyDescription()
		{
			int keyCode = keySetting.getKeyCode();
			if(changeMap.get(this) != null)
			{
				keyCode = changeMap.get(this);
				
			}
			if (keyCode == KeySetting.NO_KEY)
				return "(Disabled)";
			else 
				return KeyMappingConversion.getSimpleKeyDescription(keyCode);
				
		}
		
		public void disableKey()
		{
			keySetting.setKeyCode(KeySetting.NO_KEY);
			
		}
	}
	
}
