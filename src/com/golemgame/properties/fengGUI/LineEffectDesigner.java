package com.golemgame.properties.fengGUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.fenggui.Button;
import org.fenggui.CheckBox;
import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.ListItem;
import org.fenggui.TextEditor;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.event.mouse.MouseAdapter;
import org.fenggui.event.mouse.MousePressedEvent;
import org.fenggui.layout.GridLayout;
import org.fenggui.layout.RowLayout;
import org.fenggui.util.Color;

import com.golemgame.menu.color.ColorPatch;
import com.golemgame.menu.color.ColorWindow;
import com.golemgame.menu.color.ColorDialog.ColorDialogListener;
import com.golemgame.mvc.PropertyState;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SimplePropertyState;
import com.golemgame.mvc.golems.BeamInterpreter;
import com.golemgame.mvc.golems.LineEffectInterpreter;
import com.golemgame.mvc.golems.ParticleEffectRepositoryInterpreter;
import com.golemgame.properties.NoValueException;
import com.golemgame.states.GUILayer;
import com.golemgame.states.StateManager;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;
import com.jme.math.FastMath;
import com.jme.renderer.ColorRGBA;
import com.jme.util.GameTaskQueueManager;

public class LineEffectDesigner  extends PropertyTabAdapter{

	private LineEffectInterpreter interpreter;
/*	private PropertyStore currentStore;
	*/
	private TextEditor minAngle;
	private TextEditor maxAngle;
	
	private TextEditor minLifeSpan;
	private TextEditor maxLifeSpan;
	
	private TextEditor initialSize;
	private TextEditor finalSize;
	
	private TextEditor name;
	
	private TextEditor numberOfParticles;
	//private TextEditor creationSpeed;
	private TextEditor initialVelocity;
	private TextEditor lineWidth;
	private ColorPatch startColor;
	private ColorPatch endColor;
	
	private CheckBox<?> luminescence;
	
	private ComboBox<LineEffectInterpreter> presets ;
	
	private Container optionalComponents;
	
	public LineEffectDesigner() {
		super("Effects");
		interpreter = new LineEffectInterpreter();
	}

	public void close(boolean cancel) {
		super.close(cancel);
		if (!cancel)
		{
			super.standardClosingBehaviour(name, LineEffectInterpreter.NAME,Format.String);			
			super.standardClosingBehaviour(luminescence, LineEffectInterpreter.LUMINOUS);
			super.standardClosingBehaviour(minAngle, LineEffectInterpreter.MIN_ANGLE,Format.Angle);
			super.standardClosingBehaviour(maxAngle, LineEffectInterpreter.MAX_ANGLE,Format.Angle);
			super.standardClosingBehaviour(minLifeSpan, LineEffectInterpreter.MIN_LIFE_SPAN);
			super.standardClosingBehaviour(maxLifeSpan, LineEffectInterpreter.MAX_LIFE_SPAN);
			super.standardClosingBehaviour(numberOfParticles, LineEffectInterpreter.NUM_PARTICLES,Format.Int);
			super.standardClosingBehaviour(initialVelocity, LineEffectInterpreter.INITIAL_VELOCITY);
			super.standardClosingBehaviour(startColor, LineEffectInterpreter.START_COLOR);
			super.standardClosingBehaviour(endColor, LineEffectInterpreter.END_COLOR);
			super.standardClosingBehaviour(initialSize, LineEffectInterpreter.INITIAL_SIZE);
			super.standardClosingBehaviour(finalSize, LineEffectInterpreter.END_SIZE);
			super.standardClosingBehaviour(lineWidth, LineEffectInterpreter.LINE_WIDTH);

			Action<?> apply = super.apply();
			
			UndoManager.getInstance().addAction(apply);
			
			//interpreter.setCreationSpeed(Float.valueOf(creationSpeed.getText()));
			//set(this.currentStore);
		}		
	}

