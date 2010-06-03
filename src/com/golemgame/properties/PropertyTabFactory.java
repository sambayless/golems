package com.golemgame.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.properties.Property.PropertyType;
import com.golemgame.properties.fengGUI.BatteryOptionsTab;
import com.golemgame.properties.fengGUI.BeamEffectDesigner;
import com.golemgame.properties.fengGUI.CameraTab;
import com.golemgame.properties.fengGUI.EmptyPropertyTab;
import com.golemgame.properties.fengGUI.FunctionTab;
import com.golemgame.properties.fengGUI.GearTab;
import com.golemgame.properties.fengGUI.GrappleTab;
import com.golemgame.properties.fengGUI.HorizontalTabbedWindow;
import com.golemgame.properties.fengGUI.HydraulicsTab;
import com.golemgame.properties.fengGUI.ITab;
import com.golemgame.properties.fengGUI.InputDeviceTab;
import com.golemgame.properties.fengGUI.InputStructureTab;
import com.golemgame.properties.fengGUI.LineEffectDesigner;
import com.golemgame.properties.fengGUI.MatterTab;
import com.golemgame.properties.fengGUI.ModifierOptionsTab;
import com.golemgame.properties.fengGUI.MotorTab;
import com.golemgame.properties.fengGUI.OscilloscopeTab;
import com.golemgame.properties.fengGUI.OutputDeviceTab;
import com.golemgame.properties.fengGUI.PIDControlsTab;
import com.golemgame.properties.fengGUI.ParticleEffectDesigner;
import com.golemgame.properties.fengGUI.PhysicalTab;
import com.golemgame.properties.fengGUI.PositionTab;
import com.golemgame.properties.fengGUI.PropertyTabAdapter;
import com.golemgame.properties.fengGUI.RackGearTab;
import com.golemgame.properties.fengGUI.RocketEffectDesigner;
import com.golemgame.properties.fengGUI.RocketTab;
import com.golemgame.properties.fengGUI.SoundPropertiesTab;
import com.golemgame.properties.fengGUI.TabbedWindow;
import com.golemgame.properties.fengGUI.TextureTab;
import com.golemgame.properties.fengGUI.scale.AxleScaleTab;
import com.golemgame.properties.fengGUI.scale.BallSocketScaleTab;
import com.golemgame.properties.fengGUI.scale.BoxScaleTab;
import com.golemgame.properties.fengGUI.scale.CylinderScaleTab;
import com.golemgame.properties.fengGUI.scale.EllipsoidScaleTab;
import com.golemgame.properties.fengGUI.scale.HingeScaleTab;
import com.golemgame.properties.fengGUI.scale.HydraulicScaleTab;
import com.golemgame.properties.fengGUI.scale.PyramidScaleTab;
import com.golemgame.properties.fengGUI.scale.SphereScaleTab;
import com.golemgame.properties.fengGUI.scale.TubeScaleTab;
import com.golemgame.states.StateManager;
import com.golemgame.tool.action.ActionDependencySet;

public class PropertyTabFactory {
	

	private static final Random random = new Random();

	public void displayPropertyWindow(Collection<Property> properties)
	{
		TabbedWindow window = new HorizontalTabbedWindow();
		window.setTitle("Properties");
		populateWindow(window,properties);
	}
	

	public void populateWindow(	TabbedWindow window, Collection<Property> properties)
	{
	Collection<Property>[] propertySet = organizeProperties(properties);
		
		Collection<ITab> tabs = new ArrayList<ITab>();
		Collection<ITab> allTabs = new ArrayList<ITab>();//includes embedded tabs
		int num = random.nextInt();
		while(num == -1)
			num = random.nextInt();
		
		ActionDependencySet dependencySet = new ActionDependencySet("Properties",num);
	
		for (int i = 0;i<propertySet.length;i++)				
		{
			Collection<Property> propertyCollection = propertySet[i];
			if (!propertyCollection.isEmpty())
			{				
				try {
					PropertyTabAdapter tab = getTab(PropertyType.values()[i], propertyCollection);
					tab.getPropertyStoreAdjuster().setDependencySet(dependencySet);
					//if this tab prefers to be embedded, we can do so, if that is possible
					if (!embed(tab,allTabs))
							tabs.add(tab);
					allTabs.add(tab);
				} catch (UnhandledPropertyException e) {
					//this is ok... just means we dont have a window to deal with this yet
					StateManager.getLogger().warning("No tab for this property type: " + PropertyType.values()[i]);
				}
			}
		}
		
		for(ITab tab:tabs)
			window.addTab(tab);
	}

