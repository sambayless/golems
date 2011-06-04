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
package com.golemgame.mvc.golems;

import java.util.Collection;

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.DataType;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.DataType.Type;

public class PhysicalStructureInterpreter extends StructureInterpreter implements FunctionalInterpreter{

	public static final String PHYSICAL_STATIC = "static";
	public static final String MATERIAL_PROPERTIES = "material";	
	public static final String SURFACE_PROPERTIES = "surface";
	
	public CollectionType getInputs()
	{
		return getStore().getCollectionType(INPUTS);
	}
	
	public CollectionType getOutputs()
	{
		return getStore().getCollectionType(OUTPUTS);
	}	
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(PHYSICAL_STATIC))
			return defaultBool;
		if(key.equals(MATERIAL_PROPERTIES))
			return defaultStore;
		if(key.equals(SURFACE_PROPERTIES))
			return defaultStore;
		
		return super.getDefaultValue(key);
	}
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(PHYSICAL_STATIC);
		keys.add(MATERIAL_PROPERTIES);
		keys.add(SURFACE_PROPERTIES);
		keys.add(FunctionalInterpreter.INPUTS);
		keys.add(FunctionalInterpreter.OUTPUTS);
		return super.enumerateKeys(keys);
	}
	
	public PropertyStore getOutput(int outputNumber)
	{
		DataType output = getOutputs().getElement(outputNumber);
		if (!(output.getType()==Type.PROPERTIES))
		{
			output = new PropertyStore();
			getOutputs().setElement(output, outputNumber);
		}
		return ((PropertyStore)output);
	}
	

	public PropertyStore getInput(int inputNumber)
	{
		DataType input = getInputs().getElement(inputNumber);
		if (!(input.getType()==Type.PROPERTIES))
		{
			input = new PropertyStore();
			getInputs().setElement(input, inputNumber);
		}
		return ((PropertyStore)input);
	}

	
	
	public PhysicalStructureInterpreter(PropertyStore store) {
		super(store);
		
	}
	
	public boolean isStatic()
	{
		return getStore().getBoolean(PHYSICAL_STATIC);
	}
	
	public void setStatic(boolean isStatic)
	{
		getStore().setProperty(PHYSICAL_STATIC, isStatic);
	}

	public PhysicalStructureInterpreter() {
		this(new PropertyStore());		
	}

	public PropertyStore getMaterialProperties()
	{
		return getStore().getPropertyStore(MATERIAL_PROPERTIES,DefaultGolemsProperties.getInstance().getDefaultStore(GolemsClassRepository.MATERIAL_PROPERTIES_CLASS));
	}
	
	public PropertyStore getSurfaceProperties()
	{
		return getStore().getPropertyStore(SURFACE_PROPERTIES,DefaultGolemsProperties.getInstance().getDefaultStore(GolemsClassRepository.SURFACE_PROPERTIES_CLASS));
	}
	
}
