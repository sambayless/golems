package com.golemgame.model.texture.spatial;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;

import com.golemgame.model.texture.Images;
import com.golemgame.model.texture.TextureServer;
import com.golemgame.model.texture.TextureTypeKey;
import com.golemgame.model.texture.TextureWrapper;
import com.golemgame.mvc.golems.AppearanceInterpreter.ImageType;
import com.golemgame.states.StateManager;
import com.golemgame.util.SquareTextTextureBuilder;
import com.golemgame.util.loading.openGL.SlowLoader;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

public class SpatialTextureServer extends TextureServer{

	//should use SOFT references here probably.
	private final Map<TextureTypeKey, Texture> keyMap = new HashMap<TextureTypeKey, Texture> ();
	
	private Lock keyMapLock = new ReentrantLock();
	
	//private Lock loadingLock = new ReentrantLock();
	
	private final Set<TextureTypeKey> loadingKeys = new HashSet<TextureTypeKey>();

	
	
	public SpatialTextureServer() {
		super();

	}

	@Override
	public int getLoadedTextureID(TextureTypeKey textureClass){
		keyMapLock.lock();
		try{
			Texture t = keyMap.get(textureClass);
			if(t == null)
				return 0;
			
			if(t.getTextureId()!=0)
				return t.getTextureId();
		
			//first, check if the texture is already loaded.
				TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
				ts.setTexture(t);
				ts.load();
						
				if (t.getTextureId()>0)		
					return t.getTextureId();
				
		

			return 0;
			
	
		}finally{
			keyMapLock.unlock();
		}
	}
	
	@Override
	public Texture getLoadedTexture(TextureTypeKey textureClass) {		
		keyMapLock.lock();
		try{
			Texture t = keyMap.get(textureClass);
			if(t == null)
				return null;
			return t.createSimpleClone();
		}finally{
			keyMapLock.unlock();
		}
	
	}



	protected TextureWrapper makeTextureWrapper(TextureTypeKey textureClass) 
	{
		return new SpatialTexture(textureClass);
	}



