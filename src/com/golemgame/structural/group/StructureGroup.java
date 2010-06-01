package com.golemgame.structural.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.golemgame.model.effect.TintableColorEffect;
import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.ReferenceType;
import com.golemgame.mvc.SustainedView;
import com.golemgame.mvc.golems.GroupInterpreter;
import com.golemgame.structural.Structural;
import com.golemgame.structural.structures.PhysicalStructure;
import com.golemgame.tool.action.Action;
import com.golemgame.tool.action.ActionTypeException;
import com.golemgame.tool.action.Actionable;
import com.golemgame.tool.action.AddMemberAction;
import com.golemgame.tool.action.RemoveMemberAction;
import com.golemgame.tool.action.Action.Type;
import com.golemgame.tool.action.information.PhysicalInfoAction;
import com.golemgame.util.Ordinal;
import com.jme.renderer.ColorRGBA;


public class StructureGroup implements Comparable<StructureGroup>, Actionable,Ordinal,SustainedView{
	
	private int groupNumber;
	
	private String name = "";
	private ColorRGBA color;
	private int keyboardShortcut;	
	
	private Map<Reference,Structural> members = new HashMap<Reference,Structural>();

	private TintableColorEffect colorEffect;
	
	private  GroupManager manager;
	
	public boolean isMember(Structural structure) {
		if (structure == null)
			return false;
		
		return members.get(structure.getID()) != null;
	}
	
	
	
	public int getOrdinal() {
		return groupNumber;
	}



	public TintableColorEffect getColorEffect() {
		return colorEffect;
	}



	public int getGroupNumber() {
		return groupNumber;
	}



	public void setGroupNumber(int groupOrder) {
		this.groupNumber = groupOrder;
	}


/*
	public StructureGroup( String name,ColorRGBA color, int groupOrder, GroupManager manager) {
		super();
		this.color = color;
		this.name = name;
		colorEffect = new TintableColorEffect(color);
		this.groupNumber = groupOrder;
		this.manager = manager;
	}
*/
	public void applyGroupEffect()
	{
		for(Structural member:members.values())
			member.getAppearance().addEffect(colorEffect, false);
	}
	
