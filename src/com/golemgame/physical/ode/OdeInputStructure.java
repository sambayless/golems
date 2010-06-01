package com.golemgame.physical.ode;

import java.util.Map;

import com.golemgame.functional.component.BComponent;
import com.golemgame.functional.component.BGenericSource;
import com.golemgame.functional.component.BMind;
import com.golemgame.instrumentation.AxisControlListener;
import com.golemgame.instrumentation.Instrument;
import com.golemgame.instrumentation.KeyboardControlListener;
import com.golemgame.instrumentation.KeyboardController;
import com.golemgame.instrumentation.SlideAxisController;
import com.golemgame.instrumentation.TextBoxController;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.golems.InputInterpreter;
import com.golemgame.mvc.golems.WirePortInterpreter;
import com.golemgame.mvc.golems.input.InputDeviceInterpreter;
import com.golemgame.mvc.golems.input.KeyboardInputDeviceInterpreter;
import com.golemgame.mvc.golems.input.SliderInputDeviceInterpreter;
import com.golemgame.mvc.golems.input.TextInputDeviceInterpreter;
import com.golemgame.mvc.golems.input.InputDeviceInterpreter.InputType;
import com.golemgame.mvc.golems.input.KeyboardInputDeviceInterpreter.KeyEventType;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.jme.system.DisplaySystem;
import com.jmex.physics.PhysicsNode;

public class OdeInputStructure  extends OdePhysicalStructure{

	private final InputInterpreter interpreter;
	private Instrument instrument;
	public OdeInputStructure(PropertyStore store) {
		super(store);
		interpreter = new InputInterpreter(store);
	}


	
	@Override
	public void buildMind(BMind mind,
			Map<OdePhysicalStructure, PhysicsNode> physicalMap,
			Map<Reference, BComponent> wireMap, OdePhysicsEnvironment environment) {
		
		InputDeviceInterpreter dev = new InputDeviceInterpreter( interpreter.getInputDevice());
		InputType inputType = dev.getDeviceType();
		
		final BGenericSource output = new BGenericSource("Input " + interpreter.getName());//for now only supports a single output
		output.setLastingObservations(true);
		mind.addSource(output);
		mind.addComponent(output); 
		
		switch(inputType)
		{
			case TEXT:{
				TextInputDeviceInterpreter text = new TextInputDeviceInterpreter(dev.getStore());
				TextBoxController textInstrument = new TextBoxController();
				textInstrument.registerListener(new AxisControlListener()
				{
	
					public void valueSet(float value) {
						output.setSensorObservation(value);
					}
					
				});
				textInstrument.setCurrentValue(text.getInitialValue());
				this.instrument = textInstrument;
				break;
			}
			case SLIDER:{
				SliderInputDeviceInterpreter slider = new SliderInputDeviceInterpreter(dev.getStore());
				SlideAxisController slideInstrument = new SlideAxisController(slider.isHorizontal(), slider.showArrows());
				slideInstrument.registerListener(new AxisControlListener()
				{

					public void valueSet(float value) {
						output.setSensorObservation(value);
					}
					
				});
				slideInstrument.setCurrentValue(slider.getInitialValue());
				this.instrument = slideInstrument;
				break;
			}
			case KEYBOARD:{			
				KeyboardInputDeviceInterpreter keyboard = new KeyboardInputDeviceInterpreter(dev.getStore());
				KeyboardController keyInstrument = new KeyboardController(keyboard.getKeyCode());
				keyInstrument.setKeyCode(keyboard.getKeyCode());
				final boolean outputZero = !keyboard.outputNegative();				
				output.setSensorObservation(outputZero? 0 :-1f);
				if(keyboard.getInteractionType() == KeyEventType.HeldDown)
				{
					keyInstrument.registerListener(new KeyboardControlListener()
					{
						public void keyPress() {	
							output.setSensorObservation(1f);
						}
	
						public void keyRelease() {	
							output.setSensorObservation(outputZero? 0 :-1f);
						}						
					});
				}else 	if(keyboard.getInteractionType() == KeyEventType.Toggle)
				{
					keyInstrument.registerListener(new KeyboardControlListener()
					{
						private boolean toggle = false;
						public void keyPress() {	
							toggle = ! toggle;
							if(outputZero){
								output.setSensorObservation(toggle?1f:0f);
							}{
								output.setSensorObservation(toggle?1f:-1f);
							}
						}	
						public void keyRelease() {	
						}						
					});
				}
				this.instrument = keyInstrument;
				break;
				}
		}
		this.instrument.setName(interpreter.getName());
		environment.registerInstrument(instrument);
		WirePortInterpreter out = new WirePortInterpreter(interpreter.getOutput());
		
		//wireMap.put(out.getID(), source);
		wireMap.put(out.getID(), output);
		InputDeviceInterpreter device = new InputDeviceInterpreter(interpreter.getInputDevice());
		
		double screenWidth = DisplaySystem.getDisplaySystem().getWidth();
		double screenHeight = DisplaySystem.getDisplaySystem().getHeight();
		instrument.setWindowed(device.isInstrumentWindowed());
		if (instrument.getInstrumentInterface()!=null)
		{
			int extraX = device.isInstrumentWindowed() ? + instrument.getInstrumentInterface().getAppearance().getLeftMargins():0;
			int extraY = device.isInstrumentWindowed() ? +instrument.getInstrumentInterface().getAppearance().getBottomMargins():0;
			
			int extraWidth =  device.isInstrumentWindowed() ?  instrument.getInstrumentInterface().getAppearance().getLeftMargins() + instrument.getInstrumentInterface().getAppearance().getRightMargins() +  instrument.getInstrumentInterface().getContentContainer().getAppearance().getLeftMargins() + instrument.getInstrumentInterface().getContentContainer().getAppearance().getRightMargins() :0; 
			int extraHeight =  device.isInstrumentWindowed() ?  instrument.getInstrumentInterface().getAppearance().getTopMargins() + instrument.getInstrumentInterface().getAppearance().getBottomMargins() + instrument.getInstrumentInterface().getTitleBar().getHeight() + instrument.getInstrumentInterface().getContentContainer().getAppearance().getTopMargins() + instrument.getInstrumentInterface().getContentContainer().getAppearance().getBottomMargins()  :0; 
			
			instrument.getInstrumentInterface().setX((int)(device.getInstrumentX()*screenWidth) + extraX);
			instrument.getInstrumentInterface().setY((int)(device.getInstrumentY()*screenHeight)+ extraY);
			instrument.getInstrumentInterface().setWidth((int)(device.getInstrumentWidth()*screenWidth) + extraWidth);
			instrument.getInstrumentInterface().setHeight((int)(device.getInstrumentHeight()*screenHeight) + extraHeight);
			
		}
		instrument.setLocked(device.isInstrumentLocked());
		instrument.setUserPositioned(device.isInstrumentUserPositioned());
	}
	


