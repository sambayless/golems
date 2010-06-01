package com.golemgame.properties.fengGUI;

import org.apache.commons.math.analysis.PolynomialFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.fenggui.Button;
import org.fenggui.CheckBox;
import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.IToggable;
import org.fenggui.Label;
import org.fenggui.ListItem;
import org.fenggui.TextEditor;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.ITextChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.event.TextChangedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;
import org.fenggui.util.Spacing;

import com.golemgame.functional.FunctionSettings;
import com.golemgame.local.StringConstants;
import com.golemgame.mvc.DoubleType;
import com.golemgame.mvc.PropertyState;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SimplePropertyState;
import com.golemgame.mvc.golems.FunctionSettingsRepositoryInterpreter;
import com.golemgame.mvc.golems.functions.CombinedFunctionInterpreter;
import com.golemgame.mvc.golems.functions.FunctionSettingsInterpreter;
import com.golemgame.mvc.golems.functions.KnottedFunctionInterpreter;
import com.golemgame.mvc.golems.functions.PolynomialFunctionInterpreter;
import com.golemgame.mvc.golems.functions.SineFunctionInterpreter;
import com.golemgame.mvc.golems.functions.SqrtFunctionInterpreter;
import com.golemgame.mvc.golems.functions.FunctionSettingsInterpreter.FunctionType;
import com.golemgame.properties.fengGUI.KnottedFunctionPane.CurrentKnotListener;
import com.golemgame.properties.fengGUI.KnottedFunctionPane.Knot;
import com.golemgame.states.StateManager;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.UndoManager;
import com.golemgame.util.CombinedTransformationFunction;
import com.golemgame.util.KnottedFunction;
import com.golemgame.util.SinFunction;
import com.golemgame.util.SqrtFunction;
import com.jme.math.FastMath;


public class FunctionTab extends PropertyTabAdapter {

	private FunctionSettingsInterpreter interpreter;

	private KnottedFunctionPane pane;
	private ComboBox<PropertyStore> dropDown;
	
	

	private TextEditor time;
	private TextEditor power;
	private  CheckBox<?>  periodicity;
	//private boolean showCalc = false;

	private ScalingRuler horizontal;

	private ScalingRuler vertical;

	private TextEditor functionName;
	private TextEditor currentX;
	private TextEditor currentY;
	private Container currentKnotContainer;
	
	private final boolean zeroBased;
	
	private ListItem<PropertyStore> slopeItem;
	private ListItem<PropertyStore> areaItem;
	private Container periodContainer;
	public FunctionTab(boolean zeroBased) {
		super(StringConstants.get("PROPERTIES.FUNCTION", "Function"));
		
		this.zeroBased = zeroBased;
		

	}
	

	
	public void close(boolean cancel) {
		if(!cancel)
		{	
			if(super.isAltered(dropDown) || isAltered(horizontal) || isAltered(vertical) || isAltered(pane))
			{
				IToggable<PropertyStore> selected = dropDown.getSelectedItem();
				super.setValueAltered(FunctionSettingsInterpreter.FUNCTION_TYPE,true);
				
					if(selected==slopeItem)
					{
						interpreter.setFunctionType(FunctionType.Differentiate);
					
					}else if(selected == areaItem)
					{
						interpreter.setFunctionType(FunctionType.AntiDifferentiate);
					
					
				}else{
				
				PropertyStore baseFunction = invertView(new PropertyStore(), pane.getFunction());
				interpreter.setFunction(baseFunction);
				super.setValueAltered(FunctionSettingsInterpreter.FUNCTION,true);
				super.setValueAltered(FunctionSettingsInterpreter.FUNCTION_TYPE,true);
				//now set the other properties to their original values, in case they arent going to be altered		
				}
			}else
				interpreter.getStore().nullifyKey(FunctionSettingsInterpreter.FUNCTION);
			
			super.standardClosingBehaviour(periodicity, FunctionSettingsInterpreter.PERIODIC);
			super.standardClosingBehaviour(time, FunctionSettingsInterpreter.SCALEX);
			super.standardClosingBehaviour(power, FunctionSettingsInterpreter.SCALEY);
			standardClosingBehaviour(functionName, FunctionSettingsInterpreter.FUNCTION_NAME,Format.String);
			
			Action<?> apply = super.apply();
			
			UndoManager.getInstance().addAction(apply);
		
			/*			
			if((selected != null) && (selected.getText().contains("Slope") || selected.getText().contains("Area")))
			{
				if(selected.getText().contains("Slope"))
				{
					functional.setFunctionType(FunctionType.Differentiate);
				}else
				{
					functional.setFunctionType(FunctionType.AntiDifferentiate);
				}
				
			}else{
				
				functional.setFunctionType(FunctionType.Function);
				functional.setFunction(pane.getFunction());
				
				
				functional.getFunctionSettings().setPeriodic(periodicity.isSelected());
				try{
					float scale = Float.valueOf(power.getText());
					if (scale>0)
					{
						
						functional.getFunctionSettings().setScaleY(scale);
					}
				}catch(NumberFormatException e)
				{
					
				}
				try{
					float scale = Float.valueOf(time.getText());
					if (scale>0)
					{
						functional.getFunctionSettings().setScaleX(scale);
					}
				}catch(NumberFormatException e)
				{
					
				}
			
			}*/
		}else
		{
			//functional.setFunction(originalFunction);
			//functional.setFunctionSettings(originalSettings);
		}
		
	}


