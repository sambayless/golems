package com.golemgame.properties.fengGUI;

import java.util.Set;

import org.fenggui.IWidget;
import org.fenggui.StatefullWidget;
import org.fenggui.appearance.DefaultAppearance;
import org.fenggui.binding.render.Graphics;
import org.fenggui.binding.render.IOpenGL;
import org.fenggui.binding.render.ImageFont;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.event.ActivationEvent;
import org.fenggui.event.IActivationListener;
import org.fenggui.event.IDragAndDropListener;
import org.fenggui.event.key.Key;
import org.fenggui.event.mouse.IMouseListener;
import org.fenggui.event.mouse.MouseAdapter;
import org.fenggui.event.mouse.MouseButton;
import org.fenggui.event.mouse.MousePressedEvent;
import org.fenggui.event.mouse.MouseReleasedEvent;
import org.fenggui.util.Color;
import org.fenggui.util.Dimension;

public class ScalingRuler extends StatefullWidget<DefaultAppearance> {

	private boolean horizontal;
	private KnottedFunctionPane pane;
	
	private float originalX = 0;
	private float originalY = 0;
	
	private float originalTranslationX = 0;
	private float originalTranslationY = 0;
	
	private float originalScaleX = 0;
	private float originalScaleY = 0;
	private MouseButton mouseButton = null;
	
	public String[] axis = new String[]{"-1","0","1"};


	public ScalingRuler(boolean horizontal, KnottedFunctionPane pane) {
		super();
		this.setAppearance(new DefaultAppearance(this));
		this.getAppearance().add(new PlainBackground(Color.GRAY));
		this.addMouseListener(mouseListener);
		this.pane = pane;
		this.horizontal = horizontal;
		setTraversable(true);
		buildListeners();
	}
	
	private void buildListeners() {
		addActivationListener(new IActivationListener() {

			public void widgetActivationChanged(ActivationEvent activationEvent)
			{
				boolean enabled = activationEvent.isEnabled();

				
				if (enabled) {
					if(getDisplay() != null) {
						getDisplay().addDndListener(dndListener);
					}
				} else {
					if(getDisplay() != null) {
						getDisplay().removeDndListener(dndListener);
					}
				}
			}});	
	}
	
	
	
	public void addedToWidgetTree() 
	{
		if(getDisplay() != null && isEnabled())
			getDisplay().addDndListener(dndListener);
	}

	
	public void removedFromWidgetTree() 
	{
		if(getDisplay() != null)
			getDisplay().removeDndListener(dndListener);
	}
	
	private IMouseListener mouseListener = new MouseAdapter()
	{
		/*
		
		public void mouseDragged(MouseDraggedEvent mouseDraggedEvent) {
			 MouseButton button = mouseDraggedEvent.getButton();
			 if (button == MouseButton.LEFT)
			 {//translate
				 if (horizontal)
					 translate(mouseDraggedEvent.getLocalX(ScalingRuler.this));
				 else
					 translate(mouseDraggedEvent.getLocalY(ScalingRuler.this));
			 }else if (button == MouseButton.RIGHT)
			 {//scale
				 if (horizontal)
					 scale(mouseDraggedEvent.getLocalX(ScalingRuler.this));
				 else
					 scale(mouseDraggedEvent.getLocalY(ScalingRuler.this));
			 }
		}*/

		
		public void mousePressed(MousePressedEvent mousePressedEvent) {
			mouseButton = mousePressedEvent.getButton();
			//originalX = mousePressedEvent.getLocalX(ScalingRuler.this);
			//originalY = mousePressedEvent.getLocalY(ScalingRuler.this);

		}

		
		public void mouseReleased(MouseReleasedEvent mouseReleasedEvent) {
			mouseButton = null;
		}

	};

	private void translate(int position)
	{		
		if (horizontal)
		{
			float amount = position - originalX;
			
			pane.getTransformFunction().setTranslateX(originalTranslationX+deScaleAmountX(amount));
		}else
		{
			float amount = position - originalY;
	
			pane.getTransformFunction().setTranslateY(originalTranslationY+deScaleAmountY(amount));
		}
		pane.updateCurrentKnot();
	}
	
	protected float deScaleAmountX(float x)
	{
		float maxX = pane.getMaxX();
		float minX = pane.getMinX();
		float scale = this.getAppearance().getContentWidth()/(maxX-minX);
		return (x)/scale;
	}
	
	protected float deScaleAmountY(float y)
	{
		float maxY = pane.getMaxY();
		float minY = pane.getMinY();
		float scale = this.getAppearance().getContentHeight()/(maxY-minY);
		return (y )/scale;
	}
	
