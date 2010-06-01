package com.golemgame.local;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.zip.GZIPInputStream;

import com.golemgame.states.StateManager;


/**
 * This resource bundle will also check its parent for resources.
 * All bundles support exactly the keys that their parents support, unless the parent is null,
 * in which case they support their own keys.
 * If a bundle is missing a key or resource, it passes the request up to its parent.
 * @author Sam
 *
 */
public class Language extends ResourceBundle{
	private Map<String,String> dictionary = new HashMap<String,String>();
	public final static String DICTIONARY_EXT = ".dct";
	public final static String ALPHABET_EXT = ".alp";
	public final static String BASE = "com/golemgame/local/dct/";
	public final static String ALPHABET_BASE = "com/golemgame/local/alphabet/";
	//public final static String DICTIONARY_PACKAGE = "/dct/";
	PropertyResourceBundle b;
	private String language;
	@Override
	public String toString() {
		return language;
	}

	public Language() {
		super();
		//find the descriptor file
		language =  this.getClass().getSimpleName();
		
		//find the alphabet(do this first)
		try{
			InputStream input = StateManager.loadResource(ALPHABET_BASE + language + ALPHABET_EXT).openStream();
			if(input == null)
			{
				StateManager.logError(new Throwable("Missing alphabet: " + language));
			}else{
				DataInputStream alphaIn = new DataInputStream(new GZIPInputStream(new BufferedInputStream(input)));
				Collection<Character> alphabet = new ArrayList<Character>();
				try{
					//load the resource
					while(true)
					{		
						char c = alphaIn.readChar();
						if(! Character.isLowSurrogate(c) && ! Character.isHighSurrogate(c))
							alphabet.add(c);
						/*String key = alphaIn.readUTF();

						if(key!=null && key.length() == 1)
						{
							alphabet.add(key);
						}*/
					}
					}catch(EOFException e)
					{
						//this is the correct behaviour.
						//StateManager.logError(e);
					}catch(UTFDataFormatException e)
					{
						StateManager.logError(e);
					}
				if(alphabet.size()>0)//avoid unnecessary synchronization
				{
					LocalAlphabet.mergeAlphabet(alphabet);
				}
				
			}
		}catch(IOException e)
		{
			//fail quitely
			StateManager.logError(e);
		}
		
		//find the corresponding language descriptor
		
		try{
		InputStream input = StateManager.loadResource(BASE + language + DICTIONARY_EXT).openStream();
		if(input == null)
		{
			StateManager.logError(new Throwable("Missing translation: " + language));
		}else{
			DataInputStream dataInput = new DataInputStream(new GZIPInputStream( new BufferedInputStream(input)));
			try{
			//load the resource
			while(true)
			{				
				String key = dataInput.readUTF();
				String entry = dataInput.readUTF();
				
				if(key!=null && entry != null && entry.length()>0)
					dictionary.put(key, entry);
			}
			}catch(EOFException e)
			{
				//this is the correct behaviour.
				//StateManager.logError(e);
			}catch(UTFDataFormatException e)
			{
				StateManager.logError(e);
			}
		}
		}catch(IOException e)
		{
			//fail quitely
			StateManager.logError(e);
		}
	//	int pos = language.lastIndexOf(".");
	//	language = language.substring(pos+1);
		System.out.println(language);
	}

	@Override
	public Enumeration<String> getKeys() {
		return Collections.enumeration(dictionary.keySet());
	}

	@Override
	protected Object handleGetObject(String key) {
		return dictionary.get(key);
	}
	
	
	
}
