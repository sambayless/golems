package com.golemgame.structural.structures.ghost;

import java.util.Collection;

import com.golemgame.model.Model;
import com.golemgame.model.spatial.SpatialModel;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.mvc.golems.GhostConeInterpreter;
import com.golemgame.properties.Property;
import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.SceneElement;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Cone;
import com.jme.scene.shape.Cylinder;

public class GhostCone extends GhostCylinder {
	private static final long serialVersionUID = 1L;
	private static final float RADIUS = 0.5f;
	private static final float HEIGHT = 1f;
	
	private GhostConeInterpreter interpreter;
	public GhostCone(PropertyStore store) {
		super(store);
		this.interpreter = new GhostConeInterpreter(store);
		getModel().getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Z);
	}
	
	
	
	
	protected CylinderModel buildModel() {
		return new ConeModel();
	}



	
	protected Model buildFacade() {
		return new ConeFacade();
	}
	




	public static class ConeModel extends CylinderModel
	{
		private static final long serialVersionUID = 1L;

		
		public CylinderModel makeSharedModel() {
			return new ConeModel();
		}

		
		public ConeModel() {
			super();
		}


		protected Spatial buildSpatial() {
		//	Node node = new Node();
			Spatial	spatial = new Cone("cone",4,8, RADIUS, HEIGHT);// new Box("built", new Vector3f(), INIT_SIZE/2,INIT_SIZE/2,INIT_SIZE/2);		 
			spatial.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, new Vector3f(1,0,0));
			//Spatial spatial= new Pyramid("pyramid", INIT_SIZE, INIT_SIZE);// new Box("box", new Vector3f(), INIT_SIZE/2f,INIT_SIZE/2f,INIT_SIZE/2f);		 
			spatial.setCullMode(SceneElement.CULL_ALWAYS);
			spatial.getLocalTranslation().zero();
			//spatial.getLocalRotation().loadIdentity();
			spatial.getLocalScale().set(1,1,1);
			spatial.setModelBound(new BoundingBox());
			spatial.updateModelBound();
			return spatial;
		}
		
	
		

	}
	
	public static class ConeFacade extends SpatialModel
	{
		private static final long serialVersionUID = 1L;
		public static final float INIT_SIZE = 1f;
		protected static Cylinder baseFacade=null;


	
		public ConeFacade() {
			super();

			this.updateModelData();
		}

	

		
		protected Spatial buildSpatial() {
			
			if (baseFacade == null)
			{
				baseFacade =  new Cone("false",30,30,RADIUS,HEIGHT);
			}
			
			Spatial spatial =new SharedMesh("ConeFacade",baseFacade);
			spatial.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, new Vector3f(1,0,0));

			spatial.setIsCollidable(false);
			spatial.setModelBound(new BoundingBox());
			return spatial;
		}


		
		public boolean isShareable() {
			return true;
		}

		
		public ConeFacade makeSharedModel() {
			return new ConeFacade();
		}
		


	}
	

	

}
