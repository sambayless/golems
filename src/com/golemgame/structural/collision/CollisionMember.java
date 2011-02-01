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
package com.golemgame.structural.collision;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.golemgame.model.Model;
import com.golemgame.model.ModelListener;
import com.golemgame.model.ParentModel;
import com.golemgame.model.spatial.NodeModel;
import com.golemgame.states.StateManager;
import com.golemgame.structural.collision.CollisionMember.Contact.FailedContactException;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.information.SelectionInformation;
import com.golemgame.tool.action.information.StaticMaterialInformation;
import com.golemgame.util.Deletable;
import com.golemgame.util.event.ListenerList;


/**
 * A collision member is a single collision unit, consisting of one or more models.
 * @author Sam
 *
 */
public class CollisionMember implements ModelListener, Serializable, Deletable{
	private static final long serialVersionUID = 1;
	
	private boolean needsResolution = false;
	
	/**
	 * Generates hash numbers
	 */

	

	private ParentModel model;
	private Actionable actionable;

	private CollisionManager collisionManager;
	protected Set<Contact> contacts;
	protected Set<Model> models;
	private ListenerList<CollisionListener> collisionListeners = new ListenerList<CollisionListener>();
//	private int hashCode;//instead of an integer hashcode, which has problems for loading from files...
	//use a unique object, which serialization will ensure is unique, and from which we can grab the hashcode.
	private boolean deleted = false;

	private List<Model> constantIgnoreList = new ArrayList<Model>();

	public void addModelToIgnoreList(Model model)
	{
		this.constantIgnoreList.add(model);
	}
	
	public void clearConstantIgnoreList()
	{
		this.constantIgnoreList.clear();
	}
	
	public boolean isStatic()
	{
		for(Model model:this.getCollisionModels())
		{
			if(model.getActionable()!=null)
			{
				
				try{
					StaticMaterialInformation staticInfo =(StaticMaterialInformation) model.getActionable().getAction(Action.STATIC_INFO);
					if(staticInfo.isStatic())
						return true;
				}catch(ActionTypeException e)
				{
					
				}
			}
		}
		return false;
	}
	
	public boolean isNeedsResolution() {
		return needsResolution;
	}

	public void setNeedsResolution(boolean needsResolution) {
		this.needsResolution = needsResolution;
	}

	public boolean propagatesCollisions()
	{
		return true;
	}

	
	public void modelMoved(Model source) {
		needsResolution = true;
		
	}

	public void registerCollidingModel(Model model)
	{
		if(models.add(model))
		{
			model.addModelListener(this);
			model.registerCollisionMember(this);
		//	this.getCollisionManager().putModel(model, this);
			//this.getModel().addChild(model);
		}
		
	}
	
	public void removeCollidingModel(Model model)
	{
		if(models.remove(model))
		{
			model.removeModelListener(this);
			model.removeCollisionMember(this);
		//	this.getCollisionManager().removeModel(model);
			//this.getModel().detachChild(model);
		}
	}



	public CollisionMember(ParentModel model,Actionable actionable) {
		super();
		
	

		this.actionable = actionable;
		models = new HashSet<Model>();
		contacts = new HashSet<Contact>();
		this.needsResolution = true;
			
		if(model==null)
		{
			model = new NodeModel();
		}
		this.model = model;
		model.registerCollisionMember(this);
		model.addModelListener(this);
		


	}
	
	public Collection<CollisionMember> getContactingMembers()
	{
		return this.getContactingMembers(dummyDivider);
	}

	/**
	 * Get group members immediately contacting this member (not including this member).
	 * @param divider
	 * @return
	 */
	public Collection<CollisionMember> getContactingMembers(GroupDivider divider)
	{
		if(divider == null)
			divider = dummyDivider;
		List<CollisionMember> contactingMembers = new ArrayList<CollisionMember>();
		
		for(Contact contact:this.getContacts())
		{
			if(divider.isGroupable(contact.getOther(this)))
			{
				if(divider.propagatesGroup(contact.getOther(this)) &! divider.dividesGroup(contact.getOther(this)) )
					contactingMembers.add(contact.getOther(this));
			}
		}
		return contactingMembers;
	}
	
	/**
	 * Get all group members touching this member, or touching members touched by this member,
	 * including this member
	 * @param divider
	 * @return
	 */
	public void getGroup(GroupDivider divider,Set<CollisionMember> store)
	{
		if(divider == null)
			divider = dummyDivider;
		
		if(!divider.isGroupable(this))
			return;//dont add this member
		
		if(!store.add(this))
			return;
		
		if(!divider.propagatesGroup(this))
			return;//add this but dont add further members
		
		resolveCollisions();//ensure that all collisions are resolved.
		
		for(Contact contact:this.getContacts().toArray(new Contact[0]))
		{
			if(divider.isGroupable(contact.getOther(this)))
			{
				if(!divider.dividesGroup(contact.getOther(this)))
					contact.getOther(this).getGroup(divider,store);
			}
		}
	
	}

