package com.golemgame.states.camera;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.golemgame.model.spatial.NodeModel;
import com.golemgame.model.spatial.OrientingNodeModel;
import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.model.spatial.SpatialModelImpl;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.CameraNode;
import com.jme.scene.Node;
import com.jme.scene.Spatial;

/**
 * A camera delegate that follows the given model around, from its point of view.
 * That model is made invisible to this camera.
 * The camera can swivel in place, and zoom in or out.
 * @author Sam
 *
 */
public class EmbeddedCamera implements CameraDelegate, Serializable {


	private static final long serialVersionUID = 1L;
	
	private  Quaternion standardRotation = new Quaternion();
	private  Vector3f standardPosition = new Vector3f();

	
	private  CameraModel cameraModel;
	private OrientingNodeModel orientingModel;
	
	private static final float standardZoom = 0;
	
	
	
	
	private transient List<CameraDelegate> similarCameras = new ArrayList<CameraDelegate>();
	
	public SpatialModel getInternalCameraModel() {
		return cameraModel;
	}
	
	public NodeModel getCameraModel() {
		return orientingModel;
	}

	public EmbeddedCamera() {
		super();
		
		
		orientingModel = new OrientingNodeModel();
		  cameraModel = new CameraModel();
		orientingModel.addChild(cameraModel);
	   
	     similarCameras.add(this);
	   
		
	}
	
	public void addSimilarCamera(CameraDelegate camera)
	{
		this.similarCameras.add(camera);
	}
	
	public void clearSimilarCameras()
	{
		this.similarCameras.clear();
	}
	

	
	public boolean isSimilar(CameraDelegate compareTo) {
		if (compareTo.equals(this))
			return true;
		
		for (CameraDelegate similar:similarCameras)
			if (similar.equals(compareTo))
				return true;
		
		return false;
	}

	
	public void disengageCamera() {
		this.cameraModel.removeCamera();

	}

	
	public void engageCamera(Camera toEngage) {
		this.cameraModel.setCamera(toEngage);

	}

	
	public Quaternion getCameraRotation() {
		return this.cameraModel.getLocalRotation();
	}
	
	private  Vector3f cameraPosition = new Vector3f();

	
	public Vector3f getCameraTranslation() {

		return cameraPosition;
	}

	
	public Quaternion getStandardRotation() {
		return this.standardRotation;
	}

	
	public Vector3f getStandardTranslation() {
		return this.standardPosition;
	}

	
	public float getStandardZoom() {
		return this.standardZoom;
	}

	
	public float getZoom() {
		  return cameraModel.getCameraNodeTranslation().getZ();
	}

	
	public void setZoom(float zoom) {
		  cameraModel.getCameraNodeTranslation().setZ(zoom);
	}

	public void setLockAll(boolean lock)
	{
		this.orientingModel.setOrientationLocked(lock);
	}
	
	public void lockRollPitchYaw(boolean roll, boolean pitch, boolean yaw)
	{
		this.orientingModel.lockRollPitchYaw(roll, pitch, yaw);
	}

	public void updateCamera() {
	//	this.lockRollPitchYaw(true, true, true);
	//	this.cameraModel.updateCameraNode();
	//	cameraModel.cameraNode.s
		//cameraModel.cameraNode.updateWorldVectors();
	//	Vector3f translation = cameraModel.cameraNode.getWorldTranslation();

		//Vector3f allowed = new Vector3f();
		//allowed.set(0,1,1);
		//Matrix3f rMat = rotation.toRotationMatrix();
		
	//	rMat.multLocal(allowed);
	//	rotation.fromRotationMatrix(rMat);
/*		
		Vector3f up = new Vector3f(Vector3f.UNIT_Y);
		rotation.multLocal(up);
		up.multLocal(serialVersionUID)
		*/
	//	cameraModel.cameraNode.getCamera().setFrame(translation, rotation);
	}
	
	/**
	 * All references to CameraNode are hidden in this class
	 * @author Sam
	 *
	 */
	public static class CameraModel extends SpatialModelImpl
	{

		private static final long serialVersionUID = 1L;

		private CameraNode cameraNode;//this is restored when build spatial is called.
	//	private OrientingNode mainpivotNode;
		
		public CameraModel() {
			super(false);
			
		}

		
		protected Spatial buildSpatial() {
			//mainpivotNode = new OrientingNode();
			Node pivotNode = new Node();
		//	mainpivotNode.attachChild(pivotNode);
			cameraNode = new CameraNode();
			cameraNode.setCamera(CameraManager.getDummyCamera());
		    Quaternion oRot = new Quaternion();
			oRot.fromAngleAxis(FastMath.PI, Vector3f.UNIT_Y);
			cameraNode.setLocalRotation(oRot);//this probably wont be changing in the future

			pivotNode.attachChild(cameraNode);
			pivotNode.getLocalRotation().fromAngleNormalAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y);
			return pivotNode;
		}
		
	
		
		public Vector3f getCameraNodeTranslation()
		{
			return cameraNode.getLocalTranslation();
		}
		
		public Quaternion getCameraNodeRotation()
		{
			return cameraNode.getLocalRotation();
		}
		
		public void setCamera(Camera camera)
		{
			if(camera==null)
				cameraNode.setCamera(CameraManager.getDummyCamera());
			else
				cameraNode.setCamera(camera);
		}
		
		public void removeCamera()
		{
			cameraNode.setCamera(CameraManager.getDummyCamera());
		}
		
		public void updateCameraNode()
		{
			//cameraNode.updateWorldVectors();
			//cameraNode.updateWorldData(0);
			cameraNode.getCamera().update();
			cameraNode.refreshBranch();
	//		cameraNode.updateGeometricState(0, true);
		}

		
		
	}
	
	
	public void set(CameraDelegate delegate) {
		this.setZoom(delegate.getZoom());
		this.getCameraRotation().set(delegate.getCameraRotation());
		this.getCameraTranslation().set(delegate.getCameraTranslation());
		
	}


	public Vector3f getCentroid() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
