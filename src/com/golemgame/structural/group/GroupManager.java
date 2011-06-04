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
package com.golemgame.structural.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.golemgame.mechanical.StructuralMachine;
import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.GroupInterpreter;
import com.golemgame.mvc.golems.GroupManagerInterpreter;
import com.golemgame.states.StateManager;
import com.golemgame.structural.DesignViewFactory;
import com.golemgame.structural.Structural;
import com.golemgame.structural.structures.PhysicalStructure;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.CycleAction;
import com.golemgame.tool.action.DeleteAction;
import com.golemgame.tool.action.UndoManager;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.action.information.GroupInformation;
import com.golemgame.tool.action.information.PhysicalInfoAction;
import com.golemgame.tool.action.mvc.AddComponentAction;
import com.golemgame.util.CyclicalComparator;
import com.jme.math.FastMath;
import com.jme.renderer.ColorRGBA;

/**
 * This is a repository of all groups, and provides mappings between structures and their groups
 * @author Sam
 *
 */
public class GroupManager implements Actionable,SustainedView{
	
	private static final int MAX_COLORS = 10;
	
	
	private CyclicalComparator<StructureGroup> cyclicalComparator = new CyclicalComparator<StructureGroup>();
	private Comparator<StructureGroup> reverseComparator = Collections.reverseOrder(cyclicalComparator);
	private ArrayList<StructureGroup> groups = new ArrayList<StructureGroup>();
	
	private StructuralMachine machine;
	//private Map<Structure, Collection<StructureGroup>> groupMap = new Map<>
	/**
	 * This should be accessed through the group's actions only
	 */
	boolean addGroup(PropertyStore group)
	{
		if (group == null || interpreter.getGroups().contains(group) )
			return false;
		interpreter.addGroup(group);

		return true;
	}
	
	/**
	 * This should be accessed through the group's actions only
	 */
	void removeGroup(PropertyStore group)
	{
		interpreter.removeGroup(group);
	}
	
	public Collection<Structural> getAllGroupedStructures()
	{
		Set<Structural> structureSet = new HashSet<Structural>();
		for (StructureGroup group:groups)
		{
			structureSet.addAll(group.getMembers());
		}
		return structureSet;
	}
	
	public Collection<StructureGroup> getGroups(Structural structure)
	{
		Collection<StructureGroup> associatedGroups = new ArrayList<StructureGroup>();
		for (StructureGroup group:groups)
		{
			if (group.isMember(structure))
				associatedGroups.add(group);
		}
		return associatedGroups;
	}
	
	
	
	/**
	 * Return a single, lowest number group that this element belongs to.
	 * @param structure
	 * @return
	 */
	public StructureGroup getGroup(Structural structure)
	{
		for (StructureGroup group:groups)
		{
			if (group.isMember(structure))
				return group;
		}
		return null;
	}
	
	/**
	 * Creating a group does not add it to the group manager.
	 * @return
	 */
	public PropertyStore createGroup()
	{
		GroupInterpreter interpreter =new GroupInterpreter();
		interpreter.setGroupName(generateUniqueName());
		interpreter.setColor(generateUniqueColor());
		interpreter.setNumber(getNextNumber());
		return interpreter.getStore();
		//return new StructureGroup(generateUniqueName(), generateUniqueColor(),getNextNumber(),this);
	}
	
	public void setMachine(StructuralMachine machine)
	{
		this.machine = machine;
	}
	
	public StructuralMachine getMachine()
	{
		return this.machine;
	}
	
	public ColorRGBA generateUniqueColor()
	{
		//generate some new colors, pick the most unique one.
		ColorRGBA[] colors = new ColorRGBA[MAX_COLORS];
		
		for (int i = 0;i<MAX_COLORS;i++)
		{
			ColorRGBA color = new ColorRGBA();
			float a = 1f;
			
			float r = generateAcceptableColor();
			float g = generateAcceptableColor();
			float b = generateAcceptableColor();
			
			color.set(r,g,b,a);
			colors[i] = color;
		}
		float mostUnique = Float.NEGATIVE_INFINITY;
		int mostUniqueIndex = 0;//in case there are no groups to compare with.
		for(int i = 0;i<colors.length;i++)
		{
			float uniqueness = Float.POSITIVE_INFINITY;
			for (StructureGroup group:groups)
			{
				float evalUnique = evaluateUniqueness(colors[i],group.getColor());
				if(evalUnique<uniqueness)
					uniqueness = evalUnique;
			}
			if (uniqueness> mostUnique)
			{
				mostUnique = uniqueness;
				mostUniqueIndex = i;
			}
			
			
		}
		return colors[mostUniqueIndex];
	}
	private float evaluateUniqueness(ColorRGBA colorRGBA, ColorRGBA color) {
		//the difference between these colors, squared.
		return FastMath.sqr(colorRGBA.r - color.r) + FastMath.sqr(colorRGBA.g - color.g) + FastMath.sqr(colorRGBA.b - color.b);
		
		
	}
	

	
	
