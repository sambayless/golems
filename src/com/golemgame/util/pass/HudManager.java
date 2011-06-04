/*******************************************************************************
 * Copyright 2008, 2009, 2010 Sam Bayless.
 * 
 *     This file is part of Golems.
 * 
 *     Golems is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Golems is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Golems. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
