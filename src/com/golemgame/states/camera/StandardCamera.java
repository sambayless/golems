package com.golemgame.states.camera;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.CameraNode;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.shape.Sphere;
import com.jme.system.DisplaySystem;

/**
 * NOTE: Not serializable!
 * This creates a camera node that is placed at zoom distance away from the center
 * of another node. Rotating rotates you around that other node. Zooming past 0 will 
 * push the center of that node forward.
 * 
 * @author Sam
 *
 */
public class StandardCamera implements CameraDelegate {

	

	private  Quaternion standardRotation = new Quaternion();
	private Vector3f standardPosition = new Vector3f();
	private CameraNode cameraNode;
	private Node pivotNode;
	
	private final float standardZoom = 50;
	
	public StandardCamera(Node parentNode) {
		super();
	
		
		//parentNode.attachChild(pivotNode);
	    
	     this.cameraNode = new CameraNode();
	     this.pivotNode = new Node();
     
	  
	     pivotNode.attachChild(cameraNode);
	     
	     standardPosition.set(0,0,0);
	  
		
      Quaternion oRot = new Quaternion();
      oRot.fromAngleAxis(FastMath.PI, Vector3f.UNIT_Y);
      cameraNode.setLocalRotation(oRot);//this probably wont be changing in the future
      
      Quaternion camRot = new Quaternion();
       camRot.fromAngleAxis(-FastMath.PI/4f, Vector3f.UNIT_X);
     
      
      getStandardRotation().set( camRot);
      
      setZoom(standardZoom);
      pivotNode.getLocalTranslation().set(standardPosition);
      pivotNode.getLocalRotation().set(standardRotation);
	}
	
	
	public boolean isSimilar(CameraDelegate compareTo) {
		if (compareTo.equals(this))
			return true;

		return false;
	}

	
	public void disengageCamera() {
		// TODO Auto-generated method stub
		cameraNode.setCamera(CameraManager.getDummyCamera());
	}

	
	public void engageCamera(Camera camera) {
		// TODO Auto-generated method stub
		camera.setFrustumPerspective(45.0f, (float) DisplaySystem.getDisplaySystem().getWidth() / (float) DisplaySystem.getDisplaySystem().getHeight(), 1,15000);
		 cameraNode.setCamera(camera);
	}

	
	public Quaternion getCameraRotation() {
		return pivotNode.getLocalRotation();
	}

	
	public Vector3f getCameraTranslation() {
		return pivotNode.getLocalTranslation();
	}

	
	public Quaternion getStandardRotation() {
		return this.standardRotation;
	}

	
	public Vector3f getStandardTranslation() {
		return this.standardPosition;
	}

	
	public float getZoom() {
		return cameraNode.getLocalTranslation().z;
	}

	
	public void setZoom(float zoom) {
		cameraNode.getLocalTranslation().z = zoom;
	}
	
	
	
	public void set(CameraDelegate delegate) {
		this.setZoom(delegate.getZoom());
		this.getCameraRotation().set(delegate.getCameraRotation());
		this.getCameraTranslation().set(delegate.getCameraTranslation());
		
	}

	
	public void updateCamera() {
		pivotNode.updateWorldVectors();
	
		cameraNode.updateWorldVectors();
		cameraNode.updateWorldData(0);
		cameraNode.getCamera().update();
		
	}

	
	public float getStandardZoom() {
		return standardZoom;
	}

	private void writeObject(ObjectOutputStream out) throws IOException
	{
	throw new NotSerializableException("Not Serializable");
	}
	private void readObject(ObjectInputStream in) throws IOException
	{
	 throw new NotSerializableException("Not Serializable");
	}





	public Vector3f getCentroid() {
		return pivotNode.getLocalTranslation();
	}
	 
	
	
}