	private static Random random=  new Random();
	private float generateAcceptableColor() {
		float c = 0;
		while (c < 0.3f || c> 0.8f)
		{
			 c = random.nextFloat();
		}
		return c;
	}

	public String generateUniqueName()
	{
		int nameNum = 0;
		String name = generateName(nameNum);
		while(isNameUsed(name))
		{
			 nameNum++;
			name = generateName(nameNum);
		}
		return name;
	}
	
	private boolean isNameUsed(String name)
	{
		for (StructureGroup group:groups)
		{
			if (group.getName().equalsIgnoreCase(name))
				return true;			
		}
		return false;
	}
	
	private static String generateName(int num)
	{
		return "Group " + String.valueOf( num);
		
	}
	
	private int getNextNumber()
	{
		int highestNumber = 0;
		for (StructureGroup group:groups)
		{
			if (group.getGroupNumber()>highestNumber)
			{
				highestNumber = group.getGroupNumber();
			}
		}
		return highestNumber + 1;
	}
	private GroupManagerInterpreter interpreter;
	public GroupManager(PropertyStore store) {
		super();
		interpreter = new GroupManagerInterpreter(store);
		store.setSustainedView(this);
	}


	//private transient ArrayList<StructureGroup> loadingList = null;

	
	public void updateOrdering(StructureGroup group)
	{
		//groups.remove(group);
	//	groups.add(group);
	}

	
	public void merge(PropertyStore toMerge)
	{
		//ArrayList<StructureGroup> mergeGroups = toMerge.groups;
		GroupManagerInterpreter otherGroup = new GroupManagerInterpreter(toMerge);
		
		int nextNum = getNextNumber();
		for(DataType data:otherGroup.getGroups().getValues())
		{
			if (data.getType() == DataType.Type.PROPERTIES)
			{
				//GroupInterpreter group = new GroupInterpreter((PropertyStore)data);
				PropertyStore newGroup = ((PropertyStore) data).deepCopy();
				GroupInterpreter interp = new GroupInterpreter(newGroup);
				interp.setNumber(interp.getNumber()+nextNum);
				this.interpreter.addGroup(interp.getStore());
			}	
		}
		//
		
	}
	
	
	public void constructGroup()
	{
		Collection<Actionable> selection = StateManager.getToolManager().getSelectedActionables();

		if (selection == null ||  selection.isEmpty())
			return;
		//first, determine if all members of this selection are part of a group, and the entire group
		Actionable first = selection.iterator().next();
		
		try {
			GroupInformation groupInfo = (GroupInformation) first.getAction(Action.GET_GROUP);
			Collection<StructureGroup> groups = groupInfo.getAssignedGroups();
			
			if (groups == null || groups.isEmpty())
			{
				
			}else
			{
				for (StructureGroup group:groups)
				{
					if(group.isEntireGroup(selection))
					{
						//just update the group... dont know exactly what this should accomplish
						
						
						return;
					}
				}
			}	

			//if we make it to here, then create a new group
			
			PropertyStore group =  createGroup();
			GroupInterpreter interp = new GroupInterpreter(group);
			
			
			
		/*		CreateAction create = (CreateAction)	group.getAction(Action.CREATE);
				if (create.doAction())
				{
					UndoManager.getInstance().addAction(create);
				}*/
		
		
			for (Actionable actionable:selection)
			{
				PhysicalInfoAction physInfo = (PhysicalInfoAction) actionable.getAction(Action.PHYSICAL_INFO);
				PhysicalStructure phys = physInfo.getPhysicalStructure();
				
				interp.addMember(phys.getID());
				
			/*	AddMemberAction addMember = (AddMemberAction)	group.getAction(Action.ADD_MEMBER);
				addMember.setMember(phys);
				addMember.setProperties(UndoProperties.DEPENDENT);
				if (addMember.doAction())
				{					
					UndoManager.getInstance().addAction(addMember);
				}
				*/
			}
			
			//interpreter.addGroup(group);//StateManager.getStructuralMachine().getGroupManager().createGroup();
			
			
		/*	interpreter.refresh();
			interp.refresh();*/
			
			AddComponentAction addComp = (AddComponentAction)this.getAction(Action.ADD_COMPONENT);
			addComp.setComponent(group);
			
			if(addComp.doAction())
				UndoManager.getInstance().addAction(addComp);
			
		} catch (ActionTypeException e1) {
			StateManager.logError(e1);
			return;
		}
	}
	
	
	
