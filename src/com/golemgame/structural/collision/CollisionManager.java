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
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import com.golemgame.model.Model;
import com.golemgame.model.ModelCollision;
import com.golemgame.model.ParentModel;
import com.golemgame.model.spatial.NodeModel;


public class CollisionManager implements Serializable{
	private static final long serialVersionUID = 1;

	/**
	 * This must be weak, otherwise deleted structures will be kept alive via this list
	 */
	private List<WeakReference<CollisionMember>> collisionMembers = new CopyOnWriteArrayList<WeakReference<CollisionMember>>();

	
	private ParentModel model;
	
	public CollisionManager() {
		super();
		this.model = new NodeModel();//new com.golemgame.model.spatial.rstartree.RStarTreeModel();		
		
	}

	public void markAllAsNeedingResolution()
	{
		for (WeakReference<CollisionMember> memberRef:collisionMembers)
		{//this will end up colliding models twice, atleast! Very wasteful!
			//its also probably n^2 or worse...
			CollisionMember member = memberRef.get();
			if(member==null)
				continue;
			member.setNeedsResolution(true);
		}
	}
	
	/**
	 * Remove all collision members and groups
	 */
	public void clear()
	{
		for (Model child:this.getModel().getChildren().toArray(new Model[this.getModel().getChildren().size()]))
				{
					this.getModel().detachChild(child);
				}
		collisionMembers.clear();
	}
	public Collection<Collection<CollisionMember>> getCollisionGroups()
	{
		return this.getCollisionGroups(dummyDivider);
	}
	

	public Collection<Collection<CollisionMember>> getCollisionGroups(GroupDivider divider)
	{

		
		if(divider == null)
			divider = dummyDivider;
		//cache this information later, if its too slow
		ArrayList<Collection<CollisionMember>> groupList = new ArrayList<Collection<CollisionMember>>();
		
		Collection<CollisionMember> nonPropagatingMembers = new ArrayList<CollisionMember>();
		
		
	

		for (WeakReference<CollisionMember> memberRef:collisionMembers)
		{//this will end up colliding models twice, atleast! Very wasteful!
			//its also probably n^2 or worse...
			CollisionMember member = memberRef.get();
			if(member==null)
				continue;
			//if you are non propagating, then you will be stored and analyzed after the remaining members.
			if (!divider.propagatesGroup(member))
			{
				nonPropagatingMembers.add(member);
				continue;
			}
			
			Collection<CollisionMember> memberContacts = member.getContactingMembers(divider);
			
			
			boolean createGroup = true;
			for(Collection<CollisionMember> group:groupList)
			{
				if(containsAny(group,memberContacts))
				{
						addExclusive(group,member);
						addAllExclusive(group,memberContacts);
						createGroup = false;
						break;
				}
			}
			if(createGroup)
			{
				ArrayList<CollisionMember> newGroup = new ArrayList<CollisionMember>();
				newGroup.add(member);
				addAllExclusive(newGroup,memberContacts);
				groupList.add(newGroup);
			}
		}
		
		boolean mightOverlap = true;
		while(mightOverlap)
		{
			mightOverlap = false;
			Collection<CollisionMember> toMerge = null;
			Collection<CollisionMember> toMergeWith = null;
			for(Collection<CollisionMember> group:groupList)
			{
				
				for(Collection<CollisionMember> group2:groupList)
				{
					if(group2 == group)
						break;
					if(containsAny(group,group2))
					{
						toMerge = group2;
						toMergeWith = group;
						break;
					}
				}
				if(toMerge !=null)
				{
					break;
				}
			}
			if(toMerge != null && toMergeWith != null)
			{
				addAllExclusive(toMergeWith,toMerge);
				groupList.remove(toMerge);
				mightOverlap = true;
			}
		}
		
		//once the propagating members have been resolved, determine which (if any) group the non propagating members
		//belong to.
		for (CollisionMember nonPropagating:nonPropagatingMembers)
		{
			Collection<CollisionMember> group = null;
			//determine which (if any) of the preexisting groups have the most collisions with this member
			Collection<CollisionMember> contacts = nonPropagating.getContactingMembers(divider);
			int count = 0;
			for (Collection<CollisionMember> testGroup:groupList)
			{
				int groupCount = countIntersection(testGroup, contacts);
				if(groupCount>count)
				{
					count = groupCount;
					group = testGroup;
				}
			}
			
			if(group == null)
			{
				group = new ArrayList<CollisionMember>();
				groupList.add(group);
			}
			
			group.add(nonPropagating);
			
		}

		return groupList;
	}
	
	private int countIntersection(Collection<CollisionMember> testGroup,
			Collection<CollisionMember> contacts) {
		int count = 0;
		
		for (CollisionMember member:contacts)
		{
			if (testGroup.contains(member))
				count++;
		}
		return count;
	}

	/**
	 * Add to this collection if it does not already contain this element.
	 * @param <E>
	 * @param collection
	 * @param element
	 */
	private  <E> void addExclusive(Collection<E> collection, E element )
	{
		if(!collection.contains(element))
			collection.add(element);
	}
	
	private  <E> void addAllExclusive(Collection<E> collection,Collection<E> toAdd )
	{
		for(E element:toAdd)
			addExclusive(collection,element);
	}
	
	private boolean containsAny(Collection<?> container, Collection<?> containee)
	{
		for(Object element:containee)
		{
			if(container.contains(element))
				return true;
		}
		return false;
	}
	
