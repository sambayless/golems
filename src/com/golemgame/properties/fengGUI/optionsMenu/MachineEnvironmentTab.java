package com.golemgame.properties.fengGUI.optionsMenu;


import org.fenggui.CheckBox;
import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.ListItem;
import org.fenggui.TextEditor;
import org.fenggui.decorator.border.TitledBorder;
import org.fenggui.event.ITextChangedListener;
import org.fenggui.event.TextChangedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.GridLayout;
import org.fenggui.layout.RowLayout;

import com.golemgame.local.StringConstants;
import com.golemgame.mechanical.MachineSpaceSettings;
import com.golemgame.properties.fengGUI.ITab;
import com.golemgame.properties.fengGUI.ImageTabAdapter;
import com.golemgame.settings.Vector3fSetting;
import com.golemgame.states.GeneralSettings;
import com.golemgame.states.StateManager;
import com.golemgame.tool.ActionToolSettings;
import com.jme.image.Texture;
import com.jme.math.Vector3f;

public class MachineEnvironmentTab extends ImageTabAdapter {

	TextEditor xSpacing;
	TextEditor ySpacing ;
	TextEditor zSpacing;
	TextEditor angleSize;
	TextEditor gravityMagnitude;
	TextEditor gravityX;
	TextEditor gravityY;
	TextEditor gravityZ;
	CheckBox<Object> useBuoyancy;
	TextEditor fluidHeight;
	TextEditor densityText;
	Container background;
	CheckBox<Object> lockGrid;
	
	ComboBox<Float> speed;
	private Texture icon;
	public MachineEnvironmentTab() {
		super(StringConstants.get("MAIN_MENU.ENVIRONMENT" ,"Environment"));
		 icon =super.loadTexture("buttons/menu/Environment.png");
	}

	@Override
	public Texture getIcon() {
		return icon;
	}

