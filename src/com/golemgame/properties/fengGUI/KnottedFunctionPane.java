package com.golemgame.properties.fengGUI;


import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.fenggui.binding.render.Graphics;
import org.fenggui.binding.render.IOpenGL;
import org.fenggui.event.key.Key;
import org.fenggui.event.key.KeyPressedEvent;
import org.fenggui.event.mouse.MouseButton;
import org.fenggui.event.mouse.MouseDraggedEvent;
import org.fenggui.event.mouse.MousePressedEvent;
import org.fenggui.event.mouse.MouseReleasedEvent;
import org.fenggui.util.Color;

import com.golemgame.functional.FunctionSettings;
import com.golemgame.util.CombinedTransformationFunction;
import com.golemgame.util.LinearInterpolator;
import com.golemgame.util.TransformationFunction;
import com.golemgame.util.LinearInterpolator.LinearInterpolationFunction;


/**
 * This extends the function pane to make it interactive; users can click to place 'knots,' and adjust the knots to create linear interpolated sections of the function
 * 
 * @author Sam
 *
 */
public class KnottedFunctionPane extends GLFunctionPane {
	
	private ArrayList<Knot> knots;
	private Knot selected = null;
	private Knot hovered = null;
	private Knot current = null;

	private UnivariateRealFunction baseFunction;
	private CombinedTransformationFunction transformFunction;
	private LinearInterpolationFunction interpolatedFunction;
	private ArrayList<CurrentKnotListener> currentListeners = new ArrayList<CurrentKnotListener>();

	private boolean snap = true;
	private float snapDistance = 0.03f;

	public float getSnapDistance() {
		return snapDistance;
	}

	public void registerCurrentKnotListener(CurrentKnotListener l)
	{
		this.currentListeners.add(l);
	}
	
	public void setSnapDistance(float snapDistance) {
		this.snapDistance = snapDistance;
	}


	public boolean isSnap() {
		return snap;
	}


	public void setSnap(boolean snap) {
		this.snap = snap;
	}


	public TransformationFunction getTransformFunction() {
		return transformFunction;
	}


	
	public void paintContent(Graphics g, IOpenGL gl) {

		super.paintContent(g, gl);

		//paint knots

		for (Knot knot:knots)
		{
			knot.paint(g);


		}

	}


	
	public void setFunction(UnivariateRealFunction function, FunctionSettings f) 
	{
		if (knots == null)
			knots = new ArrayList<Knot>();
		clearKnots();
		if (function instanceof CombinedTransformationFunction)
		{
			this.transformFunction = (CombinedTransformationFunction) function;
			this.baseFunction = transformFunction.getBaseFunction();
			this.interpolatedFunction = transformFunction.getKnottedFunction();
			buildKnots(interpolatedFunction);
		}else 
		{
			this.baseFunction = function;
			this.interpolatedFunction =  LinearInterpolator.getInstance().interpolate(null, null);
			this.transformFunction = new CombinedTransformationFunction(interpolatedFunction,baseFunction,f.getMinX(),f.getMaxX(), f.getMaxY(), f.getMinY());
			
		}
		super.setFunction(transformFunction,f);
	}

	
	private void buildKnots(LinearInterpolationFunction function)
	{
		clearKnots();
		if (function == null)
			return;
		if (function.getXKnots() == null)
			return;
		
		for (double x:function.getXKnots())
		{				
				float y=(float) function.value(x);
				
				Knot newKnot = new Knot((float)x,y);
				knots.add(newKnot);
		}
		Collections.sort(knots);
		
	}
	
	public void clearKnots()
	{
		
		for (Knot knot:knots)
		{
			knot.delete();					
		}
		knots.clear();
		hovered = null;
		selected = null;
		setCurrent(null);
		
	}

	public Knot getCurrent() {
		return current;
	}


	public void setCurrent(Knot current) {
		
		
		this.current = current;
		if(currentListeners!=null){
			for(CurrentKnotListener l:currentListeners)
				l.currentKnotChanged(current);
		}
	}


