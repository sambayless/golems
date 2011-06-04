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
package com.golemgame.properties.fengGUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.fenggui.CheckBox;
import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.ListItem;
import org.fenggui.ScrollContainer;
import org.fenggui.TextEditor;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.ITextChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.event.TextChangedEvent;
import org.fenggui.event.mouse.MouseAdapter;
import org.fenggui.event.mouse.MousePressedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowExLayoutData;
import org.fenggui.layout.RowLayout;
import org.fenggui.util.Alignment;
import org.fenggui.util.Color;

import com.golemgame.local.StringConstants;
import com.golemgame.menu.color.ColorPatch;
import com.golemgame.menu.color.ColorWindow;
import com.golemgame.menu.color.ColorDialog.ColorDialogListener;
import com.golemgame.model.texture.TextureTypeKey;
import com.golemgame.model.texture.TextureWrapper.TextureFormat;
import com.golemgame.model.texture.fenggui.FengGUITexture;
import com.golemgame.model.texture.fenggui.FengGUITextureServer;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.AppearanceInterpreter;
import com.golemgame.mvc.golems.AppearanceInterpreter.ImageType;
import com.golemgame.states.GUILayer;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;
import com.jme.util.GameTaskQueueManager;

public class TextureTab extends PropertyTabAdapter {

	private AppearanceInterpreter interpreter;
	
	private List<PropertyStore> originals;

	private ComboBox<TextureItem> textureList;
	private AppearanceDisplayer displayer;

	private Map< ImageType,TextureItem> commonTextures ;

	private ColorPatch highlightColorPatch;
	private ColorPatch backgroundColorPatch;
	private ColorPatch textColorPatch;
	private CheckBox useText;
	private TextEditor text;

	public void close(boolean cancel) {
		int i = 0;
		for(PropertyStore store:super.getPropertyStoreAdjuster().getStores())
		{
			
			store.set(originals.get(i++));
		}//set them back here, before applying changes, for undo purposes.
	if(!cancel)
		{
			//super.standardClosingBehaviour(backgroundColorPatch, getTitle())
			super.standardClosingBehaviour(backgroundColorPatch, AppearanceInterpreter.BASE_COLOR);
			super.standardClosingBehaviour(highlightColorPatch, AppearanceInterpreter.HIGHLIGH_COLOR);
			super.standardClosingBehaviour(textColorPatch, AppearanceInterpreter.TEXT_COLOR);
			if(useText.isSelected())
			{
				super.standardClosingBehaviour(text, AppearanceInterpreter.TEXT,Format.String);
			}else
			{
				super.setValueAltered(AppearanceInterpreter.TEXT, true);
				interpreter.setText("");
			}
			
			/*if(super.isAltered(backgroundColorPatch))
				interpreter.setBaseColor(backgroundColorPatch.getColorRGBA());
			if(super.isAltered(highlightColorPatch))
				interpreter.setHighlightColor(highlightColorPatch.getColorRGBA());*/
			if(super.isAltered(textureList))
			{
				interpreter.setTextureImage(textureList.getSelectedItem().getValue().getTextureTypeKey().getImage());
				super.setValueAltered(AppearanceInterpreter.TEXTURE_IMAGE,true);
			}
			/*super.setValueAltered(AppearanceInterpreter.BASE_COLOR);
			super.setValueAltered(AppearanceInterpreter.HIGHLIGH_COLOR);
			super.setValueAltered(AppearanceInterpreter.TEXTURE_IMAGE);*/
			
			Action<?> apply = super.apply();
	
			UndoManager.getInstance().addAction(apply);
		}else
		{
			for(PropertyStore store:super.getPropertyStoreAdjuster().getStores())
			{//have to refresh
				store.refresh();
			}
		}
		
		
		
	}

