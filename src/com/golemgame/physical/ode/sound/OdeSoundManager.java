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
package com.golemgame.physical.ode.sound;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.odejava.JointFeedback;

import com.golemgame.mvc.golems.SurfacePropertiesInterpreter.SurfaceType;
import com.golemgame.physical.PhysicsEnvironment;
import com.golemgame.physical.ode.OdePhysicsComponent;
import com.golemgame.physical.ode.OdePhysicsObject;
import com.golemgame.physical.ode.OdePhysicsWorld;
import com.golemgame.physical.ode.compile.OdeCompiledPhysical;
import com.golemgame.physical.sound.SoundComponent;
import com.golemgame.states.StateManager;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsUpdateCallback;
import com.jmex.physics.contact.ContactCallback;
import com.jmex.physics.contact.ContactInfo;
import com.jmex.physics.contact.PendingContact;
import com.jmex.physics.geometry.PhysicsMesh;
import com.jmex.physics.impl.ode.OdePendingContact;
import com.jphya.body.Body;
import com.jphya.contact.Contact;
import com.jphya.contact.ContactDynamicData;
import com.jphya.impact.Impact;
import com.jphya.impact.ImpactDynamicData;
import com.jphya.resonator.ModalData;
import com.jphya.resonator.Resonator;
import com.jphya.scene.Scene;

public class OdeSoundManager implements PhysicsUpdateCallback{
	private Set<SoundComponent> involvedComponents = new HashSet<SoundComponent>();
	//InputHandler collisionEvents = new InputHandler();
	//could improve things alot by treating big objects as single, solid resonators... possibly combining their profiles?
	//no, take all connected objects of the same resonance profile and combine them into one larger simpler object
	
	private int maxSounds = 64;
	private int maxImpactsPerSecond = 32;
	private int impactsThisSecond = 0;
	public int getMaxSounds() {
		return maxSounds;
	}

	public void setMaxSounds(int maxSounds) {
		this.maxSounds = maxSounds;
	}
	private Lock componentLock = new ReentrantLock();
	private static ModalData wood;
	private static ModalData tin;
	private static ModalData felt;
	private static ModalData glass;
	private static ModalData chord;
	private static ModalData wood2;
	private static ModalData cushion;
	private static ModalData brick;
	private static ModalData carpet;
	private static ModalData cardboard;
	private static ModalData plastic;
	private static ModalData plastic2;
	private static ModalData plastic3;
	private static ModalData ring;
	private static ModalData styrofoam;
	private static ModalData vent;
	private static ModalData bongo;

	private static ModalData pipe;
	
