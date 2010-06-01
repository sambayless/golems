package com.golemgame.model.texture.fenggui;


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

import org.fenggui.binding.render.ITexture;
import org.fenggui.binding.render.lwjgl.LWJGLTexture;

import com.golemgame.model.texture.Images;
import com.golemgame.model.texture.TextureServer;
import com.golemgame.model.texture.TextureTypeKey;
import com.golemgame.mvc.golems.AppearanceInterpreter.ImageType;
import com.golemgame.states.StateManager;
import com.golemgame.util.SquareTextTextureBuilder;
import com.golemgame.util.loading.openGL.SlowLoader;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

public class FengGUITextureServer extends TextureServer {
		private static final FengGUITextureServer fengGUIInstance = new FengGUITextureServer();

		//should use SOFT references here probably.
		private final Map<TextureTypeKey, Texture> keyMap = new HashMap<TextureTypeKey, Texture> ();
		
		private Lock keyMapLock = new ReentrantLock();
		
		//private Lock loadingLock = new ReentrantLock();
		
		private final Set<TextureTypeKey> loadingKeys = new HashSet<TextureTypeKey>();

		
		
		public FengGUITextureServer() {
			super();

		}


		@Override
		protected FengGUITexture makeTextureWrapper(TextureTypeKey textureClass) 
		{
			return new FengGUITexture(textureClass);
		}
		

		@Override
		public int getLoadedTextureID(TextureTypeKey textureClass){
			keyMapLock.lock();
			try{
				final Texture t = keyMap.get(textureClass);
				if(t == null)
					return 0;
				
				if(t.getTextureId()!=0)
					return t.getTextureId();
			
			
			
				return t.getTextureId();
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
								
								FengGUITextureServer.this.lock();
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
									FengGUITextureServer.this.unlock();
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
		@Override
		public void loadTextureWrapper(TextureWrapper textureWrapper)
				throws NoTextureException {
			if (!(textureWrapper instanceof FengGUITexture))
				throw new NoTextureException("Wrong type");
			
			final FengGUITexture fengGUITexture = (FengGUITexture) textureWrapper;
			
			final TextureTypeKey textureClass = textureWrapper.getTextureTypeKey();
			if (textureClass == null || textureClass.getImage() == ImageType.VOID)
				return;
			
		
				int id = TextureServer.getInstance().loadTextureID(textureClass);
				if (id > 0)
				{
					fengGUITexture.setTexture( new LWJGLTexture(id,textureClass.getTextureWidth(),textureClass.getTextureHeight(),textureClass.getTextureWidth(),textureClass.getTextureHeight()));
				}
				
		
			//TextureState textureState = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
			
			//spatialTexture.setTextureState(textureState);
			
		}
		
	*/


		public static FengGUITextureServer getFengGUIInstance() {
			return fengGUIInstance;
		}


		public ITexture getLoadedFengGUITexture(TextureTypeKey textureClass) {
			int id = this.getLoadedTextureID(textureClass);
			if(id!=0)
			{
				return new LWJGLTexture(id,textureClass.getTextureWidth(),textureClass.getTextureHeight(),textureClass.getTextureWidth(),textureClass.getTextureHeight());

			}
			return null;
		}
		
	}


