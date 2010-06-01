package support;

import jmetest.game.TestStandardGame;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jmex.editors.swing.settings.GameSettingsPanel;
import com.jmex.game.StandardGame;
import com.jmex.game.state.GameStateManager;
import com.jmex.physics.DynamicPhysicsNode;


/**
 * A demonstration of how to use the new MultithreadedPhysicsGameState.
 * @author Sam Bayless
 *
 */
public class TestMultithreadedPhysics extends TestStandardGame{

	public static void main(String[] args) throws Exception {
		// Instantiate StandardGame		
		StandardGame game = new StandardGame("A Simple Test");
		
		if (GameSettingsPanel.prompt(game.getSettings())) {
		
			game.start();
			
			PhysicsMultithreadedGameState state = new PhysicsMultithreadedGameState("PhysicsGame",game);
			
			//Add a non physical component:
		
			Box box = new Box("my box", new Vector3f(0, 0, 0), 2, 2, 2);
			box.getLocalTranslation().x = 5;
		    box.setModelBound(new BoundingBox());
		    box.updateModelBound();
	
		    box.updateRenderState();
		    state.getRootNode().attachChild(box);//add the non physical component to the normal root
		    
		    
		   DynamicPhysicsNode physicsNode =   state.getPhysicsSpace().createDynamicNode();
		   physicsNode.createSphere("").getLocalScale().set(2.5f,2.5f,2.5f);
		   Sphere sphere = new Sphere("",30,30,2.5f);
		   physicsNode.attachChild(sphere);
		   physicsNode.getLocalTranslation().x = -5;
			
		   physicsNode.updateRenderState();
		   physicsNode.setModelBound(new BoundingBox());
		   physicsNode.updateModelBound();
		   
		   state.getPhysicsRoot().attachChild(physicsNode);//attach the physics node to the physics root, so it will only be updated when the physics is updated
		   
			GameStateManager.getInstance().attachChild(state);
			
			state.setActive(true);
			state.setPhysicsEnabled(true);
		}
	}
}
