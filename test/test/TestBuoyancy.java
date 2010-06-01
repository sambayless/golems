package test;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.jme.input.KeyInput;
import com.jme.input.KeyInputListener;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Pyramid;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jmetest.physics.SimplePhysicsTest;
import com.jmex.buoyancy.Buoyancy;
import com.jmex.buoyancy.FluidRegion;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.material.Material;

public class TestBuoyancy  extends SimplePhysicsTest {
	private boolean slow = false;
	private float speed = 0.1f;
	Buoyancy buoyancy;
	
	protected void simpleInitGame() {
	    java.util.logging.Logger.getLogger("").setLevel(Level.OFF);
	    java.util.logging.Logger.getLogger( PhysicsSpace.LOGGER_NAME ).setLevel(Level.OFF);
		
		KeyInput.get().addListener(new KeyInputListener()
		{

			
			public void onKey(char character, int keyCode, boolean pressed) {
				if (character == 'o')
				{
					slow = ! slow;
					if (slow)
					{
						setPhysicsSpeed(speed);
					}else
						setPhysicsSpeed(1f);
				}
				
			}
			
		});
		super.showPhysics =true;
		this.rootNode.setCullMode(SceneElement.CULL_NEVER);

		buoyancy = new Buoyancy(super.getPhysicsSpace());	
		
		getPhysicsSpace().addToUpdateCallbacks(buoyancy);
		

		//Add water, at height 0	
		//This particular region extends forever in all directions
		//But regions can define boundaries in which they operate if they wish.
		//You can also adjust the height of water with BuoyancyRegion.setFluidLevel();
		buoyancy.addRegion(FluidRegion.WATER);		
		//FluidRegion.WATER.setFluidViscocity(100);
		//Create a visualization of some water
		buildWater();
		
		//Create some interesting shapes to test the various things that might happen in to floating objects
		buildTestObjects();
		
	
	}

	private void buildWater()
	{
		//Add lighting to the seen so we can see the water
		  PointLight light = new PointLight();
	        light.setDiffuse( new ColorRGBA(1f, 1f, 1f, 1.0f  ) );
	        light.setAmbient( new ColorRGBA( 0.75f, 0.75f, 0.75f, 1.0f ) );
	        light.setLocation( new Vector3f( -10, 20, 10 ) );
	        light.setEnabled( true );

	      
	        LightState lightState = display.getRenderer().createLightState();
	        lightState.setEnabled( true );
	        lightState.attach( light );
	        rootNode.setRenderState( lightState );
	        rootNode.updateWorldBound();     
		
		Box pool = new Box("Pool", new Vector3f(), 200,7.5f, 200);
		pool.getLocalTranslation().y = -7.5f;	
		
		AlphaState alpha = display.getRenderer().createAlphaState();
		alpha.setBlendEnabled(true);
		alpha.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		alpha.setDstFunction(AlphaState.SB_ONE_MINUS_SRC_ALPHA);
		alpha.setTestEnabled(true);		
		alpha.setTestFunction(AlphaState.TF_GREATER);		
		
		MaterialState waterColor = display.getRenderer().createMaterialState();
		waterColor.setSpecular(new ColorRGBA(0.2f,0.2f,0.7f,0.3f));
		waterColor.setAmbient(new ColorRGBA(0.2f,0.2f,0.7f,0.3f));
		waterColor.setDiffuse(new ColorRGBA(0.2f,0.2f,0.7f,0.3f));		
		waterColor.setShininess(30);
		waterColor.setColorMaterial(MaterialState.CM_NONE );
		waterColor.setMaterialFace(MaterialState.MF_FRONT_AND_BACK);
		
		pool.setRenderState(alpha);
		pool.setRenderState(waterColor);
		pool.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		
		rootNode.attachChild(pool);
		rootNode.updateRenderState();
		pool.updateRenderState();		
	}
	