	@Override
	protected void buildGUI() {
		super.buildGUI();
		getTab().setLayoutManager(new BorderLayout());
		
		Container mainContainer =  FengGUI.createContainer(getTab());
		mainContainer.setLayoutData(BorderLayoutData.NORTH);
		
		mainContainer.setLayoutManager(new RowLayout(false));
		
		 Container speedContainer = FengGUI.createContainer(mainContainer);
		 speedContainer.getAppearance().add(new TitledBorder(StringConstants.get("MAIN_MENU.ENVIRONMENT.SPEED" ,"Speed")));
		 speedContainer.setLayoutManager(new GridLayout(4,2));
		 
		 speed = FengGUI.<Float>createComboBox(speedContainer);
		 
		 speed.addItem(new ListItem<Float> (StringConstants.get("MAIN_MENU.ENVIRONMENT.SPEED_NORMAL" ,"Normal"), 1f));
		 speed.addItem(new ListItem<Float> (StringConstants.get("MAIN_MENU.ENVIRONMENT.SPEED_PAUSED" ,"(Paused)"), Float.NEGATIVE_INFINITY));
		 speed.addItem(new ListItem<Float> ("1/10x", 0.1f));
		 speed.addItem(new ListItem<Float> ("1/5x", 0.2f));
		 speed.addItem(new ListItem<Float> (StringConstants.get("MAIN_MENU.ENVIRONMENT.SPEED_HALF" ,"Half"), 0.5f));
		
		 speed.addItem(new ListItem<Float> (StringConstants.get("MAIN_MENU.ENVIRONMENT.SPEED_DOUBLE" ,"Double"), 2f));
		 speed.addItem(new ListItem<Float> ("5x", 5f));
		 speed.addItem(new ListItem<Float> ("10x", 10f));
		 speed.addItem(new ListItem<Float> (StringConstants.get("MAIN_MENU.ENVIRONMENT.SPEED_UNLIMITED" ,"(Unlimited)"), -1f));
		 
		Container environment = FengGUI.createContainer(mainContainer);
		environment.getAppearance().add(new TitledBorder(StringConstants.get("MAIN_MENU.ENVIRONMENT" ,"Environment")));
		
		environment.setLayoutManager(new RowLayout());
		
		
		
		
		
		//gravity
		//FengGUI.createLabel(environment,"Stength of Gravity (m/s^2)");
		//gravityMagnitude = FengGUI.createTextEditor(environment);
		
		Label l= FengGUI.createLabel(environment,StringConstants.get("MAIN_MENU.ENVIRONMENT.GRAVITY" ,"Gravity in each direction (x,y,z)"));
		l.setExpandable(false);
		l.setShrinkable(true);
		
		Container gravDir = FengGUI.createContainer(environment);
		gravDir.setExpandable(true);
		gravDir.setLayoutManager(new GridLayout(1,3));
		
		gravityX = FengGUI.createTextEditor(gravDir);
		gravityY = FengGUI.createTextEditor(gravDir);
		gravityZ = FengGUI.createTextEditor(gravDir);
		
		
		
		
		//buoyancy
		Container buoyancyContainer = FengGUI.createContainer();//mainContainer
		buoyancyContainer.getAppearance().add(new TitledBorder("Buoyancy"));
		buoyancyContainer.setLayoutManager(new GridLayout(4,2));
		useBuoyancy = FengGUI.createCheckBox(buoyancyContainer,"Enable Buoyancy ");
		useBuoyancy.setSelected(false);
		FengGUI.createLabel(buoyancyContainer,"Note: Buoyancy is still experimental");
		FengGUI.createLabel(buoyancyContainer,"Fluid height");
		fluidHeight = FengGUI.createTextEditor(buoyancyContainer);
		FengGUI.createLabel(buoyancyContainer,"Fluid density (water is 1 g/cm^3)");
		densityText = FengGUI.createTextEditor(buoyancyContainer);
		FengGUI.createLabel(buoyancyContainer,"Pre-set fluid");
		
		
		//grid
		Container gridLargerContainer = FengGUI.createContainer(mainContainer);
		gridLargerContainer.getAppearance().add(new TitledBorder(StringConstants.get("MAIN_MENU.ENVIRONMENT.GRID_SPACING" ,"Grid Spacing")));
		gridLargerContainer.setLayoutManager(new RowLayout(false));
		
		 lockGrid = FengGUI.createCheckBox(gridLargerContainer,StringConstants.get("MAIN_MENU.ENVIRONMENT.LOCK" , "Lock"));
		 lockGrid.setSelected(true);
		
		Container gridContainer = FengGUI.createContainer(gridLargerContainer);
			
		
		gridContainer.setLayoutManager(new GridLayout(1,4));
		
		
		
		FengGUI.createLabel(gridContainer,StringConstants.get("MAIN_MENU.ENVIRONMENT.UNITS" ,"Units (x,y,z):"));
		 xSpacing = FengGUI.createTextEditor(gridContainer);
		
		 ySpacing = FengGUI.createTextEditor(gridContainer);
	
		 zSpacing = FengGUI.createTextEditor(gridContainer);	
	
		 
			Container angleContainer = FengGUI.createContainer(mainContainer);
			angleContainer.getAppearance().add(new TitledBorder(StringConstants.get("MAIN_MENU.ENVIRONMENT.ROTATION" ,"Rotation")));
			angleContainer.setLayoutManager(new GridLayout(1,2));
			FengGUI.createLabel(angleContainer,StringConstants.get("MAIN_MENU.ENVIRONMENT.ANGLES" ,"Angles snap to (degrees):"));
			 angleSize = FengGUI.createTextEditor(angleContainer);	
	
		ITextChangedListener keyListener = new ITextChangedListener()
		{
			public void textChanged(TextChangedEvent textChangedEvent) {
				
				
				if(lockGrid.isSelected())
				{
					String text = "" ;
					
					if (xSpacing == textChangedEvent.getSource()) 
						text = xSpacing.getText();
					else if (ySpacing == textChangedEvent.getSource()) 
						text = ySpacing.getText();
					else if (zSpacing == textChangedEvent.getSource()) 
						text = zSpacing.getText();
					
					
					
					if (!(xSpacing == textChangedEvent.getSource()) && !  xSpacing.getText().equals(text))
						xSpacing.setText(text);
					if (!(ySpacing == textChangedEvent.getSource()) && ! ySpacing.getText().equals(text))
						ySpacing.setText(text);
					if (!(zSpacing == textChangedEvent.getSource()) && ! zSpacing.getText().equals(text))
						zSpacing.setText(text);
				}
			}			
		};
		
		xSpacing.addTextChangedListener(keyListener);
		ySpacing.addTextChangedListener(keyListener);
		zSpacing.addTextChangedListener(keyListener);
		
		background = new Container();
		mainContainer.addWidget(background);
		//if color is selected, then provide a color selector. Otherwise, draw the image below
	//	this.embed(new SkyTab());
	}


	@Override
	public boolean embed(ITab tab) {
		if(tab instanceof SkyTab)
		{
			super.getEmbeddedTabs().add(tab);
			background.addWidget(tab.getTab());
			background.layout();
			getTab().layout();
			return true;
		}
		return super.embed(tab);
	}