	private void setOriginalFunction()
	{
		
		interpreter.getStore().clear();
		interpreter.getStore().set(original);
		FunctionSettings settings = new FunctionSettings( interpreter.getStore());
		pane.setFunction(settings.buildFunction(),settings);
		ListItem<PropertyStore> originalItem = new ListItem<PropertyStore>("Original Function", original) ;
		
		if(dropDown.getList().getItems().get(0).getText().equals("Original Function"))
		{
			dropDown.getList().removeItem(0);
		}
		
		dropDown.getList().getItems().add(0, originalItem);
		
		dropDown.getList().setSelectedIndex(0);
	
		
		dropDown.setSelected(originalItem);
		
		if(interpreter.getFunctionType()==FunctionType.Differentiate)
		{
			this.dropDown.setSelected(StringConstants.get("PROPERTIES.FUNCTION.SLOPE","(Slope)"));
			functionName.setText(StringConstants.get("PROPERTIES.FUNCTION.SLOPE","(Slope)"));
			pane.setEnabled(false);
		}else if (interpreter.getFunctionType() == FunctionType.AntiDifferentiate)
		{
			pane.setEnabled(false);
			this.dropDown.setSelected(StringConstants.get("PROPERTIES.FUNCTION.AREA","(Area)"));
			functionName.setText(StringConstants.get("PROPERTIES.FUNCTION.AREA","(Area)"));
		}
	}
	

