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
