package com.golemgame.properties.fengGUI.optionsMenu;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.concurrent.Callable;

import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.CenteringLayout;
import org.fenggui.layout.RowLayout;

import com.golemgame.local.StringConstants;
import com.golemgame.menu.FileChooserDialog;
import com.golemgame.menu.FileWindow;
import com.golemgame.menu.FileChooserDialog.FileDialogListener;
import com.golemgame.menu.FileWindow.FileMode;
import com.golemgame.mvc.golems.Golems;
import com.golemgame.properties.fengGUI.ConfirmationBox;
import com.golemgame.properties.fengGUI.ImageTabAdapter;
import com.golemgame.properties.fengGUI.mainmenu.MainMenu;
import com.golemgame.states.GUILayer;
import com.golemgame.states.GeneralSettings;
import com.golemgame.states.SaveManager;
import com.golemgame.states.StateManager;
import com.golemgame.tool.action.UndoManager;
import com.golemgame.util.loading.Loadable;
import com.golemgame.util.loading.LoadableSequence;
import com.golemgame.util.loading.Loader;
import com.jme.image.Texture;
import com.jme.util.GameTaskQueueManager;


public class MainTab extends ImageTabAdapter {
	
	private Texture icon;
	private final MainMenu menu;
	
	public MainTab(MainMenu menu) {
		super(StringConstants.get("MAIN_MENU.MAIN","Main"));
		this.menu = menu;
		 icon =super.loadTexture("buttons/menu/Folder.png");
	}
	@Override
	public Texture getIcon() {
		return icon;
	}
	protected void buildGUI() {

		Container container = FengGUI.createContainer(getTab());
		getTab().setLayoutManager(new BorderLayout());
		
		Container labelContainer = FengGUI.createContainer(getTab());
	
		labelContainer.setLayoutData(BorderLayoutData.SOUTH);
		
		
		Label versionLabel = FengGUI.createLabel(labelContainer, "Version " + Golems.getVersion());
		versionLabel.setLayoutData(BorderLayoutData.EAST);
		labelContainer.setLayoutManager(new BorderLayout());//do this after adding the label.. otherwise the layout comes out wrong.
	//	labelContainer.layout();
		
		container.setLayoutManager(new CenteringLayout());
		container.setLayoutData(BorderLayoutData.CENTER);
		Container rowContainer = FengGUI.createContainer(container);
		rowContainer.setLayoutManager(new RowLayout(false));
		
		Button newButton = FengGUI.createButton(rowContainer,StringConstants.get("MAIN_MENU.FILE_MENU.NEW" ,"New Machine"));
		newButton.addButtonPressedListener(new IButtonPressedListener()
		{

			
			public void buttonPressed(ButtonPressedEvent e) {
				newMachine();
				
			}
			
		});
		Button saveButtonn = FengGUI.createButton(rowContainer,StringConstants.get("MAIN_MENU.FILE_MENU.SAVE" ,"Save Machine"));
		saveButtonn.addButtonPressedListener(new IButtonPressedListener()
		{

			
			public void buttonPressed(ButtonPressedEvent e) {
				saveMachine();
				
			}
			
		});
		Button loadButton = FengGUI.createButton(rowContainer,StringConstants.get("MAIN_MENU.FILE_MENU.LOAD" ,"Load Machine"));
		loadButton.addButtonPressedListener(new IButtonPressedListener()
		{

			
			public void buttonPressed(ButtonPressedEvent e) {
				loadMachine();
				
			}
			
		});
		
		//Merge the given machine into this one, 
		//keeping this one's settings.
		
		Button importButton = FengGUI.createButton(rowContainer,StringConstants.get("MAIN_MENU.FILE_MENU.IMPORT" ,"Import Machine"));
		importButton.addButtonPressedListener(new IButtonPressedListener()
		{

			
			public void buttonPressed(ButtonPressedEvent e) {
				importMachine();
				
			}

			
		});
		
		Button recordButton = FengGUI.createButton(rowContainer,StringConstants.get("MAIN_MENU.FILE_MENU.RECORD" ,"Record Video"));
		recordButton.addButtonPressedListener(new IButtonPressedListener()
		{

			public void buttonPressed(ButtonPressedEvent e) {
				menu.showRecordingTab();
			}
			
		});
		
		Button exitButton = FengGUI.createButton(rowContainer,StringConstants.get("MAIN_MENU.FILE_MENU.EXIT" ,"Exit Program"));
		exitButton.addButtonPressedListener(new IButtonPressedListener()
		{

			
			public void buttonPressed(ButtonPressedEvent e) {
				exit();
				
			}
			
		});
	}
	
