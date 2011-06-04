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
import com.golemgame.mvc.GUIDType;
import com.golemgame.mvc.PropertyStore;

public class MachineSpaceInterpreter extends StoreInterpreter{
	//this contains a collection of machines
	public final static String MACHINES = "machines";
	public final static String SETTINGS = "settings";
	public final static String PARTICLE_EFFECTS = "effects.particles";
	public final static String FUNCTIONS = "defined.functions";
	public final static String SKYBOX = "skybox";
	public final static String GUID = "MachineSpace.GUID";
	public final static String GUID_IMPORTS = "Imported.GUID";
	public final static String IMAGE = "image";
	public final static String DESCRIPTION = "MachineSpace.description";
	public final static String NAME = "MachineSpace.name";
	//public final static String AUTHOR = "MachineSpace.author"//intentionally not allowing this right now... its against the spirit of cooperation.
	//public final static String COLORS = "defined.colors";
	
	@Override
	public Collection<String> enumerateKeys(Collection<String> keys) {
		keys.add(MACHINES);
		keys.add(SETTINGS);
		keys.add(PARTICLE_EFFECTS);
		keys.add(FUNCTIONS);
		keys.add(GUID);
		keys.add(GUID_IMPORTS);
		keys.add(SKYBOX);
		keys.add(IMAGE);
	//	keys.add(COLORS);
		return super.enumerateKeys(keys);
	}
	
	@Override
	public DataType getDefaultValue(String key) {
		if(key.equals(MACHINES))
			return defaultCollection;
		if(key.equals(SETTINGS))
			return defaultStore;
		if(key.equals(PARTICLE_EFFECTS))
			return defaultStore;
		if(key.equals(FUNCTIONS))
			return defaultStore;
		if(key.equals(SKYBOX))
			return defaultStore;
		if(key.equals(GUID))
			return new GUIDType();
		if(key.equals(GUID_IMPORTS))
			return defaultCollection;
		if(key.equals(IMAGE))
			return defaultStore;
	//	if(key.equals(COLORS))
	//		return defaultStore;
		return super.getDefaultValue(key);
	}
	
	public MachineSpaceInterpreter(PropertyStore store) {
		super(store);
		getStore().setClassName(GolemsClassRepository.MACHINE_SPACE_CLASS);
		getStore().getGUIDType(GUID);//force instantiation of the guid.
	}
	
	public MachineSpaceInterpreter() {
		this(new PropertyStore());		
	}

	public PropertyStore getSettings()
	{
		return getStore().getPropertyStore(SETTINGS);
	}

	public CollectionType getImportedGUIDS()
	{
		return getStore().getCollectionType(GUID_IMPORTS);
	}
	
	public void recordImport(GUIDType imported)
	{
		this.getImportedGUIDS().addElement(imported);
	}
	
	public void eraseImport(GUIDType imported)
	{
		this.getImportedGUIDS().removeElement(imported);
	}

	public String getMachineSpaceName(){
		return getStore().getString(NAME);
	}
	
	public String getDescription(){
		return getStore().getString(DESCRIPTION);
	}
	
	public void setMachineSpaceName(String name)
	{
		getStore().setProperty(NAME, name);
	}
	public void setDescription(String description)
	{
		getStore().setProperty(DESCRIPTION, description);
	}
	
	public CollectionType getMachines()
	{
		return getStore().getCollectionType(MACHINES);
	}
	
	public void addMachine(PropertyStore store)
	{
		this.getMachines().addElement(store);
	}
	
	public void removeMachine(PropertyStore store)
	{
		this.getMachines().removeElement(store);
	}


	public MachineInterpreter constructMachine() {
		MachineInterpreter machine = new MachineInterpreter(new PropertyStore());
		this.addMachine(machine.getStore());
		return machine;
	}
	
	public PropertyStore getParticleEffectRepository()
	{
		return getStore().getPropertyStore(PARTICLE_EFFECTS);
	}
	
	public PropertyStore getImage()
	{
		return getStore().getPropertyStore(IMAGE);
	}
	
	
	public PropertyStore getFunctionRepository()
	{
		return getStore().getPropertyStore(FUNCTIONS);
	}
	
	public PropertyStore getSkybox()
	{
		return getStore().getPropertyStore(SKYBOX);
	}
	
	
	public GUIDType getGUID()
	{
		return getStore().getGUIDType(GUID);
	}

/*	public PropertyStore getColorRepository()
	{
		return getStore().getPropertyStore(COLORS);
	}
	*/
	//this will also contain the various machine space properties
	
}
