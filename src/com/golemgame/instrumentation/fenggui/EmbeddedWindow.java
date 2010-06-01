package com.golemgame.instrumentation.fenggui;

import java.util.ArrayList;
import java.util.List;

import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.IWidget;
import org.fenggui.appearance.DecoratorAppearance;
import org.fenggui.composite.Window;
import org.fenggui.decorator.IDecorator;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.IPositionChangedListener;
import org.fenggui.event.IWindowResizedListener;
import org.fenggui.event.PositionChangedEvent;
import org.fenggui.event.WindowResizedEvent;
import org.fenggui.util.Spacing;

import com.jme.system.DisplaySystem;

public class EmbeddedWindow extends Window {

	//private boolean windowed = true;
	private boolean windowed = true;
	
	private boolean pinable = true;

	public boolean isPinable() {
		return pinable;
	}

	public void setPinable(boolean pinable) {
		this.pinable = pinable;
	}



	private List<IDecorator> backgroundApp = null;
	private Spacing oldBorder = null;
	private Spacing oldMargin = null;
	private Spacing oldContentMargin = null;
	//private int oldWidth = 0;
	//private int oldHeight = 0;
	/*private int oldX = 0;
	private int oldY = 0;*/
	private Button pinButton;
	public void setWindowed(boolean pinned) {
		if(this.windowed!=pinned)
		{

			if(pinned)
			{
				int minHeight = 1+ super.getAppearance().getTopMargins() + super.getAppearance().getBottomMargins() +
				super.getContentContainer().getAppearance().getTopMargins()+ super.getContentContainer().getAppearance().getBottomMargins();
				
				minHeight += super.titleBar.getHeight();

				int minWidth = 1 + super.getAppearance().getLeftMargins() + super.getAppearance().getRightMargins() +
					super.getContentContainer().getAppearance().getLeftMargins()+ super.getContentContainer().getAppearance().getRightMargins();
				super.getContentContainer().setMinSize(1, 1);
				super.setMinSize(minWidth, minHeight);
			
				if(backgroundApp!=null){
					for(IDecorator d:backgroundApp)
						super.getAppearance().add(d);
				}
				if(oldBorder!=null)
				{
					super.getAppearance().setBorder(oldBorder);
	
					super.getAppearance().setMargin(oldMargin);
					((Container)super.content).getAppearance().setMargin(oldContentMargin);
					
				}
				int oldWidth = super.getWidth() + super.getAppearance().getLeftMargins() + super.getAppearance().getRightMargins() + super.getContentContainer().getAppearance().getLeftMargins() + super.getContentContainer().getAppearance().getRightMargins();
				int oldHeight = super.getHeight() + super.getAppearance().getTopMargins() + super.getAppearance().getBottomMargins() + super.getContentContainer().getAppearance().getTopMargins() + super.getContentContainer().getAppearance().getBottomMargins() + super.titleBar.getHeight();
				
				super.setX(super.getX() - ( oldWidth-super.getWidth())/2);
				super.setWidth(oldWidth);
				super.setY(super.getY() - (oldHeight-super.getHeight())/2 + super.getTitleBar().getHeight()/2);
				super.titleBar.setVisible(true);
				super.titleBar.setMinSize(minWidth,super.titleBar.getHeight());
				super.setHeight(oldHeight);
		
			}else{
				int prefferedHeight = super.getContentContainer().getHeight()-  super.getContentContainer().getAppearance().getTopMargins() -  super.getContentContainer().getAppearance().getBottomMargins();
				
				int prefferedWidth = super.getContentContainer().getWidth() -  super.getContentContainer().getAppearance().getLeftMargins() -  super.getContentContainer().getAppearance().getRightMargins();
				
				int minHeight = 1+ super.getAppearance().getTopMargins() + super.getAppearance().getBottomMargins() +
				super.getContentContainer().getAppearance().getTopMargins()+ super.getContentContainer().getAppearance().getBottomMargins();
				
			
				int minWidth = 1 + super.getAppearance().getLeftMargins() + super.getAppearance().getRightMargins() +
					super.getContentContainer().getAppearance().getLeftMargins()+ super.getContentContainer().getAppearance().getRightMargins();
				super.setMinSize(1, 1);
				
				/*oldX = super.getX();
				oldY = super.getY();*/
			//	oldWidth = super.getWidth();
			//	oldHeight = super.getHeight();
				
				super.titleBar.setVisible(false);
				oldBorder = super.getAppearance().getBorder();
				oldMargin = super.getAppearance().getMargin();
				oldContentMargin = ((Container)super.content).getAppearance().getMargin();
				
				super.getAppearance().setBorder(new Spacing(0,0,0,0));

				super.getAppearance().setMargin(new Spacing(0,0,0,0));
	
				((Container)super.content).getAppearance().setMargin(new Spacing(0,0,0,0));
				
				backgroundApp = new ArrayList<IDecorator>(((DecoratorAppearance)super.getAppearance()).getBackgroundDecorators());
				super.getAppearance().removeAll();
				super.setMinSize(1, 1);
				super.getContentContainer().setMinSize(1, 1);
		
				super.setX(super.getX() +(getWidth()- prefferedWidth)/2);
				super.setY(super.getY() +(getHeight()- prefferedHeight)/2 - super.titleBar.getHeight()/2);
				
				super.setWidth(prefferedWidth);
				super.setHeight(prefferedHeight);
				
				super.setMinSize(1, 1);
				super.getContentContainer().setMinSize(1, 1);
			}
			this.windowed = pinned;

			this.layout();

		
		}
	
	}

