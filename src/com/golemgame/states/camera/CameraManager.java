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
package com.golemgame.states.camera;


import java.util.WeakHashMap;

import com.golemgame.mvc.Reference;
import com.golemgame.settings.ActionSettingsListener;
import com.golemgame.settings.SettingChangedEvent;
import com.golemgame.states.GeneralSettings;
import com.golemgame.states.StateManager;
import com.golemgame.states.camera.skybox.SkyBoxManager;
import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.AbstractCamera;
import com.jme.renderer.Camera;
import com.jme.scene.Node;
import com.jme.system.DisplaySystem;


/**
 * The camera manager controls the current camera(s).
 * All access to these cameras should be mediated by this manager. 
 * It will provide standard controls for manipulating the cameras.
 * 
 * It provides methods to set the current camera delegate; for convenience it also
 * maintains an ordered list of delegates (and a default delegate) and can cycle through that
 * list in either direction.
 * 
 * Finally, Camera manager is in charge of the skybox, if it is used.
 * That is, the camera manager maintains a node (within the camera node)
 * whose location is tied to the cameras absolute position. 
 * @author Sam
 *
 */
public class CameraManager {

	/*
	 * Encapsulate cameras in a class that also contains:
	 * their standard position/zoom/etc
	 * and retrieves their rotation/position (allowing them to ignore calls to those by passing dummy values)
	 * and controls zoom and the like.
	 * 
	 * Change this - instead, the ordered list is delegated to a pluggable module
	 * that depends on the given state instead.
	 * 
	 * Then the camera manager just interfaces between this camera delegate class, and the tools,
	 * and ensures that only one is used at any time, etc.
	 * 
	 * Camera classes also provide init/disengage mehods, and update.
	 * 
	 */
	
	private CameraDelegate cameraDelegate;
	

	private OrderedCameraList orderedList;
	
	private Camera camera;
	
	private Node cameraNode;
	
	private Node internalNode;
	
	private Node centroid;
	
	private SkyBoxManager skybox ;
	
	private CentroidDisplay centroidDisplay;
	
	private CameraDelegate defaultDelegate;

	public WeakHashMap<Reference,CameraDelegate> cameraReferenceMap = new WeakHashMap<Reference,CameraDelegate>();
	
	public CameraDelegate getDefaultDelegate()
	{
		return this.defaultDelegate;
	}
	
	
	
	public void setDefaultDelegate(CameraDelegate defaultDelegate) {
		this.defaultDelegate = defaultDelegate;
	}
	
	public CameraManager() {
		super();
		cameraNode = new Node();
		camera = DisplaySystem.getDisplaySystem().getRenderer().getCamera();	
	
		camera.update();
		internalNode = new Node();
		
		centroidDisplay = new NullCentroidDisplay ();
		centroid = centroidDisplay.getCentroidNode();
	
		cameraNode.attachChild(centroid);
		
		/*   Sphere sphere = new Sphere("",32,32,1);
		     sphere.setCullMode(SceneElement.CULL_NEVER);
		 
		     internalNode.attachChild(sphere);
		       sphere.updateRenderState();
		 */
		
		cameraNode.attachChild(internalNode);
		internalNode.setLocalTranslation(camera.getLocation());//tie the internal node to the camera node.
        cameraDelegate = new StandardCamera(cameraNode);
        this.defaultDelegate = cameraDelegate;
        this.setCameraDelegate(cameraDelegate);
    
        this.orderedList = new OrderedCameraList();
        this.orderedList.setDefaultDelegate(cameraDelegate);
        
        internalNode.unlock();
        cameraNode.unlock();
       
       
        
        GeneralSettings.getInstance().getCameraDefault().addSettingsListener(new ActionSettingsListener()
        {

			
			public void valueChanged(SettingChangedEvent<Object> e) {
				
				setCameraDelegate(orderedList.getDefaultDelegate());
				
			}
        	
        });
        
        
        GeneralSettings.getInstance().getCameraForward().addSettingsListener(new ActionSettingsListener()
        {

			
			public void valueChanged(SettingChangedEvent<Object> e) {
				CameraDelegate next =  getOrderedCameraList().getNextCamera();
				if(next == getCameraDelegate())//this makes the next button behave as expected.
					 next =  getOrderedCameraList().getNextCamera();
				setCameraDelegate(next);
				
			}
        	
        });
        
        
        
        GeneralSettings.getInstance().getCameraBackward().addSettingsListener(new ActionSettingsListener()
        {

			
			public void valueChanged(SettingChangedEvent<Object> e) {				
				CameraDelegate previous =  getOrderedCameraList().getPreviousCamera();
				if(previous == getCameraDelegate())//this makes the next button behave as expected.
					previous =  getOrderedCameraList().getNextCamera();
				setCameraDelegate(previous);
				
			}
        	
        });
        
        this.skybox = new SkyBoxManager(this);
        skybox.setEnabled(true);
	}

	

	
	/**
	 * Note: Not all camera views respect the root node parameter.
	 * @param rootNode
	 */
	public void setRootNode(Node rootNode)
	{
		StateManager.getGame().lock();
		try{
			cameraNode.removeFromParent();
			if(rootNode != null)
				rootNode.attachChild(cameraNode);
		}finally
		{
			StateManager.getGame().unlock();
		}
	}
	
	public Vector3f getCameraNormal()
	{
		Vector3f normal = new Vector3f(Vector3f.UNIT_Z);
		this.getCameraRotation().multLocal(normal);
		return normal;
	}
	
	public Vector3f getCameraPosition()
	{
		return cameraDelegate.getCameraTranslation();
	}
	
	public Quaternion getCameraRotation()
	{
		return cameraDelegate.getCameraRotation();
	}
	
