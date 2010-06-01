package com.golemgame.util;

import com.golemgame.constructor.Updatable;
import com.golemgame.constructor.UpdateManager;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.MaterialState;


public class TransitionController {


		private ColorRGBA currentColor;

		private float[] times;
		private ColorRGBA[] colors;
		
		private MaterialState material;


		/**
		* Creates a transition controller capable of controlling color changes between an arbitrary number of colors.
		* These colors will be applied uniformly to the provided set of MaterialStates.
		*/
		public TransitionController(ColorRGBA[] colors, float[] times, MaterialState material) {
			
			this.colors = colors;
			this.times = times;
			this.material = material;
			
			this.currentColor = new ColorRGBA();
			currentColor.set(colors[0]);			
		}

		/**
		 * Call to cause a transition from the current state to the requested state
		 */
		public void transition(int state)
		{
			float generalTime;
			endColor = colors[state];
			generalTime = times[state];
				
			if (generalTime <= 0)
			{			
				UpdateManager.getInstance().remove(transition);			
				currentColor.set(endColor);
			
					material.setAmbient(currentColor);
					material.setDiffuse(currentColor);

						
			}else
			{
				rate[0] = (endColor.r - currentColor.r)/generalTime;
				rate[1] = (endColor.g - currentColor.g)/generalTime;
				rate[2] = (endColor.b - currentColor.b)/generalTime;
				rate[3] = (endColor.a - currentColor.a)/generalTime;
				UpdateManager.getInstance().add(transition);
			}

			
			
		}
		private ColorRGBA endColor;	
		private float[] rate = new float[4];
		
		private Updatable transition = new Updatable()
		{
			
			
			public int getOrder() {
				// TODO Auto-generated method stub
				return 0;
			}

			
			public void reset() {
				// TODO Auto-generated method stub
				
			}
			
			
			public void update(float time) 
			{
				currentColor.r += rate[0]* time;
				currentColor.g += rate[1]* time;
				currentColor.b += rate[2]* time;
				currentColor.a += rate[3]* time;
				
				if (((rate[0]>0?-1:1 )*(currentColor.r - endColor.r) <= 0)&&
					((rate[1]>0?-1:1 )*(currentColor.g - endColor.g) <= 0 )&&	
					((rate[2]>0?-1:1 )*(currentColor.b - endColor.b )<= 0 )&&	
					((rate[3]>0?-1:1 )*(currentColor.a - endColor.a ) <= 0))			
				{				
					currentColor.set(endColor);
				
						material.setAmbient(currentColor);
						material.setDiffuse(currentColor);

					
				
					//buttonQuad.updateRenderState();
					UpdateManager.getInstance().remove(this);
				}
				if ((Math.abs( currentColor.r)>1 )||
						(Math.abs( currentColor.g)>1 )||
						(Math.abs( currentColor.b)>1 )||
						(Math.abs( currentColor.a)>1 ))
				{	System.out.println("COLOR GLITCH");
				currentColor.set(endColor);
				}
					material.setAmbient(currentColor);
					material.setDiffuse(currentColor);

					
			}
			
		};
		
		
		
	}