	/*public void setStructure(Structural structure) {
		this.structure = structure;
		displayer.setStructuralAppearanceEffect(structure.getStructuralAppearanceEffect());
		structure.getAppearance().addEffect(structure.getStructuralAppearanceEffect(), true);
		structure.getStructuralAppearanceEffect().requestUpdate();
		
		ColorRGBA highlightRGBA =	structure.getStructuralAppearanceEffect().getHighlight();
			
		Color highlight= new Color(highlightRGBA.r,highlightRGBA.g,highlightRGBA.b,highlightRGBA.a);
		highlightColorPatch.setColor(highlight);
		
		
		ColorRGBA baseColorRGBA =	structure.getStructuralAppearanceEffect().getBaseColor();
			
		Color baseColor= new Color(baseColorRGBA.r,baseColorRGBA.g,baseColorRGBA.b,baseColorRGBA.a);
		backgroundColorPatch.setColor(baseColor);
		
		TextureItem item = commonTextures.get(structure.getStructuralAppearanceEffect().getHighlightTexture().getTextureTypeKey().getImage());

		textureList.setSelected(textureList.getList().getItem(textureList.getList().getItem(item)));
		
	}*/


	public TextureTab() {
		super(StringConstants.get("PROPERTIES.APPEARANCE","Appearance"));
		

		



	
			displayer.setTexture(commonTextures.get(ImageType.VOID).getFengGUItexture());

	}


	private void buildTextures()
	{
		commonTextures = new HashMap<ImageType, TextureItem>();
		for(ImageType imageClass:ImageType.values())
		{
			if (imageClass == ImageType.DEFAULT || (imageClass == ImageType.CAMO4) || imageClass == ImageType.TEXT)
				continue; //|| imageClass == ImageType.VOID we want to include void
						
			TextureTypeKey fengGUIkey =  new TextureTypeKey(imageClass,512,512, TextureFormat.RGB,false);
			
			FengGUITexture fengGUItexture =(FengGUITexture) FengGUITextureServer.getFengGUIInstance().getTexture(fengGUIkey,false);
			TextureTypeKey spatialKey= new TextureTypeKey(imageClass,512,512,TextureFormat.RGB,true);
			commonTextures.put(imageClass, new TextureItem(spatialKey, fengGUItexture));
			
			
		}
				
				
		
	/*	for(ImageType imageClass:ImageType.values())
		{
			if (imageClass == ImageType.DEFAULT)
				continue;
			URL path = Images.getInstance().getURL(imageClass);
			if (path != null)
			{
				try{
					BufferedImage textureImage = null;
					String fileName = path.getPath();
					
					 int dot = fileName.lastIndexOf('.')+1;
				     String fileExt = (dot >= 0 && dot < fileName.length() )? fileName.substring(dot) : "";
						
					
				     if ("SVG".equalsIgnoreCase(fileExt))
				     {
				    	 try{
				    		 InputStream input = (new BufferedInputStream(path.openStream()));

				    		 SVGDiagram diagram= SVGImageLoader.loadSVGDiagram(input,Images.getInstance().getName(imageClass));

				    		 textureImage = SVGImageLoader.loadSVG(diagram, 512, 512, false);

				    	 }catch(IOException e)
				    	 {
				    		 StateManager.logError(e);
				    	 }

				     }else if ("SVGZ".equalsIgnoreCase(fileExt))
				     {
				    	 try{
				    		 InputStream input = new java.util.zip.GZIPInputStream(new BufferedInputStream(path.openStream()));

				    		 SVGDiagram diagram= SVGImageLoader.loadSVGDiagram(input,Images.getInstance().getName(imageClass));

				    		 textureImage = SVGImageLoader.loadSVG(diagram, 512, 512, false);

				    	 }catch(IOException e)
				    	 {
				    		 StateManager.logError(e);
				    	 }
				     }else				    	 
				     {

				    	 textureImage = ImageIO.read(path.openStream());

				     }

					if (textureImage!= null)
					{
						ITexture texture = Binding.getInstance().getTexture(textureImage);
						commonTextures.put(imageClass, new TextureItem(new TextureTypeKey(imageClass, TextureFormat.RGB), texture));
					}
				}catch(IOException e)
				{
					StateManager.logError(e);
				}
			}
		}*/
		//commonTextures.put(Images.ImageType.VOID, new TextureItem(new TextureTypeKey(Images.ImageType.VOID,512,512, TextureFormat.RGB,false), null));
		
	}
	

		
	public void open() {
		this.interpreter = new AppearanceInterpreter(getPrototype());
		getPrototype().set(super.getPropertyStoreAdjuster().getFirstStore());
		
		initializePrototype();
		
		originals = new ArrayList<PropertyStore>();
		for(PropertyStore store:super.getPropertyStoreAdjuster().getStores())
		{
			originals.add(store.deepCopy());
		}
		super.associateWithKey(text, AppearanceInterpreter.TEXT);
		super.associateWithKey(backgroundColorPatch, AppearanceInterpreter.BASE_COLOR);
		super.associateWithKey(highlightColorPatch, AppearanceInterpreter.HIGHLIGH_COLOR);
		super.associateWithKey(textColorPatch, AppearanceInterpreter.TEXT_COLOR);
		
		super.standardOpeningBehaviour(text, AppearanceInterpreter.TEXT,Format.String);
		super.standardOpeningBehaviour(backgroundColorPatch, AppearanceInterpreter.BASE_COLOR);
		super.standardOpeningBehaviour(highlightColorPatch, AppearanceInterpreter.HIGHLIGH_COLOR);
		super.standardOpeningBehaviour(textColorPatch, AppearanceInterpreter.TEXT_COLOR);
		
		
		useText.setSelected(interpreter.getText()!=null && interpreter.getText().length()>0);
		super.setUnaltered(useText);
	/*	this.backgroundColorPatch.setColor(interpreter.getBaseColor());
		
		this.highlightColorPatch.setColor(interpreter.getHighlightColor());*/
		this.displayer.getStructuralAppearanceEffect().getStore().set(interpreter.getStore());
		
		for(ListItem<TextureItem> item:textureList.getList().getItems())
		{
			if(item.getValue().getTextureTypeKey().getImage() == this.interpreter.getImage())
			{
				textureList.setSelected(item);
				displayer.setTexture(item.getValue().getFengGUItexture());
				break;
			}
		}
		super.associateWithKey(textureList, AppearanceInterpreter.TEXTURE_IMAGE);
		super.setUnaltered(textureList);
		this.displayer.getStructuralAppearanceEffect().refresh();
		
	//	super.standardOpeningBehaviour(textureList, AppearanceInterpreter.TEXTURE_IMAGE);
		
	}

