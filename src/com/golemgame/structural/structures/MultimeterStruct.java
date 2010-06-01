package com.golemgame.structural.structures;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.fenggui.CheckBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.TextEditor;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;

import com.golemgame.functional.WirePort;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.GeneralSensorInterpreter;
import com.golemgame.mvc.golems.GeneralSensorSettingsInterpreter;
import com.golemgame.mvc.golems.GeneralSensorInterpreter.SensorType;
import com.golemgame.properties.fengGUI.GeneralSensorTab;
import com.golemgame.properties.fengGUI.ITab;
import com.golemgame.properties.fengGUI.TabAdapter;
import com.golemgame.properties.fengGUI.TabbedWindow;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;



/**
 * This is a component that can be configured to be many different types of sensors.
 * @author Sam
 *
 */
public class MultimeterStruct extends BoxStructure {

	private static final long serialVersionUID = 1L;



	private WirePort[] wirePorts;
	private Map<SensorType, FloatSensorSettings> settingsMap = new HashMap<SensorType,FloatSensorSettings>();
	
	private GeneralSensorInterpreter interpreter;

	public MultimeterStruct(PropertyStore store) {
		super(store);
		this.interpreter = new GeneralSensorInterpreter(store);
		wirePorts = new WirePort[3];
		for(int i = 0; i<3;i++)
		{
			wirePorts[i] = new WirePort(this,false);
			this.getModel().addChild(wirePorts[i].getModel());
		}
		
		wirePorts[1].getModel().getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Z);
		wirePorts[2].getModel().getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_X);
		
		for (WirePort port:wirePorts)
			super.registerWirePort(port);

	}
	
	public SensorType getSensor() {
		return interpreter.getSensorType();
	}

	public void setSensor(SensorType sensor) {
		if(this.getSensor() != sensor)
		{
			interpreter.setSensorType(sensor);
			interpreter.refresh();
			
		}
	//	wirePorts[2].getModel().getLocalTranslation().addLocal(0.5f, 0.5f, 0.5f);
	//	wirePorts[1].getModel().updateWorldData();
	}
	
	
	
	
