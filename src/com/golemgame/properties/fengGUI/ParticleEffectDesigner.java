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

import com.golemgame.local.StringConstants;
import com.golemgame.menu.color.ColorPatch;
import com.golemgame.menu.color.ColorWindow;
import com.golemgame.menu.color.ColorDialog.ColorDialogListener;
import com.golemgame.mvc.PropertyState;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SimplePropertyState;
import com.golemgame.mvc.golems.ParticleEffectInterpreter;
import com.golemgame.mvc.golems.ParticleEffectRepositoryInterpreter;
import com.golemgame.mvc.golems.RocketPropellantInterpreter;
import com.golemgame.properties.NoValueException;
import com.golemgame.states.GUILayer;
import com.golemgame.states.StateManager;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;
import com.jme.math.FastMath;
import com.jme.renderer.ColorRGBA;
import com.jme.util.GameTaskQueueManager;

public class ParticleEffectDesigner  extends PropertyTabAdapter{

	private ParticleEffectInterpreter interpreter;
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
	
	private ColorPatch startColor;
	private ColorPatch endColor;
	
	private CheckBox<?> luminescence;
	
	private ComboBox<ParticleEffectInterpreter> presets ;
	
	private Container optionalComponents;
	
	public ParticleEffectDesigner() {
		super(StringConstants.get("PROPERTIES.ROCKET.EFFECTS","Effects"));
		interpreter = new ParticleEffectInterpreter();
	}