	public void set(PropertyStore set)
	{//used by the save button.
		interpreter.setEffectName(name.getText());
		
		interpreter.setLuminous(luminescence.isSelected());
		
		interpreter.setMinAngle(toRadians(Float.valueOf(minAngle.getText())));
		interpreter.setMaxAngle(toRadians(Float.valueOf(maxAngle.getText())));
		
		interpreter.setMinLifeSpan(Float.valueOf(minLifeSpan.getText()));
		interpreter.setMaxLifeSpan(Float.valueOf(maxLifeSpan.getText()));
		
		interpreter.setInitialSize(Float.valueOf(initialSize.getText()));
		interpreter.setFinalSize(Float.valueOf(finalSize.getText()));
		
		
		interpreter.setNumberOfParticles(Integer.valueOf(numberOfParticles.getText()));
		
		interpreter.setInitialVelocity(Float.valueOf(initialVelocity.getText()));
		
		interpreter.setStartColor(startColor.getColorRGBA());
		interpreter.setEndColor(endColor.getColorRGBA());

		set.set(interpreter.getStore());
	}
	


	public void open() {
		super.open();
	//	creationSpeed.setText(String.valueOf(interpreter.getCreationSpeed()));
	     buildEffectList();
		//loadFrom(this.interpreter);
		
		/*
		 * Load preset/saved effects.
		 * These will come from two places: the machine space's saved effects,
		 * and the standard, not editable, effects.
		 */
	    interpreter = new LineEffectInterpreter(super.getPrototype());
		interpreter.loadDefaults();
	    initializePrototype();
		
		try {
			presets.getLabel().setText(super.getPropertyStoreAdjuster().getCurrentValue(LineEffectInterpreter.NAME).toString());
		} catch (NoValueException e) {
			presets.getLabel().setText("(Multiple Values)");
		}
		
		
		super.associateWithKey(name, LineEffectInterpreter.NAME);
		super.associateWithKey(luminescence, LineEffectInterpreter.LUMINOUS);
		super.associateWithKey(minAngle, LineEffectInterpreter.MIN_ANGLE);
		super.associateWithKey(maxAngle, LineEffectInterpreter.MAX_ANGLE);
		super.associateWithKey(minLifeSpan, LineEffectInterpreter.MIN_LIFE_SPAN);
		super.associateWithKey(maxLifeSpan, LineEffectInterpreter.MAX_LIFE_SPAN);
		super.associateWithKey(numberOfParticles, LineEffectInterpreter.NUM_PARTICLES);
		super.associateWithKey(initialVelocity, LineEffectInterpreter.INITIAL_VELOCITY);
		super.associateWithKey(startColor, LineEffectInterpreter.START_COLOR);
		super.associateWithKey(endColor, LineEffectInterpreter.END_COLOR);
		super.associateWithKey(initialSize, LineEffectInterpreter.INITIAL_SIZE);
		super.associateWithKey(finalSize, LineEffectInterpreter.END_SIZE);
		super.associateWithKey(lineWidth, LineEffectInterpreter.LINE_WIDTH);
		
		
		super.standardOpeningBehaviour(name, LineEffectInterpreter.NAME);		
		super.standardOpeningBehaviour(luminescence, LineEffectInterpreter.LUMINOUS);
		super.standardOpeningBehaviour(minAngle, LineEffectInterpreter.MIN_ANGLE,Format.Angle);
		super.standardOpeningBehaviour(maxAngle, LineEffectInterpreter.MAX_ANGLE,Format.Angle);
		super.standardOpeningBehaviour(minLifeSpan, LineEffectInterpreter.MIN_LIFE_SPAN);
		super.standardOpeningBehaviour(maxLifeSpan, LineEffectInterpreter.MAX_LIFE_SPAN);
		super.standardOpeningBehaviour(numberOfParticles, LineEffectInterpreter.NUM_PARTICLES);
		super.standardOpeningBehaviour(initialVelocity, LineEffectInterpreter.INITIAL_VELOCITY);
		super.standardOpeningBehaviour(startColor, LineEffectInterpreter.START_COLOR);
		super.standardOpeningBehaviour(endColor, LineEffectInterpreter.END_COLOR);
		super.standardOpeningBehaviour(initialSize, LineEffectInterpreter.INITIAL_SIZE);
		super.standardOpeningBehaviour(finalSize, LineEffectInterpreter.END_SIZE);
		super.standardOpeningBehaviour(lineWidth, LineEffectInterpreter.LINE_WIDTH);
		
		
		
	}
	
	
	


