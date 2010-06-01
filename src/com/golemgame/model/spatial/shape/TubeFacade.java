package com.golemgame.model.spatial.shape;

import com.golemgame.model.quality.spatial.FlooredDistanceSwitchModel;
import com.golemgame.model.quality.spatial.SwitchModelQualityDelegate;
import com.golemgame.model.spatial.SpatialModelImpl;
import com.golemgame.states.StateManager;
import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.SceneElement;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.VBOInfo;
import com.jme.scene.lod.DiscreteLodNode;
import com.jme.scene.shape.SemiTube;
import com.jme.scene.shape.Tube;

public class TubeFacade extends SpatialModelImpl {

	private static final long serialVersionUID = 1L;
	public static final float RADIUS = 0.5f;
	public static final float HEIGHT = 1f;
	public static final float INIT_SIZE = 1f;
	protected  SemiTube baseFacade;
	protected  SemiTube baseFacade2;
	protected  SemiTube baseFacade3;
	protected  SemiTube baseFacade4;
	protected  SemiTube baseFacade5;

	private float arc = 1f;
	private float innerRadius;


	public TubeFacade() {
		super();
	
	}


	public TubeFacade(boolean registerSpatial) {
		super(registerSpatial);
		
	}



	
	protected Spatial buildSpatial() {
		
		this.innerRadius = RADIUS/2f;
		this.arc = FastMath.TWO_PI;
		
	//	if(baseFacade == null)
		{//create the static sphere mesh
			baseFacade =  new SemiTube("false",RADIUS,innerRadius,HEIGHT,30,40,arc);//because of the HUGE extra costs for tubes, since they cant be shared as their inner radius change their actual tri models, have to drop their tri count
			
		//	baseFacade =  new Tube("false",RADIUS,RADIUS/2f,HEIGHT,100,100);
			baseFacade2  = baseFacade;
			/*		baseFacade2  =  new Tube("false",RADIUS,RADIUS/2f,HEIGHT,50,50);*/
			baseFacade3  =   new SemiTube("false",RADIUS,innerRadius,HEIGHT,20,20,arc);
		
			baseFacade4 = new SemiTube("false",RADIUS,innerRadius,HEIGHT,15,10,arc);
			baseFacade5 =   new SemiTube("false",RADIUS,innerRadius,HEIGHT,3,3,arc);
			
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
	    Spatial s1 = new SharedMesh("ConeFacade",baseFacade);
	    
	  
	    s1.setModelBound(new BoundingBox());
	    s1.updateModelBound();

	    
	    
	    Spatial s2 =new SharedMesh("ConeFacade",baseFacade2);

	    s2.setModelBound(new BoundingBox());
	    s2.updateModelBound();

	    Spatial s3 = new SharedMesh("ConeFacade",baseFacade3);
	
	    s3.setModelBound(new BoundingBox());
	    s3.updateModelBound();

	    Spatial s4 =new SharedMesh("ConeFacade",baseFacade4);
	
	    s4.setModelBound(new BoundingBox());
	    s4.updateModelBound();

	    Spatial s5 =new SharedMesh("ConeFacade",baseFacade5);
	  
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
	    dlod.updateRenderState();
	
	  //  dlod.setCullMode(SceneElement.CULL_ALWAYS);
//
	    Quaternion rotation = new Quaternion();
	/*    rotation.fromAngleNormalAxis(FastMath.PI,Vector3f.UNIT_X);
	    rotation.multLocal(new Quaternion().fromAngleNormalAxis(FastMath.PI/2f,Vector3f.UNIT_Y));
	   // rotation.fromAngleNormalAxis(FastMath.HALF_PI/2f - FastMath.HALF_PI/3f + FastMath.HALF_PI/6f, Vector3f.UNIT_Y);
*/
	    s1.getLocalRotation().set(rotation);
	    s2.getLocalRotation().set(rotation);
	    s3.getLocalRotation().set(rotation);
	    s4.getLocalRotation().set(rotation);
	    s5.getLocalRotation().set(rotation);
	    
	//    dlod.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, new Vector3f(1,0,0)));

		return dlod;
	}


	public void setParameters(float innerRadius, float arc) {
		
		boolean changed = false;
		if(this.arc != arc)
			changed = true;
		if(this.innerRadius != innerRadius)
			changed = true;
		
		if(!changed)
			return;
		
		this.innerRadius = innerRadius;
		this.arc = arc;
		
		baseFacade.updateGeometry(RADIUS, innerRadius,HEIGHT, baseFacade.getAxisSamples(), baseFacade.getRadialSamples(),  arc);
	//	baseFacade2.updateGeometry(RADIUS, innerRadius,HEIGHT, baseFacade2.getAxisSamples(), baseFacade2.getRadialSamples(),  arc);
		baseFacade3.updateGeometry(RADIUS, innerRadius,HEIGHT, baseFacade3.getAxisSamples(), baseFacade3.getRadialSamples(),  arc);
		baseFacade4.updateGeometry(RADIUS, innerRadius,HEIGHT, baseFacade4.getAxisSamples(), baseFacade4.getRadialSamples(),  arc);
		baseFacade5.updateGeometry(RADIUS, innerRadius,HEIGHT, baseFacade5.getAxisSamples(), baseFacade5.getRadialSamples(),  arc);
		getSpatial().updateModelBound();
	}





}
