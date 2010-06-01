package com.golemgame.properties.fengGUI.scale;

import org.fenggui.CheckBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.TextEditor;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;

import com.golemgame.mvc.golems.AxleInterpreter;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;

public class AxleScaleTab extends ScaleTab {

	private AxleInterpreter interpreter;
	private TextEditor scaleLeft;
	private TextEditor scaleRight;
	private TextEditor leftLength;
	private TextEditor rightLength;
	private CheckBox<?> isBearing;
	//private TextEditor bearingLength;
	//private TextEditor bearingRadius;
	
	public AxleScaleTab() {
		super("Axle Scale");
	}

	@Override
	public void close(boolean cancel) {
		super.close(cancel);
		if(!cancel)
		{
			//boolean changed = false;
			standardClosingBehaviour(scaleLeft, AxleInterpreter.LEFT_JOINT_RADIUS, Format.Float);
			standardClosingBehaviour(scaleRight, AxleInterpreter.RIGHT_JOINT_RADIUS, Format.Float);
			standardClosingBehaviour(leftLength, AxleInterpreter.LEFT_JOINT_LENGTH, Format.Float);
			standardClosingBehaviour(rightLength, AxleInterpreter.RIGHT_JOINT_LENGTH, Format.Float);
			standardClosingBehaviour(isBearing, AxleInterpreter.IS_BEARING);

				Action<?> apply = super.apply();
				
				UndoManager.getInstance().addAction(apply);
			
		}
	}
	
	@Override
	protected void buildGUI() {
		super.buildGUI();
		Container internal = FengGUI.createContainer();
		internal.setLayoutManager(new RowLayout(false));
		internal.setLayoutData(BorderLayoutData.NORTH);
		isBearing = FengGUI.createCheckBox(internal,"Ball Bearing");
		
		Container scaleContainer = FengGUI.createContainer();
		scaleContainer.setLayoutData(BorderLayoutData.NORTH);
		scaleContainer.setLayoutManager(new RowLayout(false));
		

		
		Container position = FengGUI.createContainer(scaleContainer);
		
		position.setLayoutManager(new RowLayout());
		FengGUI.createLabel(position,"Left Radius").setExpandable(false);		
		scaleLeft = FengGUI.createTextEditor(position);
		
		FengGUI.createLabel(position,"Right Radius").setExpandable(false);		
		scaleRight = FengGUI.createTextEditor(position);
		
		FengGUI.createLabel(position,"Left Length").setExpandable(false);		
		leftLength = FengGUI.createTextEditor(position);

		FengGUI.createLabel(position,"Right Length").setExpandable(false);		
		rightLength = FengGUI.createTextEditor(position);

		
		
		getTab().setLayoutManager(new BorderLayout());
		internal.addWidget(scaleContainer);
		getTab().addWidget(internal);
	}
	
	@Override
	public void open() {
		super.open();
		interpreter = new AxleInterpreter(super.getPrototype());
		interpreter.loadDefaults();
		initializePrototype();
		associateWithKey(scaleLeft, AxleInterpreter.LEFT_JOINT_RADIUS);
		associateWithKey(scaleRight, AxleInterpreter.RIGHT_JOINT_RADIUS);
		associateWithKey(leftLength, AxleInterpreter.LEFT_JOINT_LENGTH);
		associateWithKey(rightLength, AxleInterpreter.RIGHT_JOINT_LENGTH);
		standardOpeningBehaviour(scaleLeft, AxleInterpreter.LEFT_JOINT_RADIUS, Format.Float);
		standardOpeningBehaviour(scaleRight, AxleInterpreter.RIGHT_JOINT_RADIUS, Format.Float);
		standardOpeningBehaviour(leftLength, AxleInterpreter.LEFT_JOINT_LENGTH, Format.Float);
		standardOpeningBehaviour(rightLength, AxleInterpreter.RIGHT_JOINT_LENGTH, Format.Float);
		
		associateWithKey(isBearing, AxleInterpreter.IS_BEARING);
		standardOpeningBehaviour(isBearing, AxleInterpreter.IS_BEARING);
		
	}

}
