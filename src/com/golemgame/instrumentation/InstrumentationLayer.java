package com.golemgame.instrumentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.fenggui.Display;
import org.fenggui.composite.Window;

import com.golemgame.states.GUILayer;
import com.golemgame.states.StateManager;
import com.golemgame.util.input.InputLayer;
import com.golemgame.util.input.ListenerGroup;
import com.jme.input.KeyInputListener;
import com.jme.input.MouseInputListener;
import com.jme.scene.Node;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jmex.game.state.GameState;

/**
 * The instrumentaiton layer is a primarily fenggui layer, with its input layer below the main state toolbar etc.
 * It presents information about the physics state, and interacts with that state.
 * 
 * It also provides key press listening capabilities.
 * 
 * It may include non-fenggui components.
 */
public class InstrumentationLayer extends GameState {
	private Node rootNode;
	private Collection<ConnectedInstrument> currentInstruments = new HashSet<ConnectedInstrument>();
	private boolean visible = true;
	private Lock instrumentLock = new ReentrantLock();
	private ListenerGroup instrumentListeners = new ListenerGroup();
	
	//private Container windowRoot;
	@Override
	public void cleanup() {
	
	}
	
	/*private ISizeChangedListener sizeChange = new ISizeChangedListener()
	{

		public void sizeChanged(SizeChangedEvent event) {
			windowRoot.setSize(new Dimension(event.getNewSize()));
			windowRoot.setPosition(new Point(0,0));
			
		}
		
	};*/
	
	@Override
	protected void finalize() throws Throwable {
		cleanup();
		super.finalize();
	}