	/*
	 * 	GLASS("Glass"),TIN("Tin"),WOOD("Wood"),FELT("Felt"),CHORD("Chords"),NONE("(No sound)"), INFER(""),
		BOOK("Book"),BRICK("Brick"),CARDBOARD("Cardboard"),PAPER("Paper"),CUSHION("Cushion"),PIPE("Pipe"),PLASTIC("Plastic"),
		PLASTIC2("Plastic 2"),PLASTIC3("Plastic 3"),RING("Ring"),STYROFOAM("Styrofoam"),VENT("Vent"),WOOD2("Wood2"),WOOD3("Wood3");
		
		
	 */
	static{
		cushion = new ModalData();
		try {
			cushion.read( new BufferedInputStream( StateManager.loadResource("com/golemgame/data/audio/md/cushion.md").openStream() ));
		} catch (IOException e) {
			StateManager.logError(e);
		}
			wood = new ModalData();
			try {
				wood.read( new BufferedInputStream( StateManager.loadResource("com/golemgame/data/audio/md/wood.md").openStream() ));
			} catch (IOException e) {
				StateManager.logError(e);
			}
			wood2 = new ModalData();
			try {
				wood2.read( new BufferedInputStream( StateManager.loadResource("com/golemgame/data/audio/md/woodhollow.md").openStream() ));
			} catch (IOException e) {
				StateManager.logError(e);
			}
			
	/*		wood3 = new ModalData();
			try {
				wood3.read( new BufferedInputStream( StateManager.loadResource("com/golemgame/data/audio/md/speakerbox.md").openStream() ));
			} catch (IOException e) {
				StateManager.logError(e);
			}*/
			pipe = new ModalData();
			try {
				pipe.read( new BufferedInputStream( StateManager.loadResource("com/golemgame/data/audio/md/pipe.md").openStream() ));
			} catch (IOException e) {
				StateManager.logError(e);
			}
			tin = new ModalData();
			try {
				tin.read( new BufferedInputStream( StateManager.loadResource("com/golemgame/data/audio/md/bigtin.md").openStream() ));
			} catch (IOException e) {
				StateManager.logError(e);
			}
			
			felt = new ModalData();
			try {
				felt.read( new BufferedInputStream( StateManager.loadResource("com/golemgame/data/audio/md/cardtop.md").openStream() ));
			} catch (IOException e) {
				StateManager.logError(e);
			}
			
			glass = new ModalData();
			try {
				glass.read( new BufferedInputStream( StateManager.loadResource("com/golemgame/data/audio/md/glass.md").openStream() ));
			} catch (IOException e) {
				StateManager.logError(e);
			}
			
			chord = new ModalData();
			try {
				chord.read( new BufferedInputStream( StateManager.loadResource("com/golemgame/data/audio/md/chord.md").openStream() ));
			} catch (IOException e) {
				StateManager.logError(e);
			}
			
			
			carpet = new ModalData();
			try {
				carpet.read( new BufferedInputStream( StateManager.loadResource("com/golemgame/data/audio/md/carpet.md").openStream() ));
			} catch (IOException e) {
				StateManager.logError(e);
			}
			
			vent = new ModalData();
			try {
				vent.read( new BufferedInputStream( StateManager.loadResource("com/golemgame/data/audio/md/vent.md").openStream() ));
			} catch (IOException e) {
				StateManager.logError(e);
			}
			
			
			ring = new ModalData();
			try {
				ring.read( new BufferedInputStream( StateManager.loadResource("com/golemgame/data/audio/md/ring.md").openStream() ));
			} catch (IOException e) {
				StateManager.logError(e);
			}
			
			
			plastic = new ModalData();
			try {
				plastic.read( new BufferedInputStream( StateManager.loadResource("com/golemgame/data/audio/md/plastic.md").openStream() ));
			} catch (IOException e) {
				StateManager.logError(e);
			}
			
			
			plastic2 = new ModalData();
			try {
				plastic2.read( new BufferedInputStream( StateManager.loadResource("com/golemgame/data/audio/md/plastictin1.md").openStream() ));
			} catch (IOException e) {
				StateManager.logError(e);
			}
			
			plastic3 = new ModalData();
			try {
				plastic3.read( new BufferedInputStream( StateManager.loadResource("com/golemgame/data/audio/md/plastictin2.md").openStream() ));
			} catch (IOException e) {
				StateManager.logError(e);
			}
			
			brick = new ModalData();
			try {
				brick.read( new BufferedInputStream( StateManager.loadResource("com/golemgame/data/audio/md/brick.md").openStream() ));
			} catch (IOException e) {
				StateManager.logError(e);
			}
			
			styrofoam = new ModalData();
			try {
				styrofoam.read( new BufferedInputStream( StateManager.loadResource("com/golemgame/data/audio/md/styrofoam.md").openStream() ));
			} catch (IOException e) {
				StateManager.logError(e);
			}
			
			cardboard = new ModalData();
			try {
				cardboard.read( new BufferedInputStream( StateManager.loadResource("com/golemgame/data/audio/md/cardboard.md").openStream() ));
			} catch (IOException e) {
				StateManager.logError(e);
			}
			
			bongo = new ModalData();
			try {
				bongo.read( new BufferedInputStream( StateManager.loadResource("com/golemgame/data/audio/md/bongo.md").openStream() ));
			} catch (IOException e) {
				StateManager.logError(e);
			}
			
			
	}
	
	public static ModalData getModalData(SurfaceType s)
	{
		switch(s)
		{
			case WOOD:
				return wood;
			case FELT:
				return felt;
			case GLASS:
				return glass;
			case TIN:
				return tin;
			case CHORD:
				return chord;
				
			case WOOD2:
				return wood2;
	
			case CARPET:
				return carpet;
			case BRICK:
				return brick;
			case STYROFOAM:
				return styrofoam;
				
			case PLASTIC:
				return plastic;
			case PLASTIC2:
				return plastic2;
			case PLASTIC3:
				return plastic3;
			case BONGO:
				return bongo;
			case RING:
				return ring;
			case PIPE:
				return pipe;
			case CUSHION:
				return cushion;
			default:
				return null;
		}
		
	}
	