	protected void buildGUI()
	{
		buildTextures();
		
		final Container tabFrame = FengGUI.createContainer(getTab());
		getTab().setLayoutManager(new BorderLayout());
		tabFrame.setLayoutData(BorderLayoutData.CENTER);
		tabFrame.setMinSize(100,600);
		tabFrame.setSizeToMinSize();
		tabFrame.setLayoutManager(new BorderLayout());
		
		Container textureContainer = FengGUI.createContainer(tabFrame);
		textureContainer.setLayoutManager(new BorderLayout());
		textureContainer.setLayoutData(BorderLayoutData.CENTER);
		
		displayer = new AppearanceDisplayer();
		textureContainer.addWidget(displayer);
       displayer.setLayoutData(BorderLayoutData.CENTER);
       
       
      textureList = FengGUI.createComboBox(textureContainer);
       textureList.setLayoutData(BorderLayoutData.NORTH);
       
       TextureItem[] textureItems = new TextureItem[this.commonTextures.size()];
       int i = 0;
       for (TextureItem textureItem:this.commonTextures.values())
       {
    	   textureItems[i++] = textureItem;
       }
       
       Arrays.sort(textureItems);
       for (TextureItem textureItem:textureItems)
       {
    	   
    	   	  textureList.addItem(new ListItem<TextureItem>(textureItem.toString(), textureItem));
              
       }
	

       textureList.addSelectionChangedListener(new ISelectionChangedListener()
       {

		
		public void selectionChanged(SelectionChangedEvent selectionChangedEvent) {
			
			TextureItem item=  textureList.getSelectedItem().getValue();
			
			setTexture(item);
		}
    	   
       });
       
       Container rightPanel = FengGUI.createContainer(tabFrame);
       rightPanel.setLayoutData(BorderLayoutData.EAST);
       rightPanel.setLayoutManager(new RowLayout(true));
      Spacer spacer = new Spacer(16,0);
       rightPanel.addWidget(spacer);
  
       Container colorContainer = FengGUI.createContainer(rightPanel);
     
       colorContainer.setLayoutManager(new RowExLayout(false));
      
       backgroundColorPatch = new ColorPatch();
       highlightColorPatch = new ColorPatch();
       Label p = FengGUI.createLabel(colorContainer, "Primary");
       p.setLayoutData(new RowExLayoutData(false,false));
       backgroundColorPatch.setLayoutData(new RowExLayoutData(true,true));
       colorContainer.addWidget(backgroundColorPatch);
       Label h = FengGUI.createLabel(colorContainer, "Highlight");
       h.setLayoutData(new RowExLayoutData(false,false));
       highlightColorPatch.setLayoutData(new RowExLayoutData(true,true));
       colorContainer.addWidget(highlightColorPatch);
       
       
       backgroundColorPatch.addMouseListener(new MouseAdapter()
       {
    	   
			
			public void mousePressed(MousePressedEvent event) {
				event.setUsed();
				final ColorWindow window = ColorWindow.getInstance();
				//have to call this at a later time, otherwise this window stays ontop
				
				
				
				if (window.getOwner() == backgroundColorPatch)
				{
					window.close();
					
				}else
				{
					
					GameTaskQueueManager.getManager().update(new Callable<Object>()
							{

								
								public Object call() throws Exception {
									window.setOwner(backgroundColorPatch);
									GUILayer layer = GUILayer.getLoadedInstance();
									Color color = backgroundColorPatch.getColor();
									window.setInitialColor(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha());
									window.display(layer.getDisplay());
									window.addColorListener(new ColorDialogListener()
									{

										
										public void cancel() {
										}

										
										public void colorChosen(int red, int green, int blue,int alpha) {
											if (window.getOwner() != backgroundColorPatch)
												return;
										setBaseColor(red,green,blue,alpha);									}
										
									});
									return null;
								}

							});
				}
			}
	    	   
       });
       
       highlightColorPatch.addMouseListener(new MouseAdapter()
       {
    	   
			
			public void mousePressed(MousePressedEvent event) {
				event.setUsed();
				final ColorWindow window = ColorWindow.getInstance();
				if (window.getOwner() == highlightColorPatch)
				{
					window.close();
					
				}else
				{
					GameTaskQueueManager.getManager().update(new Callable<Object>()
							{

								
								public Object call() throws Exception {
								window.setOwner(highlightColorPatch);
								GUILayer layer = GUILayer.getLoadedInstance();
								Color color = highlightColorPatch.getColor();
								window.setInitialColor(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha());
			
								window.display(layer.getDisplay());
									window.addColorListener(new ColorDialogListener()
									{
				
										
										public void cancel() {
										}
				
										
										public void colorChosen(int red, int green, int blue,int alpha) {
											if (window.getOwner() != highlightColorPatch)
												return;
											
											//use the alpha chanel to mix the highlight color with the background color directly...
											setHighlightColor(red,green,blue,alpha);
									
						
										}
										
									});
								return null;
								}
						});
				}
			}
	    	   
       });
       
       
      Container textContainer = FengGUI.createContainer(getTab());
       textContainer.setLayoutData(BorderLayoutData.SOUTH);
        useText = FengGUI.createCheckBox(textContainer,StringConstants.get("PROPERTIES.APPEARANCE.SHOW_TEXT","Show Text"));
       textContainer.setLayoutManager(new BorderLayout());
       useText.setLayoutData(BorderLayoutData.NORTH);
       final Container innerTextContainer = FengGUI.createContainer(textContainer);
       innerTextContainer.setLayoutData(BorderLayoutData.CENTER);
      final ScrollContainer textEditorContainer = FengGUI.createScrollContainer(innerTextContainer);
      textEditorContainer.setLayoutData(BorderLayoutData.CENTER);
      innerTextContainer.setLayoutManager(new BorderLayout());
      textColorPatch = new ColorPatch();
      Container textColorContainer = FengGUI.createContainer(innerTextContainer);
      textColorContainer.setLayoutData(BorderLayoutData.EAST);
      textColorContainer.setLayoutManager(new BorderLayout());
      Label textLabel = FengGUI.createLabel(textColorContainer,StringConstants.get("PROPERTIES.APPEARANCE.TEST","Text"));
      textLabel.setLayoutData(BorderLayoutData.NORTH);
      textLabel.getAppearance().setAlignment(Alignment.MIDDLE);
      textColorPatch.setLayoutData(BorderLayoutData.CENTER);
      textColorContainer.addWidget(textColorPatch);
      textColorContainer.setMinSize(58, 20);
      textColorContainer.setSizeToMinSize();
      textColorPatch.setShrinkable(false);
      textColorPatch.setExpandable(true);
      //textColorPatch.setSize(100,100);
      textColorPatch.setMinSize(58, 50);
      textColorPatch.setSizeToMinSize();
      innerTextContainer.setMinSize(10,100);
      innerTextContainer.setSizeToMinSize();
      innerTextContainer.setShrinkable(false);
      textEditorContainer.setShowScrollbars(true);
     
   //   textEditorContainer.setExpandable(true);
      text = FengGUI.createTextEditor(textEditorContainer);
    //  textEditorContainer.setInnerWidget(text);
      //text.setMinSize(new Dimension(10,100));
      text.getAppearance().getData().setMultiline(true);
      text.getAppearance().getData().setWordWarping(true);
      //text.setExpandable(true);
      text.getAppearance().setAlignment(Alignment.TOP_LEFT);
      text.setHeight(300);
	/*	Label l = FengGUI.createLabel(textEditorContainer, "This text is far too long to"
				+ " be displayed in one single line. But fortunately, Johannes "
				+ "implemented scrolling so that we all can enjoy the pleasure"
				+ " to read this text!   Ahhhh. I'm getting sick of these colors.");*/
       
      useText.addSelectionChangedListener(new ISelectionChangedListener()
      {
			public void selectionChanged(SelectionChangedEvent selectionChangedEvent) {
				innerTextContainer.setVisible(selectionChangedEvent.isSelected());
				innerTextContainer.updateMinSize();
				innerTextContainer.setMinSize(10,100);
				innerTextContainer.setSizeToMinSize();
			/*	textEditorContainer.setShowScrollbars(true);
				  text.setHeight(600);
			//	  text.layout();
				  textEditorContainer.layout();*/
				  
				tabFrame.updateMinSize();
				tabFrame.setSizeToMinSize();
				getTab().updateMinSize();
				getTab().setSizeToMinSize();
				getTab().layoutToRoot();
			}    	   
      });
      useText.setSelected(false);
      text.addTextChangedListener(new ITextChangedListener()
      {

		public void textChanged(TextChangedEvent textChangedEvent) {
			// TODO Auto-generated method stub
			text.updateMinSize();
			text.setSizeToMinSize();
			textEditorContainer.layout();
		}
    	  
      });
      
      
    
      
      textColorPatch.addMouseListener(new MouseAdapter()
      {
   	   
			
			public void mousePressed(MousePressedEvent event) {
				event.setUsed();
				final ColorWindow window = ColorWindow.getInstance();
				//have to call this at a later time, otherwise this window stays ontop
				
				
				
				if (window.getOwner() == textColorPatch)
				{
					window.close();
					
				}else
				{
					
					GameTaskQueueManager.getManager().update(new Callable<Object>()
							{

								
								public Object call() throws Exception {
									window.setOwner(textColorPatch);
									GUILayer layer = GUILayer.getLoadedInstance();
									Color color = textColorPatch.getColor();
									window.setInitialColor(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha());
									window.display(layer.getDisplay());
									window.addColorListener(new ColorDialogListener()
									{

										
										public void cancel() {
										}

										
										public void colorChosen(int red, int green, int blue,int alpha) {
											if (window.getOwner() != textColorPatch)
												return;
										setTextColor(red,green,blue,alpha);									}


									
										
									});
									return null;
								}

							});
				}
			}
	    	   
      });
      
      
      getTab().layout();
       
	
	}
	
