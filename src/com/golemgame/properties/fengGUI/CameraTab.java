package com.golemgame.properties.fengGUI;

import org.fenggui.CheckBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.TextEditor;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;

import com.golemgame.local.StringConstants;
import com.golemgame.mvc.golems.CameraInterpreter;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;

public class CameraTab extends PropertyTabAdapter {

	private CameraInterpreter interpreter;
	
	private CheckBox<?> allLocked;
	private TextEditor triggerValue;
	
	public CameraTab() {
		super(StringConstants.get("TOOLBAR.CAMERA","Camera"));
		
	}

	@Override
	protected void buildGUI() {
		
		getTab().setLayoutManager(new BorderLayout());

		Container cameraContainer = FengGUI.createContainer(getTab());
		cameraContainer.setLayoutData(BorderLayoutData.NORTH);
		cameraContainer.setLayoutManager(new RowLayout(false));

		allLocked = FengGUI.createCheckBox(cameraContainer,StringConstants.get("PROPERTIES.CAMERA.LOCK","Lock Camera Orientation"));

		Container viewContainer = new Container();
		cameraContainer.addWidget(viewContainer);
		viewContainer.setLayoutManager(new RowLayout(true));
		FengGUI.createLabel(viewContainer,StringConstants.get("PROPERTIES.CAMERA.TRIGGER","Viewpoint Trigger Threshold")).setExpandable(false);
		triggerValue = FengGUI.createTextEditor(viewContainer);
	}

	@Override
	public void close(boolean cancel) {
		if(!cancel)
		{
	
			standardClosingBehaviour(allLocked, CameraInterpreter.LOCK_ALL);
			standardClosingBehaviour(triggerValue,CameraInterpreter.VIEW_THRESHOLD);

			
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
		associateWithKey(triggerValue, CameraInterpreter.VIEW_THRESHOLD);
		standardOpeningBehaviour(allLocked, CameraInterpreter.LOCK_ALL);
		standardOpeningBehaviour(triggerValue, CameraInterpreter.VIEW_THRESHOLD);
		
	}
}
