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