	private void setTextColor(int red,
			int green, int blue, int alpha) {
		textColorPatch.setColor(new Color(red,green,blue,alpha));
		super.setWidgetAltered(textColorPatch);
	}
	
	private void setTexture(TextureItem item)
	{
		
		if(!displayer.getTexture().getTextureTypeKey().equals( item.getFengGUItexture().getTextureTypeKey()))
			displayer.setTexture(item.getFengGUItexture());
	
		TextureTypeKey typeKey = item.getTextureTypeKey();
		
	//	interpreter.setTextureImage(typeKey.getImage());
		
		applySettings();
/*		if(!interpreter.getStructuralAppearanceEffect().getHighlightTexture().getTextureTypeKey().equals(typeKey))
		{
			TextureWrapper wrapper = TextureServer.getInstance().getTexture(typeKey);
			wrapper.setApplyMode(ApplyMode.BLEND);

			wrapper.setTint(structure.getStructuralAppearanceEffect().getHighlight());
		
			structure.getStructuralAppearanceEffect().setHighlightTexture(wrapper);
			structure.getStructuralAppearanceEffect().requestUpdate();
		}
		
		for(PhysicalStructure s:physicalList)
		{
			if(!s.getStructuralAppearanceEffect().getHighlightTexture().getTextureTypeKey().equals(typeKey))
			{
				TextureWrapper wrapper = TextureServer.getInstance().getTexture(typeKey);
				wrapper.setApplyMode(ApplyMode.BLEND);

				wrapper.setTint(s.getStructuralAppearanceEffect().getHighlight());
			
				s.getStructuralAppearanceEffect().setHighlightTexture(wrapper);
				s.getStructuralAppearanceEffect().requestUpdate();
			}
		}
		*/
	}
	
