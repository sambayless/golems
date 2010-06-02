package com.golemgame.mvc.golems;

import java.util.HashMap;
import java.util.Map;

import com.golemgame.mvc.DoubleType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.BatteryInterpreter.ThresholdType;
import com.golemgame.mvc.golems.GeneralSensorInterpreter.SensorType;
import com.golemgame.mvc.golems.MaterialPropertiesInterpreter.MaterialClass;
import com.golemgame.mvc.golems.functions.FunctionSettingsInterpreter;
import com.golemgame.mvc.golems.functions.PolynomialFunctionInterpreter;
import com.golemgame.mvc.golems.input.KeyboardInputDeviceInterpreter;
import com.golemgame.mvc.golems.input.KeyboardInputDeviceInterpreter.KeyEventType;
import com.golemgame.mvc.golems.output.GraphOutputDeviceInterpreter;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

public class DefaultGolemsProperties {
	private static final DefaultGolemsProperties instance = new DefaultGolemsProperties();

	public static DefaultGolemsProperties getInstance() {
		return instance;
	}
	
	private final Map<String,PropertyStore> defaults = new HashMap<String,PropertyStore>();
	
	public DefaultGolemsProperties() {
		
		
		MaterialPropertiesInterpreter matter = new MaterialPropertiesInterpreter();
		matter.setDensity(1f);
		matter.setMaterial(MaterialClass.DEFAULT);
		matter.setName(MaterialClass.DEFAULT.getName());
		defaults.put(GolemsClassRepository.MATERIAL_PROPERTIES_CLASS, matter.getStore());
		
		OscilloscopeInterpreter oscilloscope = new OscilloscopeInterpreter();
		oscilloscope.setOscilloscopeName("Oscilloscope");
		
		GraphOutputDeviceInterpreter outputDeviceInterpreter = new GraphOutputDeviceInterpreter();
		outputDeviceInterpreter.setInstrumentHeight(170.0/768.0);//we can't refer to display system here, so just pick common screen size
		outputDeviceInterpreter.setInstrumentWidth(250.0/1024.0);//have to account for window frame in these sizes
		outputDeviceInterpreter.setInstrumentLocked(false);
		outputDeviceInterpreter.setInstrumentWindowed(true);
		outputDeviceInterpreter.setInstrumentX(0);
		outputDeviceInterpreter.setInstrumentY(0);
		outputDeviceInterpreter.setInstrumentUserPositioned(false);
		
		oscilloscope.setOutputDevice(outputDeviceInterpreter.getStore());
		

		defaults.put(GolemsClassRepository.OSCILLOSCOPE_CLASS, oscilloscope.getStore());
		
		ParticleEffectInterpreter particleEffect = new ParticleEffectInterpreter();
		particleEffect.getStore().set(RocketPropellantInterpreter.getDefaultParticleEffect());		
		defaults.put(GolemsClassRepository.PARTICLE_EFFECTS_CLASS, particleEffect.getStore());
		
		RocketPropellantInterpreter propellant = new RocketPropellantInterpreter();
		propellant.setEffectsEnabled(true);		
		setToDefault(propellant.getParticleEffects(),GolemsClassRepository.PARTICLE_EFFECTS_CLASS);
		defaults.put(GolemsClassRepository.ROCKET_PROPELLANT_CLASS, propellant.getStore());
		
		RocketInterpreter rocket = new RocketInterpreter();
		rocket.setMaxAcceleration(10f);
		setToDefault(rocket.getPropellantProperties(),GolemsClassRepository.ROCKET_PROPELLANT_CLASS);
		loadStructureDefaults(rocket.getStore());
		defaults.put(GolemsClassRepository.ROCKET_CLASS, rocket.getStore());
		
		BatteryInterpreter battery = new BatteryInterpreter();
		AppearanceInterpreter batteryAppearance = new AppearanceInterpreter(battery.getAppearanceStore());
		batteryAppearance.setBaseColor(		new ColorRGBA(0.5f,0.5f,0.5f,0.8f));
		battery.setThresholdType(ThresholdType.GREATER_EQUAL);
		//FunctionSettingsInterpreter batteryFunctionSettings = new FunctionSettingsInterpreter();
		FunctionSettingsInterpreter batteryFunction = new FunctionSettingsInterpreter();
		batteryFunction.setMinX(0f);
		PolynomialFunctionInterpreter poly = new PolynomialFunctionInterpreter();
	
		batteryFunction.setPeriodic(true);		
		poly.setCoefficient(0, new DoubleType(1));
		poly.setCoefficient(1, new DoubleType(0));
		batteryFunction.setFunction(poly.getStore());
		battery.setFunctionStore(batteryFunction.getStore());
		defaults.put(GolemsClassRepository.BATTERY_CLASS, battery.getStore());
		
		ModifierInterpreter modifier = new ModifierInterpreter();
		//FunctionSettingsInterpreter modifierFunctionSettings = new FunctionSettingsInterpreter();
		AppearanceInterpreter modifierAppearance = new AppearanceInterpreter(modifier.getAppearanceStore());
		modifierAppearance.setBaseColor(		new ColorRGBA(0.5f,0.5f,0.5f,0.8f));
		modifier.setThresholdType(ThresholdType.GREATER_EQUAL);
		FunctionSettingsInterpreter modifierFunction = new FunctionSettingsInterpreter();
		modifierFunction.setMinX(-1f);
		 poly = new PolynomialFunctionInterpreter();
		 poly.setCoefficient(0, new DoubleType(0));
		 poly.setCoefficient(1, new DoubleType(1));
		 modifierFunction.setFunction(poly.getStore());
		modifier.setFunctionStore(modifierFunction.getStore());
		defaults.put(GolemsClassRepository.MODIFIER_CLASS, modifier.getStore());
		
		
		GeneralSensorInterpreter multimeter = new GeneralSensorInterpreter();
		multimeter.setSensorType(SensorType.ALTITUDE);
		
		GeneralSensorSettingsInterpreter altitude = new GeneralSensorSettingsInterpreter();
		altitude.setRelative(false);
		altitude.setValue(0, 100);
		altitude.setValue(1, -100);
		
		GeneralSensorSettingsInterpreter position = new GeneralSensorSettingsInterpreter();
		position.setRelative(true);
		position.setValue(0, 100);
		position.setValue(1, -100);
		position.setValue(2, 100);
		position.setValue(3, -100);
		position.setValue(4, 100);
		position.setValue(5, -100);

		multimeter.getAltitudeSettings().set(altitude.getStore());
		multimeter.getPositionSettings().set(position.getStore());
		multimeter.getAccelerationSettings().set(position.getStore());
		multimeter.getVelocitySettings().set(position.getStore());
		loadStructureDefaults(multimeter.getStore());
		defaults.put(GolemsClassRepository.GENERAL_SENSOR_CLASS, multimeter.getStore());
		{
			GearInterpreter gear = new GearInterpreter();
			gear.setNumberOfTeeth(6);
			gear.setToothAngle(FastMath.HALF_PI/3f);
			gear.setToothHeight(0.1f);
			gear.setToothWidth(0.1f);
			loadStructureDefaults(gear.getStore());
			defaults.put(GolemsClassRepository.GEAR_CLASS, gear.getStore());
		}
		
		{
			BoxInterpreter box = new BoxInterpreter();
			RackGearInterpreter rackGear = new RackGearInterpreter(box.getStore());
			rackGear.setNumberOfTeeth(6);
			rackGear.setToothAngle(FastMath.HALF_PI/3f);
			rackGear.setToothHeight(0.1f);
			rackGear.setToothWidth(0.1f);
			
			box.setExtent(new Vector3f(0.5f,0.25f,0.5f));
			loadStructureDefaults(rackGear.getStore());
			defaults.put(GolemsClassRepository.RACK_GEAR_CLASS, rackGear.getStore());
		}
		
		BoxInterpreter box = new BoxInterpreter();
		box.setExtent(	new Vector3f(0.5f,0.5f,0.5f));
		loadStructureDefaults(box.getStore());
		defaults.put(GolemsClassRepository.BOX_CLASS, box.getStore());
	
		SphereInterpreter sphere = new SphereInterpreter();
		sphere.setExtent(new Vector3f(0.5f,0.5f,0.5f));
		loadStructureDefaults(sphere.getStore());
		defaults.put(GolemsClassRepository.SPHERE_CLASS, sphere.getStore());
		
		PyramidInterpreter pyr = new PyramidInterpreter();
		pyr.setExtent(new Vector3f(1f,1f,1f));
		loadStructureDefaults(pyr.getStore());
		defaults.put(GolemsClassRepository.PYRAMID_CLASS, pyr.getStore());
		
		CapsuleInterpreter cap = new CapsuleInterpreter();
		cap.setHeight(1f);
		cap.setRadius(0.5f);
		loadStructureDefaults(cap.getStore());
		defaults.put(GolemsClassRepository.CAP_CLASS, cap.getStore());
		
		CylinderInterpreter cyl = new CylinderInterpreter();
		cyl.setHeight(1f);
		cyl.setRadius(0.5f);
		loadStructureDefaults(cyl.getStore());
		defaults.put(GolemsClassRepository.CYL_CLASS, cyl.getStore());
		
		BallAndSocketInterpreter ballSocket = new BallAndSocketInterpreter();
		ballSocket.setLeftLength(1f);
		ballSocket.setLeftRadius(0.5f);
		ballSocket.setRightRadius(0.5f);
		loadStructureDefaults(cap.getStore());
		defaults.put(GolemsClassRepository.BALL_SOCKET_CLASS, ballSocket.getStore());
		
		{
		TubeInterpreter tube = new TubeInterpreter();
		tube.setHeight(1f);
		tube.setRadius(0.5f);
		tube.setArc(FastMath.TWO_PI);
		loadStructureDefaults(tube.getStore());
		defaults.put(GolemsClassRepository.TUBE_CLASS, tube.getStore());
		}
		
		
		{
		GrappleInterpreter grapple = new GrappleInterpreter();
		grapple.setBeamEnabled(true);
		grapple.setBeamLuminous(true);
		grapple.setBeamPullColor(ColorRGBA.blue);
		grapple.setBeamPushColor(ColorRGBA.red);
		grapple.setMaxDistance(50);
		grapple.setMaxForce(10f);
		loadStructureDefaults(grapple.getStore());
		defaults.put(GolemsClassRepository.GRAPPLE_CLASS, grapple.getStore());
		}
		
		
		InputInterpreter input = new InputInterpreter();
		AppearanceInterpreter inputAppearance = new AppearanceInterpreter(input.getAppearanceStore());
		inputAppearance.setBaseColor(		new ColorRGBA(0.5f,0.5f,0.5f,0.8f));
		input.setName("Input Device");
		KeyboardInputDeviceInterpreter keyDevice = new KeyboardInputDeviceInterpreter();
		keyDevice.setKeyCode('a');
		keyDevice.setOutputNegative(true);
		keyDevice.setInteractionType(KeyEventType.HeldDown);		
		input.setInputDevice(keyDevice.getStore());
		defaults.put(GolemsClassRepository.INPUT_CLASS, input.getStore());
	}
	
	private void loadStructureDefaults(PropertyStore store)
	{

		
	/*	PhysicalStructureInterpreter interpreter = new PhysicalStructureInterpreter(store);
		interpreter.getMaterialProperties().set(defaults.get(DesignViewFactory.MATERIAL_PROPERTIES_CLASS));
		
		interpreter.setStatic(false);
		*/
		
	}

	public void setToDefault(PropertyStore store,String className)
	{
		PropertyStore def = defaults.get(className);
		if (def != null)
			store.set(def);
	}
	
	
	public void setToDefault(PropertyStore store)
	{
		setToDefault(store, store.getClassName());
	}
	
	private final static PropertyStore defaultStore = new PropertyStore();

	public PropertyStore getDefaultStore(String className) {
		PropertyStore store = defaults.get(className);
		if (store != null)
			return store;
		return defaultStore;
	}
	
}
