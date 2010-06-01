package com.golemgame.menu;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.fenggui.Container;
import org.fenggui.Display;
import org.fenggui.FengGUI;
import org.fenggui.composite.Window;
import org.fenggui.event.IWindowClosedListener;
import org.fenggui.event.WindowClosedEvent;
import org.fenggui.layout.BorderLayoutData;

import com.golemgame.local.StringConstants;
import com.golemgame.menu.FileChooserDialog.FileDialogListener;
import com.golemgame.properties.fengGUI.CloseListener;
import com.golemgame.properties.fengGUI.IFengGUIDisplayable;
import com.golemgame.states.GeneralSettings;
import com.golemgame.states.StateManager;
import com.golemgame.util.loading.ConcurrentLoadableAdapter;
import com.jme.system.DisplaySystem;


public class FileWindow extends ConcurrentLoadableAdapter implements IFengGUIDisplayable {
	private static FileWindow instance;
	public static final String EXTENSION = "mchn";
	private Window window;
	private FileChooserDialog dialog;
	
	public static FileWindow createLoadableFileWindow()
	{
		return new FileWindow(true);
	}
	
	
	public void load() throws Exception {
		super.loadingLock.lock();
		try{
			build();
			
			super.load();
		}finally
		{
			super.loadingLock.unlock();
		}
		
	}

	private FileWindow (boolean loading)
	{
		super();
	}
	private Thread glThread = null;
	private void build()
	{
		
		try{
			StateManager.getGame().executeInGL(new Callable<Object>()
					{

						
						public Object call() throws Exception {
							final int width = DisplaySystem.getDisplaySystem().getWidth();
							final int height = DisplaySystem.getDisplaySystem().getHeight();
							
							window = FengGUI.createWindow(true, false, false, true);
					        window.setTitle(StringConstants.get("FILEMENU.MENU" , "File Dialog"));
					        window.setSize(width/2, height/2);
					        glThread = Thread.currentThread();
					        dialog = FileChooserDialog.createLoadableFileChooserDialog(null, glThread,true);
					        window.getContentContainer().addWidget(dialog);
					        return null;
						}
					});
			
	
	        dialog.load();
	        dialog.setLayoutData(BorderLayoutData.CENTER);
	        window.getContentContainer().addWidget(dialog);
			
					       FileFilter filter =  dialog.addFileFilter(StringConstants.get("FILEMENU.MACHINE_FILES" , "File Dialog"), "*."+EXTENSION);
					       dialog.setCurrentFilter(filter);
					    
					    	 File file = new File(System.getProperty("user.home") + "/Machines/");
					    		File machinesRoot = file;
					    		
								//	File f = new File(FileWindow.getInstance().getFileChooser().getCurrentDirectory().toString() + "/" + GeneralSettings.getInstance().getLastFile().getValue());
							
					    	 	 try{
						    		 file.mkdirs();
						    		dialog.addToRoots(file);
						    	
						    	
						    	 }catch(Exception e)
						    	 {
						    		 e.printStackTrace();
						    	 }
						    	 File f = new File(GeneralSettings.getInstance().getLastFile().getValue());
						    	 	if(f.exists())
									{
						    	 	
						    	 		dialog.setSelectedFile(f);
									}else{
										dialog.setCurrentDirectory(file);
									}

					     //   d.setDirectory(new File("C:\\"));
					        window.getContentContainer().layout();
					        dialog.layout();
					     
					        
					        dialog.addListener(new FileDialogListener()
							{
								
								public void cancel() {
									dialog.removeListener(this);
					
										close();		
								}

								
								public void fileSelected(final File file) {
									dialog.removeListener(this);
			
										close();
								}
								
							});
					        
					        window.addWindowClosedListener(new IWindowClosedListener()
					        {

								
								public void windowClosed(WindowClosedEvent windowClosedEvent) {
									dialog.cancel();
									
								}
					        	
					        });
				
						
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
	}
	
	public FileWindow() {
		super();
		build();

        
	}
	
	public enum FileMode
	{
		LOAD(StringConstants.get("FILEMENU.LOAD" , "Load File")),IMPORT(StringConstants.get("FILEMENU.IMPORT" , "Import File")),SAVE(StringConstants.get("FILEMENU.SAVE" , "Save File"));
		private final String description;

		private FileMode(String description) {
			this.description = description;
		}

		@Override
		public String toString() {
			return description;
		}
		
	}


	public void display(Display display) {
		this.display(display,FileMode.LOAD, Container.NORMAL_PRIORITY);
		StateManager.logError(new Throwable("FileWindow should be accessed with a declared mode."));
	}


	public void display(Display display, int priority) {
		this.display(display,FileMode.LOAD, priority);
		StateManager.logError(new Throwable("FileWindow should be accessed with a declared mode."));
	}
	
	public void display( Display display, FileMode mode) {
		this.display(display,mode, Container.NORMAL_PRIORITY);
	}
	


	
	public void display(final Display display,final FileMode mode, final int priority) {
		try{
			StateManager.getGame().executeInGL(new Callable<Object>()
					{

						
						public Object call() throws Exception {					
							window.setTitle(mode.toString());
						//	dialog.refreshFiles();
							display.addWidget(window,priority);
							display.bringToFront(window);
							display.setFocusedWidget(window);
							display.layout();
							return null;
						}
				
					});
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		
	}

	
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		
	}


	
	public synchronized static FileWindow getInstance() {
		if (instance == null)
		{
			instance = createLoadableFileWindow();
		}

		return instance;
	}
	
	public FileChooserDialog getFileChooser()
	{
		return this.dialog;
	}
	
	public void close() {
		try{
			StateManager.getGame().executeInGL(new Callable<Object>()
					{

						
						public Object call() throws Exception {					
							
							if (window.getParent() != null)
								window.close();
							return null;
						}
				
					});
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			
			if (closeListeners != null && ! closeListeners.isEmpty())
			{
				CloseListener[] listenerArray = closeListeners.toArray(new CloseListener[closeListeners.size()]);
				closeListeners.clear();
				for (CloseListener listener:listenerArray)
					listener.close(this);
			}
			this.owner = null;
	}

	private ArrayList<CloseListener> closeListeners = null;
	
	public void addCloseListener(CloseListener listener) {
		if (closeListeners == null)
		{
			closeListeners = new ArrayList<CloseListener>();
		}
		closeListeners.add(listener);
		
	}


	
	public void removeCloseListener(CloseListener listener) {
		if (closeListeners != null)
		{
				closeListeners.remove(listener);
		}
	}

	private Object owner = null;

	
	public Object getOwner() {
		return this.owner;
	}


	
	public Object setOwner(Object owner) {
		Object old = this.owner;
		this.owner = owner;
		return old;
	}


	
	
	
}
