package com.golemgame.properties.fengGUI;


import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.fenggui.StatefullWidget;
import org.fenggui.appearance.DefaultAppearance;
import org.fenggui.binding.render.Graphics;
import org.fenggui.binding.render.IOpenGL;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.util.Color;
import org.fenggui.util.Dimension;

import com.golemgame.functional.FunctionSettings;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.states.StateManager;
import com.golemgame.util.SinFunction;

/**
 * This is a simple pane that displays a function. It also displays (optionally) vertex positions, which can be used to manipulate sections of the function.
 * The function is scaled to fit the size, in pixels, of the function pane.
 * @author Sam
 *
 */
public class GLFunctionPane extends StatefullWidget<DefaultAppearance>{

	public static final Dimension MIN_SIZE = new Dimension(200,200);
	
	private UnivariateRealFunction function;

	
	/**
	 * The min and max x axis of the function to draw
	 */
	private float minX = 0;
	private float maxX = 100;
	
	/**
	 * The min and max y axis of the function to draw
	 */
	private float minY = 0;
	private float maxY = 0;



	
	private boolean changed = true;
	
	public GLFunctionPane() {
		super();
		//super.setSize(new Dimension(200,200));
	

		this.setAppearance(new DefaultAppearance(this));
		this.getAppearance().add(new PlainBackground(Color.BLACK));
		updateMinSize();
		minX = -100;
		maxX = 100;
		minY = -100;
		maxY = 100;

		setFunction( new SinFunction(50,10,1),new FunctionSettings(new PropertyStore()));
		

	}

	@Override
	public Dimension getMinContentSize() {

		return MIN_SIZE;
	}

	private Color disabledColor = new Color(0.3f,0.3f,0.3f,.7f);
	
	@Override
	public void paintContent(Graphics g, IOpenGL gl) {
		//draw function
		//draw vertices
	
		//if(super.isEnabled())
		
		drawFunction(g,getFunction(),minX,maxX,minY, maxY,2);
		
		drawZeroLine(g);
		
		/*
		 * Draw a dark screen over this
		 */
		if(!super.isEnabled())
		{
			
			g.setColor(disabledColor);
			g.drawFilledRectangle(0, 0, this.getWidth(), getHeight());
		}
	}
	
	private void drawZeroLine(Graphics g)
	{
		IOpenGL gl = g.getOpenGL();
		gl.lineWidth(1f);
		g.setColor(Color.RED);
		g.drawLine((int)getScaleX(minX),(int) getScaleY(0), (int)getScaleX(maxX), (int)getScaleY(0));
		g.setColor(Color.GREEN);
		g.drawLine((int)getScaleX((maxX + minX)/2f),(int) getScaleY(minY), (int)getScaleX((maxX + minX)/2f), (int)getScaleY(maxY));

	}
	
	/**
	 * Draw the function, between (in the functions coordinates) minX, minY, and maxX and maxY. Draw it so that there is a point for every step'th pixel (so step is in screen coordinates).
	 * @param g
	 * @param function
	 * @param minX
	 * @param maxX
	 * @param minY
	 * @param maxY
	 * @param step
	 */
	private void drawFunction(Graphics g, UnivariateRealFunction function, float minX, float maxX, float minY, float maxY, float step)
	{		
		//use quad strips instead
		
		//
		
		float fStep;
		fStep = step* (maxX-minX)/(this.getAppearance().getContentWidth());
		
		int number =(int)( (maxX-minX)/fStep);
		if ((maxX-minX)%fStep>0)
			number ++;//there will be one extra end position
		number += 3;//the corners
		float[] xValues = new float[number];
		float[] yValues = new float[number];		
		
		xValues[0] = 0;
		yValues[0] = 0;
		float x = minX;
		try{		
		
			for (int i = 1; i<number-1;i++)
			{
				
				if (x> maxX)
					x = maxX;
				float y = (float)function.value(x);
				xValues[i] = getScaleX(x);
				yValues[i] = getScaleY(y);
				x+= fStep;
				//System.out.println(xValues[i] + "\t" + yValues[i] + "\t" +  this.getWidth()+ "\t" + this.getHeight());
			}
		
			xValues[number-1] = this.getAppearance().getContentWidth() ;
			yValues[number-1]=0;
		
	
		}catch(FunctionEvaluationException e)
		{
			
			StateManager.logError(e);
			return;
		}
	
		drawQuadStripFunction(g,xValues,yValues,getScaleX(minX), getScaleY(minY),Color.BLUE, Color.DARK_BLUE );
		
	}
	
	public void drawQuadStripFunction(Graphics g, float[] xValues, float[] yValues, float minX, float minY, Color topColor, Color bottomColor)
	{
		
		IOpenGL gl = g.getOpenGL();
		gl.lineWidth(1f);
		gl.startQuadStrip();
		
		
		//g.setColor(bottomColor);
		
		//gl.vertex(minX, minY);
		
		float xOffset=  getDisplayX() + this.getAppearance().getLeftMargins();//g.getOffset().getX();
		float yOffset = getDisplayY() + this.getAppearance().getBottomMargins();// g.getOffset().getY();
		for (int i = 0; i <xValues.length;i++)
		//for (int i = xValues.length-1; i >= 0;i--)
		{
			g.setColor(bottomColor);
			//gl.color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
			gl.vertex(xValues[i]+xOffset, yOffset+minY);
			
			g.setColor(topColor);
			gl.vertex(xValues[i]+xOffset, yValues[i]+yOffset);
			
		}
	
		gl.end();
		
	}
	
	/**
	 * Gets, in local widget coordinates, the x position (in pixels) of an x position on the function.
	 * @param y
	 * @return
	 */
	protected float getScaleX(float x)
	{
		float scale = this.getAppearance().getContentWidth()/(maxX-minX);		
		return (x - minX)*scale;
	}
	
	/**
	 * Gets, in local widget coordinates, the y position (in pixels) of a y position on the function.
	 * @param y
	 * @return
	 */
	protected float getScaleY(float y)
	{
		float scale = this.getAppearance().getContentHeight()/(maxY-minY);
		return (y - minY)*scale;
	}
	
	protected float deScaleX(float x)
	{
		float scale = this.getAppearance().getContentWidth()/(maxX-minX);
		return (x)/scale+ minX ;
	}
	
	protected float deScaleY(float y)
	{
		float scale = this.getAppearance().getContentHeight()/(maxY-minY);
		return (y )/scale + minY ;
	}




	public UnivariateRealFunction getFunction() {
		return function;
	}
	
	
	

	public boolean isChanged() {
		return changed;
	}

	public float getMinX() {
		return minX;
	}

	public void setMinX(float minX) {
		this.minX = minX;
	}

	public float getMaxX() {
		return maxX;
	}

	public void setMaxX(float maxX) {
		this.maxX = maxX;
	}

	public float getMinY() {
		return minY;
	}

	public void setMinY(float minY) {
		this.minY = minY;
	}

	public float getMaxY() {
		return maxY;
	}

	public void setMaxY(float maxY) {
		this.maxY = maxY;
	}

	public void setFunction(UnivariateRealFunction function, FunctionSettings f) {
		this.function = function;
		//this.minX = settings.getMinX();
		this.maxX = f.getMaxX();
		this.minY = f.getMinY();
		this.maxY = f.getMaxY();
	}


	
	
}
