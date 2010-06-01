package com.golemgame.util.pass;

import com.jme.renderer.pass.BasicPassManager;
import com.jme.system.DisplaySystem;
import com.jmex.game.state.GameState;
import com.jmex.game.state.GameStateNode;

/**
 * This is a game state manager that also allows render passes.
 * All passes are rendered after the game state is rendered; by combining this with GameStatePass,
 * game states may be rendered at any point in the renderpass queue.
 * @author Sam
 *
 */
public class PassingGameStateManager extends GameStateNode<GameState>{
	private static final PassingGameStateManager instance = new PassingGameStateManager();
	
	private final BasicPassManager passManager;

	private PassingGameStateManager() {
		super("Passing Game State Manager");
		passManager = new BasicPassManager();
		super.setActive(true);
	}

	@Override
	public void cleanup() {
		passManager.cleanUp();
		super.cleanup();
	}

	@Override
	public void render(float tpf) {
		super.render(tpf);
		passManager.renderPasses(DisplaySystem.getDisplaySystem().getRenderer());
		
	}

	@Override
	public void update(float tpf) {
		super.update(tpf);
		passManager.updatePasses(tpf);
		
	}

	public static PassingGameStateManager getInstance() {
		return instance;
	}

	public BasicPassManager getPassManager() {
		return passManager;
	}



	
}
