package com.golemgame.properties.fengGUI;

import java.util.concurrent.Callable;

import org.fenggui.CheckBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.TextEditor;
import org.fenggui.event.mouse.MouseAdapter;
import org.fenggui.event.mouse.MousePressedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.GridLayout;
import org.fenggui.layout.RowLayout;
import org.fenggui.util.Color;

import com.golemgame.local.StringConstants;
import com.golemgame.menu.color.ColorPatch;
import com.golemgame.menu.color.ColorWindow;
import com.golemgame.menu.color.ColorDialog.ColorDialogListener;
import com.golemgame.mvc.golems.GrappleInterpreter;
import com.golemgame.states.GUILayer;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;
import com.jme.util.GameTaskQueueManager;

public class GrappleTab extends PropertyTabAdapter {


	private GrappleInterpreter interpreter;
	
	//private TextEditor toothNumber;
	private TextEditor maxDistance;
	private TextEditor maxForce;
	private ColorPatch beamColor;
	private ColorPatch beamCollideColor;
	private CheckBox<?> beamEnabled;
	//private CheckBox<?> ignoreStatics;
	private CheckBox<?> luminous;
	
	public GrappleTab() {
		super(StringConstants.get("TOOLBAR.FORCE_BEAM","Force Beam"));
		
	}

	@Override
	protected void buildGUI() {
		
		getTab().setLayoutManager(new BorderLayout());
		
		Container grappleProperties = FengGUI.createContainer(getTab());
		grappleProperties.setLayoutData(BorderLayoutData.NORTH);
		grappleProperties.setLayoutManager(new GridLayout(4,2));

		
		FengGUI.createLabel(grappleProperties, StringConstants.get("PROPERTIES.FORCE_BEAM.MAX_FORCE","Max Grapple/Push Force"));
		maxForce = FengGUI.createTextEditor(grappleProperties);
		
		FengGUI.createLabel(grappleProperties, StringConstants.get("PROPERTIES.FORCE_BEAM.BEAM_LENGTH","Beam Length"));
		maxDistance = FengGUI.createTextEditor(grappleProperties);

	//	ignoreStatics = FengGUI.createCheckBox(grappleProperties,"Ignore Static Objects");
	//	FengGUI.createLabel(grappleProperties,"");
		
		beamEnabled = FengGUI.createCheckBox(grappleProperties,  StringConstants.get("PROPERTIES.FORCE_BEAM.SHOW_BEAM","Show Beam"));
		Container colorContainer = FengGUI.createContainer(grappleProperties);
		colorContainer.setLayoutManager(new RowLayout());
		FengGUI.createLabel(colorContainer,  StringConstants.get("PROPERTIES.FORCE_BEAM.COLOR_PUSH","Push Color")).setShrinkable(true);
		beamColor = new ColorPatch();
		colorContainer.addWidget(beamColor);
		FengGUI.createLabel(colorContainer,  StringConstants.get("PROPERTIES.FORCE_BEAM.COLOR_PULL","Pull Color")).setShrinkable(true);
		beamCollideColor = new ColorPatch();
		colorContainer.addWidget(beamCollideColor);
		
		luminous = FengGUI.createCheckBox(grappleProperties,  StringConstants.get("PROPERTIES.FORCE_BEAM.LUMINOUS_BEAM","Luminous Beam"));
		FengGUI.createLabel(grappleProperties,"");
		
		beamColor.addMouseListener(new MouseAdapter()
	       {
	    	   
				
				public void mousePressed(MousePressedEvent event) {
					final ColorWindow window = ColorWindow.getInstance();
					//have to call this at a later time, otherwise this window stays ontop

					if (window.getOwner() == beamColor)
					{
						window.close();
						
					}else
					{
						
						GameTaskQueueManager.getManager().update(new Callable<Object>()
								{

									
									public Object call() throws Exception {
										window.setOwner(beamColor);
										GUILayer layer = GUILayer.getLoadedInstance();
										Color color = beamColor.getColor();
										window.setInitialColor(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha());
										window.display(layer.getDisplay());
										window.addColorListener(new ColorDialogListener()
										{

											
											public void cancel() {
											}

											
											public void colorChosen(int red, int green, int blue,int alpha) {
												if (window.getOwner() != beamColor)
													return;
												beamColor.setColor(new Color(red, green,blue,alpha));
											}
											
										});
										return null;
									}

								});
					}
				
				}
				
				
	       });
	      
		
		beamCollideColor.addMouseListener(new MouseAdapter()
	       {
	    	   
				
				public void mousePressed(MousePressedEvent event) {
					final ColorWindow window = ColorWindow.getInstance();
					//have to call this at a later time, otherwise this window stays ontop

					if (window.getOwner() == beamCollideColor)
					{
						window.close();
						
					}else
					{
						
						GameTaskQueueManager.getManager().update(new Callable<Object>()
								{

									
									public Object call() throws Exception {
										window.setOwner(beamCollideColor);
										GUILayer layer = GUILayer.getLoadedInstance();
										Color color = beamCollideColor.getColor();
										window.setInitialColor(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha());
										window.display(layer.getDisplay());
										window.addColorListener(new ColorDialogListener()
										{

											
											public void cancel() {
											}

											
											public void colorChosen(int red, int green, int blue,int alpha) {
												if (window.getOwner() != beamCollideColor)
													return;
												beamCollideColor.setColor(new Color(red, green,blue,alpha));
											}
											
										});
										return null;
									}

								});
					}
				
				}
				
				
	       });
	      
	}

	
	


