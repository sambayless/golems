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
package com.golemgame.mvc;

import java.util.Arrays;

public class SimplePropertyState extends PropertyState {

	private final KeyValue[] keyValues;
	
	private final PropertyStore store;
	
	public SimplePropertyState( PropertyStore store,String keys) {
		this(store, new String[]{keys});		
	}

	public SimplePropertyState( PropertyStore store,String[] keys) {
		super();
	
		this.store = store;
		keyValues = new KeyValue[keys.length];
		for (int i = 0; i < keys.length;i++)
		{
			keyValues[i] = new KeyValue(keys[i], store.getProperty(keys[i], NullType.get()).deepCopy());
		}
		
	}

	@Override
	public void refresh() {
	
		store.refresh();

	}

	@Override
	public void restore() {
		for (KeyValue keyValue:keyValues)
		{
			store.setProperty(keyValue.getKey(), keyValue.getValue().deepCopy());
		}
	}
	
	private static class KeyValue
	{
		private final String key;
		private final DataType value;
		public KeyValue(String key, DataType value) {
			super();
			this.key = key;
			this.value = value.deepCopy();
		}
		public String getKey() {
			return key;
		}
		public DataType getValue() {
			return value;
		}
		@Override
		public String toString() {
			return key;
		}
		
	}

	@Override
	public String toString() {
		return Arrays.toString(keyValues);
	}

}