	@Override
	public void buildRelationships(
			Map<OdePhysicalStructure, PhysicsNode> physicalMap,
			OdePhysicsEnvironment compiledEnvironment) {
		
		
		super.buildRelationships(physicalMap, compiledEnvironment);
	}
	
	@Override
	public boolean isPropagating() {
		return false;
	}



	@Override
	public void remove() {
		InputDeviceInterpreter device = new InputDeviceInterpreter(interpreter.getInputDevice());
		
		if(instrument.getInstrumentInterface()!=null)
		{
			double posX = instrument.getInstrumentInterface().getContentContainer().getDisplayX();
			double posY = instrument.getInstrumentInterface().getContentContainer().getDisplayY();
			
			double width =  instrument.getInstrumentInterface().getContentContainer().getWidth() -  instrument.getInstrumentInterface().getContentContainer().getAppearance().getLeftMargins() - instrument.getInstrumentInterface().getContentContainer().getAppearance().getRightMargins();
			double height =  instrument.getInstrumentInterface().getContentContainer().getHeight() - instrument.getInstrumentInterface().getContentContainer().getAppearance().getTopMargins() - instrument.getInstrumentInterface().getContentContainer().getAppearance().getBottomMargins();
			
			double screenWidth = DisplaySystem.getDisplaySystem().getWidth();
			double screenHeight = DisplaySystem.getDisplaySystem().getHeight();
			
			device.setInstrumentX(posX/screenWidth);
			device.setInstrumentY(posY/screenHeight);
			device.setInstrumentWidth(width/screenWidth);
			device.setInstrumentHeight(height/screenHeight);
			device.setInstrumentWindowed(instrument.isWindowed());
		}
		if(instrument.isUserPositioned())
			device.setInstrumentUserPositioned(true);//dont ever set false
		
		super.remove();
	}
	

}