	protected void buildGUI()
	{
		Container tabFrame = super.getTab();
		tabFrame.setLayoutManager(new BorderLayout());
        
		 pane = new KnottedFunctionPane();		 
	
		//Add an internal frame with controls for choosing a function
		Container controlFrame = FengGUI.createContainer(tabFrame);
		controlFrame.setLayoutData(BorderLayoutData.NORTH);
		controlFrame.setLayoutManager(new BorderLayout());
		
		Container controlsNorth = FengGUI.createContainer(controlFrame);
		controlsNorth.setLayoutData(BorderLayoutData.NORTH);
		controlsNorth.setLayoutManager(new RowLayout(false));
		dropDown = FengGUI.<PropertyStore>createComboBox(controlsNorth);
		
		dropDown.setLayoutData(BorderLayoutData.NORTH);
		dropDown.getAppearance().setPadding(new Spacing(0,5));
		
		Container nameContainer = FengGUI.createContainer(controlsNorth);
		nameContainer.setLayoutManager(new BorderLayout());
		Label nameLabel = FengGUI.createLabel(nameContainer,StringConstants.get("PROPERTIES.FUNCTIONS.NAME","Function Name "));
		nameLabel.setLayoutData(BorderLayoutData.WEST);
		functionName = FengGUI.createTextEditor(nameContainer);
		functionName.setMinSize(300, 10);
		functionName.setLayoutData(BorderLayoutData.CENTER);
		
		controlFrame.layout();
		
		dropDown.addSelectionChangedListener(new ISelectionChangedListener()
		{

			
			public void selectionChanged(
					SelectionChangedEvent selectionChangedEvent) 
			{
				
				if (selectionChangedEvent.isSelected())
				{
					IToggable<PropertyStore> selected = dropDown.getSelectedItem();
				
					if (selected == slopeItem)
					{
						pane.setEnabled(false);
						interpreter.setFunctionType(FunctionType.Differentiate);
						functionName.setText(StringConstants.get("FUNCTIONS.SLOPE","(Slope)"));
					}else if (selected == areaItem)
					{//disabled
						pane.setEnabled(false);
						interpreter.setFunctionType(FunctionType.AntiDifferentiate);
						functionName.setText(StringConstants.get("FUNCTIONS.AREA","(Area)"));
					}else if (selected != null && selected.getValue() != null)
					{	
						pane.setEnabled(true);
						interpreter.setFunctionType(FunctionType.Function);
						loadFrom(selected.getValue().deepCopy());
						/*FunctionSettingsInterpreter settings = new FunctionSettingsInterpreter(new PropertyStore());
						settings.setFunction(selected.getValue());
						setFunction(settings);*/
					}
				}

			}
			
		});	
		
		
		
		controlFrame.updateMinSize();
	
		controlFrame.pack();
		
        Container functionContainer = FengGUI.createContainer(tabFrame);
        functionContainer.setLayoutData(BorderLayoutData.CENTER);
        functionContainer.setLayoutManager(new BorderLayout());
     //  pane.setMinSize(20, 20);
        functionContainer.addWidget(pane);
       
        pane.setLayoutData(BorderLayoutData.CENTER);
      //  pane.getAppearance().setBorder(new Spacing(5,5));        

  
         horizontal = new ScalingRuler(true,pane);
        horizontal.setLayoutData(BorderLayoutData.SOUTH);
        functionContainer.addWidget(horizontal);
        
        
        Container eastContainer = FengGUI.createContainer(functionContainer);
        eastContainer.setLayoutManager(new BorderLayout());
        eastContainer.setLayoutData(BorderLayoutData.EAST);
         vertical = new ScalingRuler(false,pane);
        vertical.setLayoutData(BorderLayoutData.CENTER);
        eastContainer.addWidget(vertical);
       
        horizontal.getMinSize().setHeight(15);
        horizontal.setSizeToMinSize();
        vertical.getMinSize().setWidth(horizontal.getHeight());
        
        Spacer spacer = new Spacer(1,horizontal.getHeight());
        eastContainer.addWidget(spacer);
        spacer.setLayoutData(BorderLayoutData.SOUTH);

        Container south = FengGUI.createContainer(getTab());
        //Add controls at the bottom
        south.setLayoutData(BorderLayoutData.SOUTH);
        south.setLayoutManager(new RowLayout(false));
        periodContainer = FengGUI.createContainer(south);
       
        
        periodContainer.setLayoutManager(new RowLayout());
        
        FengGUI.createLabel(periodContainer,"Length(s):");
         time = FengGUI.createTextEditor(periodContainer);
       // FengGUI.createLabel(settingsContainer,"Power (w):");
         power = FengGUI.createTextEditor();//dont add power right now.
        
        periodicity = FengGUI.createCheckBox(periodContainer, "Periodic:");
       
        
    	
		final Container saveContainer = FengGUI.createContainer(south);
		
		saveContainer.setLayoutManager(new BorderLayout());
		Container saveButtonContainer = FengGUI.createContainer(saveContainer);
		saveButtonContainer.setLayoutData(BorderLayoutData.EAST);
		saveButtonContainer.setLayoutManager(new RowLayout());

		Button saveButton = FengGUI.createButton(saveButtonContainer);
		
		saveButton.setText("Save Function");
	
		saveButton.addButtonPressedListener(new IButtonPressedListener()
		{

			@SuppressWarnings("unchecked")
			public void buttonPressed(ButtonPressedEvent e) {
				
				PropertyStore newFunction = new PropertyStore();
				set(newFunction);
				PropertyStore effectStore =StateManager.getMachineSpace().getFunctionRepository().getStore();
				//capture the current settings, then add them to the main machine repository (overwriting any existing setting of the same name).
				final PropertyState beforePropertyState = new SimplePropertyState(effectStore, FunctionSettingsRepositoryInterpreter.FUNCTIONS);
				StateManager.getMachineSpace().getFunctionRepository().addFunction(newFunction);
				
				final	PropertyState afterPropertyState = new SimplePropertyState(effectStore, FunctionSettingsRepositoryInterpreter.FUNCTIONS);
				
				Action<?> action = new Action()
				{

					@Override
					public String getDescription() {
						return "Save Function";
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
				
				buildFunctions();
				
				dropDown.getLabel().setText(newFunction.getString(FunctionSettingsInterpreter.FUNCTION_NAME));
				
				loadFrom(newFunction.deepCopy());
				
			}
			
		});
		
		
		currentKnotContainer = FengGUI.createContainer(saveContainer);
		currentKnotContainer.setVisible(false);
		currentKnotContainer.setLayoutManager(new RowLayout(true));
		FengGUI.createLabel(currentKnotContainer,"Vertex Position: ").setExpandable(false);
		FengGUI.createLabel(currentKnotContainer,"X").setExpandable(false);
		currentX = FengGUI.createTextEditor(currentKnotContainer);
		FengGUI.createLabel(currentKnotContainer,"Y").setExpandable(false);
		currentY = FengGUI.createTextEditor(currentKnotContainer);
		
		currentY.addTextChangedListener(new ITextChangedListener(){

			public void textChanged(TextChangedEvent textChangedEvent) {
				try{
					float pos;
					float scaleY = (float)pane.getTransformFunction().getScaleY();
					if(scaleY != 0f &&! Float.isInfinite(scaleY) &&! Float.isNaN(scaleY))
					{
						pos  =( Float.valueOf((currentY.getText())) - (float) pane.getTransformFunction().getTranslateY())*(float)pane.getTransformFunction().getScaleY();

					}else
					{
						return;
						//pos = (float) pane.getTransformFunction().getTranslateY();
					}
					
					
					Knot cur = pane.getCurrent();
					if(cur!=null)
					{
						if(cur.getTranslation().y != pos){
							cur.getTranslation().y = pos;
							pane.updateKnots();
						}
					}	
				}catch(NumberFormatException e)
				{
					
				}
			}
			
		});
		
		currentX.addTextChangedListener(new ITextChangedListener(){

			public void textChanged(TextChangedEvent textChangedEvent) {
				try{
					float pos;
					float scaleX = (float)pane.getTransformFunction().getScaleX();
					if(scaleX != 0f &&! Float.isInfinite(scaleX) &&! Float.isNaN(scaleX))
					{
						pos  =( Float.valueOf((currentX.getText())) - (float) pane.getTransformFunction().getTranslateX())*(float)pane.getTransformFunction().getScaleX();

					}else
					{
						return;
					
					}					
					Knot cur = pane.getCurrent();
					if(cur!=null)
					{
						if(cur.getTranslation().x != pos){
							cur.getTranslation().x = pos;
							pane.updateKnots();
						}
					}
				}catch(NumberFormatException e)
				{
					
				}
			}
			
		});
		
		pane.registerCurrentKnotListener(new CurrentKnotListener() {
			
			public void currentKnotChanged(Knot current) {
				if(current==null)
				{
					currentKnotContainer.setVisible(false);
				}else{
					currentKnotContainer.setVisible(true);
					float scaleX = (float)pane.getTransformFunction().getScaleX();
					if(scaleX != 0f &&! Float.isInfinite(scaleX) &&! Float.isNaN(scaleX))
						scaleX = 1f/scaleX;
					else
						scaleX = 0f;
					
					float scaleY = (float)pane.getTransformFunction().getScaleY();
					if(scaleY != 0f &&! Float.isInfinite(scaleY) &&! Float.isNaN(scaleY))
						scaleY = 1f/scaleY;
					else
						scaleY = 0f;
					
					currentX.setText(String.valueOf( current.getTranslation().x*scaleX + pane.getTransformFunction().getTranslateX()));
					currentY.setText(String.valueOf( current.getTranslation().y*scaleY + pane.getTransformFunction().getTranslateY()));

					saveContainer.layout();
				}
				
			}
		});
        functionContainer.pack();
        tabFrame.pack();
	}




	public KnottedFunctionPane getPane() {
		return pane;
	}

	private void loadFrom(PropertyStore store)
	{
		interpreter.getStore().clear();
		interpreter.getStore().set(store);
		
		functionName.setText(interpreter.getFunctionName());
		
		power.setText(String.valueOf( interpreter.getScaleY()));
		time.setText(String.valueOf( interpreter.getScaleX()));
		
		periodicity.setSelected(interpreter.isPeriodic());
		
		dropDown.getLabel().setText(interpreter.getFunctionName());
		
		
		setFunction(new FunctionSettings( interpreter.getStore()));
		
	}

	private void set(PropertyStore store)
	{
		FunctionSettingsInterpreter interp = new FunctionSettingsInterpreter(store);
		interp.setFunctionName(functionName.getText());
		
		try{
			interp.setScaleX(Float.valueOf( time.getText()));
		}catch(NumberFormatException e){}
		
		try{
			interp.setScaleY(Float.valueOf( power.getText()));
		}catch(NumberFormatException e){}
		
		interp.setPeriodic(periodicity.isSelected());
		
		PropertyStore baseFunction = invertView(new PropertyStore(), pane.getFunction());
		interp.setFunction(baseFunction);
	}
	
	private void setFunction(FunctionSettings f)
	{
		if(f.getFunctionType() != FunctionType.Function)
		{
			pane.setEnabled(false);
			vertical.setEnabled(false);
			horizontal.setEnabled(false);
		}else					
	 	{
			pane.setEnabled(true);
			vertical.setEnabled(true);
			horizontal.setEnabled(true);
		
			pane.setFunction(f.buildFunction(),f);
	 	}
	}
	
	public void showCalcFunctions()
	{
		slopeItem=new ListItem<PropertyStore>("(Slope)");
		areaItem=new ListItem<PropertyStore>("(Area)");
		this.dropDown.addItem(slopeItem);
		//this.dropDown.addItem(areaItem);
			
	}
	
	private PropertyStore original;
	
	public void open() {
		this.interpreter = new FunctionSettingsInterpreter( getPrototype());
		this.interpreter.loadDefaults();
		initializePrototype();
		
	
		
		associateWithKey(periodicity, FunctionSettingsInterpreter.PERIODIC);
		associateWithKey(power, FunctionSettingsInterpreter.SCALEY);
		associateWithKey(time, FunctionSettingsInterpreter.SCALEX);
		associateWithKey(functionName, FunctionSettingsInterpreter.FUNCTION_NAME);
		
		standardOpeningBehaviour(periodicity, FunctionSettingsInterpreter.PERIODIC);
		standardOpeningBehaviour(power, FunctionSettingsInterpreter.SCALEY);
		standardOpeningBehaviour(time, FunctionSettingsInterpreter.SCALEX);
		standardOpeningBehaviour(functionName, FunctionSettingsInterpreter.FUNCTION_NAME,Format.String);
		//require all of these to be the same, for obvious reasons...
		
		periodContainer.setVisible(zeroBased);
		
		
/*		try {
			
			FloatType maxX = (FloatType) getPropertyStoreAdjuster().getFirstValue(FunctionInterpreter.MAX_X);
			FloatType minY = (FloatType) getPropertyStoreAdjuster().getFirstValue(FunctionInterpreter.MIN_Y);
			FloatType maxY = (FloatType) getPropertyStoreAdjuster().getFirstValue(FunctionInterpreter.MAX_Y);
			
			
		
			
			
		} catch (NoValueException e) {
			//gray out the function pane
			dropDown.getLabel().setText("(Multiple Values)");
		}catch(ClassCastException e)
		{
			dropDown.getLabel().setText("(Multiple Values)");
		}*/
		
/*		boolean isZeroBased = false;
		try {
			FloatType minX = (FloatType) getPropertyStoreAdjuster().getFirstValue(FunctionSettingsInterpreter.MIN_X);
			if(Math.abs( minX.getValue()) < FastMath.FLT_EPSILON)
			{
				isZeroBased = true;	
			}
		} catch (NoValueException e) {
			//gray out the function pane
			dropDown.getLabel().setText("(Multiple Values)");
		}
		*/
		if(zeroBased)
		{
			this.pane.setMinX(0);
			this.horizontal.setAxis(new String[]{"0", "0.5","1"});
			interpreter.setMinX(0);
		}else
		{
			this.pane.setMinX(-1);
			interpreter.setMinX(-1);
		}
	
			
		buildFunctions();
		if(!zeroBased)
		{
			this.time.setVisible(false);
			this.periodicity.setVisible(false);
		}

		original =  super.getPropertyStoreAdjuster().getFirstStore().deepCopy();
	

		setOriginalFunction();
		super.setUnaltered(dropDown);
		super.setUnaltered(horizontal);
		super.setUnaltered(vertical);
		super.setUnaltered(pane);
		//setFunction( interpreter);
		
	/*	FunctionSettings settings = functional.getFunctionSettings();
		periodicity.setSelected(settings.isPeriodic());
		time.setText(String.valueOf(settings.getScaleX()));
		power.setText(String.valueOf(settings.getScaleY()));

		
		originalSettings = new FunctionSettings(settings.getStore().deepCopy());
		originalSettings.refresh();
		setOriginalFunction(functional.getFunction());
		
		if(functional.getFunctionType()==FunctionType.Differentiate)
		{
			this.dropDown.setSelected("(Slope)");
		}else if (functional.getFunctionType() == FunctionType.AntiDifferentiate)
		{
			this.dropDown.setSelected("(Area)");
		}*/
	}
	


	private PropertyStore buildStandardFunction(UnivariateRealFunction f,String name, boolean zeroBased)
	{
		
		CombinedTransformationFunction fun = new CombinedTransformationFunction();
		fun.setBaseFunction(f);
		
		if (zeroBased)
		{
			fun.setScaleX(2f);
			fun.setTranslateX(0.5f);
		}
		
		
		PropertyStore fStore = invertView(new PropertyStore(), fun);
		FunctionSettingsInterpreter interp = new FunctionSettingsInterpreter(new PropertyStore());
		interp.setFunction(fStore);
		interp.setFunctionName(name);
		return interp.getStore();
		
	}
	
	private void buildFunctions()
	{
		dropDown.getList().clear();
		
		
		
		//float functionWidth = 1;//pane.getMaxX()-pane.getMinX();
		//float functionHeight = 2;///pane.getMaxY()-pane.getMinY();
		
		ListItem<PropertyStore> line;
		ListItem<PropertyStore> flat;
		ListItem<PropertyStore> on ;
		ListItem<PropertyStore> off; 
		ListItem<PropertyStore> parabola;
		ListItem<PropertyStore> cubic ;
		ListItem<PropertyStore> sin; 
		ListItem<PropertyStore> cos; 
		ListItem<PropertyStore> sqrt; 
	//	Collection<PropertyStore> functions = new ArrayList<PropertyStore>();
		
	//	if(zeroBased)
		{
			
			
			line = new ListItem<PropertyStore>(StringConstants.get("PROPERTIES.FUNCTION.FUN_LINE","Line"), buildStandardFunction(  new PolynomialFunction(new double[]{0,1}),"Line",zeroBased));  ///FunctionFactory.invertView(new PropertyStore(), ); );
			
			flat = new ListItem<PropertyStore>(StringConstants.get("PROPERTIES.FUNCTION.FUN_ZERO","Zero"), buildStandardFunction( new PolynomialFunction(new double[]{0}),"Zero",zeroBased));  ///FunctionFactory.invertView(new PropertyStore(), ); );
			
			on = new ListItem<PropertyStore>(StringConstants.get("PROPERTIES.FUNCTION.FUN_100","100%"), buildStandardFunction(new PolynomialFunction(new double[]{1}),"100%",zeroBased));  ///FunctionFactory.invertView(new PropertyStore(), ); );
			
			off = new ListItem<PropertyStore>(StringConstants.get("PROPERTIES.FUNCTION.FUN_NEG_100","-100%"), buildStandardFunction( new PolynomialFunction(new double[]{-1}),"-100%",zeroBased));  //FunctionFactory.invertView(new PropertyStore(), ); );
			
			parabola = new ListItem<PropertyStore>(StringConstants.get("PROPERTIES.FUNCTION.FUN_PARABOLA","Parabolic"), buildStandardFunction( new PolynomialFunction(new double[]{0,0,1}),"Parabolic",zeroBased));  ///FunctionFactory.invertView(new PropertyStore(), ); );
			
			cubic = new ListItem<PropertyStore>(StringConstants.get("PROPERTIES.FUNCTION.FUN_CUBIC","Cubic"), buildStandardFunction( new PolynomialFunction(new double[]{0,0,0,1}),"Cubic",zeroBased));  ///FunctionFactory.invertView(new PropertyStore(), ); );
			
			sin = new ListItem<PropertyStore>(StringConstants.get("PROPERTIES.FUNCTION.FUN_SINE","Sine"), buildStandardFunction( new SinFunction(1f, 1f/(2f*FastMath.PI),0),"Sine",false));  ///FunctionFactory.invertView(new PropertyStore(), ); );
			
			cos = new ListItem<PropertyStore>(StringConstants.get("PROPERTIES.FUNCTION.FUN_COS","Cosine"), buildStandardFunction( new SinFunction(1f, 1f/(2f*FastMath.PI),FastMath.HALF_PI),"Cosine",false));  ///FunctionFactory.invertView(new PropertyStore(), ); );
			
			sqrt = new ListItem<PropertyStore>(StringConstants.get("PROPERTIES.FUNCTION.FUN_SQRT","Square Root"), buildStandardFunction( new SqrtFunction(),"Square Root",false));  ///FunctionFactory.invertView(new PropertyStore(), ); );
			
			
		/*	flat = new ListItem<PropertyStore>("Zero", FunctionFactory.invertView(new PropertyStore(),  new PolynomialFunction(new double[]{0}) ));

		 	on = new ListItem<PropertyStore>("100%",  FunctionFactory.invertView(new PropertyStore(), new PolynomialFunction(new double[]{functionHeight}) ));

		 	off = new ListItem<PropertyStore>("-100%", FunctionFactory.invertView(new PropertyStore(),  new PolynomialFunction(new double[]{-functionHeight})) );

		 	parabola = new ListItem<PropertyStore>("Parabolic",  FunctionFactory.invertView(new PropertyStore(),  LinearUtils.generateTranslatedFunction(new double[]{0,0,functionHeight*2f/functionWidth},functionWidth/2f)));
	
			cubic = new ListItem<PropertyStore>("Cubic", FunctionFactory.invertView(new PropertyStore(),  LinearUtils.generateTranslatedFunction(new double[]{0,0,0,functionHeight*4f/functionWidth},functionWidth/2f)));
		
		 	sin = new ListItem<PropertyStore>("Sine", FunctionFactory.invertView(new PropertyStore(),  new SinFunction(functionHeight/2f, functionWidth/(2f*FastMath.PI),0)) );
		
		 	cos = new ListItem<PropertyStore>("Cosine",  FunctionFactory.invertView(new PropertyStore(), new SinFunction(functionHeight/2f, functionWidth/(2f*FastMath.PI),FastMath.HALF_PI)));
*/		}//else
		{
		/*	line = new ListItem<PropertyStore>("Line", FunctionFactory.invertView(new PropertyStore(),  new PolynomialFunction(new double[]{0,1})) );
			
			flat = new ListItem<PropertyStore>("Zero", FunctionFactory.invertView(new PropertyStore(),  new PolynomialFunction(new double[]{0}) ));

		 	on = new ListItem<PropertyStore>("100%", FunctionFactory.invertView(new PropertyStore(),  new PolynomialFunction(new double[]{functionHeight})) );

		 	off = new ListItem<PropertyStore>("-100%",  FunctionFactory.invertView(new PropertyStore(), new PolynomialFunction(new double[]{-functionHeight}) ));

		 	parabola = new ListItem<PropertyStore>("Parabolic", FunctionFactory.invertView(new PropertyStore(),   LinearUtils.generateTranslatedFunction(new double[]{0,0,functionHeight*0.5f/functionWidth},0)));
	
			cubic = new ListItem<PropertyStore>("Cubic",  FunctionFactory.invertView(new PropertyStore(), LinearUtils.generateTranslatedFunction(new double[]{0,0,0,functionHeight*0.5f/functionWidth},0)));
		
		 	sin = new ListItem<PropertyStore>("Sine",  FunctionFactory.invertView(new PropertyStore(), new SinFunction(functionHeight/2f, functionWidth/(FastMath.PI),0)) );
		
		 	cos = new ListItem<PropertyStore>("Cosine", FunctionFactory.invertView(new PropertyStore(),  new SinFunction(functionHeight/2f, functionWidth/(FastMath.PI),FastMath.HALF_PI) ));
*/
		}
		dropDown.addItem(line);
		dropDown.addItem(on);
		dropDown.addItem(flat);
		dropDown.addItem(off);
		dropDown.addItem(parabola);
		dropDown.addItem(cubic);
		dropDown.addItem(sin);
		dropDown.addItem(cos);
		dropDown.addItem(sqrt);
		if(!zeroBased)
		{
			 showCalcFunctions();
	
		}
		for(FunctionSettingsInterpreter function:	StateManager.getMachineSpace().getFunctionRepository().getFunctions())
		{
			 ListItem<PropertyStore> f = new ListItem<PropertyStore>(function.getFunctionName(), function.getStore());
			 dropDown.addItem(f);

		}
	}


	public void setXAxis(String[] xAxis)
	{
		this.horizontal.setAxis(xAxis);
	}
	public void setYAxis(String[] yAxis)
	{
		this.vertical.setAxis(yAxis);
	}
	
	

	public static PropertyStore invertView(PropertyStore store, UnivariateRealFunction function)
	{
		if (function instanceof PolynomialFunction)
		{
			PolynomialFunction f = (PolynomialFunction)function;
			PolynomialFunctionInterpreter interp = new PolynomialFunctionInterpreter(store);
			for (int i = 0; i <= f.degree();i++)
			{
				interp.setCoefficient(i, new DoubleType( f.getCoefficients()[i]));
			}
		}else if (function instanceof SinFunction)
		{
			SinFunction f= (SinFunction) function;
			SineFunctionInterpreter interp = new SineFunctionInterpreter(store);
			interp.setAmplitude(f.getAmplitude());
			interp.setLambda(f.getLambda());
			interp.setPhase(f.getPhase());
		}else if (function instanceof SqrtFunction)
		{
			SqrtFunction f= (SqrtFunction) function;
			SqrtFunctionInterpreter interp = new SqrtFunctionInterpreter(store);
	
		}else if (function instanceof CombinedTransformationFunction)
		{
			//finally, the combined knotted function will be recursively defined.
			CombinedTransformationFunction f = (CombinedTransformationFunction) function;
			CombinedFunctionInterpreter interp = new CombinedFunctionInterpreter(store);
			interp.setBottom(f.getBottom());
			interp.setTop(f.getTop());
			interp.setLeft(f.getLeft());
			interp.setRight(f.getRight());
			interp.setLocalScaleX(f.getScaleX());
			interp.setLocalScaleY(f.getScaleY());
			interp.setTranslateX(f.getTranslateX());
			interp.setTranslateY(f.getTranslateY());
			
			invertView(interp.getFunction1(),f.getBaseFunction());
			invertView(interp.getFunction2(),f.getKnottedFunction());
		}else if (function instanceof KnottedFunction)
		{
			KnottedFunction f = (KnottedFunction)function;
			KnottedFunctionInterpreter interp = new  KnottedFunctionInterpreter(store);
	
			for ( int i = 0; i < f.count();i++)
			{
				interp.setKnot(i, f.getXKnots()[i], f.getYKnots()[i]);
			}
			
		}else
		{
			StateManager.getLogger().warning("Unrecognized Function");
		}
		
		return store;
	}
}