	public void close(boolean cancel) {
		super.close(cancel);
		if (!cancel)
		{
			super.standardClosingBehaviour(name, ParticleEffectInterpreter.NAME,Format.String);			
			super.standardClosingBehaviour(luminescence, ParticleEffectInterpreter.LUMINOUS);
			super.standardClosingBehaviour(minAngle, ParticleEffectInterpreter.MIN_ANGLE,Format.Angle);
			super.standardClosingBehaviour(maxAngle, ParticleEffectInterpreter.MAX_ANGLE,Format.Angle);
			super.standardClosingBehaviour(minLifeSpan, ParticleEffectInterpreter.MIN_LIFE_SPAN);
			super.standardClosingBehaviour(maxLifeSpan, ParticleEffectInterpreter.MAX_LIFE_SPAN);
			super.standardClosingBehaviour(numberOfParticles, ParticleEffectInterpreter.NUM_PARTICLES,Format.Int);
			super.standardClosingBehaviour(initialVelocity, ParticleEffectInterpreter.INITIAL_VELOCITY);
			super.standardClosingBehaviour(startColor, ParticleEffectInterpreter.START_COLOR);
			super.standardClosingBehaviour(endColor, ParticleEffectInterpreter.END_COLOR);
			super.standardClosingBehaviour(initialSize, ParticleEffectInterpreter.INITIAL_SIZE);
			super.standardClosingBehaviour(finalSize, ParticleEffectInterpreter.END_SIZE);

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
	    interpreter = new ParticleEffectInterpreter(super.getPrototype());
		interpreter.loadDefaults();
	    initializePrototype();
		
		try {
			presets.getLabel().setText(super.getPropertyStoreAdjuster().getCurrentValue(ParticleEffectInterpreter.NAME).toString());
		} catch (NoValueException e) {
			presets.getLabel().setText("(Multiple Values)");
		}
		
		
		super.associateWithKey(name, ParticleEffectInterpreter.NAME);
		super.associateWithKey(luminescence, ParticleEffectInterpreter.LUMINOUS);
		super.associateWithKey(minAngle, ParticleEffectInterpreter.MIN_ANGLE);
		super.associateWithKey(maxAngle, ParticleEffectInterpreter.MAX_ANGLE);
		super.associateWithKey(minLifeSpan, ParticleEffectInterpreter.MIN_LIFE_SPAN);
		super.associateWithKey(maxLifeSpan, ParticleEffectInterpreter.MAX_LIFE_SPAN);
		super.associateWithKey(numberOfParticles, ParticleEffectInterpreter.NUM_PARTICLES);
		super.associateWithKey(initialVelocity, ParticleEffectInterpreter.INITIAL_VELOCITY);
		super.associateWithKey(startColor, ParticleEffectInterpreter.START_COLOR);
		super.associateWithKey(endColor, ParticleEffectInterpreter.END_COLOR);
		super.associateWithKey(initialSize, ParticleEffectInterpreter.INITIAL_SIZE);
		super.associateWithKey(finalSize, ParticleEffectInterpreter.END_SIZE);
	     
		
		
		super.standardOpeningBehaviour(name, ParticleEffectInterpreter.NAME);		
		super.standardOpeningBehaviour(luminescence, ParticleEffectInterpreter.LUMINOUS);
		super.standardOpeningBehaviour(minAngle, ParticleEffectInterpreter.MIN_ANGLE,Format.Angle);
		super.standardOpeningBehaviour(maxAngle, ParticleEffectInterpreter.MAX_ANGLE,Format.Angle);
		super.standardOpeningBehaviour(minLifeSpan, ParticleEffectInterpreter.MIN_LIFE_SPAN);
		super.standardOpeningBehaviour(maxLifeSpan, ParticleEffectInterpreter.MAX_LIFE_SPAN);
		super.standardOpeningBehaviour(numberOfParticles, ParticleEffectInterpreter.NUM_PARTICLES);
		super.standardOpeningBehaviour(initialVelocity, ParticleEffectInterpreter.INITIAL_VELOCITY);
		super.standardOpeningBehaviour(startColor, ParticleEffectInterpreter.START_COLOR);
		super.standardOpeningBehaviour(endColor, ParticleEffectInterpreter.END_COLOR);
		super.standardOpeningBehaviour(initialSize, ParticleEffectInterpreter.INITIAL_SIZE);
		super.standardOpeningBehaviour(finalSize, ParticleEffectInterpreter.END_SIZE);
	     
		
		
	}
	
	
	


	private void loadFrom(ParticleEffectInterpreter interpreter) {
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
		
		presets = FengGUI.<ParticleEffectInterpreter>createComboBox(getTab());
		
		
		
		Container colorContainer = FengGUI.createContainer(getTab());
		colorContainer.setLayoutManager(new RowLayout());
		startColor = new ColorPatch();
		endColor = new ColorPatch();
			
		colorContainer.addWidget(startColor);
		
		colorContainer.addWidget(endColor);
		
		Container settingsContainer = FengGUI.createContainer(getTab());
		settingsContainer.setLayoutManager(new GridLayout(7,3));
		
		FengGUI.createLabel(settingsContainer,StringConstants.get("PROPERTIES.ROCKET.NAME","Name"));
		FengGUI.createLabel(settingsContainer,"");
		name = FengGUI.createTextEditor(settingsContainer);
		
		this.luminescence = FengGUI.createCheckBox(settingsContainer,StringConstants.get("PROPERTIES.ROCKET.LUMINESCENT","Luminescent Trail"));
		FengGUI.createLabel(settingsContainer);
		FengGUI.createLabel(settingsContainer);
		
		FengGUI.createLabel(settingsContainer,StringConstants.get("PROPERTIES.ROCKET.ANGLE","Min, Max Angle"));//"Min, Max Angle");
		minAngle = FengGUI.createTextEditor(settingsContainer);
		maxAngle = FengGUI.createTextEditor(settingsContainer);
		
		FengGUI.createLabel(settingsContainer,StringConstants.get("PROPERTIES.ROCKET.LIFESPAN","Min, Max Lifespan (ms)"));//);
		minLifeSpan = FengGUI.createTextEditor(settingsContainer);
		maxLifeSpan = FengGUI.createTextEditor(settingsContainer);
		
		
		FengGUI.createLabel(settingsContainer,StringConstants.get("PROPERTIES.ROCKET.SIZE","Initial, Final Size"));//);
		initialSize = FengGUI.createTextEditor(settingsContainer);
		finalSize = FengGUI.createTextEditor(settingsContainer);
		
		FengGUI.createLabel(settingsContainer,StringConstants.get("PROPERTIES.ROCKET.PARTICLE_NUM","Number of Particles") );//);
		FengGUI.createLabel(settingsContainer, "");
		numberOfParticles = FengGUI.createTextEditor(settingsContainer);
		
/*		FengGUI.createLabel(settingsContainer,"Generation Speed");
		FengGUI.createLabel(settingsContainer, "");
		creationSpeed = FengGUI.createTextEditor(settingsContainer);*/
		
		FengGUI.createLabel(settingsContainer,StringConstants.get("PROPERTIES.ROCKET.VELOCITY","Initial Velocity"));//);
		FengGUI.createLabel(settingsContainer, "");
		initialVelocity = FengGUI.createTextEditor(settingsContainer);
		
		
		
		Container saveContainer = FengGUI.createContainer(getTab());
		Button saveButton = FengGUI.createButton(saveContainer);
		saveButton.setText(StringConstants.get("PROPERTIES.ROCKET.SAVE","Save Effect"));//");
	
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
				
				for (ListItem<ParticleEffectInterpreter> item:presets.getList().getItems())
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
		if(tab instanceof RocketEffectDesigner)
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
		  
		  presets.addItem(new ListItem<ParticleEffectInterpreter>("(Original Effect)", interpreter));
		  
	      List<ParticleEffectInterpreter> presetEffects = getPresetEffects();
	      for (ParticleEffectInterpreter effect:presetEffects)
	      {
	    	  ListItem<ParticleEffectInterpreter> item =  new ListItem<ParticleEffectInterpreter>(effect.getEffectName(),effect);
	    	  
	    	  presets.addItem(item);
	      }
	      
	      for (ParticleEffectInterpreter effect:StateManager.getMachineSpace().getParticleEffectRepository().getEffects())
	      {
	    	  presets.addItem(new ListItem<ParticleEffectInterpreter>(effect.getEffectName(),effect));
	      }
	      
	}

/*	public void setStartingParticleEffect(PropertyStore startFrom)
	{
		currentStore = startFrom;
		interpreter.getStore().set(startFrom);
	}*/

	
	public static List<ParticleEffectInterpreter> getPresetEffects()
	{
		ArrayList<ParticleEffectInterpreter> presets = new ArrayList<ParticleEffectInterpreter>();
		
		ColorRGBA invisibleWhite = new ColorRGBA(1f,1f,1f,0f);
		
		ColorRGBA plasmaPurple = buildColor(37,28,125,0);
		ColorRGBA darkPurple =  buildColor(37,28,125,100);
		ColorRGBA alienGreen =  buildColor(44,123,30,100);
	//	ColorRGBA plasmaBlue = buildColor(37,28,125,0);
		
		ParticleEffectInterpreter magic = new ParticleEffectInterpreter();
		magic.setEffectName(StringConstants.get("PROPERTIES.ROCKET.EFFECT_MAGIC","Magic"));
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
		
		ParticleEffectInterpreter plasma = new ParticleEffectInterpreter();
		plasma.setEffectName(StringConstants.get("PROPERTIES.ROCKET.EFFECT_PLASMA","Plasma"));
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
		
		ParticleEffectInterpreter smoke = new ParticleEffectInterpreter();
		smoke.setEffectName(StringConstants.get("PROPERTIES.ROCKET.EFFECT_SMOKE","Smoke"));
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
	
		ParticleEffectInterpreter rings = new ParticleEffectInterpreter();
		rings.setEffectName(StringConstants.get("PROPERTIES.ROCKET.EFFECT_RINGS","Rings"));
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
		
/*		ParticleEffectInterpreter alien = new ParticleEffectInterpreter();
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
		
		presets.add(new ParticleEffectInterpreter( RocketPropellantInterpreter.getDefaultParticleEffect()));
		
		ParticleEffectInterpreter saturn = new ParticleEffectInterpreter();
		saturn.setEffectName(StringConstants.get("PROPERTIES.ROCKET.EFFECT_SAT_V","Saturn V"));
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
		
		ParticleEffectInterpreter sputter = new ParticleEffectInterpreter();
		sputter.setEffectName(StringConstants.get("PROPERTIES.ROCKET.EFFECT_SPUTTER","Sputter"));
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
		
		ParticleEffectInterpreter plasma2 = new ParticleEffectInterpreter();
		plasma2.setEffectName(StringConstants.get("PROPERTIES.ROCKET.EFFECT_SPARK","Spark"));
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
		
		
		ParticleEffectInterpreter nova = new ParticleEffectInterpreter();
		nova.setEffectName(StringConstants.get("PROPERTIES.ROCKET.EFFECT_NOVA","Nova"));
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