	@Override
	public void close(boolean cancel) {
		super.close(cancel);
		if(!cancel)
		{
			MachineSpaceSettings settings = StateManager.getMachineSpace().getMachineSpaceSettings();
			Vector3f newDirection = new Vector3f( settings.getGravitySettings().getDirectionOfGravity());
			
	/*		try{
				float mag= Float.valueOf(gravityMagnitude.getText());
				settings.getGravitySettings().setMagnitude(mag);
			}catch(NumberFormatException e){}
			*/
			try{
				float x= Float.valueOf(gravityX.getText());
				newDirection.setX(x);
	
				float y= Float.valueOf(gravityY.getText());
				newDirection.setY(y);
	
				float z= Float.valueOf(gravityZ.getText());
				newDirection.setZ(z);
				
				//only if none of these fail should gravity direction be changed.
			//	newDirection.normalizeLocal();
				settings.getGravitySettings().setDirectionOfGravity(newDirection.normalize());
				settings.getGravitySettings().setMagnitude(newDirection.length());
			}catch(NumberFormatException e){}

			Vector3f newValue = new Vector3f(settings.getGridSettings().getGridSpacing());
			try{
				float x= Float.valueOf(xSpacing.getText());
				newValue.setX(x);
			}catch(NumberFormatException e){}
			try{
				float y= Float.valueOf(ySpacing.getText());
				newValue.setY(y);
			}catch(NumberFormatException e){}
			try{
				float z= Float.valueOf(zSpacing.getText());
				newValue.setZ(z);
			}catch(NumberFormatException e){}
			
			try{
				float angle= Float.valueOf(angleSize.getText());
				ActionToolSettings.getInstance().getRotationSnapStep().setValue(180f/angle);
				
			}catch(NumberFormatException e){}
			
			Vector3fSetting grid = ActionToolSettings.getInstance().getGridUnits();
			grid.setValue(newValue);//store this as a preference also, so next time a machine is created it defaults to this grid spacing.
			settings.getGridSettings().getGridSpacing().set(grid.getValue());
			
			
			settings.getBuoyancySettings().setEnabled(useBuoyancy.isSelected());
			if(settings.getBuoyancySettings().isEnabled())
			{
				try{
					float den= Float.valueOf(densityText.getText());
					settings.getBuoyancySettings().setFluidDensity(den);
				}catch(NumberFormatException e){}
				try{
					float height= Float.valueOf(fluidHeight.getText());
					settings.getBuoyancySettings().setFluidHeight(height);
				}catch(NumberFormatException e){}
			}
			
		
				float speedValue= speed.getSelectedItem().getValue();
			
				GeneralSettings.getInstance().getPhysicsSpeed().setValue(speedValue);
	
			
			
			
			
		}
	
	}

	@Override
	public void open() {
		super.open();
		MachineSpaceSettings settings = StateManager.getMachineSpace().getMachineSpaceSettings();
		Vector3f dir = new Vector3f(settings.getGravitySettings().getDirectionOfGravity());
		dir.multLocal(settings.getGravitySettings().getMagnitude());
	//	gravityMagnitude.setText(String.valueOf(settings.getGravitySettings().getMagnitude()));
		gravityX.setText(String.valueOf(dir.getX()));
		gravityY.setText(String.valueOf(dir.getY()));
		gravityZ.setText(String.valueOf(dir.getZ()));
		
		Vector3f grid = settings.getGridSettings().getGridSpacing();
		xSpacing.setText(String.valueOf(grid.getX()));
		ySpacing.setText(String.valueOf(grid.getY()));
		zSpacing.setText(String.valueOf(grid.getZ()));
		
		angleSize.setText(String.valueOf(Math.round(10f * 180f/ActionToolSettings.getInstance().getRotationSnapStep().getValue())/10f));
		
		this.useBuoyancy.setSelected(settings.getBuoyancySettings().isEnabled());
		this.densityText.setText(String.valueOf(settings.getBuoyancySettings().getFluidDensity()));
		this.fluidHeight.setText(String.valueOf(settings.getBuoyancySettings().getFluidHeight()));
		
		float speedValue= GeneralSettings.getInstance().getPhysicsSpeed().getValue();
		
		for (ListItem<Float> listItem:speed.getList().getItems())
		{
			if (listItem.getValue() == speedValue)
			{
				speed.setSelected(listItem);
				break;
			}
		}
		
		
	}
	
		
	
	
	
}
