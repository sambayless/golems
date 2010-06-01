package com.golemgame.structural.collision;


/**
 * This provides a method for dividing groups that would otherwise be together.
 * @author Sam
 *
 */
public interface GroupDivider {
	public boolean dividesGroup(CollisionMember member);
	
	/**
	 * Whether or not this member can propagate a grouping. A non propagating member can be part of groups,
	 * but it will not bridge any two groups.
	 * @param member
	 * @return
	 */
	public boolean propagatesGroup(CollisionMember member);
	
	public boolean isGroupable(CollisionMember member);
}
