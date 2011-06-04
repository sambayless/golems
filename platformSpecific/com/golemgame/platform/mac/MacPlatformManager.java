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
package com.golemgame.platform.mac;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.fenggui.binding.render.Cursor;

import com.golemgame.platform.DefaultKeyBindings;
import com.golemgame.platform.PlatformManager;
import com.golemgame.states.SaveManager;
import com.golemgame.states.StateManager;
import com.jme.input.KeyInput;

public class MacPlatformManager extends PlatformManager {

	private DefaultKeyBindings keySettings = new MacKeyBindings();

	@Override
	public DefaultKeyBindings getDefaultKeyBindings() {
		return keySettings;
	}
	@Override
	public String[] getLaunchCommands() {
		return new String[]{"-d32"};
	}
	@Override
	public void init() {
		super.init();
		
		Cursor.setShowCursor(false);
		System.out.println("Detected Mac");
        try {
            Class applicationClass = Class.forName("com.apple.eawt.Application");
   
            Class applicationListenerClass = Class.forName("com.apple.eawt.ApplicationListener");
            Method addListenerMethod = applicationClass.getDeclaredMethod("addApplicationListener", new Class[] { applicationListenerClass });
           
            Object eawtApplication = applicationClass.getConstructor((Class[])null).newInstance((Object[])null);
            
            InvocationHandler macHandler = new InvocationHandler()
            {  
               
                public Object invoke (Object proxy, Method method, Object[] args) throws Throwable {
                	if (args.length < 1)
                	{
                		return null;
                	}
                	
                	Object eawtEvent = args[0];
                	
                	if (method.getName().equals("handleOpenFile"))
                	{
                		  if (eawtEvent != null) {
                              try {
                                  Method getFilenameMethod = eawtEvent.getClass().getDeclaredMethod("getFilename", (Class[])null);
                                  String filename = (String) getFilenameMethod.invoke(eawtEvent, (Object[])null);
                                  openFile(filename);
                              } catch (Exception ex) {
                                  
                              }
                          }
                		
                	       setEAWTEventHandled(eawtEvent, true);
                	}else             
                		setEAWTEventHandled(eawtEvent, false);
                    return null;
                }
                
        

				protected void setEAWTEventHandled(Object event, boolean handled) {
                    if (event != null) {
                        try {
                            Method setHandledMethod = event.getClass().getDeclaredMethod("setHandled", new Class[] { boolean.class });
                            setHandledMethod.invoke(event, new Object[] {handled });
                        } catch (Exception e) {
                        	StateManager.logError(e);  
                        }
                    }
                }
            };

            Object adapterProxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { applicationListenerClass }, macHandler);
            addListenerMethod.invoke(eawtApplication, new Object[] { adapterProxy });
                      
        } catch (ClassNotFoundException e) {
          	StateManager.logError(e);
        } catch (Exception e) {
        	  StateManager.logError(e);
         
        }
		
	}
	
    private void openFile(String filename) {
    	SaveManager.getInstance().loadAndSetMachine(new File(filename));
		
	}
	@Override
	public String getNameForKey(int jmeKeyCode) {
		if (jmeKeyCode == KeyInput.KEY_LWIN || jmeKeyCode == KeyInput.KEY_RWIN )
		{
			return "Command";
		}else
			return null;
	}
}
