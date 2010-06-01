package com.golemgame.mvc;

/**
 * A property state represents a snapshot of some subset of the properties of a data type.
 * Its only purpose is to restore that state at a later point in time.
 * @author Sam
 *
 */
public abstract class PropertyState {
	public abstract void restore();
	public abstract void refresh();
	public static PropertyState getDummy() {
		return dummy;
	}

	private static final PropertyState dummy = new PropertyState()
	{

		@Override
		public void restore() {
			//do nothing
		}

		@Override
		public void refresh() {
			// TODO Auto-generated method stub
			
		}
		
	};
}