	public KnottedFunctionPane() {
		super();
		if (knots == null)//Might get set in setFunction called from super class
			knots = new ArrayList<Knot>();
		super.setTraversable(true);
	}

	private void dehover()
	{
		if (hovered != null)
			hovered.setHovered(false);
	}

	private void hover(Knot knot)
	{
		dehover();
		knot.setHovered(true);
		hovered = knot;
	}


	private void deselect()
	{
		if (selected!= null)
		{
			selected.setSelected(false);
			selected = null;
		}
	}

	private void select(Knot knot)
	{
		deselect();
		dehover();
		setCurrent(knot);
		selected = knot;
		selected.setSelected(true);
	}

	private Knot comparator = new Knot();

	
	public void mouseDragged(MouseDraggedEvent mouseDraggedEvent) 
	{
		super.mouseDragged(mouseDraggedEvent);
		if(!super.isEnabled())
			return;
		if (selected!= null&& mouseDraggedEvent.getButton() == MouseButton.LEFT)
		{

			float x = mouseDraggedEvent.getLocalX(KnottedFunctionPane.this)- getAppearance().getLeftMargins();
			float y = mouseDraggedEvent.getLocalY(KnottedFunctionPane.this)- getAppearance().getBottomMargins();

			float fX = deScaleX(x);
			float fY = deScaleY(y);
			
			if(isSnap())
			{
				//snap to midles
				if(Math.abs(fY)<getSnapDistance())
					fY = 0;
				
				if(Math.abs(fX -((super.getMaxX() + super.getMinX())/2f))<  getSnapDistance())
					fX = ((super.getMaxX() + super.getMinX())/2f);
				
				//snap to outer edges
				if (Math.abs(fX - super.getMaxX())<getSnapDistance())
					fX = super.getMaxX();
				else if (Math.abs(fX - super.getMinX())<getSnapDistance())
					fX = super.getMinX();
				
				if (Math.abs(fY - super.getMaxY())<getSnapDistance())
					fY = super.getMaxY();
				else if (Math.abs(fY - super.getMinY())<getSnapDistance())
					fY = super.getMinY();
			}
			
			fX = (float) transformFunction.invertX(fX);
			

			fY = (float) transformFunction.invertY(fY);
			
			
		
			
			selected.getTranslation().setLocation(fX,fY);
			updateKnots();
			setCurrent(selected);
		}
	}

	public void updateCurrentKnot()
	{
		for(CurrentKnotListener l:currentListeners)
			l.currentKnotChanged(current);
	}
	
	public void updateKnots()
	{
		Collections.sort(knots);
		interpolateFunction();
	}

	
	public void mousePressed(MousePressedEvent mousePressedEvent) {
		//first, try to select a nearby knot

		//intead, vertex locations have to be in an internal unit system, that translates correctly when the graph is scaled.

		super.mousePressed(mousePressedEvent);
		if(!super.isEnabled())
			return;
		if (mousePressedEvent.getButton() == MouseButton.LEFT)
		{
			float x = mousePressedEvent.getLocalX(KnottedFunctionPane.this)- getAppearance().getLeftMargins();
			float y = mousePressedEvent.getLocalY(KnottedFunctionPane.this)- getAppearance().getBottomMargins();
	
			float fX = deScaleX(x);
			float fY = deScaleY(y);
	
			fX = (float) transformFunction.invertX(fX);
			fY = (float) transformFunction.invertY(fY);
	
			deselect();
	
			{
	
				Knot pressed = findKnot(x,y,fX,fY);
				if (pressed != null)
				{
					select(pressed);
				}
			}
	
			if (selected == null)
			{
	
				//Create a new knot, select it
				Knot newKnot = new Knot();
	
				newKnot.getTranslation().setLocation(fX, fY);
	/*
				float left = (float) transformFunction.invertX(getMinX());
				float right = (float) transformFunction.invertX(getMaxX());
				
				//if this knot is the left most knot, add a new know, farther to the left (if posible)
				if (knots.isEmpty()|| knots.get(0).getTranslation().x > fX)
				{
					Knot leftMost = new Knot();
					float leftPos = (fX - 0.2f);
					if (leftPos<left)
						leftPos = left;
					leftMost.getTranslation().setLocation(leftPos,fY);
					
					if (newKnot.getTranslation().x <= leftPos)
						newKnot.getTranslation().x =leftPos+ 0.01f;
					
					addKnot(leftMost);
				}
				
				//if this knot is the right most knot, add a new know, farther to the left (if posible)
				if (knots.isEmpty()|| knots.get(knots.size()-1).getTranslation().x < fX)
				{
					Knot rightMost = new Knot();
					float rightPos = (fX + 0.2f);
					if (rightPos>right)
						rightPos = right;
					rightMost.getTranslation().setLocation(rightPos,fY);
					
					if (newKnot.getTranslation().x >=rightPos)
						newKnot.getTranslation().x =rightPos- 0.01f;
					
					addKnot(rightMost);
				}
			*/
				
				addKnot(newKnot);
				
				if (newKnot.testContact(x, y))
				{//if a prexisting knot was clicked.
					select(newKnot);	
				}
			}		
			
		}
	}