	public OdeSoundManager() {
		super();
		
		//this has two jobs: detect impacts (initial collisions) and contacts (continued collisions), between objects with sounds.
		//this has to deal with some noise, if things touch and come out of contact frequently.
		//report these, along with their surface properties, to jphya.
		//since ode only reports when two objects collide, and not whether it is a continued collision from before, or a new impact,
		//have to decide that here.
	
	
	}
	private OdePhysicsWorld physics;
	public OdePhysicsWorld getPhysics() {
		return physics;
	}

	public void setPhysics(OdePhysicsWorld physics) {
		this.physics = physics;
	}
	private PhysicsEnvironment environment;
	
	public PhysicsEnvironment getEnvironment() {
		return environment;
	}

	public void setEnvironment(PhysicsEnvironment environment) {
		this.environment = environment;
	}

	public void attach(OdeCompiledPhysical compiledWorld)
	{	
		detach();
		this.physics = compiledWorld.getPhysicsWorld();
		environment = compiledWorld.getCompiledEnvironment();
		ContactCallback callback = new ContactCallback(){

			public boolean adjustContact(PendingContact contact) {
				   processCollision(contact,contact.getTime());
				return false;
			}
			
		};
		this.physics.getPhysics().getContactCallbacks().add(callback);
		
/*		collisionEvents.addAction( new InputAction() {
            public void performAction( InputActionEvent evt ) {
                ContactInfo info = (ContactInfo) evt.getTriggerData();
                
                processCollision(info,evt.getTime());
                
            }

	
        }, physics.getPhysics().getCollisionEventHandler(), false );*/
	
	}
	
	/**
	 * Using this as a set, but with more access
	 */
	private Map<PhyaCollision,PhyaCollision> collisions = new HashMap<PhyaCollision,PhyaCollision>();
	
	private ArrayList<PhyaCollision> sortedCollisions = new ArrayList<PhyaCollision>();
	
	
	private Map<OdePhysicsObject,Integer> numberOfCollisions = new HashMap<OdePhysicsObject,Integer>();
	
	private static ArrayList<JointFeedback> feedbackset = new ArrayList<JointFeedback>();
	private static int lastUsedFeedback = 0;
	
	private static JointFeedback registerContact(OdePendingContact contact)
	{
		JointFeedback feedback = null;
		if(lastUsedFeedback<feedbackset.size())
		{
			feedback = feedbackset.get(lastUsedFeedback++);
		}else{
			feedback = new JointFeedback();
			feedbackset.add(feedback);
			lastUsedFeedback++;
		}
		contact.enableJointFeedback(feedback);
		return feedback;
	}

