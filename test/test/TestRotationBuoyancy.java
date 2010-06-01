package test;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.jme.input.KeyInput;
import com.jme.input.KeyInputListener;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
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

public class TestRotationBuoyancy  extends SimplePhysicsTest {
	private boolean slow = false;
	private float speed = 0.1f;
	Buoyancy buoyancy;
	
	protected void simpleInitGame() {
	    java.util.logging.Logger.getLogger("").setLevel(Level.OFF);
	    java.util.logging.Logger.getLogger( PhysicsSpace.LOGGER_NAME ).setLevel(Level.OFF);
	   
		super.showPhysics =true;
		this.rootNode.setCullMode(SceneElement.CULL_NEVER);
		Node debugNode = new Node();
		this.rootNode.attachChild(debugNode);
		buoyancy = new Buoyancy(super.getPhysicsSpace());	
		
		
		getPhysicsSpace().addToUpdateCallbacks(buoyancy);


		//Add water, at height 0	
		//This particular region extends forever in all directions
		//But regions can define boundaries in which they operate if they wish.
		//You can also adjust the height of water with BuoyancyRegion.setFluidLevel();
		buoyancy.addRegion(FluidRegion.WATER);		
		
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
		
		//Create a visualization of some water
		buildWater();
		
		//Create some interesting shapes to test the various things that might happen in to floating objects
		buildTestObjects();
		
		
	}
    private static class RotatingController extends Controller {
        private Quaternion rot;
        private Vector3f axis;
        private final Spatial spatial;
        private  Vector3f translation;
        private DynamicPhysicsNode physicsNode;
        private Vector3f omega;
        
        public RotatingController( Spatial spatial, DynamicPhysicsNode physicsNode ) {
            this.spatial = spatial;
            rot = new Quaternion();
          //  axis = new Vector3f( 0.038056172f, 0.48872367f, 0.37676573f ).normalizeLocal();
           // axis = new Vector3f( 0.865637f, 0.28094327f, 0.49204808f).normalizeLocal();
            axis = new Vector3f(0.8787964f, -0.07770755f, -0.07770755f).normalizeLocal();
            translation = new Vector3f(0,-5f, 0);
            this.physicsNode = physicsNode;
            omega = new Vector3f(1,0,0);

         //   3.3173127	com.jme.math.Vector3f [X=0.8787964, Y=-0.07770755, Z=0.47082725
        }

        public void update( float time ) {
          //  rot.fromAngleNormalAxis( 0.5f * time, axis );
            spatial.getLocalRotation().multLocal( rot );
            spatial.getLocalTranslation().set(translation);
            physicsNode.clearDynamics();
            physicsNode.setAngularVelocity(omega);
            
        }
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
/*
		//Make a sphere		
		DynamicPhysicsNode sphere = getPhysicsSpace().createDynamicNode();
		sphere.attachChild(new Sphere("",10,10,1f));
		sphere.setMaterial(Material.WOOD);
		
		PhysicsNode.generatePhysicsGeometry(sphere, sphere, true, collisionGeometryMap);
		sphere.computeMass();//This is crucial to ensuring the buoyancy force is applied correctly
		sphere.getLocalTranslation().y = height;
		sphere.getLocalTranslation().x = -25f;
		sphere.getLocalTranslation().z += zDistance;
		rootNode.attachChild(sphere);
		
		//Construct buoyant objects for this sphere
		buoyancy.addPhysicsNode(sphere, collisionGeometryMap);
		
	*/
/*	
		//Make a sphere	to drop on the box	
		DynamicPhysicsNode sphere2 = getPhysicsSpace().createDynamicNode();
		sphere2.attachChild(new Sphere("",10,10,1f));
		sphere2.setMaterial(Material.WOOD);
		collisionGeometryMap.clear();
		PhysicsNode.generatePhysicsGeometry(sphere2, sphere2, true, collisionGeometryMap);
		sphere2.computeMass();//This is crucial to ensuring the buoyancy force is applied correctly
		sphere2.getLocalTranslation().y = 50;
		sphere2.getLocalTranslation().x = 15f;
		sphere2.getLocalTranslation().z += zDistance+2;
		rootNode.attachChild(sphere2);
		buoyancy.addPhysicsNode(sphere2, collisionGeometryMap);
		
		DynamicPhysicsNode sphere3 = getPhysicsSpace().createDynamicNode();
		sphere3.attachChild(new Sphere("",10,10,1.3f));
		sphere3.setMaterial(Material.WOOD);
		collisionGeometryMap.clear();
		PhysicsNode.generatePhysicsGeometry(sphere3, sphere3, true, collisionGeometryMap);
		sphere3.computeMass();//This is crucial to ensuring the buoyancy force is applied correctly
		sphere3.getLocalTranslation().y = 20;
		sphere3.getLocalTranslation().x = 15 +2;
		sphere3.getLocalTranslation().z += zDistance -2 ;
		rootNode.attachChild(sphere3);
		
		//Construct buoyant objects for this sphere
		buoyancy.addPhysicsNode(sphere3, collisionGeometryMap);*/

