package com.golemgame.properties.fengGUI.optionsMenu;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Date;

import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.RadioButton;
import org.fenggui.TextEditor;
import org.fenggui.ToggableGroup;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;

import com.golemgame.local.StringConstants;
import com.golemgame.menu.FileChooserDialog;
import com.golemgame.menu.FileWindow;
import com.golemgame.menu.FileChooserDialog.FileDialogListener;
import com.golemgame.menu.FileWindow.FileMode;
import com.golemgame.properties.fengGUI.TabAdapter;
import com.golemgame.states.GUILayer;
import com.golemgame.states.GeneralSettings;
import com.golemgame.states.StateManager;
import com.golemgame.states.record.RecordingListener;
import com.golemgame.states.record.RecordingManager;
import com.golemgame.states.record.RecordingSession;
import com.golemgame.states.record.RecordingSession.Compression;
import com.golemgame.states.record.RecordingSession.Format;

public class RecordingOptionsTab extends TabAdapter {
	
	private static final long MEGABYTE = 1048576L ;
	private static final long KILOBYTE = 1024L ;
	private static final long GIGABYTE = 1073741824L ;
	private ToggableGroup<RecordingSession.Format> format;
	private ToggableGroup<RecordingSession.Compression> compression;
	private TextEditor quality;
	private TextEditor fps;
	
	private Container recordingContainer;
	private Container controlContainer;
	private Label sessionName;
	private Label sessionLength;
	private Label sessionSize;
	
	private Container warningContainer;
	public RecordingOptionsTab() {
		super(StringConstants.get("MAIN_MENU.RECORDING" ,"Recording Options"));

	}

