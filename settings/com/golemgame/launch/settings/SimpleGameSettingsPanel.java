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
package com.golemgame.launch.settings;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.golemgame.states.GeneralSettings;
import com.jme.system.GameSettings;

public class SimpleGameSettingsPanel {
	private static boolean okValue = false;

	public boolean prompt(GameSettings prefs)
	{
		frame.setVisible(true);
		
		while (frame.isVisible()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if(okValue)
		{
			prefs.setFullscreen(fs.isSelected());
			prefs.setVerticalSync(vs.isSelected());
			if(bit16.isSelected())
				prefs.setDepth(16);
			else 
				prefs.setDepth(32);
			
			
			try{
				prefs.setWidth(Integer.valueOf(width.getText()));
			}catch(NumberFormatException e)
			{
				System.out.println(e.getMessage());
			}
			try{
				prefs.setHeight(Integer.valueOf(height.getText()));
			}catch(NumberFormatException e)
			{
				System.out.println(e.getMessage());
			}
			try{
				prefs.setFrequency(Integer.valueOf(freq.getText()));
			}catch(NumberFormatException e)
			{
				System.out.println(e.getMessage());
			}
		}
		frame.setVisible(false);
		return okValue;
		
	}

	public String getMaxMemory()
	{
		return (maxMemory.getText());
	}
	
	private JFrame frame;
	private JTextField maxMemory;
	private JCheckBox fs;
	private JTextField width;
	private JTextField height;
	private JCheckBox vs;
	private JTextField freq;
	private JRadioButton bit16 ;
	private JRadioButton bit32; 
	
	public SimpleGameSettingsPanel(GameSettings prefs, String reason) {
		super();
		 frame = new JFrame("Golems Display Settings");
		
		frame.setLayout(new BorderLayout());
		
		JPanel settings = new JPanel();
		frame.add(settings,BorderLayout.NORTH);
		
		if(reason!=null)
		{
			JLabel message = new JLabel();
			frame.add(message,BorderLayout.CENTER);
			message.setText(reason);
		
		}
		settings.setLayout(new GridLayout(6,2));
		
		settings.add(new JLabel("Resolution (Width,Height)"));
		JPanel res = new JPanel();
		res.setLayout(new BoxLayout(res,BoxLayout.LINE_AXIS));
		
		 width = new JTextField();
		res.add(width);
		res.add(new JLabel(" x "));
		 height = new JTextField();
		res.add(height);
		settings.add(res);
		
		/*Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		width.setText(String.valueOf( size.width));
		height.setText(String.valueOf( size.height));*/
		
		
		 fs = new JCheckBox("FullScreen");
		settings.add(fs);
		settings.add(new JLabel(""));
		
		 vs = new JCheckBox("VSync");
		settings.add(vs);
		settings.add(new JLabel(""));
		
		ButtonGroup bitGroup = new ButtonGroup();

		 bit16 = new JRadioButton("16 Bit Colour");
		 bit32 = new JRadioButton("32 Bit Colour");
		
		bitGroup.add(bit32);
		bitGroup.add(bit16);
		
		settings.add(bit16);
		settings.add(bit32);
		
		settings.add(new JLabel("Frequency"));
		 freq = new JTextField("60");
		settings.add(freq);
		
		settings.add(new JLabel("Maximum Memory (MB)"));
		maxMemory = new JTextField();
		settings.add(maxMemory);
		
		maxMemory.setText(String.valueOf(GeneralSettings.getInstance().getMaxMemory().getValue()));

		
		
		vs.setSelected(prefs.isVerticalSync());
		fs.setSelected(prefs.isFullscreen());
		freq.setText(String.valueOf(prefs.getFrequency()));
		if(prefs.getDepth()== 16)
		{
			bit16.setSelected(true);
		}else
			bit32.setSelected(true);
		
		width.setText(String.valueOf(prefs.getWidth()));
		height.setText(String.valueOf(prefs.getHeight()));
		
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				okValue = true;
				frame.setVisible(false);
			}			
		});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				okValue = false;
				frame.setVisible(false);
			}			
		});
		JPanel okCancel = new JPanel();
		okCancel.setLayout(new FlowLayout());
		okCancel.add(ok);
		okCancel.add(cancel);
		frame.add(okCancel,BorderLayout.PAGE_END);
		settings.doLayout();
		okCancel.doLayout();
		frame.setResizable(false);
		frame.doLayout();
	//	frame.getContentPane().setSize(frame.getContentPane().getMinimumSize());
		
	//	frame.setSize(frame.getMinimumSize());
		frame.pack();
		
		
	}
	
	
	
}
