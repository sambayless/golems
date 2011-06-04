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
package com.golemgame.model.quality;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * This class is used to control the quality of a set of models.
 * It maintains only weak references to those models.
 * @author Sam
 *
 */
public class QualityManager {
	
	private Collection<ModelQualityDelegate> delegates = new ArrayList<ModelQualityDelegate>();
	private int quality = 0;
	
	public void addQualtiyDelegate(ModelQualityDelegate delegate)
	{
		delegates.add(delegate);
		delegate.setQuality(quality);//init the quality
	}
	
	/**
	 * Set the quality of the model to some value on a scale, with 0 being highest possible quality.
	 * Quality decreases as the values decrease below zero.
	 * @param quality
	 */
	public void setQuality(int quality)
	{
		//this isn't impacted by scene locking, because the lod calculations happen in the render
		//queue... although display lists might cause problems?
		//most likely it works because the display lists are created at the geometry level
		//and the quality controler just decides which of those geoms to draw - so the displaylists
		//are all created, but only one is drawn.
		
		
		/*
		 * Ok, but: it seems likely that all the different children get updated at each cycle.
		 *
		 * 
		 */
		this.quality = quality;
		Iterator<ModelQualityDelegate> it = delegates.iterator();
		while(it.hasNext())
		{
			//WeakReference<ModelQualityDelegate> ref = it.next();
			ModelQualityDelegate del = it.next();//ref.get();
			if(del == null)
			{
				it.remove();
			}else
			{
				boolean result = del.setQuality(quality);
				if(!result)
					it.remove();
			}
		}
	}
}
