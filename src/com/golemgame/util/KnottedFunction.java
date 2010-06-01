package com.golemgame.util;

/**
 * A function containing an ordered set of knots, which should be represented as Vertices on a function pane
 * @author Sam
 *
 */
public interface KnottedFunction {
	public double[] getXKnots();
	public double[] getYKnots();
	public int count();
	public boolean isKnotted();
	
}