	private void setBaseColor( int red, int green, int blue, int alpha)
	{
	
		float r = ((float)red)/255f;
		float g = ((float)green)/255f;
		float b = ((float)blue)/255f;
		float a = ((float)alpha)/255f;
		
		backgroundColorPatch.setColor(new Color(red,green,blue,alpha));
		
	//	interpreter.setBaseColor(new ColorRGBA(r,g,b,a));
	
		
		
		applySettings();
	}
	
	private void setHighlightColor( int red, int green, int blue, int alpha)
	{
		float r = ((float)red)/255f;
		float g = ((float)green)/255f;
		float b = ((float)blue)/255f;
		float a = ((float)alpha)/255f;
		
		highlightColorPatch.setColor(new Color(red,green,blue,alpha ));
	
		//interpreter.setHighlightColor(new ColorRGBA(r,g,b,a));
		
		applySettings();
	}

	private void applySettings() {
		boolean changed =false;
		//apparently the appearance effect instance is disconnected when the tab is opened, 
		//and then after its interpreter is modified, a new instance is created when the whole structre refreshes, 
		//and THAT is when the appearance is applied
		
		if(super.isAltered(backgroundColorPatch))
		{
			changed= true;
			interpreter.setBaseColor(backgroundColorPatch.getColorRGBA());
			super.setValueAltered(AppearanceInterpreter.BASE_COLOR,true);		
		}
		if(super.isAltered(highlightColorPatch))
		{
			changed= true;
			super.setValueAltered(AppearanceInterpreter.HIGHLIGH_COLOR,true);			
			interpreter.setHighlightColor(highlightColorPatch.getColorRGBA());
		}
		if(super.isAltered(textureList))
		{
			changed= true;
			super.setValueAltered(AppearanceInterpreter.TEXTURE_IMAGE,true);
			interpreter.setTextureImage(textureList.getSelectedItem().getValue().getTextureTypeKey().getImage());
		}
	
		if(changed)
		{
			this.displayer.getStructuralAppearanceEffect().getStore().set(interpreter.getStore().deepCopy());
			this.displayer.getStructuralAppearanceEffect().refresh();

			Action<?> apply = super.apply();
			
			UndoManager.getInstance().addAction(apply);
		}
	}

	

	
	
	
}
