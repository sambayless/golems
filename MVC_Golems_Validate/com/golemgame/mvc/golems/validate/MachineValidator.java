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
package com.golemgame.mvc.golems.validate;

import com.golemgame.mvc.CollectionType;
import com.golemgame.mvc.golems.GolemsClassRepository;
import com.golemgame.mvc.golems.MachineInterpreter;
import com.golemgame.mvc.golems.validate.requirement.RecursiveValidationRequirement;
import com.golemgame.mvc.golems.validate.requirement.StoreValidationRequirement;

public class MachineValidator extends Validator {

	public MachineValidator() {
		super();
		super.requireData(MachineInterpreter.STRUCTURES,new CollectionType());	

		super.addRequirement(new RecursiveValidationRequirement(MachineInterpreter.STRUCTURES,GolemsValidator.getInstance()));
		super.addRequirement(new StoreValidationRequirement(MachineInterpreter.LAYER_REPOSITORY,GolemsClassRepository.LAYER_REPOSITORY_CLASS,GolemsValidator.getInstance()));

	}

}
