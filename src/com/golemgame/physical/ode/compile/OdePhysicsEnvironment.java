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
package com.golemgame.physical.ode.compile;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.golemgame.instrumentation.Instrument;
import com.golemgame.mechanical.InteractionServer;
import com.golemgame.physical.PhysicsEnvironment;
import com.golemgame.physical.ode.OdeContactManager;
import com.golemgame.physical.ode.OdeContactStructure;
import com.golemgame.states.camera.CameraDelegate;
import com.golemgame.states.camera.CameraManager;
import com.golemgame.structural.Interactor;
import com.jmex.physics.CollisionGroup;
import com.jmex.physics.Joint;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpatial;

/**
 * Compiled Environment - there is one of these for the entire compiled physics
 * @author Sam
 *
 */
public class OdePhysicsEnvironment extends PhysicsEnvironment {
	/**
	 * Register the physics involved in sensor detection,
	 * so they can know to avoid detecting each other.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Using this as a hashset.
	 */
	private Map<PhysicsSpatial,PhysicsSpatial> sensors = new WeakHashMap<PhysicsSpatial,PhysicsSpatial>();
	
	private Map<PhysicsNode, OdeCompiledIsland> islandMap = new WeakHashMap<PhysicsNode,OdeCompiledIsland>();
	
	private CollisionGroup sensorCollisionGroup = null;
	
	private Map<Joint,PhysicsNode> staticJointMap = new HashMap<Joint,PhysicsNode>();

	private Map<PhysicsSpatial,OdeContactStructure> contacts = new WeakHashMap<PhysicsSpatial,OdeContactStructure>();
	
	private OdeContactManager contactManager = new OdeContactManager();
	
	private CameraManager cameraManager;
	
	//private int uniqueContactID = 0;
	
	public void setCameraManager(CameraManager cameraManager) {
		this.cameraManager = cameraManager;
	}

	@Override
	public void setCameraDelegate(CameraDelegate camera) {
		if(this.cameraManager!=null)
			this.cameraManager.setCameraDelegate(camera);
		super.setCameraDelegate(camera);
	}

	/**
	 * Generate a unique int for an OdeContact
	 * @return
	 */
	public synchronized int generateUniqueContactID(OdeContactStructure structure) {
		
		for(int i = 0;i<contactSet.size();i++)
		{
			if(contactSet.get(i).contains(structure))
				return i;
		}
		
		throw new IllegalStateException("No contact ID");
	}

	public void registerContact(PhysicsSpatial contact, OdeContactStructure odeGhostContact)
	{
		contacts.put(contact,odeGhostContact);
	}
	
	public boolean isContact(PhysicsSpatial contact)
	{
		return contacts.keySet().contains(contact);
	}
	
	public OdeContactStructure getContact(PhysicsSpatial contact)
	{
		return contacts.get(contact);
	}
	
	
	public CollisionGroup getSensorCollisionGroup() {
		return sensorCollisionGroup;
	}

	public void setSensorCollisionGroup(CollisionGroup sensorCollisionGroup) {
		this.sensorCollisionGroup = sensorCollisionGroup;
	}


	private InteractionServer interactionServer = new InteractionServer();
	
	public void registerInstrument(Instrument instrument)
	{
		//note: implemented in the ode controller.
	}
	
	public void registerSensorSpatial(PhysicsSpatial physics) {
		sensors.put(physics, physics);

	}

	
	public boolean isSensorSpatial(PhysicsSpatial physics) {
		return (sensors.get(physics) != null);
	}

	
	/**
	 * Get the island of physics nodes that are connected to the given physics spatial
	 * @param physics
	 * @return
	 */
	public OdeCompiledIsland getJointIsland(PhysicsSpatial physics) {
		
		return islandMap.get(physics.getPhysicsNode());
	}


	
	public void setIslandMap(Map<PhysicsNode, OdeCompiledIsland> map) {
		islandMap = map;
		
	}



	
	public void registerInteractor(Interactor listener) {
		this.interactionServer.addInteractionListener(listener);
		
	}

	
	public InteractionServer getInteractionServer() {
		return this.interactionServer;
	}
	
	private CollisionGroup rayGroup = null;
	
	public CollisionGroup getRayGroup() {
		return rayGroup;
	}

	public void setRayGroup(CollisionGroup group) {
		rayGroup = group;
	}

	public void setJointStatic(PhysicsNode physicsNode, Joint joint) {
		staticJointMap.put(joint, physicsNode);
	}

	public PhysicsNode getStaticForJoint(Joint joint)
	{
		return staticJointMap.get(joint);
	}

	public OdeContactManager getContactManager() {
		return contactManager;
	}

	public Collection<OdeContactStructure> getContacts() {
		return contacts.values();
	}

	private List<Collection<OdeContactStructure>> contactSet;
	public synchronized void setContactGroups(List<Collection<OdeContactStructure>> contactSet) {
		this.contactSet = contactSet;
		
	}


	

}
