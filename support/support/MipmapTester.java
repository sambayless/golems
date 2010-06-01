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