	public EmbeddedWindow() {
		this(true,true,true,true);
	
	}

	public EmbeddedWindow( boolean closeBtn, boolean maximizeBtn, boolean minimizeBtn, boolean autoClose, boolean pinButton) {
		super(closeBtn, maximizeBtn, minimizeBtn, autoClose);
		if(pinButton)
		{
			//add pin button
			this.pinButton = new Button("~");
			this.pinButton.setExpandable(false);
			List<IWidget> widgets = new ArrayList<IWidget>();
			for(IWidget w:titleBar.getWidgets()){
				widgets.add(w);
			}
			titleBar.removeAllWidgets();
			if(!widgets.isEmpty())
				titleBar.addWidget(widgets.remove(0));//put the title bar first
			titleBar.addWidget(this.pinButton);
			for(IWidget w:widgets)
				titleBar.addWidget(w);
			this.pinButton.addButtonPressedListener(new IButtonPressedListener()
			{
				public void buttonPressed(ButtonPressedEvent e)
				{
					if(pinable)
						setWindowed(!isWindowed());
				}
			});
		}
		super.addPositionChangedListener(new IPositionChangedListener(){

			public void positionChanged(PositionChangedEvent event) {
				ensureBounds();
			}
			
		});
		super.addWindowResizedListener(new IWindowResizedListener(){

			public void windowResized(WindowResizedEvent windowResizedEvent) {
	
			
				ensureBounds();
			}
			
		});
		
		
	}
	
	@Override
	protected boolean setCheckedHeight(int height) {
		boolean changed = false;
		int oldHeight = super.getHeight();
		
		int minHeight = 1+ super.getAppearance().getTopMargins() + super.getAppearance().getBottomMargins() +
		super.getContentContainer().getAppearance().getTopMargins()+ super.getContentContainer().getAppearance().getBottomMargins();
		if(super.titleBar.isVisible())
			minHeight += super.titleBar.getHeight();
		if(height<minHeight )
			height =minHeight;

		if(height!=oldHeight)
		{
			changed = true;
			super.setHeight(height);
		}
		
		
		return changed;
	}

	@Override
	protected boolean setCheckedWidth(int width) {
		boolean changed = false;
		int oldWidth = super.getWidth();
		int minWidth = 1 + super.getAppearance().getLeftMargins() + super.getAppearance().getRightMargins() +
		super.getContentContainer().getAppearance().getLeftMargins()+ super.getContentContainer().getAppearance().getRightMargins();

		if(width<minWidth)
			width = minWidth;
		
		if(width!=oldWidth)
		{
			changed = true;
			super.setWidth(width);
		}
		
		
		return changed;
	}

	private void ensureBounds(){
	
		if(getWidth()>= DisplaySystem.getDisplaySystem().getWidth()*0.9)
		{
			setWidth((int)( DisplaySystem.getDisplaySystem().getWidth()*0.9));
		}
		
		if(getHeight()>= DisplaySystem.getDisplaySystem().getHeight()*0.9)
		{
			setHeight((int)( DisplaySystem.getDisplaySystem().getHeight()*0.9));
		}
		
		
		if(getY() + getHeight()<(super.titleBar.isVisible()? super.titleBar.getHeight():20))
			setY(- getHeight()+  (super.titleBar.isVisible()? super.titleBar.getHeight():20));
		
		if(getY()+ getHeight() >DisplaySystem.getDisplaySystem().getHeight() )
			setY(DisplaySystem.getDisplaySystem().getHeight() - getHeight());
		
		if(getX() + getWidth()<20)
		{
			setX(-getWidth() + 20);
		}
	
		if(getX() + 20>DisplaySystem.getDisplaySystem().getWidth())
			setX(DisplaySystem.getDisplaySystem().getWidth() - 20);
		
	}
	
	public EmbeddedWindow(boolean closeBtn, boolean maximizeBtn, boolean minimizeBtn, boolean autoClose) {
		this(closeBtn, maximizeBtn, minimizeBtn, autoClose,true);		
	}

	public EmbeddedWindow(boolean closeBtn, boolean maximizeBtn, boolean minimizeBtn) {
		this(closeBtn, maximizeBtn, minimizeBtn,true);
	
	}


	
	public boolean isWindowed(){
		return windowed;
	}

	
}
