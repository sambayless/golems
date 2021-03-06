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

import com.golemgame.model.ParentModel;
import com.golemgame.tool.action.Actionable;


public class NonPropagatingCollisionMember extends CollisionMember {
	private static final long serialVersionUID = 1;
	
	public NonPropagatingCollisionMember(ParentModel model,Actionable actionable) {
		super(model,actionable);
	}

	@Override
	public boolean propagatesCollisions() {
		return false;
	}
	
	/*
	@Override
	public boolean resolveCollisions(Set<Model> ignoreList)
	{
		boolean change = false;
		//First, test this collision node's pre-existing contacts
		Iterator<Contact> i = contacts.iterator();
		while(i.hasNext())
		{
			Contact c = i.next();
			if (!c.checkContact())
			{//returns false if the contact fails
				i.remove();
				c.destroy();
				change = true;
			}
		}
		
		//this class seems to be broken!
		
		//Two possibilities. If this node is already a member of a group that has more than one collision member in it, then only test for additional collisions within that group.
		//otherwise, test for any collisions.
		
		//out of the currently existing contacts, find the largest group, designate that the primary group
		

	
		
		Set<CollisionMember> collisions ;
		if (this.getGroup().getMembers().size() > 1)
		{
			//if this member is not all by itself in its group, then only allow collisions within the group.
			collisions = CollisionManager.findCollisions(this, this.getGroup().getModel(), ignoreList);
		}else
		{
		//Now, test for new contacts
		
			collisions = CollisionManager.findCollisions(this, this.getCollisionManager().getModel(), ignoreList);
		}
		
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
		
		return change;
		
	}*/
	


}
