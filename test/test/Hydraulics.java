package test;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Cylinder;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.TranslationalJointAxis;
import com.jmex.physics.geometry.PhysicsBox;
import com.jmex.physics.geometry.PhysicsCylinder;
import com.jmex.physics.util.SimplePhysicsGame;

public class Hydraulics extends SimplePhysicsGame {

	@Override
	protected void simpleInitGame() {
		
		DynamicPhysicsNode node1 = super.getPhysicsSpace().createDynamicNode();
		DynamicPhysicsNode node2 = super.getPhysicsSpace().createDynamicNode();
		super.showPhysics = true;
		
		rootNode.attachChild(node1);
		rootNode.attachChild(node2);
		
		StaticPhysicsNode floor = super.getPhysicsSpace().createStaticNode();
		rootNode.attachChild(floor);
		floor.getLocalTranslation().y = -5;
		PhysicsBox box = floor.createBox("");
		box.getLocalScale().set(20,1,20);
		Box floorBox = new Box("", new Vector3f(), 10,0.5f,10);
		floor.attachChild(floorBox);
		
		Cylinder cyl1 = new Cylinder();
		cyl1 = new Cylinder("cyl1", 4, 8, 0.5f, 1f,true);
		
		Cylinder cyl2 = new Cylinder();
		cyl2 = new Cylinder("cyl2", 4, 8, 0.5f, 1f,true);
		
		cyl1.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, new Vector3f(1,0,0));
		cyl2.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI, new Vector3f(1,0,0));
		
		//cyl2.getLocalTranslation().set(0,3,0);
		
		node1.attachChild(cyl1);
		node2.attachChild(cyl2);
		node2.getLocalTranslation().y = 3;
		
		PhysicsCylinder cylGeom1 = node1.createCylinder("");

		cylGeom1.getLocalScale().set(cyl1.getRadius(), cyl1.getRadius(), cyl1.getHeight()).multLocal(cyl1.getLocalScale());

		PhysicsCylinder cylGeom2 = node2.createCylinder("");

		cylGeom2.getLocalScale().set(cyl2.getRadius(), cyl2.getRadius(), cyl2.getHeight()).multLocal(cyl2.getLocalScale());
		cylGeom1.getLocalRotation().set(cyl1.getLocalRotation());
		cylGeom2.getLocalRotation().set(cyl2.getLocalRotation());
		
		makeJoint(cyl1, cyl2, node1,node2);
		

	}
	




	
	private void makeJoint(Cylinder cyl1, Cylinder cyl2, DynamicPhysicsNode physicsNode1,DynamicPhysicsNode physicsNode2)
	{
		Joint joint = physicsNode1.getSpace().createJoint();//this already is set, but for clarity its here too
		
		
		joint.setCollisionEnabled(true);
		
	
		//node2.updateWorldVectors();
		physicsNode1.updateWorldVectors();
		//node2.physicsNode.updateWorldVectors();

        joint.attach((DynamicPhysicsNode)physicsNode1,(DynamicPhysicsNode) physicsNode2) ;
        joint.setAnchor(new Vector3f().set(physicsNode1.getLocalTranslation())); //the anchor has to be set at the center of the SECOND node
		
        
        TranslationalJointAxis axis = joint.createTranslationalAxis();	
        Vector3f dir = new Vector3f(0,1,0);
  
    	  dir.multLocal(-1);
    	  
    	  axis.setDirection( dir );//if there is rotation, this might be messed...
        

        
    	  axis.setPositionMinimum(-2f );

    	  axis.setPositionMaximum(1f );
	
	}
	
    public static void main(String[] args) {
    	Hydraulics app = new Hydraulics();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

}
