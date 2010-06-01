package com.golemgame.properties.fengGUI;

import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.ListItem;
import org.fenggui.TextEditor;
import org.fenggui.decorator.border.TitledBorder;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;

import com.golemgame.local.StringConstants;
import com.golemgame.mvc.golems.ModifierInterpreter;
import com.golemgame.mvc.golems.BatteryInterpreter.ThresholdType;
import com.golemgame.mvc.golems.ModifierInterpreter.ModifierSwitchType;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;


public class ModifierOptionsTab extends PropertyTabAdapter {
	
	private ModifierInterpreter interpreter;
	
	private ComboBox<ModifierSwitchType> switchMode;

	private ComboBox<ThresholdType> thresholdType;

	private TextEditor threshold;

	public ModifierOptionsTab() {
		super(StringConstants.get("PROPERTIES.FUNCTION.OPTIONS_MODIFIER","Modifier Options"));
	}

	@Override
	protected void buildGUI() {
		getTab().setLayoutManager(new BorderLayout());
		
		Container innerContainer = FengGUI.createContainer(getTab());
		innerContainer.setLayoutData(BorderLayoutData.NORTH);
		innerContainer.setLayoutManager(new RowLayout(false));
		
		final Container thresholdOptions = FengGUI.createContainer(innerContainer);
		thresholdOptions.getAppearance().add(new TitledBorder(StringConstants.get("PROPERTIES.FUNCTION.SWITCH","Battery Switch")));
		
		
		thresholdOptions.setLayoutManager(new RowLayout(false));
		FengGUI.createLabel(thresholdOptions,StringConstants.get("PROPERTIES.FUNCTION.THRESHOLD_DESCRIPTION","Set the minimum signal needed to activate the switch."));
		
		final Container innerThresholdOptions = FengGUI.createContainer(thresholdOptions);
		innerThresholdOptions.setLayoutManager(new RowLayout());
		
	
		FengGUI.createLabel(innerThresholdOptions,StringConstants.get("PROPERTIES.FUNCTION.SWITCH_THRESHOLD","Switch Threshold"));// "Switch Threshold: ");
		
		threshold = FengGUI.createTextEditor(innerThresholdOptions, "0");
		
		FengGUI.createLabel(innerThresholdOptions, StringConstants.get("PROPERTIES.FUNCTION.THRESHOLD_DESCRIPTION_2","Input must be"));
		
		
		thresholdType = FengGUI.<ThresholdType>createComboBox(innerThresholdOptions);
		for (ThresholdType type:ThresholdType.values())
		{
			thresholdType.addItem(new ListItem<ThresholdType>(type.getDescription(), type));
		}		
		
		FengGUI.createLabel(innerThresholdOptions, StringConstants.get("PROPERTIES.FUNCTION.THRESHOLD_DESCRIPTION_3","the threshold to activate."));

		final Container switchOptions = FengGUI.createContainer(thresholdOptions);
		switchOptions.setLayoutManager(new RowLayout());
		FengGUI.createLabel(switchOptions, StringConstants.get("PROPERTIES.FUNCTION.SWITCH_ACTION","Switch Action"));
		switchMode = FengGUI.<ModifierSwitchType>createComboBox(switchOptions);
		
		for (ModifierSwitchType type:ModifierSwitchType.values())
		{
			switchMode.addItem(new ListItem<ModifierSwitchType>(type.getDescription(), type));
		}
		

		FengGUI.createLabel(thresholdOptions,"Example: if Threshold is 0.5, and the operation is >=, and the action is 'Off/On'");
		FengGUI.createLabel(thresholdOptions,"then the function will output 0 unless the switch recieves 0.5 or more input.");

		
		
		
		
	}

	@Override
	public void close(boolean cancel) {
		super.close(cancel);
		if(!cancel)
		{
			super.standardClosingBehaviour(threshold, ModifierInterpreter.THRESHOLD);
			super.standardClosingBehaviour(thresholdType, ModifierInterpreter.THRESHOLD_TYPE);
		
			super.standardClosingBehaviour(switchMode, ModifierInterpreter.MODIFIER_TYPE);
		
	
			Action<?> apply = super.apply();
			
			UndoManager.getInstance().addAction(apply);	
		}
		
		/*if(modifier == null)
			return;
		if(!cancel)
		{
			try{
				float t = Float.valueOf(  threshold.getText());
				modifier.setThreshold(t);
			}catch(NumberFormatException e)
			{
				
			}
			modifier.setThresholdInverted(invert.isSelected());
			if(switchMode.getSelectedItem()!= null)
			{
				modifier.setSwitchType(switchMode.getSelectedItem().getValue());
			}
		}*/
	}

	@Override
	public void open() {
		this.interpreter = new ModifierInterpreter(getPrototype());
		this.interpreter.loadDefaults();
		initializePrototype();
		
		super.associateWithKey(threshold, ModifierInterpreter.THRESHOLD);
		super.associateWithKey(thresholdType, ModifierInterpreter.THRESHOLD_TYPE);

		super.associateWithKey(switchMode, ModifierInterpreter.MODIFIER_TYPE);
	
		super.standardOpeningBehaviour(threshold, ModifierInterpreter.THRESHOLD);
		super.standardOpeningBehaviour(thresholdType, ModifierInterpreter.THRESHOLD_TYPE);
	
		super.standardOpeningBehaviour(switchMode, ModifierInterpreter.MODIFIER_TYPE);

		
		/*if (modifier != null)
		{
			invert.setSelected(modifier.isThresholdInverted());
			threshold.setText(String.valueOf(modifier.getThreshold()));
			for (ListItem<ModifierSwitchType> switchItem:this.switchMode.getList().getItems())
			{
				if (switchItem.getValue() == modifier.getSwitchType())
				{
				
					switchMode.setSelected(switchItem);
					break;
				}
			}
		}*/
	}
	

}
