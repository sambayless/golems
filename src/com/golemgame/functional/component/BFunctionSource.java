package com.golemgame.functional.component;

import java.util.logging.Level;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.PolynomialFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;

import com.golemgame.functional.FunctionSettings;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.BatteryInterpreter.SwitchType;
import com.golemgame.mvc.golems.BatteryInterpreter.ThresholdType;
import com.golemgame.states.StateManager;




public class BFunctionSource extends BSource {
	static final long serialVersionUID =1;

	private FunctionSettings settings;

	
	protected float totalTime=0;
	private UnivariateRealFunction function;
	private final BAuxInput switchInput = new BAuxInput();
	private float threshold = 0;
	private float currentTime = 0;

	private ThresholdType thresholdType;
	
	private boolean switchActive = false;
	
	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	@Override
	public void updateSource(float time) 
	{	
		currentTime= totalTime;
		
		 if ((!this.switchActive) && (this.switchType == SwitchType.On))	
			 return;//of its off, then dont update the time
		
		if (this.switchType == SwitchType.Pause && this.switchActive == true)
		{
			//dont add to the time.
			return;
		}else
			this.totalTime += time;
		
		if (this.switchType == SwitchType.Reset && this.switchActive == true)
		{
			this.totalTime = 0;
			return;
		}
		
		
		if (settings.isPeriodic())
		{
			if (this.totalTime/settings.getScaleX()>settings.getMaxX()) //keep this function within its periodic bounds
				this.totalTime = settings.getMinX()*settings.getScaleX();
		}
		else{
			if (this.totalTime/settings.getScaleX()>settings.getMaxX()) //keep this function within its periodic bounds
				this.totalTime = settings.getMaxX()*settings.getScaleX();	
		}
		
	}
	
	public BFunctionSource(FunctionSettings settings)
	{
		super();
		this.setUpdatable(true);
		this.setSettings(settings);
		
		this.totalTime = settings.getMinX();
		this.setFunction(settings.buildFunction());
		

	}
	public BFunctionSource()
	{
		this(new FunctionSettings(new PropertyStore()));


	}
	
	@Override
	public float generateSignal(float time) 
	{
		 if ((!this.switchActive) && (this.switchType == SwitchType.On))	
			 return 0;
		
		float output = 0;
		try{
			//a null pointer here is tied to physics not deleting...
			output = settings.clampY( (float) function.value(settings.clampX(currentTime/settings.getScaleX()))*settings.getScaleY());
	
			
		}catch(FunctionEvaluationException e)
		{
			StateManager.getLogger().log(Level.WARNING, e.getStackTrace().toString());
			//send no signal on error
		}
		if(this.switchActive && this.switchType==SwitchType.Invert)
			output = -output;
	
		
		return output;
	}


	public UnivariateRealFunction getFunction() {

		return function;
	}

	public void setFunction(UnivariateRealFunction function) {
		if(function == null)
			this.function = dummyFunction;
		else
			this.function =function;
	}


	public FunctionSettings getSettings() {
		return settings;
	}


	public void setSettings(FunctionSettings settings) {
		if(settings == null)
			this.settings = dummySettings;
		else
			this.settings = settings;
	}

	/**
	 * Reset the elapsed time of this function source.
	 */
	public void reset()
	{
		this.totalTime = 0;
	}
	

	protected class BAuxInput extends BComponent
	{
		private static final long serialVersionUID =1;
		@Override
		public float generateSignal(float time) 
		{
			switch (thresholdType)
			{
				case GREATER_EQUAL:
					if(state>=threshold)
					{
						setSwitchActive(true);
					}else
						setSwitchActive(false);
					break;
				case LESSER_EQUAL:
					if(state<=threshold)
					{
						setSwitchActive(true);
					}else
						setSwitchActive(false);
					break;
				case GREATER_THAN:
					if(state>threshold)
					{
						setSwitchActive(true);
					}else
						setSwitchActive(false);
					break;
				case LESSER_THAN:
					if(state<threshold)
					{
						setSwitchActive(true);
					}else
						setSwitchActive(false);					
					break;
			}

			state = 0;
			return 0;
		}
		
		private void setSwitchActive(boolean active)
		{
			switchActive = active;
/*			switch(switchType)
			{
				case Zero:
					//this is handled in switch output
					break;
				case Invert:
					break;
				case Pause:
					break;
				case Reset:
					if(active)
						reset();
					break;
				default:
						
			}*/
		}
		
	
		
	}

	public BComponent getSwitchInput() {
		return switchInput;
	}
	public ThresholdType getThresholdType() {
		return thresholdType;
	}


	private static final FunctionSettings dummySettings =new FunctionSettings(new PropertyStore());
	private static final UnivariateRealFunction dummyFunction =new PolynomialFunction(new double[]{1});

	private SwitchType switchType = SwitchType.On;
	
	public void setSwitchType(SwitchType type)
	{
		this.switchType = type;
	}
	
	public SwitchType getSwitchType()
	{
		return switchType;
	}

	public void setThresholdType(ThresholdType thresholdType) {
		this.thresholdType = thresholdType;
		
	}
	
	
	
}