	public InstrumentationLayer() {
		super();
	/*	windowRoot = new Container();
		windowRoot.setLayoutManager(new StaticLayout());
		
		GUILayer.getInstance().getDisplay().addWidget(windowRoot);
		GUILayer.getInstance().getDisplay().addSizeChangedListener(sizeChange);
*/
		rootNode = new Node();
		ZBufferState buf = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.CF_LEQUAL);
        rootNode.setRenderState(buf);
     
	}
	
	public ListenerGroup getListeners()
	{
		return instrumentListeners;
	}

	@Override
	public void setActive(boolean active) {
		instrumentLock.lock();
		try{
			super.setActive(active);
		//windowRoot.setVisible(super.isActive());
		}finally{
			instrumentLock.unlock();
		}
	}

	
	//shouldn't let the instruments go over crucial controls
	

	@Override
	public void render(float tpf) {
		instrumentLock.lock();
		try{
			DisplaySystem.getDisplaySystem().getRenderer().draw(rootNode);
		}finally{
			instrumentLock.unlock();
		}
	}

	@Override
	public void update(float tpf) {
		instrumentLock.lock();
		try{
			for(ConnectedInstrument instr:currentInstruments)
				instr.instrument.update(tpf);
		}finally{
			instrumentLock.unlock();
		}
		//StateManager.getGame().lock();
	//	try{
			instrumentLock.lock();
			try{
				rootNode.updateGeometricState(tpf, true);
			}finally{
				instrumentLock.unlock();
			}
	//	}finally{
	//		StateManager.getGame().unlock();
	//	}
	}

	public void clear()
	{
		instrumentListeners.clear();
		InputLayer.get().removeKeyListener(instrumentListeners);
		InputLayer.get().removeMouseListener(instrumentListeners);
		
		try {
			StateManager.getGame().executeInGL(new Callable<Object>(){

				public Object call() throws Exception {
					for(ConnectedInstrument instr:currentInstruments)
					{
						if(instr.hasWindow())
						{
							if(instr.window.getParent()!=null)
								instr.window.close();
							
						}
					}
					return null;
				}
				
			});
		} catch (Exception e) {
			StateManager.logError(e);
		}
		instrumentLock.lock();
		try{
			currentInstruments.clear();
			rootNode.detachAllChildren();
		}finally{
			instrumentLock.unlock();
		}
	
	}
	
	
	public void setVisible(boolean visible)
	{
		StateManager.getGame().lock();
		try{
	//	if(visible!=this.visible)
		{
			this.visible = visible;
			instrumentLock.lock();
			try{
				for(ConnectedInstrument instr:currentInstruments)
				{
					if(instr.hasWindow())
						instr.window.setVisible(visible);
					
					if(visible)
					{
						if(!rootNode.hasChild(instr.privateNode))
							rootNode.attachChild(instr.privateNode);
					}else
						instr.privateNode.removeFromParent();
				}
			}finally{
				instrumentLock.unlock();
			}
		}
		}finally{
			StateManager.getGame().unlock();
		}
	}
	
	public void addInstrument(final Instrument instrument)
	{
		try {
			StateManager.getGame().executeInGL(new Callable<Object>(){

				public Object call() throws Exception {
					ConnectedInstrument conInst = new ConnectedInstrument(instrument);
					instrumentLock.lock();
					try{
						if(currentInstruments.add(conInst))
						{
							
							rootNode.attachChild(conInst.privateNode);
							if(conInst.hasWindow())
							{
								//windowRoot.addWidget(conInst.window);
								
								//windowRoot.layout();
								Display display = GUILayer.getInstance().getDisplay();
								Window window = conInst.window;
								display.addWidget(window,-1);
								display.bringToFront(window);
								//display.setFocusedWidget(window);
								if (window.getDisplayY() + window.getHeight()> window.getParent().getSize().getHeight())
									window.getPosition().setY(window.getParent().getSize().getHeight() - window.getHeight());
							
								conInst.window.layout();
								display.layout();
							}
							
							instrument.attach(InstrumentationLayer.this);
							
						}
					}finally{
						instrumentLock.unlock();
					}
					return null;
				}

			});
		} catch (Exception e) {
			StateManager.logError(e);
		}
	}
	
	class ConnectedInstrument implements Comparable<ConnectedInstrument>
	{
		Instrument instrument;
		Node privateNode = new Node();
		Window window;

		
		private ConnectedInstrument(Instrument instrument) {
			super();
			this.instrument = instrument;
			privateNode.attachChild(privateNode);
			this.window = instrument.getInstrumentInterface();
			
		
		}

	
		public boolean hasWindow() {
			return window!=null;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((instrument == null) ? 0 : instrument.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ConnectedInstrument other = (ConnectedInstrument) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (instrument == null) {
				if (other.instrument != null)
					return false;
			} else if (!instrument.equals(other.instrument))
				return false;
			return true;
		}
		private InstrumentationLayer getOuterType() {
			return InstrumentationLayer.this;
		}

		public int compareTo(ConnectedInstrument o) {
			return instrument.getName().compareTo( o.instrument.getName());
		}
		
	}

	public boolean hasInstruments() {
		return ! currentInstruments.isEmpty();
	}

	public boolean isVisible() {
		return visible;
	}
	
	public void attachKeyListener(KeyInputListener listener)
	{
		this.instrumentListeners.addKeyListener(listener);
	}
	
	
	public void attachMouseListener(MouseInputListener listener)
	{
		this.instrumentListeners.addMouseListener(listener);
	}
	
	public void layout()
	{
		List<ConnectedInstrument> toLayout = new ArrayList<ConnectedInstrument>();
	
		instrumentLock.lock();
		try{
			for(ConnectedInstrument c: currentInstruments)
			{
				if(c.hasWindow()  &&! c.instrument.isUserPositioned() && c.instrument.isWindowed()  &&!c.instrument.isLocked())
					toLayout.add(c);
			}
		}finally{
			instrumentLock.unlock();
		}
		Collections.sort(toLayout);
		int x = 0;
		int y = 0;
		int curLayerHeight = 0;
		//int topLastLayer = 0;
		for(ConnectedInstrument c: toLayout)
		{
			//start at the bottom, work upwords. If you hit the top, start over again.
			int width = c.window.getWidth();
			int height = c.window.getHeight();
			
			if(width+x>DisplaySystem.getDisplaySystem().getWidth())
			{
				y+= curLayerHeight;
				curLayerHeight = 0;
				x = 0;
				
			}
			
			if(y+height>DisplaySystem.getDisplaySystem().getHeight())
			{
				y = 0;
				curLayerHeight= 0;
				x = 0;
				
			}
			
			{
				c.window.getPosition().setXY(x, y);
				x+= width;
				curLayerHeight = Math.max(curLayerHeight, height);
			}
			
		}
	}

	

}
