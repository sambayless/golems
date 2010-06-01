package com.golemgame.properties.fengGUI.scale;

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.TextEditor;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.golems.SphereInterpreter;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;

public class SphereScaleTab extends ScaleTab {

	private SphereInterpreter interpreter;
	private TextEditor radius;

	public SphereScaleTab() {
		super("Sphere Scale");
	}

	@Override
	public void close(boolean cancel) {
		super.close(cancel);
		if(!cancel)
		{
			//boolean changed = false;
			standardClosingBehaviourMultiplex(radius, SphereInterpreter.RADII,0, DataType.Type.VECTOR3, Format.Float);

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
		

		
		Container position = FengGUI.createContainer(internal);
		
		position.setLayoutManager(new RowLayout());
		FengGUI.createLabel(position,"Radius").setExpandable(false);		
		radius = FengGUI.createTextEditor(position);

		
		getTab().setLayoutManager(new BorderLayout());
		getTab().addWidget(internal);
	}
	
	@Override
	public void open() {
		super.open();
		interpreter = new SphereInterpreter(super.getPrototype());
		interpreter.loadDefaults();
		initializePrototype();
		super.associateWithKey(radius, SphereInterpreter.RADII);
	
		super.standardOpeningBehaviour(radius, SphereInterpreter.RADII, Format.Float,0,DataType.Type.VECTOR3);
	
	}

}