	/**
	 * Attempt to embed this tab in a previous tab. Note this behaviour is dependent on the ordering of the enums in property.
	 * @param tab
	 * @param tabs
	 * @return
	 */
	private boolean embed(ITab tab, Collection<ITab> tabs) {
	
		for (ITab potentialParent:tabs){
			if (potentialParent.embed(tab))
			{
				return true;
			}
		}
		
		
		return false;
	}


	private Collection<Property>[] organizeProperties(Collection<Property> properties)
	{
		@SuppressWarnings("unchecked")
		Collection<Property>[] propertySet = new ArrayList[PropertyType.values().length];
		
		for (PropertyType prop:PropertyType.values())
			propertySet[prop.ordinal()] = new ArrayList<Property>();		
		
		for (Property prop:properties)
			propertySet[prop.getPropertyType().ordinal()].add(prop);
		return propertySet;
	}
	
	private PropertyTabAdapter getTab(PropertyType propertyType,Collection<Property> propertyCollection) throws UnhandledPropertyException
	{
		Collection<PropertyStore> stores = new ArrayList<PropertyStore>();
		
		

		
		for (Property prop:propertyCollection)
		{
			stores.add(prop.getPropertyStore());
		}
		PropertyTabAdapter tab = buildTab(propertyType);
		tab.setPropertyStores(stores);
		
	
		
		return tab;
		
	}
	private PropertyTabAdapter buildTab(PropertyType propertyType) throws UnhandledPropertyException
	{
		switch(propertyType)
		{
			case GEAR:
			{
				GearTab gear = new GearTab();
		
				return gear;
				
			}case PHYSICAL:
			{
				//return a physical tab, for adjusting glue, static, etc.
				PhysicalTab physics = new PhysicalTab();
		
				return physics;
				
			}case MATERIAL:
			{
				//construct a matter tab. This will be embedded in the physical tab.
				MatterTab tab = new MatterTab();			
	
				return tab;
			}case MOTOR:
			{
				MotorTab tab = new MotorTab();
			
				return tab;
			}case HYDRAULICS:
			{
				HydraulicsTab tab = new HydraulicsTab();
		
				return tab;
			}case ROCKET:
			{				
				RocketTab tab = new RocketTab();
		
				return tab;
			}case ROCKET_EFFECTS:
			{				
				RocketEffectDesigner tab = new RocketEffectDesigner();
	
				return tab;
			}case PARTICLE_EFFECTS:
			{				
				ParticleEffectDesigner tab = new ParticleEffectDesigner();
			
				return tab;
			}case FUNCTION_TIMED:
			{
				
				return new FunctionTab(true);
			}case FUNCTION_APPLIED:
			{				
				return new FunctionTab(false);
			}case BATTERY:
			{
				return new BatteryOptionsTab();
			}case MODIFIER:
			{
				return new ModifierOptionsTab();
			}case APPEARANCE:
			{
				return new TextureTab();
			}case CAMERA:
				return new CameraTab();
			case POSITION:
			{
				PositionTab tab = new PositionTab();
			
				return tab;
			}
			case PID:
				return new PIDControlsTab();
			case SCALE_BOX:
				return new BoxScaleTab();
			case SCALE_SPHERE:
				return new SphereScaleTab();
			case SCALE_ELLIPSOID:
				return new EllipsoidScaleTab();
			case SCALE_PYRAMID:
				return new PyramidScaleTab();
			case SCALE_CYLINDER:
				return new CylinderScaleTab();
			case SCALE_GEAR:
				return new CylinderScaleTab("Gear Scale");
			case SCALE_TUBE:
				return new TubeScaleTab();
			case SCALE_CONE:
				return new CylinderScaleTab("Cone Scale");
			case SCALE_CAPSULE:
				return new CylinderScaleTab("Capsule Scale");
			case SCALE_HINGE:
				return new HingeScaleTab();
			case SCALE_AXLE:
				return new AxleScaleTab();
			case SCALE_HYDRAULICS:
				return new HydraulicScaleTab();
			case SCALE_BALL_SOCKET:
				return new BallSocketScaleTab();
			case OSCILLOSCOPE:
				return new OscilloscopeTab();
			case SOUND:
			{
				if(StateManager.SOUND_ENABLED)
					return new SoundPropertiesTab();
				else
					return new EmptyPropertyTab();
			}
			case LINE_EFFECTS:
				return new LineEffectDesigner();
			case BEAM_EFFECTS:
				return new BeamEffectDesigner();
			case GRAPPLE:
				return new GrappleTab();
			case INPUT:
				return new InputStructureTab();
			case INPUT_DEVICE:
				return new InputDeviceTab();
			case OUTPUT_DEVICE:
				return new OutputDeviceTab();
			case RACK_GEAR:
				return new RackGearTab();

			default:
				throw new UnhandledPropertyException(propertyType.toString());
		}
		
		
	}
	
}