	protected float deScaleX(float x)
	{
		float maxX = pane.getMaxX();
		float minX = pane.getMinX();
		float scale = this.getAppearance().getContentWidth()/(maxX-minX);
		return (x)/scale+minX;
	}
	
	protected float deScaleY(float y)
	{
		float maxY = pane.getMaxY();
		float minY = pane.getMinY();
		float scale = this.getAppearance().getContentHeight()/(maxY-minY);
		return (y)/scale+minY ;
	}

	
	private void scale(int position)
	{
	
		//scale the graph so that a point at the position that the scaling started at will be moved to this position

		//Should scaling be relative to wherever the mouse starts? or the center of the function pane?
		
		//convert this position to a percentage of the size of this ruler
		if (horizontal)
		{
			float scalePos = deScaleX(position);
			float scaleOriginal = deScaleX(originalX);
			
			float percent = scalePos/scaleOriginal;
			
			pane.getTransformFunction().setScaleX(originalScaleX/percent);

			//this is the change, in function terms
			//what scale factor is required to create that change
			
			
		}
		else
		{
			float scalePos = deScaleY(position);
			float scaleOriginal = deScaleY(originalY);
			
			float percent = scalePos/scaleOriginal;
			
			pane.getTransformFunction().setScaleY(originalScaleY/percent);
	
		}
		pane.updateCurrentKnot();
	}
	
	
	
	public Dimension getMinContentSize() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void paintContent(Graphics g, IOpenGL gl) {
		
		//Paint ticks
		paintTicks(g);
	}
	
	private void paintTicks(Graphics g)
	{
		if (axis == null || axis.length == 0)
			return;
		
		int spacing;
		int pos = 0;
		ImageFont oldFont = g.getFont();

		g.setColor(Color.WHITE);
		g.setFont(ImageFont.getDefaultFont());//have to set the font to default font here, otherwise their may be an error...

		int initialWordSize;
		
	
		if (this.isHorizontal())
		{
			initialWordSize= g.getFont().getWidth(axis[0]);
			spacing= (this.getWidth()-initialWordSize)/(axis.length-1);
		}
		else
		{
			initialWordSize= g.getFont().getLineHeight();
			spacing= (this.getHeight()-initialWordSize)/(axis.length-1);
		}
		
		for (String tick:axis)
		{
			if(this.isHorizontal())
				g.drawString(tick, pos+initialWordSize-g.getFont().getWidth(tick), this.getHeight()/2-(g.getFont().getHeight()/2));
			else
				g.drawString(tick, this.getWidth()/2-(g.getFont().getWidth(tick)/2), pos );
			pos+= spacing;
		}
		g.setFont(oldFont);
	}

	public boolean isHorizontal() {
		return horizontal;
	}
	

	


	public String[] getAxis() {
		return axis;
	}

	public void setAxis(String[] axis) {
		this.axis = axis;
	}
	private IDragAndDropListener dndListener = new IDragAndDropListener()
	{

		private int cacheDisplayX = -1; // only for performance reasons
		private int cacheDisplayY = -1;

		public void select(int x, int y, Set<Key> modifiers) 
		{
			if (!isEnabled())
				return;
			cacheDisplayX = getDisplayX();
			cacheDisplayY = getDisplayY();
	
			//pressed = hitsHorizontalSlider(x, y);
			originalX = x - cacheDisplayX;
			originalY = y - cacheDisplayY;
			originalTranslationX=(float)pane.getTransformFunction().getTranslateX();
			originalTranslationY=(float)pane.getTransformFunction().getTranslateY();
			originalScaleX = (float) pane.getTransformFunction().getScaleX();
			originalScaleY = (float) pane.getTransformFunction().getScaleY();
		}
		
		public void drag(int displayX, int displayY, Set<Key> modifiers) {
			if (!isEnabled())
				return;
			int x = displayX - cacheDisplayX;
			int y = displayY - cacheDisplayY;
			
			 if (mouseButton == MouseButton.LEFT)
			 {//translate
				 if (horizontal)
					 translate(x);
				 else
					 translate(y);
			 }else if (mouseButton == MouseButton.RIGHT)
			 {//scale
				 if (horizontal)
					 scale(x);
				 else
					 scale(y);
			 }
			
		}

		
		public void drop(int displayX, int displayY, IWidget droppedOn,
				Set<Key> modifiers) {
			// TODO Auto-generated method stub
			
		}

		
		public boolean isDndWidget(IWidget w, int displayX, int displayY) {
			return (w.equals(ScalingRuler.this));
		}

		
	};

	

}