	private int currentCollisions = 0;
	private void processCollision(ContactInfo info, float time) {
		PhysicsCollisionGeometry geom1 = info.getGeometry1();
		PhysicsCollisionGeometry geom2 = info.getGeometry2();
		
		OdePhysicsComponent comp1 = physics.getPhysicsComponent(geom1);
		OdePhysicsComponent comp2 =physics.getPhysicsComponent(geom2);
		
		if(comp1 == null||comp2 == null)
			return;
		
		
		
		if(((comp1.getSoundComponent()==null)|| (comp2.getSoundComponent()==null)))
			return;
		
		
		
		//determine if we already have a collision like this
		
		testCollision.component1 = comp1;
		testCollision.component2 = comp2;
		PhyaCollision collision = collisions.get(testCollision);
		
		//we are going to need to find out the contact force that occurs after the time step.
	/*
		if(comp1.getSoundComponent().getBody().getSurface()==null)
		{
			WhiteFunction whitefun = new WhiteFunction();
			FunctionSurface whitesurf = new FunctionSurface(this.environment.getSoundScene());
			
			whitesurf.setFun(whitefun); // White noise surface texture.
			whitesurf.setContactMasterGain(32000.0f); 
			whitesurf.setCutoffFreqAtRoll(10.0f); 												
			whitesurf.setCutoffFreqRate(1000.0f); 												
			whitesurf.setCutoffFreq2AtRoll(10.0f); 
			whitesurf.setCutoffFreq2Rate(1000.0f);
			whitesurf.setContactDirectGain(0.0f); 											
			whitesurf.setContactAmpLimit(1000);
			
			comp1.getSoundComponent().getBody().setSurface(whitesurf);
			comp1.getSoundComponent().getBody().setDistanceModel(inverseDistance);
			ModalResonator mr  = new ModalResonator(this.environment.getSoundScene());
			mr.setData(data);
			mr.setQuietLevel(1.0f); // Determines at what rms envelope level
									// a resonator will be
			// faded out when no longer in contact, to save cpu.
			// Make bigger to save more cpu, but possibly truncate decays
			// notceably.

			// mr.setnActiveModes(10); // Can trade detail for speed.
			mr.setAuxAmpScale(0.01f);
			float freq = comp1.getSoundComponent().getFrequencyScale();
			mr.setAuxFreqScale(freq/2f);
		//	System.out.println(freq);
			//mr.setAuxFreqScale(0.6f/comp1.getMass());
			mr.setAuxDampScale(1f);
			
			comp1.getSoundComponent().getBody().setRes(mr); // NB Possible to have several bodies using
								// one res for efficiency.
			comp1.getSoundComponent().getBody().setSurface(whitesurf);
		
			comp1.getSoundComponent().getBody().setCurrentDistance(StateManager.getCameraManager().distanceTo(comp1.getCollisionGeometry().getWorldTranslation())/100f);
			
			
		}
		
		if(comp2.getSoundComponent().getBody().getSurface()==null)
		{
			WhiteFunction whitefun = new WhiteFunction();
			FunctionSurface whitesurf = new FunctionSurface(this.environment.getSoundScene());
			
			whitesurf.setFun(whitefun); // White noise surface texture.
			whitesurf.setContactMasterGain(32000.0f); 
			whitesurf.setCutoffFreqAtRoll(10.0f); 												
			whitesurf.setCutoffFreqRate(1000.0f); 												
			whitesurf.setCutoffFreq2AtRoll(10.0f); 
			whitesurf.setCutoffFreq2Rate(1000.0f);
			whitesurf.setContactDirectGain(0.0f); 	
			//whitesurf.setHardness(2);
			whitesurf.setContactAmpLimit(1000);
			
			
			comp2.getSoundComponent().getBody().setSurface(whitesurf);
			comp2.getSoundComponent().getBody().setDistanceModel(inverseDistance);
			
			ModalResonator mr  = new ModalResonator(this.environment.getSoundScene());
			mr.setData(data);
			mr.setQuietLevel(1.0f); 

			// mr.setnActiveModes(10); // Can trade detail for speed.
			mr.setAuxAmpScale(0.01f);
		//	mr.setAuxFreqScale(0.5f + 0.1f * 1);
			float freq = comp2.getSoundComponent().getFrequencyScale();
		//	System.out.println(freq);
			mr.setAuxFreqScale(freq/2f);
			mr.setAuxDampScale(1f);
			
			comp2.getSoundComponent().getBody().setRes(mr); // NB Possible to have several bodies using
								// one res for efficiency.
			comp2.getSoundComponent().getBody().setSurface(whitesurf);
	
			comp2.getSoundComponent().getBody().setCurrentDistance(StateManager.getCameraManager().distanceTo(comp2.getCollisionGeometry().getWorldTranslation())/100f);
			
			
		}*/
		
		if(collision != null)
		{
			collision.update(info,time);
		}else if( environment.getSoundScene().getActiveResonators().size()<maxSounds) {
			//this.impactsThisSecond ++;
			currentCollisions++;
			componentLock.lock();
			try{
				involvedComponents.add(comp1.getSoundComponent());
				involvedComponents.add(comp2.getSoundComponent());
			}finally{
				componentLock.unlock();
			}
			PhyaCollision newCollision = new PhyaCollision(comp1,comp2,time);
			collisions.put(newCollision, newCollision);//map against itself.
			sortedCollisions.add(newCollision);
			newCollision.update(info,time);
		}
		
	}
	
