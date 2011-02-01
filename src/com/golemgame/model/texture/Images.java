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
package com.golemgame.model.texture;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.golemgame.model.texture.TextureTypeKey.TextureShape;
import com.golemgame.mvc.golems.AppearanceInterpreter.ImageType;
import com.golemgame.states.StateManager;


/**
 * A class mapping ImageClass enums to image fils
 * @author Sam
 *
 */
public class Images {
	private static final String textureDirectory = "com/golemgame/data/textures/";

	private final  static  Images instance = new Images();
	
	public static Map<ImageType, String> nameMap = new HashMap<ImageType, String>();
	static
	{
		nameMap.put(ImageType.DEFAULT, "Default");
		nameMap.put(ImageType.SPOTS, "Spots");
		nameMap.put(ImageType.STRIPES, "Stripes");
		nameMap.put(ImageType.CAMO, "Camouflage(1)");
		nameMap.put(ImageType.CAMO3, "Camouflage(2)");
		nameMap.put(ImageType.CAMO4, "Camouflage(3)");
		nameMap.put(ImageType.CHECKER1, "Checkers");
		nameMap.put(ImageType.CHECKER2, "Checkers(2)");
		nameMap.put(ImageType.CHECKER3, "Checkers(3)");

		nameMap.put(ImageType.STAR1, "Star");
		nameMap.put(ImageType.STAR2, "Stars(2)");
		nameMap.put(ImageType.STAR3, "Stars(3)");
		nameMap.put(ImageType.STAR4, "Stars(4)");
		nameMap.put(ImageType.RIVET1, "Rivets(1)");
		nameMap.put(ImageType.RIVET2, "Rivets(2)");
		nameMap.put(ImageType.TREADPLATE, "Treadplate");
		nameMap.put(ImageType.STEELBOX1, "Steel Box (1)");
		nameMap.put(ImageType.STEELBOX2, "Steel Box (2)");
		nameMap.put(ImageType.WOOD, "Wood (1)");
		nameMap.put(ImageType.WOOD2, "Wood (2)");
		nameMap.put(ImageType.CONCRETE, "Concrete");
		nameMap.put(ImageType.GALVANIZED, "Galvanized Metal");
		
		
		nameMap.put(ImageType.ARROW, "Arrow");
		nameMap.put(ImageType.TRIANGLE3, "Triangles");
		nameMap.put(ImageType.HEART, "Heart");
		nameMap.put(ImageType.FACE, "Face");
		nameMap.put(ImageType.HAMMER, "Hammer Finish");
		nameMap.put(ImageType.GRADIENT, "Gradient");
		nameMap.put(ImageType.TRIANGLE, "Triangle");
		
		
		//nameMap.put(ImageType.BARREL, "Barrel");
		nameMap.put(ImageType.VOID, "None");
	}
	
	private final Map<ImageType, String> fileNameMap = new HashMap<ImageType, String>();


	private final Map<TextureShape,Map<ImageType,ArrayList<URL>>> urlShapeMap = new HashMap<TextureShape,Map<ImageType,ArrayList<URL>>>();
/*	private Map<ImageType,URL> getURLMap(ImageType image,TextureShape shape, int elementNumber)
	{
		return urlShapeMap.get(shape);
	}*/
	
