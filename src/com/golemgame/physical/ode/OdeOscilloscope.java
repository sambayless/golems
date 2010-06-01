package com.golemgame.physical.ode;

import java.util.Map;

import com.golemgame.functional.component.BComponent;
import com.golemgame.functional.component.BGenericInput;
import com.golemgame.functional.component.BMind;
import com.golemgame.instrumentation.ColorInstrument;
import com.golemgame.instrumentation.Instrument;
import com.golemgame.instrumentation.OscilloscopeInstrument;
import com.golemgame.instrumentation.TextOutputInstrument;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.golems.OscilloscopeInterpreter;
import com.golemgame.mvc.golems.WirePortInterpreter;
import com.golemgame.mvc.golems.output.ColorOutputDeviceInterpreter;
import com.golemgame.mvc.golems.output.OutputDeviceInterpreter;
import com.golemgame.mvc.golems.output.OutputDeviceInterpreter.OutputType;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.jme.system.DisplaySystem;
import com.jmex.physics.PhysicsNode;

public class OdeOscilloscope  extends OdePhysicalStructure{

	private final OscilloscopeInterpreter interpreter;
	private Instrument instrument;
	public OdeOscilloscope(PropertyStore store) {
		super(store);
		interpreter = new OscilloscopeInterpreter(store);
	}


	
	@Override
	public void buildMind(BMind mind,
			Map<OdePhysicalStructure, PhysicsNode> physicalMap,
			Map<Reference, BComponent> wireMap, OdePhysicsEnvironment environment) {
		
		BGenericInput input = new BGenericInput(){

			@Override
			protected float signalRecieved(float signal,float time) {
				if(instrument instanceof OscilloscopeInstrument)
				{
					OscilloscopeInstrument in = (OscilloscopeInstrument)instrument;
					in.updateSignal(signal,time);
					in.setVisible(this.isConnectedTo());
				}else if(instrument instanceof ColorInstrument)
				{
					ColorInstrument in = (ColorInstrument)instrument;
					in.setCurrentValue(signal);
					in.setVisible(this.isConnectedTo());
				}else if (instrument instanceof TextOutputInstrument)
				{
					TextOutputInstrument in = (TextOutputInstrument)instrument;
					in.setCurrentValue(signal);
					in.setVisible(this.isConnectedTo());
				}
				return 0;
			}
			
		};
	
		mind.addComponent(input);

		
		//WirePortInterpreter out = new WirePortInterpreter(interpreter.getOutput());
		WirePortInterpreter in = new WirePortInterpreter(interpreter.getInput());
		
		//wireMap.put(out.getID(), source);
		wireMap.put(in.getID(), input);
	}
	


	@Override
	public void buildRelationships(
			Map<OdePhysicalStructure, PhysicsNode> physicalMap,
			OdePhysicsEnvironment compiledEnvironment) {
		
		OutputDeviceInterpreter device = new OutputDeviceInterpreter(interpreter.getOutputDevice());
		OutputType outputDev = device.getDeviceType();
		switch(outputDev)
		{	
			case GRAPH:
				instrument = new OscilloscopeInstrument();
				instrument.setName(interpreter.getOscilloscopeName());
				break;
			case COLOR:
				instrument = new ColorInstrument();
				ColorInstrument cIn = (ColorInstrument)instrument;
				ColorOutputDeviceInterpreter colorOut = new ColorOutputDeviceInterpreter(interpreter.getOutputDevice());
				cIn.setPositiveColor(colorOut.getPositiveColor());
				cIn.setNeutralColor(colorOut.getNeutralColor());
				cIn.setNegativeColor(colorOut.getNegativeColor());
				instrument.setName(interpreter.getOscilloscopeName());
				break;
			case TEXT:
				instrument = new TextOutputInstrument();
				instrument.setName(interpreter.getOscilloscopeName());
				break;
				
		}
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
			
		
		compiledEnvironment.registerInstrument(instrument);
		super.buildRelationships(physicalMap, compiledEnvironment);
	}
	
	@Override
	public boolean isPropagating() {
		return false;
	}



	@Override
	public void remove() {
		
		//Save the state of the graphical component. Note: even though these are ignored for non embedded
		//components right now, we will save it anyhow
		OutputDeviceInterpreter device = new OutputDeviceInterpreter(interpreter.getOutputDevice());
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
