package com.golemgame.instrumentation;

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.composite.Window;
import org.fenggui.event.IWindowChangedListener;
import org.fenggui.event.mouse.MouseAdapter;
import org.fenggui.event.mouse.MouseDoubleClickedEvent;
import org.fenggui.layout.BorderLayoutData;

import com.golemgame.instrumentation.fenggui.ColorDisplay;
import com.golemgame.instrumentation.fenggui.EmbeddedWindow;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;

public class ColorInstrument implements Instrument{
	protected EmbeddedWindow window;
	protected Container controlContainer;
	private boolean visible = false;
	private String name = "Color";
	private String typeName = "Color";
	private boolean locked = false;
	private boolean userPositioned = false;
	
	public boolean isUserPositioned() {
		return userPositioned;
	}

	public void setUserPositioned(boolean userPositioned) {
		this.userPositioned = userPositioned;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
		window.setResizable(!locked);
		window.setMovable(!locked);
		window.getCloseButton().setEnabled(!locked);
		window.setPinable(!locked);
	}

	public boolean isLocked() {
		return locked;
	}
	
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public void setWindowed(boolean windowed) {
		window.setWindowed(windowed);
	}

	private ColorDisplay display;
	public ColorInstrument() {
		super();
		window = new EmbeddedWindow(true, false, false, true);
		FengGUI.getTheme().setUp(window);
		window.setTitle("Color");
		window.setSize(50, 200);
		//window.getContentContainer().setLayoutManager(new BorderLayout());
		window.addWindowChangedListener(new IWindowChangedListener(){

			public void userChanged() {
				userPositioned = true;
			}
			
		});
		controlContainer = new Container();
	
		window.getContentContainer().addWidget(controlContainer);
		controlContainer.setLayoutData(BorderLayoutData.CENTER);
		display = new ColorDisplay();
		controlContainer.addWidget(display);
		//window.setVisible(false);
		window.layout();

		display.addMouseListener(new MouseAdapter(){

			@Override
			public void mouseDoubleClicked(MouseDoubleClickedEvent mouseDoubleClickedEvent) {
				if(!locked)
					window.setWindowed(!window.isWindowed());
			}
			
		});
		window.setMinSize(1,1);
//		setWindowed(false);
	}
	
	

	public boolean isWindowed() {
		return window.isWindowed();
	}
	public void setName(String name)
	{
		this.name = name;
		if(name.equalsIgnoreCase("(Unnamed)"))
			window.setTitle(typeName);
		else if(name.toUpperCase().contains(typeName.toUpperCase()))
		{
			window.setTitle(name);
		}else
			window.setTitle(name + " (" + typeName + ")");
	//	window.layout();
	}

	public void attachSpatial(Node attachTo) {
		
	}


	public void attach(InstrumentationLayer instrumentLayer) {
	
	}

	public String getName() {
		return name;
	}
	public Window getInstrumentInterface() {

		return window;
	}

	public void update(float tpf) {
		
	} 
	

	public void setVisible(boolean visible) {
		if(this.visible!=visible)
		{
			this.visible = visible;
			window.setVisible(visible);
		}
	
	}
	
	public void setCurrentValue(float value)
	{
		display.setVal(value);
	}	
	
	public void setPositiveColor(ColorRGBA color)
	{
		display.getColorPos().setColor(color.r, color.g, color.b, color.a);
	}
	public void setNeutralColor(ColorRGBA color)
	{
		display.getColorZero().setColor(color.r, color.g, color.b, color.a);
	}
	public void setNegativeColor(ColorRGBA color)
	{
		display.getColorNeg().setColor(color.r, color.g, color.b, color.a);
	}

}