	private void putURL(URL url, ImageType image, TextureShape shape, int elementNumber)
	{
		ArrayList<URL> shapes = urlShapeMap.get(shape).get(image);
		while(shapes.size()<elementNumber+1)
			shapes.add(null);
		
		shapes.set(elementNumber, url);
		 
	}
	private void putURL(URL url, ImageType image, TextureShape shape)
	{
		putURL(url,image,shape,0);
	}
	public int elements(ImageType image,TextureShape shape)
	{
		ArrayList<URL> shapes = urlShapeMap.get(shape).get(image);
		if(shapes == null)
			return 0;
		else{
			int count = 0;
			for(int i = 0;i<shapes.size();i++)
			{	
				if(shapes.get(i)!=null)
					count++;
			}
			return count;
		}
	}
	private URL getURLImageType(ImageType image,TextureShape shape,int elementNumber)
	{
		if(image == ImageType.TEXT || image==ImageType.VOID)
			throw new IllegalArgumentException(image.name() + " cannot be loaded.");
		return urlShapeMap.get(shape).get(image).get(elementNumber);
	}
	

	
	public Images() {
		
		findImages();
	}

	
	private void findImages() {
		fileNameMap.clear();
		urlShapeMap.clear();
		
		fileNameMap.put(ImageType.DEFAULT, "spots.png");
		fileNameMap.put(ImageType.SPOTS, "spots.png");
		fileNameMap.put(ImageType.STRIPES, "Stripes.png");
		fileNameMap.put(ImageType.CAMO, "Camouflage1.png");
		fileNameMap.put(ImageType.CAMO3, "Camouflage3.png");
		fileNameMap.put(ImageType.CAMO4, "Camouflage4.png");
		fileNameMap.put(ImageType.CHECKER1, "Checker1.png");
		fileNameMap.put(ImageType.CHECKER2, "Checker2.png");
		fileNameMap.put(ImageType.CHECKER3, "Checker3.png");
		fileNameMap.put(ImageType.STAR1, "Star1.png");
		fileNameMap.put(ImageType.STAR2, "Star2.png");
		fileNameMap.put(ImageType.STAR3, "Star3.png");
		fileNameMap.put(ImageType.STAR4, "Star4.png");
		fileNameMap.put(ImageType.RIVET1, "rivet1.png");
		fileNameMap.put(ImageType.RIVET2, "rivet2.png");
		fileNameMap.put(ImageType.TREADPLATE, "DiamondPlate.png");
		fileNameMap.put(ImageType.STEELBOX1, "SteelBox1.png");
		fileNameMap.put(ImageType.STEELBOX2, "SteelBox2.png");
		fileNameMap.put(ImageType.WOOD, "Wood.png");
		fileNameMap.put(ImageType.WOOD2, "Wood2.png");
		fileNameMap.put(ImageType.CONCRETE, "Concrete.png");
		fileNameMap.put(ImageType.GALVANIZED, "Galvanized.png");
		
		fileNameMap.put(ImageType.HAMMER, "HammerFinish.png");
		fileNameMap.put(ImageType.GRADIENT, "Gradient.png");
		fileNameMap.put(ImageType.HEART, "Heart.png");
		fileNameMap.put(ImageType.FACE, "Face.png");
		fileNameMap.put(ImageType.TRIANGLE, "Triangle.png");
		fileNameMap.put(ImageType.ARROW, "Arrow.png");
		fileNameMap.put(ImageType.TRIANGLE3, "Triangle3.png");
		//fileNameMap.put(ImageType.BARREL, "Barrel.png");
		fileNameMap.put(ImageType.VOID, "");
	
	
	
	for(TextureShape shape:TextureShape.values())
	{
		urlShapeMap.put(shape, new HashMap<ImageType,ArrayList<URL>>());
		for(ImageType i:ImageType.values())
		{
			urlShapeMap.get(shape).put(i, new ArrayList<URL>(1));
		}
	}
	
	//querry the texture folders for their contents.
	String[] baseAddress = new String[TextureShape.values().length];
	
	baseAddress[TextureShape.Plane.ordinal()] = textureDirectory + "plane/";
	baseAddress[TextureShape.Box.ordinal()] = textureDirectory + "box/";
	baseAddress[TextureShape.Sphere.ordinal()] = textureDirectory + "sphere/";
	baseAddress[TextureShape.Cylinder.ordinal()] = textureDirectory + "cylinder/";
	baseAddress[TextureShape.Capsule.ordinal()] = textureDirectory + "capsule/";
	baseAddress[TextureShape.Cone.ordinal()] = textureDirectory + "cone/";
	
	for(TextureShape textureShape:TextureShape.values())
	{
		for(ImageType image:ImageType.values())
		{
			if(image==ImageType.VOID || image == ImageType.TEXT)
				continue;
			//if the url is valid, add it.
			URL url = getURL(baseAddress[textureShape.ordinal()],fileNameMap.get(image));
			if(url!=null)
			{
				putURL(url,image, textureShape);
				//urlShapeMap.get(textureShape).put(image, url);
			}else if (textureShape == TextureShape.Plane) 
			{
				StateManager.logError(new RuntimeException("Required texture " + image + " could not be found at " + baseAddress[textureShape.ordinal()] + fileNameMap.get(image) ));
			}
			
			//look for subfolders labelled 'entry#' within this shapes address. Note: The first entry (0) is in the base directory.
			int i = 1;
			String entryPath = "element"+(i++);
			URL entryURL =getURL(baseAddress[textureShape.ordinal()],entryPath);			
			while(entryURL!=null)
			{
				URL entryImageURL;
			
					entryImageURL = getURL(baseAddress[textureShape.ordinal()] + entryPath + "/",fileNameMap.get(image));
			 
					if(entryImageURL!=null)
					{
						putURL(entryImageURL,image, textureShape,i-1);
					}
					 entryPath = "element"+(i++);
				entryURL =getURL(baseAddress[textureShape.ordinal()],entryPath);	
			}
		}
	}
	}

	public boolean isShapeAvailable(ImageType image, TextureTypeKey.TextureShape shape)
	{
		if(shape == null)
			return false;
		return elements(image,shape)!=0;
	}
	
	public String getName(ImageType imageType)
	{
		String name =nameMap.get(imageType);
		if (name == null)
			name = "(Unnamed)";
		return name;
	}
	
	
	private  URL getURL(String directory, String resource)
	{		
	//	return getClass().getClassLoader().getResource(directory + "/" + resource);
		return StateManager.loadResource(directory + resource);
		//return StateManager.class.getClassLoader().getResource(directory + "/" + resource);
	}
	public InputStream getInputStream(ImageType textureClass,int element) throws IOException
	{
		return getInputStream(textureClass,TextureTypeKey.TextureShape.Plane,element);
	}
	public InputStream getInputStream(ImageType textureClass,TextureTypeKey.TextureShape shape,int elementNumber) throws IOException
	{
		URL url = getURL(textureClass,shape,elementNumber);
		if(url == null)
			throw new RuntimeException("Texture " + textureClass + " (" + shape + ") could not be found." );
		return getURL(textureClass,shape,elementNumber).openStream();
	}
	public URL getURL(ImageType textureClass) {
		return getURL(textureClass,TextureTypeKey.TextureShape.Plane,0);
	}
	public URL getURL(ImageType textureClass,TextureTypeKey.TextureShape shape,int element) {
		return getURLImageType(textureClass,shape,element);
	}

/*	public InputStream getInputStream(ImageType textureClass,TextureTypeKey.TextureShape shape) throws IOException
	{
		return getInputStream(textureClass,shape,0);
	}*/
	
	public static Images getInstance() {
		return instance;
	}

	public boolean suppliesMipmap(ImageType image, TextureShape shape)
	{
		if(shape==TextureShape.Plane)
			return true;
		return false;
	}
	
}
