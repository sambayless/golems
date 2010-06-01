package com.golemgame.properties.fengGUI;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.ListItem;
import org.fenggui.TextEditor;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;
import org.fenggui.util.Color;
import org.fenggui.util.Spacing;

import com.golemgame.local.StringConstants;
import com.golemgame.mvc.golems.MaterialPropertiesInterpreter;
import com.golemgame.mvc.golems.MaterialPropertiesInterpreter.MaterialClass;
import com.golemgame.structural.MaterialWrapper;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;

public class MatterTab extends PropertyTabAdapter {
	
	private MaterialPropertiesInterpreter interpreter;

	private Container positionContainer;
	
	
	private ComboBox<MaterialClass> dropDown;
	private Map<String,ListItem<MaterialClass>> materialItems;
	
	private Label densityLabel;

	private TextEditor densityText;

	private SoundPropertiesTab soundTab = null;
	
	public void setSoundTab(SoundPropertiesTab soundTab) {
		this.soundTab = soundTab;
	}



	public MatterTab() {
		super(StringConstants.get("PROPERTIES.MATTER","Matter"));
		
		
	}



	public void close(boolean cancel) {
		if(!cancel)
		{
			if(super.isAltered(dropDown))
			{
				ListItem<MaterialClass> selected = materialItems.get(dropDown.getSelectedValue());
				if (selected != null)
				{		
					interpreter.setMaterial(selected.getValue());
					setValueAltered(MaterialPropertiesInterpreter.MATERIAL,true);
				}
			}
	
			
			super.standardClosingBehaviour(densityText, MaterialPropertiesInterpreter.DENSITY);
	
			
			Action<?> apply = super.apply();
			
			UndoManager.getInstance().addAction(apply);

			
		}
		
	}
	


	protected void buildGUI()
	{
		materialItems= new HashMap<String,ListItem<MaterialClass>>();
	
		getTab().setLayoutManager(new BorderLayout());

		positionContainer = FengGUI.createContainer(getTab());
		positionContainer.setLayoutData(BorderLayoutData.NORTH);
	      
	    Container comboFrame = FengGUI.createContainer(getTab());
	   
		comboFrame.setLayoutData(BorderLayoutData.SOUTH);
		comboFrame.setLayoutManager(new BorderLayout());
		dropDown = FengGUI.<MaterialClass>createComboBox();// new ComboBox<Material>();
		
		dropDown.getAppearance().setPadding(new Spacing(0,5));
		dropDown.setLayoutData(BorderLayoutData.CENTER);
		dropDown.getAppearance().add(new PlainBackground(Color.WHITE));
		
		Label labelCombo = FengGUI.createLabel(comboFrame,StringConstants.get("PROPERTIES.MATTER.MATERIAL","Material:"));
	//	labelCombo.getAppearance().setFont(Font.getDefaultFont());
		labelCombo.setLayoutData(BorderLayoutData.WEST);
		comboFrame.addWidget(dropDown);
		//labelCombo.getAppearance().add(new PlainBackground(Color.WHITE));
		comboFrame.layout();
		
		for (MaterialClass mat:MaterialClass.values())
		{
			if(mat.getName().equalsIgnoreCase("Sponge"))
				continue;//skip sponge
			ListItem<MaterialClass> item = new ListItem<MaterialClass>(mat.getName() ,mat);
			materialItems.put(mat.getName(), item);
			dropDown.addItem(item);			
	
		}
		
		dropDown.addSelectionChangedListener(new ISelectionChangedListener()
		{

			
			public void selectionChanged(
					SelectionChangedEvent selectionChangedEvent) 
			{
			
	
				if (selectionChangedEvent.isSelected())
				{
					ListItem<MaterialClass> selected = materialItems.get(dropDown.getSelectedValue());
					if (selected != null)
					{					
						
						DecimalFormat format = new DecimalFormat();
						format.setMaximumFractionDigits(6);
						
						//float density = (float) ((sliderMovedEvent.getPosition() * (MaterialNode.MAX_DENSITY - MaterialNode.MIN_DENSITY) + MaterialNode.MIN_DENSITY));
						densityText.setText(format.format(MaterialWrapper.getMaterial( selected.getValue()).getDensity()));
						
						if(soundTab!=null)
							soundTab.setMaterial (selected.getValue());
					}
				}

			}
			
		});	
		
        Container frame = FengGUI.createContainer(getTab());
        frame.setLayoutManager(new BorderLayout());
        frame.setLayoutData(BorderLayoutData.CENTER);
        frame.setShrinkable(true);
        
        Container grid = FengGUI.createContainer(frame);
        grid.setLayoutData(BorderLayoutData.NORTH);
        grid.setLayoutManager(new RowLayout(false));
        
        Container sliderFrame = FengGUI.createContainer(grid);
        //frame.addWidget(sliderFrame);
        sliderFrame.setLayoutData(BorderLayoutData.NORTH);
        sliderFrame.setLayoutManager(new RowLayout());
		densityLabel = FengGUI.createLabel();
		sliderFrame.addWidget(densityLabel);

		densityLabel.setText(StringConstants.get("PROPERTIES.MATTER.DENSITY","Density  (g/cm^3):"));
     
        
        densityText = FengGUI.createTextField(sliderFrame);
        densityText.getAppearance().setPadding(new Spacing(0,0,55,0));
       densityText.getAppearance().getData().setMultiline(false);
        
        densityText.getAppearance().getData().setWordWarping(false);
        densityText.setText("10000000.0");
   

        
        sliderFrame.layout();
        frame.layout();
		
        getTab().layout();
		
	}


	
	public void open() {
		super.open();
		this.interpreter = new MaterialPropertiesInterpreter(super.getPropertyStoreAdjuster().getPrototype());
		this.interpreter.loadDefaults();
		super.setComparePoint();
		

		super.associateWithKey(densityText, MaterialPropertiesInterpreter.DENSITY);
		super.associateWithKey(this.dropDown, MaterialPropertiesInterpreter.MATERIAL);
		
		
		super.standardOpeningBehaviour(dropDown, MaterialPropertiesInterpreter.MATERIAL);//this has to happen first
		super.standardOpeningBehaviour(densityText, MaterialPropertiesInterpreter.DENSITY);
	
	}



	
}