	public void addKnot(Knot newKnot)
	{
		knots.add(newKnot);
		Collections.sort(knots);
		interpolateFunction();
	}
	
	
	public void mouseReleased(MouseReleasedEvent mouseReleasedEvent) {
		super.mouseReleased(mouseReleasedEvent);
		if(!super.isEnabled())
			return;
		if (mouseReleasedEvent.getButton() == MouseButton.LEFT)
			deselect();

	}

	private Knot findKnot(float x, float y, float fX, float fY)
	{
		if (!knots.isEmpty())
		{
			comparator.getTranslation().setLocation(fX,fY);
			int pos = Collections.binarySearch(knots, comparator);
			//find the closest knot, and then check if it contacts this position;
			if (pos<0)
			{
				pos = -pos ;
			}
			if (pos>=knots.size())
				pos = knots.size()-1;

			Knot pressed = knots.get(pos);

			if (pressed.testContact(x,y))
			{//if a prexisting knot was clicked.
				return pressed;		

			}

			if (selected == null && pos>0)
			{					
				pressed = knots.get(pos-1);
				if (pressed.testContact(x,y))
				{
					return pressed;	

				}
			}
			if (selected == null && pos>1)
			{
				pressed = knots.get(pos-2);
				if (pressed.testContact(x,y))
				{
					return pressed;		

				}


			}
			if (selected == null && pos < knots.size()-2)
			{
				pressed = knots.get(pos+1);
				if (pressed.testContact(x,y))
				{
					return pressed;		

				}
			}
		}
		return null;
	}








	
	public void keyPressed(KeyPressedEvent keyPressedEvent) {
		super.keyPressed(keyPressedEvent);
		if(!super.isEnabled())
			return;
		if (selected != null && (keyPressedEvent.getKeyClass() == Key.DELETE || keyPressedEvent.getKeyClass() == Key.BACKSPACE))
		{
			//delete this knot
			deleteKnot(selected);
		}
		
	}
	
	private void deleteKnot(Knot knot)
	{
		knot.delete();
		knots.remove(knot);		
		Collections.sort(knots);
		
		if (hovered == knot)
			dehover();
		if (selected == knot)
			deselect();
		if(current == knot)
			setCurrent(null);
		interpolateFunction();
		
	}


	private void interpolateFunction()
	{
		if (knots.size()>1)
		{
			//build set of interpolation points

			double[] xValues = new double[knots.size()];
			double[] yValues = new double[knots.size()];

			//Force the first and last vertices onto the function lines
	/*		Knot left = knots.get(0);
			Knot right = knots.get(knots.size()-1);
			try{
				left.getTranslation().y =(float) baseFunction.value(left.getTranslation().x);
				right.getTranslation().y =(float) baseFunction.value(right.getTranslation().x);
				
			}catch(FunctionEvaluationException e)
			{

			}*/

			for (int i = 0, size = knots.size();i<size;i++)
			{
				Point2D.Float translation = knots.get(i).getTranslation();
				xValues[i] =translation.x;
				yValues[i] = translation.y;
			}

			this.interpolatedFunction = LinearInterpolator.getInstance().interpolate(xValues, yValues);
			this.transformFunction.setKnottedFunction(interpolatedFunction);
		}else
		{
		/*	if (knots.size() ==1)
			{
				Knot left = knots.get(0);

				try{
					left.getTranslation().y =(float) baseFunction.value(left.getTranslation().x);

				}catch(FunctionEvaluationException e)
				{

				}
			}*/
		}

	}


