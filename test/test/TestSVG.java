package test;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.swing.JFrame;

import org.fenggui.util.SVGImageFactory;


import com.golemgame.util.SVGImageLoader;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGUniverse;

public class TestSVG {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AppFrame app = new AppFrame();
		app.setVisible(true);
	}

	private static class AppFrame extends JFrame
	{
	
		private ImageCanvas imageCanvas;
		public AppFrame() throws HeadlessException {
			super();

			imageCanvas= new ImageCanvas();
			this.setSize(600,600);
			this.getContentPane().add(imageCanvas,BorderLayout.CENTER);
	    	

		    URL svgz =	TestSVG.class.getClassLoader().getResource( "test/Checker3.svgz");
			try{
			    InputStream input = new java.util.zip.GZIPInputStream(new BufferedInputStream(svgz.openStream()));
			//    SVGImageFactory g;
			//	BufferedImage image  =createSVGImage(input,"stripes",128,128);//SVGImageLoader.loadSVG(input,"Stripes", 128,128);
			    BufferedImage image  =SVGImageLoader.loadSVG(input,"Stripes", 512,512);
				
			    imageCanvas.setImage(image);
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			

			
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			//BufferedImage image = new BufferedImage();
			
			
		}
	
		
		
	};
	
	
	private static class ImageCanvas extends Canvas
	{
		private BufferedImage image;

		public ImageCanvas() {
			super();
	
		}

		@Override
		public void paint(Graphics g) {
		//	g.clearRect(getX(), getY(), getWidth(), getHeight());
			g.setColor(Color.BLUE);
			g.fillRect(getX(), getY(), getWidth(), getHeight());
			if(this.image != null)
				g.drawImage(image,0,0,null);
		}

		public BufferedImage getImage() {
			return image;
		}

		public void setImage(BufferedImage image) {
			this.image = image;
			this.setSize(image.getWidth()*2, image.getHeight()*2);
		}
		
		
	}
	public static BufferedImage createSVGImage(InputStream fis,String name, int width, int height) throws Exception
	{

	
		SVGUniverse universe = new SVGUniverse();
		URI svg = universe.loadSVG(fis,name);
		SVGDiagram diagram = universe.getDiagram(svg);
		diagram.setIgnoringClipHeuristic(true);
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = image.createGraphics();
		//g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//g.transform(getAffineTransform((int)diagram.getWidth(), (int)diagram.getHeight(), width, height));
		diagram.render(g);
		//g.dispose();
		return image;
	}
	
	private static AffineTransform getAffineTransform(int srcWidth, int srcHeight, int destWidth, int destHeight)
	{
		double scaleX = (double)destWidth / (double)srcWidth;
		double scaleY = (double)destHeight / (double)srcHeight;
		double scale = Math.min(scaleX, scaleY);
		return AffineTransform.getScaleInstance(scale, scale);
	}
	
}