	private void loadFrom(LineEffectInterpreter interpreter) {
		name.setText(interpreter.getEffectName());
		
		luminescence.setSelected(interpreter.isLuminous());
		
		minAngle.setText(String.valueOf(toDegrees(interpreter.getMinAngle())));
		maxAngle.setText(String.valueOf(toDegrees(interpreter.getMaxAngle())));
		
		minLifeSpan.setText(String.valueOf(interpreter.getMinLifeSpan()));
		maxLifeSpan.setText(String.valueOf(interpreter.getMaxLifeSpan()));
		
		numberOfParticles.setText(String.valueOf(interpreter.getNumberOfParticles()));
		initialVelocity.setText(String.valueOf(interpreter.getInitialVelocity()));
		
		startColor.setColor(interpreter.getStartColor());
		endColor.setColor(interpreter.getEndColor());
		
		initialSize.setText(String.valueOf(interpreter.getInitialSize()));
		finalSize.setText(String.valueOf(interpreter.getFinalSize()));
		
		presets.getLabel().setText(interpreter.getEffectName());
		

	}

	private static float toDegrees(float value) {
		return value*180f/FastMath.PI;
	}

	@Override
	protected void buildGUI() {
		
		getTab().setLayoutManager(new RowLayout(false));
		
		presets = FengGUI.<LineEffectInterpreter>createComboBox(getTab());
		
		
		
		Container colorContainer = FengGUI.createContainer(getTab());
		colorContainer.setLayoutManager(new RowLayout());
		startColor = new ColorPatch();
		endColor = new ColorPatch();
			
		colorContainer.addWidget(startColor);
		
		colorContainer.addWidget(endColor);
		
		Container settingsContainer = FengGUI.createContainer(getTab());
		settingsContainer.setLayoutManager(new GridLayout(8,3));
		
		FengGUI.createLabel(settingsContainer,"Name");
		FengGUI.createLabel(settingsContainer,"");
		name = FengGUI.createTextEditor(settingsContainer);
		
		this.luminescence = FengGUI.createCheckBox(settingsContainer,"Luminescent Trail");
		FengGUI.createLabel(settingsContainer);
		FengGUI.createLabel(settingsContainer);
		
		FengGUI.createLabel(settingsContainer,"Min, Max Angle");
		minAngle = FengGUI.createTextEditor(settingsContainer);
		maxAngle = FengGUI.createTextEditor(settingsContainer);
		
		FengGUI.createLabel(settingsContainer,"Min, Max Lifespan (ms)");
		minLifeSpan = FengGUI.createTextEditor(settingsContainer);
		maxLifeSpan = FengGUI.createTextEditor(settingsContainer);
		
		FengGUI.createLabel(settingsContainer,"Line Width");
		FengGUI.createLabel(settingsContainer,"");
		lineWidth = FengGUI.createTextEditor(settingsContainer);
	
		FengGUI.createLabel(settingsContainer,"Initial, Final Size");
		initialSize = FengGUI.createTextEditor(settingsContainer);
		finalSize = FengGUI.createTextEditor(settingsContainer);
		
		FengGUI.createLabel(settingsContainer,"Number of Particles");
		FengGUI.createLabel(settingsContainer, "");
		numberOfParticles = FengGUI.createTextEditor(settingsContainer);
		
/*		FengGUI.createLabel(settingsContainer,"Generation Speed");
		FengGUI.createLabel(settingsContainer, "");
		creationSpeed = FengGUI.createTextEditor(settingsContainer);*/
		
		FengGUI.createLabel(settingsContainer,"Initial Velocity");
		FengGUI.createLabel(settingsContainer, "");
		initialVelocity = FengGUI.createTextEditor(settingsContainer);
		
		
		
		Container saveContainer = FengGUI.createContainer(getTab());
		Button saveButton = FengGUI.createButton(saveContainer);
		saveButton.setText("Save Effect");
	
		saveButton.addButtonPressedListener(new IButtonPressedListener()
		{

			@SuppressWarnings("unchecked")
			public void buttonPressed(ButtonPressedEvent e) {
				
				PropertyStore newEffect = new PropertyStore();
				set(newEffect);
				PropertyStore effectStore =StateManager.getMachineSpace().getParticleEffectRepository().getStore();
				//capture the current settings, then add them to the main machine repository (overwriting any existing setting of the same name).
				final PropertyState beforePropertyState = new SimplePropertyState(effectStore, ParticleEffectRepositoryInterpreter.PARTICLE_EFFECTS);
				StateManager.getMachineSpace().getParticleEffectRepository().addEffect(newEffect);
				
				final	PropertyState afterPropertyState = new SimplePropertyState(effectStore, ParticleEffectRepositoryInterpreter.PARTICLE_EFFECTS);
				
				Action<?> action = new Action()
				{

					@Override
					public String getDescription() {
						return "Save Particle Effect";
					}

					@Override
					public Type getType() {
						return null;
					}

					@Override
					public boolean doAction() {
						afterPropertyState.restore();
						afterPropertyState.refresh();
						return true;
					}

					@Override
					public boolean undoAction() {
						beforePropertyState.restore();
						beforePropertyState.refresh();						
						return true;
					}
					
				};
				action.setDependencySet(getPropertyStoreAdjuster().getDependencySet());
				
				UndoManager.getInstance().addAction(action);
				
				buildEffectList();
				
				for (ListItem<LineEffectInterpreter> item:presets.getList().getItems())
				{
					if (item.getValue().getStore().equals(newEffect))
					{
						presets.setSelected(item);
						break;
					}
				}
			}
			
		});
		
		
	      startColor.addMouseListener(new MouseAdapter()
	       {
	    	   
				
				public void mousePressed(MousePressedEvent event) {
					final ColorWindow window = ColorWindow.getInstance();
					//have to call this at a later time, otherwise this window stays ontop

					if (window.getOwner() == startColor)
					{
						window.close();
						
					}else
					{
						
						GameTaskQueueManager.getManager().update(new Callable<Object>()
								{

									
									public Object call() throws Exception {
										window.setOwner(startColor);
										GUILayer layer = GUILayer.getLoadedInstance();
										Color color = startColor.getColor();
										window.setInitialColor(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha());
										window.display(layer.getDisplay());
										window.addColorListener(new ColorDialogListener()
										{

											
											public void cancel() {
											}

											
											public void colorChosen(int red, int green, int blue,int alpha) {
												if (window.getOwner() != startColor)
													return;
												startColor.setColor(new Color(red, green,blue,alpha));
											}
											
										});
										return null;
									}

								});
					}
				
				}
				
				
	       });
	      
	      endColor.addMouseListener(new MouseAdapter()
	       {
	    	   
				
				public void mousePressed(MousePressedEvent event) {
					final ColorWindow window = ColorWindow.getInstance();
					//have to call this at a later time, otherwise this window stays ontop

					if (window.getOwner() == endColor)
					{
						window.close();
						
					}else
					{
						
						GameTaskQueueManager.getManager().update(new Callable<Object>()
								{

									
									public Object call() throws Exception {
										window.setOwner(endColor);
										GUILayer layer = GUILayer.getLoadedInstance();
										Color color = endColor.getColor();
										window.setInitialColor(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha());
										window.display(layer.getDisplay());
										window.addColorListener(new ColorDialogListener()
										{

											
											public void cancel() {
											}

											
											public void colorChosen(int red, int green, int blue,int alpha) {
												if (window.getOwner() != endColor)
													return;
												endColor.setColor(new Color(red, green,blue,alpha));
											}
											
										});
										return null;
									}

								});
					}
				
				}
				
				
	       });
	      
	      
	 
	      
	      presets.addSelectionChangedListener(new ISelectionChangedListener()
	      {

			public void selectionChanged(SelectionChangedEvent selectionChangedEvent) {
				
				loadFrom(presets.getList().getSelectedItem().getValue());
			}
	    	  
	      });
			 optionalComponents = FengGUI.createContainer(getTab());
		
	}