	public void removeGroupEffect()
	{
		for(Structural member:members.values())
			member.getAppearance().removeEffect(colorEffect, false);
	}

/*	private boolean addMember(Structural structure)
	{
		return members.add(structure);
	}
	
	public void addMembers(Collection<Structure>members)
	{
		members.addAll(members);
	}

	private boolean removeMember(Structural member)
	{
		return members.remove(member);
	}
	*/
	public boolean isEntireGroup(Collection<Actionable> groupToTest)
	{		
		try {
			for (Actionable toTest:groupToTest)
			{			
				PhysicalInfoAction physInfo;
			
					physInfo = (PhysicalInfoAction) toTest.getAction(Action.PHYSICAL_INFO);
			
				PhysicalStructure phys = physInfo.getPhysicalStructure();
				
				if (!isMember(phys))
				{
					return false;
				}
			}	
		} catch (ActionTypeException e) {
			return false;
		}
		
		return members.size() == groupToTest.size();
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ColorRGBA getColor() {
		return color;
	}

	public void setColor(ColorRGBA color) {
		this.color = color;
	}

	public int getKeyboardShortcut() {
		return keyboardShortcut;
	}

	public void setKeyboardShortcut(int keyboardShortcut) {
		this.keyboardShortcut = keyboardShortcut;
	}



	public Collection<Structural> getMembers() {
		return members.values();
	}
	
	private GroupInterpreter interpreter;
	
	public StructureGroup(PropertyStore store) {
		super();
		interpreter = new GroupInterpreter(store);
		store.setSustainedView(this);
		this.colorEffect = new TintableColorEffect();
		color = new ColorRGBA();
		this.colorEffect.setColor(color);
	}

	public int compareTo(StructureGroup o) {
		return  o.getGroupNumber()- getGroupNumber();//invert the ordering so new things come first
		
		
	}







	@Override
	public String toString() {
		return name + " (" + groupNumber + ")";
	}



	public Action<?> getAction(Type type) throws ActionTypeException {
		
		switch(type)
		{
/*			case CREATE:
				return new CreateGroup();
			case DELETE:
				return new DestroyGroup();*/
			case ADD_MEMBER:
				return new AddMember(interpreter);
			case REMOVE_MEMBER:
				return new RemoveMember(interpreter);
		}		
		throw new ActionTypeException();
	}
	
/*	
	private class CreateGroup extends CreateAction
	{

		private final GroupManager manager;
		public CreateGroup() {
			super();
			manager = StructureGroup.this.manager;
		}

		@Override
		public boolean doAction() {
			manager.addGroup(StructureGroup.this);
			return true;
		}

		@Override
		public boolean undoAction() {
			manager.removeGroup(StructureGroup.this);
			return super.undoAction();
		}
		
	}
	private class DestroyGroup extends DeleteAction
	{
		private final GroupManager manager;
		public DestroyGroup() {
			super();
			manager = StructureGroup.this.manager;

		}

		@Override
		public boolean doAction() {
		
			manager.removeGroup(StructureGroup.this);
			
			return true;
		}

		@Override
		public boolean undoAction() {
			manager.addGroup(StructureGroup.this);
			return super.undoAction();
		}
	}*/
	
	private class AddMember extends AddMemberAction
	{
		private GroupInterpreter interpreter;
		
		public AddMember(GroupInterpreter interpreter) {
			super();
			this.interpreter = interpreter;
		}

		@Override
		public boolean doAction() {
			interpreter.addMember(super.getMember());
			interpreter.refresh();
			return true;
		}

		@Override
		public boolean undoAction() {
			interpreter.removeMember(super.getMember());
			interpreter.refresh();
			return true;
		}

		@Override
		protected Object clone() throws CloneNotSupportedException {
			AddMember clone = new AddMember(interpreter);
			
			clone.setMember(getMember());
			return clone;
		}
		
	}
	
	private class RemoveMember extends RemoveMemberAction
	{
		private GroupInterpreter interpreter;
		public RemoveMember(GroupInterpreter interpreter) {
			super();
			this.interpreter = interpreter;
		}
		@Override
		public boolean doAction() {
			interpreter.removeMember(super.getMember());
			interpreter.refresh();
			return true;
		}

		@Override
		public boolean undoAction() {
			interpreter.addMember(super.getMember());
			interpreter.refresh();
			return true;
		}

		@Override
		protected Object clone() throws CloneNotSupportedException {
			RemoveMember clone = new RemoveMember(interpreter);
	
			clone.setMember(getMember());
			return clone;
		}
		
	}

	public void invertView(PropertyStore store) {
		// TODO Auto-generated method stub
		
	}

	public void remove() {
		// TODO Auto-generated method stub
		
	}

	public void refresh() {
		
		this.color.set(interpreter.getColor());
		this.colorEffect.setColor(this.color);
		this.name = interpreter.getGroupName();
		this.groupNumber = interpreter.getNumber();
		
		CollectionType structureCollection = interpreter.getMembers();

		ArrayList<Structural> toRemove = new ArrayList<Structural>();
	
		for(Structural structure:this.getMembers())
		{
			if (!structureCollection.getValues().contains(structure.getStore()))
			{
				//delete this machine
				toRemove.add(structure);
			}
		}
		
		for(Structural structure:toRemove)
		{
			//remove the VIEW from this machine
			//this.removeStructural(structure);
			//structure.delete();
			this.getMembers().remove(structure);
		}
		
		for(DataType data:structureCollection.getValues())
		{
			if(! (data instanceof ReferenceType))
					continue;
			boolean exists = false;
			for(Structural structure:getMembers())
			{
				if (structure == null)
					continue;
				if(structure.getID().equals(data))
				{
					exists = true;
					break;
				}
			}
			if(!exists)
			{
				Reference id = ((ReferenceType)data).getID();
				Structural structure = this.manager.getMachine().getStructure(id);
				
				this.members.put(id, structure);
				
			
			}
		}

	}



	public PropertyStore getStore() {
		return interpreter.getStore();
	}



	public void setManager(GroupManager groupManager) {
		this.manager = groupManager;
	}
	
	
}