		DynamicPhysicsNode box;// = getPhysicsSpace().createDynamicNode();
		Box b;// = new Box("",new Vector3f(), 3f,3,5f);
		/*	box.attachChild(b);
		
		box.setMaterial(Material.WOOD);
		collisionGeometryMap.clear();
		PhysicsNode.generatePhysicsGeometry(box, box, true, collisionGeometryMap);
		box.computeMass();//This is crucial to ensuring the buoyancy force is applied correctly
		box.getLocalTranslation().y = height;
		box.getLocalTranslation().x = 15f;
		box.getLocalTranslation().z += zDistance;
		//box.getLocalRotation().fromAngleNormalAxis(FastMath.PI*0.5f, Vector3f.UNIT_X);
		box.getLocalRotation().fromAngleNormalAxis(FastMath.PI*1f+ FastMath.PI/4.1f, Vector3f.UNIT_Z);
		//box.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(FastMath.PI/4f, new Vector3f(1,0,0)));
		//box.getLocalRotation().fromAngleAxis(FastMath.PI * (float) Math.random(), new Vector3f((float) Math.random(),(float) Math.random(),(float) Math.random()));
		box.setAngularVelocity(new Vector3f(0,0,1));
		AlphaState alpha = display.getRenderer().createAlphaState();
		alpha.setBlendEnabled(true);
		alpha.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		alpha.setDstFunction(AlphaState.SB_ONE_MINUS_SRC_ALPHA);
		alpha.setTestEnabled(true);		
		alpha.setTestFunction(AlphaState.TF_GREATER);		
		
		MaterialState waterColor = display.getRenderer().createMaterialState();
		waterColor.setSpecular(new ColorRGBA(0.2f,0.7f,0.7f,0.3f));
		waterColor.setAmbient(new ColorRGBA(0.2f,0.7f,0.7f,0.3f));
		waterColor.setDiffuse(new ColorRGBA(0.2f,0.7f,0.7f,0.3f));		
		waterColor.setShininess(30);
		waterColor.setColorMaterial(MaterialState.CM_NONE );
		waterColor.setMaterialFace(MaterialState.MF_FRONT_AND_BACK);
		
		b.setRenderState(alpha);
		b.setRenderState(waterColor);
		b.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		
	
		rootNode.attachChild(box);
		b.updateRenderState();
		box.updateRenderState();
		rootNode.updateRenderState();
		
	
	
		buoyancy.addPhysicsNode(box, collisionGeometryMap);
	
	*/
		box = getPhysicsSpace().createDynamicNode();
		b = new Box("",new Vector3f(), 5f,1.1f,5f);
		box.attachChild(b);
		b.getLocalTranslation().set(5,5,5);
		box.setMaterial(Material.WOOD);
		collisionGeometryMap.clear();
		PhysicsNode.generatePhysicsGeometry(box, box, true, collisionGeometryMap);
		box.computeMass();//This is crucial to ensuring the buoyancy force is applied correctly
		box.getLocalTranslation().y = height+5f;
		box.getLocalTranslation().x = -15f;
		box.getLocalTranslation().z += zDistance;
	//	box.getLocalRotation().fromAngleNormalAxis(FastMath.PI/2.3f, Vector3f.UNIT_Z);
	//	box.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(FastMath.PI/4f, new Vector3f(1,0,0)));
		
		
		float a =2.4554831f;
		float b2 = 0.865637f;
		float c =0.28094327f;
		float d=0.49204808f;
		