	public ParentModel getModel() {
		return model;
	}
	public Actionable getActionable() {
		return actionable;
	}
	public Set<Contact> getContacts() {
		return contacts;
	}
	
	public boolean addContact(Contact contact)
	{
		return this.getContacts().add(contact);
	}
	
	public boolean removeContact(Contact contact)
	{
		return this.getContacts().remove(contact);
	}
	
	
	
	/**
	 * These are the models in this collision member for which collision detection are performed.
	 * @return
	 */
	public Set<Model> getCollisionModels()
	{
		return models;
	}
	
	public boolean isSelectable()
	{
		try{
			SelectionInformation selectInfo = (SelectionInformation) actionable.getAction(Action.SELECTINFO);
			return selectInfo.isSelectable();
		}catch (ActionTypeException e)
		{
			StateManager.logError(e);
			return true;
		}
	}
	
	public boolean resolveCollisions()
	{
		return resolveCollisions((Set<Model>)null);
	}
	
	
	public boolean resolveCollisionsIgnore(CollisionMember ignore)
	{
		Set<Model> ignoreSet = new HashSet<Model>();
		ignoreSet.addAll(ignore.getCollisionModels());
		return resolveCollisions(ignoreSet);
	}
	private boolean containsAny(Collection<?> c, Collection<?> b)
	{
		for(Object o:b)
			if(c.contains(o))
				return true;
		return false;
	}
	/**
	 * 
	 * @return True if there is any change in the state of the contacts or group of this collision member
	 */
	public boolean resolveCollisions(Set<Model> ignoreList)
	{
	//	if (!this.needsResolution)
	//		return false;
/*	long[] time = new long[6];
	 time[0] = System.nanoTime();
		*/
		/*
		 * Dont forget: one thing that has a big impact on collision time is all the hidden elements: functionals, ghosts.
		 */
		//do this randomly...
		if(Math.random()> 0.75)
		{
			this.collisionManager.updateCollisionMember(this);
		}else
		{
			this.getModel().updateWorldData();
		}
	//	time[1] = System.nanoTime();
		boolean change = false;
		Set<Model> internalIgnoreList = ignoreList;
		if(this.constantIgnoreList!=null)
		{
			 if(ignoreList != null)
				 	internalIgnoreList = new HashSet<Model>(ignoreList);
			 else
				 internalIgnoreList = new HashSet<Model>();
		    
			internalIgnoreList.addAll(this.constantIgnoreList);
		}
		//time[2] = System.nanoTime();
		//First, test this collision node's pre-existing contacts
		Iterator<Contact> i = contacts.iterator();
		while(i.hasNext())
		{
			Contact c = i.next();
			if (!c.checkContact() &! (ignoreList!=null &&  containsAny(internalIgnoreList, c.getOther(this).getCollisionModels()) ))
			{//returns false if the contact fails
				//dont destroy contacts that are known to be good (ie, on the ignore list)
				i.remove();
				c.destroy();
				change = true;
			}
		}
	//	time[3] = System.nanoTime();
		//Now, test for new contacts
		//slow
		Set<CollisionMember> collisions = CollisionManager.findCollisions(this, this.getCollisionManager().getModel(), internalIgnoreList);
	//time[4] = System.nanoTime();
		for (CollisionMember collision:collisions)
		{

			try{
				new Contact(this, collision);
				change = true;
			}catch(FailedContactException e)
			{
				//the contact was not formed.
			}
		
		}
	//time[5] = System.nanoTime();
/*		String times = "CollisionMemeber Resolution\t";
		for (int e = 1;e<time.length;e++)
		{
			times += "\t" + (time[e] - time[e-1])/1000000.0;
		}
		System.out.println(times);*/
		needsResolution = false;
		return change;
		
	}
	
	/**
	 * Returns true if a comparison succeeds
	 */
	public boolean collectContacts(Set<CollisionMember> contactSet, Set<CollisionMember> compare)
	{
		for (Contact contact:contacts)
		{
			CollisionMember other  = contact.getOther(this);
			
			if (compare.contains(other))
				return true;			
			
			if (contactSet.add(other))//if the object was not already in the set, add it
			{
				if (other.collectContacts(contactSet, compare))
					return true;
			}			
		}
		
		return false;
	}
	
	
	
	
	public void deleteGroup()
	{
		if (this.isDeleted())
			return;
		this.deleted = true;
		
		for(CollisionListener listener:collisionListeners)
		{
			listener.notifyDelete();//physical structures listen, and they make sure to remove themselves
			//from the machine when this is called
		}
		
		if (model != null)
			model.removeCollisionMember(this);
	
		collisionManager.removeCollisionMember(this);
	}
	
