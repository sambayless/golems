package com.golemgame.states.camera;

import java.util.ArrayList;
import java.util.List;


/**
 * This should NOT be serializable.
 * @author Sam
 *
 */
public class OrderedCameraList {
	

	private List<CameraDelegate> orderedDelegates;
	
	private int currentPosition = 0;
	
	private CameraDelegate defaultDelegate;

	
	public CameraDelegate getDefaultDelegate()
	{
		return this.defaultDelegate;
	}
	
	
	
	public void setDefaultDelegate(CameraDelegate defaultDelegate) {
		this.defaultDelegate = defaultDelegate;
	}



	public OrderedCameraList() {
		super();
	    orderedDelegates = new ArrayList<CameraDelegate>();
	    this.defaultDelegate = CameraManager.default_stand_in_camera;
	}

	public void addOrderedDelegate(CameraDelegate delegate)
	{
		this.orderedDelegates.add(delegate);
	}
	
	public boolean removeOrderedDelegate(CameraDelegate toRemove)
	{
		return this.orderedDelegates.remove(toRemove);
	}
	
	public CameraDelegate getNextCamera()
	{
		if(orderedDelegates.isEmpty())
		{
			return (getDefaultDelegate());
		
		}
		
		currentPosition++;
		if(currentPosition>=orderedDelegates.size())
			currentPosition = 0;
		return (orderedDelegates.get(currentPosition));
	}
	

	
	public CameraDelegate getPreviousCamera()
	{
		if(orderedDelegates.isEmpty())
		{
			return (getDefaultDelegate());
			
		}
		
		currentPosition--;
		if(currentPosition<0)
			currentPosition = orderedDelegates.size()-1;
		return (orderedDelegates.get(currentPosition));
	}
	
	public List<CameraDelegate> getOrderedDelegates() {
		return orderedDelegates;
	}



	public void clear() {
		orderedDelegates.clear();
		orderedDelegates.add(defaultDelegate);
	}
}
