package com.golemgame.model.spatial.shape;

import com.golemgame.model.quality.spatial.FlooredDistanceSwitchModel;
import com.golemgame.model.quality.spatial.SwitchModelQualityDelegate;
import com.golemgame.model.spatial.SpatialModelImpl;
import com.golemgame.states.StateManager;
import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.VBOInfo;
import com.jme.scene.lod.DiscreteLodNode;
import com.jme.scene.shape.Cone;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.shape.Disk;
import com.jme.scene.state.RenderState;

public class ConeFacade extends SpatialModelImpl {
	private static final long serialVersionUID = 1L;

	protected static Cylinder baseFacade=null;
	protected static Cylinder baseFacade2=null;
	protected static Cylinder baseFacade3=null;
	protected static Cylinder baseFacade4=null;
	protected static Cylinder baseFacade5=null;
	
	protected static Disk diskFacade=null;
	protected static Disk diskFacade1=null;
	protected static Disk diskFacade2=null;
	protected static Disk diskFacade3=null;
	protected static Disk diskFacade4=null;
	protected static Disk diskFacade5=null;
	
	public static final float RADIUS = 0.5f;
	public static final float HEIGHT = 1f;

	public ConeFacade() {
		super();

		this.updateModelData();
	}

	

	@Override
	public void applyRenderState(RenderState state, int position) {
		if(position == 0)
		{
			for(Spatial s:cylinders)
			{
				s.setRenderState(state);
			}
			
		}else if (position == 1)
		{
			for(Spatial s:rightEndCaps)
			{
				s.setRenderState(state);
				s.updateRenderState();
			}
			
		}
		this.getSpatial().updateRenderState();
	}

	public int getNumberOfPositions()
	{
		return 2;
	}