	private Comparator<Resonator> resComparator = new Comparator<Resonator>(){

		public int compare(Resonator o1, Resonator o2) {
			
			return (int)- FastMath.sign(o1.estimateVolume()-o2.estimateVolume());
		}
		
	};
	private int updateRun =0;
	public void update()
	{			
		
	/*		environment.getSoundScene().lock();
			try{
				if(environment.getSoundScene().getActiveContacts().size()> maxSounds + 8)//add some wiggle room 
				{
				//	System.out.println(environment.getSoundScene().getActiveResonators().size());
					for(int i = this.maxSounds;i<environment.getSoundScene().getActiveContacts().size();i++)
						environment.getSoundScene().getActiveContacts().get(i).fadeAndDelete();
					
				}
	
				if(environment.getSoundScene().getActiveResonators().size()> maxSounds + 8)//add some wiggle room 
				{
					//System.out.println(environment.getSoundScene().getActiveResonators().size());
				//	Collections.sort(environment.getSoundScene().getActiveResonators(),resComparator);
	
					
					for(int i = maxSounds;i<environment.getSoundScene().getActiveResonators().size();i++)
						environment.getSoundScene().getActiveResonators().get(i).makeQuiet();
					
				}
				
			}finally{
				environment.getSoundScene().unlock();
			}*/
			
		//at most every ten runs, update the resonator order so that quiet items get clipped first.
		/*	if(updateRun++ %10 == 0){
			environment.getSoundScene().lock();
			try{
				if(environment.getSoundScene().getActiveResonators().size()> environment.getSoundScene().getMaxResonators())//add some wiggle room 
				{
					Collections.sort(environment.getSoundScene().getActiveResonators(),resComparator);
					
				}
				
			}finally{
				environment.getSoundScene().unlock();
			}
			}*/
		
			this.environment.getSoundScene().generate();
	
			environment.getSoundScene().lock();
			try{
				componentLock.lock();
				try{
				//update object positions
					for(SoundComponent comp:this.involvedComponents)
					{
						comp.getBody().setPreviousDistance(comp.getBody().getCurrentDistance());
					}
				}finally{
					componentLock.unlock();
				}
			}finally{
				environment.getSoundScene().unlock();
			}
	}
	
	public void detach()
	{
	//	collisionEvents.clearActions();
		this.physics = null;
	}
	private final Vector3f positionStore = new Vector3f();
	public void afterStep(PhysicsSpace space,float time) {
		
		//update all sound objects here
		
		this.environment.getSoundScene().lock();
		try{
			componentLock.lock();
			try{
				for(SoundComponent soundComponent:involvedComponents)
				{
					Body body1 = soundComponent.getBody();
				
					float dist1 = StateManager.getCameraManager().distanceTo(soundComponent.getPhysicsObject().getWorldTranslation(positionStore))/100f;//convert to cm
					if(Float.isNaN( body1.getCurrentDistance())){
						body1.setPreviousDistance(dist1);
					}else{
						body1.setPreviousDistance(body1.getCurrentDistance());
					}
					
					body1.setCurrentDistance(dist1);
				
				}
			}finally{
				componentLock.unlock();
			}
		}finally{
			this.environment.getSoundScene().unlock();
		}
		
		numberOfCollisions.clear();
		
		Collections.sort( sortedCollisions);
	//	environment.getSoundScene().lock();
		int curPos = 0;
		try{
			Iterator<PhyaCollision> it = this.sortedCollisions.iterator();
			while(it.hasNext())
			{
				PhyaCollision collision = it.next();
				if(curPos++>this.maxSounds)//suppress the weakest sounds
				{
				//	collision.fadeOut();//note: this doesnt remove the object from the list					
				}
				if(!collision.process(environment.getSoundScene(),time))
				{
					it.remove();
					this.collisions.remove(collision);
				}
				
			}		
		//update();
		}catch(Exception e)
		{
			StateManager.logError(e);
		}finally{
	//		environment.getSoundScene().unlock();
		}
	}

	private float timeSinceImpactClear = 0f;
	public void beforeStep(PhysicsSpace space, float time) {
		timeSinceImpactClear+=time;
		//if(timeSinceImpactClear>=1f)
		{
			timeSinceImpactClear = 0f;
			impactsThisSecond = 0;
		}
		numberOfCollisions.clear();	
		lastUsedFeedback = 0;
		currentCollisions=0;
	}
	
	private static final PhyaCollision testCollision = new PhyaCollision(null,null,0);
	

	
	private static class PhyaCollision implements Comparable<PhyaCollision>
	{
		private static Vector3f _vector = new Vector3f();
		
		private final static int LIFETIME = 4;//how many (non-contacting) physics frames to let go by before letting a new impact occur, as opposed to maintaing a contact
		
		private boolean suppress = false;
		
		/**
		 * Number of physics frames since the last collision
		 */
		private int age = 0;
		private float contactVelocity =0;
		private float collisionTime;		
	
		private Vector3f contactNormal = new Vector3f();
		private boolean hasImpact = false;
		private Impact impact;
		private Collection<Contact> contacts = new ArrayList<Contact>();
		private Contact contact = null;
		private OdePhysicsComponent component1;
		private OdePhysicsComponent component2;
		