	@Override
	protected void buildGUI() {
	
		//Tab: Provide recording options ()
		//start recording from the main menu button will take you to this tab
		//then here, there are recording options + a start recording button.
		//pressing this first opens up a save menu.
		//this also turns on a recording toolbar. Closing that toolbar will stop recording and save the movie.
		getTab().setLayoutManager(new BorderLayout());
		recordingContainer = FengGUI.createContainer(getTab());
		recordingContainer.setLayoutData(BorderLayoutData.CENTER);
		recordingContainer.setLayoutManager(new BorderLayout());
		Container settingsContainer = FengGUI.createContainer(recordingContainer);
		settingsContainer.setLayoutData(BorderLayoutData.NORTH);
		
		settingsContainer.setLayoutManager(new RowLayout(false));
		Container formatRow = FengGUI.createContainer(settingsContainer);
		formatRow.setLayoutManager(new RowLayout());
		
		FengGUI.createLabel(formatRow, StringConstants.get("MAIN_MENU.RECORDING.FORMAT" ,"Video Format"));
		format = new ToggableGroup<Format>();
		for(Format f:Format.values())
		{
			RadioButton<Format> opt = FengGUI.createRadioButton(formatRow, format);
			opt.setValue(f);
			opt.setText(f.getName());
			if(f==Format.AVI)
				opt.setSelected(true);
		}
		
		Container compressionRow = FengGUI.createContainer();
		compressionRow.setLayoutManager(new RowLayout());
		
		FengGUI.createLabel(compressionRow, StringConstants.get("MAIN_MENU.RECORDING.COMPRESSION" ,"Video Compression"));
		compression = new ToggableGroup<Compression>();
		for(Compression f:Compression.values())
		{
			RadioButton<Compression> opt = FengGUI.createRadioButton(compressionRow, compression);
			opt.setValue(f);
			opt.setText(f.getName());
			if(f==Compression.JPG)
				opt.setSelected(true);
		}
		
		Container qualityRow = FengGUI.createContainer(settingsContainer);
		qualityRow.setLayoutManager(new RowLayout());
		FengGUI.createLabel(qualityRow,StringConstants.get("MAIN_MENU.RECORDING.QUALITY" , "Compression Quality (1% to 100%)"));
		quality = FengGUI.createTextEditor(qualityRow);
		
		Container fpsRow = FengGUI.createContainer(settingsContainer);
		fpsRow.setLayoutManager(new RowLayout());
		FengGUI.createLabel(fpsRow, StringConstants.get("MAIN_MENU.RECORDING.FPS" , "fps"));
		fps = FengGUI.createTextEditor(fpsRow);
		
		fps.setText("20");
		quality.setText("80");
		
		
		Container recordContainer = new Container();
		
		recordContainer.setLayoutData(BorderLayoutData.SOUTH);
		settingsContainer.addWidget(recordContainer);
		recordContainer.setLayoutManager(new RowLayout(false));
		Button recordingButton = FengGUI.createButton(recordContainer);
		recordingButton.setText( StringConstants.get("MAIN_MENU.RECORDING.BEGIN" ,"Begin Recording"));
		if(StateManager.SOUND_ENABLED)
			FengGUI.createLabel(recordContainer,"Note: Recordings do not currently include sound.");
		
		recordingButton.addButtonPressedListener(new IButtonPressedListener()
		{

			public void buttonPressed(ButtonPressedEvent e) {
				
				//get the user to select a file. If they dont cancel then start recording to that file with a new session
				GeneralSettings.getInstance().getMenu().initiateAction();
				
				//ensure the /video directory exists, and put us there.
				//File defFile = StateManager.gets
				 File file = new File(System.getProperty("user.home") + "/Machines/Videos/");
		    	// try{
		    	file.mkdirs();
		    		 
				GUILayer layer = GUILayer.getLoadedInstance();
				final FileWindow fileWindow = FileWindow.getInstance();
				if(file.exists())
					fileWindow.getFileChooser().setCurrentDirectory(file);
				fileWindow.getFileChooser().clearFileFilters();
				 fileWindow.getFileChooser().addFileFilter("All Files", "*");
				if(format.getSelectedValue()==Format.AVI)
				{
					FileFilter filter = fileWindow.getFileChooser().addFileFilter("AVI","*.avi");
					fileWindow.getFileChooser().setCurrentFilter(filter);
				}
				else				
				{
					FileFilter filter = fileWindow.getFileChooser().addFileFilter("Quicktime","*.mov");
					fileWindow.getFileChooser().setCurrentFilter(filter);
				}
				
				
				fileWindow.display(layer.getDisplay(),FileMode.SAVE);
				
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
						if(file ==null)
							return;
						if(StateManager.getRecordingManager().getRecordingLock().tryLock()){
							try{
								RecordingSession session = new RecordingSession();
							
								session.setDestination(file);
							
								session.setFormat(format.getSelectedValue());
								session.setCompression(compression.getSelectedValue());
								
								float q= 1f;
								try{
									 q = Float.valueOf(quality.getText())/100f;
								}catch(NumberFormatException e)
								{
									q =1f;
								}
								if(q<=0)
									q = 0.1f;
								if(q>1f)
									q = 1f;
								session.setQuality(q);
							
								
								float fpers= 1f;
								try{
									fpers = Float.valueOf(fps.getText());
								}catch(NumberFormatException e)
								{
									fpers =1f;
								}
								if(fpers<1)
									fpers = 1;
								
								session.setFps(fpers);
								
								
								try {
									StateManager.getRecordingManager().newSession(session);
								} catch (IOException e) {
									StateManager.logError(e);
								}
								
						}finally{
							StateManager.getRecordingManager().getRecordingLock().unlock();
						}
				}
			
						
					}
					
				});
				
				//the recording manager will close any existing recording
				
			}
			
		});
		
