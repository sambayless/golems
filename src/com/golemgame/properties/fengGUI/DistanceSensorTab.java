package com.golemgame.properties.fengGUI;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.fenggui.CheckBox;
import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.ListItem;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;

import com.golemgame.local.StringConstants;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.DistanceSensorInterpreter.SensorMode;
import com.golemgame.structural.structures.DistanceSensor;
import com.golemgame.structural.structures.ghost.GhostBox;
import com.golemgame.structural.structures.ghost.GhostCone;
import com.golemgame.structural.structures.ghost.GhostCylinder;
import com.golemgame.structural.structures.ghost.GhostPyramid;
import com.golemgame.structural.structures.ghost.GhostSphere;
import com.golemgame.structural.structures.ghost.GhostStructure;
import com.golemgame.views.Viewable.ViewMode;


public class DistanceSensorTab extends TabAdapter {

	private ComboBox<ShapeOption> shapes;
	private ComboBox<SensorMode> sensorMode;
	private CheckBox<Object> ignoreStatics;
	
	private DistanceSensor sensor = null;
	
	
	public DistanceSensor getSensor() {
		return sensor;
	}

	public void setSensor(DistanceSensor sensor) {
		this.sensor = sensor;
	}

	
	
	public DistanceSensorTab() {
		super(StringConstants.get("PROPERTIES.SENSOR","Sensor"));
	
	}

	@Override
	protected void buildGUI() {
		getTab().setLayoutManager(new BorderLayout());
		Container row = FengGUI.createContainer(getTab());
		row.setLayoutData(BorderLayoutData.NORTH);
		row.setLayoutManager(new RowLayout(false));
		
		shapes = FengGUI.<ShapeOption>createComboBox(row);
		
		List<ListItem<ShapeOption>> items = new ArrayList<ListItem<ShapeOption>>();
		
		items.add( new ListItem<ShapeOption>(StringConstants.get("TOOLBAR.BOX","Box"), new ShapeOption(GhostBox.class)));
		//items.add( new ListItem<ShapeOption>("Cone", new ShapeOption(GhostCone.class)));
		items.add( new ListItem<ShapeOption>(StringConstants.get("TOOLBAR.PYRAMID","Pyramid"), new ShapeOption(GhostPyramid.class)));
		items.add( new ListItem<ShapeOption>(StringConstants.get("TOOLBAR.SPHERE","Sphere"), new ShapeOption(GhostSphere.class)));
		items.add( new ListItem<ShapeOption>(StringConstants.get("TOOLBAR.CYLINDER","Cylinder"), new ShapeOption(GhostCylinder.class)));
		
		
		for(ListItem<ShapeOption> item:items)
			shapes.addItem(item);
		
		List<ListItem<SensorMode>> sensorModes = new ArrayList<ListItem<SensorMode>>();
		
	//	sensorModes.add( new ListItem<SensorMode>("Ignore ", SensorMode.IGNORE_NONE));
		sensorModes.add( new ListItem<SensorMode>(StringConstants.get("PROPERTIES.SENSOR.IGNORE_ATTACHED","Ignore Attached Objects (not including joints)"), SensorMode.IGNORE_SELF));
		
		sensorModes.add( new ListItem<SensorMode>(StringConstants.get("PROPERTIES.SENSOR.IGNORE_JOINTS","Ignore Attached Objects (Including joints)"), SensorMode.IGNORE_SIMILAR));
		sensorModes.add( new ListItem<SensorMode>(StringConstants.get("PROPERTIES.SENSOR.IGNORE_NON_STATIC","Ignore Non-Static Objects"), SensorMode.IGNORE_NON_STATIC));
		
		
		sensorMode = FengGUI.<SensorMode>createComboBox(row);
		for (ListItem<SensorMode> item:sensorModes)
			sensorMode.addItem(item);
		
		ignoreStatics = FengGUI.createCheckBox(row,StringConstants.get("PROPERTIES.SENSOR.IGNORE_STATIC","Ignore Static Objects"));
		
		
	}

	@Override
	public void close(boolean cancel) {
		if(!cancel && sensor != null &&  shapes.getSelectedItem() != null)
		{
			
			sensor.setIgnoreStatics( this.ignoreStatics.isSelected());
			
			sensor.setSensorMode(this.sensorMode.getSelectedItem().getValue());
			
			Class<? extends GhostStructure> ghostClass = shapes.getSelectedItem().getValue().getShape();
			if(ghostClass != null && ! sensor.getDistanceGhost().getClass().equals(ghostClass))
			{
				try{
					Constructor<? extends GhostStructure> c = ghostClass.getConstructor(PropertyStore.class);
					GhostStructure ghost = c.newInstance(new PropertyStore());
					sensor.setDistanceGhost(ghost);
					
					ghost.getModel().getLocalTranslation().set(1,0,0);
					ghost.addViewMode(ViewMode.GHOST);
				}catch(NoSuchMethodException e)
				{
					
				}catch(SecurityException e){
					
				}catch(InstantiationException e){
					
				}catch(IllegalAccessException e){
					
				}catch(IllegalArgumentException e){
					
				}catch(InvocationTargetException e){
					
				}
		

			}
		}
		super.close(cancel);
	}

	@Override
	public void open() {
		if(sensor!= null)
		{
			ignoreStatics.setSelected(sensor.isIgnoreStatics());
			
			SensorMode mode = sensor.getSensorMode();
			for(ListItem<SensorMode> option:sensorMode.getList().getItems())
			{
				if(option.getValue().equals(mode))
				{
					sensorMode.setSelected(option);
					
					break;
				}
			}
			
			
			
			
			GhostStructure ghost = sensor.getDistanceGhost();
			for(ListItem<ShapeOption> option:shapes.getList().getItems())
			{
				if(option.getValue().getShape().equals(ghost.getClass()))
				{
					shapes.setSelected(option);
					
					break;
				}
			}
			//handle cones, which have been disabled...
			if(ghost instanceof GhostCone)
			{
				ListItem<ShapeOption> coneItem = new ListItem<ShapeOption>("Cone", new ShapeOption(GhostCone.class));
				shapes.addItem(coneItem );
				coneItem.setSelected(true);
			}
		}
		super.open();
	}

	private static class ShapeOption
	{
		private final Class<? extends GhostStructure> shape;

		public ShapeOption(Class<? extends GhostStructure> shape) {
			super();
			this.shape = shape;
		}

		public Class<? extends GhostStructure> getShape() {
			return shape;
		}
		
	}
	
}
