package com.golemgame.util.pass;

import com.jme.renderer.Renderer;
import com.jme.renderer.pass.Pass;
import com.jmex.game.state.GameState;

/**
 * This just wraps a game state into a pass.
 * @author Sam
 *
 */
public class GameStatePass extends Pass {

	private static final long serialVersionUID = 1L;
	
	private final GameState gameState;
	
	public GameStatePass(GameState gameState) {
		super();
		this.gameState = gameState;
	}

	@Override
	protected void doRender(Renderer r) {
		gameState.render(0);

	}

	@Override
	public void cleanUp() {
		
		gameState.cleanup();
	}

	@Override
	protected void doUpdate(float tpf) {
		gameState.update(tpf);
	}

	public GameState getGameState() {
		return gameState;
	}

}
