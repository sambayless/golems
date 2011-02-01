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
