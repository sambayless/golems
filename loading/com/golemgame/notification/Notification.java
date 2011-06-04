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
package com.golemgame.notification;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class Notification {
	
	/**
	 * Note: this class is compiled separately, and included in the class path under the loading folder.
	 * @param args
	 */
	public static void main(String[] args) {
		
		String title = "Unexpected Error";
		String message = "An unexpected error has caused the program to crash";
		
		
		
		if (args.length>=1)
		{
			message = args[0];
		}
		
		if (args.length>=2)
			title = args[1];
			
		
		//JOptionPane.showMessageDialog(null,message,title, JOptionPane.ERROR_MESSAGE);
		
		final JOptionPane optionPane = new JOptionPane(
               message,
                JOptionPane.ERROR_MESSAGE,
                JOptionPane.DEFAULT_OPTION);


		
	
		final JDialog dialog = new JDialog((Frame)null, 
                title,
                true);
		
		optionPane.addPropertyChangeListener(
			    new PropertyChangeListener() {
			        public void propertyChange(PropertyChangeEvent e) {
			            String prop = e.getPropertyName();

			            if (dialog.isVisible() 
			             && (e.getSource() == optionPane)
			             && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
			                //If you were going to check something
			                //before closing the window, you'd do
			                //it here.
			            	System.exit(0);
			            }
			        }
			    });

		
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE );
		
		dialog.setContentPane(optionPane);

		dialog.pack();
		
		Timer timer = new Timer(500, new ActionListener()
		{

			
			public void actionPerformed(ActionEvent e) {
				
				dialog.setAlwaysOnTop(true);
				
			}
			
		});
		timer.start();
		dialog.setVisible(true);

		System.exit(0);
	
		
	}

	
}
