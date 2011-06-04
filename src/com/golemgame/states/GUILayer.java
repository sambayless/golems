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
package com.golemgame.states;

import java.awt.Dimension;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

import org.fenggui.Display;
import org.fenggui.FengGUI;
import org.fenggui.binding.render.ImageFont;
import org.fenggui.binding.render.lwjgl.LWJGLBinding;
import org.fenggui.binding.render.text.DirectTextRenderer;
import org.fenggui.binding.render.text.advanced.ContentFactory;
import org.fenggui.theme.DefaultTheme;
import org.fenggui.theme.ITheme;
import org.fenggui.theme.XMLTheme;
import org.fenggui.theme.xml.XMLInputStream;
import org.fenggui.util.Alphabet;
import org.fenggui.util.fonttoolkit.FontFactory;

import com.golemgame.local.LocalAlphabet;
import com.golemgame.properties.fengGUI.MessageBox;
import com.golemgame.settings.SettingChangedEvent;
import com.golemgame.settings.SettingsListener;
import com.golemgame.util.FengGUISpatial;
import com.golemgame.util.input.FengGUILayerAdapter;
import com.golemgame.util.input.FengJMEListener;
import com.golemgame.util.input.InputLayer;
import com.golemgame.util.loading.ConcurrentLoadable;
import com.jme.system.DisplaySystem;
import com.jmex.game.state.GameState;

/**
 * This layer is where FengGUI elements are rendered
 * @author Sam
 *
 */
public class GUILayer extends GameState implements ConcurrentLoadable {

	private static GUILayer instance = null;
	public static final int dialogDepth = -32;
	/**
	 * Get this instance. NOTE: It may not be loaded yet; it is up to the user to wait until it is loaded.
	 * @return
	 */
	public static GUILayer getInstance() {
		return instance;
	}

	private GUILayer(boolean loading) {
		super();
	}

	protected ReentrantLock loadingLock = new ReentrantLock();;
	
	public boolean isLoaded() {
		if (loadingLock.tryLock())
		{
			loadingLock.unlock();
			return true;
		}else
			return false;
	}

	
	public void waitForLoad() {
		loadingLock.lock();
		loadingLock.unlock();		
	}
	
	
	
	public void load()  throws Exception {
		loadingLock.lock();
		
		try{
			buildUI();
			this.setActive(true);
		}finally
		{
			loadingLock.unlock();
		}
	}

	/**
	 * FengGUI display.
	 */
	private Display display = null;

	private FengGUISpatial guiSpatial;
	
	/**
	 * FengGUI InputListener.
	 */
	private FengJMEListener fengGUIListener;
	
	private FengGUILayerAdapter fengGUILayer;