	private void exit()
	{
		menu.showExitTab();
	}


	private void importMachine() {
	
		try{
			GameTaskQueueManager.getManager().update(new Callable<Object>()
					{
				
				public Object call() {	
					//Close the menu
					menu.close(false);
					//StateManager.getGeneralSettings().getMenu().initiateAction();
					
					//display a window
					GUILayer layer = GUILayer.getLoadedInstance();
					final FileWindow fileWindow = FileWindow.getInstance();
				//	fileWindow.getFileChooser().setCurrentDirectory(StateManager.getSaveDirectory());
					prepareFileWindow(fileWindow);
					fileWindow.display(layer.getDisplay(),FileMode.IMPORT);
			
					final FileChooserDialog chooser = fileWindow.getFileChooser();
					chooser.addListener(new FileDialogListener()
					{

						
						public void cancel() {
							chooser.removeListener(this);
							fileWindow.close();
							
						}

						
						public void fileSelected(final File file) {
							chooser.removeListener(this);
							fileWindow.close();
					
				
									
							SaveManager.getInstance().loadAndMergeMachine(file);
				
							
						}
						
					});
					return null;
				}

			
					});
		}catch(Exception e2)
		{

		}
		
		
	}
	private void prepareFileWindow(FileWindow fileWindow) {
		 File file = new File(System.getProperty("user.home") + "/Machines/");
	    	// try{
	    	file.mkdirs();
	    	fileWindow.getFileChooser().setCurrentDirectory(file);
	    	fileWindow.getFileChooser().clearFileFilters();
	    	FileFilter filter =  fileWindow.getFileChooser().addFileFilter("Machine Files", "*."+FileWindow.EXTENSION);
	    	 fileWindow.getFileChooser().addFileFilter("All Files", "*");
	    	fileWindow.getFileChooser().setCurrentFilter(filter);

	}
	private void loadMachine()
	{
		try{
		GameTaskQueueManager.getManager().update(new Callable<Object>()
				{
			
			public Object call() {	
				//Close the menu
				
				menu.close(true);
				//StateManager.getGeneralSettings().getMenu().initiateAction();
				
				
				StateManager.getThreadPool().execute(new Runnable(){

					public void run() {
					
						if(GeneralSettings.getInstance().getMachineChanged().isValue() && !userConfirm("Load Machine","This will erase your current machine (cannot be undone)."))
							return;
				
				
						
						//display a window
						GUILayer layer = GUILayer.getLoadedInstance();
						final FileWindow fileWindow = FileWindow.getInstance();
					//	fileWindow.getFileChooser().setCurrentDirectory(StateManager.getSaveDirectory());
						prepareFileWindow(fileWindow);
						fileWindow.display(layer.getDisplay(),FileMode.LOAD);
				
						final FileChooserDialog chooser = fileWindow.getFileChooser();
						chooser.addListener(new FileDialogListener()
						{
		
							
							public void cancel() {
								chooser.removeListener(this);
								fileWindow.close();
								
							}
		
							
							public void fileSelected( File file) {
								chooser.removeListener(this);
								fileWindow.close();
						
								if((!file.exists()) && file.getName().indexOf('.')<=0 )
								{
									//if the user forgot to add an extension, default to the standard extension
									file = new File(file.getAbsolutePath()+"."+FileWindow.EXTENSION);
								}else if( (!file.exists()) && file.getName().charAt(file.getName().length()-1)== '.')
								{
									file = new File(file.getAbsolutePath()+FileWindow.EXTENSION);
								}
								
								if(file.exists())
								{
									GeneralSettings.getInstance().getLastFile().setValue(file.toString());
								}
								SaveManager.getInstance().loadAndSetMachine(file);
					
								
							}
							
						});
						
					}	
					});
				
				
				return null;
				}
				});
		}catch(Exception e2)
		{
	
		}
	
	}
	
