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
package com.golemgame.model.effect;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Collection;

import com.golemgame.model.Model;
import com.golemgame.model.Model.ModelTypeException;


public interface ModelEffect extends Serializable{
	
	public enum ModelEffectType
	{
		COLOR(true),TEXTURE(true), TEXTURELAYER(false),TEXTUREWRAPPER(true),LIGHTING(true),ALWAYS_VISIBLE(true);
		private final boolean exclusive;
		private ModelEffectType(boolean exclusive)
		{
			this.exclusive = exclusive;
		}
		public boolean isExclusive() {
			return exclusive;
		}
	}
	
	public void attachModel(Model model) throws ModelTypeException;
	public void removeModel(Model model);

	public void refreshEffect();
	
	//public void applyEffect(Model model) throws ModelTypeException;

	/**
	 * Return the types of effects which this model effect provides.
	 * @return
	 */
	public ModelEffectType[] getEffectTypes();
	
//	public void removeEffect(Model model);
	
	
//	public void addUpdateListener(ModelEffectListener listener);

//	public void removeUpdateListener(ModelEffectListener listener);
	
//	public void clearUpdateListeners();
	

	//public void requestUpdate();
	
	
	/**
	 * This this effects data to the provided effect's
	 * @param setFrom
	 */
	public void set(ModelEffect setFrom) throws ModelEffectTypeException;

	public ModelEffect copy();
	
	public class ModelEffectTypeException extends RuntimeException
	{

		private static final long serialVersionUID = 1L;

		public ModelEffectTypeException() {
			super();
		}

		public ModelEffectTypeException(String message, Throwable cause) {
			super(message, cause);
		}

		public ModelEffectTypeException(String message) {
			super(message);
		}

		public ModelEffectTypeException(Throwable cause) {
			super(cause);
		}
		
	}
}
