package com.golemgame.states;

import java.util.HashMap;
import java.util.Map;

import com.golemgame.functional.component.BComponent;
import com.golemgame.functional.component.BMind;
import com.golemgame.functional.component.BSpace;
import com.golemgame.mvc.Reference;
import com.golemgame.physical.ode.OdePhysicalStructure;
import com.golemgame.physical.ode.OdePhysicsMachineSpace;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.golemgame.states.physics.IllegalTransitionException;
import com.golemgame.states.physics.PhysicsController;
import com.golemgame.util.loading.ProgressObserver;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsUpdateCallback;

public class MVCFunctionalPhysicsController extends PhysicsController {

	private final FunctionalPhysicsState functionalPhysicsState;
	
	
	
	public MVCFunctionalPhysicsController(	FunctionalPhysicsState physicsState) {
		super(physicsState);
		functionalPhysicsState = physicsState;
	}

	@Override
	public synchronized void preparePhysics()throws IllegalTransitionException {		
		lockState();
		try{			
			super.preparePhysics();
			functionalPhysicsState.getBSpace().clear();
			
			final BSpace bSpace = new BSpace();
			functionalPhysicsState.setBSpace(bSpace);
		
			this.functionalPhysicsState.getPhysicsSpace().removeFromUpdateCallbacks(mindCallback);
			this.functionalPhysicsState.getPhysicsSpace().addToUpdateCallbacks(mindCallback);
			
		}finally
		{
			unlockState();
		}
	}

	private final PhysicsUpdateCallback mindCallback = 	new PhysicsUpdateCallback()
	{
		public void beforeStep(PhysicsSpace space, float time) {
		
		}		
		public void afterStep(PhysicsSpace space, float time) {
	
			if (time>1f/30f)
				functionalPhysicsState.getBSpace().update(1f/30f);	
			else if (time <= 0)
				return;
			else
				functionalPhysicsState.getBSpace().update(time);
			functionalPhysicsState.timeout.physicsCompleted();				
		}
	};
	
	public synchronized void load(OdePhysicsMachineSpace machineSpace,Map<OdePhysicalStructure, PhysicsNode> physicalMap,   OdePhysicsEnvironment compilationEnvironment,ProgressObserver observer)throws IllegalTransitionException {
		lockState();
		try{
			BSpace bSpace = functionalPhysicsState.getBSpace();
			
			
			BMind mind = new BMind(bSpace);
			 Map<Reference,BComponent> wireMap = new HashMap<Reference,BComponent>();
		
			 machineSpace.buildMinds(mind, physicalMap,wireMap, compilationEnvironment);
			 
			observer.setProgress(0.8f);
			
			machineSpace.buildConnections(wireMap);

			observer.setProgress(0.9f);
			bSpace.attachMind(mind);
			
			super.load();
		}finally
		{
			unlockState();
		}
	}

	

	@Override
	public synchronized void load() throws IllegalTransitionException {
		throw new IllegalTransitionException("Cannot load a functional physics controller directly.");
	}

	@Override
	public synchronized void start()throws IllegalTransitionException {
		
		super.start();
	}

	@Override
	public synchronized void stop()throws IllegalTransitionException {
	
		super.stop();
	}

	@Override
	public synchronized void unload() throws IllegalTransitionException{
		functionalPhysicsState.getBSpace().clear();
		
		final BSpace bSpace = new BSpace();
		functionalPhysicsState.setBSpace(bSpace);
		super.unload();
		
	
	}

}
