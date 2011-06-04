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

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.SkyboxInterpreter;
import com.golemgame.mvc.golems.SkyboxInterpreter.Sky;
import com.jme.renderer.ColorRGBA;

public class SkyBoxDataFactory {
	private static final SkyBoxDataFactory instance = new SkyBoxDataFactory();
	//private Collection<String> enumeration;
	
	public static SkyBoxDataFactory getInstance() {
		return instance;
	}

	private SkyBoxDataFactory() {
		super();
	/*	List<String> enumeration = new ArrayList<String>();
		enumeration.add("(None)");
		enumeration.add("Color");
		enumeration.add("Clouds");
		this.enumeration = Collections.unmodifiableList(enumeration);*/
	}

	public SkyBoxData  construct(PropertyStore store) throws IOException
	{
		SkyboxInterpreter interpreter = new SkyboxInterpreter(store);
		Sky sky = interpreter.getImage();
		switch(sky)
		{
			case NONE:
				return new SolidColorSkyBox(ColorRGBA.black);
			case COLOR:
				return new SolidColorSkyBox(interpreter.getColor());
			default:
				return new ImageSkyBox(sky.getName(),getImages(sky));
		}
	}
/*
	public Collection<String> enumerate()
	{
		return enumeration;
	}*/
	
	private BufferedImage[] getImages(Sky sky) throws IOException {
		switch(sky)
		{
			case CLOUDS:
				return new BufferedImage[]{
						ImageIO.read(getClass().getClassLoader().getResourceAsStream( "com/golemgame/data/textures/skybox/clouds/north.jpg")),
						ImageIO.read(getClass().getClassLoader().getResourceAsStream( "com/golemgame/data/textures/skybox/clouds/east.jpg")),
						ImageIO.read(getClass().getClassLoader().getResourceAsStream( "com/golemgame/data/textures/skybox/clouds/south.jpg")),
						ImageIO.read(getClass().getClassLoader().getResourceAsStream( "com/golemgame/data/textures/skybox/clouds/west.jpg")),
						ImageIO.read(getClass().getClassLoader().getResourceAsStream( "com/golemgame/data/textures/skybox/clouds/down.jpg")),
						ImageIO.read(getClass().getClassLoader().getResourceAsStream( "com/golemgame/data/textures/skybox/clouds/up.jpg"))};
			case SERENE:
				return new BufferedImage[]{
						ImageIO.read(getClass().getClassLoader().getResourceAsStream( "com/golemgame/data/textures/skybox/serene/Xplus.jpg")),				
						ImageIO.read(getClass().getClassLoader().getResourceAsStream( "com/golemgame/data/textures/skybox/serene/Zminus.jpg")),
						ImageIO.read(getClass().getClassLoader().getResourceAsStream( "com/golemgame/data/textures/skybox/serene/Xminus.jpg")),
						ImageIO.read(getClass().getClassLoader().getResourceAsStream( "com/golemgame/data/textures/skybox/serene/Zplus.jpg")),
						ImageIO.read(getClass().getClassLoader().getResourceAsStream( "com/golemgame/data/textures/skybox/serene/Yplus.jpg")),
						ImageIO.read(getClass().getClassLoader().getResourceAsStream( "com/golemgame/data/textures/skybox/serene/Yminus.jpg"))};
		
			case SUNSET:
				return new BufferedImage[]{
						ImageIO.read(getClass().getClassLoader().getResourceAsStream( "com/golemgame/data/textures/skybox/sunset/Xplus.jpg")),				
						ImageIO.read(getClass().getClassLoader().getResourceAsStream( "com/golemgame/data/textures/skybox/sunset/Zminus.jpg")),
						ImageIO.read(getClass().getClassLoader().getResourceAsStream( "com/golemgame/data/textures/skybox/sunset/Xminus.jpg")),
						ImageIO.read(getClass().getClassLoader().getResourceAsStream( "com/golemgame/data/textures/skybox/sunset/Zplus.jpg")),
						ImageIO.read(getClass().getClassLoader().getResourceAsStream( "com/golemgame/data/textures/skybox/sunset/Yplus.jpg")),
						ImageIO.read(getClass().getClassLoader().getResourceAsStream( "com/golemgame/data/textures/skybox/sunset/Yminus.jpg"))};
				
			case STARS:
				return new BufferedImage[]{
						ImageIO.read(getClass().getClassLoader().getResourceAsStream( "com/golemgame/data/textures/skybox/stars/Xplus.png")),				
						ImageIO.read(getClass().getClassLoader().getResourceAsStream( "com/golemgame/data/textures/skybox/stars/Zminus.png")),
						ImageIO.read(getClass().getClassLoader().getResourceAsStream( "com/golemgame/data/textures/skybox/stars/Xminus.png")),
						ImageIO.read(getClass().getClassLoader().getResourceAsStream( "com/golemgame/data/textures/skybox/stars/Zplus.png")),
						ImageIO.read(getClass().getClassLoader().getResourceAsStream( "com/golemgame/data/textures/skybox/stars/Yminus.png")),
						ImageIO.read(getClass().getClassLoader().getResourceAsStream( "com/golemgame/data/textures/skybox/stars/Yplus.png"))};
				
			
						
		
			
				
			default:
				return null;
		}
	}
}