	@Override
	public void close(boolean cancel) {
		if(!cancel)
		{

			standardClosingBehaviour(maxForce, GrappleInterpreter.MAX_FORCE,Format.Float);
			standardClosingBehaviour(maxDistance, GrappleInterpreter.MAX_DISTANCE,Format.Float);
			standardClosingBehaviour(beamColor, GrappleInterpreter.BEAM_COLOR);
			standardClosingBehaviour(beamEnabled, GrappleInterpreter.BEAM_ENABLED);
			standardClosingBehaviour(luminous, GrappleInterpreter.IS_BEAM_LUMINOUS);
		//	standardClosingBehaviour(ignoreStatics, GrappleInterpreter.IGNORE_STATICS);
			standardClosingBehaviour(beamCollideColor, GrappleInterpreter.BEAM_COLOR_COLLIDE);
			
			Action<?> apply = super.apply();
			
			UndoManager.getInstance().addAction(apply);
			
	
		}
	}



	@Override
	public void open() {
		this.interpreter = new GrappleInterpreter(getPrototype());
		this.interpreter.loadDefaults();
		initializePrototype();
		
		super.associateWithKey(maxForce, GrappleInterpreter.MAX_FORCE);
		super.associateWithKey(maxDistance, GrappleInterpreter.MAX_DISTANCE);
	//	super.associateWithKey(ignoreStatics, GrappleInterpreter.IGNORE_STATICS);

		super.associateWithKey(beamCollideColor, GrappleInterpreter.BEAM_COLOR_COLLIDE);
		super.associateWithKey(beamColor, GrappleInterpreter.BEAM_COLOR);
		super.associateWithKey(beamEnabled, GrappleInterpreter.BEAM_ENABLED);
		super.associateWithKey(luminous, GrappleInterpreter.IS_BEAM_LUMINOUS);

		super.standardOpeningBehaviour(maxForce, GrappleInterpreter.MAX_FORCE,Format.Float);		
		super.standardOpeningBehaviour(maxDistance, GrappleInterpreter.MAX_DISTANCE,Format.Float);
		super.standardOpeningBehaviour(beamColor, GrappleInterpreter.BEAM_COLOR);
		super.standardOpeningBehaviour(beamEnabled, GrappleInterpreter.BEAM_ENABLED);
		super.standardOpeningBehaviour(luminous, GrappleInterpreter.IS_BEAM_LUMINOUS);
	//	super.standardOpeningBehaviour(ignoreStatics, GrappleInterpreter.IGNORE_STATICS);
		super.standardOpeningBehaviour(beamCollideColor, GrappleInterpreter.BEAM_COLOR_COLLIDE);
	
	}

}