/*		warningContainer = new Container();
	
		warningContainer.setLayoutData(BorderLayoutData.NORTH);
		warningContainer.addWidget(FengGUI.createLabel("You must stop recording before adjusting\nsettings or starting a new recording."));
		getTab().addWidget(warningContainer);*/
		
		/*Label warningLabel = FengGUI.createLabel(getTab(),"Note: Recordings may take a lot of disk space.");
		warningLabel.setLayoutData(BorderLayoutData.SOUTH);*/
		
		controlContainer = new Container();
		getTab().addWidget(controlContainer);
		controlContainer.setLayoutData(BorderLayoutData.NORTH);
		controlContainer.setLayoutManager(new RowLayout(false));
		
		Container nameContainer = FengGUI.createContainer(controlContainer);
		nameContainer.setLayoutManager(new RowLayout());
	//	FengGUI.createLabel(controlContainer,"Recording... ");
		sessionName = FengGUI.createLabel(nameContainer, StringConstants.get("MAIN_MENU.RECORDING" ,"Recording"));
		sessionLength = FengGUI.createLabel(nameContainer, StringConstants.get("MAIN_MENU.RECORDING.TIME" ,"Time"));
		sessionSize = FengGUI.createLabel(nameContainer, StringConstants.get("MAIN_MENU.RECORDING.SIZE" ,"Size"));
		
		Container ctrlContainer = FengGUI.createContainer(controlContainer);
		ctrlContainer.setLayoutManager(new RowLayout());
		final Button pauseResume = FengGUI.createButton(ctrlContainer, StringConstants.get("MAIN_MENU.RECORDING.PAUSE" ,"Pause"));
		pauseResume.addButtonPressedListener(new IButtonPressedListener(){

			public void buttonPressed(ButtonPressedEvent e) {
				StateManager.getRecordingManager().getRecordingLock().lock();
				try{
					if(StateManager.getRecordingManager().isRecording())
					{
						StateManager.getRecordingManager().setPaused(!StateManager.getRecordingManager().isPaused());
						if(StateManager.getRecordingManager().isPaused())
						{
							pauseResume.setText( StringConstants.get("MAIN_MENU.RECORDING.RESUME" ,"Resume"));
						}else
							pauseResume.setText( StringConstants.get("MAIN_MENU.RECORDING.PAUSE" ,"Pause"));
					}
				}finally{
					StateManager.getRecordingManager().getRecordingLock().unlock();
				}
			}
		});
		Button stop = FengGUI.createButton(ctrlContainer, StringConstants.get("MAIN_MENU.RECORDING.STOP" ,"Stop"));
		stop.addButtonPressedListener(new IButtonPressedListener(){

			public void buttonPressed(ButtonPressedEvent e) {
				StateManager.getRecordingManager().getRecordingLock().lock();
				try{
					try {
						StateManager.getRecordingManager().closeSession();
						RecordingOptionsTab.this.open();
					} catch (IOException e1) {
						StateManager.logError(e1);
					}
				}finally{
					StateManager.getRecordingManager().getRecordingLock().unlock();
				}
			}
			
		});
		
		StateManager.getRecordingManager().registerListener(new RecordingListener(){

			public void frameDrawn(RecordingSession session,
					RecordingManager source) {
				long msDif = new Date().getTime() - session.getRecordingStart().getTime();
				long hours = msDif/(1000 * 3600);
				
				long minutes =  msDif/(1000 * 60) - hours*60;
				long seconds =  msDif/(1000)- hours*60*60 - minutes*60;
			//	System.out.println(hours + ":" + minutes + ":" + seconds);
				sessionLength.setText( StringConstants.get("MAIN_MENU.RECORDING.LENGTH" ,"Length" ) + " " + hours + ":" + minutes + ":" + seconds );
				
				long length = session.getDestination().length();
				if(length <KILOBYTE)
				{
					sessionSize.setText(length + " bytes");
				}if(length < MEGABYTE)
				{
					sessionSize.setText(length/KILOBYTE + " KB");
				}else if  (length<GIGABYTE)
				{
					sessionSize.setText(length/MEGABYTE + " MB");
				}else{
					
					long lengthGB = length/GIGABYTE;
					long lengthRemainer = length%GIGABYTE;
					double percent =Math.round(10.0* ( ((double) lengthRemainer) /( (double)GIGABYTE)))/10.0;
					sessionSize.setText(lengthGB + "." + percent + " GB");
				}
				
			}

			public void recordingState(boolean recording, boolean paused) {
				if(paused)
				{
					pauseResume.setText( StringConstants.get("MAIN_MENU.RECORDING.RESUME" ,"Resume"));
				}else
					pauseResume.setText( StringConstants.get("MAIN_MENU.RECORDING.PAUSE" ,"Pause"));
				RecordingOptionsTab.this.open();
			}
			
			
		});
		
	}

	@Override
	public void close(boolean cancel) {
		// TODO Auto-generated method stub
		super.close(cancel);
	}

	@Override
	public void open() {
		// TODO Auto-generated method stub
		//note: this tab must acquire the recording lock before editing any options, otherwise everything will be greyed out.
		super.open();
		
		
		RecordingManager r = StateManager.getRecordingManager();
		r.getRecordingLock().lock();
		try{
			if(!r.isRecording())
			{
				recordingContainer.setVisible(true);
				controlContainer.setVisible(false);
			}else{
				recordingContainer.setVisible(false);
				
				sessionName.setText("Recording: " + r.getCurrentSession().getDestination().getName());
				
			/*	updateTime();
				
				*/
				
				controlContainer.setVisible(true);
				controlContainer.layout();
				getTab().layout();
				
			}
			//getTab().layout();
		}finally{
			r.getRecordingLock().unlock();
		}	
	}


	
}
