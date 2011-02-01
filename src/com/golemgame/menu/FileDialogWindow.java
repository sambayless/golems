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
package com.golemgame.menu;

import java.io.File;

import org.fenggui.Display;
import org.fenggui.composite.Window;
import org.fenggui.event.IWindowClosedListener;
import org.fenggui.event.WindowClosedEvent;
import org.fenggui.layout.BorderLayoutData;

import com.golemgame.menu.FileChooserDialog.FileDialogListener;


/**
 * A window containing a file open/save dialog.
 * To retrieve the chosen file from the dialog, attach a FileChooserDialog.FileDialogListener
 * to the FileChooserDialog.
 * @author Sam Bayless
 *
 */
public class FileDialogWindow extends Window{
	private FileChooserDialog dialog;
	public FileDialogWindow() {
		super();
		build();
	}

	public FileDialogWindow(boolean closeBtn, boolean maximizeBtn,
			boolean minimizeBtn, boolean autoClose) {
		super(closeBtn, maximizeBtn, minimizeBtn, autoClose);
		build();
	}

	public FileDialogWindow(boolean closeBtn, boolean maximizeBtn,
			boolean minimizeBtn) {
		super(closeBtn, maximizeBtn, minimizeBtn);
		build();
	}


	private void build()
	{
		Window window = this;
		 dialog =  new FileChooserDialog(null, null,true);
		 
	//	FengGUI.getTheme().setUp(window);


        window.setTitle("File Dialog");
 

       dialog.setLayoutData(BorderLayoutData.CENTER);
       window.getContentContainer().addWidget( dialog);

        window.getContentContainer().layout();
        dialog.layout();
      
        dialog.addListener(new FileDialogListener()
		{
			
			public void cancel() {
				dialog.removeListener(this);
				if (getParent() != null)
					close();		
			}

			
			public void fileSelected(final File file) {
				dialog.removeListener(this);
				if (getParent() != null)
					close();
			}
			
		});
        
        this.addWindowClosedListener(new IWindowClosedListener()
        {

			
			public void windowClosed(WindowClosedEvent windowClosedEvent) {
				dialog.cancel();
				
			}
        	
        });
	}

	/**
	 * Get the FileChooserDialog in this window. 
	 * @return
	 */
	public FileChooserDialog getDialog() {
		return dialog;
	}


	/**
	 * Convenience method to create and display a file dialog window.
	 * @param display
	 * @return
	 */
	public static FileDialogWindow displayFileDialog(Display display)
	{
			FileDialogWindow window = new FileDialogWindow(true, false, false, true);

			 window.setSize(display.getWidth()/2, display.getHeight()/2);
			 display.addWidget(window);
		
	
		return window;
	}
	
}