		private SoundComponent soundComponent1;
		private SoundComponent soundComponent2;
		
	//	private Vector3f velocity = new Vector3f();
		private Vector3f contactPosition = new Vector3f();
		private Vector3f relPosition2 = new Vector3f();
		private Vector3f relPosition1 = new Vector3f();
		private JointFeedback feedback = null;
		private float tangentialSpeed = 0;
		//note: this has to be able to handle multiple contact points between the same two components!
		
		private PhyaCollision(OdePhysicsComponent component1,OdePhysicsComponent component2,float collisionTime) {
			super();
			this.collisionTime = collisionTime;
			this.component1 = component1;
			this.component2 = component2;
			if(component1 != null)
				soundComponent1 = component1.getSoundComponent();
			if(component2 !=null)
				soundComponent2 = component2.getSoundComponent();
		}
		private final static Vector3f tangentSpeed = new Vector3f();
		private final static Vector3f rotationalSpeed = new Vector3f();
		private final static Vector3f tangentSpeed2 = new Vector3f();
		private final static Vector3f rotationalSpeed2 = new Vector3f();
		private final static Vector3f postSpeed = new Vector3f();
		private final static Vector3f tangentDirection = new Vector3f();
		
		private final static Vector3f positionStore = new Vector3f();
		/**
		 * Process this collision in the sound thread. 
		 * @return false if this collision has now ended and should be removed from updating.
		 */
		public boolean process(Scene scene,float tpf)
		{
			age++;
			if(age>=LIFETIME)
			{
		
				if(contact!=null)
					contact.fadeAndDelete();
				
				return false;
			}
		
			if(contact!=null && ( !contact.isActive()))
			{
				contact = null;
			}
			
			Body body1 = soundComponent1.getBody();
			Body body2 = soundComponent2.getBody();
	
			if(Float.isNaN(body1.getCurrentDistance() ))
			{				
				float dist1 = StateManager.getCameraManager().distanceTo(soundComponent1.getPhysicsObject().getWorldTranslation(positionStore))/100f;//convert to cm
	
				body1.setPreviousDistance(dist1);				
				body1.setCurrentDistance(dist1);
			}
			
			if(Float.isNaN(body2.getCurrentDistance() ))
			{				
				float dist2 = StateManager.getCameraManager().distanceTo(soundComponent2.getPhysicsObject().getWorldTranslation(positionStore))/100f;//convert to cm
	
				body2.setPreviousDistance(dist2);				
				body2.setCurrentDistance(dist2);
			}
			
			scene.lock();
			try{
				if(impact==null)
				{
					if(!suppress){
					//create a new impact to model this collision
						float impulse = feedback.getForce1().length()*tpf;
						
						if(component1.getCollisionGeometry() instanceof PhysicsMesh || component2.getCollisionGeometry() instanceof PhysicsMesh)
						{
							impulse = contactVelocity*Math.min(component1.getMass(), component2.getMass());//use a very rough approximation for trimeshes, since the impulse data seems to be unreliable
						}
						
						//System.out.println(impulse + "\t" + component1.getMass() + "\t"  + component2.getMass());
						if(impulse!=0)
						{	
						
							ImpactDynamicData data = new ImpactDynamicData();				
							data.relNormalSpeedAtImpact = contactVelocity;		
							data.relTangentSpeedAtImpact =tangentialSpeed;				
							data.impactImpulse =impulse;
							
							impact =Impact.newImpact(scene);
							impact.setDynamicData(data);
							
							impact.setSurface1(body1.getSurface());			
							impact.setBody1(body1);
							impact.setSurface2(body2.getSurface());
							impact.setBody2(body2);
							hasImpact = true;//register is as having an impact already, so we dont create a contact
						}else{
							impact = null;
						}
					}else{
						hasImpact = true;//register is as having an impact already, so we dont create a contact
					}
					
				}else if (hasImpact && (contact==null )){// && scene.getActiveContacts().size()<maxSounds){
					
					//create a contact now (just one for now)
					 contact = Contact.newContact(scene);
					 ContactDynamicData data = new ContactDynamicData();
					
				//	 data.speedContactRelBody1 = tangentialSpeed;
					// data.speedContactRelBody2=-tangentialSpeed;
					 //have to do special stuff for rotational vs linear speed here, possibly...
					 
					 contact.setDynamicData(data);
					 contact.setBody1(body1);
					//contact.setSurface1(body1.getSurface());
					contact.setBody2(body2);
					
					//contact.setSurface2(body2.getSurface());
				}
				if(contact!=null && contact.isActive())
				{
					 ContactDynamicData data = contact.getDynamicData();
					 if(data == null)//watch this... it should be happening
					 {
						data = new ContactDynamicData();
		
								 contact.setDynamicData(data);
								 contact.setBody1(body1);
								//contact.setSurface1(body1.getSurface());
								contact.setBody2(body2);
					 }
					 Vector3f force = feedback.getForce1();
					 if(force!=null)
						 data.contactForce =  feedback.getForce1().length();//not impulsei
					 else
						 data.contactForce = 0;
					 
					//	 System.out.println(tangentialSpeed);
					 data.speedBody1RelBody2 = tangentialSpeed;
					 data.speedContactRelBody2 = tangentialSpeed;
					 data.speedContactRelBody1 = 0;
				}
				
			}finally{
				scene.unlock();
			}
			return true;
		}
		

