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
package com.golemgame.platform.windows;

import org.fenggui.binding.render.Cursor;

import com.golemgame.platform.DefaultKeyBindings;
import com.golemgame.platform.PlatformManager;
import com.jme.input.KeyInput;

public class WindowsPlatformManager extends PlatformManager {

	private DefaultKeyBindings keySettings = new DefaultKeyBindings();

	@Override
	public void init() {
		Cursor.setShowCursor(true);
		System.out.println("Detected Windows");
		super.init();
	}
	@Override
	public DefaultKeyBindings getDefaultKeyBindings() {
		return keySettings;
	}
	@Override
	public String getNameForKey(int jmeKeyCode) {
		if (jmeKeyCode == KeyInput.KEY_LWIN || jmeKeyCode == KeyInput.KEY_RWIN )
		{
			return "Windows";
		}else
			return null;
	}
	@Override
	public String getJavaLauncher() {
		return "javaw";
	}
}