	public void breakApartGroup()
	{
		Collection<Actionable> selection = StateManager.getToolManager().getSelectedActionables();

		if (selection == null ||  selection.isEmpty())
			return;
		
		Actionable first = selection.iterator().next();
		
		try {
			GroupInformation groupInfo = (GroupInformation) first.getAction(Action.GET_GROUP);
			Collection<StructureGroup> groups = groupInfo.getAssignedGroups();
			
			if (groups == null || groups.isEmpty())
			{
				
			}else
			{
				for (StructureGroup group:groups)
				{
					if(group.isEntireGroup(selection))
					{
						try{
							DeleteAction delete = (DeleteAction)	group.getAction(Action.DELETE);
							if (delete.doAction())
							{
								UndoManager.getInstance().addAction(delete);
							}
						} catch (ActionTypeException e) {
							return;
						}
						return;
					}
				}
			}	
		
			//do nothing
			
		} catch (ActionTypeException e1) {
			return;
		}
	}
	
	void cycleGroups(boolean forwards)
	{
		if(groups.size()<=1)
			return;
		int pivot = cyclicalComparator.getPivot();
		if (forwards)
			pivot --;
		else
			pivot++;
		
		pivot = pivot  % groups.size();
		if (pivot <= 0)
			pivot = groups.size();
	/*	if (forwards)
			pivot =groups.get(groups.size()-1).getOrdinal();//reverse these, because the list is reversed after sorting
		else
			pivot =  groups.get(groups.size()-2).getOrdinal();*/
		
		cyclicalComparator.setPivot(pivot);
		
		Collections.sort(groups,reverseComparator);

	}

	public Action<?> getAction(Type type) throws ActionTypeException {
		
		if (type == Action.CYCLE)
		{
			return new Cycle();
		}else if (type == Action.REMOVE_COMPONENT)
		{
			return new RemoveComponent(interpreter);
		}else if (type == Action.ADD_COMPONENT)
		{
			return new AddComponent(interpreter);
		}
		
		
		throw new ActionTypeException();
	}
	
	
	private static class AddComponent extends AddComponentAction
	{
		
		private final GroupManagerInterpreter interpreter;
		
		public AddComponent(GroupManagerInterpreter interpreter) {
			super();
			this.interpreter = interpreter;
		}

		@Override
		public boolean doAction() {
			interpreter.addGroup(this.getComponent());
			interpreter.refresh();
			return true;
		}

		@Override
		public boolean undoAction() {
			interpreter.removeGroup(this.getComponent());
			interpreter.refresh();
			return true;
		}
		
	}

	
	private static class RemoveComponent extends AddComponentAction
	{
		
		private final GroupManagerInterpreter interpreter;
		
		public RemoveComponent(GroupManagerInterpreter interpreter) {
			super();
			this.interpreter = interpreter;
		}

		@Override
		public boolean doAction() {
			interpreter.removeGroup(this.getComponent());			
			interpreter.refresh();
			return true;
		}

		@Override
		public boolean undoAction() {
			interpreter.addGroup(this.getComponent());
			interpreter.refresh();
			return true;
		}
		
	}
	
	private class Cycle extends CycleAction
	{

		public Cycle() {
			super();
			
		}

		@Override
		public CycleAction copy() {
			Cycle copy = new Cycle();
			copy.setDirection(getDirection());
			return copy;
		}

		@Override
		public boolean doAction() {
			cycleGroups(getDirection()==Direction.RIGHT);
			StateManager.getViewManager().refreshViews();
			return true;
		}

		@Override
		public boolean undoAction() {
			cycleGroups(getDirection()!=Direction.RIGHT);
			StateManager.getViewManager().refreshViews();
			return true;
		}

	}

	public PropertyStore getStore() {
		return interpreter.getStore();
	}

	public void invertView(PropertyStore store) {
		
	}
	public void remove() {
		// TODO Auto-generated method stub
		
	}
	public void refresh() {
		CollectionType structureCollection = interpreter.getGroups();

		ArrayList<StructureGroup> toRemove = new ArrayList<StructureGroup>();
	
		for(StructureGroup structure:this.groups)
		{
			if (!structureCollection.getValues().contains(structure.getStore()))
			{
				//delete this machine
				toRemove.add(structure);
			}
		}
		
		for(StructureGroup structure:toRemove)
		{
			//remove the VIEW from this machine
			//this.removeStructural(structure);
			//structure.delete();
			this.groups.remove(structure);
		}
		
		for(DataType data:structureCollection.getValues())
		{
			if(! (data instanceof PropertyStore))
					continue;
			boolean exists = false;
			for(StructureGroup structure:groups)
			{
				if(structure.getStore().equals(data))
				{
					exists = true;
					break;
				}
			}
			if(!exists)
			{
				StructureGroup component = (StructureGroup) DesignViewFactory.constructView((PropertyStore)data);
				
				
				component.setManager(this);
				component.refresh();
				this.groups.add(component);
			
			}
		}
	/*	
		for(DataType data:interpreter.getGroups().getValues())
		{
			if (data instanceof PropertyStore)
				((PropertyStore)data).refresh();
		}*/
	}
	
	
	
}