	public void update()
	{
		cameraDelegate.updateCamera();
		internalNode.setLocalTranslation(camera.getLocation());//tie the internal node to the camera node.
		internalNode.updateGeometricState(0, true);
		Vector3f c = cameraDelegate.getCentroid();
		if(c!=null)
		{
			centroid.getLocalTranslation().set(c);
		//	centroid.updateWorldVectors();
			centroid.updateGeometricState(0, true);
			centroid.updateRenderState();
			centroidDisplay.update();
		}
	//	internalNode.refreshBranch();
	//	internalNode.updateGeometricState(0, true);
	}
	
	/**
	 * Shows the centroid of the camera.
	 */
	public void showCentroid()
	{
		centroidDisplay.display();
	}



	public Node getCameraNode() {
		return cameraNode;
	}



	public CameraDelegate getCameraDelegate() {
		return cameraDelegate;
	}



	/**
	 * Set the current camera view point
	 * @param delegate
	 */
	public void setCameraDelegate(CameraDelegate delegate)
	{
		if (delegate == default_stand_in_camera)
			delegate = this.defaultDelegate;
		
		this.cameraDelegate.disengageCamera();
		this.cameraDelegate = delegate;
		this.cameraDelegate.engageCamera(camera);
		this.update();
	}
	
	public void zoomCamera(float f)
	{
		cameraDelegate.setZoom(cameraDelegate.getZoom() + f);
		
	}
	
	public float getCameraZoom() {
		return cameraDelegate.getZoom();
	}
	
	public void focusOn(Vector3f focusOn)
	{
		cameraDelegate.getCameraTranslation().set(focusOn);
		cameraDelegate.getCameraRotation().set(cameraDelegate.getStandardRotation());
		cameraDelegate.setZoom(15);
	}
	
	/**
	 * Set the camera to its default orientation/zoom/etc.
	 */
	public void resetCamera()
	{
		cameraDelegate.setZoom(cameraDelegate.getStandardZoom());
		cameraDelegate.getCameraTranslation().set(cameraDelegate.getStandardTranslation());
		cameraDelegate.getCameraRotation().set(cameraDelegate.getCameraRotation());
	}
	
	/**
	 * Return a node that always has its origin at the camera's absolute
	 * location (mainly for sky boxes).
	 * @return
	 */
	public Node getCameraLocationNode()
	{
		return internalNode;
	}
		
	public OrderedCameraList getOrderedCameraList()
	{
		return this.orderedList;
	}
	
	public SkyBoxManager getSkyBoxManager()
	{
		return skybox;
	}
	
	public void setOrderedCameraList(OrderedCameraList orderedList)
	{
		boolean foundSimilar = false;
		this.orderedList = orderedList;
		for (CameraDelegate camera:orderedList.getOrderedDelegates())
		{
			if (camera.isSimilar(this.cameraDelegate))
			{
				this.setCameraDelegate(camera);
				foundSimilar = true;
				break;
			}
		}
		if (!foundSimilar)
			this.setCameraDelegate(this.getDefaultDelegate());
	}

	/*public Camera getCamera()
	{
		return camera;
	}
*/
	public static Camera getDummyCamera() {
		return dummyCamera;
	}
	
	private static Camera dummyCamera = new AbstractCamera()
	{

		private static final long serialVersionUID = 1L;

		
		public int getHeight() {
			// TODO Auto-generated method stub
			return 0;
		}

		
		public Matrix4f getModelViewMatrix() {
		
			return dummyMatrix;
		}

		private final Matrix4f dummyMatrix = new Matrix4f();
			
		
		public Matrix4f getProjectionMatrix() {
			return dummyMatrix;
		}

		
		public int getWidth() {
			// TODO Auto-generated method stub
			return 0;
		}

		
		public void apply() {
			// TODO Auto-generated method stub
			
		}

		
		public void onViewPortChange() {
			// TODO Auto-generated method stub
			
		}

		
		public void resize(int width, int height) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	public static final CameraDelegate default_stand_in_camera = new CameraDelegate()
	{

		private static final long serialVersionUID = 1L;


		public void disengageCamera() {
			// TODO Auto-generated method stub
			
		}

		
		public void engageCamera(Camera toEngage) {
			// TODO Auto-generated method stub
			
		}

		
		public Quaternion getCameraRotation() {
			// TODO Auto-generated method stub
			return null;
		}

		
		public Vector3f getCameraTranslation() {
			// TODO Auto-generated method stub
			return null;
		}

		
		public Quaternion getStandardRotation() {
			// TODO Auto-generated method stub
			return null;
		}

		
		public Vector3f getStandardTranslation() {
			// TODO Auto-generated method stub
			return null;
		}

		
		public float getStandardZoom() {
			// TODO Auto-generated method stub
			return 0;
		}

		
		public float getZoom() {
			// TODO Auto-generated method stub
			return 0;
		}

		
		public void setZoom(float zoom) {
			// TODO Auto-generated method stub
			
		}

		
		public void updateCamera() {
			// TODO Auto-generated method stub
			
		}

		
		public boolean isSimilar(CameraDelegate compareTo) {
			// TODO Auto-generated method stub
			return false;
		}

		
		public void set(CameraDelegate delegate) {
			// TODO Auto-generated method stub
			
		}


		public Vector3f getCentroid() {
			// TODO Auto-generated method stub
			return null;
		}
		
	};

	/**
	 * Distance from a point in world coordinates to the active camera
	 * @param worldTranslation
	 * @return
	 */
	public float distanceTo(Vector3f worldTranslation) {
		return this.camera.getLocation().distance(worldTranslation);
	}



}
