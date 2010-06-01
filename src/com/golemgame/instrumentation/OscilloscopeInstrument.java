package com.golemgame.instrumentation;


import java.awt.geom.Point2D;

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.composite.Window;
import org.fenggui.event.IWindowChangedListener;
import org.fenggui.event.mouse.MouseAdapter;
import org.fenggui.event.mouse.MouseDoubleClickedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;

import com.golemgame.instrumentation.fenggui.EmbeddedWindow;
import com.golemgame.instrumentation.fenggui.Graph;
import com.golemgame.instrumentation.fenggui.Graph.GraphStyle;
import com.jme.scene.Node;

public class OscilloscopeInstrument implements Instrument{
	private EmbeddedWindow window;
	private Graph graph;
	private boolean visible = false;
	private String name = "Oscilloscope";
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
	}

	public boolean isLocked() {
		return locked;
	}
	public OscilloscopeInstrument() {
		super();
		window = new EmbeddedWindow(true, false, false, true);
		FengGUI.getTheme().setUp(window);
		
		window.setTitle("Oscilloscope");
		window.setSize(300, 200);
		graph = new Graph();
	//	window.addWidget(graph);
		graph.setXAxisIsRelative(true);

		graph.setMinSize(0, 50);
		graph.setStyle(GraphStyle.LINE);
		graph.setMinX(0);
		graph.setMaxX(3f);
		graph.setMinY(-1f);
		graph.setMaxY(1f);
		//graph.setSize(290, 150);
		//window.addWidget(FengGUI.createButton("Test"));
		Container graphContainer = new Container();
		graphContainer.setLayoutManager(new BorderLayout());
		graph.setLayoutData(BorderLayoutData.CENTER);
		graphContainer.addWidget(graph);
		window.getContentContainer().addWidget(graphContainer);
		graphContainer.setLayoutData(BorderLayoutData.CENTER);
		
		graph.addMouseListener(new MouseAdapter(){

			@Override
			public void mouseDoubleClicked(MouseDoubleClickedEvent mouseDoubleClickedEvent) {
				if(!locked)
					window.setWindowed(!window.isWindowed());
			}
			
		});
		window.addWindowChangedListener(new IWindowChangedListener(){

			public void userChanged() {
				userPositioned = true;
			}
			
		});
	}
	public void setName(String name)
	{
		this.name = name;
		if(name.toUpperCase().contains("Oscilloscope".toUpperCase()))
		{
			window.setTitle(name);
		}else
			window.setTitle(name + " (Oscilloscope)");
	}

	public void attachSpatial(Node attachTo) {
		
	}

	public void attach(InstrumentationLayer instrumentLayer) {
	
	}
	
	public void updateSignal(float signal, float time) {
		graph.addPoint(new Point2D.Float(time,signal));
	}

	public Window getInstrumentInterface() {
		window.setVisible(false);
		return window;
	}

	public void update(float tpf) {
		
	} 
	
	public boolean isWindowed() {
		return window.isWindowed();
	}
	public void setVisible(boolean visible) {
		if(this.visible!=visible)
		{
			this.visible = visible;
			window.setVisible(visible);
		}
	
	}
	public String getName() {
		return name;
	}
	public void setWindowed(boolean windowed) {
		window.setWindowed(windowed);
	}
	
}
