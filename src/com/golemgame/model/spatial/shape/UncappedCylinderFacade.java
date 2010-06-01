package com.golemgame.model.spatial.shape;

import com.golemgame.model.quality.spatial.FlooredDistanceSwitchModel;
import com.golemgame.model.quality.spatial.SwitchModelQualityDelegate;
import com.golemgame.model.spatial.SpatialModelImpl;
import com.golemgame.states.StateManager;
import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.VBOInfo;
import com.jme.scene.lod.DiscreteLodNode;
import com.jme.scene.shape.Cylinder;

public class UncappedCylinderFacade extends SpatialModelImpl{
	private static final long serialVersionUID = 1L;
	public static final float INIT_SIZE = 1f;
	protected static Cylinder baseFacade=null;
	protected static Cylinder baseFacade1=null;
	protected static Cylinder baseFacade2=null;
	protected static Cylinder baseFacade3=null;
	protected static Cylinder baseFacade4=null;
	protected static Cylinder baseFacade5=null;



	
	public UncappedCylinderFacade() {
		super();
	}




	public UncappedCylinderFacade(boolean registerSpatial) {
		super(registerSpatial);
	}




	protected Spatial buildSpatial() {
	
		
		if (baseFacade == null)
		{//create the static sphere mesh
			baseFacade = new Cylinder("built",  CylinderFacade.DETAIL_5_AXIS, CylinderFacade.DETAIL_5_RADIAL, INIT_SIZE/2,INIT_SIZE,false);
			baseFacade2  = new Cylinder("built", CylinderFacade.DETAIL_4_AXIS, CylinderFacade.DETAIL_4_RADIAL, INIT_SIZE/2,INIT_SIZE,false);
			baseFacade3  =  new Cylinder("built", CylinderFacade.DETAIL_3_AXIS, CylinderFacade.DETAIL_3_RADIAL, INIT_SIZE/2,INIT_SIZE,false);
			baseFacade4 = new Cylinder("built",CylinderFacade.DETAIL_2_AXIS, CylinderFacade.DETAIL_2_RADIAL, INIT_SIZE/2,INIT_SIZE,false);
			baseFacade5 =  new Cylinder("built", CylinderFacade.DETAIL_1_AXIS, CylinderFacade.DETAIL_1_RADIAL, INIT_SIZE/2,INIT_SIZE,false);
			
			if(StateManager.useVBO){
				baseFacade.setVBOInfo(new VBOInfo(true));
				baseFacade2.setVBOInfo(new VBOInfo(true));
				baseFacade3.setVBOInfo(new VBOInfo(true));		
				//baseFacade4.setVBOInfo(new VBOInfo(true));
				//baseFacade5.setVBOInfo(new VBOInfo(true));
			}

			
		}
		//ideally, the LOD node should take into account its scale...
		//so when you are far away from a big sphere it still looks nice.
		
		//note, this lod node is completely thread safe - no changes to the scene graph 
		//are made when it switches which node it is drawing.
		SharedMesh s1 = new SharedMesh("Cyl",baseFacade);
	    //s1.setVBOInfo(new VBOInfo(true));
	
	    s1.setModelBound(new BoundingBox());
	    s1.updateModelBound();
	    
	    SharedMesh s2 =new SharedMesh("Cyl",baseFacade2);
	   // s2.setVBOInfo(new VBOInfo(true));
		
	    s2.setModelBound(new BoundingBox());
	    s2.updateModelBound();

	    SharedMesh s3 = new SharedMesh("Cyl",baseFacade3);
	    //s3.setVBOInfo(new VBOInfo(true));
		
	    s3.setModelBound(new BoundingBox());
	    s3.updateModelBound();

	    SharedMesh s4 =new SharedMesh("Cyl",baseFacade4);
	  //  s4.setVBOInfo(new VBOInfo(true));
		
	    s4.setModelBound(new BoundingBox());
	    s4.updateModelBound();

	    SharedMesh s5 =new SharedMesh("Cyl",baseFacade5);
	  //  s5.setVBOInfo(new VBOInfo(true));
		
	    s5.setModelBound(new BoundingBox());
	    s5.updateModelBound();
	    
	    
	    
	    FlooredDistanceSwitchModel m = new FlooredDistanceSwitchModel(5);
	    m.setModelDistance(0, CylinderFacade.DISTANCE[0], CylinderFacade.DISTANCE[1]);
	    m.setModelDistance(1, CylinderFacade.DISTANCE[1], CylinderFacade.DISTANCE[2]);
	    m.setModelDistance(2, CylinderFacade.DISTANCE[2], CylinderFacade.DISTANCE[3]);
	    m.setModelDistance(3,  CylinderFacade.DISTANCE[3], CylinderFacade.DISTANCE[4]);
	    m.setModelDistance(4,  CylinderFacade.DISTANCE[4], CylinderFacade.DISTANCE[5]);
	   
	    StateManager.getQualityManager().addQualtiyDelegate(new SwitchModelQualityDelegate(m));
		 
	 
	    DiscreteLodNode dlod = new DiscreteLodNode("DLOD", m);
	    dlod.attachChild(s1);
	    dlod.attachChild(s2);
	    dlod.attachChild(s3);
	    dlod.attachChild(s4);
	    dlod.attachChild(s5);
	    dlod.setActiveChild(2);
	  
	    
	    dlod.setIsCollidable(false);
	    dlod.setModelBound(new BoundingBox());
	    dlod.updateModelBound();
	    dlod.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.HALF_PI, new Vector3f(1,0,0)));		
	    dlod.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, new Vector3f(0,1,0)));

	    
		return dlod;
	}


}