	public void undeleteGroup()
	{
		if (!this.isDeleted())
			return;
		this.deleted = false;

		if (model != null)
			model.registerCollisionMember(this);
		collisionManager.addCollisionMember(this);
	}
	
	
	public void delete() {
		deleted = true;
		
	
		while(! contacts.isEmpty())
		{
			contacts.iterator().next().destroy();
		}
				
		if (model != null)
			model.removeCollisionMember(this);
	
		collisionManager.removeCollisionMember(this);
	}

	
	public boolean isDeleted() {
		return deleted;
	}

	
	public boolean undelete() {
		if (!deleted)
			return false;
		for(CollisionListener listener:collisionListeners)
		{
			listener.notifyUndelete();
		}
		if (model != null)
			model.registerCollisionMember(this);
		
		collisionManager.addCollisionMember(this);
		this.needsResolution= true;
		this.resolveCollisions();
		
		deleted = false;
		return true;
	}

	public static class Contact implements Serializable{
		private static final long serialVersionUID = 1;

		
			
		private CollisionMember contact1;
		private CollisionMember contact2;
		
		private boolean propagatesCollisions = true;
		
		public boolean isPropagatesCollisions() {
			return propagatesCollisions;
		}

		public void setPropagatesCollisions(boolean propagatesCollisions) {
			this.propagatesCollisions = propagatesCollisions;
		}

	
		
		public Contact(CollisionMember contact1, CollisionMember contact2, boolean permanent, boolean falseContact)throws FailedContactException
		{
			
		
			this.contact1 = contact1;
			this.contact2 = contact2;
		
			boolean success = contact1.addContact(this);
			success |= contact2.addContact(this);

			

	
		}
				
		public Contact(CollisionMember contact1, CollisionMember contact2) throws FailedContactException{

			//currently, only the contact is merged, and not its followers
			this(contact1,contact2, false, false);
			
		}
		
		/**
		 * Whether or not this contact should create a single physical node, or if (like a joint contact) the two contacts may form two different physical nodes
		 * @return
		 */
		public boolean isPhysical()
		{
			return true;
		}		
		
		public boolean destroy()
		{

			contact1.removeContact(this);//why was this contact not listed to begin with?
			contact2.removeContact(this);
			
			return true;
		}

		/**
		 * Return false if the contact is no longer valid. Return true if it is still valid.
		 * @return
		 */
		public boolean checkContact()
		{
			//in the future make this more intelligent by storing releative position/rotation 
			//and just ensuring those are the same.
			return false;
		
			

		}


		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj  instanceof Contact))
				return false;
			//check equality (ignoring order of contacts)
			Contact other = (Contact) obj;
			if (other.contact1 != contact1 && other.contact2 != contact1)
				return false;
			if (other.contact1 != contact2 && other.contact2 != contact2)
				return false;
			
			return true;
		}
		


		public CollisionMember getOther(CollisionMember toCheck)
		{
			return (toCheck == contact1) ? contact2:contact1;
		}

		

		public static class FailedContactException extends Exception
		{
			private static final long serialVersionUID = 1L;

			public FailedContactException() {
				super();
			}

			public FailedContactException(String message, Throwable cause) {
				super(message, cause);
			}

			public FailedContactException(String message) {
				super(message);

			}

			public FailedContactException(Throwable cause) {
				super(cause);
			}
			
		}


		public CollisionMember getContact1() {
			return contact1;
		}

		public CollisionMember getContact2() {
			return contact2;
		}
	

		
		
	}

	public CollisionManager getCollisionManager() {
		return collisionManager;
	}


	public void addCollisionListener(CollisionListener listener)
	{
		this.collisionListeners.addListener(listener);
	}
	
	public void removeCollisionListener(CollisionListener listener)
	{
		this.collisionListeners.removeListener(listener);
	}
	private static final GroupDivider dummyDivider = new GroupDivider()
	{

		
		public boolean dividesGroup(CollisionMember member) {
			return false;
		}
		
		public boolean propagatesGroup(CollisionMember member) {
			return true;
		}

		public boolean isGroupable(CollisionMember member) {
			return  member.isSelectable();
		}
		
		
	};

	public void setManager(CollisionManager collisionManager) {
		this.collisionManager = collisionManager;
		
		for (Model model:this.getCollisionModels())
		{
			model.registerCollisionMember(this);
		}
		
		
	}
	
	
	
	
	
	
}
