package com.golemgame.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.UnivariateRealInterpolator;


public class LinearInterpolator implements UnivariateRealInterpolator, Serializable  {
	private static final long serialVersionUID =1;
	private static final LinearInterpolator instance = new LinearInterpolator();
	
	public static LinearInterpolator getInstance() {
		return instance;
	}

	
	public LinearInterpolationFunction interpolate(double[] arg0, double[] arg1)			{
		return new LinearInterpolationFunction(arg0, arg1);
	}
	
	public static class LinearInterpolationFunction implements UnivariateRealFunction, KnottedFunction, Serializable
	{
		private static final long serialVersionUID =1;
		double[] x;
		double[]y;
		
		
		public LinearInterpolationFunction(double[]x, double[]y)
		{
			this.x = x;
			this.y = y;
			//these tests are no longer necessary (moved into golems validation), but they dont hurt.
			if(y != null){
				for(int i = 0;i<y.length;i++)
				{
					if(Double.isInfinite(y[i]) || Double.isNaN(y[i]))
						this.y[i] = 0f;
					
				}
			}
			if(x != null){
				for(int i = 0;i<x.length;i++)
				{
					if(Double.isInfinite(x[i]) || Double.isNaN(x[i]))
						this.x[i] = 0f;
					
				}
			}
		}
		
		public boolean isEmpty()
		{
			return (x==null || x.length<2 || y == null || y.length < 2);
		}
		
		
		public double value(double arg0){
			
			
			int pos = Arrays.binarySearch(x, arg0);
	        if (pos == -1)
	        {
	        	pos = 0;
	        }
	        else if (pos < 0) {
	        	pos = -pos - 2;
	        }
	        pos ++;
	        if ( pos >= x.length ) {
	        	pos= x.length-1;
	        }
	        double y0= y[pos-1];
	        double y1 =y[pos];
	        double x0= x[pos-1];
	        double x1 =x[pos];        
	        
	        if(x0 == x1)
	        	x1 += Double.MIN_VALUE;
	        return  y0 + (arg0-x0)*((y1-y0)/(x1-x0)) ;
	        
	        
		}
		
		
		public double[] getXKnots()
		{
			return x;
		}
		
		
		public double[] getYKnots()
		{
			return y;
		}
		
		
		public boolean isKnotted() {
			return true;
		}
		
		public int count() {
			return x == null? 0:x.length;
		}

	
		private transient ArrayList<Double> xList = null;
		private transient ArrayList<Double> yList = null;
		
	

		
	}
	
}
