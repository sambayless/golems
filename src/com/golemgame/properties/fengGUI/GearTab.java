package com.golemgame.properties.fengGUI;

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.TextEditor;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.GridLayout;

import com.golemgame.local.StringConstants;
import com.golemgame.mvc.golems.GearInterpreter;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;
import com.jme.math.FastMath;

public class GearTab extends PropertyTabAdapter {

	//private Collection<PropertyStore> properties = new ArrayList<PropertyStore>();

	private GearInterpreter interpreter;
	
	//private TextEditor toothNumber;
	private TextEditor toothHeight;
	private TextEditor toothAngle;
	private TextEditor toothSpacing;
	
	public GearTab() {
		super(StringConstants.get("TOOLBAR.GEAR","Gear"));
		
	}

	@Override
	protected void buildGUI() {
		
		getTab().setLayoutManager(new BorderLayout());
		
		Container toothContainer = FengGUI.createContainer(getTab());
		toothContainer.setLayoutData(BorderLayoutData.NORTH);
		toothContainer.setLayoutManager(new GridLayout(3,2));
	/*	FengGUI.createLabel(toothContainer, "Number of Teeth");
		toothNumber = FengGUI.createTextEditor();*/
		
		FengGUI.createLabel(toothContainer, StringConstants.get("PROPERTIES.GEAR.TOOTH_WIDTH","Tooth Width"));
		toothSpacing = FengGUI.createTextEditor(toothContainer);
		
		FengGUI.createLabel(toothContainer, StringConstants.get("PROPERTIES.GEAR.TOOTH_HEIGHT","Tooth Height"));
		toothHeight = FengGUI.createTextEditor(toothContainer);
		
		FengGUI.createLabel(toothContainer, StringConstants.get("PROPERTIES.GEAR.TOOTH_ANGLE","Tooth Angle"));
		toothAngle = FengGUI.createTextEditor(toothContainer);
		
		
	}

	
	


	@Override
	public void close(boolean cancel) {
		if(!cancel)
		{

			standardClosingBehaviour(toothAngle, GearInterpreter.TOOTH_ANGLE,Format.Angle);
			
			if(super.isAltered(toothAngle))
			{
				if(interpreter.getToothAngle()<0)
					interpreter.setToothAngle(0f);
				else if (interpreter.getToothAngle()>FastMath.HALF_PI)
					interpreter.setToothAngle(FastMath.HALF_PI);
				
			}
			standardClosingBehaviour(toothHeight, GearInterpreter.TOOTH_HEIGHT);
			standardClosingBehaviour(toothSpacing, GearInterpreter.TOOTH_WIDTH);
			
			
			Action<?> apply = super.apply();
			
			UndoManager.getInstance().addAction(apply);
			
	
		}
	}



	@Override
	public void open() {
		this.interpreter = new GearInterpreter(getPrototype());
		this.interpreter.loadDefaults();
		initializePrototype();
		
		super.associateWithKey(toothAngle, GearInterpreter.TOOTH_ANGLE);
		super.associateWithKey(toothSpacing, GearInterpreter.TOOTH_WIDTH);
		super.associateWithKey(toothHeight, GearInterpreter.TOOTH_HEIGHT);
		
		super.standardOpeningBehaviour(toothAngle, GearInterpreter.TOOTH_ANGLE,Format.Angle);
		super.standardOpeningBehaviour(toothSpacing, GearInterpreter.TOOTH_WIDTH);
		super.standardOpeningBehaviour(toothHeight, GearInterpreter.TOOTH_HEIGHT);
	
	
	}
}