	/**
	 * Initializes the FengGUI display and 
	 * creates the GUI elements.
	 */
	private void buildUI() throws Exception {


		StateManager.getGame().executeInGL(new Callable<Object>()
				{

			
			public Object call() throws Exception {
				display = new Display(new LWJGLBinding());
				display.getBinding().setUseClassLoader(true);
/*				
				return null;
			}
				});

		StateManager.getGame().executeInGL(new Callable<Object>()
				{

			
			public Object call() throws Exception {
				*/
				//this will initialize the local language if it isn't already
				Alphabet localizedAlphabet = new Alphabet(LocalAlphabet.getPrimitiveAlphabet());
				
				ImageFont font = FontFactory.renderStandardFont(new java.awt.Font("Sans", java.awt.Font.PLAIN, 14), true, localizedAlphabet);
				font.uploadToVideoMemory();
				ImageFont.setDefaultFont(font);
				DirectTextRenderer defaultRenderer = new DirectTextRenderer(ImageFont.getDefaultFont());
				ContentFactory.getDefaultFactory().addRenderer(org.fenggui.binding.render.text.advanced.IContentFactory.TEXTRENDERER_DEFAULT , defaultRenderer);
				
				return null;
			}
				});

		
		ITheme theme;
		try{

			/*File rootFile = new File("C:\\Program Files\\Golems\\dist\\lib\\"); //	new File(getClass().getClassLoader().getResource(".").getFile());
			String themePath = new File( rootFile, "/theme/QtCurve2").getPath(); 
			
			StateManager.getLogger().log(Level.INFO,themePath);*/
			theme = new XMLTheme() ;
			final XMLTheme xmlTheme = (XMLTheme) theme;
			final XMLInputStream  inputStream= xmlTheme.loadXML("com/golemgame/data/theme/","com/golemgame/data/theme/QtCurve2");
			StateManager.getGame().executeInGL(new Callable<Object>()
					{

				
				public Object call() throws Exception {
					xmlTheme.loadTheme(inputStream);
					
					return null;
				}
					});
		}catch(Exception e)
		{
			StateManager.logError(e);
			theme = new DefaultTheme();
		}
		
		FengGUI.setTheme(theme);
		//ImageFont defaultFont =FontFactory.renderStandardFont(new Font("S"), true, null)
			
	//	ContentFactory.getDefaultFactory().setActiveStyle(new TextStyle());
		
		/*StateManager.getGame().executeInGL(new Callable<Object>()
				{

			
			public Object call() throws Exception {
				ImageFont font = FontFactory.renderStandardFont(new java.awt.Font("Sans", java.awt.Font.PLAIN, 14), true, Alphabet
						.getDefaultAlphabet());
				
				ImageFont.setDefaultFont(font);
				DirectTextRenderer defaultRenderer = new DirectTextRenderer(ImageFont.getDefaultFont());
				ContentFactory.getDefaultFactory().addRenderer(org.fenggui.binding.render.text.advanced.IContentFactory.TEXTRENDERER_DEFAULT , defaultRenderer);
			
				return null;
			}
				});*/

		
		guiSpatial = new FengGUISpatial(display);

		fengGUIListener = new FengJMEListener(display);
		fengGUILayer = new FengGUILayerAdapter(fengGUIListener);
		InputLayer.get().addKeyListener(fengGUILayer, InputLayer.GUI_LAYER);
		InputLayer.get().addMouseListener(fengGUILayer,InputLayer.GUI_LAYER);

		guiSpatial.setZOrder(dialogDepth - 8,true);

		guiSpatial.updateGeometricState(0, true);
		
		
		GeneralSettings.getInstance().getResolution().addSettingsListener(new SettingsListener<Dimension>()
				{

					
					public void valueChanged(SettingChangedEvent<Dimension> e) {
						
						int width = (int) (e.getNewValue().getWidth());
						int height = (int) (e.getNewValue().getHeight());
					
						display.setSize(width, height);
					}
			
				},false);
	}


	
	
	/**
	 * Reset RenderStates before rendering FengGUI,
	 * especially the TextureState.
	 */
	
	public void render(float tpf) {
		// render the GUI	
		//only draw if its not empty
		//if (renderingGUI)
			DisplaySystem.getDisplaySystem().getRenderer().draw(guiSpatial);
	}

	/**
	 * Remvoe all widgets from the FengGUI display
	 */
	public void clearDisplay()
	{
		display.removeAllWidgets();
		
	}

	/**
	 * Get the FengGUI display
	 * @return
	 */
	public Display getDisplay() {
		return display;
	}


	
	public void update(float tpf) 
	{

			
				
	}

	public static void releaseFocus()
	{
		if(!getInstance().isLoaded())
			return;
		
		getInstance().getDisplay().setFocusedWidget(null);
	}
	
	public void cleanup() {
		display.removeAllWidgets();
		
	}

	public static synchronized GUILayer loadInstance() throws Exception {
		if (instance == null)
			instance = new GUILayer(true);
	
		instance.load();
		return instance;
	}
	
	/**
	 * Blocks until the singleton instance is loaded.
	 * @return The singleton GUILayer instance.
	 */
	public static GUILayer getLoadedInstance()
	{
		instance.waitForLoad();
		return instance;
	}

	public static void setInstance(GUILayer instance) {
		GUILayer.instance = instance;
	}

	public static void displayMessage(String title, String message) {
		if (GUILayer.getInstance().isLoaded())
		{
			GUILayer.getLoadedInstance();
			MessageBox.showMessageBox(title,message, GUILayer.getLoadedInstance().getDisplay());			
		}
		
	}

/*	public static boolean displayBlockingConfirm(String title, String message) {
		if (GUILayer.getInstance().isLoaded())
		{
			GUILayer.getLoadedInstance();
			MessageBox.showMessageBox(title,message, GUILayer.getLoadedInstance().getDisplay());			
		}
		
	}*/

	
}
