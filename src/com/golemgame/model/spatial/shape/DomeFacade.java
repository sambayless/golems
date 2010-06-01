package com.golemgame.model.spatial.shape;

import com.golemgame.model.quality.spatial.FlooredDistanceSwitchModel;
import com.golemgame.model.quality.spatial.SwitchModelQualityDelegate;
import com.golemgame.model.spatial.SpatialModelImpl;
import com.golemgame.states.StateManager;
import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.VBOInfo;
import com.jme.scene.lod.DiscreteLodNode;
import com.jme.scene.shape.Dome;

public class DomeFacade extends SpatialModelImpl{
	private static final long serialVersionUID = 1L;
	public static final float INIT_SIZE = 1f;
	protected static TriMesh baseFacade=null;
	//protected static TriMesh baseFacade1=null;
	protected static TriMesh baseFacade2=null;
	protected static TriMesh baseFacade3=null;
	protected static TriMesh baseFacade4=null;
	protected static TriMesh baseFacade5=null;
	
	protected static TriMesh invertedFacade=null;
	//protected static TriMesh invertedFacade1=null;
	protected static TriMesh invertedFacade2=null;
	protected static TriMesh invertedFacade3=null;
	protected static TriMesh invertedFacade4=null;
	protected static TriMesh invertedFacade5=null;
	

	public DomeFacade() {
		super();
	}

	public DomeFacade(boolean registerSpatial) {
		super(registerSpatial);
	}
	
	public void invertTexture(boolean invert)
	{
		if(invert)
		{
		
				cylinders[0].setTarget(invertedFacade);
				cylinders[1].setTarget(invertedFacade2);
				cylinders[2].setTarget(invertedFacade3);
				cylinders[3].setTarget(invertedFacade4);
				cylinders[4].setTarget(invertedFacade5);
		}else{
			cylinders[0].setTarget(baseFacade);
			cylinders[1].setTarget(baseFacade2);
			cylinders[2].setTarget(baseFacade3);
			cylinders[3].setTarget(baseFacade4);
			cylinders[4].setTarget(baseFacade5);
		}
	}
	private SharedMesh[] cylinders;
	protected Spatial buildSpatial() {
		if (baseFacade == null)
		{//create the static sphere mesh
			baseFacade  = new Dome("facadeCap", new Vector3f(), CylinderFacade.DETAIL_5_AXIS, CylinderFacade.DETAIL_5_RADIAL, INIT_SIZE/2,true);	
			baseFacade2   = new Dome("facadeCap", new Vector3f(),  CylinderFacade.DETAIL_4_AXIS, CylinderFacade.DETAIL_4_RADIAL, INIT_SIZE/2,true);	
			baseFacade3   = new Dome("facadeCap", new Vector3f(), CylinderFacade.DETAIL_3_AXIS, CylinderFacade.DETAIL_3_RADIAL, INIT_SIZE/2,true);	
			baseFacade4  = new Dome("facadeCap", new Vector3f(),  CylinderFacade.DETAIL_2_AXIS, CylinderFacade.DETAIL_2_RADIAL, INIT_SIZE/2,true);	
			baseFacade5  = new Dome("facadeCap", new Vector3f(),  CylinderFacade.DETAIL_1_AXIS, CylinderFacade.DETAIL_1_RADIAL, INIT_SIZE/2,true);	
	
			invertedFacade  = new Dome("facadeCap", new Vector3f(), CylinderFacade.DETAIL_5_AXIS, CylinderFacade.DETAIL_5_RADIAL, INIT_SIZE/2,true,true);	
			invertedFacade2   = new Dome("facadeCap", new Vector3f(),  CylinderFacade.DETAIL_4_AXIS, CylinderFacade.DETAIL_4_RADIAL, INIT_SIZE/2,true,true);	
			invertedFacade3   = new Dome("facadeCap", new Vector3f(), CylinderFacade.DETAIL_3_AXIS, CylinderFacade.DETAIL_3_RADIAL, INIT_SIZE/2,true,true);	
			invertedFacade4  = new Dome("facadeCap", new Vector3f(),  CylinderFacade.DETAIL_2_AXIS, CylinderFacade.DETAIL_2_RADIAL, INIT_SIZE/2,true,true);	
			invertedFacade5  = new Dome("facadeCap", new Vector3f(),  CylinderFacade.DETAIL_1_AXIS, CylinderFacade.DETAIL_1_RADIAL, INIT_SIZE/2,true,true);	
	
			
			if(StateManager.useVBO){
				baseFacade.setVBOInfo(new VBOInfo(true));
				baseFacade2.setVBOInfo(new VBOInfo(true));
				baseFacade3.setVBOInfo(new VBOInfo(true));		
				//baseFacade4.setVBOInfo(new VBOInfo(true));
				//baseFacade5.setVBOInfo(new VBOInfo(true));
				invertedFacade.setVBOInfo(new VBOInfo(true));
				invertedFacade2.setVBOInfo(new VBOInfo(true));
				invertedFacade3.setVBOInfo(new VBOInfo(true));		
			}
		}
		cylinders = new SharedMesh[5];
		//ideally, the LOD node should take into account its scale...
		//so when you are far away from a big sphere it still looks nice.
		
		//note, this lod node is completely thread safe - no changes to the scene graph 
		//are made when it switches which node it is drawing.
		SharedMesh s1 = new SharedMesh("Cyl",baseFacade);
	    cylinders[0] = s1;
	    s1.setModelBound(new BoundingBox());
	    s1.updateModelBound();

	    SharedMesh s2 =new SharedMesh("Cyl",baseFacade2);
	    cylinders[1] = s2;
	    s2.setModelBound(new BoundingBox());
	    s2.updateModelBound();

	    SharedMesh s3 = new SharedMesh("Cyl",baseFacade3);
	    cylinders[2] = s3;
	    s3.setModelBound(new BoundingBox());
	    s3.updateModelBound();

	    SharedMesh s4 =new SharedMesh("Cyl",baseFacade4);
	    cylinders[3] = s4;
	    s4.setModelBound(new BoundingBox());
	    s4.updateModelBound();

	    SharedMesh s5 =new SharedMesh("Cyl",baseFacade5);
	    cylinders[4] = s5;
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
	    dlod.updateGeometricState(0, true);
	    

		return dlod;
	}


}