public boolean isMindful() {
		return true;
	}


	
	public void refreshController() {
		super.refreshController();
		
		
				
	}
	
	public FloatSensorSettings getSettings(SensorType type)
	{
		return settingsMap.get(type);
	}

	




	@Override
	public void refresh() {
		super.refresh();
		setConnections(interpreter.getSensorType());
		
		for(WirePort port:wirePorts)
			port.getModel().getLocalTranslation().zero();
		
		Vector3f boxExtent = super.getInterpreter().getExtent();
		wirePorts[0].getModel().getLocalTranslation().y = boxExtent.y;
		wirePorts[1].getModel().getLocalTranslation().x = boxExtent.x;
		wirePorts[2].getModel().getLocalTranslation().z =boxExtent.z;
		
		for(WirePort port:wirePorts)
		{
			port.getModel().updateWorldData();
		}
		
		
		PropertyStore altitudeSettingsStore = interpreter.getAltitudeSettings();
		FloatSensorSettings altitudeSettings =this.settingsMap.get(SensorType.ALTITUDE) ;
		if(altitudeSettings == null || !altitudeSettings.getStore().equals(altitudeSettingsStore))
		{
			this.settingsMap.put(SensorType.ALTITUDE, new AltitudeSensorSettings(altitudeSettingsStore));
		}
		
		PropertyStore accelerationSettingsStore = interpreter.getAccelerationSettings();
		FloatSensorSettings accelerationSettings =this.settingsMap.get(SensorType.ACCELERATION) ;
		if(accelerationSettings == null || !accelerationSettings.getStore().equals(accelerationSettingsStore))
		{
			this.settingsMap.put(SensorType.ACCELERATION, new PositionSensorSettings(accelerationSettingsStore));
		}
		
		PropertyStore velocitySettingsStore = interpreter.getVelocitySettings();
		FloatSensorSettings velocitySettings =this.settingsMap.get(SensorType.VELOCITY) ;
		if(velocitySettings == null || !velocitySettings.getStore().equals(velocitySettingsStore))
		{
			this.settingsMap.put(SensorType.VELOCITY, new PositionSensorSettings(velocitySettingsStore));
		}
		
		PropertyStore positionSettingsStore = interpreter.getPositionSettings();
		FloatSensorSettings positionSettings =this.settingsMap.get(SensorType.POSITION) ;
		if(positionSettings == null || !positionSettings.getStore().equals(positionSettingsStore))
		{
			this.settingsMap.put(SensorType.POSITION, new PositionSensorSettings(positionSettingsStore));
		}
		
		for (FloatSensorSettings settings:settingsMap.values())
			settings.refresh();
		
/*
		PropertyStore orientationSettingsStore = interpreter.getOrientationSettings();
		FloatSensorSettings orientationSettings =this.settingsMap.get(SensorType.ORIENTATION) ;
		if(orientationSettings == null || !orientationSettings.getStore().equals(orientationSettingsStore))
		{
			this.settingsMap.put(SensorType.ORIENTATION, new OrientationSensorSettings(orientationSettingsStore));
		}*/
	}


	private void setConnections(SensorType sensor)
	{
		if(sensor == SensorType.ALTITUDE)
		{
			wirePorts[1].setDisabled(true);
			wirePorts[2].setDisabled(true);
			wirePorts[0].setDisabled(false);
		}else
		{
			wirePorts[0].setDisabled(false);
			wirePorts[1].setDisabled(false);
			wirePorts[2].setDisabled(false);
		}
		
	}
	

	
	
	
	
	public void populateProperties(TabbedWindow window) {
		refresh();
		GeneralSensorTab sensorTab = new GeneralSensorTab();
		sensorTab.setGeneralSensor(this);
		window.addTab(sensorTab);
		super.populateProperties(window);
	}

	public ITab getSettingsTab(SensorType type)
	{
		switch(type)
		{
			case ALTITUDE:
				return new AltitudeSensorTab(settingsMap.get(type));
			case POSITION:
				return new PositionSensorTab((PositionSensorSettings)settingsMap.get(type),false);
			case ORIENTATION:
				return new NoValueTab();
			default:
				return new PositionSensorTab((PositionSensorSettings)settingsMap.get(type),true);
		}
	}


	public static class AltitudeSensorSettings  extends FloatSensorSettings
	{
		private static final long serialVersionUID = 1L;
		public AltitudeSensorSettings(PropertyStore store) {
			super(store);
	
		}
		
		public float getMaxY()
		{
			return super.getValue(0);
		}
		public float getMinY()
		{
			return super.getValue(1);
		}
		
	
	}
	
	public static class PositionSensorSettings  extends FloatSensorSettings
	{
		private static final long serialVersionUID = 1L;

		public boolean isRelative() {
			return interpreter.isRelative();
		}
		public void setRelative(boolean isRelative) {
			interpreter.setRelative(isRelative);
		}
		public PositionSensorSettings(PropertyStore store) {
			super(store);
			
	/*		super.setValue(0, 100);
			super.setValue(1, -100);
			super.setValue(2, 100);
			super.setValue(3, -100);
			super.setValue(4, 100);
			super.setValue(5, -100);*/
		}
		public float getMaxX()
		{
			return super.getValue(0);
		}
		public float getMinX()
		{
			return super.getValue(1);
		}
		public float getMaxY()
		{
			return super.getValue(2);
		}
		public float getMinY()
		{
			return super.getValue(3);
		}
		public float getMaxZ()
		{
			return super.getValue(4);
		}
		public float getMinZ()
		{
			return super.getValue(5);
		}
		
	
		
	}

	public static abstract class FloatSensorSettings  implements Serializable, SustainedView
	{
		private static final long serialVersionUID = 1L;
	
	//	private float[] values;
				
		protected GeneralSensorSettingsInterpreter interpreter;
		
		public FloatSensorSettings(PropertyStore store) {
			super();
			interpreter = new GeneralSensorSettingsInterpreter(store);
			store.setSustainedView(this);
			//values = new float[numberOfValues];
	
		}
		public void remove() {
			// TODO Auto-generated method stub
			
		}
	/*	public void set(FloatSensorSettings setFrom) {
			this.interpreter.
			
		}*/

		public int getNumberOfValues()
		{
			return interpreter.getNumberOfValues();
		}
				
		public PropertyStore getStore() {
			return interpreter.getStore();
		}

		public void invertView(PropertyStore store) {
			store.set(getStore());
			
		}

		public void refresh() {
			//do nothing
			
		}

		public float getValue(int index)
		{
			return interpreter.getFloat(index);
		}
		
		public void setValue(int index,float value)
		{
			interpreter.setValue(index,value);
		}

	
		
	}

	public static abstract class SensorSettingsTab extends TabAdapter
	{
		private static final long serialVersionUID = 1L;

		private final String[] valuesToSet;
		private TextEditor[] textEditors;
		private Container mainContainer;
		
		private FloatSensorSettings settings;
		
		private SensorSettingsTab(String[] valuesToSet,FloatSensorSettings settings) {
			super("Sensor");
			this.settings=settings;
			this.valuesToSet = valuesToSet;
			this.buildGUIInternal();
		}
		
		public TextEditor[] getTextEditors() {
			return textEditors;
		}

		public void setTextEditors(TextEditor[] textEditors) {
			this.textEditors = textEditors;
		}

		protected void buildGUIInternal()
		{
			mainContainer = FengGUI.createContainer();
			
			mainContainer.setLayoutManager(new BorderLayout());
			
			Container row = FengGUI.createContainer(mainContainer);
			row.setLayoutData(BorderLayoutData.NORTH);
			row.setLayoutManager(new RowLayout(false));
			
			addOptionsToTop(row);
			
			textEditors = new TextEditor[valuesToSet.length];
			
			for(int i = 0; i<valuesToSet.length;i++)
			{
				Container valueRow =FengGUI.createContainer(row);
				valueRow.setLayoutManager(new RowLayout());
				FengGUI.createLabel(valueRow,valuesToSet[i]);
				textEditors[i]=FengGUI.createTextEditor(valueRow);
			}
		
		}
		
		
		public Container getTab() {
			return mainContainer;
		}

		
		public String getTitle() {
			return "Sensor Settings";
		}
		
		protected void addOptionsToTop(Container row)
		{
			
		}
		
		
		public void close(boolean cancel) {
			if(!cancel)
			{
				for(int i = 0;i<settings.getNumberOfValues();i++)
				{
					try{
						settings.setValue(i,Float.valueOf( getTextEditors()[i].getText()));
					}catch(NumberFormatException e){}
				}
			}
		}
		
		
		public void open() {
			for(int i = 0; i<settings.getNumberOfValues();i++)
			{
				getTextEditors()[i].setText(String.valueOf(settings.getValue(i)));
		
			}
		}

	}
	
	private static class NoValueTab extends TabAdapter
	{

		public NoValueTab() {
			super("");
		}

		
		protected void buildGUI() {
			super.getTab().setLayoutManager(new BorderLayout());
			FengGUI.createLabel(super.getTab(),"No Settings").setLayoutData(BorderLayoutData.CENTER);
		}

	}
	
	private static class PositionSensorTab extends SensorSettingsTab
	{
		private CheckBox<Object> checkBox;
		private PositionSensorSettings settings;
		private PositionSensorTab(PositionSensorSettings settings, boolean showRelative) {
			super(new String[]{"Maximum X Range","Minimum X Range","Maximum Y Range","Minimum Y Range","Maximum Z Range","Minimum Z Range"},settings);
			this.settings = settings;
		}

		
		protected void addOptionsToTop(Container row) {
			checkBox = FengGUI.<Object>createCheckBox(row,"Report values relative to the orientation of the component");	
		}

		
		public void close(boolean cancel) {
			if(!cancel)
			{
				settings.setRelative(checkBox.isSelected());
			}
			super.close(cancel);
		}

		
		public void open() {
			checkBox.setSelected(settings.isRelative());
			super.open();
		}
		
	}
	
	private static class AltitudeSensorTab extends SensorSettingsTab
	{
		private AltitudeSensorTab(FloatSensorSettings settings) {
			super(new String[]{"Maximum Height","Minimum Height"},settings);
		}
	}
}


