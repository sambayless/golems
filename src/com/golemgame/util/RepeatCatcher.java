package com.golemgame.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.Timer;

public class RepeatCatcher implements KeyListener {

	private Set<KeyListener> listeners = new HashSet<KeyListener>();
	
	private long[] keyPressStatus = new long[255];
	private Map<Integer, KeyEvent> releases = new HashMap<Integer,KeyEvent>();
	
	private final long MIN_TIME = 50000000;//100 ms
	private ActionListener timerListener = new ActionListener()
	{
		public void actionPerformed(ActionEvent arg0) {
			//fire pending release events
			for (int key:releases.keySet())
			{
		//		System.out.println("release");
				keyPressStatus[key] = 0;
				for (KeyListener listener:listeners.toArray(new KeyListener[listeners.size()]))
					listener.keyReleased(releases.get(key));
			}
			releases.clear();
		}		
	};
	
	private Timer timer = new Timer(250,timerListener);
	
	public RepeatCatcher() 
	{
	
		for (int n= 0; n < keyPressStatus.length; n++)
			keyPressStatus[n] = 0;//init key status to 0
		//timer.setInitialDelay(200000000/1000000);
		timer.setInitialDelay((int) MIN_TIME/1000000);
		timer.setRepeats(false);
		
	}
	public boolean addKeyListener(KeyListener listener)
	{
		return listeners.add(listener);
	}
	
	public boolean removeKeyListener(KeyListener listener)
	{
		return listeners.remove(listener);
	}
	
	//have to check if this is autorepeating before executing keypress event.
	//after itnercepting the release
	//check if key press is called within short time period
	//if not, then fire the release after all

	
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
	//	System.out.print("pressing..");
		long cTime = System.nanoTime();
		if (keyPressStatus[key] == 0)
		{	
			//System.out.println("press " + key);
			keyPressStatus[key] =cTime;
			KeyListener[] temp = new KeyListener[listeners.size()];
			for (KeyListener listener:listeners.toArray(temp))
				listener.keyPressed(e);
			
		
		}else if (cTime <= keyPressStatus[key] + MIN_TIME)
		{//stop the timer - this appears to be an autorepeat
			releases.remove(key);//find any entry corresponding to this key, and remove it from the set
			keyPressStatus[key] = cTime;
			
		}
	}

	
	public void keyReleased(KeyEvent e) 
	{
		int key = e.getKeyCode();
		long cTime = System.nanoTime();
		
		if (cTime >= keyPressStatus[key] + MIN_TIME)
		{
		//	System.out.println("release " + keyPressStatus[key] + "\t" + cTime);
			keyPressStatus[key] = 0;	
			for (KeyListener listener:listeners.toArray(new KeyListener[listeners.size()]))
				listener.keyReleased(e);
			releases.remove(key);
		}
		else
		{
			keyPressStatus[key] = cTime;
			//create timer here
			releases.put(key,e);
			timer.restart(); 
			
		}
	}

	
	public void keyTyped(KeyEvent e) 
	{		
		
		//Do NOT forward type events to the listener
	}
	


}