	/*	float a =2.4554831f;
		float b2 = (float) Math.random();
		float c =(float) Math.random();
		float d=(float) Math.random();
		*/
	/*	float a = 2.5770442f;//FastMath.PI * (float) Math.random();
		float b2 = 0.038056172f;//(float) Math.random();
		float c = 0.48872367f;//(float) Math.random();
		float d= 0.37676573f;//(float) Math.random();*/
//		System.out.println(a + "\t" + b2 + "\t" + c + "\t" + d);
	//	box.getLocalRotation().fromAngleAxis(a, new Vector3f(b2 ,c,d));
		box.getLocalRotation().fromAngleAxis(FastMath.PI/2f, Vector3f.UNIT_Z);
		
		AlphaState alpha = display.getRenderer().createAlphaState();
		alpha.setBlendEnabled(true);
		alpha.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		alpha.setDstFunction(AlphaState.SB_ONE_MINUS_SRC_ALPHA);
		alpha.setTestEnabled(true);		
		alpha.setTestFunction(AlphaState.TF_GREATER);		
		
		MaterialState waterColor = display.getRenderer().createMaterialState();
		waterColor.setSpecular(new ColorRGBA(0.2f,0.7f,0.7f,0.3f));
		waterColor.setAmbient(new ColorRGBA(0.2f,0.7f,0.7f,0.3f));
		waterColor.setDiffuse(new ColorRGBA(0.2f,0.7f,0.7f,0.3f));		
		waterColor.setShininess(30);
		waterColor.setColorMaterial(MaterialState.CM_NONE );
		waterColor.setMaterialFace(MaterialState.MF_FRONT_AND_BACK);
		
		b.setRenderState(alpha);
		b.setRenderState(waterColor);
		b.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
	
		rootNode.attachChild(box);
		b.updateRenderState();
		box.updateRenderState();
		rootNode.updateRenderState();
		
	
	
		buoyancy.addPhysicsNode(box, collisionGeometryMap);
        box.addController( new RotatingController( box,box ) );
	/*	
		DynamicPhysicsNode oddShape = getPhysicsSpace().createDynamicNode();
		Box b2 = new Box("",new Vector3f(), 5f,0.5f,0.5f);
		oddShape.attachChild(b2);
		
		Box b3 = new Box("",new Vector3f(), 1.5f,1,5);
		b3.getLocalTranslation().x = 3;
		oddShape.attachChild(b3);
		
		oddShape.setMaterial(Material.WOOD);
		collisionGeometryMap.clear();
		PhysicsNode.generatePhysicsGeometry(oddShape, oddShape, true, collisionGeometryMap);
		oddShape.computeMass();//This is crucial to ensuring the buoyancy force is applied correctly
		oddShape.getLocalTranslation().y = height;
		oddShape.getLocalTranslation().x = -15f;
		oddShape.getLocalTranslation().z += zDistance;
		oddShape.getLocalRotation().fromAngleNormalAxis(FastMath.PI/12f, new Vector3f(1,0,0));
		//box.getLocalRotation().multLocal(new Quaternion().fromAngleNormalAxis(FastMath.PI/6f, new Vector3f(0,0,1)));
	//	box.getLocalRotation().fromAngleAxis(FastMath.PI * (float) Math.random(), new Vector3f((float) Math.random(),(float) Math.random(),(float) Math.random()));
		

		b2.setRenderState(alpha);
		b2.setRenderState(waterColor);
		b2.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		
	
		rootNode.attachChild(oddShape);
		b2.updateRenderState();
		oddShape.updateRenderState();
		rootNode.updateRenderState();
		
		b3.setRenderState(alpha);
		b3.setRenderState(waterColor);
		b3.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		
	
		rootNode.attachChild(oddShape);
		b3.updateRenderState();
		oddShape.updateRenderState();
		rootNode.updateRenderState();
	
		buoyancy.addPhysicsNode(oddShape, collisionGeometryMap);
		*/
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
		rootNode.updateRenderState();		
		
		*/
	}
	/*
    
	protected void simpleRender() {
		
		super.simpleRender();
		//for (BuoyantObject object:buoyancy.getBuoyantObjects())
		//{
			//if (object.applyBalance())
				//Debugger.drawCustomBound(object.getCustomWorldBound(),DisplaySystem.getDisplaySystem().getRenderer());
		//}
	}
*/
	public static void main(String[] args) {
    	TestRotationBuoyancy app = new TestRotationBuoyancy();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }
}
