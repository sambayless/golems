/*******************************************************************************
 * Copyright 2008, 2009, 2010 Sam Bayless.
 * 
 *     This file is part of Golems.
 * 
 *     Golems is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Golems is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Golems. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.golemgame.physical.ode;

import java.util.Map;

import com.golemgame.constructor.Updatable;
import com.golemgame.constructor.UpdateManager;
import com.golemgame.constructor.UpdateManager.Stream;
import com.golemgame.functional.component.BComponent;
import com.golemgame.functional.component.BGenericInput;
import com.golemgame.functional.component.BGenericSource;
import com.golemgame.functional.component.BMind;
import com.golemgame.model.spatial.ManualUpdateNodeModel;
import com.golemgame.model.spatial.shape.CameraModel;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.Reference;
import com.golemgame.mvc.golems.CameraInterpreter;
import com.golemgame.mvc.golems.WirePortInterpreter;
import com.golemgame.physical.ode.compile.OdePhysicsEnvironment;
import com.golemgame.states.StateManager;
import com.golemgame.states.camera.EmbeddedCamera;
import com.golemgame.structural.collision.CollisionMember;
import com.jme.scene.Node;
import com.jmex.physics.PhysicsNode;

public class OdeCamera extends OdePhysicalStructure{

	private CameraInterpreter interpreter;
	public OdeCamera(PropertyStore store) {
		super(store);
		interpreter = new CameraInterpreter(store);
	}
	
	private ManualUpdateNodeModel updateCameraNode;
	
	private EmbeddedCamera embeddedPhysicsCamera;
	
	@Override
	public void buildCollidable(CollisionMember collidable) {
		CameraModel physicsCollidable = new CameraModel(true);
		
		embeddedPhysicsCamera = new EmbeddedCamera();
		embeddedPhysicsCamera.lockRollPitchYaw(interpreter.isRollLocked(), interpreter.isPitchLocked(), interpreter.isYawLocked());
		embeddedPhysicsCamera.setLockAll(interpreter.isOrientationLocked());
		
	//	embeddedCamera.clearSimilarCameras();//important, to avoid memory leaks
	//	embeddedPhysicsCamera.addSimilarCamera(embeddedCamera);
	//	embeddedCamera.addSimilarCamera(embeddedPhysicsCamera);
	//	embeddedPhysicsCamera.set(embeddedCamera);
		
		
		collidable.getModel().addChild(physicsCollidable);
		collidable.registerCollidingModel(physicsCollidable);
	
	

		physicsCollidable.getLocalTranslation().set(interpreter.getLocalTranslation());
		physicsCollidable.getLocalRotation().set(interpreter.getLocalRotation());
		physicsCollidable.updateWorldData();
		
		physicsCollidable.getListeners().clear();
		physicsCollidable.updateModelData();
		physicsCollidable.updateWorldData();
		
		updateCameraNode = new ManualUpdateNodeModel();
		updateCameraNode.addChild(embeddedPhysicsCamera.getCameraModel());
		
		collidable.getModel().addChild(updateCameraNode);
		embeddedPhysicsCamera.getCameraModel().getLocalTranslation().set(interpreter.getLocalTranslation());
		embeddedPhysicsCamera.getCameraModel().getLocalRotation().set(interpreter.getLocalRotation());
		embeddedPhysicsCamera.getCameraModel().getNode().unlock();
		embeddedPhysicsCamera.getInternalCameraModel().getSpatial().unlock();
		

	}
	
	public boolean isPropagating() {
		return false;
	}
	
	private Updatable cameraUpdate;
	private Updatable mouseUpdate;
	private Updatable updateTwo ;
	@Override
	public void buildRelationships(
			Map<OdePhysicalStructure, PhysicsNode> physicalMap,
			OdePhysicsEnvironment compiledEnvironment) {
		compiledEnvironment.addCameraDelegate(embeddedPhysicsCamera, interpreter.getReference());
		
		cameraUpdate = new Updatable()
		{

			public void update(float time) {
				if(embeddedPhysicsCamera!=null)
				{
					Node parent = embeddedPhysicsCamera.getCameraModel().getSpatial().getParent();
					if(parent!=null)
					{
						parent.unlockTransforms();
						parent.updateWorldVectors();
					}
					embeddedPhysicsCamera.getCameraModel().updateWorldData();
					embeddedPhysicsCamera.getInternalCameraModel().updateWorldData();
					embeddedPhysicsCamera.getCameraModel().getSpatial().updateGeometricState(time, true);
				}
				updateCameraNode.manualUpdate(time);
		/*		if(embeddedPhysicsCamera!=null)
				{
					embeddedPhysicsCamera.getCameraModel().updateWorldData();
					embeddedPhysicsCamera.getInternalCameraModel().updateWorldData();
					embeddedPhysicsCamera.getCameraModel().getSpatial().updateGeometricState(time, true);
				//	System.out.println(embeddedPhysicsCamera.getCameraModel().getSpatial().getWorldTranslation());
				
				}*/
				StateManager.getCameraManager().update();
			
			}
			
		};
		 updateTwo = new 	 Updatable()
		{

			public void update(float time) {
	
			//	updateCameraNode.manualUpdate(time);

				StateManager.getCameraManager().update();
			
			}
			
		};
		UpdateManager.getInstance().add(cameraUpdate ,Stream.PHYISCS_POST_UPDATE);//has to be in the gl render stream AND the physics update stream
		//UpdateManager.getInstance().add(cameraUpdate ,Stream.PHYSICS_RENDER);
		UpdateManager.getInstance().add(updateTwo ,Stream.PHYSICS_RENDER);
	//	UpdateManager.getInstance().add(cameraUpdate ,Stream.GL_UPDATE);
		UpdateManager.getInstance().add(updateTwo ,Stream.GL_RENDER);
		//conflicting requirements... the mouse movments should upate in the gl render stream, but the rest of the updates only in the physics update stream...
		mouseUpdate = new Updatable()
		{

			public void update(float time) {
				//StateManager.getCameraManager().update();
				
				embeddedPhysicsCamera.getCameraModel().updateWorldData();
				embeddedPhysicsCamera.getInternalCameraModel().updateWorldData();
				embeddedPhysicsCamera.getCameraModel().getSpatial().updateGeometricState(time, true);
				StateManager.getCameraManager().update();
			}
			
		};
		UpdateManager.getInstance().add(mouseUpdate ,Stream.GL_RENDER);//has to be in the render stream

	}

	@Override
	public void remove() {
		super.remove();
		UpdateManager.getInstance().remove(cameraUpdate );
		UpdateManager.getInstance().remove(mouseUpdate );
		UpdateManager.getInstance().remove(updateTwo);
	//	embeddedCamera.set(embeddedPhysicsCamera);
	//	embeddedCamera.clearSimilarCameras();
		embeddedPhysicsCamera.clearSimilarCameras();
		embeddedPhysicsCamera.getCameraModel().detachFromParent();//to eliminate references to the machine
		embeddedPhysicsCamera.getCameraModel().getSpatial().removeFromParent();
		embeddedPhysicsCamera = null;
	}

	@Override
	public void buildMind(BMind mind,
			Map<OdePhysicalStructure, PhysicsNode> physicalMap,
			Map<Reference, BComponent> wireMap,
			final OdePhysicsEnvironment environment) {

		BGenericInput triggerCamera = new BGenericInput(){
			@Override
			protected float signalRecieved(float signal,float time) {
				float t = interpreter.getViewThreshold();
				if(signal>= t)
				{
					environment.setCameraDelegate(embeddedPhysicsCamera);
				}
				state = 0;
				return 0;
			}			
		};	
		WirePortInterpreter inPort = new WirePortInterpreter(interpreter.getInput());
		wireMap.put(inPort.getID(), triggerCamera);
		mind.addComponent(triggerCamera);

		super.buildMind(mind, physicalMap, wireMap, environment);
	}

}