	private void newMachine()
	{
		menu.close(true);
		StateManager.getThreadPool().execute(new Runnable(){

			public void run() {
			
				if(GeneralSettings.getInstance().getMachineChanged().isValue()&& !userConfirm("New Machine","This will erase your current machine (cannot be undone)."))
					return;
				SaveManager.getInstance().clearMachine();
				SaveManager.getInstance().newMachine();
				
			}
		});
	}
	
	private void saveMachine()
	{
		LoadableSequence sequence = new LoadableSequence();
		
		menu.close(false);
	//	StateManager.getGeneralSettings().getMenu().initiateAction();
		
		Loader.getInstance().setDelayTime(250);
		Loader.getInstance().queueWaitForLoad(GUILayer.getInstance());
	
		
		Loader.getInstance().queueWaitForLoad( FileWindow.getInstance());
		
		Loader.getInstance().queueLoadable(new Loadable<Object>(false){
	
			
			public void load() throws Exception {
				
				final GUILayer layer = GUILayer.getLoadedInstance();
				final FileWindow fileWindow = FileWindow.getInstance();
				prepareFileWindow(fileWindow);
				StateManager.getGame().executeInGL(new Callable<Object>(){

					
					public Object call() throws Exception {
					
						fileWindow.display(layer.getDisplay(),FileMode.SAVE);
						
						final FileChooserDialog chooser = fileWindow.getFileChooser();
						chooser.addListener(new FileDialogListener()
						{

							
							public void cancel() {
								chooser.removeListener(this);
								fileWindow.close();
								
							}

							
							public void fileSelected(File file) {
								chooser.removeListener(this);
								fileWindow.close();

								
								if (file != null)
								{
									if(file.getName().indexOf('.')<=0 )
									{
										//if the user forgot to add an extension, default to the standard extension
										file = new File(file.getAbsolutePath()+"."+FileWindow.EXTENSION);
									}else if(  file.getName().charAt(file.getName().length()-1)== '.')
									{
										file = new File(file.getAbsolutePath()+FileWindow.EXTENSION);
									}

									final File fFile = file;
									//ask for confirmation
									StateManager.getThreadPool().execute(new Runnable(){

										public void run() {
											if(fFile.exists())
											{
												GeneralSettings.getInstance().getLastFile().setValue(fFile.toString());
										
												if(!userConfirm("Overwrite File?","File \"" + fFile.getName() + "\" already exists. Overwrite?"))
													return;
												
												final File fileOpen = fFile;
												
												GameTaskQueueManager.getManager().update(new Callable<Object>()
												{
													
													public Object call() {	
														try{
														
														
														SaveManager.getInstance().save(fileOpen, StateManager.getMachineSpace(), StateManager.getStructuralMachine());
														
														}catch(IOException e)
														{
															StateManager.logError(e);
														}
														return null;
													}
												});
											}else{

												final File fileOpen = fFile;
												
												GameTaskQueueManager.getManager().update(new Callable<Object>()
												{
													
													public Object call() {	
														try{
														
														
														SaveManager.getInstance().save(fileOpen, StateManager.getMachineSpace(), StateManager.getStructuralMachine());
														
														}catch(IOException e)
														{
															StateManager.logError(e);
														}
														return null;
													}
												});
											}
											
											
										
								
									}
									});
								}
							}


							
							
						});
						return null;
					}
					
				});
				

			}
			
		});
		
		Loader.getInstance().setDelayTime(100);
		Loader.getInstance().queueLoadable(sequence);
	}
	

	
	private boolean userConfirm(String title, String message) {
		return ConfirmationBox.showBlockingConfirmBox(title, message, GUILayer.getInstance().getDisplay());
	}
	
	

	





}
