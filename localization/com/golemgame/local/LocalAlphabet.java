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
package com.golemgame.local;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import org.fenggui.util.Alphabet;

public class LocalAlphabet {
	private static  Set<Character> alphabet = new HashSet<Character>();

	public static synchronized Set<Character> getAlphabet()
	{
		char[] basicAlphabet = Alphabet.getDefaultAlphabet().getAlphabet();
		for(int i = 0;i<basicAlphabet.length;i++)
			alphabet.add(basicAlphabet[i]);
		
	/*	for (int i = 0;i<127;i++)
		{
			//add all the standard ascii characters
			//alphabet.add(( (char)i));
		}*/
		
		//force the loading of resource bundles
		ResourceBundle.getBundle("com.golemgame.local.Language");
		//now return the alphabet.
		//merge the standard alphabet with the local alphabet.
		
		return alphabet;
	}
	public synchronized static char[] getPrimitiveAlphabet()
	{
		Set<Character> a = getAlphabet();
		char[] primAlphabet = new char[a.size()];
		int i =0;
		for(Character c:a)
			primAlphabet[i++]=(char)c;
		return primAlphabet;
	}
	public static synchronized void mergeAlphabet(Collection<Character> alphabet2)
	{
		alphabet.addAll(alphabet2);
	}
	
}
