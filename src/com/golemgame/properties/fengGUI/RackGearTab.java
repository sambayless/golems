package com.golemgame.properties.fengGUI;

import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.TextEditor;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.GridLayout;

import com.golemgame.local.StringConstants;
import com.golemgame.model.Model;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.CylinderInterpreter;
import com.golemgame.mvc.golems.GearInterpreter;
import com.golemgame.mvc.golems.GolemsClassRepository;
import com.golemgame.mvc.golems.RackGearInterpreter;
import com.golemgame.states.StateManager;
import com.golemgame.structural.structures.GearStructure;
import com.golemgame.tool.SelectionListener;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.UndoManager;
import com.golemgame.tool.action.information.PropertyStoreInformation;
import com.jme.math.FastMath;

public class RackGearTab extends PropertyTabAdapter {

	//private Collection<PropertyStore> properties = new ArrayList<PropertyStore>();

	private RackGearInterpreter interpreter;
	
	//private TextEditor toothNumber;
	private TextEditor toothHeight;
	private TextEditor toothAngle;
	private TextEditor toothWidth;
	//private TextEditor toothSpacing;
	
	private Button match;
	
	public RackGearTab() {
		super(StringConstants.get("TOOLBAR.RACKGEAR","Rack Gear"));		
	}

	@Override
	protected void buildGUI() {
		
		getTab().setLayoutManager(new BorderLayout());
		
		Container toothContainer = FengGUI.createContainer(getTab());
		toothContainer.setLayoutData(BorderLayoutData.NORTH);
		toothContainer.setLayoutManager(new GridLayout(4,2));
	/*	FengGUI.createLabel(toothContainer, "Number of Teeth");
		toothNumber = FengGUI.createTextEditor();*/
		
		FengGUI.createLabel(toothContainer, StringConstants.get("PROPERTIES.GEAR.TOOTH_WIDTH","Tooth Width"));
		toothWidth = FengGUI.createTextEditor(toothContainer);
		
		FengGUI.createLabel(toothContainer, StringConstants.get("PROPERTIES.GEAR.TOOTH_HEIGHT","Tooth Height"));
		toothHeight = FengGUI.createTextEditor(toothContainer);
		
		FengGUI.createLabel(toothContainer, StringConstants.get("PROPERTIES.GEAR.TOOTH_ANGLE","Tooth Angle"));
		toothAngle = FengGUI.createTextEditor(toothContainer);
		
	/*	FengGUI.createLabel(toothContainer, StringConstants.get("PROPERTIES.GEAR.TOOTH_SPACING","Tooth Spacing"));
		toothSpacing = FengGUI.createTextEditor(toothContainer);		
		*/
		match = FengGUI.createButton(toothContainer,StringConstants.get("PROPERTIES.GEAR.MATCH", "Match Gear"));
		FengGUI.createLabel(toothContainer,StringConstants.get("PROPERTIES.GEAR.MATCH.DESCRIPTION", "Select a gear to copy properties from so they can mesh properly."));

		match.addButtonPressedListener(new IButtonPressedListener(){

			public void buttonPressed(ButtonPressedEvent e) {
				if(!StateManager.getToolManager().hasListener(matchListener))
				{
					StateManager.getToolManager().attachListener(matchListener);
				}
			}
			
		});
		
	
	}

