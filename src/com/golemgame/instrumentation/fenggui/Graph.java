package com.golemgame.instrumentation.fenggui;


import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.fenggui.StatefullWidget;
import org.fenggui.appearance.DefaultAppearance;
import org.fenggui.binding.render.Graphics;
import org.fenggui.binding.render.IOpenGL;
import org.fenggui.binding.render.ImageFont;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.util.Color;
import org.fenggui.util.Dimension;

/**
 * This is a simple pane that displays a function. It also displays (optionally) vertex positions, which can be used to manipulate sections of the function.
 * The function is scaled to fit the size, in pixels, of the function pane.
 * @author Sam
 *
 */
public class Graph extends StatefullWidget<DefaultAppearance>{

	public static final Dimension MIN_SIZE = new Dimension(350,350);
	
	private LinkedList<Point2D.Float> plotPoints = new LinkedList<Point2D.Float>();

	public static enum GraphStyle
	{
		FILLED,LINE;
	}
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

	private boolean writeY = true;

	
	protected boolean isWriteY() {
		return writeY;
	}

	protected void setWriteY(boolean writeY) {
		this.writeY = writeY;
	}




	private boolean xAxisIsRelative=false;
	
	private GraphStyle style = GraphStyle.FILLED;
	
	public GraphStyle getStyle() {
		return style;
	}

	public void setStyle(GraphStyle style) {
		this.style = style;
	}

	public boolean isXAxisIsRelative() {
		return xAxisIsRelative;
	}

	public void setXAxisIsRelative(boolean axisIsRelative) {
		xAxisIsRelative = axisIsRelative;
	}




	private boolean changed = true;
	
	private Lock plotLock = new ReentrantLock();
	
	public Graph() {
		super();
		//super.setSize(new Dimension(200,200));
	

		this.setAppearance(new DefaultAppearance(this));
		this.getAppearance().add(new PlainBackground(Color.BLACK));
		updateMinSize();
		minX = -100;
		maxX = 100;
		minY = -100;
		maxY = 100;

		
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
		
	//	drawFunction(g,,minX,maxX,minY, maxY,2);
		drawZeroLine(g);
		plotLock.lock();
		try{
			if(!plotPoints.isEmpty())
			{
				if(style==GraphStyle.FILLED)
				{
					drawQuadStripFunction(g,plotPoints,minX,minY,Color.LIGHT_GREEN,Color.WHITE);
				}else if(style==GraphStyle.LINE)
				{
					drawLineFunction(g,plotPoints,minX,minY,Color.LIGHT_GREEN,Color.WHITE);		
				}
				
			}
		}finally{
			plotLock.unlock();
		}

		if(this.isWriteY())
		{
			if(!plotPoints.isEmpty())
			{
				g.setColor(Color.GRAY);
				if(g.getFont()==null)
					g.setFont(ImageFont.getDefaultFont());
				if(g.getFont()!=null){
					String yVal = String.valueOf(Math.round( plotPoints.get(plotPoints.size()-1).y * 10.0)/10.0);
					g.drawString(yVal, this.getWidth()- g.getFont().getWidth(yVal), this.getHeight()-g.getFont().getHeight());
				}
			}
		}
		
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
		gl.lineWidth(1);
		g.setColor(Color.RED);
		g.drawLine((int)getScaleX(minX),(int) getScaleY(0), (int)getScaleX(maxX), (int)getScaleY(0));
		g.setColor(Color.GREEN);
		g.drawLine((int)getScaleX((maxX + minX)/2f),(int) getScaleY(minY), (int)getScaleX((maxX + minX)/2f), (int)getScaleY(maxY));

	}
	public void drawLineFunction(Graphics g, LinkedList<Point2D.Float> points, float minX, float minY, Color topColor, Color bottomColor)
	{
		if(points.isEmpty())
			return;
		
		IOpenGL gl = g.getOpenGL();
	gl.lineWidth(1);
			gl.startLineStrip();
	
		
		float xOffset=  getDisplayX() + this.getAppearance().getLeftMargins();//g.getOffset().getX();
		float yOffset = getDisplayY() + this.getAppearance().getBottomMargins();// g.getOffset().getY();
		
		
		float startX = points.getFirst().x;
		
		gl.vertex( getScaleX(startX)+xOffset, getScaleY(points.getFirst().y) + yOffset+minY);
		
		for(Point2D.Float point:points)
		{
			g.setColor(bottomColor);
			//gl.color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
			
			float xPos = ( point.x);
			if(xAxisIsRelative)
			{
				xPos+=startX;
				startX =xPos;
			}else{
				xPos -= startX;//assume 0,0
			}
			xPos = getScaleX(xPos);
			float yPos =  getScaleY(point.y);
			//gl.vertex( xPos+xOffset, yOffset+minY);
			
			g.setColor(topColor);
			gl.vertex( xPos+xOffset,yPos+yOffset);
			
		}
	
		gl.end();
		
	}
	
	public void drawQuadStripFunction(Graphics g, LinkedList<Point2D.Float> points, float minX, float minY, Color topColor, Color bottomColor)
	{
		if(points.isEmpty())
			return;
		
		IOpenGL gl = g.getOpenGL();
		gl.lineWidth(1f);
		if(style==GraphStyle.FILLED)
		{
			gl.startQuadStrip();
		}else if(style==GraphStyle.LINE)
		{
			gl.startLines();			
		}
		
		float xOffset=  getDisplayX() + this.getAppearance().getLeftMargins();//g.getOffset().getX();
		float yOffset = getDisplayY() + this.getAppearance().getBottomMargins();// g.getOffset().getY();
		
		
		float startX = points.getFirst().x;
		
		for(Point2D.Float point:points)
		{
			g.setColor(bottomColor);
			//gl.color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
			
			float xPos = ( point.x);
			if(xAxisIsRelative)
			{
				xPos+=startX;
				startX =xPos;
			}else{
				xPos -= startX;//assume 0,0
			}
			xPos = getScaleX(xPos);
			float yPos =  getScaleY(point.y);
			gl.vertex( xPos+xOffset, yOffset+minY);
			
			g.setColor(topColor);
			gl.vertex( xPos+xOffset,yPos+yOffset);
			
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
		float scale =( this.getAppearance().getContentHeight()-2)/(maxY-minY);//adjust so you can see 100%
		return (y - minY)*scale + 1;
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

	public void addPoint(Point2D.Float point)
	{
		plotLock.lock();
		try{
			plotPoints.add(point);
			
			trimPoints();
		
			
		}finally{
			plotLock.unlock();
		}
	}
	private void trimPoints()
	{
		float xSize = 0;
		for(Point2D.Float point:plotPoints)
		{
			xSize += point.x;
		}
		
		while(xSize>maxX-minX &! plotPoints.isEmpty())
		{
			Point2D.Float point =  plotPoints.removeFirst();
			xSize-= point.x;
		}

	}
}
