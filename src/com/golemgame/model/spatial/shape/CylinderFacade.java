package com.golemgame.model.spatial.shape;

import com.golemgame.model.quality.spatial.FlooredDistanceSwitchModel;
import com.golemgame.model.quality.spatial.SwitchModelQualityDelegate;
import com.golemgame.model.spatial.SpatialModelImpl;
import com.golemgame.states.StateManager;
import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.VBOInfo;
import com.jme.scene.lod.DiscreteLodNode;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.shape.Disk;
import com.jme.scene.state.RenderState;

public class CylinderFacade extends SpatialModelImpl{
	private static final long serialVersionUID = 1L;
	public static final float INIT_SIZE = 1f;
	protected static Cylinder baseFacade=null;
	//protected static Cylinder baseFacade1=null;
	protected static Cylinder baseFacade2=null;
	protected static Cylinder baseFacade3=null;
	protected static Cylinder baseFacade4=null;
	protected static Cylinder baseFacade5=null;
	
	protected static Disk diskFacade=null;
	//protected static Disk diskFacade1=null;
	protected static Disk diskFacade2=null;
	protected static Disk diskFacade3=null;
	protected static Disk diskFacade4=null;
	protected static Disk diskFacade5=null;

	public static final int DETAIL_5_AXIS = 50;	
	public static final int DETAIL_5_RADIAL = 30;
	
	public static final int DETAIL_4_AXIS = 35;	
	public static final int DETAIL_4_RADIAL = 20;
	
	public static final int DETAIL_3_AXIS = 15;	
	public static final int DETAIL_3_RADIAL = 10;
	
	public static final int DETAIL_2_AXIS = 10;	
	public static final int DETAIL_2_RADIAL = 8;
	
	public static final int DETAIL_1_AXIS = 5;	
	public static final int DETAIL_1_RADIAL = 3;
	
	public static final float[] DISTANCE = new float[]{0,3,15,100,1000,Float.POSITIVE_INFINITY};

	public CylinderFacade() {
		super();
	}




