package com.golemgame.properties.fengGUI.scale;

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.TextEditor;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;

import com.golemgame.mvc.DataType;
import com.golemgame.mvc.golems.BoxInterpreter;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;

public class BoxScaleTab extends ScaleTab {

	private BoxInterpreter interpreter;
	private TextEditor scaleX;
	private TextEditor scaleY;
	private TextEditor scaleZ;
	
	public BoxScaleTab() {
		super("Box Scale");
	}

	@Override
	public void close(boolean cancel) {
		super.close(cancel);
		if(!cancel)
		{
			//boolean changed = false;
			standardClosingBehaviourMultiplex(scaleX, BoxInterpreter.BOX_EXTENT,0, DataType.Type.VECTOR3, Format.Float2x);
			standardClosingBehaviourMultiplex(scaleY, BoxInterpreter.BOX_EXTENT,1, DataType.Type.VECTOR3, Format.Float2x);
			standardClosingBehaviourMultiplex(scaleZ, BoxInterpreter.BOX_EXTENT,2, DataType.Type.VECTOR3, Format.Float2x);

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
		FengGUI.createLabel(positionBig,"Size: ").setExpandable(false);
		
		Container position = FengGUI.createContainer(positionBig);
		
		position.setLayoutManager(new RowLayout());
		FengGUI.createLabel(position,"X").setExpandable(false);		
		scaleX = FengGUI.createTextEditor(position);
		FengGUI.createLabel(position,"Y").setExpandable(false);
		scaleY = FengGUI.createTextEditor(position);
		FengGUI.createLabel(position,"Z").setExpandable(false);
		scaleZ = FengGUI.createTextEditor(position);
		
		getTab().setLayoutManager(new BorderLayout());
		getTab().addWidget(internal);
	}
	
	@Override
	public void open() {
		super.open();
		interpreter = new BoxInterpreter(super.getPrototype());
		interpreter.loadDefaults();
		initializePrototype();
		super.associateWithKey(scaleX, BoxInterpreter.BOX_EXTENT);
		super.associateWithKey(scaleY, BoxInterpreter.BOX_EXTENT);
		super.associateWithKey(scaleZ, BoxInterpreter.BOX_EXTENT);
		
		super.standardOpeningBehaviour(scaleX, BoxInterpreter.BOX_EXTENT, Format.Float2x,0,DataType.Type.VECTOR3);
		super.standardOpeningBehaviour(scaleY, BoxInterpreter.BOX_EXTENT, Format.Float2x,1,DataType.Type.VECTOR3);
		super.standardOpeningBehaviour(scaleZ, BoxInterpreter.BOX_EXTENT, Format.Float2x,2,DataType.Type.VECTOR3);

	}

}
