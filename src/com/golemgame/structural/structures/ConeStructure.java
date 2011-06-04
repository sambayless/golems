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
package com.golemgame.structural.structures;

import com.golemgame.model.Model;
import com.golemgame.model.spatial.shape.ConeFacade;
import com.golemgame.model.spatial.shape.ConeModel;
import com.golemgame.model.spatial.shape.CylinderModel;
import com.golemgame.model.texture.TextureTypeKey.TextureShape;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.ConeInterpreter;

public class ConeStructure extends CylinderStructure {
	private static final long serialVersionUID = 1L;
	private static final float RADIUS = 0.5f;
	private static final float HEIGHT = 1f;
	
	private ConeInterpreter interpreter;
	public ConeStructure(PropertyStore store) {
		super(store);
		this.interpreter = new ConeInterpreter(store);
	}
	@Override
	protected TextureShape getPrefferedShape() {
		return TextureShape.Cone;
	}

	protected CylinderModel buildModel() {
		return new ConeModel(true);
	}

	protected Model buildFacade() {
		return new ConeFacade();
	}

	

}
