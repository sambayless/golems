package test;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import com.golemgame.model.spatial.shape.BoxModel;
import com.golemgame.mvc.PropertyStore;
import com.golemgame.structural.StructuralAppearanceEffect;
import com.jme.bounding.BoundingSphere;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.SceneElement;
import com.jme.scene.shape.Box;
import com.jme.scene.state.LightState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueueManager;
import com.jmex.editors.swing.settings.GameSettingsPanel;
import com.jmex.game.StandardGame;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameStateManager;

public class TestMemory {
	
	private static Callable<Object> upd = null;
	//private static Box box = null;
	private static    int i = 0;
	   public static void main(String[] args) throws InterruptedException {
	
		   
			
			StandardGame game = new StandardGame("A Simple Test");
			// Show settings screen
			if (GameSettingsPanel.prompt(game.getSettings())) {
				// Start StandardGame, it will block until it has initialized successfully, then return
				game.start();
				
				// Create a DebugGameState - has all the built-in features that SimpleGame provides
				// NOTE: for a distributable game implementation you'll want to use something like
				// BasicGameState instead and provide control features yourself.
				final DebugGameState state = new DebugGameState();
				// Put our box in it
				
				// Add it to the manager
				GameStateManager.getInstance().attachChild(state);
				// Activate the game state
				state.setActive(true);
				 java.util.logging.Logger.getLogger("com.jme").setLevel(Level.WARNING);
				    java.util.logging.Logger.getLogger("com.jmex").setLevel(Level.WARNING);
		  
		
				
				    

			        PointLight light = new PointLight();
			        light.setDiffuse( new ColorRGBA(0.75f, 0.75f, 0.75f, 1.0f  ) );
			        light.setAmbient( new ColorRGBA( 0.75f, 0.75f, 0.75f, 1.0f ) );
			        light.setLocation( new Vector3f( -10, 20, 10 ) );
			      
			        light.setEnabled( true );


			        LightState lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
			        lightState.setEnabled( true );
			        
			        lightState.attach( light );

			        lightState.setTwoSidedLighting(false);
			        state.getRootNode().setRenderState( lightState );
			        state.getRootNode().updateWorldBound();     

					ZBufferState buf = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
			        buf.setEnabled(true);
			        buf.setFunction(ZBufferState.CF_LEQUAL);
			        
			        state.getRootNode().setRenderState(buf);
			    
			        state.getRootNode().updateRenderState();
			        state.getRootNode().updateGeometricState(0f,true);    	
				    
				    
				    
			    
				final ArrayList<BoxModel> boxes = new ArrayList<BoxModel>();
				final int numBoxes = 1000;
			    
				for(int e = 0;e<numBoxes;e++)
				{
					BoxModel box = new BoxModel(true);
					state.getRootNode().attachChild(box.getSpatial());
					/*Box box = new Box("my box", new Vector3f(0, 0, 0), 2, 2, 2);
					box.setCullMode(SceneElement.CULL_NEVER);
				   // box.setModelBound(new BoundingSphere());
				   // box.updateModelBound();
				    // We had to add the following line because the render thread is already running
				    // Anytime we add content we need to updateRenderState or we get funky effects
				
				    state.getRootNode().attachChild(box);
				    box.updateRenderState();*/
				 
				    boxes.add(box);
				}
				
				upd = new Callable<Object>(){
					
			
				public Object call() throws Exception {
					if(i++%100==0)
						System.out.println(i);
					
					for(int e = 0;e<numBoxes;e++)
					{
						boxes.get(e).getSpatial().removeFromParent();
						
					}
			
					boxes.clear();
					System.gc();
					
					for(int e = 0;e<numBoxes;e++)
					{
					
						BoxModel box = new BoxModel(true);
					
						state.getRootNode().attachChild(box.getSpatial());
						box.getSpatial().updateRenderState();
						boxes.add(box);
					/*	Box box = new Box("my box", new Vector3f(0, 0, 0), 2, 2, 2);
						   box.setModelBound(new BoundingSphere());
						    box.updateModelBound();
						box.setCullMode(SceneElement.CULL_NEVER);
						VBOInfo info = new VBOInfo();
						info.setVBOColorEnabled(true);
						info.setVBOIndexEnabled(true);
						info.setVBONormalEnabled(true);
						info.setVBOTextureEnabled(true);
						
						box.setVBOInfo(info);
						
						
					 
					    // We had to add the following line because the render thread is already running
					    // Anytime we add content we need to updateRenderState or we get funky effects
						
					    state.getRootNode().attachChild(box);
					    box.updateRenderState();
					    box.setLocks(SceneElement.LOCKED_MESH_DATA | SceneElement.LOCKED_TRANSFORMS);
					    box.updateRenderState();
					    boxes.add(box);*/
					
					}
					state.getRootNode().updateRenderState();
					
					GameTaskQueueManager.getManager().update(upd);
			
					return null;
				}
					
				};
				GameTaskQueueManager.getManager().update(upd);
				
				   
			}
	   }
}