	private Spatial[] rightEndCaps;
	private Spatial[] cylinders;
	protected Spatial buildSpatial() {
		

		
		if (baseFacade == null)
		{//create the static sphere mesh
			baseFacade =  new Cone("false",CylinderFacade.DETAIL_5_AXIS, CylinderFacade.DETAIL_5_RADIAL,RADIUS,HEIGHT,false);
			baseFacade2  =  new Cone("false",CylinderFacade.DETAIL_4_AXIS, CylinderFacade.DETAIL_4_RADIAL,RADIUS,HEIGHT,false);
			baseFacade3  =   new Cone("false",CylinderFacade.DETAIL_3_AXIS, CylinderFacade.DETAIL_3_RADIAL,RADIUS,HEIGHT,false);
			baseFacade4 = new Cone("false",CylinderFacade.DETAIL_2_AXIS, CylinderFacade.DETAIL_2_RADIAL,RADIUS,HEIGHT,false);
			baseFacade5 =   new Cone("false",CylinderFacade.DETAIL_1_AXIS, CylinderFacade.DETAIL_1_RADIAL,RADIUS,HEIGHT,false);
			
			diskFacade = new Disk("cap", CylinderFacade.DETAIL_5_AXIS, CylinderFacade.DETAIL_5_RADIAL, RADIUS);
			diskFacade2  = new Disk("cap",  CylinderFacade.DETAIL_4_AXIS, CylinderFacade.DETAIL_4_RADIAL, RADIUS);
			diskFacade3  =  new Disk("cap",  CylinderFacade.DETAIL_3_AXIS, CylinderFacade.DETAIL_3_RADIAL,RADIUS);
			diskFacade4 = new Disk("cap",  CylinderFacade.DETAIL_2_AXIS, CylinderFacade.DETAIL_2_RADIAL, RADIUS);
			diskFacade5 =  new Disk("cap",  CylinderFacade.DETAIL_1_AXIS, CylinderFacade.DETAIL_1_RADIAL, RADIUS);
		
			if(StateManager.useVBO){
				baseFacade.setVBOInfo(new VBOInfo(true));
				baseFacade2.setVBOInfo(new VBOInfo(true));
				baseFacade3.setVBOInfo(new VBOInfo(true));		
				//baseFacade4.setVBOInfo(new VBOInfo(true));
				//baseFacade5.setVBOInfo(new VBOInfo(true));
			}
			
			if(StateManager.useVBO){
				diskFacade.setVBOInfo(new VBOInfo(true));
				diskFacade2.setVBOInfo(new VBOInfo(true));
				diskFacade3.setVBOInfo(new VBOInfo(true));
				//diskFacade4.setVBOInfo(new VBOInfo(true));
				//diskFacade5.setVBOInfo(new VBOInfo(true));
			}
		}
		//ideally, the LOD node should take into account its scale...
		//so when you are far away from a big sphere it still looks nice.
		
		Node cylNode1 = new Node();
		Node cylNode2 = new Node();
		Node cylNode3 = new Node();
		Node cylNode4 = new Node();
		Node cylNode5 = new Node();
		
		rightEndCaps = new Spatial[5];
		cylinders = new Spatial[5];
		
		//note, this lod node is completely thread safe - no changes to the scene graph 
		//are made when it switches which node it is drawing.
	    Spatial s1 = new SharedMesh("ConeFacade",baseFacade);
	    cylinders[0] = s1;	  
	    s1.setModelBound(new BoundingBox());
	    s1.updateModelBound();	    
	    
	    Spatial s2 =new SharedMesh("ConeFacade",baseFacade2);
	    cylinders[1] = s2;
	    s2.setModelBound(new BoundingBox());
	    s2.updateModelBound();

	    Spatial s3 = new SharedMesh("ConeFacade",baseFacade3);
	    cylinders[2] = s3;
	    s3.setModelBound(new BoundingBox());
	    s3.updateModelBound();

	    Spatial s4 =new SharedMesh("ConeFacade",baseFacade4);
	    cylinders[3] = s4;
	    s4.setModelBound(new BoundingBox());
	    s4.updateModelBound();

	    Spatial s5 =new SharedMesh("ConeFacade",baseFacade5);
	    cylinders[4] = s5;
	    s5.setModelBound(new BoundingBox());
	    s5.updateModelBound();
	    
	    
	    Spatial d1 = new SharedMesh("Cap1",diskFacade);  
	    rightEndCaps[0] = d1;
	    d1.setModelBound(new BoundingBox());
	    d1.updateModelBound();	    
	    
	    Spatial d2 =new SharedMesh("Cap1",diskFacade2);
	    rightEndCaps[1] = d2;
	    d2.setModelBound(new BoundingBox());
	    d2.updateModelBound();

	    Spatial d3 = new SharedMesh("Cap1",diskFacade3);
	    rightEndCaps[2] = d3;
	    d3.setModelBound(new BoundingBox());
	    d3.updateModelBound();

	    Spatial d4 =new SharedMesh("Cap1",diskFacade4);
	    rightEndCaps[3] = d4;
	    d4.setModelBound(new BoundingBox());
	    d4.updateModelBound();

	    Spatial d5 =new SharedMesh("Cap1",diskFacade5);
	    rightEndCaps[4] = d5;
	    d5.setModelBound(new BoundingBox());
	    d5.updateModelBound();
	    
	    
	    cylNode1.attachChild(s1);
	    cylNode1.attachChild(d1);
	    
	    cylNode2.attachChild(s2);
	    cylNode2.attachChild(d2);
	    
	    cylNode3.attachChild(s3);
	    cylNode3.attachChild(d3);
	    
	    cylNode4.attachChild(s4);
	    cylNode4.attachChild(d4);
	    
	    cylNode5.attachChild(s5);
	    cylNode5.attachChild(d5);
	    
	    d1.getLocalTranslation().z += 0.5f;
	    d2.getLocalTranslation().set(d1.getLocalTranslation());
	    d3.getLocalTranslation().set(d1.getLocalTranslation());
	    d4.getLocalTranslation().set(d1.getLocalTranslation());
	    d5.getLocalTranslation().set(d1.getLocalTranslation());
	    
	  
	    
	    FlooredDistanceSwitchModel m = new FlooredDistanceSwitchModel(5);
	    m.setModelDistance(0, CylinderFacade.DISTANCE[0], CylinderFacade.DISTANCE[1]);
	    m.setModelDistance(1, CylinderFacade.DISTANCE[1], CylinderFacade.DISTANCE[2]);
	    m.setModelDistance(2, CylinderFacade.DISTANCE[2], CylinderFacade.DISTANCE[3]);
	    m.setModelDistance(3,  CylinderFacade.DISTANCE[3], CylinderFacade.DISTANCE[4]);
	    m.setModelDistance(4,  CylinderFacade.DISTANCE[4], CylinderFacade.DISTANCE[5]);
	    
	    StateManager.getQualityManager().addQualtiyDelegate(new SwitchModelQualityDelegate(m));
		 
	 
	    DiscreteLodNode dlod = new DiscreteLodNode("DLOD", m);
	    dlod.attachChild(cylNode1);
	    dlod.attachChild(cylNode2);
	    dlod.attachChild(cylNode3);
	    dlod.attachChild(cylNode4);
	    dlod.attachChild(cylNode5);
	    dlod.setActiveChild(2);
	  
	    
	    dlod.setIsCollidable(false);
	    dlod.setModelBound(new BoundingBox());

		
	    dlod.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, new Vector3f(1,0,0));


		return dlod;
	}





}
