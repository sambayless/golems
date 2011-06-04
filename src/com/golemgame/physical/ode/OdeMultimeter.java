/*******************************************************************************
 * Copyright 2008, 2009, 2010 Sam Bayless.
 * 
 *     This file is part of Golems.
 * 
 *     Golems is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Golems is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Golems. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.golemgame.physical.ode;

import java.util.HashMap;
import java.util.Map;

import com.golemgame.functional.component.BComponent;
import com.golemgame.functional.component.BGenericSource;
import com.golemgame.functional.component.BMind;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.golems.GeneralSensorInterpreter;
import com.golemgame.mvc.golems.WirePortInterpreter;
import com.golemgame.mvc.golems.GeneralSensorInterpreter.SensorType;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.golemgame.structural.structures.MultimeterStruct.AltitudeSensorSettings;
import com.golemgame.structural.structures.MultimeterStruct.FloatSensorSettings;
import com.golemgame.structural.structures.MultimeterStruct.PositionSensorSettings;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsUpdateCallback;

public class OdeMultimeter extends OdeBoxStructure {
	private GeneralSensorInterpreter interpreter;
	
	private Map<SensorType, FloatSensorSettings> settingsMap = new HashMap<SensorType,FloatSensorSettings>();
	
	
	public OdeMultimeter(PropertyStore store) {
		super(store);
		interpreter = new GeneralSensorInterpreter(store);
		
	
		
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
	}

	public FloatSensorSettings getSettings(SensorType type)
	{
		return settingsMap.get(type);
	}
	
	@Override
	public void buildMind(BMind mind,
			Map<OdePhysicalStructure, PhysicsNode> physicalMap,
			Map<Reference, BComponent> wireMap, OdePhysicsEnvironment environment) {
		buildSensorMind(mind,physicalMap,wireMap);
	}



	private void buildSensorMind(BMind mind, Map<OdePhysicalStructure, PhysicsNode> physicsMap, Map<Reference,BComponent> wireMap)
	{
		switch(interpreter.getSensorType())
		{
			case ALTITUDE:
				buildAltitudeSensor(mind,physicsMap,wireMap);
				break;
			case ORIENTATION:
				buildOrientationSensor(mind,physicsMap,wireMap);
				break;
			default:
				buildPositionSensor(mind,physicsMap,wireMap);
		}
		
	}
	
	private void buildAltitudeSensor(BMind mind, Map<OdePhysicalStructure,PhysicsNode> physicsMap, Map<Reference,BComponent> wireMap)
	{
		FloatSensorSettings settings = getSettings(SensorType.ALTITUDE);
		final BGenericSource source = new BGenericSource(SensorType.ALTITUDE.getName());
		
		source.setNormalizingMax(settings.getValue(0));
		source.setNormalizingMin(settings.getValue(1));
		
		final PhysicsCollisionGeometry geometry = super.getCollisionGeometry();
		final PhysicsNode physicsNode = geometry.getPhysicsNode();
		if(physicsNode == null)
			return;
		
		WirePortInterpreter in1 = new WirePortInterpreter(interpreter.getOutput(0));
	
		mind.addComponent(source);
		wireMap.put(in1.getID(), source);
		mind.addSource(source);
		
		if(physicsNode.isStatic())
		{
			//if its static, no need to continue sending observations - they wont change
			source.setLastingObservations(true);
			physicsNode.updateWorldVectors();
			geometry.updateWorldVectors();
			
			source.setSensorObservation(geometry.getWorldTranslation().getY());
			return;
		}
		
		this.getCollisionGeometry().getPhysicsNode().getSpace().addToUpdateCallbacks(new PhysicsUpdateCallback()
		{

			
			public void afterStep(PhysicsSpace space, float time) {
				source.setSensorObservation(geometry.getWorldTranslation().getY());
				
			}

			
			public void beforeStep(PhysicsSpace space, float time) {
				
			}
			
		});
		
		
		
	
	}
	
	private void buildPositionSensor(BMind mind, Map<OdePhysicalStructure,PhysicsNode> physicsMap, Map<Reference,BComponent> wireMap)
	{
		final SensorType sensor = interpreter.getSensorType();
		
		final PositionSensorSettings settings =(PositionSensorSettings) getSettings(sensor);
		
		final BGenericSource[] sources = new BGenericSource[SensorType.POSITION.getNumberOfConnections()];
		
		float[] minRanges = new float[sensor.getNumberOfConnections()];
		float[] maxRanges = new float[sensor.getNumberOfConnections()];
		
		for (int i = 0;i<sensor.getNumberOfConnections();i++)
		{
			//source.setNormalizingMax();
			//source.setNormalizingMin(settings.getValue(i*2+1));
			maxRanges[i] = 		settings.getValue(i*2);	
			minRanges[i] = 		settings.getValue(i*2 + 1);	
			
			if(i ==1)
			{//swap x and y
				float tempRange = maxRanges[0];
				maxRanges[0] = maxRanges[1];
				maxRanges[1] = tempRange;
				
				
				tempRange = minRanges[0];
				minRanges[0] = minRanges[1];
				minRanges[1] = tempRange;
			}
		}
		
		for(int i = 0;i< sensor.getNumberOfConnections();i++)
		{
			BGenericSource source = new BGenericSource(sensor.getName() + "(" + i + ")");
			source.setNormalizingMax(maxRanges[i]);
			source.setNormalizingMin(minRanges[i]);
			sources[i] = source;
			WirePortInterpreter in = new WirePortInterpreter(interpreter.getOutput(i));

			
			wireMap.put(in.getID(), source);
			mind.addSource(source);
			mind.addComponent(source);
		}
		
		final PhysicsCollisionGeometry geometry = super.getCollisionGeometry();
		final PhysicsNode physicsNode = geometry.getPhysicsNode();
		if(physicsNode == null)
			return;
		
		physicsNode.updateWorldVectors();
		final Vector3f startingWorldTranslation = new Vector3f(physicsNode.getWorldTranslation());
		final boolean isRelative = settings.isRelative();
		if(physicsNode.isStatic())
		{
			//if its static, no need to continue sending observations - they wont change
			sources[0].setLastingObservations(true);
			sources[1].setLastingObservations(true);
			sources[2].setLastingObservations(true);
			physicsNode.updateWorldVectors();
			geometry.updateWorldVectors();
			
			if(interpreter.getSensorType()==SensorType.POSITION)
			{
				Vector3f translation =new Vector3f(geometry.getWorldTranslation());
			
				if(settings.isRelative())
				{
					translation.subtractLocal(startingWorldTranslation);
					geometry.getWorldRotation().inverse().multLocal(translation);
				}
					sources[0].setSensorObservation(translation.getY());
					sources[1].setSensorObservation(translation.getX());
					sources[2].setSensorObservation(translation.getZ());
			}else
			{
				//velocity and acceleration are zero for statics.
				sources[0].setSensorObservation(0);
				sources[1].setSensorObservation(0);
				sources[2].setSensorObservation(0);
			}
			return;
		}
		final DynamicPhysicsNode dynamicPhysicsNode = (DynamicPhysicsNode) physicsNode;
	
		this.getCollisionGeometry().getPhysicsNode().getSpace().addToUpdateCallbacks(new PhysicsUpdateCallback()
		{
			private Vector3f previousVelocity = new Vector3f();
			private Vector3f store = new Vector3f();
			private Vector3f store2 = new Vector3f();
			private Vector3f store3 = new Vector3f();
			private Quaternion quat = new Quaternion();
			
			public void afterStep(PhysicsSpace space, float time) {
				switch(sensor)
				{
					case POSITION:
						Vector3f translation = store.set(geometry.getWorldTranslation());
						translation.subtractLocal(startingWorldTranslation);
						if(isRelative)
						{
							quat.set(geometry.getWorldRotation()).inverseLocal().multLocal(translation);
						}
							sources[0].setSensorObservation(translation.getY());
							sources[1].setSensorObservation(translation.getX());
							sources[2].setSensorObservation(translation.getZ());

						break;
					case VELOCITY:
							Vector3f v = getVelocity(store,isRelative);
						//	System.out.println(v);
						
							sources[0].setSensorObservation(v.getY());
							sources[1].setSensorObservation(v.getX());
							sources[2].setSensorObservation(v.getZ());
						
						break;
					case ACCELERATION:
						Vector3f currentVelocity = getVelocity(store,isRelative);
			
						Vector3f dif = previousVelocity.subtractLocal(currentVelocity).multLocal(-1);
						dif.divideLocal(time);
						sources[0].setSensorObservation(dif.getY());
						sources[1].setSensorObservation(dif.getX());
						sources[2].setSensorObservation(dif.getZ());
						
						previousVelocity.set(currentVelocity);
						break;
				}
			}

			private Vector3f getVelocity(Vector3f store, boolean relative)
			{
				//its important to distinguish between different concepts of local and world coords here.
				//the velocity, whether relative or not, is from the sensors perspective.
				//this only matters when you consider rotation
				//importantly, regardless of using relative or not, the magnitude reported is the same.
				//the differences is just whether the velocity is translated into the sensors world rotation, or not
				
				Quaternion inverseWorldRotation = quat.set(dynamicPhysicsNode.getWorldRotation()).inverseLocal();
				
				//so start by making everything relative, for simplicty
				dynamicPhysicsNode.getLinearVelocity(store);			
				//translate the velocity into the local rotation of the physics node
				inverseWorldRotation.multLocal(store);//store now  holds the linear velocity, relative to the current rotation of the physics node
			
			//	store.zero();
				
				//now, add in the rotational velocity
				Vector3f omega = dynamicPhysicsNode.getAngularVelocity(store2);
				inverseWorldRotation.multLocal(omega);//store 2 now has the angular velocity, in the dynamic node's coordinates
				
				//store3.set(geometry.getLocalTranslation());
				
			//	store2.x *= FastMath.sqrt(store3.y*store3.y + store3.z*store3.z);
			//	store2.y *= FastMath.sqrt(store3.x*store3.x + store3.z*store3.z);
			//	store2.z *= FastMath.sqrt(store3.x*store3.x + store3.y*store3.y);
				
				Vector3f r = store3.set(geometry.getLocalTranslation());
				Vector3f v = omega.crossLocal(r);
				//Vector3f v = omega.multLocal(r.lengthSquared()).crossLocal(r);//possibly this is correct? (derived from wikipedia... but it doesnt seem right)
				//(W*|R|2)xR=v
				
				//store2 now holds the linear velocity that the sensor experiences as a result of the nodes notation
				
				//store.addLocal(store2);//this is now the combined linear velocity that the sensor experiences.		
				store.addLocal(v);
				if(relative)
				{
					geometry.getLocalRotation().multLocal(store);
				}else
				{
					//quat.set(dynamicPhysicsNode.getWorldRotation());
					//quat.inverseLocal();
					//quat.multLocal(store);
					dynamicPhysicsNode.getWorldRotation().multLocal(store);
				}
				
				return store;
			}
			
			public void beforeStep(PhysicsSpace space, float time) {
				
			}
			
		});
	}
	
	private final Vector3f _orientTempUnitX = new Vector3f();
	private final Vector3f _orientTempUnitY = new Vector3f();
	private final Vector3f _orientTempUnitZ = new Vector3f();
	private final Vector3f _orientTempUnitX2 = new Vector3f();
	private final Vector3f _orientTempUnitY2 = new Vector3f();
	private final Vector3f _orientTempUnitZ2 = new Vector3f();
	
	private final Vector3f _orientTempUnitX3 = new Vector3f();
	private final Vector3f _orientTempUnitY3 = new Vector3f();
	private final Vector3f _orientTempUnitZ3 = new Vector3f();	
	
	private final Vector3f _orientTempUnitX4 = new Vector3f();
	private final Vector3f _orientTempUnitY4 = new Vector3f();
	private final Vector3f _orientTempUnitZ4 = new Vector3f();
	
	private final Vector3f _orientTempUnitX5 = new Vector3f();
	private final Vector3f _orientTempUnitY5 = new Vector3f();
	private final Vector3f _orientTempUnitZ5 = new Vector3f();	
	
	private final Vector3f _orientTempUnitX6 = new Vector3f();
	private final Vector3f _orientTempUnitY6 = new Vector3f();
	private final Vector3f _orientTempUnitZ6 = new Vector3f();
	
	
	private final static float STRONG_EPSILON = 1E-5f;

	
	private void strongLocalNormalize(Vector3f vector)
	{
		vector.normalizeLocal();
		if(Math.abs(vector.x)<=STRONG_EPSILON)
			vector.x = 0f;
		if(Math.abs(vector.y) <= STRONG_EPSILON)
			vector.y = 0f;
		if(Math.abs(vector.z) <= STRONG_EPSILON)
			vector.z = 0f;
		vector.normalizeLocal();
	}
	
	private final static Quaternion tempOrientationQuat = new Quaternion();
	private void buildOrientationSensor(BMind mind, Map<OdePhysicalStructure,PhysicsNode> physicsMap, Map<Reference,BComponent> wireMap)
	{
		final PositionSensorSettings settings =(PositionSensorSettings) getSettings(interpreter.getSensorType());
		
		final BGenericSource[] sources = new BGenericSource[SensorType.ORIENTATION.getNumberOfConnections()];
		
		/**
		 * This maps wireports to their respoective orientations.
		 * order: x, y, z orientations
		 */
		int[] portMap = new int[]{2,0,1};
	
		for(int i = 0;i< interpreter.getSensorType().getNumberOfConnections();i++)
		{
			BGenericSource source = new BGenericSource(interpreter.getSensorType().getName() + "(" + i + ")");
			source.setNormalizingMax(FastMath.PI);
			source.setNormalizingMin(-FastMath.PI);
			sources[i] = source;
			WirePortInterpreter in = new WirePortInterpreter(interpreter.getOutput(i));

			wireMap.put(in.getID(), source);
			mind.addSource(source);
			mind.addComponent(source);
		}
		
		final PhysicsCollisionGeometry geometry = super.getCollisionGeometry();
		final PhysicsNode physicsNode = geometry.getPhysicsNode();
		if(physicsNode == null)
			return;
		
		if(physicsNode.isStatic())
		{
			//if its static, no need to continue sending observations - they wont change
			sources[0].setLastingObservations(true);
			sources[1].setLastingObservations(true);
			sources[2].setLastingObservations(true);
			physicsNode.updateWorldVectors();
			geometry.updateWorldVectors();
			
			float[] angles = new float[3];
		
			geometry.getWorldRotation().toAngles(angles);
		
			sources[0].setSensorObservation(angles[1]);
			sources[1].setSensorObservation(angles[2]);//for unknown reasons, the angles have to be rearanged like this...
			sources[2].setSensorObservation(angles[0]);

			return;
		}
	
		final DynamicPhysicsNode dynamicPhysicsNode = (DynamicPhysicsNode) physicsNode;
		this.getCollisionGeometry().getPhysicsNode().getSpace().addToUpdateCallbacks(new PhysicsUpdateCallback()
		{

			
			public void afterStep(PhysicsSpace space, float time) {
				float[] angles = new float[3];
				geometry.updateWorldVectors();
				tempOrientationQuat.set(geometry.getWorldRotation());
			//	tempOrientationQuat.inverseLocal();
				//tempOrientationQuat.toAngles(angles);
				
				//problem:the axis are  the world axis, not local, need to be moved to local axis
				//take unit vectors, rotate them into the local coordinate system, find rotation around them 
				
				Vector3f unitX = _orientTempUnitX.set(Vector3f.UNIT_X);
				geometry.getLocalRotation().multLocal(unitX);//local rotation
				strongLocalNormalize(unitX);
				
				Vector3f unitY =_orientTempUnitY.set(Vector3f.UNIT_Y);
				geometry.getLocalRotation().multLocal(unitY);//local rotation
				strongLocalNormalize(unitY);
				
				Vector3f unitZ = _orientTempUnitZ.set(Vector3f.UNIT_Z);
				geometry.getLocalRotation().multLocal(unitZ);//local rotation
				strongLocalNormalize(unitZ);
				
				Vector3f unitXprime =_orientTempUnitX2.set(Vector3f.UNIT_X);
				geometry.getWorldRotation().multLocal(unitXprime); //world rotation			
				strongLocalNormalize(unitXprime);
				
				Vector3f unitYprime = _orientTempUnitY2.set(Vector3f.UNIT_Y);
				geometry.getWorldRotation().multLocal(unitYprime); //world rotation			
				strongLocalNormalize(unitYprime);
				
				Vector3f unitZprime =_orientTempUnitZ2.set(Vector3f.UNIT_Z);
				geometry.getWorldRotation().multLocal(unitZprime); //world rotation			
				strongLocalNormalize(unitZprime);
				
				//take the angle between these two.. NOT what we want to do. We want to find the rotation around this axis. to do this, project one of the other world rotation unit vectors
				//onto the plane belonging to this unit, and also project one of the local rotation unit vectors.
				//since these are all unit vectors, projection onto the normal is just the dot product in the direction of the normal; then subtract that from the original vector to get the component in the plane.
				
				//not neccesary to project the primes; just project the original angles
				
				Vector3f unrotatedOnPlaneZ =_orientTempUnitZ4.set(unitX).subtractLocal(_orientTempUnitZ3.set(unitZprime).multLocal(unitX.dot(unitZprime))); //the unrotated vector uses X
				Vector3f unrotatedOnPlaneY = _orientTempUnitY4.set(unitZ).subtract(_orientTempUnitY3.set(unitYprime).multLocal(unitZ.dot(unitYprime)));//the unrotated vector uses Z
				Vector3f unrotatedOnPlaneX = _orientTempUnitX4.set(unitY).subtract(_orientTempUnitX3.set(unitXprime).multLocal(unitY.dot(unitXprime)));//the unrotated vector uses Y
				
				Vector3f unrotatedPerpOnPlaneZ =_orientTempUnitZ6.set(unitY).subtractLocal(_orientTempUnitZ5.set(unitZprime).multLocal(unitY.dot(unitZprime))); //the unrotated vector uses X
				Vector3f unrotatedPerpOnPlaneY = _orientTempUnitY6.set(unitX).subtract(_orientTempUnitY5.set(unitYprime).multLocal(unitX.dot(unitYprime)));//the unrotated vector uses Z
				Vector3f unrotatedPerpOnPlaneX = _orientTempUnitX6.set(unitZ).subtract(_orientTempUnitX5.set(unitXprime).multLocal(unitZ.dot(unitXprime)));//the unrotated vector uses Y
				
				/*Vector3f unrotatedPerpOnPlaneZ = unitY.subtract(unitZprime.mult(unitY.dot(unitZprime)));
				Vector3f unrotatedPerpOnPlaneY = unitX.subtract(unitYprime.mult(unitX.dot(unitYprime)));
				Vector3f unrotatedPerpOnPlaneX = unitZ.subtract(unitXprime.mult(unitZ.dot(unitXprime)));*/
				
				strongLocalNormalize(unrotatedOnPlaneX);
				strongLocalNormalize(unrotatedOnPlaneY);
				strongLocalNormalize(unrotatedOnPlaneZ);
				
				strongLocalNormalize(unrotatedPerpOnPlaneX);
				strongLocalNormalize(unrotatedPerpOnPlaneY);
				strongLocalNormalize(unrotatedPerpOnPlaneZ);
				
				angles[0] = unrotatedOnPlaneX.angleBetween(unitYprime);
				angles[0] %=FastMath.PI;
				
				if(unitYprime.dot(unrotatedPerpOnPlaneX)<0f)
					angles[0] = -angles[0];//invert the angle if the y vector is on the bottom side of the plane
				
				if(unitXprime.dot(unitX)<0f) //if the unit x is flipped all around, also flip this
					angles[0] = -angles[0];//invert the angle if the x vector is backwards
				
				if(Math.abs( Math.abs(angles[0]) - FastMath.PI) <=5E-4f)//this is a very 'strong' test because of floating point errors that would otherwise cause jitters.
					angles[0] = 0f;
				
			//	System.out.println(angles[0]);
				
				angles[1] = unrotatedOnPlaneY.angleBetween(unitZprime);
				angles[1] %=FastMath.PI;
				//System.out.println(angles[1]);
				
		
				if(unitZprime.dot(unrotatedPerpOnPlaneY)<0f)
					angles[1] = -angles[1];
				
				if(unitYprime.dot(unitY)<0f) //if the unit x is flipped all around, also flip this
					angles[1] = -angles[1];//invert the angle if the x vector is backwards
		
				
				if(Math.abs( Math.abs(angles[1]) - FastMath.PI)<=5E-4f)
					angles[1] = 0f;
				
				
				
				
				
				angles[2] = unrotatedOnPlaneZ.angleBetween(unitXprime);
				angles[2] %=FastMath.PI;
				
				
				
				if(Math.abs( Math.abs(angles[2]) - FastMath.PI) <=5E-4f)
					angles[2] = 0f;
				
				if(unitXprime.dot(unrotatedPerpOnPlaneZ)<0f)
					angles[2] = -angles[2];
				
				if(unitZprime.dot(unitZ)<0f) //if the unit x is flipped all around, also flip this
					angles[2] = -angles[2];//invert the angle if the x vector is backwards
				
				
				//if the rotation around x is above the z plane, then this is positive, otherwise its negative
				
				
				
				//restrict the angles to plus/- half pi.
				
				
			//	angles[1] %=  FastMath.PI;
			//	angles[2] %=  FastMath.PI;
	/*			if(angles[0]<0)
					angles[0]+= FastMath.TWO_PI;
				if(angles[1]<0)
					angles[1]+= FastMath.TWO_PI;
				if(angles[2]<0)
					angles[2]+= FastMath.TWO_PI;*/
				//System.out.println(Math.round(angles[0]*100f)/100f+"\t" + Math.round(angles[1]*100f)/100f+ "\t" +Math.round(angles[2]*100f)/100f);
			/*		sources[0].setSensorObservation(angles[1]);
					sources[1].setSensorObservation(angles[2]);//for unknown reasons, the angles have to be rearanged like this...
					sources[2].setSensorObservation(angles[0]);*/
				sources[0].setSensorObservation(angles[1]);
				sources[1].setSensorObservation(angles[0]);//for unknown reasons, the angles have to be rearanged like this...
				sources[2].setSensorObservation(angles[2]);
			//		System.out.println(angles[1] + "\t" + angles[2] + "\t" + angles[0]);
			}

			
			public void beforeStep(PhysicsSpace space, float time) {
				
			}
			
		});
	}
	
	
}