	private void buildTestObjects()
	{
		float height = 5;//the height above water level to start the phsycs objects at
		float zDistance = -15;
		//Need to keep track of which physicsCollisionGeometries are represented by which Spatials, as the VolumeBounds from those spatials are used to calculate some buoyancy properties
		Map<Spatial, PhysicsCollisionGeometry> collisionGeometryMap =  new HashMap<Spatial, PhysicsCollisionGeometry> ();

			//Make a sphere		
		DynamicPhysicsNode sphere = getPhysicsSpace().createDynamicNode();
		sphere.attachChild(new Sphere("",10,10,1f));
		sphere.setMaterial(Material.WOOD);
		
		PhysicsNode.generatePhysicsGeometry(sphere, sphere, true, collisionGeometryMap);
		sphere.computeMass();//This is crucial to ensuring the buoyancy force is applied correctly
		sphere.getLocalTranslation().y = height+5;
		sphere.getLocalTranslation().x = -25f;
		sphere.getLocalTranslation().z += zDistance;
		rootNode.attachChild(sphere);
		
		//Construct buoyant objects for this sphere
		buoyancy.addPhysicsNode(sphere, collisionGeometryMap);
	
	

		//Make a sphere	to drop on the box	
		DynamicPhysicsNode sphere2 = getPhysicsSpace().createDynamicNode();
		sphere2.attachChild(new Sphere("",10,10,1f));
		sphere2.setMaterial(Material.WOOD);
		collisionGeometryMap.clear();
		PhysicsNode.generatePhysicsGeometry(sphere2, sphere2, true, collisionGeometryMap);
		sphere2.computeMass();//This is crucial to ensuring the buoyancy force is applied correctly
		sphere2.getLocalTranslation().y = 50;
		sphere2.getLocalTranslation().x = -1f;
		sphere2.getLocalTranslation().z += zDistance;
		rootNode.attachChild(sphere2);
		buoyancy.addPhysicsNode(sphere2, collisionGeometryMap);
		
		DynamicPhysicsNode sphere3 = getPhysicsSpace().createDynamicNode();
		sphere3.attachChild(new Sphere("",10,10,1.3f));
		sphere3.setMaterial(Material.WOOD);
		collisionGeometryMap.clear();
		PhysicsNode.generatePhysicsGeometry(sphere3, sphere3, true, collisionGeometryMap);
		sphere3.computeMass();//This is crucial to ensuring the buoyancy force is applied correctly
		sphere3.getLocalTranslation().y = 20;
		sphere3.getLocalTranslation().x =  2;
		sphere3.getLocalTranslation().z += zDistance ;
		rootNode.attachChild(sphere3);
		
		//Construct buoyant objects for this sphere
		buoyancy.addPhysicsNode(sphere3, collisionGeometryMap);

		DynamicPhysicsNode box = getPhysicsSpace().createDynamicNode();
		Box b = new Box("",new Vector3f(), 1.5f,1f,1.5f);
			box.attachChild(b);
		
		box.setMaterial(Material.WOOD);
		collisionGeometryMap.clear();
		PhysicsNode.generatePhysicsGeometry(box, box, true, collisionGeometryMap);
		box.computeMass();//This is crucial to ensuring the buoyancy force is applied correctly
		box.getLocalTranslation().y = height+5;
		box.getLocalTranslation().x = 0f;
		box.getLocalTranslation().z += zDistance;
	
		box.setAngularVelocity(new Vector3f(0,0,20));

		rootNode.attachChild(box);
		b.updateRenderState();
		box.updateRenderState();
		rootNode.updateRenderState();
		
	
	
		buoyancy.addPhysicsNode(box, collisionGeometryMap);
	
	
		DynamicPhysicsNode box2 = getPhysicsSpace().createDynamicNode();
		Box b4 = new Box("",new Vector3f(), 1.5f,1f,1.5f);
			box2.attachChild(b4);
		
		box2.setMaterial(Material.WOOD);
		collisionGeometryMap.clear();
		PhysicsNode.generatePhysicsGeometry(box2, box2, true, collisionGeometryMap);
		box2.computeMass();//This is crucial to ensuring the buoyancy force is applied correctly
		box2.getLocalTranslation().y = height+5;
		box2.getLocalTranslation().x = 10f;
		box2.getLocalTranslation().z += zDistance;
	
		box2.setAngularVelocity(new Vector3f(0,0,0));
		//box2.setAngularVelocity(new Vector3f(0,-1,0));
		//box2.setLinearVelocity(new Vector3f (1,0,0));
	//	box2.getLocalRotation().fromAngleAxis(FastMath.PI/2f, Vector3f.UNIT_Z);
		
		rootNode.attachChild(box2);
	
		box2.updateRenderState();
		rootNode.updateRenderState();
		
	
	
		buoyancy.addPhysicsNode(box2, collisionGeometryMap);

	
		DynamicPhysicsNode oddShape = getPhysicsSpace().createDynamicNode();

		Box b2 = new Box("",new Vector3f(), 5f,0.5f,0.5f);
		oddShape.attachChild(b2);
		b2.getLocalTranslation().x = -1;
		Box b3 = new Box("",new Vector3f(), 1.5f,1,5);
		b3.getLocalTranslation().set(4,1,2);
		oddShape.attachChild(b3);
		
		oddShape.setMaterial(Material.WOOD);
		collisionGeometryMap.clear();
		PhysicsNode.generatePhysicsGeometry(oddShape, oddShape, true, collisionGeometryMap);
		oddShape.computeMass();//This is crucial to ensuring the buoyancy force is applied correctly
		oddShape.getLocalTranslation().y = height;
		oddShape.getLocalTranslation().x = 15f;
		oddShape.getLocalTranslation().z += zDistance;
		oddShape.getLocalRotation().fromAngleAxis(FastMath.PI/5f, new Vector3f(1,0,1));
		
	
		


		rootNode.attachChild(oddShape);
		b3.updateRenderState();
		oddShape.updateRenderState();
		rootNode.updateRenderState();
		
	
		
	
		rootNode.attachChild(oddShape);
	
		oddShape.updateRenderState();
		rootNode.updateRenderState();

		buoyancy.addPhysicsNode(oddShape, collisionGeometryMap);
	
	/*	
		//Make an asterisk of boxes		
		DynamicPhysicsNode asterisk = getPhysicsSpace().createDynamicNode();
		asterisk.attachChild(new Box("",new Vector3f(),0.5f,0.5f,5));
		asterisk.attachChild(new Box("",new Vector3f(),0.5f,5f,0.5f));
		asterisk.attachChild(new Box("",new Vector3f(),5f,0.5f,0.5f));
		asterisk.setMaterial(Material.WOOD);
		collisionGeometryMap.clear();
		PhysicsNode.generatePhysicsGeometry(asterisk, asterisk, true, collisionGeometryMap);
		asterisk.computeMass();
		asterisk.getLocalTranslation().y = height;
		asterisk.getLocalTranslation().z += zDistance;
		asterisk.getLocalRotation().fromAngleAxis(FastMath.PI * (float) Math.random(), new Vector3f((float) Math.random(),(float) Math.random(),(float) Math.random()));
		rootNode.attachChild(asterisk);
		
		//Construct buoyant objects for each PhysicsCollisionGeometry in this PhysicsNode, using the CollisionGeometryMap
		//This object also needs the balancing force
		buoyancy.addPhysicsNode(asterisk, collisionGeometryMap);
		
		
		//Make an star of pyramids
		DynamicPhysicsNode pyramids = getPhysicsSpace().createDynamicNode();

		Vector3f distance = new Vector3f(0,1.5f,0);
		
		Pyramid point = new Pyramid("",0.5f, 3f);
		point.getLocalTranslation().set(distance);
		pyramids.attachChild(point);
		
		point = new Pyramid("",0.5f, 3f);
		point.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, new Vector3f(1,0,0));
		point.getLocalTranslation().set(point.getLocalRotation().mult(distance));
		pyramids.attachChild(point);
		
		point = new Pyramid("",0.5f, 3f);
		point.getLocalRotation().fromAngleNormalAxis(FastMath.PI, new Vector3f(1,0,0));
		point.getLocalTranslation().set(point.getLocalRotation().mult(distance));
		pyramids.attachChild(point);	
		
		point = new Pyramid("",0.5f, 3f);
		point.getLocalRotation().fromAngleNormalAxis(-FastMath.HALF_PI, new  Vector3f(1,0,0));
		point.getLocalTranslation().set(point.getLocalRotation().mult(distance));
		pyramids.attachChild(point);	
		
		point = new Pyramid("",0.5f, 3f);
		point.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, new  Vector3f(0,0,1));
		point.getLocalTranslation().set(point.getLocalRotation().mult(distance));
		pyramids.attachChild(point);		
		
		point = new Pyramid("",0.5f, 3f);
		point.getLocalRotation().fromAngleNormalAxis(-FastMath.HALF_PI, new  Vector3f(0,0,1));
		point.getLocalTranslation().set(point.getLocalRotation().mult(distance));
		pyramids.attachChild(point);			
		
		pyramids.setMaterial(Material.WOOD);
		collisionGeometryMap.clear();
		PhysicsNode.generatePhysicsGeometry(pyramids, pyramids, true, collisionGeometryMap);
		pyramids.computeMass();
		pyramids.getLocalTranslation().y = height;
		pyramids.getLocalTranslation().x = -15;
		pyramids.getLocalTranslation().z += zDistance;
		pyramids.getLocalRotation().fromAngleAxis(FastMath.PI * (float) Math.random(), new Vector3f((float) Math.random(),(float) Math.random(),(float) Math.random()));
		rootNode.attachChild(pyramids);
		pyramids.updateRenderState();
		
		//This star is composed of collisionGeometries with centers of mass far from the center of the object, and so it will right itself without adding any extra balancing
		buoyancy.addPhysicsNode(pyramids, collisionGeometryMap);
		//rootNode.attachChild(buoyancy.debugBox);
		rootNode.updateRenderState();		*/
		
		
	}
		

	public static void main(String[] args) {
    	TestBuoyancy app = new TestBuoyancy();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }
}
