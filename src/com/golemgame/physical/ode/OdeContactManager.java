package com.golemgame.physical.ode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.physical.PhysicsComponent;
import com.golemgame.util.DisjointObjectSet;
import com.golemgame.util.DisjointSet;
import com.jme.intersection.CollisionData;
import com.jme.intersection.TriangleCollisionResults;
import com.jme.scene.Geometry;
import com.jme.scene.Spatial;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;

public class OdeContactManager {
	private float[] signals;
	//private float[] previousSignals;
	private float[] signalSize;
//	private boolean outputsCalculated = false;
//	private OdeContactStructure[] contacts;
	private DisjointSet contactSet ;
	private Collection<OdeContactStructure> odeContacts;
	public void initialize(Collection<OdeContactStructure> odeContacts)
	{
		this.odeContacts = odeContacts;
/*		contacts = new OdeContactStructure[odeContacts.size()];
		for(OdeContactStructure c:odeContacts)
		{
			contacts[c.getContactID()] = c;
		}
		*/
		contactSet = new DisjointSet(odeContacts.size());
		signals = new float[odeContacts.size()];
		signalSize = new float[odeContacts.size()];
		//previousSignals= new float[odeContacts.size()];
	}
	
	public void reportContact(OdeContactStructure contact1, OdeContactStructure contact2) {
		if(contact1==contact2 || contact1 == null || contact2 == null)
			return;
		
		//union contact1 and 2
		contactSet.union(contact1.getContactID(), contact2.getContactID());
		
	}
	
	public void reportInputSignal(OdeContactStructure contact, float input)
	{
		int set = contactSet.find(contact.getContactID());
		signals[set] += input;
		signalSize[set] ++;
	}
	
	public void calculateOutputs()
	{
		//Dont average the outputs
	/*	for(int i = 0;i<signals.length;i++)
		{
			if(signalSize[i]>0)
				signals[i]/=signalSize[i];
		}
		*/
	
	
	}
	
	private float getOutputSignal(OdeContactStructure contact)
	{
	/*	if(!outputsCalculated)
			throw new IllegalStateException("Contact signals not yet calculated");*/
		int set = contactSet.find(contact.getContactID());
		float signal = signals[set];
		if(signal>1f)
			signal = 1f;
		else if (signal<-1f)
			signal = -1f;
		return signal;
	}
	
	public void reset()
	{
		calculateOutputs();
		for(OdeContactStructure c:odeContacts)
		{
			c.reportOutput(getOutputSignal(c));
		}
		
		contactSet.reset();
		for(int i = 0;i<contactSet.size();i++)
		{
			signals[i] = 0;//put each contact in a unique set
			signalSize[i] = 0;
			//previousSignals[i]=0;
		}
	}

	public static List<Collection<OdeContactStructure>>  buildContactGroups(ArrayList<OdePhysicalStructure> physicalList, Map<OdePhysicalStructure, PhysicsNode> physicalMap, Map<PhysicsCollisionGeometry, PhysicsComponent> compMap) {
		DisjointObjectSet<OdeContactStructure> connectingSets = new DisjointObjectSet<OdeContactStructure>();

		
		ArrayList<Collection<OdeContactStructure>> contactSets = new ArrayList<Collection<OdeContactStructure>>();
		int i = 0;
		Map<PhysicsNode, Integer> physicsEnum = new HashMap<PhysicsNode,Integer>();
		ArrayList<PhysicsNode> physicsNodes = new ArrayList<PhysicsNode>();
		for(PhysicsNode p:physicalMap.values())
		{			
			physicsEnum.put(p,(i++));
			physicsNodes.add(p);
			contactSets.add(new ArrayList<OdeContactStructure>());
		}
		
		for(OdePhysicalStructure s:physicalList)
		{
			if(s instanceof OdeContactStructure)
			{
				contactSets.get(physicsEnum.get(physicalMap.get(s))).add((OdeContactStructure)s);
			}
		}
		
		for(Collection<OdeContactStructure> c: contactSets)
		{

			for(OdeContactStructure s:c)
			{
				connectingSets.addElement(s);
		
			}
		}
		
		for(Collection<OdeContactStructure> c: contactSets)
		{
			Map<Spatial,OdeContactStructure> geomMap = new HashMap<Spatial,OdeContactStructure>();
			for(OdeContactStructure s:c)
			{
				geomMap.put(compMap.get(s.getCollision()).getSpatial().getSpatial(),s);
			}
			for(OdeContactStructure s:c)
			{
				PhysicsNode p = physicalMap.get(s);
				TriangleCollisionResults results = new TriangleCollisionResults();
				
				compMap.get(s.getCollision()).getSpatial().getSpatial().findCollisions(p, results);
				
				for (int e = 0; e<results.getNumber(); e++)
				{
					CollisionData data = results.getCollisionData(e);
					//if (!data.getTargetTris().isEmpty() && !data.getSourceTris().isEmpty())
					{
						Geometry sourceSpatial = data.getSourceMesh();
						Geometry targetSpatial = data.getTargetMesh();

						//only test further if both geometries belong to contacts
						if(geomMap.containsKey(targetSpatial) && geomMap.containsKey(sourceSpatial))
						{
							//ensure the trangles also collide
							
							//THIS is the slow call
							if(!SpatialModel.trianglesCollide(sourceSpatial, targetSpatial))
								continue;
							//search up the target model's parents until the collision model is found
							//this is to ensure that the collision models are actually members of the intended targets...

							connectingSets.union(geomMap.get(targetSpatial), geomMap.get(sourceSpatial));
						}
						
												
					
							
					}				
				}
				
			}
			
		}
		
		//we now have a complete set of all touching contacts in the machine; assign to each set a contactID.
		List<Collection<OdeContactStructure>> enumeration = connectingSets.enumerateSets();
		
		
		return enumeration;
	}
	
}
