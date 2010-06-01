package com.golemgame.model.texture;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import com.golemgame.model.effect.TextureLayerEffect;
import com.golemgame.mvc.golems.AppearanceInterpreter.ImageType;



/**
 * This controls TextureWrappers applied to a class.
 * It chooses which unit the texture should be applied in.
 * @author Sam
 *
 */
public abstract class TextureController  implements Serializable{
	
	
	private static final long serialVersionUID = 1L;

	/**
	 * An ordered list of textures
	 */
	private ArrayList<TextureUnitPair> textureUnitPairs = new ArrayList<TextureUnitPair>();
//	private ModelEffectType[] types = new ModelEffectType[]{ModelEffectType.TEXTUREWRAPPER};
	
	private Map<TextureLayerEffect,TextureUnitPair> layerMap = new HashMap<TextureLayerEffect, TextureUnitPair>();
	
	private int numberAdded = 0;
	private transient WrapperIterator wrapperIterator;
	
	public TextureController() {
		wrapperIterator = new WrapperIterator();
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		wrapperIterator = new WrapperIterator();
	}
	
	/**
	 * Add a (loaded) texture layer effect to the controller.
	 * @param layer
	 */
	public void addTextureLayerEffect(TextureLayerEffect layer)
	{
		TextureWrapper textureWrapper = layer.getTextureWrapper();
		int maximumUnit = layer.getMaximumAllowedLayer();
		if(textureWrapper.getTextureTypeKey(0).getImage()== ImageType.VOID)
		{
			apply();
			return;
		}
		
		if(!textureWrapper.isLoaded())
			return;//require the textue wrapper to be loaded
		
		if (findTextureWrapper(textureWrapper, maximumUnit)>=0)
		{
			apply();
			return;//check if the texture wrapper is already added at this layer.
		}
		

		//layer.addLoadingListener(this);

		
		TextureUnitPair unitPair = new TextureUnitPair(layer, maximumUnit, layer.getPrecedence());
		layerMap.put(layer, unitPair);
		//layer.addUpdateListener(this);
		//first, see if there is an available slot that is no deeper than maximum unit.
		if (textureUnitPairs.size()<=maximumUnit)
		{
			textureUnitPairs.add(unitPair);			
		}else
		{
			for (int i = 0; i < maximumUnit;i++)
			{//if there is an object with a greater maximum unit in this texture's spot, replace it.
				if (textureUnitPairs.get(i).maximumUnit > unitPair.maximumUnit)
				{
					textureUnitPairs.add(unitPair);
					break;
				}
			}
		}
		
		
		Collections.sort(textureUnitPairs);

		this.apply();
	}
	
	public Iterator<TextureWrapper> getTextureWrappers()
	{
		Collections.sort(textureUnitPairs);
		wrapperIterator.reset();
		return wrapperIterator;
	}
	
	public void removeTextureLayerEffect(TextureLayerEffect layer)
	{
	//	layer.removeUpdateListener(this);
		TextureUnitPair pair = layerMap.get(layer);
		if (pair != null)
		{
			removeTextureWrapper(pair.getTextureLayer().getTextureWrapper(),pair.getMaximumUnit());
			layerMap.remove(layer);
			
		}
		apply();
	}
	

	/**
	 * Efficiently remove the texture wrapper with a given maximum unit from the list
	 * @param textureWrapper
	 * @param maximumUnit
	 */
	private void removeTextureWrapper(TextureWrapper textureWrapper, int maximumUnit)
	{

		
		int pos = findTextureWrapper(textureWrapper, maximumUnit);
		if (pos>=0)
			textureUnitPairs.remove(pos);
		
	}
	

	
	public  abstract void apply();
	
	private int findTextureWrapper(TextureWrapper textureWrapper, int maximumUnit)
	{
		int pos = Collections.binarySearch(textureUnitPairs, TextureUnitPair.getComparator(maximumUnit));
		
		if (pos<0)
			pos = -pos-1;
		
		//now, search until the actual texture wrapper is found, as there may be multiple wrappers with the same max unit at this position
		if (pos>=0)
		{
			for (;pos<textureUnitPairs.size(); pos++)
			{
				if (textureUnitPairs.get(pos).maximumUnit!= maximumUnit)
					return -1;//it wasn't found.
				if (textureUnitPairs.get(pos).getTextureLayer().getTextureWrapper().equals(textureWrapper))
				{
				
					return pos;
				}
			}
			
		}
		
		
		return -1;
	}


