package com.golemgame.properties.fengGUI.scale;

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.TextEditor;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;

import com.golemgame.mvc.golems.CylinderInterpreter;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;

public class CylinderScaleTab extends ScaleTab {

	private CylinderInterpreter interpreter;
	private TextEditor height;

	private TextEditor radius;
	
	public CylinderScaleTab() {
		super("Cylinder Scale");
	}
	public CylinderScaleTab(String name) {
		super(name);
	}
	@Override
	public void close(boolean cancel) {
		super.close(cancel);
		if(!cancel)
		{
			//boolean changed = false;
			standardClosingBehaviour(height, CylinderInterpreter.CYL_HEIGHT, Format.Float);
			standardClosingBehaviour(radius, CylinderInterpreter.CYL_RADIUS, Format.Float);

			
			Action<?> apply = super.apply();				
			UndoManager.getInstance().addAction(apply);			
		}
	}
	
	@Override
	protected void buildGUI() {
		super.buildGUI();
	
		Container internal = FengGUI.createContainer();
		internal.setLayoutData(BorderLayoutData.NORTH);
		internal.setLayoutManager(new RowLayout(false));
		
		Container positionBig = FengGUI.createContainer(internal);
		positionBig.setLayoutManager(new RowLayout());
		FengGUI.createLabel(positionBig,"Cylinder: ").setExpandable(false);
		
		Container position = FengGUI.createContainer(positionBig);
		
		position.setLayoutManager(new RowLayout());
		FengGUI.createLabel(position,"Height").setExpandable(false);		
		height = FengGUI.createTextEditor(position);
		FengGUI.createLabel(position,"Radius").setExpandable(false);
		radius = FengGUI.createTextEditor(position);

		
		getTab().setLayoutManager(new BorderLayout());
		getTab().addWidget(internal);
	}
	
	@Override
	public void open() {
		super.open();
		interpreter = new CylinderInterpreter(super.getPrototype());
		interpreter.loadDefaults();
		initializePrototype();
		super.associateWithKey(radius, CylinderInterpreter.CYL_RADIUS);
		super.associateWithKey(height, CylinderInterpreter.CYL_HEIGHT);
		super.standardOpeningBehaviour(radius, CylinderInterpreter.CYL_RADIUS,Format.Float);
		super.standardOpeningBehaviour(height, CylinderInterpreter.CYL_HEIGHT);

	}

}