	@Override
	public boolean embed(ITab tab) {
		if(tab instanceof BeamEffectDesigner)
		{
			super.getEmbeddedTabs().add(tab);
			this.optionalComponents.addWidget(tab.getTab());
			this.getTab().layout();
			return true;
		}
		return super.embed(tab);
	}
	

	private void buildEffectList() {
		  presets.getList().clear();
		  
		  presets.addItem(new ListItem<LineEffectInterpreter>("(Original Effect)", interpreter));
		  
	      List<LineEffectInterpreter> presetEffects = getPresetEffects();
	      for (LineEffectInterpreter effect:presetEffects)
	      {
	    	  ListItem<LineEffectInterpreter> item =  new ListItem<LineEffectInterpreter>(effect.getEffectName(),effect);
	    	  
	    	  presets.addItem(item);
	      }
	      
/*	      for (LineEffectInterpreter effect:StateManager.getMachineSpace().getParticleEffectRepository().getEffects())
	      {
	    	  presets.addItem(new ListItem<LineEffectInterpreter>(effect.getEffectName(),effect));
	      }*/
	      
	}

/*	public void setStartingParticleEffect(PropertyStore startFrom)
	{
		currentStore = startFrom;
		interpreter.getStore().set(startFrom);
	}*/

	
	public static List<LineEffectInterpreter> getPresetEffects()
	{
		ArrayList<LineEffectInterpreter> presets = new ArrayList<LineEffectInterpreter>();
		
		ColorRGBA invisibleWhite = new ColorRGBA(1f,1f,1f,0f);
		
		ColorRGBA plasmaPurple = buildColor(37,28,125,0);
		ColorRGBA darkPurple =  buildColor(37,28,125,100);
		ColorRGBA alienGreen =  buildColor(44,123,30,100);
	//	ColorRGBA plasmaBlue = buildColor(37,28,125,0);
		
		LineEffectInterpreter magic = new LineEffectInterpreter();
		magic.setEffectName("Magic");
		magic.setMinLifeSpan(700);
		magic.setMaxLifeSpan(4000);
		magic.setMinAngle(toRadians(0));
		magic.setMaxAngle(toRadians(15));
		magic.setInitialSize(0.1f);
		magic.setFinalSize(5f);
		magic.setInitialVelocity(0.01f);
		magic.setNumberOfParticles(30);
		magic.setStartColor(invisibleWhite);
		magic.setEndColor(darkPurple);
		presets.add(magic);
		
		LineEffectInterpreter plasma = new LineEffectInterpreter();
		plasma.setEffectName("Plasma");
		plasma.setMinLifeSpan(100);
		plasma.setMaxLifeSpan(500);
		plasma.setMinAngle(0);
		plasma.setMaxAngle(toRadians(60));
		plasma.setInitialSize(0.1f);
		plasma.setFinalSize(5f);
		plasma.setInitialVelocity(0.01f);
		plasma.setNumberOfParticles(25);
		plasma.setStartColor(ColorRGBA.white);
		plasma.setEndColor(plasmaPurple);
		presets.add(plasma);
		
		LineEffectInterpreter smoke = new LineEffectInterpreter();
		smoke.setEffectName("Smoke");
		smoke.setLuminous(false);
		smoke.setMinLifeSpan(1000);
		smoke.setMaxLifeSpan(2500);
		smoke.setMinAngle(0);
		smoke.setMaxAngle(toRadians(5));
		smoke.setInitialSize(0.5f);
		smoke.setFinalSize(15f);
		smoke.setInitialVelocity(0.01f);
		smoke.setNumberOfParticles(30);
		smoke.setStartColor(buildColor(39,39,39,100));
		smoke.setEndColor(buildColor(128,128,128,0));
		presets.add(smoke);
	
		LineEffectInterpreter rings = new LineEffectInterpreter();
		rings.setEffectName("Rings");
		rings.setMinLifeSpan(700);
		rings.setMaxLifeSpan(700);
		rings.setMinAngle(toRadians(60));
		rings.setMaxAngle(toRadians(60));
		rings.setInitialSize(0f);
		rings.setFinalSize(2f);
		rings.setInitialVelocity(0.01f);
		rings.setNumberOfParticles(200);
		rings.setStartColor(alienGreen);
		rings.setEndColor(buildColor(44,123,30,0));
		presets.add(rings);
		
/*		LineEffectInterpreter alien = new LineEffectInterpreter();
		alien.setEffectName("Alien");
		alien.setMinLifeSpan(700);
		alien.setMaxLifeSpan(700);
		alien.setMinAngle(1);
		alien.setMaxAngle(0);
		alien.setInitialSize(0f);
		alien.setFinalSize(2f);
		alien.setInitialVelocity(0.01f);
		alien.setNumberOfParticles(60);
		alien.setStartColor(invisibleWhite);
		alien.setEndColor(plasmaBlue);
		presets.add(alien);*/
		
		presets.add(new LineEffectInterpreter( BeamInterpreter.getDefaultLineEffect()));
		
		LineEffectInterpreter saturn = new LineEffectInterpreter();
		saturn.setEffectName("Saturn V");
		saturn.setMinLifeSpan(300);
		saturn.setMaxLifeSpan(1500);
		saturn.setMinAngle(0);
		saturn.setMaxAngle(toRadians(10));
		saturn.setInitialSize(1f);
		saturn.setFinalSize(5f);
		saturn.setInitialVelocity(0.01f);
		saturn.setNumberOfParticles(300);
		saturn.setStartColor( new ColorRGBA( 1.0f, 1.0f, 1.0f, 1.0f ));
		saturn.setEndColor( new ColorRGBA( 0.6f, 0.2f, 0.0f, 0.0f ));
		presets.add(saturn);
		
		LineEffectInterpreter sputter = new LineEffectInterpreter();
		sputter.setEffectName("Sputter");
		sputter.setMinLifeSpan(300);
		sputter.setMaxLifeSpan(1500);
		sputter.setMinAngle(toRadians(10));
		sputter.setMaxAngle(FastMath.HALF_PI);
		sputter.setInitialSize(1f);
		sputter.setFinalSize(5f);
		sputter.setInitialVelocity(0.01f);
		sputter.setNumberOfParticles(15);
		sputter.setStartColor( new ColorRGBA( 0.5f, 0.5f, 0.5f, 1.0f ));
		sputter.setEndColor( new ColorRGBA( 0.2f, 0.2f, 0.2f, 0.0f ));
		sputter.setLuminous(false);
		presets.add(sputter);
		
		LineEffectInterpreter plasma2 = new LineEffectInterpreter();
		plasma2.setEffectName("Spark");
		plasma2.setMinLifeSpan(100);
		plasma2.setMaxLifeSpan(300);
		plasma2.setMinAngle(toRadians(10));
		plasma2.setMaxAngle( FastMath.HALF_PI );
		plasma2.setInitialSize(1f);
		plasma2.setFinalSize(5f);
		plasma2.setInitialVelocity(0.01f);
		plasma2.setNumberOfParticles(5);
		plasma2.setStartColor( ColorRGBA.white);
		plasma2.setEndColor(buildColor(198,40,234,0));
		presets.add(plasma2);
		
		
		LineEffectInterpreter nova = new LineEffectInterpreter();
		nova.setEffectName("Nova");
		nova.setMinLifeSpan(300);
		nova.setMaxLifeSpan(500);
		nova.setMinAngle(0f);
		nova.setMaxAngle(toRadians(5));
		nova.setInitialSize(1f);
		nova.setFinalSize(5f);
		nova.setInitialVelocity(0.001f);
		nova.setNumberOfParticles(10);
		nova.setStartColor( new ColorRGBA( 1.0f, 1.0f, 1.0f, 1.0f ));
		nova.setEndColor( new ColorRGBA( 0.6f, 0.2f, 0.0f, 0.0f  ));
		presets.add(nova);
		
		Collections.sort(presets);
	      
		return presets;
	}
	
	private static ColorRGBA buildColor(float r, float g, float b, float a)
	{
		return new ColorRGBA(((r/255f)),((g/255f)),((b/255f)),((a/100f)));
	}
	
	
}
