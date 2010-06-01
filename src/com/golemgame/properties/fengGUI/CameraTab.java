package com.golemgame.properties.fengGUI;

import org.fenggui.CheckBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;

import com.golemgame.local.StringConstants;
import com.golemgame.mvc.golems.CameraInterpreter;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;

public class CameraTab extends PropertyTabAdapter {

	private CameraInterpreter interpreter;
	
	//private TextEditor toothNumber;
/*	private CheckBox rollLocked;
	private CheckBox pitchLocked;
	private CheckBox yawLocked;*/
	private CheckBox<?> allLocked;
	public CameraTab() {
		super(StringConstants.get("TOOLBAR.CAMERA","Camera"));
		
	}

	@Override
	protected void buildGUI() {
		
		getTab().setLayoutManager(new BorderLayout());

		Container toothContainer = FengGUI.createContainer(getTab());
		toothContainer.setLayoutData(BorderLayoutData.NORTH);
		toothContainer.setLayoutManager(new RowLayout());

		allLocked = FengGUI.createCheckBox(toothContainer,StringConstants.get("PROPERTIES.CAMERA.LOCK","Lock Camera Orientation"));
		
	/*	rollLocked = FengGUI.createCheckBox(toothContainer,"Lock Camera Roll");
		
		pitchLocked = FengGUI.createCheckBox(toothContainer,"Lock Camera Pitch");
	
		yawLocked = FengGUI.createCheckBox(toothContainer,"Lock Camera Yaw");*/
		
	}

	
	


	@Override
	public void close(boolean cancel) {
		if(!cancel)
		{
	
			standardClosingBehaviour(allLocked, CameraInterpreter.LOCK_ALL);
		/*	standardClosingBehaviour(rollLocked, CameraInterpreter.LOCK_ROLL);
			standardClosingBehaviour(pitchLocked, CameraInterpreter.LOCK_PITCH);
			standardClosingBehaviour(yawLocked, CameraInterpreter.LOCK_YAW);
			*/
			
			Action<?> apply = super.apply();
			
			UndoManager.getInstance().addAction(apply);
			
	
		}
	}



	@Override
	public void open() {
		this.interpreter = new CameraInterpreter(getPrototype());
		this.interpreter.loadDefaults();
		initializePrototype();
		associateWithKey(allLocked, CameraInterpreter.LOCK_ALL);
		standardOpeningBehaviour(allLocked, CameraInterpreter.LOCK_ALL);
	/*	associateWithKey(rollLocked, CameraInterpreter.LOCK_ROLL);
		associateWithKey(pitchLocked, CameraInterpreter.LOCK_PITCH);
		associateWithKey(yawLocked, CameraInterpreter.LOCK_YAW);
	
		standardOpeningBehaviour(rollLocked, CameraInterpreter.LOCK_ROLL);
		standardOpeningBehaviour(pitchLocked, CameraInterpreter.LOCK_PITCH);
		standardOpeningBehaviour(yawLocked, CameraInterpreter.LOCK_YAW);*/
	}
}
