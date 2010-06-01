package com.golemgame.tool.action;

public class ActionDependencySet {
	private final String name;

	private final int num;
	
	public ActionDependencySet(String name) {
		super();
		this.name = name;
		num = -1;
	}
	
	public ActionDependencySet(String name, int num) {
		super();
		this.name = name;
		this.num = num;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + num;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActionDependencySet other = (ActionDependencySet) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (num != other.num)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name + "(" + num + ")";
	}
	
}