	public CylinderFacade(boolean registerSpatial) {
		super(registerSpatial);
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
			
		}else if (position == 2)
		{
			for(Spatial s:leftEndCaps)
			{
				s.setRenderState(state);
				s.updateRenderState();
			}
		}
		this.getSpatial().updateRenderState();
	}

	public int getNumberOfPositions()
	{
		return 3;
	}
	
	private Spatial[] leftEndCaps;
	private Spatial[] rightEndCaps;
	private Spatial[] cylinders;

	protected Spatial buildSpatial() {
	
		
		if (baseFacade == null)
		{//create the static sphere mesh
			baseFacade = new Cylinder("built", DETAIL_5_AXIS,DETAIL_5_RADIAL, INIT_SIZE/2f,INIT_SIZE,false);
			baseFacade2  = new Cylinder("built", DETAIL_4_AXIS,DETAIL_4_RADIAL, INIT_SIZE/2f,INIT_SIZE,false);
			baseFacade3  =  new Cylinder("built", DETAIL_3_AXIS,DETAIL_3_RADIAL, INIT_SIZE/2f,INIT_SIZE,false);
			baseFacade4 = new Cylinder("built", DETAIL_2_AXIS,DETAIL_2_RADIAL, INIT_SIZE/2f,INIT_SIZE,false);
			baseFacade5 =  new Cylinder("built", DETAIL_1_AXIS,DETAIL_1_RADIAL, INIT_SIZE/2f,INIT_SIZE,false);
		
			if(StateManager.useVBO){
				baseFacade.setVBOInfo(new VBOInfo(true));
				baseFacade2.setVBOInfo(new VBOInfo(true));
				baseFacade3.setVBOInfo(new VBOInfo(true));		
				//baseFacade4.setVBOInfo(new VBOInfo(true));
				//baseFacade5.setVBOInfo(new VBOInfo(true));
			}
			
			diskFacade = new Disk("cap", DETAIL_5_AXIS,DETAIL_5_RADIAL, INIT_SIZE/2f);
			diskFacade2  = new Disk("cap", DETAIL_4_AXIS,DETAIL_4_RADIAL, INIT_SIZE/2f);
			diskFacade3  =  new Disk("cap", DETAIL_3_AXIS,DETAIL_3_RADIAL, INIT_SIZE/2f);
			diskFacade4 = new Disk("cap", DETAIL_2_AXIS,DETAIL_2_RADIAL, INIT_SIZE/2f);
			diskFacade5 =  new Disk("cap", DETAIL_1_AXIS,DETAIL_1_RADIAL, INIT_SIZE/2f);

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
		
		//note, this lod node is completely thread safe - no changes to the scene graph 
		//are made when it switches which node it is drawing.
		
		leftEndCaps = new Spatial[5];
		rightEndCaps = new Spatial[5];
		cylinders = new Spatial[5];
		
		Node cylNode1 = new Node();
		Node cylNode2 = new Node();
		Node cylNode3 = new Node();
		Node cylNode4 = new Node();
		Node cylNode5 = new Node();
		
	    Spatial s1 = new SharedMesh("Cyl",baseFacade);  
	    cylinders[0] = s1;
	    s1.setModelBound(new BoundingBox());
	    s1.updateModelBound();	    
	    
	    Spatial s2 =new SharedMesh("Cyl",baseFacade2);
	    cylinders[1] = s2;
	    s2.setModelBound(new BoundingBox());
	    s2.updateModelBound();

	    Spatial s3 = new SharedMesh("Cyl",baseFacade3);
	    cylinders[2] = s3;
	    s3.setModelBound(new BoundingBox());
	    s3.updateModelBound();

	    Spatial s4 =new SharedMesh("Cyl",baseFacade4);
	    cylinders[3] = s4;
	    s4.setModelBound(new BoundingBox());
	    s4.updateModelBound();

	    Spatial s5 =new SharedMesh("Cyl",baseFacade5);
	    cylinders[4] = s5;
	    s5.setModelBound(new BoundingBox());
	    s5.updateModelBound();
	    
	    
	    Spatial d1 = new SharedMesh("Cap1",diskFacade);  
	    leftEndCaps[0] = d1;
	    d1.setModelBound(new BoundingBox());
	    d1.updateModelBound();	    
	    
	    Spatial d2 =new SharedMesh("Cap1",diskFacade2);
	    leftEndCaps[1] = d2;
	    d2.setModelBound(new BoundingBox());
	    d2.updateModelBound();

	    Spatial d3 = new SharedMesh("Cap1",diskFacade3);
	    leftEndCaps[2] = d3;
	    d3.setModelBound(new BoundingBox());
	    d3.updateModelBound();

	    Spatial d4 =new SharedMesh("Cap1",diskFacade4);
	    leftEndCaps[3] = d4;
	    d4.setModelBound(new BoundingBox());
	    d4.updateModelBound();

	    Spatial d5 =new SharedMesh("Cap1",diskFacade5);
	    leftEndCaps[4] = d5;
	    d5.setModelBound(new BoundingBox());
	    d5.updateModelBound();
	    
	    
	    
	    Spatial d1b = new SharedMesh("Cap2",diskFacade);  
	    rightEndCaps[0] = d1b;
	    d1b.setModelBound(new BoundingBox());
	    d1b.updateModelBound();	    
	    
	    Spatial d2b =new SharedMesh("Cap2",diskFacade2);
	    rightEndCaps[1] = d2b;
	    d2b.setModelBound(new BoundingBox());
	    d2b.updateModelBound();

	    Spatial d3b = new SharedMesh("Cap2",diskFacade3);
	    rightEndCaps[2] = d3b;
	    d3b.setModelBound(new BoundingBox());
	    d3b.updateModelBound();

	    Spatial d4b =new SharedMesh("Cap2",diskFacade4);
	    rightEndCaps[3] = d4b;
	    d4b.setModelBound(new BoundingBox());
	    d4b.updateModelBound();

	    Spatial d5b =new SharedMesh("Cap2",diskFacade5);
	    rightEndCaps[4] = d5b;
	    d5b.setModelBound(new BoundingBox());
	    d5b.updateModelBound();
	    
	    
	    d1.getLocalTranslation().z += 0.5f;
	    d2.getLocalTranslation().set(d1.getLocalTranslation());
	    d3.getLocalTranslation().set(d1.getLocalTranslation());
	    d4.getLocalTranslation().set(d1.getLocalTranslation());
	    d5.getLocalTranslation().set(d1.getLocalTranslation());
	    
	    d1b.getLocalTranslation().z -= 0.5f;
	    d2b.getLocalTranslation().set(d1b.getLocalTranslation());
	    d3b.getLocalTranslation().set(d1b.getLocalTranslation());
	    d4b.getLocalTranslation().set(d1b.getLocalTranslation());
	    d5b.getLocalTranslation().set(d1b.getLocalTranslation());
	    
	    d1b.getLocalRotation().fromAngleAxis(FastMath.PI, Vector3f.UNIT_Y).multLocal(new Quaternion().fromAngleAxis(FastMath.PI, Vector3f.UNIT_Z));
	    d2b.getLocalRotation().set(d1b.getLocalRotation());
	    d3b.getLocalRotation().set(d1b.getLocalRotation());
	    d4b.getLocalRotation().set(d1b.getLocalRotation());
	    d5b.getLocalRotation().set(d1b.getLocalRotation());
		   
/*	    CullState cs = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
	    cs.setCullMode(CullState.CS_FRONT);
	    d1b.setRenderState(cs);
	    d2b.setRenderState(cs);
	    d3b.setRenderState(cs);
	    d4b.setRenderState(cs);
	    d5b.setRenderState(cs);
	    */
	/*    d1b.getLocalRotation().fromAngleAxis(FastMath.PI, Vector3f.UNIT_Y);
	    d2b.getLocalRotation().set(d1b.getLocalRotation());
	    d3b.getLocalRotation().set(d1b.getLocalRotation());
	    d4b.getLocalRotation().set(d1b.getLocalRotation());
	    d5b.getLocalRotation().set(d1b.getLocalRotation());*/
	    
	    cylNode1.attachChild(d1);
	    cylNode1.attachChild(s1);
	    cylNode1.attachChild(d1b);
	    
	    cylNode2.attachChild(d2);
	    cylNode2.attachChild(s2);
	    cylNode2.attachChild(d2b);
	    
	    cylNode3.attachChild(d3);
	    cylNode3.attachChild(s3);
	    cylNode3.attachChild(d3b);
	    
	    cylNode4.attachChild(d4);
	    cylNode4.attachChild(s4);
	    cylNode4.attachChild(d4b);
	    
	    cylNode5.attachChild(d5);
	    cylNode5.attachChild(s5);
	    cylNode5.attachChild(d5b);
	    
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
	    dlod.updateModelBound();
	    dlod.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.HALF_PI, new Vector3f(1,0,0)));		
	    dlod.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, new Vector3f(0,1,0)));

	    
		return dlod;
	}


}
