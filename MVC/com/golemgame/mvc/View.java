package com.golemgame.mvc;

/**
 * Views come in two varieties: snapshot views, that only reflect the model at one point in time, and sustained views, that will
 * update themselves to reflect changes in the model.
 * @author Sam
 *
 */
public interface View {
	public PropertyStore getStore();
	
	/**
	 * Called when this view is removed.
	 */
	public abstract void remove();
}
