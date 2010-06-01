package test;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import com.golemgame.model.spatial.shape.BoxModel;
import com.golemgame.model.texture.TextureServer;
import com.golemgame.model.texture.TextureTypeKey;
import com.golemgame.model.texture.TextureWrapper;
import com.golemgame.model.texture.TextureTypeKey.TextureShape;
import com.golemgame.mvc.golems.AppearanceInterpreter.ImageType;
import com.jme.image.Texture;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.LightState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueueManager;
import com.jmex.editors.swing.settings.GameSettingsPanel;
import com.jmex.game.StandardGame;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameStateManager;

public class TestMemory3 {

	private static Callable<Object> upd = null;
	// private static Box box = null;
	private static int i = 0;

	public static void main(String[] args) throws InterruptedException {

		/*
		 * SaveManager.getInstance().newMachine(); MachineSpace space =
		 * StateManager.getMachineSpace(); StructuralMachine machine =
		 * space.getMachines().get(0);
		 */

		StandardGame game = new StandardGame("A Simple Test");
		// Show settings screen
		if (GameSettingsPanel.prompt(game.getSettings())) {
			// Start StandardGame, it will block until it has initialized
			// successfully, then return
			game.start();

			// Create a DebugGameState - has all the built-in features that
			// SimpleGame provides
			// NOTE: for a distributable game implementation you'll want to use
			// something like
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
			light.setDiffuse(new ColorRGBA(0.75f, 0.75f, 0.75f, 1.0f));
			light.setAmbient(new ColorRGBA(0.75f, 0.75f, 0.75f, 1.0f));
			light.setLocation(new Vector3f(-10, 20, 10));

			light.setEnabled(true);

			LightState lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
			lightState.setEnabled(true);

			lightState.attach(light);

			lightState.setTwoSidedLighting(false);
			state.getRootNode().setRenderState(lightState);
			state.getRootNode().updateWorldBound();

			ZBufferState buf = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
			buf.setEnabled(true);
			buf.setFunction(ZBufferState.CF_LEQUAL);

			state.getRootNode().setRenderState(buf);

			state.getRootNode().updateRenderState();
			state.getRootNode().updateGeometricState(0f, true);

			final ArrayList<BoxModel> boxes = new ArrayList<BoxModel>();
			final int numBoxes = 1000;

			for (int e = 0; e < numBoxes; e++) {
				BoxModel box = new BoxModel(true);
				state.getRootNode().attachChild(box.getSpatial());
				/*
				 * Box box = new Box("my box", new Vector3f(0, 0, 0), 2, 2, 2);
				 * box.setCullMode(SceneElement.CULL_NEVER); //
				 * box.setModelBound(new BoundingSphere()); //
				 * box.updateModelBound(); // We had to add the following line
				 * because the render thread is already running // Anytime we
				 * add content we need to updateRenderState or we get funky
				 * effects
				 * 
				 * state.getRootNode().attachChild(box);
				 * box.updateRenderState();
				 */

				boxes.add(box);
			}

			upd = new Callable<Object>() {

				public Object call() throws Exception {
					if (i++ % 100 == 0) {
						System.out.println(i);
					}
					Texture nText = new Texture();
					for (int i = 0;i<100;i++){
						
						/*String text= "";
						TextureTypeKey requestedTexture = new TextureTypeKey(ImageType.TEXT, 256, 256, TextureWrapper.TextureFormat.RGBA, false, TextureShape.Plane);
						requestedTexture.setText(text);
			
						Texture test = nText.createSimpleClone();
						test.setApply(Texture.AM_BLEND);
						ColorRGBA tintColor = new ColorRGBA(ColorRGBA.blue);
						test.setBlendColor(tintColor);
						
						SpatialTexture texture = new SpatialTexture(requestedTexture);
						texture.setTexture(test,0);*/
						
						String text= "";
						TextureTypeKey requestedTexture = new TextureTypeKey(ImageType.TEXT, 256, 256, TextureWrapper.TextureFormat.RGBA, false, TextureShape.Plane);
						requestedTexture.setText(text);
						TextureWrapper texture;
						//nText.createSimpleClone();
						texture = TextureServer.getInstance().getTexture(requestedTexture);
						System.out.print("");
					//	texture.setApplyMode(ApplyMode.BLEND);
						//TextureLayerEffect textEffect = new TextureLayerEffect(texture, 4);
					//	textEffect.setPrecedence(4);
						// textEffect.addUpdateListener(this);
					//	boolean changed = true;
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