	@Override
	public void loadTexture(final TextureTypeKey textureClass) {
		
		super.lock();
		try{
			keyMapLock.lock();
			try{
				
				if(super.isLoaded(textureClass))
					return;//this condition shouldn't ever occur, but just to be safe we will check.
				
				if ((keyMap.get(textureClass)) != null)
				{		
				
						super.notifyIsLoaded(textureClass);//this condition shouldn't ever occur, but just to be safe we will check.
						return;
					
				}
			
				if(loadingKeys.contains(textureClass))
					return;//already queued for loading.
		
				loadingKeys.add(textureClass);
				
				super.execute(new Runnable()//dont worry, this is a limited thread pool
				{
					public void run()
					{
						try{
						
	 						Texture texture = null;
							
	 						//if this is a text texture, process it separately:
	 						if(textureClass.getImage() == ImageType.TEXT)
	 						{
	 							String text = textureClass.getText();
	 							if(text == null)
	 								text = "";//we dont need to load or display this, could optimize later...
	 							if(text.length() == 0)
	 							{
	 								texture = new Texture();
	 							}else
	 								texture = SquareTextTextureBuilder.buildSquareText(text);
	 							
	 						}else if (textureClass.getImage() == null ||textureClass.getImage() == ImageType.VOID )
	 						{	
	 							texture = new Texture();
							}else{
								final InputStream textureStream =  Images.getInstance().getInputStream(textureClass.getImage(),textureClass.getShape(),textureClass.getElementNumber());
								final URL textureURL = Images.getInstance().getURL(textureClass.getImage(),textureClass.getShape(),textureClass.getElementNumber());
								if (textureURL == null)
									throw new NoTextureException("No url");
								/*	File file = new File(textureURL.getPath());
								if (file == null || ! file.exists())
									throw new NoTextureException("No file");
								
								String fileName = file.getName();
								
								 int dot = fileName.lastIndexOf('.')+1;
							     String fileExt = (dot >= 0 && dot < fileName.length() )? fileName.substring(dot) : "";
								
							     boolean isSVG = false;*/
							  //   InputStream input = null;
							     //if the file extension is SVG, handle here, otherwise, attempt to load the texture through JME
							     
							    	 
							     {//If the image is NOT an svg image, use the built in java io...
							         try{
							        
							        	 	final BufferedImage  dataImage = ImageIO.read(textureStream);
								    	 	final Image textureImage =  TextureManager.loadImage(dataImage, false);
								    	 	
								      //     final TextureKey tkey = new TextureKey(textureURL, true, Image.GUESS_FORMAT);

								   
									    		texture = SlowLoader.getInstance().queue(new Callable<Texture>()
								    			 {

													
													public Texture call() throws Exception {
														Texture texture = new Texture();
														
														
														
														if(Images.getInstance().suppliesMipmap(textureClass.getImage(), textureClass.getShape()))
														{	
															textureImage.setWidth(dataImage.getWidth());
															textureImage.setHeight(dataImage.getWidth());
															textureImage.setMipMapSizes(new int[]{512*512*4, 256*256*4, 128*128*4, 64*64*4,32*32*4,16*16*4,8*8*4,4*4*4,2*2*4,1*4});	
														}else{
															textureImage.setWidth(dataImage.getWidth());
															textureImage.setHeight(dataImage.getHeight());
														}
														//if(textureClass.useMipmaps())
														//	
															//if(!textureClass.useMipmaps())
															//	texture.setScale(new Vector3f(1,-1,1));
																	
														 texture.setAnisoLevel(0);
														 texture.setMipmapState( Texture.MM_LINEAR);
														 texture.setFilter(Texture.FM_LINEAR);
														 texture.setImage(textureImage);
														 return texture;
													
											           // return TextureManager.loadTexture(null, tkey, jmeImage,(textureClass.useMipmaps()? Texture.MM_LINEAR_LINEAR:Texture.MM_NONE), Texture.FM_LINEAR, 0f);
													}
								    			 }).get();
														
								    	}catch(ExecutionException e)
								    	{
								    		throw new NoTextureException(e);
								    	}catch(CancellationException  e)
								    	{
								    		throw new NoTextureException(e);
								    	}catch(InterruptedException  e)
								    	{
								    		throw new NoTextureException();
								    	} catch (IOException e) {
								    		throw new NoTextureException(e);
										}
							    	
							     }
	 						}
							
							if (texture == null)
							{
								throw new NoTextureException("Failed to load texture");
								
							}
							
							final Texture text = texture;
							StateManager.getGame().executeInGL(new Callable<Object>(){

								public Object call() throws Exception {
									TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
									ts.setTexture(text);
									ts.load();
									return null;
								}
								
							});
							
							SpatialTextureServer.this.lock();
							try{
								keyMapLock.lock();
								try{
							
									keyMap.put(textureClass, texture);
									loadingKeys.remove(textureClass);//the class is no longer being loaded.
									notifyIsLoaded(textureClass);
								}finally
								{
									keyMapLock.unlock();
								}
							}finally
							{
								SpatialTextureServer.this.unlock();
							}
							
						
							
							return;
							
						}catch(NoTextureException e)
						{
							StateManager.logError(e);
							notifyIsLoaded(textureClass);//dont attempt to load a texture twice if it fails for some reason
						} catch (IOException e) {
							StateManager.logError(e);
							notifyIsLoaded(textureClass);//dont attempt to load a texture twice if it fails for some reason
						} catch (Exception e) {
							StateManager.logError(e);
							notifyIsLoaded(textureClass);//dont attempt to load a texture twice if it fails for some reason

						}finally{
							
						}
					}
				});
				
			
			
			}finally{
				keyMapLock.unlock();
			}
		}finally{
			super.unlock();
		}
		
	}



	
	/*
	
	public void loadTextureWrapper(TextureWrapper textureWrapper)
			throws NoTextureException {
		if (!(textureWrapper instanceof SpatialTexture))
			throw new NoTextureException("Wrong type");
		final SpatialTexture spatialTexture = (SpatialTexture) textureWrapper;
		
		super.loadTextureWrapper(textureWrapper);
		
		boolean allLoaded = true;
		
		for(int element = 0;element<spatialTexture.getElements();element++)
		{
			if(!spatialTexture.isLoaded(element))
			{
				final TextureTypeKey textureClass = spatialTexture.getTextureTypeKey(element);
				//right now, this will get called 50 million times as the texture repeatedly tries to load.
			
					
		 
				
				if (textureClass == null || textureClass.getImage() == ImageType.VOID || (textureClass.getImage() == ImageType.TEXT && (textureClass.getText()==null || textureClass.getText().length()==0)))
				{//this counts as loaded.
					super.textureLoaded(textureClass);
					continue;
				}
				spatialTexture.setTexture(new Texture(),textureClass.getElementNumber());
				super.textureLoaded(textureClass);
			
				loadingLock.lock();
				try{
					keyMapLock.lock();
					try{
						if (loadingKeys.contains(textureClass))
							continue;//were already trying to load this texture.
				
						Texture texture = getTextureIfLoaded(textureClass);
						
						if (texture != null)
						{//first, check if the texture is already loaded.
							spatialTexture.setTexture(texture,textureClass.getElementNumber());
							super.textureLoaded(textureClass);
							continue;
						}else
						{	
							allLoaded = false;
				
							loadingKeys.add(textureClass);
							
			
							loadTexture(textureClass);
						}
					}finally
					{
						keyMapLock.unlock();
					}
				}finally
				{
					loadingLock.unlock();
				}
			}
		}
		
	}
	
	
	public boolean isLoaded(TextureTypeKey typeKey) {
		if(getTextureIfLoaded(typeKey)!= null)
			return true;
		return false;
	}


	
	public int loadTextureID(TextureTypeKey textureClass)  throws NoTextureException{
		
		//right now, this will get called 50 million times as the texture repeatedly tries to load.
		if (textureClass == null || textureClass.getImage() == ImageType.VOID)
			return 0;
		
		loadingLock.lock();
		try{
			keyMapLock.lock();
			try{
				if (loadingKeys.contains(textureClass))
					return 0;//were already trying to load this texture.
				Texture texture = getTextureIfLoaded(textureClass);
				//if (texture == null)
				//	texture = singleThreadedLoad(textureClass);
				if (texture != null)
				{//first, check if the texture is already loaded.
					TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
					ts.setTexture(texture);
					ts.load();
					
					if (texture.getTextureId()>0)		
						return texture.getTextureId();
					throw new NoTextureException("Texture ID did not load");
				}
				

				
				loadingKeys.add(textureClass);
				
				loadTexture(textureClass);
					
			}finally
				{
					keyMapLock.unlock();
				}
		}finally
		{
			loadingLock.unlock();
		}

		return 0;
	}
	
	private Texture singleThreadedLoad(TextureTypeKey textureClass) throws NoTextureException
	{
		Texture texture = null;
		
		
			if ((texture=keyMap.get(textureClass)) != null)
			{
				return texture;
			
			}
			
		final URL textureURL = Images.getInstance().getURL(textureClass.getImage(),textureClass.getShape(),textureClass.getElement());
		if (textureURL == null)
			throw new NoTextureException("No url");
		File file = new File(textureURL.getPath());
		if (file == null || ! file.exists())
			throw new NoTextureException("No file");
		
		String fileName = file.getName();
		
		 int dot = fileName.lastIndexOf('.')+1;
	     String fileExt = (dot >= 0 && dot < fileName.length() )? fileName.substring(dot) : "";
		
	     boolean isSVG = false;
	     InputStream input = null;
	     //if the file extension is SVG, handle here, otherwise, attempt to load the texture through JME
	     if ("SVG".equalsIgnoreCase(fileExt))
	     {
	    	 try{
				    input = (new BufferedInputStream(textureURL.openStream()));
	    	 }catch(IOException e)
	    	 {
	    		 
	    	 }
	    	 isSVG=true;
	    }else if ("SVGZ".equalsIgnoreCase(fileExt))
		 {
					try{
					
					    input = new java.util.zip.GZIPInputStream(new BufferedInputStream(textureURL.openStream()));
					    
					
					}catch(IOException e)
					{
						 throw new NoTextureException(e);
					}
					isSVG = true;
		 }
	    if (isSVG && input!= null)
	    {
	    	try{
		        SVGDiagram diagram= SVGImageLoader.loadSVGDiagram(input,Images.getInstance().getName(textureClass.getImage()));
			    
	    	 	final Image jmeImage =  SVGImageLoader.loadSVG(diagram, textureClass.getTextureWidth(), textureClass.getTextureHeight(), textureClass.getFormat() == TextureWrapper.TextureFormat.RGBA?true:false, textureClass.useMipmaps());

	           final TextureKey tkey = new TextureKey(textureURL, true, Image.GUESS_FORMAT);
		 
	            
	  
						       
				            texture = TextureManager.loadTexture(null, tkey, jmeImage, Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, 0f);
				    	
			
	       	}catch(IOException e)
	    	{
	    		throw new NoTextureException();
	    	}
	    }else
	    	 
	     {
		    	
							texture=  TextureManager.loadTexture(textureURL);
				
	    	
	     }

		
		if (texture == null)
		{
			throw new NoTextureException("Failed to load texture");
			
		}
		
		return texture;

	}
	
	private Texture getTextureIfLoaded(TextureTypeKey textureClass)
	{
		Texture texture = null;
	
		if ((texture=keyMap.get(textureClass)) != null)
		{
			
			//if cloning is required in the future, then mipmaps must be pregenerated for efficiency (apparently, the are regenerated after cloning...)
			texture =  texture.createSimpleClone();
	
		
		}
		return texture;

	}

	*//**
	 * Load the texture concurrently.
	 * To be informed when the texture is loaded, register as a LoadingListener.
	 * @param textureClass
	 * @throws NoTextureException
	 *//*
	private void loadTexture(final TextureTypeKey textureClass) throws NoTextureException
	{
	
		
		keyMapLock.lock();
		try{
			if ((keyMap.get(textureClass)) != null)
			{		
			
					super.textureLoaded(textureClass);
					return;
				
			}
			
			

		
			super.execute(new Runnable()//dont worry, this is a limited thread pool
			{
				public void run()
				{
					try{
					
 						Texture texture = null;
						
 						//if this is a text texture, process it separately:
 						if(textureClass.getImage() == ImageType.TEXT)
 						{
 							String text = textureClass.getText();
 							if(text == null)
 								text = "";//we dont need to load or display this, could optimize later...
 							if(text.length() == 0)
 							{
 								texture = new Texture();
 							}else
 								texture = SquareTextTextureBuilder.buildSquareText(text);
 							
 						}else{
							final InputStream textureStream =  Images.getInstance().getInputStream(textureClass.getImage(),textureClass.getShape(),textureClass.getElementNumber());
							final URL textureURL = Images.getInstance().getURL(textureClass.getImage(),textureClass.getShape(),textureClass.getElementNumber());
							if (textureURL == null)
								throw new NoTextureException("No url");
								File file = new File(textureURL.getPath());
							if (file == null || ! file.exists())
								throw new NoTextureException("No file");
							
							String fileName = file.getName();
							
							 int dot = fileName.lastIndexOf('.')+1;
						     String fileExt = (dot >= 0 && dot < fileName.length() )? fileName.substring(dot) : "";
							
						     boolean isSVG = false;
						  //   InputStream input = null;
						     //if the file extension is SVG, handle here, otherwise, attempt to load the texture through JME
						     
						    	 
						     {//If the image is NOT an svg image, use the built in java io...
						         try{
						        
						        	 	final BufferedImage  dataImage = ImageIO.read(textureStream);
							    	 	final Image textureImage =  TextureManager.loadImage(dataImage, false);
							    	 	
							      //     final TextureKey tkey = new TextureKey(textureURL, true, Image.GUESS_FORMAT);
						
							   
								    		texture = SlowLoader.getInstance().queue(new Callable<Texture>()
							    			 {
				
												
												public Texture call() throws Exception {
													Texture texture = new Texture();
													
													
													
													if(Images.getInstance().suppliesMipmap(textureClass.getImage(), textureClass.getShape()))
													{	
														textureImage.setWidth(dataImage.getWidth());
														textureImage.setHeight(dataImage.getWidth());
														textureImage.setMipMapSizes(new int[]{512*512*4, 256*256*4, 128*128*4, 64*64*4,32*32*4,16*16*4,8*8*4,4*4*4,2*2*4,1*4});	
													}else{
														textureImage.setWidth(dataImage.getWidth());
														textureImage.setHeight(dataImage.getHeight());
													}
													//if(textureClass.useMipmaps())
													//	
														//if(!textureClass.useMipmaps())
														//	texture.setScale(new Vector3f(1,-1,1));
																
													 texture.setAnisoLevel(0);
													 texture.setMipmapState( Texture.MM_LINEAR);
													 texture.setFilter(Texture.FM_LINEAR);
													 texture.setImage(textureImage);
													 return texture;
												
										           // return TextureManager.loadTexture(null, tkey, jmeImage,(textureClass.useMipmaps()? Texture.MM_LINEAR_LINEAR:Texture.MM_NONE), Texture.FM_LINEAR, 0f);
												}
							    			 }).get();
													
							    	}catch(ExecutionException e)
							    	{
							    		throw new NoTextureException(e);
							    	}catch(CancellationException  e)
							    	{
							    		throw new NoTextureException(e);
							    	}catch(InterruptedException  e)
							    	{
							    		throw new NoTextureException();
							    	} catch (IOException e) {
							    		throw new NoTextureException(e);
									}
						    	
						     }
 						}
						
						if (texture == null)
						{
							throw new NoTextureException("Failed to load texture");
							
						}
						
						loadingLock.lock();
						try{
							keyMapLock.lock();
							try{
						
								keyMap.put(textureClass, texture);
								loadingKeys.remove(textureClass);//the class is no longer being loaded.
								
							}finally
							{
								keyMapLock.unlock();
							}
						}finally
						{
							loadingLock.unlock();
						}
						
					
						
						return;
						
					}catch(NoTextureException e)
					{
						StateManager.logError(e);
					} catch (IOException e) {
						StateManager.logError(e);
					}finally{
						textureLoaded(textureClass);
					}
				}
			});
			
			
		}finally
		{
			keyMapLock.unlock();
		}
	}*/
}
