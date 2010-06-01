package com.golemgame.mvc.golems.validate;

import java.util.Map;
import java.util.TreeMap;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GolemsClassRepository;


public final class GolemsValidator extends ValidationFactory{
	public static final GolemsValidator instance = new GolemsValidator();

	private Map<String,Validator> validatorMap = new TreeMap<String,Validator>();

	public static GolemsValidator getInstance() {

		return instance;
	}

	public GolemsValidator() {
		super();
	}

	public void build()
	{

		validatorMap.clear();
		validatorMap.put(GolemsClassRepository.BOX_CLASS, new BoxValidator());
		validatorMap.put(GolemsClassRepository.CYL_CLASS, new CylinderValidator());
		validatorMap.put(GolemsClassRepository.CAP_CLASS, new CapsuleValidator());
		validatorMap.put(GolemsClassRepository.CONE_CLASS, new ConeValidator());
		validatorMap.put(GolemsClassRepository.GEAR_CLASS, new GearValidator());
		validatorMap.put(GolemsClassRepository.TUBE_CLASS, new TubeValidator());
		validatorMap.put(GolemsClassRepository.SPHERE_CLASS, new SphereValidator());
		
		validatorMap.put(GolemsClassRepository.HINGE_CLASS, new HingeValidator());
		validatorMap.put(GolemsClassRepository.HYDRAULIC_CLASS, new HydraulicValidator());
		validatorMap.put(GolemsClassRepository.AXLE_CLASS, new AxleValidator());
//		validatorMap.put(GolemsClassRepository.BALL_SOCKET_CLASS_OLD, new OldBallAndSocketValidator());
		validatorMap.put(GolemsClassRepository.BALL_SOCKET_CLASS, new BallAndSocketValidator());
		
		validatorMap.put(GolemsClassRepository.BATTERY_CLASS, new BatteryValidator());
		validatorMap.put(GolemsClassRepository.MODIFIER_CLASS, new ModifierValidator());

		validatorMap.put(GolemsClassRepository.MATERIAL_PROPERTIES_CLASS, new MaterialValidator());	
		validatorMap.put(GolemsClassRepository.MACHINE_SPACE_CLASS, new MachineSpaceValidator());
		validatorMap.put(GolemsClassRepository.MACHINE_CLASS, new MachineValidator());
		
		validatorMap.put(GolemsClassRepository.LAYER_REPOSITORY_CLASS, new LayerRepositoryValidator());
		
		validatorMap.put(GolemsClassRepository.CONTACT_CLASS, new ContactValidator());
		validatorMap.put(GolemsClassRepository.GRAPPLE_CLASS, new GrappleValidator());
		
		validatorMap.put(GolemsClassRepository.FUNCTION_SETTINGS_CLASS, new FunctionSettingsValidator());
		validatorMap.put(GolemsClassRepository.COMBINED_FUNCTION_CLASS, new CombinedFunctionValidator());

		validatorMap.put(GolemsClassRepository.KNOTTED_FUNCTION_CLASS, new KnottedFunctionValidator());
		validatorMap.put(GolemsClassRepository.OSCILLOSCOPE_CLASS, new OscilloscopeValidator());
		validatorMap.put(GolemsClassRepository.COLOR_OUTPUT_DEVICE_CLASS, new OutputDeviceValidator());
		validatorMap.put(GolemsClassRepository.GRAPH_OUTPUT_DEVICE_CLASS, new OutputDeviceValidator());
		validatorMap.put(GolemsClassRepository.TEXT_OUTPUT_DEVICE_CLASS, new OutputDeviceValidator());
		
		validatorMap.put(GolemsClassRepository.KEY_INPUT_DEVICE_CLASS, new InputDeviceValidator());
		validatorMap.put(GolemsClassRepository.SLIDER_INPUT_DEVICE_CLASS, new InputDeviceValidator());
		validatorMap.put(GolemsClassRepository.TEXT_INPUT_DEVICE_CLASS, new InputDeviceValidator());
		validatorMap.put(GolemsClassRepository.INPUT_CLASS, new InputValidator());
	}



	public void isValid(PropertyStore store)throws ValidationFailureException
	{
		 isValid(store,store.getClassName());
	}
	
	public void isValid(PropertyStore store, String classType) throws ValidationFailureException
	{
		Validator v = validatorMap.get(classType);
		if (v == null)
		{
			
			return;//unsafe default behaviour
		}

		v.isValid(store);
	}
	
	/**
	 * Does nothing if this store doesn't have a registered validator
	 */
	public void makeValid(PropertyStore store)
	{
		makeValid(store,store.getClassName());
	}
	
	/**
	 * Does nothing if this class type doesn't have a registered validator
	 */
	public void makeValid(PropertyStore store, String classType) 
	{
		Validator v = validatorMap.get(classType);
		if (v == null)
		{
			//StateManager.getLogger().warning("Missing validator: " + classType);
			return;
		}
		v.makeValid(store);
	}
}

