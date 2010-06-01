package com.golemgame.menu.color;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.fenggui.Display;
import org.fenggui.FengGUI;
import org.fenggui.composite.Window;
import org.fenggui.event.IWindowClosedListener;
import org.fenggui.event.WindowClosedEvent;

import com.golemgame.menu.color.ColorDialog.ColorDialogListener;
import com.golemgame.properties.fengGUI.CloseListener;
import com.golemgame.properties.fengGUI.IFengGUIDisplayable;
import com.golemgame.states.StateManager;
import com.jme.system.DisplaySystem;

public class ColorWindow implements IFengGUIDisplayable {

	private static final ColorWindow instance = new ColorWindow();

	private Window window;
	private ColorDialog colorDialog;
	private ArrayList<ColorDialogListener> listeners = new ArrayList<ColorDialogListener>();
	private boolean dialogInitiated = false;
	public ColorWindow() {
		super();
		try{
			StateManager.getGame().executeInGL(new Callable<Object>()
					{

						
						public Object call() throws Exception {
							final int width = DisplaySystem.getDisplaySystem().getWidth();
							final int height = DisplaySystem.getDisplaySystem().getHeight();
							
							window = FengGUI.createWindow(true, false, false, true);
					        window.setTitle("Color Chooser");
					        window.setSize(width/2, height/3);
		/*			        window.getAppearance().removeAll();
							window.getAppearance().add(new PlainBackground(Color.TRANSPARENT));
							window.getContentContainer().getAppearance().removeAll();
							window.getContentContainer().getAppearance().add(new PlainBackground(Color.TRANSPARENT));*/
					        colorDialog = new ColorDialog();
					        colorDialog.addColorListener(new ColorDialogListener()
							{
								
								public void cancel() {
									for (ColorDialogListener listener:listeners.toArray(new ColorDialogListener[listeners.size()]))
									{
										listener.cancel();
									}
									dialogInitiated = true;
										close();	
								
								}

								
								public void colorChosen(int red, int green,
										int blue,int alpha) {
									for (ColorDialogListener listener:listeners.toArray(new ColorDialogListener[listeners.size()]))
									{
										listener.colorChosen(red, green, blue, alpha);
									}
									dialogInitiated = true;
									close();	
							
								}
								
							
							});
					        
	
					        window.getContentContainer().addWidget(colorDialog);

					        window.getContentContainer().layout();
					  
					        window.addWindowClosedListener(new IWindowClosedListener()
					        {

								
								public void windowClosed(
										WindowClosedEvent windowClosedEvent) {
									close();
								}
					        	
					        });

							return null;
						}
				
					});
			}catch(Exception e)
			{
				e.printStackTrace();
			}

        
	}

	
	public void display( Display display) {
		this.display(display, 0);
	}
	


	
	public void display(final Display display,final int priority) {
		try{
			StateManager.getGame().executeInGL(new Callable<Object>()
					{

						
						public Object call() throws Exception {					
							
							dialogInitiated = false;
							display.addWidget(window,priority);
							display.bringToFront(window);
							display.setFocusedWidget(window);
							window.layout();
							return null;
						}
				
					});
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		
	}

	
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public String getTitle() {
		return "Colors";
	}

	
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		
	}

	public void setInitialColor(int red, int green, int blue, int alpha)
	{
		ColorData color = new ColorData();
		color.setR(red);
		color.setG(green);
		color.setB(blue);
		color.setAlpha(((double)alpha )/255.0);
		color.copyRGBtoHSL();
		if (color.getLightness() > 0.9 || color.getLightness() < 0.1)
		{
			color.setLightness(0.5);
			color.copyHSLtoRGB();
		}
		
		colorDialog.setColor(color);
	}
	
	public void setInitialColor(float red, float green, float blue, float alpha)
	{
		ColorData color = new ColorData();
		
		int r = (int)Math.round((red * 255.0));
		if (r >255)
			r = 255;
		if (r <0)
			r = 0;
		
		int g = (int)Math.round((green * 255.0));
		if (g >255)
			g = 255;
		if (g <0)
			g = 0;
		
		
		int b = (int)Math.round((blue * 255.0));
		if (b >255)
			b = 255;
		if (b <0)
			b = 0;
		
		
		color.setR(r);
		color.setG(g);
		color.setB(b);
		color.setAlpha(alpha);
		color.copyRGBtoHSL();
		if (color.getLightness() > 0.9 || color.getLightness() < 0.1)
		{
			color.setLightness(0.5);
			color.copyHSLtoRGB();
		}
		
		colorDialog.setColor(color);
	}

	public static ColorWindow getInstance() {
		return instance;
	}
	

	
	public void close() {
		try{
			StateManager.getGame().executeInGL(new Callable<Object>()
					{

						
						public Object call() throws Exception {					
							
							if (window.getParent() != null)
								window.close();
							return null;
						}
				
					});
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			
			if (!dialogInitiated)
			{
				for (ColorDialogListener listener:this.listeners.toArray(new ColorDialogListener[listeners.size()]))
				{
					listener.cancel();
				}
			}
			listeners.clear();
			if (closeListeners != null && ! closeListeners.isEmpty())
			{
				CloseListener[] listenerArray = closeListeners.toArray(new CloseListener[closeListeners.size()]);
				closeListeners.clear();
				for (CloseListener listener:listenerArray)
					listener.close(this);
				
			}
			this.owner = null;
	}

	private ArrayList<CloseListener> closeListeners = null;
	
	public void addCloseListener(CloseListener listener) {
		if (closeListeners == null)
		{
			closeListeners = new ArrayList<CloseListener>();
		}
		closeListeners.add(listener);
		
	}


	
	public void removeCloseListener(CloseListener listener) {
		if (closeListeners != null)
		{
				closeListeners.remove(listener);
		}
	}

	private Object owner = null;

	
	public Object getOwner() {
		return this.owner;
	}


	
	public Object setOwner(Object owner) {
		Object old = this.owner;
		this.owner = owner;
		return old;
	}



	public ColorDialog getColorDialog() {
		return colorDialog;
	}
	public void addColorListener(ColorDialogListener listener)
	{
		listeners.add(listener);
	}
	
	public void removeColorListener(ColorDialogListener listener)
	{
		listeners.remove(listener);
	}
	

}
