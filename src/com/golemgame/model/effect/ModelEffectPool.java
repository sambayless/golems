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

import com.jme.renderer.ColorRGBA;

/**
 * Can change the model effects by changing the effect pool.
 * @author Sam
 *
 */
public class ModelEffectPool {
	private static ModelEffectPool instance = new ModelEffectPool();

	public static ModelEffectPool getInstance() {
		return instance;
	}
	
	private ModelEffect selectedEffect;
	
	public synchronized ModelEffect getSelectedEffect()
	{
		if (selectedEffect == null)
		{
			selectedEffect = new TintableColorEffect(new ColorRGBA(0f,0.7f,0f,1f),true);
		}
		
		return selectedEffect;
	}
	
	private ModelEffect wireModeHideEffect;
	public synchronized ModelEffect getWireModeHideEffect()
	{
		if (wireModeHideEffect == null)
		{
			wireModeHideEffect = new TintableColorEffect(new ColorRGBA(0.2f,0.2f,0.7f,0.3f),true);
		}
		
		return wireModeHideEffect;
	}
	private ModelEffect functionalModelEffect;
	public synchronized ModelEffect getFunctionalModelEffect()
	{
		if (functionalModelEffect == null)
		{
			functionalModelEffect = new TintableColorEffect(new ColorRGBA(1f,0.1f,0.1f,1),true);
		}
		
		return functionalModelEffect;
	}
	private ModelEffect defaultModelEffect;
	public synchronized ModelEffect getDefaultModelEffect()
	{
		if (defaultModelEffect == null)
		{
			defaultModelEffect= new TintableColorEffect(new ColorRGBA(0.5f,0.5f,0.5f,1f),true);
		}
		
		return defaultModelEffect;
	}
	
	private ModelEffect controlPointEffect;
	public synchronized ModelEffect getControlPointEffect()
	{
		if (controlPointEffect == null)
		{
			controlPointEffect = new TintableColorEffect(new ColorRGBA(1f,1f,1,1f),true);
		}
		
		return controlPointEffect;
	}
	
	
	private ModelEffect wirePositiveEffect;
	public synchronized ModelEffect getWirePositiveEffect()
	{
		if (wirePositiveEffect == null)
		{
			wirePositiveEffect = new TintableColorEffect(new ColorRGBA(0.8f,0.8f,0,1),true);
		}
		
		return wirePositiveEffect;
	}
	
	private ModelEffect wireNegativeEffect;
	public synchronized ModelEffect getWireNegativeEffect()
	{
		if (wireNegativeEffect == null)
		{
			wireNegativeEffect= new TintableColorEffect(new ColorRGBA(1f,0f,0,1),true);
		}
		
		return wireNegativeEffect;
	}
	

	public synchronized ModelEffect getWireSelectedEffect()
	{
		return getSelectedEffect();
	}
	
	private ModelEffect ghostEffect;
	public synchronized ModelEffect getGhostEffect()
	{
		if (ghostEffect == null)
		{
			ghostEffect= new TintableColorEffect(new ColorRGBA(1f,0f,0,0.7f),true);
		}
		
		return ghostEffect;
	}
	
	private ModelEffect wirePortInputEffect;
	public synchronized ModelEffect getWirePortInputEffect()
	{
		if (wirePortInputEffect == null)
		{
			wirePortInputEffect= new TintableColorEffect(new ColorRGBA(0f,1f,0,1f),true);
		}
		
		return wirePortInputEffect;
	}
	private ModelEffect wirePortOutputEffect;
	public synchronized ModelEffect getWirePortOutputEffect()
	{
		if (wirePortOutputEffect == null)
		{
			wirePortOutputEffect= new TintableColorEffect(new ColorRGBA(1f,1f,0f,1f),true);
		}
		
		return wirePortOutputEffect;
	}
	
	private ModelEffect unselectableEffect;
	public synchronized ModelEffect getUnselectableEffect()
	{
		if (unselectableEffect == null)
		{
			unselectableEffect= new TintableColorEffect(new ColorRGBA(0.3f,0.3f,0.3f,0.5f),true);
		}
		
		return unselectableEffect;
	}
/*
	public ModelEffect getEffectForName(String name) throws ObjectConstructionException
	{

		
		try {
			return (ModelEffect) this.getClass().getMethod(name, ( Class<?> ) null).invoke(this, ( Class<?> )null);
		} catch (Exception e)
		{
			throw new ObjectConstructionException(e);
		}
	
		
		
	}
	
	public void registerDataTypes()
	{
		//this pattern is applicable because each of these pool effects only ever has a single instance - so 
		//the normal constructor pattern doesnt make sense.
		
		final Map<String, Method> methodMap = new HashMap<String, Method>();
		try {
		methodMap.put("selectedEffect", this.getClass().getDeclaredMethod("getSelectedEffect",new Class[0]));
		
		methodMap.put("wireModeHideEffect", this.getClass().getDeclaredMethod("getWireModeHideEffect", new Class[0]));
		
		methodMap.put("functionalModelEffect", this.getClass().getDeclaredMethod("getFunctionalModelEffect", new Class[0]));
	
		methodMap.put("defaultModelEffect", this.getClass().getDeclaredMethod("getDefaultModelEffect", new Class[0]));
		
		methodMap.put("controlPointEffect", this.getClass().getDeclaredMethod("getControlPointEffect", new Class[0]));
		
		methodMap.put("wirePositiveEffect", this.getClass().getDeclaredMethod("getWirePositiveEffect", new Class[0]));
		
		methodMap.put("wireNegativeEffect", this.getClass().getDeclaredMethod("getWireNegativeEffect", new Class[0]));
		
		methodMap.put("wireSelectedEffect", this.getClass().getDeclaredMethod("getWireSelectedEffect", new Class[0]));
		
		methodMap.put("ghostEffect", this.getClass().getDeclaredMethod("getGhostEffect", new Class[0]));
		
		methodMap.put("wirePortInputEffect", this.getClass().getDeclaredMethod("getWirePortInputEffect", new Class[0]));
		
	
			methodMap.put("wirePortOutputEffect", this.getClass().getDeclaredMethod("getWirePortOutputEffect", new Class[0]));
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for (String effectName:methodMap.keySet())
		{
			final String effectNameF = effectName;
			final Method effectMethod = methodMap.get(effectNameF);
			LoadableCallback modelPoolLoader = new LoadableCallbackAdapter()
			{
				
				@Override
				public Object construct() {
					try {
							return 	effectMethod.invoke(this, (Object)null);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return getDefaultModelEffect();
				}

				@Override
				public void load(Object object, Store store) {
	
				}
			
				@Override
				public void store(Object object, Store store) {
					
				}
				
			};
			try {
				DataTypeMap.getInstance().put("model.effect.pool." + effectName, methodMap.get(effectName).invoke(this,new Object[0]).getClass() , modelPoolLoader );
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
	}
	
	*/
}