	public void addCollisionMember(CollisionMember member)
	{
		WeakReference<CollisionMember> cRef = new WeakReference<CollisionMember>(member);
		if(!collisionMembers.contains(cRef))
		{
			collisionMembers.add( cRef);
			member.setManager(this);		
			
			this.getModel().addChild(member.getModel());
		}
	}
	
	public boolean removeCollisionMember(CollisionMember member)
	{		
		this.getModel().detachChild(member.getModel());
		return collisionMembers.remove(member);
	
	}
	
	public void suspendCollisionMember(CollisionMember member)
	{
		this.getModel().detachChild(member.getModel());
	}
	
	public void restoreCollisionMember(CollisionMember member)
	{
		
		if(member.getModel().getParent()==null)
			this.getModel().addChild(member.getModel());
	}

	public void updateCollisionMember(CollisionMember member)
	{
		//adds and removes a colliision member from the tree, so that it ends up in a new, optimal position
		
		this.suspendCollisionMember(member);
		
		this.restoreCollisionMember(member);

	}
	
	public boolean canCollide(CollisionMember group1, CollisionMember group2) {
		
		return collisionMembers.contains(group1) && collisionMembers.contains(group2);
	}

	
	public static Set<CollisionMember> findCollisions(CollisionMember toResolve,Model toResolveAgainst, Set<Model> ignoreList) 
	{
		ArrayList<ModelCollision> modelCollisions = new ArrayList<ModelCollision>();
		Set<Model> ignoreModels = ignoreList;
	/*	int t = 0;
		long[] times = new long[11];
		times[t++] = System.nanoTime();*/
		
		
	//	 time[1] = System.nanoTime();
		//modelCollisions.clear();
		
		//this is the slow part
		for (Model model:toResolve.getCollisionModels())
		{
			//memoize this
			model.findCollisionsConcat(toResolveAgainst, ignoreModels, modelCollisions);
		}
//		 time[2] = System.nanoTime();
	//	times[t++] = System.nanoTime();
		Set<CollisionMember> collisions = new HashSet<CollisionMember>();
		for (ModelCollision collision:modelCollisions)
		{
			CollisionManager manager = toResolve.getCollisionManager();
			CollisionMember targetMember =collision.getTarget().getCollisionMember(manager);
			if (targetMember == toResolve)
			{
				CollisionMember sourceMember = collision.getSource().getCollisionMember(manager);
				if (sourceMember != toResolve && sourceMember != null)
				{
					collisions.add(sourceMember);
				}
			}else if (targetMember != null)
			{
				collisions.add(targetMember);
			}

		}
	//	times[t++] = System.nanoTime();
//		time[3] = System.nanoTime();
/*		String time = "CollisionManager\t";
		for (int e = 1;e<t;e++)
		{
			time += "\t" + (times[e] - times[e-1])/1000000.0;
		}
		System.out.println(time);*/
		return collisions;
	}

	/**
	 * Perform a thorough, all member collision resolution.
	 */
	public void resolveAll()
	{

		this.getModel().updateWorldData();
	
		this.getModel().updateModelData();

		
		
		for (WeakReference<CollisionMember> memberRef:collisionMembers)
		{//this will end up colliding models twice, atleast! Very wasteful!
			//its also probably n^2 or worse...
			CollisionMember member = memberRef.get();
			if(member!=null)
				member.resolveCollisions(null);
		}

	}
	
	public ParentModel getModel()
	{
		return model;
	}


	private final static  GroupDivider dummyDivider = new GroupDivider()
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

	public void merge(CollisionManager structuralManager) {
		for (WeakReference<CollisionMember> memberRef:collisionMembers)
		{//this will end up colliding models twice, atleast! Very wasteful!
			//its also probably n^2 or worse...
			CollisionMember member = memberRef.get();
			if(member==null)
				continue;
			addCollisionMember(member);
	
			member.setNeedsResolution(true);
			member.getModel().updateWorldData();
		
		/*	for(Model child:member.getModel().getChildren())
			{
				child.updateModelData();
			}
			member.getModel().updateModelData();*/
		}
		
	}
	


	public void count() {

	/*	if (this.model instanceof RTreeModel)
		{
			System.out.println( ((RTreeModel)this.model).count() +"\t" + this.collisionMembers.size());
		}*/
		
		
	}
	
	public void rebuild()
	{
		this.getModel().detachAllChildren();

		for (WeakReference<CollisionMember> memberRef:collisionMembers)
		{//this will end up colliding models twice, atleast! Very wasteful!
			//its also probably n^2 or worse...
			CollisionMember member = memberRef.get();
			if(member==null)
				continue;
			this.getModel().addChild( member.getModel());
		}
	}
	

}



class CollisionMemberPair
{
	private final CollisionMember source;
	private final CollisionMember target;
	public CollisionMemberPair(CollisionMember source, CollisionMember target) {
		super();
		this.source = source;
		this.target = target;
	}
	public CollisionMember getSource() {
		return source;
	}
	public CollisionMember getTarget() {
		return target;
	}
	private static class IdentityWeakReference<E> extends WeakReference<E>
	{

		public IdentityWeakReference(E referent) {
			super(referent);			
		}

		@Override
		public boolean equals(Object obj) {
			E thisObj = this.get();
			if(thisObj!=null)
				return thisObj.equals(obj);
			else 			
				return obj == null;
		}

		@Override
		public int hashCode() {
			E obj = this.get();
			if(obj!=null)
				return obj.hashCode();
			else
				return super.hashCode();
		}
		
	}
}



