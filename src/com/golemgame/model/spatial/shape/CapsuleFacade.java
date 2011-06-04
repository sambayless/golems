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
package com.golemgame.model.spatial.shape;

import com.golemgame.model.quality.spatial.FlooredDistanceSwitchModel;
import com.golemgame.model.quality.spatial.SwitchModelQualityDelegate;
import com.golemgame.model.spatial.SpatialModelImpl;
import com.golemgame.states.StateManager;
import com.jme.bounding.BoundingBox;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.VBOInfo;
import com.jme.scene.lod.DiscreteLodNode;
import com.jme.scene.shape.Capsule;

public class CapsuleFacade extends SpatialModelImpl {

	private static final long serialVersionUID = 1L;
	public static final float INIT_SIZE = 1f;
	protected static TriMesh baseFacade=null;
	protected static TriMesh baseFacade1=null;
	protected static TriMesh baseFacade2=null;
	protected static TriMesh baseFacade3=null;
	protected static TriMesh baseFacade4=null;
	protected static TriMesh baseFacade5=null;

	
	public CapsuleFacade(float height, float radius) {
		super();
	
		
	}

	
	protected Spatial buildSpatial() {
		if (baseFacade == null)
		{//create the static sphere mesh
			
			baseFacade =  new Capsule("facadeCapsule", CylinderFacade.DETAIL_5_AXIS, CylinderFacade.DETAIL_5_RADIAL, CylinderFacade.DETAIL_5_RADIAL, 0.5f,1f);	
			baseFacade2  = new Capsule("facadeCapsule",CylinderFacade.DETAIL_4_AXIS, CylinderFacade.DETAIL_4_RADIAL, CylinderFacade.DETAIL_4_RADIAL, 0.5f,1f);	
			baseFacade3  = new Capsule("facadeCapsule",CylinderFacade.DETAIL_3_AXIS, CylinderFacade.DETAIL_3_RADIAL, CylinderFacade.DETAIL_3_RADIAL, 0.5f,1f);	
			baseFacade4 =  new Capsule("facadeCapsule",CylinderFacade.DETAIL_2_AXIS, CylinderFacade.DETAIL_2_RADIAL, CylinderFacade.DETAIL_2_RADIAL, 0.5f,1f);	
			baseFacade5 =   new Capsule("facadeCapsule",CylinderFacade.DETAIL_1_AXIS, CylinderFacade.DETAIL_1_RADIAL, CylinderFacade.DETAIL_1_RADIAL, 0.5f,1f);	
		
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
	    Spatial s1 = new SharedMesh("Cyl",baseFacade);
	    
	
	    s1.setModelBound(new BoundingBox());
	    s1.updateModelBound();

	    
	    
	    Spatial s2 =new SharedMesh("Cyl",baseFacade2);
	  
	    s2.setModelBound(new BoundingBox());
	    s2.updateModelBound();

	    Spatial s3 = new SharedMesh("Cyl",baseFacade3);
	    
	    s3.setModelBound(new BoundingBox());
	    s3.updateModelBound();

	    Spatial s4 =new SharedMesh("Cyl",baseFacade4);
	    
	    s4.setModelBound(new BoundingBox());
	    s4.updateModelBound();

	    Spatial s5 =new SharedMesh("Cyl",baseFacade5);
	   
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
		return dlod;
	}


}
