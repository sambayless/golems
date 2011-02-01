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
package com.golemgame.states.camera.skybox;

import com.jme.scene.Skybox;

/**
 * This class encapsulates an abstract representation of a sky box decoration
 * @author Sam
 *
 */
public class SkyBoxData {
	private static final SkyBoxData nullbox = new SkyBoxData("(None)");
	private final String name;
	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public SkyBoxData(String name) {
		super();
		this.name = name;
	}

	public static enum Face
	{
		 NORTH(Skybox.NORTH),EAST(Skybox.EAST),SOUTH(Skybox.SOUTH),WEST(Skybox.WEST), UP(Skybox.UP), DOWN(Skybox.DOWN);
		private final int skyboxenum;

		private Face(int skyboxenum) {
			this.skyboxenum = skyboxenum;
		}

		public int getSkyboxEnum() {
			return skyboxenum;
		}
		
	}
	
	public static SkyBoxData getNullBox()
	{
		return nullbox;
	}
	
	/**
	 * Rebuild this sky box for a given height and width
	 * @param width
	 * @param height
	 */
	public void build(int width, int height)
	{
		
	}
	
	public void apply(Face face,Skybox skybox, int unit)
	{
		//skybox.getSide(face.getSkyboxEnum()).getre
		skybox.removeTexture(face.getSkyboxEnum(),unit);
	}
	
}
