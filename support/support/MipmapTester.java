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
package support;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class MipmapTester {
	
	public static void main(String[] args) throws IOException {
		JFrame frame = new JFrame();
		
		
		
		
		MipMapMaker maker = new MipMapMaker();
		
		final BufferedImage mipmap = maker.generateMipmap("C:/Users/Sam/GlassIcons/Mipmap/GlassBlue");
		
		
		JLabel pic = new JLabel()
		{

			@Override
			protected void paintComponent(Graphics g) {
				g.drawImage(mipmap,0,0,null);
			}

		
			
		};
		
		frame.add(pic,BorderLayout.CENTER);
		frame.layout();
		
		frame.setVisible(true);
		frame.repaint();
	}
}