	/**
	 * Knots represent vertices added to a function. Their translations are in function coordinates (use get/set local coordinates to deal with local widget coordinates).
	 * @author Sam
	 *
	 */
	public class Knot implements Comparable<Knot>
	{
		private boolean selected = false;
		private boolean hovered = false;

		private Color light = Color.YELLOW;
		private Color dark = Color.DARK_YELLOW;
		private Color selectedLight = Color.GREEN;
		private Color selectedDark = Color.DARK_GREEN;

		private Color hoveredLight = Color.RED;
		private Color hoveredDark = Color.DARK_RED;

		private Point2D.Float translation;
		public static final int radius = 5;
		public static final int error = 1;

		
		public int compareTo(Knot o) {
			return (o.translation.x > this.translation.x)? -1:1;
		}

		private Knot()
		{
			this(new  Point2D.Float());
		}

		private Knot( Point2D.Float translation)
		{
			this.translation = translation;

		}
		private Knot(float x, float y)
		{
			this(new  Point2D.Float(x,y));
		}

		public void delete()
		{
			
		}

		/**
		 * Test whether the point is on this knot (in GL coordinates, not function coordinates).
		 * @param x
		 * @param y
		 * @return
		 */
		public boolean testContact(float x, float y)
		{
			boolean result = true;
			result &= (Math.abs(x - getLocalTranslationX() ))<=radius+error;
			
			result &= (Math.abs(y - getLocalTranslationY()))<=(radius+error);
			
			return result;
		}



		public void paint(Graphics g)
		{
			if (selected)
			{
				g.drawBlendedFilledRect(getLocalTranslationX() - radius , getLocalTranslationY() - radius , radius*2, radius*2, selectedLight, selectedDark, selectedLight, selectedDark);

			}else if (hovered)
			{
				g.drawBlendedFilledRect(getLocalTranslationX() - radius , getLocalTranslationY() - radius , radius*2, radius*2,hoveredLight, hoveredDark, hoveredLight, hoveredDark);

			}else
			{
				g.drawBlendedFilledRect(getLocalTranslationX() - radius , getLocalTranslationY() - radius , radius*2, radius*2, light, dark, light, dark);

			}
		}

		public  Point2D.Float getTranslation() {
			return translation;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		public  Point2D.Float getLocalTranslation( Point2D.Float store)
		{
			if (store == null)
				store = new Point2D.Float();

			store.x = getScaleX(translation.x);
			store.y = getScaleY(translation.y);

			return store;
		}

		public int getLocalTranslationX()
		{
			return (int) getScaleX((float)transformFunction.transformX(translation.x));
		}

		public int getLocalTranslationY()
		{
			return (int) getScaleY((float)transformFunction.transformY(translation.y));
		}

		public boolean isHovered() {
			return hovered;
		}

		public void setHovered(boolean hovered) {
			this.hovered = hovered;
		}





	}


	
	public void mouseMoved(int displayX, int displayY) {
		if(!super.isEnabled())
			return;
		super.mouseMoved(displayX, displayY);

		if (selected == null)
		{

			float x =  displayX - getDisplayX()- getAppearance().getLeftMargins();
			float y = displayY - getDisplayY()- getAppearance().getBottomMargins();

			float fX = deScaleX(x);
			float fY = deScaleY(y);

			fX = (float) transformFunction.invertX(fX);
			fY = (float) transformFunction.invertY(fY);

			Knot hovered = findKnot(x,y,fX,fY);
			if (hovered != null)
			{
				hover(hovered);
			}else
				dehover();
		}


	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
	}

	
	public static interface CurrentKnotListener
	{
		public void currentKnotChanged(Knot current);
	
	}
}