	/**
	 * The texture order might have changed. Update as needed to match the new texture order.
	 */
	protected abstract void resetTextures();

	
	private static class TextureUnitPair implements Comparable<TextureUnitPair>, Serializable
	{

		private static final long serialVersionUID = 1L;
		private final TextureLayerEffect textureLayer;
		private int maximumUnit;
		private final int precedence;
		
		private final boolean isComparator ;
		private static TextureUnitPair comparatorUnit = new TextureUnitPair();
		
		
		public static TextureUnitPair getComparator(int maximumUnit)
		{
			comparatorUnit.maximumUnit = maximumUnit;
			return comparatorUnit;
		}
		
		private TextureUnitPair ()
		{
			isComparator = true;
			this.textureLayer = null;
			this.maximumUnit = 0;
			this.precedence = 0;
		}
		
		public TextureUnitPair(TextureLayerEffect textureLayer, int maximumUnit, int orderAdded) {
			super();
			isComparator = false;
			this.textureLayer = textureLayer;
			this.maximumUnit = maximumUnit;
			this.precedence = orderAdded;
		}
		public TextureLayerEffect getTextureLayer() {
			return textureLayer;
		}
		public int getMaximumUnit() {
			return maximumUnit;
		}
		
		
		public int getOrderAdded() {
			return precedence;
		}
		
		public int compareTo(TextureUnitPair o) {
			/*if (o.maximumUnit < this.maximumUnit)
				return 1;
			else if (o.maximumUnit>this.maximumUnit)
				return -1;*/
			if (o.isComparator )
			{//ignore the time it was added to the controller, just find the first member that has the same maximum Unit
				return 1;
			}else if (this.isComparator)
			{
				return -1;
			}
		
			
		/*	
			if (o.getOrderAdded() == Integer.MAX_VALUE && this.getOrderAdded() == Integer.MIN_VALUE)
				return -1;//special case: make the integer wrap around max to min
			else if (o.getOrderAdded() == Integer.MIN_VALUE && this.getOrderAdded() == Integer.MAX_VALUE)
				return 1;*/

			
			return this.getOrderAdded()- o.getOrderAdded(); //if order was added AFTER this, put it FIRST in the array.
		}
	}

	

	/*
	public ModelEffectType[] getEffectTypes() {
		return types;
	}*/

	
	
	private class WrapperIterator  implements Iterator<TextureWrapper>
	{
		private int currentIndex = 0;
		private int currentUnit = 0;
		
		public void reset()
		{
			currentIndex = 0;
			currentUnit = 0;
		}
		
		
		public boolean hasNext() {
			while (currentIndex < textureUnitPairs.size())
			{
				TextureUnitPair pair = textureUnitPairs.get(currentIndex);
				if(pair.getTextureLayer().getTextureWrapper().getTextureTypeKey(0).getImage()!= ImageType.VOID)
				{
					if (pair.maximumUnit>= currentUnit)
					{
						
						return true;
					}
				}
				currentIndex++; //increment the index AFTER the last loop, so that it is in the right spot for next();
				
			}
			return false;
		}

		
		public TextureWrapper next() {
			
			
			//get the unit pair at the current index, and increment the current index
			while (currentIndex < textureUnitPairs.size())
			{
				TextureUnitPair pair = textureUnitPairs.get(currentIndex);
				currentIndex++;
				if(pair.getTextureLayer().getTextureWrapper().getTextureTypeKey(0).getImage()!= ImageType.VOID)
				{//skip voids
					if (pair.maximumUnit>= currentUnit)
					{
						currentUnit ++;
						return pair.getTextureLayer().getTextureWrapper();
					}
				}
				
			}
			
			throw new NoSuchElementException ();
		}

		
		public void remove() {
			throw new UnsupportedOperationException ();
			
		}
		
	};
	
}
