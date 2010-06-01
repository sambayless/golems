package com.golemgame.util.pass;

import com.jme.renderer.Renderer;
import com.jme.system.DisplaySystem;
import com.jmex.game.state.GameState;
import com.jmex.game.state.GameStateNode;

/**
 * HudManager is a singleton game state pass.
 * All game states added here will be rendered on top of other states. 
 * @author Sam
 *
 */
public class HudManager extends GameStatePass {

	private static final long serialVersionUID = 1L;
	
	private final static HudManager instance = new HudManager();
	
	public static HudManager getInstance() {
		return instance;
	}

	private final GameStateNode<GameState> gameStateNode;
	
	
	@SuppressWarnings("unchecked")
	private HudManager() {
		super(new GameStateNode<GameState>("HudManagerNode"));
		gameStateNode = (GameStateNode<GameState>) super.getGameState();

	}

	@Override
	protected void doRender(Renderer r) {
		//apparently this is very slow? Thats because this is really rendering everything in the whole scene at this point, BEFORE the hud is rendered, so that it appears on top (important for fenggui probably).
		r.renderQueue();//render and clear from the queue all items drawn up to this point
		//this will cause items drawn after this point to be drawn above previously drawn items.
		DisplaySystem.getDisplaySystem().getRenderer().clearZBuffer();
		super.doRender(r);
	}

	public void attachChild(GameState gameState)
	{
		this.gameStateNode.attachChild(gameState);
		
	}
	
	public void detachChild(GameState gameState)
	{
		this.gameStateNode.detachChild(gameState);
	}
}