	private SelectionListener matchListener = new SelectionListener(){

		public boolean select(Actionable actionable, Model model) {
			
			try
			{
				PropertyStoreInformation info = (PropertyStoreInformation) actionable.getAction(Action.PROPERTY_STORE);
				PropertyStore store = info.getStore();			
				if(store!=null)
/*						&& store.hasProperty(GearInterpreter.TOOTH_ANGLE, Type.FLOAT)
						&& store.hasProperty(GearInterpreter.TOOTH_WIDTH, Type.FLOAT)			
						&& store.hasProperty(GearInterpreter.TOOTH_HEIGHT, Type.FLOAT))		*/		
				{
					if(store.getClassName().equalsIgnoreCase(GolemsClassRepository.GEAR_CLASS))
					{//its a circular gear:
						
						double width = store.getFloat(GearInterpreter.TOOTH_WIDTH);
						double height = store.getFloat(GearInterpreter.TOOTH_HEIGHT);
						double angle = store.getFloat(GearInterpreter.TOOTH_ANGLE);
						double radius = store.getFloat(CylinderInterpreter.CYL_RADIUS);
						double newToothHeight = height;
						
						if(radius ==0)
							return true;
						
						
						if(angle>0.0)
						{
							double theta;
						
							double fullWidth = width*2.0 + Math.tan(angle/2.0)*height*2.0;
							double numberOfTeeth;
							if(width == 0 && angle == 0 )
							{
								numberOfTeeth = 0;	//remove all teeth
								return true;//nothing to do here
							}else
								numberOfTeeth =(int)Math.floor( 2f*FastMath.PI*radius/fullWidth);//interpreter.getNumberOfTeeth();
							
							//float deltaH = GearStructure.exactHeight(height,width,angle,radius);
							
							/**
							 * R is the radial distance from the center of the cylinder to the edge of the tooth (NOT the center).
							 */
							double R = (float) Math.sqrt( (radius + height)*(radius + height) + width*width/4f) ;
													
							//we need to find the intersection between the (angled) side of the gear and
							//the cylinder to find out the arc that is subtended by that section.
							//formula adapted from http://mathworld.wolfram.com/Circle-LineIntersection.html
		/*					float dx = R;
							float dy = (float) Math.tan(angle)*R;
							float dr = (float) Math.sqrt(dx*dx + dy*dy);
							float D = (float) (-R *  Math.tan(angle)*R);
							
							float intersectionX = (float)( D*dy*dx*Math.sqrt(radius*radius*dr*dr - D*D)) / (dr*dr);
							float intersectionY = -D*dx*Math.abs(dy)*Math.sqrt(radius*radius*dr*dr - D*D)/(dr*dr);*/
							
							//equation for the line of the gear edge
							double m = Math.tan(angle/2.0);
							double b = m*R;//y intersect is slope times BIG R.
							
							//y = mx + b;
							//so intersection with circle is at mx+b = sqrt(r^2 - x^2)
							// -> 0 = (m^2 + 1)*x^2 + 2mb(x) + (b^2-r^2)
							//quadratic formula
							//we want the negative x intersect.
							double xIntersect = quadraticFormula(m*m+1.0,2.0*m*b,b*b-radius*radius);
							if(Double.isNaN(xIntersect))
								xIntersect = 0.0;
							//float yIntersect = m*xIntersect+b;
							double radiansPerToothAngle = Math.abs(Math.acos(Math.abs(xIntersect)/radius));
							
							
							
							double radiansPerTooth = FastMath.TWO_PI/((double)numberOfTeeth);
							
							//Radians per tooth width at BASE of tooth
							double radiansPerToothWidth =2.0*Math.abs( Math.atan(width/(2.0*radius)) );
							
						//	float radiansPerToothAngle =  (float)(2.0* Math.asin(width/(R*2f)));
							
							theta = radiansPerTooth - radiansPerToothAngle*2.0 - radiansPerToothWidth;
							
							//ok, so theta should be the width of a tooth
							
							//ok, so the width of the base of our tooth (including angled edges) should be 2*R*sin(theta/2)
							double baseWidth = 2.0*(radius)*Math.tan(theta/2f);
							//double baseAngleWidth = Math.tan(angle/2.0)*newToothHeight;
							
						//	double newWidth = baseWidth-2.0*baseAngleWidth;
							
							toothHeight.setText(String.valueOf(newToothHeight));
							toothAngle.setText(String.valueOf(fromRadians((float)angle)));
							toothWidth.setText(String.valueOf(baseWidth));
						}
					}else if( store.getClassName().equalsIgnoreCase(GolemsClassRepository.RACK_GEAR_CLASS))
					{//its a rack gear
						//just match that gears spacing
						
						toothHeight.setText(String.valueOf(store.getFloat(RackGearInterpreter.TOOTH_HEIGHT)));
						toothAngle.setText(String.valueOf(fromRadians(store.getFloat(RackGearInterpreter.TOOTH_ANGLE))));
						//toothAngle.setText(String.valueOf(store.getFloat(RackGearInterpreter.TOOTH_ANGLE)));
						toothWidth.setText(String.valueOf(store.getFloat(RackGearInterpreter.TOOTH_WIDTH)));
					//	toothSpacing.setText(String.valueOf(store.getFloat(RackGearInterpreter.TOOTH_SPACING)));
						
						
					}
					
					
				}
			}catch(ActionTypeException e)
			{
				
			}
			
			
			StateManager.getToolManager().removeListener(this);
			return true;
		}
		
	};
	
	public static double quadraticFormula(double a, double b, double c) {
		//positive only, if it exists
		double det = (b*b)- 4.0*a*c;
		if(det<0.0)
			return Double.NaN;
		
		return (-b - Math.sqrt(det))/(2.0*a);
	}


	@Override
	public void close(boolean cancel) {
		StateManager.getToolManager().removeListener(matchListener);
		if(!cancel)
		{

			standardClosingBehaviour(toothAngle, RackGearInterpreter.TOOTH_ANGLE,Format.Angle);
			
			if(super.isAltered(toothAngle))
			{
				if(interpreter.getToothAngle()<0)
					interpreter.setToothAngle(0f);
				else if (interpreter.getToothAngle()>FastMath.HALF_PI)
					interpreter.setToothAngle(FastMath.HALF_PI);
				
			}
			standardClosingBehaviour(toothHeight, RackGearInterpreter.TOOTH_HEIGHT);
			standardClosingBehaviour(toothWidth, RackGearInterpreter.TOOTH_WIDTH);
		//	standardClosingBehaviour(toothSpacing, RackGearInterpreter.TOOTH_SPACING);
			
			
			Action<?> apply = super.apply();
			
			UndoManager.getInstance().addAction(apply);
			
	
		}
	}



	@Override
	public void open() {
		this.interpreter = new RackGearInterpreter(getPrototype());
		this.interpreter.loadDefaults();
		initializePrototype();
		
		super.associateWithKey(toothAngle, RackGearInterpreter.TOOTH_ANGLE);
		super.associateWithKey(toothWidth, RackGearInterpreter.TOOTH_WIDTH);
		super.associateWithKey(toothHeight, RackGearInterpreter.TOOTH_HEIGHT);
//		super.associateWithKey(toothSpacing, RackGearInterpreter.TOOTH_SPACING);
		
		super.standardOpeningBehaviour(toothAngle, RackGearInterpreter.TOOTH_ANGLE,Format.Angle);
		super.standardOpeningBehaviour(toothWidth, RackGearInterpreter.TOOTH_WIDTH);
		super.standardOpeningBehaviour(toothHeight, RackGearInterpreter.TOOTH_HEIGHT);
//		super.standardOpeningBehaviour(toothSpacing, RackGearInterpreter.TOOTH_SPACING);
		
	
	}
}