		public void fadeOut()
		{
			suppress = true;
			if(this.contact!=null)
			{
				this.contact.fadeAndDelete();
			}
		}
		
		private final static Vector3f totalRelativeVelocity = new Vector3f();
		private final static Vector3f relativeVelocity = new Vector3f();
		
	
		public void update(ContactInfo info, float time) {
			age = 0;
			contactVelocity = info.getContactVelocity(_vector).length();
			info.getContactNormal(contactNormal);
			info.getContactPosition(contactPosition);
			relPosition1.set(contactPosition);
			relPosition2.set(contactPosition);
			relPosition1.subtractLocal(info.getNode1().getLocalTranslation());
			relPosition2.subtractLocal(info.getNode2().getLocalTranslation());
			
			totalRelativeVelocity.zero();
			if(!component1.getParent().isStatic())
			{
				((DynamicPhysicsNode)component1.getParent().getPhysicsNode()).getLinearVelocity(relativeVelocity);
				totalRelativeVelocity.subtractLocal(relativeVelocity);
			     ( (DynamicPhysicsNode)component1.getParent().getPhysicsNode() ).getAngularVelocity(relativeVelocity);
		   
			     totalRelativeVelocity.subtractLocal( relativeVelocity.crossLocal(relPosition1));//add in the angular velocity component of node 1
		           
			}
       
			if(!component2.getParent().isStatic())
			{
				((DynamicPhysicsNode)component2.getParent().getPhysicsNode()).getLinearVelocity(relativeVelocity);
				totalRelativeVelocity.addLocal(relativeVelocity);
			     ( (DynamicPhysicsNode)component2.getParent().getPhysicsNode() ).getAngularVelocity(relativeVelocity);		   
			     totalRelativeVelocity.addLocal( relativeVelocity.crossLocal(relPosition2));//add in the angular velocity component of node 1
		        
			}
			
			this.tangentialSpeed = FastMath.sqrt(totalRelativeVelocity.lengthSquared() - contactVelocity*contactVelocity);
			if(Float.isNaN(tangentialSpeed)||Float.isInfinite(tangentialSpeed))
				tangentialSpeed = 0f;
			//might be able to skip this if we know it wont produce an impact
			feedback=	registerContact( ((OdePendingContact)info));
		}

		@Override
		public int hashCode() {
			//NOTE: these have been modified to be order independent.
			int result = 1;
			result = result + ((component1 == null) ? 0 : component1.hashCode());
			result = result + ((component2 == null) ? 0 : component2.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			//NOTE: these have been modified to be order independent.
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PhyaCollision other = (PhyaCollision) obj;
			if (other.component1 == this.component1 && other.component2 == this.component2)
				return true;
			if (other.component1 == this.component2 && other.component2 == this.component1)
				return true;
			return true;
		}

		public float estimateVolume()
		{
			float dist = Math.min(soundComponent1.getBody().getCurrentDistance(), soundComponent2.getBody().getCurrentDistance());
			return contactVelocity * 1f/(dist*dist );
		}

		public int compareTo(PhyaCollision o) {
			int comp = (int) Math.signum(o.estimateVolume()-estimateVolume());
			if(comp == 0)
				return (int) Math.signum(Math.random()-0.5);
			return comp;
		}
		
	}
	
}
